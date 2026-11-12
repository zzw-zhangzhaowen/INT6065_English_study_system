<template>
  <div class="exam-list">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>考试中心</h2>
      <p>选择考试类型，开始您的测试之旅</p>
    </div>

    <!-- 考试分类 -->
    <div class="exam-categories">
      <div
        v-for="category in categories"
        :key="category.value"
        class="category-item"
        :class="{ active: activeCategory === category.value }"
        @click="activeCategory = category.value"
      >
        <el-icon :size="20">
          <component :is="category.icon" />
        </el-icon>
        <span>{{ category.label }}</span>
      </div>
    </div>

    <!-- 筛选和搜索 -->
    <div class="filter-bar">
      <div class="filter-left">
        <el-select v-model="filterStatus" placeholder="考试状态" clearable style="width: 120px">
          <el-option label="全部状态" value="" />
          <el-option label="未开始" value="pending" />
          <el-option label="进行中" value="ongoing" />
          <el-option label="已结束" value="finished" />
        </el-select>
        <el-select v-model="filterLevel" placeholder="考试等级" clearable style="width: 120px">
          <el-option label="全部等级" value="" />
          <el-option label="CET-4" value="CET4" />
          <el-option label="CET-6" value="CET6" />
        </el-select>
      </div>
      <div class="filter-right">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索考试名称"
          :prefix-icon="Search"
          clearable
          style="width: 240px"
        />
      </div>
    </div>

    <!-- 考试列表 -->
    <div class="exam-grid" v-loading="loading">
      <div v-for="exam in filteredExams" :key="exam.id" class="exam-card">
        <div class="exam-header">
          <el-tag :type="getStatusType(exam.status)" size="small">
            {{ getStatusText(exam.status) }}
          </el-tag>
          <el-tag type="info" size="small">{{ exam.level }}</el-tag>
        </div>
        <h3 class="exam-title">{{ exam.name }}</h3>
        <div class="exam-info">
          <div class="info-item">
            <el-icon><Calendar /></el-icon>
            <span>{{ formatDate(exam.startTime) }}</span>
          </div>
          <div class="info-item">
            <el-icon><Timer /></el-icon>
            <span>{{ exam.duration }}分钟</span>
          </div>
          <div class="info-item">
            <el-icon><Document /></el-icon>
            <span>{{ exam.questionCount }}题</span>
          </div>
          <div class="info-item">
            <el-icon><Trophy /></el-icon>
            <span>满分{{ exam.totalScore }}分</span>
          </div>
        </div>
        <div class="exam-footer">
          <el-button @click="viewDetail(exam)">查看详情</el-button>
          <el-button
            type="primary"
            :disabled="exam.status === 'finished' || exam.status === 'pending'"
            @click="startExam(exam)"
          >
            {{ exam.status === 'finished' ? '已结束' : exam.status === 'pending' ? '未开始' : '开始考试' }}
          </el-button>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <el-empty v-if="filteredExams.length === 0" description="暂无符合条件的考试" />

    <!-- 分页 -->
    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[12, 24, 36]"
        layout="total, sizes, prev, pager, next, jumper"
        background
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Search, Calendar, Timer, Document, Trophy,
  Grid, List, Clock, Finished, Loading
} from '@element-plus/icons-vue'
import { examApi } from '@/api/exam'

const router = useRouter()

// 分类
const categories = [
  { label: '全部考试', value: 'all', icon: Grid },
  { label: '模拟考试', value: '模拟', icon: Document },
  { label: '正在考试', value: 'ongoing', icon: Trophy },
  { label: '章节练习', value: '专项', icon: List },
  { label: '每日一练', value: '每日', icon: Clock }
]

const activeCategory = ref('all')
const filterStatus = ref('')
const filterLevel = ref('')
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)
const loading = ref(false)

// 考试数据
const exams = ref([])

