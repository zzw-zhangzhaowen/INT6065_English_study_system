<template>
  <div class="exam-management">
    <div class="page-header">
      <h2>考试管理</h2>
      <el-button type="primary" @click="createExam">
        <el-icon><Plus /></el-icon>
        创建考试
      </el-button>
    </div>

    <!-- 筛选 -->
    <div class="filter-section">
      <div class="filter-options">
        <el-select v-model="filterStatus" placeholder="考试状态" clearable style="width: 120px">
          <el-option label="全部" value="" />
          <el-option label="未发布" value="unpublished" />
          <el-option label="未开始" value="pending" />
          <el-option label="进行中" value="ongoing" />
          <el-option label="已结束" value="finished" />
        </el-select>
        <el-input v-model="searchKeyword" placeholder="搜索考试名称" :prefix-icon="Search" clearable style="width: 200px" />
      </div>
    </div>

    <!-- 考试列表 -->
    <div v-loading="loading" class="exam-list">
      <el-empty v-if="!loading && exams.length === 0" description="暂无考试数据" />
      <div v-for="exam in exams" :key="exam.id" class="exam-card">
        <div class="exam-header">
          <div class="exam-tags">
            <el-tag v-if="exam.publishStatus === 0" type="warning" size="small">未发布</el-tag>
            <el-tag :type="getStatusType(exam.computedStatus)" size="small">{{ getStatusText(exam.computedStatus) }}</el-tag>
          </div>
          <el-dropdown trigger="click">
            <el-button :icon="MoreFilled" circle size="small" />
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="editExam(exam)">编辑</el-dropdown-item>
                <el-dropdown-item v-if="exam.publishStatus === 0" @click="publishExam(exam)">发布</el-dropdown-item>
                <el-dropdown-item @click="copyExam(exam)">复制</el-dropdown-item>
                <el-dropdown-item v-if="exam.publishStatus === 0" @click="deleteExam(exam)" divided>删除</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
        <h3 class="exam-title">{{ exam.name }}</h3>
        <div class="exam-info">
          <div class="info-item">
            <el-icon><Calendar /></el-icon>
            <span>{{ formatTime(exam.startTime) }}</span>
          </div>
          <div class="info-item">
            <el-icon><Timer /></el-icon>
            <span>{{ exam.duration }}分钟</span>
          </div>
          <div class="info-item">
            <el-icon><Document /></el-icon>
            <span>{{ exam.questionCount || 0 }}题</span>
          </div>
        </div>
        <div class="exam-stats">
          <div class="stat-item">
            <span class="stat-value">{{ exam.totalScore }}</span>
            <span class="stat-label">满分</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">{{ exam.passScore }}</span>
            <span class="stat-label">及格分</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">{{ exam.questionCount || 0 }}</span>
            <span class="stat-label">题目数</span>
          </div>
        </div>
        <div class="exam-actions">
          <el-button size="small" @click="openQuestionDialog(exam)">管理题目</el-button>
          <el-button v-if="exam.computedStatus === 'ongoing'" size="small" @click="openMonitorDialog(exam)">
            监控考试
          </el-button>
          <el-button v-if="exam.computedStatus === 'finished'" size="small" @click="openAnalysisDialog(exam)">
            成绩分析
          </el-button>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <el-pagination
      v-if="totalExams > 0"
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :total="totalExams"
      :page-sizes="[10, 20, 50]"
      layout="total, sizes, prev, pager, next"
      @size-change="loadExams"
      @current-change="loadExams"
      style="margin-top: 20px; justify-content: flex-end;"
    />

    <!-- 创建/编辑考试弹窗 -->
    <el-dialog v-model="showCreateDialog" :title="isEdit ? '编辑考试' : '创建考试'" width="600px">
      <el-form :model="examForm" :rules="formRules" ref="examFormRef" label-width="100px">
        <el-form-item label="考试名称" prop="examName">
          <el-input v-model="examForm.examName" placeholder="请输入考试名称" />
        </el-form-item>
        <el-form-item label="考试类型" prop="examType">
          <el-select v-model="examForm.examType" style="width: 100%">
            <el-option label="模拟考试" value="MOCK" />
            <el-option label="正式考试" value="FORMAL" />
            <el-option label="章节练习" value="CHAPTER" />
            <el-option label="每日练习" value="DAILY" />
          </el-select>
        </el-form-item>
        <el-form-item label="等级">
          <el-select v-model="examForm.level" clearable style="width: 100%">
            <el-option label="CET-4" value="CET4" />
            <el-option label="CET-6" value="CET6" />
            <el-option label="IELTS" value="IELTS" />
            <el-option label="TOEFL" value="TOEFL" />
          </el-select>
        </el-form-item>
        <el-form-item label="考试说明">
          <el-input v-model="examForm.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker v-model="examForm.startTime" type="datetime" placeholder="选择开始时间" style="width: 100%" />
        </el-form-item>
        <el-form-item label="考试时长" prop="duration">
          <el-input-number v-model="examForm.duration" :min="10" :max="300" />
          <span style="margin-left: 8px">分钟</span>
        </el-form-item>
        <el-form-item label="总分">
          <el-input-number v-model="examForm.totalScore" :min="1" :max="1000" />
        </el-form-item>
        <el-form-item label="及格分">
          <el-input-number v-model="examForm.passScore" :min="0" />
        </el-form-item>
        <el-form-item label="最大次数">
          <el-input-number v-model="examForm.maxAttempts" :min="1" :max="10" />
        </el-form-item>
        <el-form-item label="防作弊">
          <el-switch v-model="examForm.enableAntiCheat" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="题目乱序">
          <el-switch v-model="examForm.randomQuestion" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="选项乱序">
          <el-switch v-model="examForm.randomOption" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="考后显示结果">
          <el-switch v-model="examForm.showResult" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="submitExam">{{ isEdit ? '更新' : '创建' }}</el-button>
      </template>
    </el-dialog>

    <!-- 题目管理弹窗 -->
    <ExamQuestionDialog
      v-model="showQuestionDialog"
      :examId="selectedExamId"
      :examName="selectedExamName"
      @updated="loadExams"
    />

    <!-- 成绩分析弹窗 -->
    <ScoreAnalysisDialog
      v-model="showAnalysisDialog"
      :examId="selectedExamId"
      :examName="selectedExamName"
    />

    <!-- 考试监控弹窗 -->
    <ExamMonitorDialog
      v-model="showMonitorDialog"
      :examId="selectedExamId"
      :examName="selectedExamName"
    />
  </div>
