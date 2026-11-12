<template>
  <div class="homework-management">
    <el-card class="page-header">
      <div class="header-content">
        <h2>作业管理</h2>
        <el-button type="primary" @click="showCreateDialog">
          <el-icon><Plus /></el-icon>
          布置作业
        </el-button>
      </div>
    </el-card>

    <!-- 搜索和筛选 -->
    <el-card class="filter-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="关键词">
          <el-input v-model="queryParams.keyword" placeholder="作业标题" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable>
            <el-option label="进行中" value="1" />
            <el-option label="已截止" value="2" />
            <el-option label="草稿" value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchHomeworks">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 作业列表 -->
    <el-card class="list-card">
      <el-table :data="homeworkList" v-loading="loading" stripe>
        <el-table-column prop="title" label="作业标题" min-width="200" />
        <el-table-column prop="description" label="描述" min-width="150" show-overflow-tooltip />
        <el-table-column prop="totalScore" label="总分" width="80" align="center" />
        <el-table-column prop="deadline" label="截止时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.deadline) }}
          </template>
        </el-table-column>
        <el-table-column label="提交情况" width="120" align="center">
          <template #default="{ row }">
            <el-tag>{{ row.submittedCount || 0 }} / {{ row.totalCount || 0 }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewSubmissions(row)">查看提交</el-button>
            <el-button type="warning" link @click="editHomework(row)">编辑</el-button>
            <el-button type="danger" link @click="deleteHomework(row)">删除</el-button>
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

    <!-- 创建/编辑作业对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑作业' : '布置作业'" width="600px">
      <el-form :model="homeworkForm" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="作业标题" prop="title">
          <el-input v-model="homeworkForm.title" placeholder="请输入作业标题" />
        </el-form-item>
        <el-form-item label="作业描述" prop="description">
          <el-input v-model="homeworkForm.description" type="textarea" :rows="3" placeholder="请输入作业描述" />
        </el-form-item>
        <el-form-item label="作业内容" prop="content">
          <el-input v-model="homeworkForm.content" type="textarea" :rows="5" placeholder="请输入作业内容要求" />
        </el-form-item>
        <el-form-item label="总分" prop="totalScore">
          <el-input-number v-model="homeworkForm.totalScore" :min="1" :max="100" />
        </el-form-item>
        <el-form-item label="截止时间" prop="deadline">
          <el-date-picker
            v-model="homeworkForm.deadline"
            type="datetime"
            placeholder="选择截止时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DDTHH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="作业类型" prop="homeworkType">
          <el-select v-model="homeworkForm.homeworkType">
            <el-option label="普通作业" value="NORMAL" />
            <el-option label="写作作业" value="WRITING" />
            <el-option label="口语作业" value="SPEAKING" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitHomework">确定</el-button>
      </template>
    </el-dialog>

    <!-- 提交列表对话框 -->
    <el-dialog v-model="submissionDialogVisible" title="作业提交列表" width="800px">
      <el-table :data="submissionList" v-loading="submissionLoading">
        <el-table-column prop="studentName" label="学生姓名" width="120" />
        <el-table-column prop="submitTime" label="提交时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.submitTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'GRADED' ? 'success' : 'warning'">
              {{ row.status === 'GRADED' ? '已批改' : '待批改' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="得分" width="80" align="center" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="primary" link @click="gradeSubmission(row)">
              {{ row.status === 'GRADED' ? '查看' : '批改' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 批改对话框 -->
    <el-dialog v-model="gradeDialogVisible" title="批改作业" width="600px">
      <div class="submission-content">
        <h4>学生提交内容：</h4>
        <div class="content-box">{{ currentSubmission.content }}</div>
      </div>
      <el-form :model="gradeForm" label-width="80px" class="grade-form">
        <el-form-item label="得分">
          <el-input-number v-model="gradeForm.score" :min="0" :max="currentHomework.totalScore || 100" />
        </el-form-item>
        <el-form-item label="评语">
          <el-input v-model="gradeForm.feedback" type="textarea" :rows="3" placeholder="请输入评语" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="gradeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitGrade">提交批改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { homeworkApi } from '@/api/homework'

const loading = ref(false)
const homeworkList = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  status: ''
})

const homeworkForm = reactive({
  id: null,
  title: '',
  description: '',
  content: '',
  totalScore: 100,
  deadline: '',
  homeworkType: 'NORMAL'
})

const rules = {
  title: [{ required: true, message: '请输入作业标题', trigger: 'blur' }],
  deadline: [{ required: true, message: '请选择截止时间', trigger: 'change' }]
}

// 提交列表相关
const submissionDialogVisible = ref(false)
const submissionLoading = ref(false)
const submissionList = ref([])
const currentHomework = ref({})

// 批改相关
const gradeDialogVisible = ref(false)
const currentSubmission = ref({})
const gradeForm = reactive({
  score: 0,
  feedback: ''
})

const fetchHomeworks = async () => {
  loading.value = true
  try {
    const res = await homeworkApi.getTeacherHomeworks(queryParams)
    homeworkList.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('获取作业列表失败:', error)
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  queryParams.keyword = ''
  queryParams.status = ''
  queryParams.pageNum = 1
  fetchHomeworks()
}

const showCreateDialog = () => {
  isEdit.value = false
  Object.assign(homeworkForm, {
    id: null,
    title: '',
    description: '',
    content: '',
    totalScore: 100,
    deadline: '',
    homeworkType: 'NORMAL'
  })
  dialogVisible.value = true
}

const editHomework = (row) => {
  isEdit.value = true
  Object.assign(homeworkForm, row)
  dialogVisible.value = true
}

const submitHomework = async () => {
  await formRef.value.validate()
  try {
    if (isEdit.value) {
      await homeworkApi.updateHomework(homeworkForm.id, homeworkForm)
      ElMessage.success('更新成功')
    } else {
      await homeworkApi.createHomework(homeworkForm)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchHomeworks()
  } catch (error) {
    console.error('操作失败:', error)
  }
}

const deleteHomework = async (row) => {
  await ElMessageBox.confirm('确定要删除该作业吗？', '提示', { type: 'warning' })
  try {
    await homeworkApi.deleteHomework(row.id)
    ElMessage.success('删除成功')
    fetchHomeworks()
  } catch (error) {
    console.error('删除失败:', error)
  }
}

const viewSubmissions = async (row) => {
  currentHomework.value = row
  submissionDialogVisible.value = true
  submissionLoading.value = true
  try {
    const res = await homeworkApi.getHomeworkSubmissions(row.id, { pageNum: 1, pageSize: 100 })
    submissionList.value = res.data.records || []
  } catch (error) {
    console.error('获取提交列表失败:', error)
  } finally {
    submissionLoading.value = false
  }
}

const gradeSubmission = (row) => {
  currentSubmission.value = row
  gradeForm.score = row.score || 0
  gradeForm.feedback = row.feedback || ''
  gradeDialogVisible.value = true
}

const submitGrade = async () => {
  try {
    await homeworkApi.gradeHomework(currentSubmission.value.id, gradeForm)
    ElMessage.success('批改成功')
    gradeDialogVisible.value = false
    viewSubmissions(currentHomework.value)
  } catch (error) {
    console.error('批改失败:', error)
  }
}

const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

const getStatusType = (status) => {
  const types = { 0: 'info', 1: 'success', 2: 'warning' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 0: '草稿', 1: '进行中', 2: '已截止' }
  return texts[status] || '未知'
}

onMounted(() => {
  fetchHomeworks()
})
</script>

<style scoped>
.homework-management {
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

.filter-card {
  margin-bottom: 20px;
}

.list-card {
  margin-bottom: 20px;
}

.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}

.submission-content {
  margin-bottom: 20px;
}

.content-box {
  background: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
  white-space: pre-wrap;
  max-height: 200px;
  overflow-y: auto;
}

.grade-form {
  margin-top: 20px;
}
</style>
