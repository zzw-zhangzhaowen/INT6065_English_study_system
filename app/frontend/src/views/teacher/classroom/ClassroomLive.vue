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
        <el-button type="warning" @click="handleSignIn">发起签到</el-button>
        <el-button type="danger" @click="handleEndClassroom">结束课堂</el-button>
      </div>
    </div>

    <!-- 主体内容 -->
    <div class="main-content">
      <!-- 左侧：视频区 + 互动区 -->
      <div class="left-panel">
        <!-- 视频区域 -->
        <div class="video-area">
          <VideoStream ref="videoStreamRef" />
        </div>

        <!-- 互动功能区 -->
        <div class="interaction-area">
          <el-tabs v-model="interactionTab">
            <el-tab-pane label="白板" name="whiteboard">
              <div class="tab-content">
                <Whiteboard
                  ref="whiteboardRef"
                  :classroom-id="classroomId"
                  :stomp-client="stompClientRef"
                  :ws-connected="wsConnected"
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
                  :is-teacher="true"
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
                  :is-teacher="true"
                />
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>

      <!-- 右侧：聊天 + 参与者 -->
      <div class="right-panel">
        <el-tabs v-model="rightTab">
          <!-- 聊天区 -->
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

          <!-- 参与者列表 -->
          <el-tab-pane :label="`参与者(${participants.length})`" name="participants">
            <div class="participants-area">
              <div class="sign-in-stats" v-if="signInList.length > 0">
                <span>已签到: {{ signedCount }}/{{ participants.length }}</span>
              </div>
              <div v-for="p in participants" :key="p.id" class="participant-item">
                <el-avatar :size="32" :src="p.studentAvatar">
                  {{ (p.studentName || '?').charAt(0) }}
                </el-avatar>
                <div class="participant-info">
                  <span class="name">{{ p.studentName || '未知' }}</span>
                  <el-tag v-if="isStudentSigned(p.studentId)" type="success" size="small">已签到</el-tag>
                  <el-tag v-else type="info" size="small">未签到</el-tag>
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
import { ArrowLeft } from '@element-plus/icons-vue'
import { classroomApi } from '@/api/classroom'
import { useClassroomStore } from '@/stores/classroom'
import { useUserStore } from '@/stores/user'
import { connectWebSocket, disconnectWebSocket } from '@/utils/websocket'
import request from '@/utils/request'
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
const signInList = ref([])
const stompClientRef = ref(null)

// 子组件引用
const videoStreamRef = ref(null)
const whiteboardRef = ref(null)
const qaRef = ref(null)
const pollRef = ref(null)

let stompClient = null
let chatSubscription = null
let whiteboardSubscription = null
let qaSubscription = null
let pollSubscription = null

const signedCount = computed(() => {
  return signInList.value.filter(s => s.signed).length
})

const isStudentSigned = (studentId) => {
  const record = signInList.value.find(s => s.studentId === studentId)
  return record?.signed || false
}

const formatMsgTime = (time) => {
  if (!time) return ''
  const str = typeof time === 'string' ? time : time.toString()
  return str.substring(11, 16)
}

const goBack = () => {
  router.push('/teacher/classroom')
}

const loadClassroomData = async () => {
  try {
    await classroomStore.loadClassroom(classroomId.value)
    await classroomStore.loadMessages(classroomId.value)
    await loadSignInList()
    nextTick(() => scrollToBottom())
  } catch (error) {
    ElMessage.error('加载课堂数据失败')
    console.error(error)
  }
}

const loadSignInList = async () => {
  try {
    const res = await classroomApi.getSignInList(classroomId.value)
    if (res.data) {
      signInList.value = res.data
    }
  } catch (error) {
    console.error('加载签到列表失败:', error)
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

      // 订阅聊天消息
      chatSubscription = client.subscribe(`/topic/classroom/${classroomId.value}`, (message) => {
        const data = JSON.parse(message.body)
        classroomStore.addMessage(data)
        nextTick(() => scrollToBottom())
      })

      // 订阅白板消息
      whiteboardSubscription = client.subscribe(`/topic/classroom/${classroomId.value}/whiteboard`, (message) => {
        const data = JSON.parse(message.body)
        if (whiteboardRef.value) {
          whiteboardRef.value.handleRemoteWhiteboard(data)
        }
      })

      // 订阅问答消息
      qaSubscription = client.subscribe(`/topic/classroom/${classroomId.value}/qa`, (message) => {
        const data = JSON.parse(message.body)
        if (qaRef.value) {
          qaRef.value.handleQaMessage(data)
        }
      })

      // 订阅投票消息
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
      console.info('课堂实时通道不可用，消息改由本地数据通道同步')
    }
  )
  if (!stompClient) {
    // 本地运行模式：无实时通道，课堂消息走本地数据层
    wsConnected.value = false
    stompClientRef.value = null
  }
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
    await ElMessageBox.confirm('确定要发起签到吗？所有在线学生将收到签到通知。', '发起签到', { type: 'info' })
    // 先调用后端API激活签到
    await classroomApi.activateSignIn(classroomId.value)
    // 通过 WebSocket 发送系统通知，提醒学生签到
    if (stompClient && stompClient.connected) {
      stompClient.publish({
        destination: `/app/classroom/${classroomId.value}/chat`,
        body: JSON.stringify({ content: '📋 教师已发起签到，请及时签到！', messageType: 'SYSTEM' })
      })
    }
    ElMessage.success('签到已发起，学生可通过签到按钮完成签到')
    setTimeout(() => loadSignInList(), 3000)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '发起签到失败')
      console.error('发起签到失败:', error)
    }
  }
}

