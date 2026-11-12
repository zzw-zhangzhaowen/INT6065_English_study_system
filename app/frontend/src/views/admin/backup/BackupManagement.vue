<template>
  <div class="backup-management">
    <el-card class="page-header">
      <div class="header-content">
        <h2>系统备份</h2>
        <el-button type="primary" @click="createBackup" :loading="creating">
          <el-icon><Plus /></el-icon>
          创建备份
        </el-button>
      </div>
    </el-card>

    <!-- 备份列表 -->
    <el-card class="list-card">
      <el-table :data="backupList" v-loading="loading" stripe>
        <el-table-column prop="filename" label="备份文件名" min-width="250" />
        <el-table-column prop="size" label="文件大小" width="120">
          <template #default="{ row }">
            {{ formatFileSize(row.size) }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="downloadBackup(row)">
              <el-icon><Download /></el-icon>
              下载
            </el-button>
            <el-popconfirm
              title="确定要恢复此备份吗？这将覆盖当前数据！"
              @confirm="restoreBackup(row)"
            >
              <template #reference>
                <el-button type="warning" link>
                  <el-icon><RefreshRight /></el-icon>
                  恢复
                </el-button>
              </template>
            </el-popconfirm>
            <el-popconfirm
              title="确定要删除此备份吗？"
              @confirm="deleteBackup(row)"
            >
              <template #reference>
                <el-button type="danger" link>
                  <el-icon><Delete /></el-icon>
                  删除
                </el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 上传恢复 -->
    <el-card class="upload-card">
      <template #header>
        <span>上传备份文件恢复</span>
      </template>
      <el-upload
        class="upload-area"
        drag
        action="#"
        :auto-upload="false"
        :on-change="handleFileChange"
        accept=".sql,.zip"
      >
        <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
        <div class="el-upload__text">
          将备份文件拖到此处，或<em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            支持 .sql 或 .zip 格式的备份文件
          </div>
        </template>
      </el-upload>
      <el-button
        type="primary"
        :disabled="!selectedFile"
        :loading="uploading"
        @click="uploadAndRestore"
        style="margin-top: 16px"
      >
        上传并恢复
      </el-button>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Download, RefreshRight, Delete, UploadFilled } from '@element-plus/icons-vue'
import { backupApi } from '@/api/backup'

const loading = ref(false)
const creating = ref(false)
const uploading = ref(false)
const backupList = ref([])
const selectedFile = ref(null)

const fetchBackupList = async () => {
  loading.value = true
  try {
    const res = await backupApi.getBackupList()
    backupList.value = res.data || []
  } catch (error) {
    console.error('获取备份列表失败:', error)
  } finally {
    loading.value = false
  }
}

const createBackup = async () => {
  creating.value = true
  try {
    await backupApi.createBackup()
    ElMessage.success('备份创建成功')
    fetchBackupList()
  } catch (error) {
    ElMessage.error('备份创建失败')
  } finally {
    creating.value = false
  }
}

const downloadBackup = async (row) => {
  try {
    const res = await backupApi.downloadBackup(row.filename)
    const blob = new Blob([res], { type: 'application/octet-stream' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = row.filename
    link.click()
    window.URL.revokeObjectURL(url)
  } catch (error) {
    ElMessage.error('下载失败')
  }
}

const restoreBackup = async (row) => {
  try {
    await backupApi.restoreBackup(row.filename)
    ElMessage.success('备份恢复成功')
  } catch (error) {
    ElMessage.error('备份恢复失败')
  }
}

const deleteBackup = async (row) => {
  try {
    await backupApi.deleteBackup(row.filename)
    ElMessage.success('删除成功')
    fetchBackupList()
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

const handleFileChange = (file) => {
  selectedFile.value = file.raw
}

const uploadAndRestore = async () => {
  if (!selectedFile.value) return

  uploading.value = true
  try {
    await backupApi.uploadAndRestore(selectedFile.value)
    ElMessage.success('上传并恢复成功')
    selectedFile.value = null
    fetchBackupList()
  } catch (error) {
    ElMessage.error('上传恢复失败')
  } finally {
    uploading.value = false
  }
}

const formatFileSize = (bytes) => {
  if (!bytes) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

onMounted(() => {
  fetchBackupList()
})
</script>

<style scoped>
.backup-management {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-content h2 {
  margin: 0;
}

.list-card {
  margin-bottom: 20px;
}

.upload-card {
  margin-bottom: 20px;
}

.upload-area {
  width: 100%;
}
</style>
