<template>
  <div class="classroom-live">
    <!-- 顶部工具栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <el-button text @click="goBack">
          <el-icon><ArrowLeft /></el-icon> 返回
        </el-button>
        <h3>{{ classroom?.classroomName || '加载中...' }}</h3>
        <el-tag type="danger" v-if="classroom?.status === 'LIVE'">直播中</el-tag>
        <el-tag type="info" v-else>{{ classroom?.status }}</el-tag>
      </div>
      <div class="toolbar-right">
        <el-button v-if="signInAvailable && !hasSigned" type="success" @click="handleSignIn">
          <el-icon><Check /></el-icon> 签到
        </el-button>
        <el-tag v-if="hasSigned" type="success">已签到</el-tag>
        <el-button type="danger" plain @click="handleLeave">离开课堂</el-button>
      </div>
    </div>

    <!-- 主体内容 -->
    <div class="main-content">
      <!-- 左侧：视频区 + 互动区 -->
      <div class="left-panel">
        <div class="video-area">
          <VideoStream ref="videoStreamRef" :is-teacher="false" />
        </div>

        <div class="interaction-area">
          <el-tabs v-model="interactionTab">
            <el-tab-pane label="白板" name="whiteboard">
              <div class="tab-content">
                <Whiteboard
                  ref="whiteboardRef"
                  :classroom-id="classroomId"
                  :stomp-client="stompClientRef"
                  :ws-connected="wsConnected"
                  :read-only="true"
                />
              </div>
            </el-tab-pane>
            <el-tab-pane label="问答" name="qa">
              <div class="tab-content">
                <ClassroomQA
                  ref="qaRef"
                  :classroom-id="classroomId"
                  :stomp-client="stompClientRef"
                  :ws-connected="wsConnected"
                  :is-teacher="false"
                />
              </div>
            </el-tab-pane>
            <el-tab-pane label="投票" name="poll">
              <div class="tab-content">
                <ClassroomPoll
                  ref="pollRef"
                  :classroom-id="classroomId"
                  :stomp-client="stompClientRef"
                  :ws-connected="wsConnected"
                  :is-teacher="false"
                />
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>

      <!-- 右侧：聊天 + 参与者 -->
      <div class="right-panel">
        <el-tabs v-model="rightTab">
          <el-tab-pane label="课堂聊天" name="chat">
            <div class="chat-area">
              <div class="chat-messages" ref="chatMessagesRef">
                <div v-for="msg in messages" :key="msg.id" class="chat-message"
                     :class="{ 'system-msg': msg.messageType === 'SYSTEM' }">
                  <template v-if="msg.messageType === 'SYSTEM'">
                    <div class="system-notice">{{ msg.content }}</div>
                  </template>
                  <template v-else>
                    <div class="msg-row">
                      <el-avatar :size="28" :src="msg.avatar || undefined">
                        {{ (msg.username || '?').charAt(0) }}
                      </el-avatar>
                      <div class="msg-body">
                        <div class="msg-header">
                          <span class="msg-username">{{ msg.username }}</span>
                          <span class="msg-time">{{ formatMsgTime(msg.createTime) }}</span>
                        </div>
                        <div class="msg-content">{{ msg.content }}</div>
                      </div>
                    </div>
                  </template>
                </div>
                <div v-if="messages.length === 0" class="chat-empty">暂无消息</div>
              </div>
              <div class="chat-input">
                <el-input v-model="chatInput" placeholder="输入消息..." @keyup.enter="sendMessage"
                          :disabled="!wsConnected" />
                <el-button type="primary" @click="sendMessage" :disabled="!wsConnected || !chatInput.trim()">
                  发送
                </el-button>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane :label="`参与者(${participants.length})`" name="participants">
            <div class="participants-area">
              <div v-for="p in participants" :key="p.id" class="participant-item">
                <el-avatar :size="32" :src="p.studentAvatar">
                  {{ (p.studentName || '?').charAt(0) }}
                </el-avatar>
                <div class="participant-info">
                  <span class="name">{{ p.studentName || '未知' }}</span>
                </div>
              </div>
              <el-empty v-if="participants.length === 0" description="暂无参与者" :image-size="60" />
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Check } from '@element-plus/icons-vue'
import { classroomApi } from '@/api/classroom'
import { useClassroomStore } from '@/stores/classroom'
import { useUserStore } from '@/stores/user'
import { connectWebSocket, disconnectWebSocket } from '@/utils/websocket'
import VideoStream from '@/components/classroom/VideoStream.vue'
import Whiteboard from '@/components/classroom/Whiteboard.vue'
import ClassroomQA from '@/components/classroom/ClassroomQA.vue'
import ClassroomPoll from '@/components/classroom/ClassroomPoll.vue'