const handleEndClassroom = async () => {
  try {
    await ElMessageBox.confirm('确定要结束课堂吗？结束后学生将无法继续参与。', '结束课堂', { type: 'warning' })

    // 检查当前课堂状态，如果已经结束则直接跳转
    try {
      const detailRes = await classroomApi.getClassroomDetail(classroomId.value)
      if (detailRes.data?.classroom?.status === 'ENDED') {
        ElMessage.info('课堂已结束')
        router.push('/teacher/classroom')
        return
      }
    } catch (e) {
      console.error('检查课堂状态失败:', e)
    }

    // 先获取录制的视频
    let recordUrl = null
    if (videoStreamRef.value) {
      try {
        const blob = await videoStreamRef.value.getRecordingBlob()
        if (blob && blob.size > 0) {
          const loadingMsg = ElMessage({
            message: '正在上传录播视频，请勿关闭页面...',
            type: 'info',
            duration: 0,
            showClose: false
          })
          try {
            const formData = new FormData()
            formData.append('file', blob, `classroom_${classroomId.value}_${Date.now()}.webm`)
            const uploadRes = await request({
              url: '/file/upload/video',
              method: 'post',
              data: formData,
              headers: { 'Content-Type': undefined },
              timeout: 300000,
              onUploadProgress: (progressEvent) => {
                if (progressEvent.total) {
                  const percent = Math.round((progressEvent.loaded / progressEvent.total) * 100)
                  loadingMsg.message = `正在上传录播视频 ${percent}%...`
                }
              }
            })
            if (uploadRes.data?.url) {
              recordUrl = uploadRes.data.url
            }
          } finally {
            loadingMsg.close()
          }
        }
      } catch (uploadErr) {
        console.error('录播上传失败:', uploadErr)
        ElMessage.warning('录播视频上传失败，课堂仍将结束')
      }
    }

    // 保存录播地址（在结束课堂之前）
    if (recordUrl) {
      try {
        await classroomApi.saveRecordUrl(classroomId.value, recordUrl)
      } catch (e) {
        console.error('保存录播地址失败:', e)
      }
    }

    // 结束课堂
    try {
      await classroomStore.endClassroom(classroomId.value)
      ElMessage.success(recordUrl ? '课堂已结束，录播已保存' : '课堂已结束')
    } catch (endError) {
      console.error('结束课堂请求失败:', endError)
      // 再次检查课堂状态，可能后端已经成功结束
      try {
        const detailRes = await classroomApi.getClassroomDetail(classroomId.value)
        if (detailRes.data?.classroom?.status === 'ENDED') {
          ElMessage.success('课堂已结束')
        } else {
          throw endError
        }
      } catch (e) {
        throw endError
      }
    }
    router.push('/teacher/classroom')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('结束课堂失败:', error)
      ElMessage.error(error.message || '结束课堂失败，请重试')
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

    .el-tabs__content {
      flex: 1;
      overflow: hidden;

      .el-tab-pane {
        height: 100%;
      }
    }
  }

  :deep(.el-tabs__header) {
    margin: 0;
    padding: 0 16px;
  }
}

.chat-area {
  height: 100%;
  display: flex;
  flex-direction: column;

  .chat-messages {
    flex: 1;
    overflow-y: auto;
    padding: 12px 16px;

    .chat-message {
      margin-bottom: 12px;

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
          color: #409eff;
        }

        .msg-time {
          font-size: 11px;
          color: #999;
        }
      }

      .msg-content {
        font-size: 14px;
        color: #333;
        line-height: 1.5;
      }

      &.system-msg {
        .system-notice {
          text-align: center;
          font-size: 12px;
          color: #999;
          padding: 4px 12px;
          background: #f5f5f5;
          border-radius: 4px;
        }
      }
    }

    .chat-empty {
      text-align: center;
      color: #ccc;
      padding: 40px 0;
      font-size: 14px;
    }
  }

  .chat-input {
    display: flex;
    gap: 8px;
    padding: 12px 16px;
    border-top: 1px solid #f0f0f0;
  }
}

.participants-area {
  padding: 12px 16px;
  overflow-y: auto;
  height: 100%;

  .sign-in-stats {
    padding: 8px 12px;
    background: #f0f9ff;
    border-radius: 8px;
    margin-bottom: 12px;
    font-size: 13px;
    color: #409eff;
  }

  .participant-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 8px 0;
    border-bottom: 1px solid #f5f5f5;

    .participant-info {
      display: flex;
      align-items: center;
      gap: 8px;

      .name {
        font-size: 14px;
        color: #333;
      }
    }
  }
}
</style>
