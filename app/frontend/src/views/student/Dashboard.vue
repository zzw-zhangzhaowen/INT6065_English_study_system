<template>
  <div class="dashboard">
    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <div class="welcome-content">
        <h1>欢迎回来，{{ userInfo.realName || userInfo.username }}！</h1>
        <p>今天是学习的第 <strong>{{ userInfo.studyDays }}</strong> 天，继续加油！</p>
      </div>
      <div class="quick-actions">
        <el-button type="primary" @click="$router.push('/student/ai-tutor/chat')">
          <el-icon><ChatDotRound /></el-icon>
          问 AI 老师
        </el-button>
        <el-button @click="$router.push('/student/exam')">
          <el-icon><Document /></el-icon>
          去考试
        </el-button>
      </div>
    </div>

    <!-- 首屏主线：今天做什么。统计与图表收进下方折叠区，避免一进来就被数字淹没 -->
    <div class="focus-grid">
      <div class="card today-tasks">
        <div class="card-header">
          <h3>今日任务</h3>
          <el-button type="primary" link @click="$router.push('/student/learning')">
            查看全部
          </el-button>
        </div>
        <div v-if="todayTasks.length" class="task-list">
          <div
            v-for="task in todayTasks"
            :key="task.id"
            class="task-item"
            :class="{ completed: task.completed }"
          >
            <el-checkbox v-model="task.completed" @change="toggleTask(task)" />
            <div class="task-info">
              <div class="task-title">{{ task.title }}</div>
              <div class="task-meta">
                <el-tag size="small" :type="task.type === 'exam' ? 'danger' : 'primary'">
                  {{ task.type === 'exam' ? '考试' : '学习' }}
                </el-tag>
                <span class="task-time">{{ task.time }}</span>
              </div>
            </div>
            <el-button type="primary" size="small" @click="goToTask(task)">
              {{ task.completed ? '查看' : '开始' }}
            </el-button>
          </div>
        </div>
        <el-empty v-else description="今天没有安排任务，去学习中心挑一个吧" :image-size="70" />
      </div>

      <div class="card wrong-questions">
        <div class="card-header">
          <h3>错题待复习</h3>
          <el-button type="primary" link @click="$router.push('/student/learning/wrong')">
            进入错题本
          </el-button>
        </div>
        <div class="wrong-stats">
          <div class="wrong-stat-item">
            <div class="wrong-count">{{ wrongStats.total }}</div>
            <div class="wrong-label">总错题</div>
          </div>
          <div class="wrong-stat-item">
            <div class="wrong-count warning">{{ wrongStats.needReview }}</div>
            <div class="wrong-label">待复习</div>
          </div>
          <div class="wrong-stat-item">
            <div class="wrong-count success">{{ wrongStats.mastered }}</div>
            <div class="wrong-label">已掌握</div>
          </div>
        </div>
        <el-button type="primary" class="review-btn" @click="startReview">
          开始复习
        </el-button>
      </div>
    </div>

    <!-- 学习数据默认收起 -->
    <el-collapse v-model="activePanels" class="data-collapse">
      <el-collapse-item name="data" title="学习数据与趋势（累计时长 · 考试次数 · 正确率 · 学习进度 · 能力分析 · 最近考试）">
        <!-- 统计卡片 -->
        <div class="stats-cards">
      <div class="stat-card" @click="handleCardClick('studyTime')" style="cursor: pointer;">
        <div class="stat-icon study-time">
          <el-icon :size="24"><Timer /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.studyTime }}<span class="unit">小时</span></div>
          <div class="stat-label">累计学习时长</div>
        </div>
      </div>
      <div class="stat-card" @click="handleCardClick('examCount')" style="cursor: pointer;">
        <div class="stat-icon exam-count">
          <el-icon :size="24"><Document /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.examCount }}<span class="unit">次</span></div>
          <div class="stat-label">完成考试</div>
        </div>
      </div>
      <div class="stat-card" @click="handleCardClick('correctRate')" style="cursor: pointer;">
        <div class="stat-icon correct-rate">
          <el-icon :size="24"><TrendCharts /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.correctRate }}<span class="unit">%</span></div>
          <div class="stat-label">平均正确率</div>
        </div>
      </div>
      <div class="stat-card" @click="handleCardClick('streak')" style="cursor: pointer;">
        <div class="stat-icon streak">
          <el-icon :size="24"><Sunny /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.streak }}<span class="unit">天</span></div>
          <div class="stat-label">连续学习</div>
        </div>
      </div>
    </div>

    <!-- 主要内容区 -->
    <div class="main-content">
      <!-- 左侧内容 -->
      <div class="left-content">
        <!-- 学习进度 -->
        <div class="card study-progress">
          <div class="card-header">
            <h3>学习进度</h3>
            <el-button type="primary" link @click="$router.push('/student/profile')">
              详细报告
            </el-button>
          </div>
          <div class="progress-chart" ref="progressChartRef"></div>
        </div>

        <!-- 最近考试 -->
        <div class="card recent-exams">
          <div class="card-header">
            <h3>最近考试</h3>
            <el-button type="primary" link @click="$router.push('/student/exam')">
              查看全部
            </el-button>
          </div>
          <el-table :data="recentExams" style="width: 100%">
            <el-table-column prop="name" label="考试名称" />
            <el-table-column prop="score" label="得分" width="100">
              <template #default="{ row }">
                <span :class="getScoreClass(row.score)">{{ row.score }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="date" label="考试时间" width="120" />
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button type="primary" link size="small" @click="viewExamResult(row)">
                  查看详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>

      <!-- 右侧内容 -->
      <div class="right-content">
        <!-- 能力雷达图 -->
        <div class="card ability-radar">
          <div class="card-header">
            <h3>能力分析</h3>
          </div>
          <div class="radar-chart" ref="radarChartRef"></div>
        </div>
      </div>
    </div>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import {
  Document, ChatDotRound, Timer, TrendCharts, Sunny
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { studentApi } from '@/api/student'
import { examApi } from '@/api/exam'
import { learningApi } from '@/api/learning'
import { communityApi } from '@/api/community'

const router = useRouter()
const userStore = useUserStore()

// 用户信息
const displayName = userStore.userInfo?.realName || userStore.userInfo?.nickname || userStore.userInfo?.username || '学习者'
const userInfo = ref({
  nickname: displayName,
  realName: displayName,
  username: userStore.userInfo?.username || '',
  studyDays: 1
})

// 统计数据
const stats = ref({
  studyTime: 0,
  examCount: 0,
  correctRate: 0,
  streak: 0
})

// 今日任务
const todayTasks = ref([])

// 最近考试
const recentExams = ref([])

// 错题统计
const wrongStats = ref({
  total: 0,
  needReview: 0,
  mastered: 0
})

// 社区帖子
const communityPosts = ref([])

// 图表引用
const progressChartRef = ref()
const radarChartRef = ref()
let progressChart = null
let radarChart = null

// 加载仪表板数据
const loadDashboardData = async () => {
  try {
    // 加载统计数据
    const statsRes = await studentApi.getDashboardStats()
    if (statsRes.data) {
      stats.value = {
        studyTime: statsRes.data.studyTime || 0,
        examCount: statsRes.data.examCount || 0,
        correctRate: statsRes.data.correctRate || 0,
        streak: statsRes.data.streak || 0
      }
      userInfo.value.studyDays = statsRes.data.studyDays || 1
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
    // 使用默认数据
    stats.value = { studyTime: 0, examCount: 0, correctRate: 0, streak: 0 }
    userInfo.value.studyDays = 1
  }

  try {
    // 加载最近考试记录
    const examRes = await examApi.getExamRecords({ pageNum: 1, pageSize: 5 })
    if (examRes.data?.records) {
      const records = examRes.data.records.filter(item => item.examId) // 过滤无examId的记录
      // 如果后端未返回examName，前端补充查询
      const needFetchNames = records.some(item => !item.examName && item.examId)
      let examNameMap = {}
      if (needFetchNames) {
        const examIds = [...new Set(records.map(r => r.examId).filter(Boolean))]
        const nameResults = await Promise.allSettled(
          examIds.map(id => examApi.getExamDetail(id, { silent: true }).catch(() => ({ data: null })))
        )
        nameResults.forEach((result, index) => {
          if (result.status === 'fulfilled' && result.value?.data) {
            const exam = result.value.data.exam || result.value.data
            examNameMap[examIds[index]] = exam.examName || exam.name || ''
          }
        })
      }
      recentExams.value = records.map(item => ({
        id: item.id,
        examId: item.examId,
        name: item.examName || examNameMap[item.examId] || `考试#${item.examId}`,
        score: item.obtainedScore ?? '-',
        date: item.submitTime?.substring(0, 10) || item.createTime?.substring(0, 10)
      }))
    }
  } catch (error) {
    console.error('加载考试记录失败:', error)
    recentExams.value = []
  }

  try {
    // 加载错题统计
    const wrongRes = await learningApi.getWrongQuestionStatistics()
    if (wrongRes.data) {
      wrongStats.value = {
        total: wrongRes.data.total || 0,
        needReview: wrongRes.data.notMastered || 0,
        mastered: wrongRes.data.mastered || 0
      }
    }
  } catch (error) {
    console.error('加载错题统计失败:', error)
    wrongStats.value = { total: 0, needReview: 0, mastered: 0 }
  }

  try {
    // 加载社区帖子
    const postRes = await communityApi.getPostList({ pageNum: 1, pageSize: 3 })
    if (postRes.data?.records) {
      communityPosts.value = postRes.data.records.map(item => ({
        id: item.id,
        author: item.username || '匿名用户',
        title: item.title,
        views: item.viewCount || 0,
        comments: item.replyCount || 0,
        avatar: item.avatar || ''
      }))
    }
  } catch (error) {
    console.error('加载社区帖子失败:', error)
  }

  // 加载今日任务
  try {
    const planRes = await learningApi.getPlanList({ pageNum: 1, pageSize: 5, status: 'IN_PROGRESS' })
    if (planRes.data?.records) {
      todayTasks.value = planRes.data.records.map(item => ({
        id: item.id,
        title: item.planName || item.title,
        type: item.planType === 'EXAM' ? 'exam' : 'study',
        time: item.estimatedTime ? `预计${item.estimatedTime}分钟` : '',
        completed: item.status === 'COMPLETED'
      }))
    }
  } catch (error) {
    console.error('加载今日任务失败:', error)
  }
}

const toggleTask = async (task) => {
  // 更新任务状态
  try {
    const newStatus = task.completed ? 'COMPLETED' : 'IN_PROGRESS'
    await learningApi.updatePlan(task.id, { status: newStatus })
  } catch (error) {
    console.error('更新任务状态失败:', error)
    // 回滚状态
    task.completed = !task.completed
  }
}

const goToTask = (task) => {
  if (task.type === 'exam') {
    router.push('/student/exam')
  } else {
    router.push('/student/learning')
  }
}

// 处理统计卡片点击事件
const handleCardClick = (cardType) => {
  switch (cardType) {
    case 'studyTime':
      // 累计学习时长 - 跳转到学习中心
      router.push('/student/learning')
      break
    case 'examCount':
      // 完成考试 - 跳转到考试列表
      router.push('/student/exam')
      break
    case 'correctRate':
      // 平均正确率 - 跳转到考试列表（查看历史成绩）
      router.push('/student/exam')
      break
    case 'streak':
      // 连续学习 - 跳转到每日打卡页面
      router.push('/student/checkin')
      break
    default:
      break
  }
}

const getScoreClass = (score) => {
  if (score >= 85) return 'score-excellent'
  if (score >= 60) return 'score-pass'
  return 'score-fail'
}

const viewExamResult = (exam) => {
  router.push(`/student/exam/result/${exam.id}`)
}

const startReview = () => {
  router.push('/student/learning/wrong')
}

// 初始化学习进度图表
const initProgressChart = () => {
  if (!progressChartRef.value) return
  progressChart = echarts.init(progressChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: {
      type: 'value',
      name: '学习时长(分钟)'
    },
    series: [
      {
        name: '学习时长',
        type: 'line',
        smooth: true,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 128, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 128, 255, 0.05)' }
          ])
        },
        lineStyle: {
          color: '#4080ff',
          width: 2
        },
        itemStyle: {
          color: '#4080ff'
        },
        data: [60, 90, 45, 120, 80, 150, 100]
      }
    ]
  }
  progressChart.setOption(option)
}