// 加载考试列表
const loadExams = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value
    }
    
    // 处理分类筛选：如果是'ongoing'则作为状态筛选，否则作为类型筛选
    if (activeCategory.value !== 'all') {
      if (activeCategory.value === 'ongoing') {
        // 注意：只在没有选择状态筛选器时才设置
        if (!filterStatus.value) {
          params.status = 'ongoing'
        }
      } else {
        params.examType = activeCategory.value
      }
    }
    
    // 状态筛选器优先级更高
    if (filterStatus.value) {
      params.status = filterStatus.value
    }
    if (filterLevel.value) {
      params.level = filterLevel.value
    }
    if (searchKeyword.value) {
      params.keyword = searchKeyword.value
    }

    const res = await examApi.getExamList(params)
    if (res.data) {
      const now = new Date()
      exams.value = (res.data.records || []).map(item => {
        // 根据时间判断考试状态
        let examStatus = 'ongoing'
        if (item.startTime && new Date(item.startTime) > now) {
          examStatus = 'pending'
        } else if (item.endTime && new Date(item.endTime) < now) {
          examStatus = 'finished'
        }
        return {
          id: item.id,
          name: item.examName || item.title,
          level: item.examLevel || item.level || 'CET-4',
          category: item.examType || 'mock',
          status: examStatus,
          startTime: item.startTime || item.createTime,
          duration: item.duration || 120,
          questionCount: item.questionCount || 0,
          totalScore: item.totalScore || 100
        }
      })
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('加载考试列表失败:', error)
    ElMessage.error('加载考试列表失败')
  } finally {
    loading.value = false
  }
}

// 筛选已由后端处理，直接使用exams
const filteredExams = computed(() => exams.value)

const getStatusType = (status) => {
  const types = {
    pending: 'info',
    ongoing: 'success',
    finished: ''
  }
  return types[status] || ''
}

const getStatusText = (status) => {
  const texts = {
    pending: '未开始',
    ongoing: '进行中',
    finished: '已结束'
  }
  return texts[status] || ''
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' })
}

const viewDetail = (exam) => {
  router.push(`/student/exam/detail/${exam.id}`)
}

const startExam = (exam) => {
  router.push(`/student/exam/room/${exam.id}`)
}

// 监听筛选条件变化 - 重置页码
watch([activeCategory, filterStatus, filterLevel, searchKeyword], () => {
  currentPage.value = 1
  loadExams()
})

// 监听分页变化
watch([currentPage, pageSize], () => {
  loadExams()
})

onMounted(() => {
  loadExams()
})
</script>

<style lang="scss" scoped>
.exam-list {
  .page-header {
    margin-bottom: 24px;

    h2 {
      font-size: 24px;
      font-weight: 600;
      color: var(--text-primary);
      margin-bottom: 8px;
    }

    p {
      color: var(--text-secondary);
    }
  }

  .exam-categories {
    display: flex;
    gap: 12px;
    margin-bottom: 24px;
    padding: 16px;
    background: var(--bg-primary);
    border-radius: var(--radius-lg);

    .category-item {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 10px 20px;
      border-radius: var(--radius-md);
      cursor: pointer;
      color: var(--text-secondary);
      transition: all 0.3s;

      &:hover {
        background: var(--bg-tertiary);
        color: var(--primary-color);
      }

      &.active {
        background: var(--primary-color);
        color: #fff;
      }
    }
  }

  .filter-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;

    .filter-left {
      display: flex;
      gap: 12px;
    }
  }

  .exam-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
    gap: 20px;
    margin-bottom: 24px;
  }

  .exam-card {
    background: var(--bg-primary);
    border-radius: var(--radius-lg);
    padding: 24px;
    box-shadow: var(--shadow-sm);
    transition: all 0.3s;

    &:hover {
      box-shadow: var(--shadow-md);
      transform: translateY(-2px);
    }

    .exam-header {
      display: flex;
      gap: 8px;
      margin-bottom: 16px;
    }

    .exam-title {
      font-size: 16px;
      font-weight: 600;
      color: var(--text-primary);
      margin-bottom: 16px;
      line-height: 1.4;
    }

    .exam-info {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 12px;
      margin-bottom: 20px;

      .info-item {
        display: flex;
        align-items: center;
        gap: 6px;
        font-size: 13px;
        color: var(--text-secondary);

        .el-icon {
          color: var(--text-placeholder);
        }
      }
    }

    .exam-footer {
      display: flex;
      gap: 12px;

      .el-button {
        flex: 1;
      }
    }
  }

  .pagination-wrapper {
    display: flex;
    justify-content: center;
    padding: 24px 0;
  }
}
</style>
