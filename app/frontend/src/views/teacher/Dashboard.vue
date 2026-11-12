<template>
  <div class="teacher-dashboard">
    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <div class="welcome-content">
        <h1>欢迎回来，{{ teacherName }}！</h1>
        <p>今天有 <strong>{{ ongoingExams.length }}</strong> 场考试需要监控，<strong>{{ pendingHomework.length }}</strong> 份作业待批改</p>
      </div>
      <div class="quick-actions">
        <el-button type="primary" @click="$router.push('/teacher/exam')">
          <el-icon><Plus /></el-icon>
          创建考试
        </el-button>
        <el-button @click="$router.push('/teacher/question')">
          <el-icon><Document /></el-icon>
          添加试题
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon students">
          <el-icon :size="24"><User /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalStudents }}</div>
          <div class="stat-label">学生总数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon exams">
          <el-icon :size="24"><Document /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalExams }}</div>
          <div class="stat-label">考试总数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon questions">
          <el-icon :size="24"><List /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalQuestions }}</div>
          <div class="stat-label">题库试题</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon pass-rate">
          <el-icon :size="24"><TrendCharts /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.avgPassRate }}%</div>
          <div class="stat-label">平均通过率</div>
        </div>
      </div>
    </div>

    <!-- AI 今日简报 -->
    <div class="ai-digest-card">
      <div class="adc-glow"></div>
      <div class="adc-head">
        <div class="adc-title">
          <span class="adc-icon"><el-icon><MagicStick /></el-icon></span>
          AI 今日简报
          <span class="adc-badge">后台自动生成</span>
        </div>
        <div class="adc-right">
          <span class="adc-time">{{ digestTimeText }}</span>
          <el-button link type="primary" @click="router.push('/teacher/ai-assistant')">
            查看完整分析
            <el-icon class="el-icon--right"><ArrowRight /></el-icon>
          </el-button>
        </div>
      </div>

      <div v-if="digest" class="adc-body">
        <div class="adc-summary">{{ digest.summary }}</div>
        <div class="adc-metrics">
          <div class="adc-metric">
            <span class="m-label">提交率</span>
            <span class="m-value">{{ digest.submitRate }}<small>%</small></span>
          </div>
          <div class="adc-metric">
            <span class="m-label">平均分</span>
            <span class="m-value">{{ digest.avgScore }}</span>
          </div>
          <div class="adc-metric">
            <span class="m-label">需关注</span>
            <span class="m-value warn">{{ digest.attention.length }}<small>人</small></span>
          </div>
          <div class="adc-metric">
            <span class="m-label">及格率</span>
            <span class="m-value">{{ digest.passRate }}<small>%</small></span>
          </div>
        </div>
      </div>
      <div v-else class="adc-body adc-loading">
        <el-icon class="is-loading"><Loading /></el-icon>
        正在后台汇总学生提交…
      </div>

      <div v-if="digest && digest.commonIssues.length" class="adc-issues">
        <span class="is-label">共性问题：</span>
        <span v-for="it in digest.commonIssues.slice(0, 4)" :key="it.label" class="is-chip">
          {{ it.label }}
          <i>{{ it.studentCount }}人</i>
        </span>
      </div>
    </div>

    <!-- 主要内容区 -->
    <div class="main-content">
      <div class="left-content">
        <!-- 今日课程 -->
        <div class="card today-schedule">
          <div class="card-header">
            <h3>今日课程安排</h3>
            <el-button type="primary" link @click="router.push('/teacher/classroom')">查看全部</el-button>
          </div>
          <div class="schedule-list">
            <div v-for="item in todaySchedule" :key="item.id" class="schedule-item">
              <div class="schedule-time">{{ item.time }}</div>
              <div class="schedule-info">
                <div class="schedule-title">{{ item.title }}</div>
                <div class="schedule-meta">
                  <span>{{ item.class }}</span>
                  <span>{{ item.students }}人</span>
                </div>
              </div>
              <el-button type="primary" size="small" @click="enterClassroom(item)">
                进入课堂
              </el-button>
            </div>
          </div>
        </div>

        <!-- 学生成绩趋势 -->
        <div class="card score-trend">
          <div class="card-header">
            <h3>学生成绩趋势</h3>
            <el-select v-model="trendRange" size="small" style="width: 120px">
              <el-option label="最近7天" value="7" />
              <el-option label="最近30天" value="30" />
            </el-select>
          </div>
          <div class="chart-container" ref="trendChartRef"></div>
        </div>

        <!-- 待批改作业 -->
        <div class="card pending-homework">
          <div class="card-header">
            <h3>待批改作业</h3>
            <el-button type="primary" link @click="router.push('/teacher/homework')">查看全部</el-button>
          </div>
          <el-table :data="pendingHomework" style="width: 100%">
            <el-table-column prop="title" label="作业名称" min-width="150" show-overflow-tooltip />
            <el-table-column prop="class" label="等级" width="100" show-overflow-tooltip />
            <el-table-column prop="submitted" label="已提交" width="80" align="center" />
            <el-table-column prop="deadline" label="截止时间" width="110" />
            <el-table-column label="操作" width="80" align="center">
              <template #default>
                <el-button type="primary" link size="small">批改</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>

      <div class="right-content">
        <!-- 进行中的考试 -->
        <div class="card ongoing-exams">
          <div class="card-header">
            <h3>进行中的考试</h3>
          </div>
          <div class="exam-list">
            <div v-for="exam in ongoingExams" :key="exam.id" class="exam-item">
              <div class="exam-info">
                <div class="exam-title">{{ exam.title }}</div>
                <div class="exam-meta">
                  <span>{{ exam.participants }}人参加</span>
                  <span>剩余{{ exam.remaining }}分钟</span>
                </div>
              </div>
              <el-button type="primary" size="small" @click="monitorExam(exam)">
                监控
              </el-button>
            </div>
          </div>
        </div>

        <!-- 学习预警 -->
        <div class="card learning-alerts">
          <div class="card-header">
            <h3>学习预警</h3>
            <el-badge :value="alerts.length" type="danger" />
          </div>
          <div class="alert-list">
            <div v-if="alerts.length === 0" class="empty-alert">
              暂无学习预警
            </div>
            <div v-for="alert in alerts" :key="alert.id" class="alert-item">
              <el-avatar :size="32">{{ (alert.student || '-').charAt(0) }}</el-avatar>
              <div class="alert-info">
                <div class="alert-student">{{ alert.student }}</div>
                <div class="alert-reason">{{ alert.reason }}</div>
              </div>
              <el-button type="primary" link size="small" @click="viewAlertStudent(alert)">查看</el-button>
            </div>
          </div>
        </div>

        <!-- 题库统计 -->
        <div class="card question-stats">
          <div class="card-header">
            <h3>题库统计</h3>
          </div>
          <div class="chart-container" ref="questionChartRef"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { Plus, Document, User, List, TrendCharts } from '@element-plus/icons-vue'
