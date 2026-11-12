<template>
  <div class="classroom-home">
    <div class="page-header">
      <h2>在线课堂</h2>
      <el-button type="primary" @click="openCreateDialog">
        <el-icon><Plus /></el-icon>
        创建课堂
      </el-button>
    </div>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="我的课堂" name="my">
        <div v-loading="loading" class="classroom-grid">
          <div v-for="item in myClassrooms" :key="item.id" class="classroom-card">
            <div class="card-cover" :style="getCoverStyle(item)">
              <el-tag :type="getStatusTagType(item.status)" size="small">
                {{ getStatusLabel(item.status) }}
              </el-tag>
            </div>
            <div class="card-content">
              <h3>{{ item.classroomName }}</h3>
              <div class="card-info">
                <span><el-icon><Calendar /></el-icon> {{ formatTime(item.startTime) }}</span>
                <span><el-icon><User /></el-icon> {{ item.currentStudents || 0 }}人</span>
              </div>
            </div>
            <div class="card-actions">
              <el-button v-if="item.status === 'LIVE'" type="danger" size="small" @click="enterClassroom(item)">
                进入课堂
              </el-button>
              <el-button v-else-if="item.status === 'PENDING'" type="primary" size="small" @click="startAndEnter(item)">
                开始直播
              </el-button>
              <el-button v-if="item.status === 'LIVE'" type="warning" size="small" @click="handleEndClassroom(item)">
                结束
              </el-button>
              <el-button v-if="item.status === 'PENDING'" size="small" @click="openEditDialog(item)">编辑</el-button>
              <el-button v-if="item.status !== 'LIVE'" type="danger" size="small" link @click="handleDeleteClassroom(item)">
                删除
              </el-button>
            </div>
          </div>
          <el-empty v-if="!loading && myClassrooms.length === 0" description="暂无课堂，点击右上角创建" />
        </div>
      </el-tab-pane>

      <el-tab-pane label="录播课堂" name="record">
        <div class="record-list">
          <div v-for="item in recordings" :key="item.id" class="record-item">
            <div class="record-cover">
              <img v-if="item.coverImage" :src="getFileUrl(item.coverImage)" class="cover-img" />
              <div v-else class="cover-placeholder">
                <el-icon :size="32"><VideoCamera /></el-icon>
              </div>
              <span class="duration">{{ formatDuration(item) }}</span>
            </div>
            <div class="record-info">
              <h4>{{ item.classroomName }}</h4>
              <p>{{ item.description || '暂无描述' }}</p>
              <div class="record-meta">
                <span>{{ item.endTime?.substring(0, 10) || '' }}</span>
                <span>{{ item.currentStudents || 0 }}人参与</span>
              </div>
            </div>
            <div class="record-actions">
              <el-button type="primary" link @click="playRecording(item)">播放</el-button>
              <el-button type="primary" link @click="shareRecording(item)">分享</el-button>
              <el-button type="danger" link @click="handleDeleteRecording(item)">删除</el-button>
            </div>
          </div>
          <el-empty v-if="recordings.length === 0" description="暂无录播课堂" />
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 创建/编辑课堂弹窗 -->
    <el-dialog v-model="showDialog" :title="isEditing ? '编辑课堂' : '创建课堂'" width="500px" @closed="resetForm">
      <el-form :model="classroomForm" label-width="100px">
        <el-form-item label="课堂名称" required>
          <el-input v-model="classroomForm.classroomName" placeholder="请输入课堂名称" />
        </el-form-item>
        <el-form-item label="课堂类型">
          <el-select v-model="classroomForm.classroomType" style="width: 100%">
            <el-option label="直播课堂" value="LIVE" />
            <el-option label="录播课堂" value="RECORD" />
          </el-select>
        </el-form-item>
        <el-form-item label="课堂描述">
          <el-input v-model="classroomForm.description" type="textarea" :rows="3" placeholder="请输入课堂描述" />
        </el-form-item>
        <el-form-item label="课堂封面">
          <div class="cover-upload-area">
            <div v-if="classroomForm.coverImage" class="cover-preview">
              <img :src="getFileUrl(classroomForm.coverImage)" />
              <div class="cover-preview-mask" @click="classroomForm.coverImage = ''">
                <el-icon :size="20"><Delete /></el-icon>
                <span>移除</span>
              </div>
            </div>
            <el-upload
              v-else
              class="cover-uploader"
              action="/api/file/upload/image"
              :headers="uploadHeaders"
              :show-file-list="false"
              :on-success="handleCoverSuccess"
              :before-upload="beforeCoverUpload"
              accept="image/*"
            >
              <div class="upload-trigger">
                <el-icon :size="28"><Plus /></el-icon>
                <span>上传封面</span>
              </div>
            </el-upload>
          </div>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker v-model="classroomForm.startTime" type="datetime" placeholder="选择开始时间" style="width: 100%" />
        </el-form-item>
        <el-form-item label="最大人数">
          <el-input-number v-model="classroomForm.maxStudents" :min="1" :max="500" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="submitClassroom">{{ isEditing ? '保存' : '创建' }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Calendar, User, VideoCamera, Delete } from '@element-plus/icons-vue'
