<template>
  <div class="photo-management">
    <el-page-header @back="$router.back()" title="返回">
      <template #content>
        <h2>系统图片管理</h2>
      </template>
    </el-page-header>

    <el-card class="upload-card" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>上传新图片</span>
          <el-text type="info" size="small">支持 jpg、jpeg、png、gif、webp、bmp 格式，最大10MB</el-text>
        </div>
      </template>
      
      <el-upload
        class="upload-demo"
        drag
        :action="uploadUrl"
        :headers="uploadHeaders"
        :on-success="handleUploadSuccess"
        :on-error="handleUploadError"
        :before-upload="beforeUpload"
        :show-file-list="false"
        accept="image/*"
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          将文件拖到此处，或<em>点击上传</em>
        </div>
      </el-upload>
    </el-card>

    <el-card class="gallery-card" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>系统图片库 ({{ photos.length }})</span>
          <el-button :icon="Refresh" @click="loadPhotos" circle size="small" />
        </div>
      </template>

      <el-alert
        title="提示"
        type="info"
        :closable="false"
        style="margin-bottom: 20px;"
      >
        这里的图片将作为系统默认图片资源，可被登录页、默认头像等功能使用。
      </el-alert>

      <el-empty v-if="loading" description="加载中..." />
      <el-empty v-else-if="photos.length === 0" description="暂无图片" />
      
      <div v-else class="photo-grid">
        <div v-for="photo in photos" :key="photo.filename" class="photo-item">
          <div class="photo-preview" @click="handlePreview(photo)">
            <img :src="getPhotoUrl(photo.url)" :alt="photo.filename" />
          </div>
          <div class="photo-info">
            <el-tooltip :content="photo.filename" placement="top">
              <div class="filename">{{ photo.filename }}</div>
            </el-tooltip>
            <div class="size">{{ formatFileSize(photo.size) }}</div>
          </div>
          <div class="photo-actions">
            <el-button
              type="primary"
              size="small"
              :icon="CopyDocument"
              @click="copyUrl(photo)"
            >
              复制链接
            </el-button>
            <el-upload
              :action="uploadUrl"
              :headers="uploadHeaders"
              :on-success="(response) => handleReplaceSuccess(response, photo)"
              :before-upload="beforeUpload"
              :show-file-list="false"
              accept="image/*"
            >
              <el-button type="warning" size="small" :icon="Edit">
                替换
              </el-button>
            </el-upload>
            <el-popconfirm
              title="确定要删除这张图片吗？"
              @confirm="handleDelete(photo)"
            >
              <template #reference>
                <el-button type="danger" size="small" :icon="Delete">
                  删除
                </el-button>
              </template>
            </el-popconfirm>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 图片预览对话框 -->
    <el-dialog
      v-model="previewVisible"
      title="图片预览"
      width="60%"
      center
    >
      <div class="preview-content">
        <img :src="previewUrl" alt="预览" style="width: 100%; height: auto;" />
        <div class="preview-info">
          <p><strong>文件名：</strong>{{ currentPhoto?.filename }}</p>
          <p><strong>访问地址：</strong>{{ currentPhoto?.url }}</p>
          <p><strong>API地址：</strong>{{ currentPhoto?.apiUrl }}</p>
          <p><strong>大小：</strong>{{ formatFileSize(currentPhoto?.size) }}</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled, Refresh, CopyDocument, Edit, Delete } from '@element-plus/icons-vue'
import photoApi from '@/api/photo'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const photos = ref([])
const loading = ref(false)
const previewVisible = ref(false)
const previewUrl = ref('')
const currentPhoto = ref(null)

// 上传配置
const uploadUrl = computed(() => {
  const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
  return `${baseURL}/api/photo/upload`
})

const uploadHeaders = computed(() => ({
  'Authorization': `Bearer ${userStore.token}`
}))

// 加载图片列表
const loadPhotos = async () => {
  loading.value = true
  try {
    const res = await photoApi.getPhotoList()
    photos.value = res.data || []
  } catch (error) {
    ElMessage.error('加载图片列表失败')
  } finally {
    loading.value = false
  }
}

// 上传前检查
const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过 10MB!')
    return false
  }
  return true
}

// 上传成功
const handleUploadSuccess = (response) => {
  if (response.code === 200) {
    ElMessage.success('图片上传成功')
    loadPhotos()
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

// 上传失败
const handleUploadError = () => {
  ElMessage.error('图片上传失败')
}

// 替换成功
const handleReplaceSuccess = (response, photo) => {
  if (response.code === 200) {
    ElMessage.success('图片替换成功')
    loadPhotos()
  } else {
    ElMessage.error(response.message || '替换失败')
  }
}

// 删除图片
const handleDelete = async (photo) => {
  try {
    await photoApi.deletePhoto(photo.filename)
    ElMessage.success('删除成功')
    loadPhotos()
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

// 预览图片
const handlePreview = (photo) => {
  currentPhoto.value = photo
  previewUrl.value = getPhotoUrl(photo.url)
  previewVisible.value = true
}

// 复制链接
const copyUrl = async (photo) => {
  const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
  const fullUrl = `${baseURL}${photo.url}`
  
  try {
    await navigator.clipboard.writeText(fullUrl)
    ElMessage.success('链接已复制到剪贴板')
  } catch (error) {
    // 降级方案
    const textarea = document.createElement('textarea')
    textarea.value = fullUrl
    document.body.appendChild(textarea)
    textarea.select()
    document.execCommand('copy')
    document.body.removeChild(textarea)
    ElMessage.success('链接已复制到剪贴板')
  }
}

// 获取图片URL
const getPhotoUrl = (url) => {
  const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
  return `${baseURL}${url}`
}

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (!bytes) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return (bytes / Math.pow(k, i)).toFixed(2) + ' ' + sizes[i]
}

onMounted(() => {
  loadPhotos()
})
</script>

<style scoped>
.photo-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.upload-demo {
  width: 100%;
}

.photo-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.photo-item {
  border: 1px solid var(--el-border-color);
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s;
}

.photo-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.photo-preview {
  width: 100%;
  height: 200px;
  overflow: hidden;
  cursor: pointer;
  background: var(--el-fill-color-light);
  display: flex;
  align-items: center;
  justify-content: center;
}

.photo-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.photo-preview:hover img {
  transform: scale(1.05);
}

.photo-info {
  padding: 12px;
  background: var(--el-fill-color-lighter);
}

.filename {
  font-size: 14px;
  font-weight: 500;
  color: var(--el-text-color-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 4px;
}

.size {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.photo-actions {
  display: flex;
  gap: 8px;
  padding: 12px;
  border-top: 1px solid var(--el-border-color);
}

.photo-actions .el-button {
  flex: 1;
}

.preview-content {
  text-align: center;
}

.preview-info {
  margin-top: 20px;
  text-align: left;
  background: var(--el-fill-color-lighter);
  padding: 16px;
  border-radius: 4px;
}

.preview-info p {
  margin: 8px 0;
  font-size: 14px;
  color: var(--el-text-color-regular);
  word-break: break-all;
}
</style>
