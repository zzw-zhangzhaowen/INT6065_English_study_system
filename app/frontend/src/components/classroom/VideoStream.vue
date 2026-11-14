<template>
  <div class="video-stream">
    <!-- 视频显示区域 -->
    <div class="video-container" v-show="isStreaming">
      <video ref="videoRef" autoplay playsinline muted class="video-element"></video>
      <div class="video-overlay">
        <span class="stream-type-tag">{{ streamTypeLabel }}</span>
      </div>
    </div>

    <!-- 未开始时的画面占位 -->
    <div class="video-placeholder" v-show="!isStreaming">
      <el-icon :size="64" color="#999"><VideoCamera /></el-icon>
      <p>课堂画面</p>
      <p class="hint">点击下方按钮开始上课</p>
    </div>

    <!-- 控制栏 -->
    <div class="video-controls">
      <el-tooltip :content="isMicOn ? '关闭麦克风' : '开启麦克风'" placement="top">
        <el-button :type="isMicOn ? 'primary' : 'default'" circle @click="toggleMic">
          <el-icon><Microphone v-if="isMicOn" /><MuteNotification v-else /></el-icon>
        </el-button>
      </el-tooltip>

      <el-tooltip :content="isCameraOn ? '关闭摄像头' : '开启摄像头'" placement="top">
        <el-button :type="isCameraOn ? 'primary' : 'default'" circle @click="toggleCamera">
          <el-icon><VideoCameraFilled /></el-icon>
        </el-button>
      </el-tooltip>

      <el-tooltip :content="isScreenSharing ? '停止共享' : '屏幕共享'" placement="top">
        <el-button :type="isScreenSharing ? 'danger' : 'default'" circle @click="toggleScreenShare">
          <el-icon><Monitor /></el-icon>
        </el-button>
      </el-tooltip>

      <el-tooltip v-if="isStreaming" content="结束直播" placement="top">
        <el-button type="danger" circle @click="stopStream">
          <el-icon><VideoPause /></el-icon>
        </el-button>
      </el-tooltip>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { VideoCamera, Microphone, MuteNotification, VideoCameraFilled, Monitor, VideoPause } from '@element-plus/icons-vue'

const videoRef = ref(null)
const localStream = ref(null)
const isMicOn = ref(false)
const isCameraOn = ref(false)
const isScreenSharing = ref(false)

// 录制相关
let mediaRecorder = null
const recordedChunks = ref([])

const isStreaming = computed(() => isCameraOn.value || isScreenSharing.value)

const streamTypeLabel = computed(() => {
  if (isScreenSharing.value) return '屏幕共享中'
  if (isCameraOn.value) return '摄像头直播中'
  return ''
})

// 开始录制当前流
const startRecording = (stream) => {
  stopRecording()
  recordedChunks.value = []

  const mimeType = MediaRecorder.isTypeSupported('video/webm;codecs=vp9,opus')
    ? 'video/webm;codecs=vp9,opus'
    : MediaRecorder.isTypeSupported('video/webm;codecs=vp8,opus')
      ? 'video/webm;codecs=vp8,opus'
      : 'video/webm'

  try {
    mediaRecorder = new MediaRecorder(stream, { mimeType })
    mediaRecorder.ondataavailable = (e) => {
      if (e.data && e.data.size > 0) {
        recordedChunks.value.push(e.data)
      }
    }
    // 每5秒收集一次数据，避免内存积压
    mediaRecorder.start(5000)
  } catch (err) {
    console.error('MediaRecorder 初始化失败:', err)
  }
}

const stopRecording = () => {
  if (mediaRecorder && mediaRecorder.state !== 'inactive') {
    mediaRecorder.stop()
  }
  mediaRecorder = null
}

// 获取录制的 Blob，供父组件调用
const getRecordingBlob = () => {
  return new Promise((resolve) => {
    if (mediaRecorder && mediaRecorder.state !== 'inactive') {
      mediaRecorder.onstop = () => {
        const blob = new Blob(recordedChunks.value, { type: 'video/webm' })
        resolve(blob.size > 0 ? blob : null)
      }
      mediaRecorder.stop()
      mediaRecorder = null
    } else if (recordedChunks.value.length > 0) {
      const blob = new Blob(recordedChunks.value, { type: 'video/webm' })
      resolve(blob.size > 0 ? blob : null)
    } else {
      resolve(null)
    }
  })
}