</template>

<script setup>
import { ref, reactive, watch, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Calendar, Search, MoreFilled, Timer, Document } from '@element-plus/icons-vue'
import { examApi } from '@/api/exam'
import ExamQuestionDialog from './ExamQuestionDialog.vue'
import ScoreAnalysisDialog from './ScoreAnalysisDialog.vue'
import ExamMonitorDialog from './ExamMonitorDialog.vue'

const loading = ref(false)
const route = useRoute()
const filterStatus = ref('')
const searchKeyword = ref('')
const showCreateDialog = ref(false)
const isEdit = ref(false)
const editingExamId = ref(null)
const examFormRef = ref(null)

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const totalExams = ref(0)

// 弹窗控制
const showQuestionDialog = ref(false)
const showAnalysisDialog = ref(false)
const showMonitorDialog = ref(false)
const selectedExamId = ref(null)
const selectedExamName = ref('')

// 考试列表
const exams = ref([])

// 表单校验规则
const formRules = {
  examName: [{ required: true, message: '请输入考试名称', trigger: 'blur' }],
  examType: [{ required: true, message: '请选择考试类型', trigger: 'change' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  duration: [{ required: true, message: '请输入考试时长', trigger: 'blur' }]
}

// 考试表单
const examForm = reactive({
  examName: '',
  examType: 'MOCK',
  level: '',
  description: '',
  startTime: '',
  duration: 120,
  totalScore: 100,
  passScore: 60,
  maxAttempts: 1,
  enableAntiCheat: 1,
  randomQuestion: 0,
  randomOption: 0,
  showResult: 1
})

const resetForm = () => {
  Object.assign(examForm, {
    examName: '',
    examType: 'MOCK',
    level: '',
    description: '',
    startTime: '',
    duration: 120,
    totalScore: 100,
    passScore: 60,
    maxAttempts: 1,
    enableAntiCheat: 1,
    randomQuestion: 0,
    randomOption: 0,
    showResult: 1
  })
}

// 根据时间计算考试状态
const computeStatus = (exam) => {
  if (!exam.startTime) return 'pending'
  const now = new Date()
  const start = new Date(exam.startTime)
  const end = exam.endTime ? new Date(exam.endTime) : new Date(start.getTime() + (exam.duration || 120) * 60000)
  if (now < start) return 'pending'
  if (now > end) return 'finished'
  return 'ongoing'
}

const getStatusType = (status) => {
  const types = { pending: 'info', ongoing: 'success', finished: '' }
  return types[status] || ''
}

const getStatusText = (status) => {
  const texts = { pending: '未开始', ongoing: '进行中', finished: '已结束' }
  return texts[status] || ''
}

const formatTime = (time) => {
  if (!time) return '-'
  return String(time).substring(0, 16).replace('T', ' ')
}

// 弹窗打开方法
const openQuestionDialog = (exam) => {
  selectedExamId.value = exam.id
  selectedExamName.value = exam.name
  showQuestionDialog.value = true
}

const openMonitorDialog = (exam) => {
  selectedExamId.value = exam.id
  selectedExamName.value = exam.name
  showMonitorDialog.value = true
}

const openAnalysisDialog = (exam) => {
  selectedExamId.value = exam.id
  selectedExamName.value = exam.name
  showAnalysisDialog.value = true
}

const createExam = () => {
  isEdit.value = false
  editingExamId.value = null
  resetForm()
  showCreateDialog.value = true
}

const editExam = async (exam) => {
  isEdit.value = true
  editingExamId.value = exam.id
  try {
    // 从 API 加载完整数据
    const res = await examApi.getExamDetail(exam.id)
    const data = res.data?.exam || res.data || {}
    Object.assign(examForm, {
      examName: data.examName || '',
      examType: data.examType || 'MOCK',
      level: data.level || '',
      description: data.description || '',
      startTime: data.startTime || '',
      duration: data.duration || 120,
      totalScore: data.totalScore || 100,
      passScore: data.passScore || 60,
      maxAttempts: data.maxAttempts || 1,
      enableAntiCheat: data.enableAntiCheat ?? 1,
      randomQuestion: data.randomQuestion ?? 0,
      randomOption: data.randomOption ?? 0,
      showResult: data.showResult ?? 1
    })
    showCreateDialog.value = true
  } catch (error) {
    ElMessage.error('加载考试详情失败')
  }
}

const copyExam = async (exam) => {
  try {
    const res = await examApi.getExamDetail(exam.id)
    const data = res.data?.exam || res.data || {}
    const newExam = {
      examName: (data.examName || exam.name) + ' (副本)',
      examType: data.examType || 'MOCK',
      level: data.level || '',
      startTime: data.startTime,
      duration: data.duration || 120,
      totalScore: data.totalScore || 100,
      passScore: data.passScore || 60,
      maxAttempts: data.maxAttempts || 1,
      enableAntiCheat: data.enableAntiCheat ?? 1,
      randomQuestion: data.randomQuestion ?? 0,
      randomOption: data.randomOption ?? 0,
      showResult: data.showResult ?? 1
    }
    await examApi.createExam(newExam)
    ElMessage.success('考试已复制')
    loadExams()
  } catch (error) {
    ElMessage.error('复制失败')
  }
}

const deleteExam = (exam) => {
  ElMessageBox.confirm('确定要删除这场考试吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await examApi.deleteExam(exam.id)
      ElMessage.success('删除成功')
      loadExams()
    } catch (error) {
      ElMessage.error(error.message || '删除失败')
    }
  }).catch(() => {})
}