const route = useRoute()
const router = useRouter()
const classroomStore = useClassroomStore()
const userStore = useUserStore()

const classroomId = computed(() => route.params.id)
const classroom = computed(() => classroomStore.currentClassroom)
const participants = computed(() => classroomStore.participants)
const messages = computed(() => classroomStore.messages)

const interactionTab = ref('whiteboard')
const rightTab = ref('chat')
const chatInput = ref('')
const chatMessagesRef = ref(null)
const wsConnected = ref(false)
const stompClientRef = ref(null)
const hasSigned = ref(false)
const signInAvailable = ref(false)

const videoStreamRef = ref(null)
const whiteboardRef = ref(null)
const qaRef = ref(null)
const pollRef = ref(null)

let stompClient = null
let chatSubscription = null
let whiteboardSubscription = null
let qaSubscription = null
let pollSubscription = null

const formatMsgTime = (time) => {
  if (!time) return ''
  const str = typeof time === 'string' ? time : time.toString()
  return str.substring(11, 16)
}

const goBack = () => {
  router.push('/student/dashboard')
}

const loadClassroomData = async () => {
  try {
    // 先加入课堂
    try {
      await classroomApi.joinClassroom(classroomId.value)
    } catch (e) {
      // 可能已经加入过，忽略
    }

    await classroomStore.loadClassroom(classroomId.value)
    await classroomStore.loadMessages(classroomId.value)
    await checkSignInStatus()
    nextTick(() => scrollToBottom())
  } catch (error) {
    ElMessage.error('加载课堂数据失败')
    console.error(error)
  }
}

const checkSignInStatus = async () => {
  try {
    const res = await classroomApi.getSignInStatus(classroomId.value)
    if (res.data) {
      hasSigned.value = res.data.signed || false
      signInAvailable.value = res.data.signInActive || false
    }
  } catch (e) {
    console.error('检查签到状态失败:', e)
  }
}

const initWebSocket = () => {
  const token = userStore.token
  if (!token) return

  stompClient = connectWebSocket(
    token,
    (client) => {
      wsConnected.value = true
      stompClientRef.value = client

      chatSubscription = client.subscribe(`/topic/classroom/${classroomId.value}`, (message) => {
        const data = JSON.parse(message.body)
        classroomStore.addMessage(data)
        nextTick(() => scrollToBottom())

        // 检测签到系统消息
        if (data.messageType === 'SYSTEM' && data.content && data.content.includes('签到')) {
          signInAvailable.value = true
          checkSignInStatus()
        }
      })

      whiteboardSubscription = client.subscribe(`/topic/classroom/${classroomId.value}/whiteboard`, (message) => {
        const data = JSON.parse(message.body)
        if (whiteboardRef.value) {
          whiteboardRef.value.handleRemoteWhiteboard(data)
        }
      })

      qaSubscription = client.subscribe(`/topic/classroom/${classroomId.value}/qa`, (message) => {
        const data = JSON.parse(message.body)
        if (qaRef.value) {
          qaRef.value.handleQaMessage(data)
        }
      })

      pollSubscription = client.subscribe(`/topic/classroom/${classroomId.value}/poll`, (message) => {
        const data = JSON.parse(message.body)
        if (pollRef.value) {
          pollRef.value.handlePollMessage(data)
        }
      })
    },
    (error) => {
      wsConnected.value = false
      stompClientRef.value = null
      console.error('WebSocket连接失败:', error)
    }
  )
}

const sendMessage = () => {
  if (!chatInput.value.trim() || !stompClient || !stompClient.connected) return

  stompClient.publish({
    destination: `/app/classroom/${classroomId.value}/chat`,
    body: JSON.stringify({ content: chatInput.value.trim() })
  })
  chatInput.value = ''
}

const scrollToBottom = () => {
  if (chatMessagesRef.value) {
    chatMessagesRef.value.scrollTop = chatMessagesRef.value.scrollHeight
  }
}