const startCameraStream = async () => {
  try {
    const stream = await navigator.mediaDevices.getUserMedia({
      video: { width: 1280, height: 720, facingMode: 'user' },
      audio: true
    })
    localStream.value = stream
    if (videoRef.value) {
      videoRef.value.srcObject = stream
    }
    isCameraOn.value = true
    isMicOn.value = true
    startRecording(stream)
    ElMessage.success('摄像头已开启')
  } catch (err) {
    console.error('获取摄像头失败:', err)
    if (err.name === 'NotAllowedError') {
      ElMessage.error('请允许浏览')
    } else if (err.name === 'NotFoundError') {
      ElMessage.error('未检测到摄像头设备')
    } else {
      ElMessage.error('摄像头开启失败: ' + err.message)
    }
  }
}

const stopCameraStream = () => {
  if (localStream.value) {
    localStream.value.getTracks().forEach(track => track.stop())
    localStream.value = null
  }
  if (videoRef.value) {
    videoRef.value.srcObject = null
  }
  isCameraOn.value = false
  isMicOn.value = false
}

const toggleCamera = async () => {
  if (isCameraOn.value) {
    if (isScreenSharing.value) {
      stopScreenShare()
    }
    stopRecording()
    stopCameraStream()
    ElMessage.info('摄像头已关闭')
  } else {
    if (isScreenSharing.value) {
      stopRecording()
      stopScreenShare()
    }
    await startCameraStream()
  }
}

const toggleMic = () => {
  if (!localStream.value) {
    ElMessage.warning('请先开启摄像头或屏幕共享')
    return
  }
  const audioTracks = localStream.value.getAudioTracks()
  if (audioTracks.length > 0) {
    isMicOn.value = !isMicOn.value
    audioTracks.forEach(track => {
      track.enabled = isMicOn.value
    })
    ElMessage.info(isMicOn.value ? '麦克风已开启' : '麦克风已静音')
  } else {
    ElMessage.warning('未检测到麦克风')
  }
}

const startScreenShare = async () => {
  try {
    const stream = await navigator.mediaDevices.getDisplayMedia({
      video: { width: 1920, height: 1080 },
      audio: true
    })

    stream.getVideoTracks()[0].onended = () => {
      stopRecording()
      stopScreenShare()
      ElMessage.info('屏幕共享已停止')
    }

    localStream.value = stream
    if (videoRef.value) {
      videoRef.value.srcObject = stream
    }
    isScreenSharing.value = true
    isMicOn.value = stream.getAudioTracks().length > 0
    startRecording(stream)
    ElMessage.success('屏幕共享已开启')
  } catch (err) {
    console.error('屏幕共享失败:', err)
    if (err.name === 'NotAllowedError') {
      ElMessage.info('已取消屏幕共享')
    } else {
      ElMessage.error('屏幕共享失败: ' + err.message)
    }
  }
}

const stopScreenShare = () => {
  if (localStream.value) {
    localStream.value.getTracks().forEach(track => track.stop())
    localStream.value = null
  }
  if (videoRef.value) {
    videoRef.value.srcObject = null
  }
  isScreenSharing.value = false
  isMicOn.value = false
}

const toggleScreenShare = async () => {
  if (isScreenSharing.value) {
    stopRecording()
    stopScreenShare()
    ElMessage.info('屏幕共享已停止')
  } else {
    if (isCameraOn.value) {
      stopRecording()
      stopCameraStream()
    }
    await startScreenShare()
  }
}

const stopStream = () => {
  stopRecording()
  if (isScreenSharing.value) {
    stopScreenShare()
  }
  if (isCameraOn.value) {
    stopCameraStream()
  }
  ElMessage.info('直播已结束')
}

defineExpose({ getRecordingBlob })

onUnmounted(() => {
  stopRecording()
  if (isScreenSharing.value) stopScreenShare()
  if (isCameraOn.value) stopCameraStream()
})
</script>

<style lang="scss" scoped>
.video-stream {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #000;
  border-radius: 12px;
  overflow: hidden;
}

.video-container {
  flex: 1;
  position: relative;
  min-height: 0;

  .video-element {
    width: 100%;
    height: 100%;
    object-fit: contain;
    background: #000;
  }

  .video-overlay {
    position: absolute;
    top: 12px;
    left: 12px;

    .stream-type-tag {
      background: rgba(245, 63, 63, 0.85);
      color: #fff;
      padding: 4px 12px;
      border-radius: 4px;
      font-size: 12px;
      font-weight: 500;
    }
  }
}

.video-placeholder {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #999;
  gap: 8px;

  p {
    margin: 0;
    font-size: 14px;
  }

  .hint {
    font-size: 12px;
    color: #666;
  }
}

.video-controls {
  display: flex;
  justify-content: center;
  gap: 12px;
  padding: 12px;
  background: rgba(0, 0, 0, 0.6);
}
</style>