const publishExam = async (exam) => {
  try {
    await examApi.publishExam(exam.id)
    ElMessage.success('发布成功')
    loadExams()
  } catch (error) {
    ElMessage.error(error.message || '发布失败')
  }
}

const submitExam = async () => {
  if (!examFormRef.value) return
  try {
    await examFormRef.value.validate()
  } catch {
    return
  }
  try {
    const data = {
      examName: examForm.examName,
      examType: examForm.examType,
      level: examForm.level || undefined,
      description: examForm.description || undefined,
      startTime: examForm.startTime,
      duration: examForm.duration,
      totalScore: examForm.totalScore,
      passScore: examForm.passScore,
      maxAttempts: examForm.maxAttempts,
      enableAntiCheat: examForm.enableAntiCheat,
      randomQuestion: examForm.randomQuestion,
      randomOption: examForm.randomOption,
      showResult: examForm.showResult
    }
    if (isEdit.value && editingExamId.value) {
      await examApi.updateExam(editingExamId.value, data)
      ElMessage.success('考试更新成功')
    } else {
      await examApi.createExam(data)
      ElMessage.success('考试创建成功')
    }
    showCreateDialog.value = false
    loadExams()
  } catch (error) {
    ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
  }
}

const loadExams = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value || undefined,
      forTeacher: true
    }
    // 未发布筛选走前端过滤，时间状态筛选走后端
    if (filterStatus.value && filterStatus.value !== 'unpublished') {
      params.status = filterStatus.value
    }
    const res = await examApi.getExamList(params)
    if (res.data) {
      let records = res.data.records || []
      // 前端过滤未发布
      if (filterStatus.value === 'unpublished') {
        records = records.filter(e => e.status === 0)
      }
      exams.value = records.map(e => ({
        id: e.id,
        name: e.examName,
        publishStatus: e.status, // 0=未发布, 1=已发布
        computedStatus: computeStatus(e),
        startTime: e.startTime,
        endTime: e.endTime,
        duration: e.duration,
        totalScore: e.totalScore || 100,
        passScore: e.passScore || 60,
        questionCount: e.questionCount || 0,
        examType: e.examType
      }))
      totalExams.value = res.data.total || 0
    }
  } catch (error) {
    console.error('加载考试列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 监听筛选条件变化
watch(filterStatus, () => {
  currentPage.value = 1
  loadExams()
})

// 搜索防抖
let searchTimer = null
watch(searchKeyword, () => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => {
    currentPage.value = 1
    loadExams()
  }, 500)
})