import { teacherApi } from '@/api/teacher'
import { useUserStore } from '@/stores/user'
import { readCache, runDigest, timeAgo } from '@/ai/agent/digestAgent'

const router = useRouter()

// ---------- AI 今日简报 ----------
const digest = ref(null)
const digestAt = ref(0)
const digestTimeText = computed(() => (digestAt.value ? `上次分析 ${timeAgo(digestAt.value)}` : '尚未分析'))

async function loadDigest() {
  const cached = readCache()
  if (cached?.digest) {
    digest.value = cached.digest
    digestAt.value = cached.at
    return
  }
  try {
    const d = await runDigest()
    digest.value = d
    digestAt.value = d.generatedAt
  } catch (e) {
    // 静默失败，不打扰仪表板
  }
}
const userStore = useUserStore()

const trendRange = ref('7')
const trendChartRef = ref()
const questionChartRef = ref()
let trendChart = null
let questionChart = null

// 教师名称
const teacherName = ref(userStore.userInfo?.realName || userStore.userInfo?.username || '老师')

// 统计数据
const stats = ref({
  totalStudents: 0,
  totalExams: 0,
  totalQuestions: 0,
  avgPassRate: 0
})

// 今日课程
const todaySchedule = ref([])

// 待批改作业
const pendingHomework = ref([])

// 进行中的考试
const ongoingExams = ref([])

// 学习预警
const alerts = ref([])