import { classroomApi } from '@/api/classroom'
import { getFileUrl } from '@/utils/file'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const activeTab = ref('my')
const loading = ref(false)
const showDialog = ref(false)
const isEditing = ref(false)
const editingClassroomId = ref(null)

const myClassrooms = ref([])
const recordings = ref([])

const gradients = [
  '#e6edff',
  '#eaf5ff',
  '#e9f7f0',
  '#fff4e6'
]

const defaultForm = () => ({
  classroomName: '',
  classroomType: 'LIVE',
  description: '',
  coverImage: '',
  startTime: '',
  maxStudents: 100
})

const classroomForm = ref(defaultForm())

const uploadHeaders = { Authorization: `Bearer ${userStore.token}` }

const getCoverStyle = (item) => {
  if (item.coverImage) {
    return {
      backgroundImage: `url(${getFileUrl(item.coverImage)})`,
      backgroundSize: 'cover',
      backgroundPosition: 'center'
    }
  }
  return { background: item.gradient }
}

const handleCoverSuccess = (res) => {
  if (res.code === 200 && res.data?.url) {
    classroomForm.value.coverImage = res.data.url
  } else {
    ElMessage.error('上传失败')
  }
}

const beforeCoverUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB')
    return false
  }
  return true
}

const getStatusTagType = (status) => {
  const map = { LIVE: 'danger', PENDING: 'info', ENDED: 'success' }
  return map[status] || 'info'
}

const getStatusLabel = (status) => {
  const map = { LIVE: '直播中', PENDING: '未开始', ENDED: '已结束' }
  return map[status] || status
}

const formatTime = (time) => {
  if (!time) return '待定'
  return typeof time === 'string' ? time.substring(0, 16).replace('T', ' ') : time
}

// 格式化课堂时长
const formatDuration = (item) => {
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

const loadMyClassrooms = async () => {
  loading.value = true
  try {
    const res = await classroomApi.getTeacherClassrooms()
    if (res.data) {
      myClassrooms.value = res.data.map((item, index) => ({
        ...item,
        gradient: gradients[index % gradients.length]
      }))
    }
  } catch (error) {
    console.error('加载课堂失败:', error)
  } finally {
    loading.value = false
  }
}

const loadRecordings = async () => {
  try {
    const res = await classroomApi.getClassroomList({ status: 'ENDED', pageNum: 1, pageSize: 20 })
    if (res.data?.records) {
      recordings.value = res.data.records
    }
  } catch (error) {
    console.error('加载录播失败:', error)
  }
}

const openCreateDialog = () => {
  isEditing.value = false
  editingClassroomId.value = null
  classroomForm.value = defaultForm()
  showDialog.value = true
}

const openEditDialog = (item) => {
  isEditing.value = true
  editingClassroomId.value = item.id
  classroomForm.value = {
    classroomName: item.classroomName,
    classroomType: item.classroomType || 'LIVE',
    description: item.description || '',
    coverImage: item.coverImage || '',
    startTime: item.startTime || '',
    maxStudents: item.maxStudents || 100
  }
  showDialog.value = true
}

const resetForm = () => {
  isEditing.value = false
  editingClassroomId.value = null
  classroomForm.value = defaultForm()
}

const submitClassroom = async () => {
  if (!classroomForm.value.classroomName) {
    ElMessage.warning('请输入课堂名称')
    return
  }
  try {
    if (isEditing.value && editingClassroomId.value) {
      await classroomApi.updateClassroom(editingClassroomId.value, classroomForm.value)
      ElMessage.success('更新成功')
    } else {
      await classroomApi.createClassroom(classroomForm.value)
      ElMessage.success('创建成功')
    }
    showDialog.value = false
    loadMyClassrooms()
  } catch (error) {
    console.error('操作失败:', error)
  }
}

const startAndEnter = async (item) => {
  try {
    await classroomApi.startClassroom(item.id)
    ElMessage.success('课堂已开始')
    router.push(`/teacher/classroom/live/${item.id}`)
  } catch (error) {
    console.error('开始课堂失败:', error)
  }
}

const enterClassroom = (item) => {
  router.push(`/teacher/classroom/live/${item.id}`)
}

const handleEndClassroom = async (item) => {
  try {
    await ElMessageBox.confirm('确定要结束这个课堂吗？', '提示', { type: 'warning' })
    await classroomApi.endClassroom(item.id)
    ElMessage.success('课堂已结束')
    loadMyClassrooms()
    loadRecordings()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('结束课堂失败:', error)
    }
  }
}