// 初始化能力雷达图
const initRadarChart = () => {
  if (!radarChartRef.value) return
  radarChart = echarts.init(radarChartRef.value)
  const option = {
    radar: {
      indicator: [
        { name: '听力', max: 100 },
        { name: '阅读', max: 100 },
        { name: '写作', max: 100 },
        { name: '翻译', max: 100 },
        { name: '词汇', max: 100 },
        { name: '语法', max: 100 }
      ],
      radius: '65%'
    },
    series: [
      {
        type: 'radar',
        data: [
          {
            value: [75, 82, 68, 70, 85, 78],
            name: '当前能力',
            areaStyle: {
              color: 'rgba(64, 128, 255, 0.3)'
            },
            lineStyle: {
              color: '#4080ff'
            },
            itemStyle: {
              color: '#4080ff'
            }
          }
        ]
      }
    ]
  }
  radarChart.setOption(option)
}

// 窗口大小变化时重绘图表
const handleResize = () => {
  progressChart?.resize()
  radarChart?.resize()
}

// 学习数据折叠区：默认收起，展开时再初始化图表
const activePanels = ref([])
let chartsInited = false

const initCharts = () => {
  nextTick(() => {
    initProgressChart()
    initRadarChart()
    chartsInited = true
  })
}

watch(activePanels, val => {
  if (val.includes('data') && !chartsInited) initCharts()
})