const handleSignIn = async () => {
  try {
    await classroomApi.signIn(classroomId.value)
    hasSigned.value = true
    ElMessage.success('签到成功')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '签到失败')
  }
}

const handleLeave = async () => {
  try {
    await ElMessageBox.confirm('确定要离开课堂吗？', '提示', { type: 'warning' })
    await classroomApi.leaveClassroom(classroomId.value)
    ElMessage.success('已离开课堂')
    router.push('/student/dashboard')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('离开课堂失败:', error)
    }
  }
}

watch(messages, () => {
  nextTick(() => scrollToBottom())
}, { deep: true })

onMounted(() => {
  loadClassroomData()
  initWebSocket()
})

onUnmounted(() => {
  if (chatSubscription) chatSubscription.unsubscribe()
  if (whiteboardSubscription) whiteboardSubscription.unsubscribe()
  if (qaSubscription) qaSubscription.unsubscribe()
  if (pollSubscription) pollSubscription.unsubscribe()
  disconnectWebSocket()
  classroomStore.resetClassroom()
})
</script>

<style lang="scss" scoped>
.classroom-live {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);

  .toolbar-left {
    display: flex;
    align-items: center;
    gap: 12px;

    h3 {
      font-size: 16px;
      font-weight: 600;
      margin: 0;
    }
  }

  .toolbar-right {
    display: flex;
    align-items: center;
    gap: 8px;
  }
}

.main-content {
  flex: 1;
  display: flex;
  gap: 16px;
  padding: 16px;
  overflow: hidden;
}

.left-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-width: 0;
}

.video-area {
  background: #000;
  border-radius: 12px;
  overflow: hidden;
  aspect-ratio: 16 / 9;
  max-height: 50vh;
}

.interaction-area {
  flex: 1;
  background: #fff;
  border-radius: 12px;
  min-height: 200px;
  display: flex;
  flex-direction: column;
  overflow: hidden;

  :deep(.el-tabs) {
    height: 100%;
    display: flex;
    flex-direction: column;

    .el-tabs__header {
      margin: 0;
      padding: 0 16px;
      flex-shrink: 0;
    }

    .el-tabs__content {
      flex: 1;
      overflow: hidden;

      .el-tab-pane {
        height: 100%;
      }
    }
  }

  .tab-content {
    height: 100%;
    overflow: hidden;
  }
}

.right-panel {
  width: 360px;
  flex-shrink: 0;
  background: #fff;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  overflow: hidden;

  :deep(.el-tabs) {
    height: 100%;
    display: flex;
    flex-direction: column;

    .el-tabs__header {
      margin: 0;
      padding: 0 16px;
      flex-shrink: 0;
    }

    .el-tabs__content {
      flex: 1;
      overflow: hidden;

      .el-tab-pane {
        height: 100%;
      }
    }
  }
}

.chat-area {
  height: 100%;
  display: flex;
  flex-direction: column;

  .chat-messages {
    flex: 1;
    overflow-y: auto;
    padding: 12px;

    .chat-message {
      margin-bottom: 12px;

      &.system-msg {
        text-align: center;

        .system-notice {
          display: inline-block;
          padding: 4px 16px;
          background: #f0f0f0;
          border-radius: 12px;
          font-size: 12px;
          color: #999;
        }
      }

      .msg-row {
        display: flex;
        gap: 8px;
        align-items: flex-start;
      }

      .msg-body {
        flex: 1;
        min-width: 0;
      }

      .msg-header {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 4px;

        .msg-username {
          font-size: 13px;
          font-weight: 600;
          color: #333;
        }

        .msg-time {
          font-size: 11px;
          color: #999;
        }
      }

      .msg-content {
        font-size: 14px;
        color: #444;
        line-height: 1.5;
      }
    }

    .chat-empty {
      text-align: center;
      color: #ccc;
      padding: 40px 0;
      font-size: 13px;
    }
  }

  .chat-input {
    display: flex;
    gap: 8px;
    padding: 12px;
    border-top: 1px solid #f0f0f0;
  }
}

.participants-area {
  padding: 12px;
  overflow-y: auto;
  height: 100%;

  .participant-item {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 8px;
    border-radius: 8px;
    transition: background 0.2s;

    &:hover {
      background: #f5f5f5;
    }

    .participant-info {
      .name {
        font-size: 14px;
        color: #333;
      }
    }
  }
}
</style>