// 加载仪表板数据
const loadDashboardData = async () => {
  try {
    const res = await teacherApi.getDashboardStats()
    if (res.data) {
      stats.value = {
        totalStudents: res.data.totalStudents || 0,
        totalExams: res.data.totalExams || 0,
        totalQuestions: res.data.totalQuestions || 0,
        avgPassRate: res.data.avgPassRate || 0
      }
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }

  try {
    const scheduleRes = await teacherApi.getTodaySchedule()
    if (scheduleRes.data) {
      todaySchedule.value = scheduleRes.data.map(item => ({
        id: item.id,
        time: item.startTime + '-' + item.endTime,
        title: item.title || item.name,
        class: item.className,
        students: item.studentCount || 0
      }))
    }
  } catch (error) {
    console.error('加载课程安排失败:', error)
  }

  try {
    const homeworkRes = await teacherApi.getPendingHomework({ pageNum: 1, pageSize: 5 })
    if (homeworkRes.data?.records) {
      pendingHomework.value = homeworkRes.data.records.map(item => ({
        id: item.id,
        title: item.title,
        class: item.className,
        submitted: `${item.submittedCount || 0}/${item.totalCount || 0}`,
        deadline: item.deadline?.substring(0, 10)
      }))
    }
  } catch (error) {
    console.error('加载待批改作业失败:', error)
  }

  try {
    const examRes = await teacherApi.getOngoingExams()
    if (examRes.data) {
      ongoingExams.value = (Array.isArray(examRes.data) ? examRes.data : []).map(item => ({
        id: item.id,
        title: item.title || item.examName,
        participants: item.participantCount || 0,
        remaining: item.remainingMinutes || 0
      }))
    }
  } catch (error) {
    console.error('加载进行中考试失败:', error)
  }

  try {
    const alertRes = await teacherApi.getLearningAlerts({ days: 7 })
    const alertData = alertRes.data
    if (Array.isArray(alertData)) {
      alerts.value = alertData.slice(0, 5).map(item => ({
        id: item.id,
        student: item.realName || item.username || '-',
        reason: item.message || item.alertReason || item.reason || '学习异常'
      }))
    } else if (alertData?.records) {
      alerts.value = alertData.records.map(item => ({
        id: item.id,
        student: item.realName || item.username || '-',
        reason: item.message || item.alertReason || item.reason || '学习异常'
      }))
    }
  } catch (error) {
    console.error('加载学习预警失败:', error)
  }
}

const enterClassroom = (item) => {
  router.push(`/teacher/classroom/live/${item.id}`)
}

const monitorExam = (exam) => {
  router.push({ path: '/teacher/exam', query: { monitor: exam.id, examName: exam.title } })
}

const viewAlertStudent = (alert) => {
  if (!alert?.id) return
  router.push(`/teacher/student/detail/${alert.id}`)
}

const initTrendChart = async () => {
  if (!trendChartRef.value) return
  if (!trendChart) {
    trendChart = echarts.init(trendChartRef.value)
  }
  try {
    const trendRes = await teacherApi.getScoreTrend({ days: trendRange.value })
    if (trendRes.data) {
      const series = []
      if (trendRes.data.avgScores) {
        series.push({
          name: '平均分',
          type: 'line',
          smooth: true,
          data: trendRes.data.avgScores
        })
      }
      if (trendRes.data.examCounts) {
        series.push({
          name: '考试人次',
          type: 'bar',
          yAxisIndex: 1,
          data: trendRes.data.examCounts
        })
      }
      trendChart.setOption({
        tooltip: { trigger: 'axis' },
        legend: { bottom: 0 },
        grid: { left: '3%', right: '4%', bottom: '15%', containLabel: true },
        xAxis: { type: 'category', data: trendRes.data.dates || [] },
        yAxis: [
          { type: 'value', name: '平均分' },
          { type: 'value', name: '人次', min: 0 }
        ],
        series: series
      }, true)
    }
  } catch (error) {
    console.error('加载成绩趋势失败:', error)
    trendChart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: [] },
      yAxis: { type: 'value', name: '平均分' },
      series: []
    }, true)
  }
}

