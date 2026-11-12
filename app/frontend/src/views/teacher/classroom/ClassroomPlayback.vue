<template>
  <div class="classroom-playback">
    <div class="page-header">
      <el-button text @click="goBack">
        <el-icon><ArrowLeft /></el-icon> 返回课堂列表
      </el-button>
      <h2>{{ classroom?.classroomName || '加载中...' }}</h2>
    </div>

    <div class="playback-content">
      <!-- 左侧：视频回放 + 课堂信息 -->
      <div class="left-section">
        <div class="video-player">
          <!-- 有录播URL时显示视频播放器 -->
          <div v-if="classroom?.recordUrl" class="video-container">
            <video
              ref="videoRef"
              :src="getFileUrl(classroom.recordUrl)"
              controls
              controlslist="nodownload"
              preload="auto"
              :poster="classroom.coverImage ? getFileUrl(classroom.coverImage) : ''"
              @error="onVideoError"
            >
              您的浏览器不支持视频播放
            </video>
            <div class="video-controls-extra">
              <div class="speed-control">
                <span>倍速:</span>
                <el-select v-model="playbackRate" size="small" style="width: 80px" @change="changePlaybackRate">
                  <el-option :value="0.5" label="0.5x" />
                  <el-option :value="1" label="1.0x" />
                  <el-option :value="1.5" label="1.5x" />
                  <el-option :value="2" label="2.0x" />
                </el-select>
              </div>
            </div>
          </div>
          <!-- 无录播URL时显示封面或占位 -->
          <div v-else class="video-placeholder">
            <img v-if="classroom?.coverImage" :src="getFileUrl(classroom.coverImage)" class="placeholder-cover" />
            <template v-else>
              <el-icon :size="64" color="#999"><VideoPlay /></el-icon>
            </template>
            <div class="placeholder-overlay">
              <p>暂无录播视频</p>
              <p class="hint">课堂结束后录播视频将自动生成</p>
            </div>
            <div class="playback-info" v-if="classroom">
              <span>时长: {{ formatClassroomDuration(classroom) }}</span>
              <span>参与人数: {{ participants.length }}人</span>
            </div>
          </div>
        </div>

        <div class="classroom-info" v-if="classroom">
          <h3>课堂信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="课堂名称">{{ classroom.classroomName }}</el-descriptions-item>
            <el-descriptions-item label="课堂类型">
              <el-tag size="small">{{ classroom.classroomType === 'LIVE' ? '直播课堂' : '录播课堂' }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="开始时间">{{ formatDateTime(classroom.startTime) }}</el-descriptions-item>
            <el-descriptions-item label="结束时间">{{ formatDateTime(classroom.endTime) }}</el-descriptions-item>
            <el-descriptions-item label="课堂时长">{{ formatClassroomDuration(classroom) }}</el-descriptions-item>
            <el-descriptions-item label="参与人数">{{ participants.length }}人</el-descriptions-item>
            <el-descriptions-item label="课堂描述" :span="2">{{ classroom.description || '暂无描述' }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 参与者记录 -->
        <div class="participant-records" v-if="participants.length > 0">
          <h3>参与记录</h3>
          <el-table :data="participants" stripe>
            <el-table-column prop="studentName" label="学生姓名" width="120" />
            <el-table-column label="加入时间" width="180">
              <template #default="{ row }">{{ formatDateTime(row.joinTime) }}</template>
            </el-table-column>
            <el-table-column label="离开时间" width="180">
              <template #default="{ row }">{{ formatDateTime(row.leaveTime) || '未离开' }}</template>
            </el-table-column>
            <el-table-column label="参与时长" width="100">
              <template #default="{ row }">{{ formatParticipantDuration(row) }}</template>
            </el-table-column>
            <el-table-column label="签到状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.isSigned ? 'success' : 'info'" size="small">
                  {{ row.isSigned ? '已签到' : '未签到' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>

      <!-- 右侧：聊天历史 -->
      <div class="right-section">
        <h3>聊天记录</h3>
        <div class="chat-history">
          <div v-for="msg in chatMessages" :key="msg.id" class="chat-msg"
               :class="{ 'system-msg': msg.messageType === 'SYSTEM' }">
            <template v-if="msg.messageType === 'SYSTEM'">
              <div class="system-notice">{{ msg.content }}</div>
            </template>
            <template v-else>
              <div class="msg-header">
                <span class="msg-user">{{ msg.username }}</span>
                <span class="msg-time">{{ formatTime(msg.createTime) }}</span>
              </div>
              <div class="msg-body">{{ msg.content }}</div>
            </template>
          </div>
          <el-empty v-if="chatMessages.length === 0" description="暂无聊天记录" :image-size="60" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, VideoPlay } from '@element-plus/icons-vue'
import { classroomApi } from '@/api/classroom'
import { getFileUrl } from '@/utils/file'

const route = useRoute()
const router = useRouter()

const classroom = ref(null)
const teacher = ref(null)
const participants = ref([])
const chatMessages = ref([])
const videoRef = ref(null)
const playbackRate = ref(1)

const classroomId = route.params.id

const formatDateTime = (time) => {
  if (!time) return ''
  const str = typeof time === 'string' ? time : time.toString()
  return str.substring(0, 19).replace('T', ' ')
}

const formatTime = (time) => {
  if (!time) return ''
  const str = typeof time === 'string' ? time : time.toString()
  return str.substring(11, 16)
}

const goBack = () => {
  const path = route.path
  if (path.startsWith('/student')) {
    router.push('/student/classroom')
  } else {
    router.push('/teacher/classroom')
  }
}

const formatDuration = (seconds) => {
  const m = Math.floor(seconds / 60)
  const s = Math.floor(seconds % 60)
  return `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
}

// 格式化课堂时长
const formatClassroomDuration = (item) => {
  if (!item) return '未知'
  
  // 如果有明确的duration字段且大于0，显示分钟
  if (item.duration && item.duration > 0) {
    return `${item.duration}分钟`
  }
  
  // 如果duration为0或不存在，且有startTime和endTime，计算秒数
  if (item.startTime && item.endTime) {
    const start = new Date(item.startTime)
    const end = new Date(item.endTime)
    const seconds = Math.floor((end - start) / 1000)
    
    if (seconds < 60) {
      return `${seconds}秒`
    }
    const minutes = Math.floor(seconds / 60)
    return `${minutes}分钟`
  }
  
  return '未知'
}

// 格式化参与时长
const formatParticipantDuration = (row) => {
  if (!row) return '-'
  
  // 如果有duration且大于0，显示分钟
  if (row.duration && row.duration > 0) {
    return `${row.duration}分钟`
  }
  
  // 如果duration为0或不存在，且有joinTime和leaveTime，计算秒数
  if (row.joinTime && row.leaveTime) {
    const start = new Date(row.joinTime)
    const end = new Date(row.leaveTime)
    const seconds = Math.floor((end - start) / 1000)
    
    if (seconds < 60) {
      return `${seconds}秒`
    }
    const minutes = Math.floor(seconds / 60)
    return `${minutes}分钟`
  }
  
  return '-'
}

const onVideoError = () => {
  ElMessage.error('视频加载失败，请检查录播文件是否存在')
}

const changePlaybackRate = (rate) => {
  if (videoRef.value) {
    videoRef.value.playbackRate = rate
  }
}

const loadData = async () => {
  try {
    const res = await classroomApi.getClassroomDetail(classroomId)
    if (res.data) {
      classroom.value = res.data.classroom
      teacher.value = res.data.teacher
      participants.value = res.data.participants || []
    }
  } catch (error) {
    ElMessage.error('加载课堂数据失败')
    console.error(error)
  }
}

const loadChatHistory = async () => {
  try {
    const res = await classroomApi.getClassroomMessages(classroomId, { pageNum: 1, pageSize: 500 })
    if (res.data) {
      chatMessages.value = res.data.records || res.data
    }
  } catch (error) {
    console.error('加载聊天记录失败:', error)
  }
}

onMounted(() => {
  loadData()
  loadChatHistory()
})
</script>

<style lang="scss" scoped>
.classroom-playback {
  .page-header {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 24px;

    h2 {
      font-size: 22px;
      font-weight: 600;
      color: var(--text-primary);
      margin: 0;
    }
  }
}

.playback-content {
  display: flex;
  gap: 20px;
}

.left-section {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.video-player {
  background: #000;
  border-radius: 12px;
  overflow: hidden;

  .video-container {
    video {
      width: 100%;
      display: block;
      max-height: 60vh;
      background: #000;
    }

    .video-controls-extra {
      display: flex;
      justify-content: flex-end;
      padding: 8px 16px;
      background: #1a1a1a;

      .speed-control {
        display: flex;
        align-items: center;
        gap: 6px;
        font-size: 13px;
        color: #ccc;
      }
    }
  }

  .video-placeholder {
    width: 100%;
    aspect-ratio: 16 / 9;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #999;
    gap: 8px;
    position: relative;

    .placeholder-cover {
      position: absolute;
      inset: 0;
      width: 100%;
      height: 100%;
      object-fit: cover;
      filter: brightness(0.4);
    }

    .placeholder-overlay {
      position: relative;
      z-index: 1;
      text-align: center;

      p {
        margin: 0;
        font-size: 14px;
      }

      .hint {
        font-size: 12px;
        color: #888;
        margin-top: 4px;
      }
    }

    .playback-info {
      position: relative;
      z-index: 1;
      margin-top: 12px;
      display: flex;
      gap: 20px;
      font-size: 13px;
      color: #aaa;
    }
  }
}

.classroom-info {
  background: var(--bg-primary);
  border-radius: 12px;
  padding: 20px;

  h3 {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 16px;
    color: var(--text-primary);
  }
}

.participant-records {
  background: var(--bg-primary);
  border-radius: 12px;
  padding: 20px;

  h3 {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 16px;
    color: var(--text-primary);
  }
}

.right-section {
  width: 360px;
  flex-shrink: 0;
  background: var(--bg-primary);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  max-height: calc(100vh - 140px);

  h3 {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 16px;
    color: var(--text-primary);
  }
}

.chat-history {
  flex: 1;
  overflow-y: auto;

  .chat-msg {
    margin-bottom: 12px;

    .msg-header {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 4px;

      .msg-user {
        font-size: 13px;
        font-weight: 600;
        color: #409eff;
      }

      .msg-time {
        font-size: 11px;
        color: #999;
      }
    }

    .msg-body {
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
}
</style>