onMounted(() => {
  loadDashboardData()
  if (activePanels.value.includes('data')) initCharts()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  progressChart?.dispose()
  radarChart?.dispose()
})
</script>

<style lang="scss" scoped>
.dashboard {
  .welcome-section {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: #dbeafe;
    border-radius: var(--radius-lg);
    padding: 32px;
    margin-bottom: 24px;
    color: #1e40af;

    .welcome-content {
      h1 {
        font-size: 24px;
        font-weight: 600;
        margin-bottom: 8px;
      }

      p {
        opacity: 0.9;

        strong {
          font-size: 20px;
          color: #ffd700;
        }
      }
    }

    .quick-actions {
      display: flex;
      gap: 12px;

      .el-button {
        height: 40px;
      }
    }
  }

  // 首屏主线：今日任务 + 错题复习
  .focus-grid {
    display: grid;
    grid-template-columns: 1.55fr 1fr;
    gap: 20px;
    margin-bottom: 20px;
    align-items: start;

    @media (max-width: 1200px) {
      grid-template-columns: 1fr;
    }
  }

  // 学习数据折叠区
  .data-collapse {
    border: none;
    background: transparent;

    :deep(.el-collapse-item__header) {
      height: 50px;
      padding: 0 18px;
      background: #fff;
      border: 1px solid #e7edf8;
      border-radius: 14px;
      font-size: 13px;
      font-weight: 600;
      color: #33415c;
    }

    :deep(.el-collapse-item__wrap) {
      background: transparent;
      border: none;
    }

    :deep(.el-collapse-item__content) {
      padding: 20px 0 0;
    }
  }

  .stats-cards {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
    margin-bottom: 24px;

    @media (max-width: 1200px) {
      grid-template-columns: repeat(2, 1fr);
    }

    .stat-card {
      background: var(--bg-primary);
      border-radius: var(--radius-lg);
      padding: 24px;
      display: flex;
      align-items: center;
      gap: 16px;
      box-shadow: var(--shadow-sm);

      .stat-icon {
        width: 56px;
        height: 56px;
        border-radius: var(--radius-md);
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;

        &.study-time {
          background: #818cf8;
        }

        &.exam-count {
          background: #f9a8d4;
        }

        &.correct-rate {
          background: #67e8f9;
        }

        &.streak {
          background: #fbbf24;
        }
      }

      .stat-info {
        .stat-value {
          font-size: 28px;
          font-weight: 700;
          color: var(--text-primary);

          .unit {
            font-size: 14px;
            font-weight: 400;
            color: var(--text-secondary);
            margin-left: 4px;
          }
        }

        .stat-label {
          font-size: 13px;
          color: var(--text-secondary);
          margin-top: 4px;
        }
      }
    }
  }

  .main-content {
    display: grid;
    grid-template-columns: 1fr 360px;
    gap: 24px;

    @media (max-width: 1200px) {
      grid-template-columns: 1fr;
    }
  }

  .card {
    background: var(--bg-primary);
    border-radius: var(--radius-lg);
    padding: 24px;
    box-shadow: var(--shadow-sm);
    margin-bottom: 24px;

    &:last-child {
      margin-bottom: 0;
    }

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;

      h3 {
        font-size: 16px;
        font-weight: 600;
        color: var(--text-primary);
      }
    }
  }

  .today-tasks {
    .task-list {
      .task-item {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 16px;
        border-radius: var(--radius-md);
        background: var(--bg-secondary);
        margin-bottom: 12px;
        transition: all 0.3s;

        &:last-child {
          margin-bottom: 0;
        }

        &:hover {
          background: var(--bg-tertiary);
        }

        &.completed {
          opacity: 0.6;

          .task-title {
            text-decoration: line-through;
          }
        }

        .task-info {
          flex: 1;

          .task-title {
            font-size: 14px;
            color: var(--text-primary);
            margin-bottom: 6px;
          }

          .task-meta {
            display: flex;
            align-items: center;
            gap: 8px;

            .task-time {
              font-size: 12px;
              color: var(--text-secondary);
            }
          }
        }
      }
    }
  }

  .study-progress {
    .progress-chart {
      height: 280px;
    }
  }

  .recent-exams {
    .score-excellent {
      color: var(--success-color);
      font-weight: 600;
    }

    .score-pass {
      color: var(--warning-color);
      font-weight: 600;
    }

    .score-fail {
      color: var(--danger-color);
      font-weight: 600;
    }
  }

  .ability-radar {
    .radar-chart {
      height: 240px;
    }
  }

  .wrong-questions {
    .wrong-stats {
      display: flex;
      justify-content: space-around;
      margin-bottom: 20px;

      .wrong-stat-item {
        text-align: center;

        .wrong-count {
          font-size: 28px;
          font-weight: 700;
          color: var(--text-primary);

          &.warning {
            color: var(--warning-color);
          }

          &.success {
            color: var(--success-color);
          }
        }

        .wrong-label {
          font-size: 13px;
          color: var(--text-secondary);
          margin-top: 4px;
        }
      }
    }

    .review-btn {
      width: 100%;
    }
  }

  .community-feed {
    .feed-list {
      .feed-item {
        display: flex;
        gap: 12px;
        padding: 12px 0;
        border-bottom: 1px solid var(--border-light);

        &:last-child {
          border-bottom: none;
        }

        .feed-content {
          flex: 1;

          .feed-author {
            font-size: 13px;
            color: var(--text-secondary);
            margin-bottom: 4px;
          }

          .feed-title {
            font-size: 14px;
            color: var(--text-primary);
            margin-bottom: 6px;
            cursor: pointer;

            &:hover {
              color: var(--primary-color);
            }
          }

          .feed-meta {
            display: flex;
            gap: 16px;
            font-size: 12px;
            color: var(--text-placeholder);

            span {
              display: flex;
              align-items: center;
              gap: 4px;
            }
          }
        }
      }
    }
  }
}
</style>