const initQuestionChart = async () => {
  if (!questionChartRef.value) return
  if (!questionChart) {
    questionChart = echarts.init(questionChartRef.value)
  }
  try {
    const statsRes = await teacherApi.getQuestionStats()
    if (statsRes.data && statsRes.data.typeStats) {
      const typeMapping = {
        'SINGLE_CHOICE': '单选题',
        'MULTIPLE_CHOICE': '多选题',
        'FILL_BLANK': '填空题',
        'TRUE_FALSE': '判断题',
        'READING': '阅读理解',
        'LISTENING': '听力题',
        'WRITING': '写作题'
      }
      const chartData = Object.entries(statsRes.data.typeStats).map(([key, value]) => ({
        name: typeMapping[key] || key,
        value: value
      }))

      questionChart.setOption({
        tooltip: { trigger: 'item', formatter: '{b}: {c} 题 ({d}%)' },
        legend: { bottom: 0, left: 'center' },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: true,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: true,
            formatter: '{b}: {c}'
          },
          data: chartData.length > 0 ? chartData : [{ name: '暂无数据', value: 1 }]
        }]
      }, true)
    }
  } catch (error) {
    console.error('加载题库统计失败:', error)
    questionChart.setOption({
      tooltip: { trigger: 'item' },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        data: [{ name: '暂无数据', value: 1, itemStyle: { color: '#e0e0e0' } }],
        label: { show: true, formatter: '暂无数据' }
      }]
    }, true)
  }
}

const handleResize = () => {
  trendChart?.resize()
  questionChart?.resize()
}

watch(trendRange, () => {
  initTrendChart()
})

onMounted(async () => {
  loadDashboardData()
  loadDigest()
  await nextTick()
  initTrendChart()
  initQuestionChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
  questionChart?.dispose()
})
</script>