onMounted(() => {
  loadExams()
  // 支持从仪表盘跳转直接打开监控弹窗
  if (route.query.monitor) {
    selectedExamId.value = Number(route.query.monitor)
    selectedExamName.value = route.query.examName || ''
    showMonitorDialog.value = true
  }
})
</script>

<style lang="scss" scoped>
.exam-management {
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

  .filter-section {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    margin-bottom: 24px;
    padding: 16px;
    background: var(--bg-primary);
    border-radius: var(--radius-lg);

    .filter-options {
      display: flex;
      gap: 12px;
    }
  }

  .exam-list {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
    gap: 20px;
    min-height: 200px;
  }

  .exam-card {
    background: var(--bg-primary);
    border-radius: var(--radius-lg);
    padding: 24px;
    box-shadow: var(--shadow-sm);

    .exam-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 12px;

      .exam-tags {
        display: flex;
        gap: 6px;
      }
    }

    .exam-title {
      font-size: 16px;
      font-weight: 600;
      color: var(--text-primary);
      margin-bottom: 16px;
    }

    .exam-info {
      display: flex;
      flex-wrap: wrap;
      gap: 16px;
      margin-bottom: 16px;

      .info-item {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: 13px;
        color: var(--text-secondary);
      }
    }

    .exam-stats {
      display: flex;
      justify-content: space-around;
      padding: 16px 0;
      border-top: 1px solid var(--border-light);
      border-bottom: 1px solid var(--border-light);
      margin-bottom: 16px;

      .stat-item {
        text-align: center;

        .stat-value {
          font-size: 20px;
          font-weight: 700;
          color: var(--text-primary);
        }

        .stat-label {
          font-size: 12px;
          color: var(--text-secondary);
        }
      }
    }

    .exam-actions {
      display: flex;
      gap: 8px;
    }
  }
}
</style>