const handleDeleteClassroom = async (item) => {
  try {
    await ElMessageBox.confirm('确定要删除这个课堂吗？', '提示', { type: 'warning' })
    await classroomApi.deleteClassroom(item.id)
    ElMessage.success('删除成功')
    loadMyClassrooms()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

const playRecording = (item) => {
  router.push(`/teacher/classroom/playback/${item.id}`)
}

const shareRecording = (item) => {
  const shareUrl = `${window.location.origin}/classroom/playback/${item.id}`
  navigator.clipboard.writeText(shareUrl).then(() => {
    ElMessage.success('分享链接已复制到剪贴板')
  }).catch(() => {
    ElMessage.info(`分享链接: ${shareUrl}`)
  })
}

const handleDeleteRecording = async (item) => {
  try {
    await ElMessageBox.confirm('确定要删除这个录播吗？', '提示', { type: 'warning' })
    await classroomApi.deleteClassroom(item.id)
    ElMessage.success('删除成功')
    loadRecordings()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

onMounted(() => {
  loadMyClassrooms()
  loadRecordings()
})
</script>

<style lang="scss" scoped>
.classroom-home {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;

    h2 {
      font-size: 24px;
      font-weight: 600;
      color: var(--text-primary);
    }
  }

  .classroom-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 20px;
  }

  .classroom-card {
    background: var(--bg-primary);
    border-radius: var(--radius-lg);
    overflow: hidden;
    box-shadow: var(--shadow-sm);

    .card-cover {
      height: 120px;
      display: flex;
      align-items: flex-start;
      justify-content: flex-end;
      padding: 12px;
    }

    .card-content {
      padding: 16px;

      h3 {
        font-size: 16px;
        font-weight: 600;
        color: var(--text-primary);
        margin-bottom: 12px;
      }

      .card-info {
        display: flex;
        gap: 16px;
        font-size: 13px;
        color: var(--text-secondary);

        span {
          display: flex;
          align-items: center;
          gap: 4px;
        }
      }
    }

    .card-actions {
      padding: 0 16px 16px;
      display: flex;
      gap: 8px;
    }
  }

  .record-list {
    .record-item {
      display: flex;
      gap: 16px;
      padding: 16px;
      background: var(--bg-primary);
      border-radius: var(--radius-lg);
      margin-bottom: 16px;

      .record-cover {
        position: relative;
        width: 160px;
        height: 90px;
        border-radius: var(--radius-md);
        overflow: hidden;
        flex-shrink: 0;

        .cover-placeholder {
          width: 100%;
          height: 100%;
          background: #edf2f8;
          display: flex;
          align-items: center;
          justify-content: center;
          color: #7f8ea3;
        }

        .cover-img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }

        .duration {
          position: absolute;
          bottom: 4px;
          right: 4px;
          background: rgba(0, 0, 0, 0.7);
          color: #fff;
          padding: 2px 6px;
          border-radius: 4px;
          font-size: 12px;
        }
      }

      .record-info {
        flex: 1;

        h4 {
          font-size: 15px;
          font-weight: 600;
          color: var(--text-primary);
          margin-bottom: 8px;
        }

        p {
          font-size: 13px;
          color: var(--text-secondary);
          margin-bottom: 8px;
        }

        .record-meta {
          display: flex;
          gap: 16px;
          font-size: 12px;
          color: var(--text-placeholder);
        }
      }

      .record-actions {
        display: flex;
        flex-direction: column;
        gap: 4px;
      }
    }
  }
}

.cover-upload-area {
  .cover-preview {
    position: relative;
    width: 200px;
    height: 120px;
    border-radius: 8px;
    overflow: hidden;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .cover-preview-mask {
      position: absolute;
      inset: 0;
      background: rgba(0, 0, 0, 0.5);
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      color: #fff;
      gap: 4px;
      font-size: 12px;
      opacity: 0;
      cursor: pointer;
      transition: opacity 0.2s;
    }

    &:hover .cover-preview-mask {
      opacity: 1;
    }
  }

  .cover-uploader {
    .upload-trigger {
      width: 200px;
      height: 120px;
      border: 1px dashed #dcdfe6;
      border-radius: 8px;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      gap: 6px;
      color: #909399;
      font-size: 13px;
      cursor: pointer;
      transition: border-color 0.2s;

      &:hover {
        border-color: #6b8fcf;
        color: #6b8fcf;
      }
    }
  }
}
</style>