<style lang="scss" scoped>
.teacher-dashboard {
  .welcome-section {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: #f4f8ff;
    border: 1px solid #e1ebfa;
    border-radius: var(--radius-lg);
    padding: 32px;
    margin-bottom: 24px;
    color: var(--text-primary);

    .welcome-content {
      h1 {
        font-size: 24px;
        font-weight: 600;
        margin-bottom: 8px;
      }

      p {
        color: var(--text-secondary);

        strong {
          font-size: 20px;
          color: #3b5b93;
        }
      }
    }

    .quick-actions {
      display: flex;
      gap: 12px;
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
        color: #4d648c;
        border: 1px solid #dbe6f6;

        &.students {
          background: #dfe9ff;
        }

        &.exams {
          background: #e8f5ff;
        }

        &.questions {
          background: #e8f7ef;
        }

        &.pass-rate {
          background: #fff2df;
        }
      }

      .stat-info {
        flex: 1;

        .stat-value {
          font-size: 28px;
          font-weight: 700;
          color: var(--text-primary);
        }

        .stat-label {
          font-size: 13px;
          color: var(--text-secondary);
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

  .today-schedule {
    .schedule-list {
      .schedule-item {
        display: flex;
        align-items: center;
        gap: 16px;
        padding: 16px;
        background: var(--bg-secondary);
        border-radius: var(--radius-md);
        margin-bottom: 12px;

        &:last-child {
          margin-bottom: 0;
        }

        .schedule-time {
          font-size: 14px;
          font-weight: 600;
          color: var(--primary-color);
          white-space: nowrap;
        }

        .schedule-info {
          flex: 1;

          .schedule-title {
            font-size: 14px;
            font-weight: 500;
            color: var(--text-primary);
            margin-bottom: 4px;
          }

          .schedule-meta {
            display: flex;
            gap: 12px;
            font-size: 12px;
            color: var(--text-secondary);
          }
        }
      }
    }
  }

  .score-trend {
    .chart-container {
      height: 280px;
    }
  }

  .ongoing-exams {
    .exam-list {
      .exam-item {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 16px;
        background: var(--bg-secondary);
        border-radius: var(--radius-md);
        margin-bottom: 12px;

        .exam-info {
          .exam-title {
            font-size: 14px;
            font-weight: 500;
            color: var(--text-primary);
            margin-bottom: 4px;
          }

          .exam-meta {
            display: flex;
            gap: 12px;
            font-size: 12px;
            color: var(--text-secondary);
          }
        }
      }
    }
  }

  .learning-alerts {
    .alert-list {
      .empty-alert {
        text-align: center;
        padding: 24px;
        color: var(--text-secondary);
        font-size: 14px;
      }

      .alert-item {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 12px 0;
        border-bottom: 1px solid var(--border-light);

        &:last-child {
          border-bottom: none;
        }

        .alert-info {
          flex: 1;

          .alert-student {
            font-size: 14px;
            font-weight: 500;
            color: var(--text-primary);
          }

          .alert-reason {
            font-size: 12px;
            color: var(--danger-color);
          }
        }
      }
    }
  }

  .question-stats {
    .chart-container {
      height: 200px;
    }
  }
}

/* ==================== AI 今日简报 ==================== */
.ai-digest-card {
  position: relative;
  margin: 16px 0;
  padding: 18px 22px;
  border-radius: 16px;
  background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
  border: 1px solid #e2ebfa;
  box-shadow: 0 16px 40px -30px rgba(16, 58, 140, 0.55);
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    border-radius: inherit;
    padding: 1px;
    background: linear-gradient(120deg, rgba(37, 99, 235, 0.5), rgba(56, 189, 248, 0.12) 40%, transparent 66%);
    -webkit-mask: linear-gradient(#000 0 0) content-box, linear-gradient(#000 0 0);
    -webkit-mask-composite: xor;
    mask-composite: exclude;
    pointer-events: none;
  }
}

.adc-glow {
  position: absolute;
  width: 320px;
  height: 240px;
  right: -90px;
  top: -110px;
  background: radial-gradient(circle, rgba(56, 189, 248, 0.22), transparent 68%);
  filter: blur(18px);
  pointer-events: none;
}

.adc-head {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  flex-wrap: wrap;
}

.adc-title {
  display: flex;
  align-items: center;
  gap: 9px;
  font-size: 14.5px;
  font-weight: 750;
  color: #0e1a32;
}

.adc-icon {
  width: 28px;
  height: 28px;
  border-radius: 8px;
  display: grid;
  place-items: center;
  font-size: 15px;
  color: #fff;
  background: linear-gradient(135deg, #2563eb, #38bdf8);
  box-shadow: 0 8px 16px -10px rgba(37, 99, 235, 0.9);
}

.adc-badge {
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.3px;
  color: #1d4ed8;
  background: rgba(37, 99, 235, 0.1);
  border: 1px solid rgba(37, 99, 235, 0.2);
  padding: 1px 8px;
  border-radius: 20px;
}

.adc-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.adc-time {
  font-size: 11px;
  color: #a9b6c9;
}

.adc-body {
  position: relative;
  display: flex;
  align-items: center;
  gap: 20px;
  margin-top: 13px;
  flex-wrap: wrap;
}

.adc-loading {
  font-size: 12.5px;
  color: #8494ae;
  gap: 8px;
}

.adc-summary {
  flex: 1;
  min-width: 320px;
  font-size: 12.5px;
  line-height: 1.8;
  color: #4e5f7c;
}

.adc-metrics {
  display: flex;
  gap: 22px;
}

.adc-metric {
  text-align: right;

  .m-label {
    display: block;
    font-size: 10.5px;
    color: #a9b6c9;
    margin-bottom: 3px;
  }

  .m-value {
    font-size: 19px;
    font-weight: 800;
    color: #0e1a32;
    font-variant-numeric: tabular-nums;
    letter-spacing: -0.6px;

    small {
      font-size: 10px;
      font-weight: 600;
      color: #8494ae;
      margin-left: 2px;
    }

    &.warn {
      color: #d97706;
    }
  }
}

.adc-issues {
  position: relative;
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  margin-top: 13px;
  padding-top: 12px;
  border-top: 1px dashed #e8effb;

  .is-label {
    font-size: 11px;
    color: #a9b6c9;
  }
}

.is-chip {
  font-size: 11px;
  padding: 3px 9px;
  border-radius: 5px;
  background: #fff7ed;
  border: 1px solid #fde9cf;
  color: #b45309;
  font-weight: 600;

  i {
    font-style: normal;
    font-weight: 700;
    color: #d97706;
    margin-left: 5px;
    font-size: 10px;
  }
}
</style>
