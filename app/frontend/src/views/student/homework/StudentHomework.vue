<template>
  <div class="student-homework">
    <el-card class="page-header">
      <h2>我的作业</h2>
    </el-card>

    <!-- 作业列表 -->
    <el-card class="list-card">
      <div class="list-header">
        <el-tabs v-model="activeTab" @tab-change="handleTabChange">
          <el-tab-pane label="待完成" name="pending" />
          <el-tab-pane label="已提交" name="submitted" />
          <el-tab-pane label="已批改" name="graded" />
        </el-tabs>
        <div class="search-box">
          <el-input
            v-model="queryParams.keyword"
            placeholder="搜索作业标题"
            clearable
            @clear="handleSearch"
            @keyup.enter="handleSearch"
            style="width: 300px; margin-right: 10px;"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
        </div>
      </div>

      <el-table :data="homeworkList" v-loading="loading" stripe>
        <el-table-column prop="title" label="作业标题" min-width="200" />
        <el-table-column prop="teacherName" label="布置教师" width="120" />
        <el-table-column prop="totalScore" label="总分" width="80" align="center" />
        <el-table-column prop="deadline" label="截止时间" width="180">
          <template #default="{ row }">
            <span :class="{ 'text-danger': isOverdue(row.deadline) }">
              {{ formatDateTime(row.deadline) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row)">{{ getStatusText(row) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="得分" width="80" align="center">
          <template #default="{ row }">
            {{ row.score !== null ? row.score : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewHomework(row)">
              {{ row.submitted ? '查看' : '完成作业' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="fetchHomeworks"
        @current-change="fetchHomeworks"
      />
    </el-card>

    <!-- 作业详情/提交对话框 -->
    <el-dialog v-model="dialogVisible" :title="currentHomework.title" width="700px">
      <div class="homework-detail">
        <div class="detail-section">
          <h4>作业要求</h4>
          <div class="content-box">{{ currentHomework.content }}</div>
        </div>

        <div class="detail-section" v-if="currentHomework.description">
          <h4>作业说明</h4>
          <p>{{ currentHomework.description }}</p>
        </div>

        <div class="detail-info">
          <span>总分：{{ currentHomework.totalScore }} 分</span>
          <span>截止时间：{{ formatDateTime(currentHomework.deadline) }}</span>
        </div>

        <!-- 提交表单 -->
        <div class="submit-section" v-if="!currentHomework.submitted">
          <h4>提交作业</h4>
          <el-form :model="submitForm" ref="formRef">
            <el-form-item prop="content">
              <el-input
                v-model="submitForm.content"
                type="textarea"
                :rows="6"
                placeholder="请输入作业内容"
              />
            </el-form-item>
            <el-form-item>
              <el-upload
                action="#"
                :auto-upload="false"
                :on-change="handleFileChange"
                :file-list="[]"
                :limit="1"
              >
                <el-button type="primary" plain>
                  <el-icon><Upload /></el-icon>
                  上传附件
                </el-button>
              </el-upload>
              <div v-if="submitForm.attachmentUrl" class="file-info">
                已上传文件
              </div>
            </el-form-item>
          </el-form>
        </div>

        <!-- 已提交内容 -->
        <div class="submission-section" v-else>
          <h4>我的提交</h4>
          <div class="content-box">{{ currentSubmission.content }}</div>
          <div class="submission-info">
            <span>提交时间：{{ formatDateTime(currentSubmission.submitTime) }}</span>
          </div>

          <!-- 批改结果 -->
          <div class="grade-section" v-if="currentSubmission.status === 'GRADED'">
            <h4>批改结果</h4>
            <div class="score-display">
              <span class="score">{{ currentSubmission.score }}</span>
              <span class="total">/ {{ currentHomework.totalScore }}</span>
            </div>
            <div class="feedback" v-if="currentSubmission.feedback">
              <h5>教师评语：</h5>
              <p>{{ currentSubmission.feedback }}</p>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button
          v-if="!currentHomework.submitted"
          type="primary"
          @click="submitHomework"
          :loading="submitting"
        >
          提交作业
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Upload, Search } from '@element-plus/icons-vue'
import homeworkApi from '@/api/homework'

const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const activeTab = ref('pending')
const homeworkList = ref([])
const total = ref(0)
const currentHomework = ref({})
const currentSubmission = ref({})

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  status: '',
  keyword: ''
})

const submitForm = reactive({
  content: '',
  attachmentUrl: ''
})

const fetchHomeworks = async () => {
  loading.value = true
  queryParams.status = activeTab.value
  try {
    const res = await homeworkApi.getStudentHomeworks(queryParams)
    homeworkList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取作业列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  queryParams.pageNum = 1
  fetchHomeworks()
}

const handleSearch = () => {
  queryParams.pageNum = 1
  fetchHomeworks()
}

const viewHomework = async (row) => {
  currentHomework.value = row
  if (row.submitted) {
    try {
      const res = await homeworkApi.getMySubmission(row.id)
      currentSubmission.value = res.data || {}
    } catch (error) {
      console.error('获取提交详情失败:', error)
    }
  } else {
    currentSubmission.value = {}
    submitForm.content = ''
    submitForm.attachmentUrl = ''
  }
  dialogVisible.value = true
}

const handleFileChange = async (uploadFile) => {
  try {
    const formData = new FormData()
    formData.append('file', uploadFile.raw)
    const res = await fetch('/api/file/upload', {
      method: 'POST',
      body: formData,
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    const data = await res.json()
    if (data.code === 200) {
      // 确保attachmentUrl为字符串，如果是对象则提取URL
      submitForm.attachmentUrl = typeof data.data === 'string' ? data.data : (data.data?.url || '')
      ElMessage.success('文件上传成功')
    } else {
      ElMessage.error('文件上传失败')
    }
  } catch (error) {
    console.error('文件上传失败:', error)
    ElMessage.error('文件上传失败')
  }
}

const submitHomework = async () => {
  if (!submitForm.content.trim()) {
    ElMessage.warning('请输入作业内容')
    return
  }

  submitting.value = true
  try {
    await homeworkApi.submitHomework(currentHomework.value.id, submitForm)
    ElMessage.success('作业提交成功')
    dialogVisible.value = false
    // 切换到已提交标签页并刷新
    activeTab.value = 'submitted'
    await fetchHomeworks()
  } catch (error) {
    ElMessage.error('提交失败')
  } finally {
    submitting.value = false
  }
}

const isOverdue = (deadline) => {
  return new Date(deadline) < new Date()
}

const getStatusType = (row) => {
  if (row.submissionStatus === 'GRADED') return 'success'
  if (row.submissionStatus === 'SUBMITTED') return 'warning'
  if (isOverdue(row.deadline)) return 'danger'
  return 'info'
}

const getStatusText = (row) => {
  if (row.submissionStatus === 'GRADED') return '已批改'
  if (row.submissionStatus === 'SUBMITTED') return '已提交'
  if (row.submitted && !row.submissionStatus) return '待审核'
  if (isOverdue(row.deadline)) return '已逾期'
  return '待完成'
}

const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

onMounted(() => {
  fetchHomeworks()
})
</script>

<style scoped>
.student-homework {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
}

.list-card {
  margin-bottom: 20px;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.list-header :deep(.el-tabs) {
  flex: 1;
}

.search-box {
  display: flex;
  align-items: center;
}

.text-danger {
  color: #f56c6c;
}

.homework-detail {
  max-height: 60vh;
  overflow-y: auto;
}

.detail-section {
  margin-bottom: 20px;
}

.detail-section h4 {
  margin-bottom: 10px;
  color: #303133;
}

.content-box {
  background: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
  white-space: pre-wrap;
}

.detail-info {
  display: flex;
  gap: 30px;
  color: #909399;
  font-size: 14px;
  margin-bottom: 20px;
}

.submit-section,
.submission-section {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.submission-info {
  margin-top: 10px;
  color: #909399;
  font-size: 14px;
}

.grade-section {
  margin-top: 20px;
  padding: 15px;
  background: #f0f9eb;
  border-radius: 4px;
}

.score-display {
  font-size: 24px;
  margin: 10px 0;
}

.score-display .score {
  color: #67c23a;
  font-weight: bold;
}

.score-display .total {
  color: #909399;
}

.feedback {
  margin-top: 15px;
}

.feedback h5 {
  margin-bottom: 5px;
  color: #606266;
}
</style>
