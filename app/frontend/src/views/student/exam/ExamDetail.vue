<template>
  <div class="exam-detail">
    <!-- 返回按钮 -->
    <div class="back-nav">
      <el-button :icon="ArrowLeft" link @click="$router.back()">返回考试列表</el-button>
    </div>

    <!-- 错误提示 -->
    <el-alert v-if="loadError" :title="loadError" type="error" show-icon :closable="false" style="margin-bottom: 16px">
      <el-button type="primary" link @click="loadExamDetail">重新加载</el-button>
    </el-alert>

    <!-- 考试信息卡片 -->
    <div v-if="!loadError" class="exam-info-card" v-loading="loading">
      <div class="exam-main-info">
        <div class="exam-tags">
          <el-tag :type="getStatusType(exam.status)">{{ getStatusText(exam.status) }}</el-tag>
          <el-tag type="info">{{ exam.level }}</el-tag>
          <el-tag v-if="exam.isAntiCheat" type="warning">防作弊监控</el-tag>
        </div>
        <h1 class="exam-title">{{ exam.name }}</h1>
        <p class="exam-desc">{{ exam.description }}</p>
      </div>

      <div class="exam-stats">
        <div class="stat-item">
          <div class="stat-value">{{ exam.duration }}</div>
          <div class="stat-label">考试时长(分钟)</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ exam.questionCount }}</div>
          <div class="stat-label">题目数量</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ exam.totalScore }}</div>
          <div class="stat-label">满分</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ exam.passScore }}</div>
          <div class="stat-label">及格分</div>
        </div>
      </div>
    </div>

    <!-- 详细信息 -->
    <div class="detail-content">
      <div class="left-section">
        <!-- 考试时间 -->
        <div class="info-card">
          <h3><el-icon><Calendar /></el-icon> 考试时间</h3>
          <div class="time-info">
            <div class="time-item">
              <span class="label">开始时间：</span>
              <span class="value">{{ exam.startTime }}</span>
            </div>
            <div class="time-item">
              <span class="label">结束时间：</span>
              <span class="value">{{ exam.endTime }}</span>
            </div>
            <div class="time-item">
              <span class="label">考试时长：</span>
              <span class="value">{{ exam.duration }}分钟</span>
            </div>
          </div>
        </div>

        <!-- 考试说明 -->
        <div class="info-card">
          <h3><el-icon><InfoFilled /></el-icon> 考试说明</h3>
          <div class="exam-rules">
            <ul>
              <li>本次考试共{{ exam.questionCount }}道题目，满分{{ exam.totalScore }}分</li>
              <li>考试时间为{{ exam.duration }}分钟，请合理分配答题时间</li>
              <li>考试过程中请勿切换页面或离开考试界面</li>
              <li>答题完成后请及时提交，超时将自动提交</li>
              <li v-if="exam.isAntiCheat">本次考试开启防作弊监控，请确保摄像头正常工作</li>
            </ul>
          </div>
        </div>

        <!-- 题型分布 -->
        <div class="info-card">
          <h3><el-icon><PieChart /></el-icon> 题型分布</h3>
          <div v-if="exam.questionTypes && exam.questionTypes.length > 0" class="question-types">
            <div v-for="type in exam.questionTypes" :key="type.name" class="type-item">
              <div class="type-info">
                <span class="type-name">{{ type.name }}</span>
                <span class="type-count">{{ type.count }}题</span>
              </div>
              <el-progress
                :percentage="(type.count / exam.questionCount) * 100"
                :stroke-width="8"
                :show-text="false"
              />
            </div>
          </div>
          <el-empty v-else description="暂无题目数据" :image-size="60" />
        </div>
      </div>

      <div class="right-section">
        <!-- 近期考试统计 -->
        <div class="stats-card">
          <h3>近期考试统计</h3>
          <div class="stats-grid">
            <div class="stat-box">
              <div class="stat-number">{{ recentStats.listening }}</div>
              <div class="stat-name">听力</div>
            </div>
            <div class="stat-box">
              <div class="stat-number">{{ recentStats.reading }}</div>
              <div class="stat-name">阅读</div>
            </div>
            <div class="stat-box">
              <div class="stat-number">{{ recentStats.writing }}</div>
              <div class="stat-name">写作</div>
            </div>
            <div class="stat-box">
              <div class="stat-number">{{ recentStats.translation }}</div>
              <div class="stat-name">翻译</div>
            </div>
          </div>
        </div>

        <!-- 操作卡片 -->
        <div class="action-card">
          <div class="countdown" v-if="exam.status === 'pending'">
            <div class="countdown-label">距离考试开始</div>
            <div class="countdown-time">
              <span class="time-block">{{ countdown.days }}<small>天</small></span>
              <span class="time-block">{{ countdown.hours }}<small>时</small></span>
              <span class="time-block">{{ countdown.minutes }}<small>分</small></span>
              <span class="time-block">{{ countdown.seconds }}<small>秒</small></span>
            </div>
          </div>
          <div class="action-buttons">
            <el-button
              type="primary"
              size="large"
              :disabled="exam.status === 'finished' || exam.status === 'pending'"
              @click="startExam"
            >
              {{ exam.status === 'finished' ? '考试已结束' : exam.status === 'pending' ? '考试未开始' : '开始考试' }}
            </el-button>
            <el-button size="large" @click="showRules = true">
              查看考试规则
            </el-button>
          </div>
        </div>

        <!-- 历史成绩 -->
        <div class="history-card">
          <h3>历史成绩</h3>
          <div v-if="historyRecords.length > 0" class="history-list">
            <div v-for="record in historyRecords" :key="record.id" class="history-item">
              <div class="record-info">
                <div class="record-score" :class="getScoreClass(record.score)">
                  {{ record.score }}分
                </div>
                <div class="record-time">{{ record.time }}</div>
              </div>
              <el-button type="primary" link size="small" @click="viewRecord(record)">
                查看详情
              </el-button>
            </div>
          </div>
          <el-empty v-else description="暂无考试记录" :image-size="80" />
        </div>

        <!-- 相关推荐 -->
        <div class="recommend-card">
          <h3>相关推荐</h3>
          <div class="recommend-list">
            <div v-for="item in recommendExams" :key="item.id" class="recommend-item" @click="goToExam(item)">
              <div class="recommend-name">{{ item.name }}</div>
              <el-tag size="small">{{ item.level }}</el-tag>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 考试规则弹窗 -->
    <el-dialog v-model="showRules" title="考试规则" width="600px">
      <div class="rules-content">
        <h4>一、考试须知</h4>
        <ol>
          <li>请在规定时间内完成考试，超时将自动提交试卷</li>
          <li>考试过程中请勿刷新页面或关闭浏览器</li>
          <li>每道题目只能作答一次，提交后不可修改</li>
        </ol>
        <h4>二、防作弊规则</h4>
        <ol>
          <li>考试期间将开启摄像头监控</li>
          <li>切换页面或离开考试界面将被记录</li>
          <li>多次违规将取消考试资格</li>
        </ol>
        <h4>三、成绩说明</h4>
        <ol>
          <li>客观题系统自动评分，主观题由教师批改</li>
          <li>成绩将在考试结束后公布</li>
        </ol>
      </div>
      <template #footer>
        <el-button type="primary" @click="showRules = false">我已了解</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ArrowLeft, Calendar, InfoFilled, PieChart } from '@element-plus/icons-vue'
import { examApi } from '@/api/exam'

const router = useRouter()
const route = useRoute()

const showRules = ref(false)
const loading = ref(false)
const loadError = ref('')

// 考试数据
const exam = ref({
  id: route.params.id,
  name: '',
  level: '',
  status: 'pending',
  description: '',
  startTime: '',
  endTime: '',
  duration: 0,
  questionCount: 0,
  totalScore: 0,
  passScore: 0,
  isAntiCheat: false,
  questionTypes: []
})

// 倒计时
const countdown = ref({
  days: 0,
  hours: 0,
  minutes: 0,
  seconds: 0
})

// 近期考试统计
const recentStats = ref({
  listening: 0,
  reading: 0,
  writing: 0,
  translation: 0
})

// 历史记录
const historyRecords = ref([])

// 推荐考试
const recommendExams = ref([])

let countdownTimer = null

// 加载考试详情
const loadExamDetail = async () => {
  loading.value = true
  loadError.value = ''
  try {
    const id = route.params.id
    if (!id) {
      loadError.value = '考试ID无效'
      return
    }
    const res = await examApi.getExamDetail(id)
    if (res.data) {
      const data = res.data
      const examData = data.exam || data
      const now = new Date()
      let examStatus = 'ongoing'
      if (examData.startTime && new Date(examData.startTime) > now) {
        examStatus = 'pending'
      } else if (examData.endTime && new Date(examData.endTime) < now) {
        examStatus = 'finished'
      }

      exam.value = {
        id: examData.id,
        name: examData.examName || examData.title || '',
        level: examData.level || 'CET-4',
        status: examStatus,
        description: examData.description || '',
        startTime: examData.startTime || '',
        endTime: examData.endTime || '',
        duration: examData.duration || 120,
        questionCount: data.questionCount || 0,
        totalScore: examData.totalScore || 100,
        passScore: examData.passScore || 60,
        isAntiCheat: examData.enableAntiCheat === 1,
        questionTypes: (data.questionTypes || []).map(t => ({
          name: t.name || t.type,
          count: t.count || 0
        }))
      }

      if (examStatus === 'pending') {
        updateCountdown()
        // 启动倒计时定时器
        countdownTimer = setInterval(updateCountdown, 1000)
      }
    }
  } catch (error) {
    console.error('加载考试详情失败:', error)
    loadError.value = '加载考试详情失败，请重试'
  } finally {
    loading.value = false
  }

  // 加载历史记录（显示当前考试的历史记录）
  try {
    const recordRes = await examApi.getExamRecords({ pageNum: 1, pageSize: 10 })
    if (recordRes.data?.records) {
      // 只显示当前考试ID的历史记录，并且只显示已完成的记录
      historyRecords.value = recordRes.data.records
        .filter(r => String(r.examId) === String(route.params.id) && r.status === 'COMPLETED')
        .slice(0, 5)
        .map(r => ({
          id: r.id,
          examId: r.examId,
          score: r.obtainedScore ?? r.score ?? 0,
          time: r.submitTime?.substring(0, 16)?.replace('T', ' ') || r.createTime?.substring(0, 16)?.replace('T', ' ') || ''
        }))
    }
  } catch (error) {
    console.error('加载历史记录失败:', error)
    historyRecords.value = []
  }

  // 加载推荐考试
  try {
    const listRes = await examApi.getExamList({ pageNum: 1, pageSize: 4, level: exam.value.level })
    if (listRes.data?.records) {
      recommendExams.value = listRes.data.records
        .filter(e => String(e.id) !== String(route.params.id))
        .slice(0, 3)
        .map(e => ({
          id: e.id,
          name: e.examName || e.title,
          level: e.level || 'CET-4'
        }))
    }
  } catch (error) {
    console.error('加载推荐考试失败:', error)
    recommendExams.value = []
  }

  // 根据当前考试的题型分布填充统计
  if (exam.value.questionTypes && exam.value.questionTypes.length > 0) {
    const typeMap = {}
    exam.value.questionTypes.forEach(t => {
      const name = (t.name || '').toLowerCase()
      if (name.includes('听力')) typeMap.listening = (typeMap.listening || 0) + t.count
      else if (name.includes('阅读')) typeMap.reading = (typeMap.reading || 0) + t.count
      else if (name.includes('写作')) typeMap.writing = (typeMap.writing || 0) + t.count
      else if (name.includes('翻译')) typeMap.translation = (typeMap.translation || 0) + t.count
    })
    recentStats.value = {
      listening: typeMap.listening || 0,
      reading: typeMap.reading || 0,
      writing: typeMap.writing || 0,
      translation: typeMap.translation || 0
    }
  }
}

const getStatusType = (status) => {
  const types = { pending: 'info', ongoing: 'success', finished: '' }
  return types[status] || ''
}

const getStatusText = (status) => {
  const texts = { pending: '未开始', ongoing: '进行中', finished: '已结束' }
  return texts[status] || ''
}

const getScoreClass = (score) => {
  const passScore = exam.value.passScore || 60
  if (score >= exam.value.totalScore * 0.85) return 'excellent'
  if (score >= passScore) return 'pass'
  return 'fail'
}

const startExam = () => {
  router.push(`/student/exam/room/${exam.value.id}`)
}

const viewRecord = (record) => {
  router.push(`/student/exam/result/${record.id}`)
}

const goToExam = (item) => {
  router.push(`/student/exam/detail/${item.id}`)
}

onMounted(() => {
  loadExamDetail()
})

onUnmounted(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
  }
})

// 更新倒计时
const updateCountdown = () => {
  if (exam.value.status !== 'pending') return // 只有pending状态才计算倒计时
  
  const start = new Date(exam.value.startTime)
  const now = new Date()
  const diff = start - now

  if (diff > 0) {
    countdown.value.days = Math.floor(diff / (1000 * 60 * 60 * 24))
    countdown.value.hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
    countdown.value.minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
    countdown.value.seconds = Math.floor((diff % (1000 * 60)) / 1000)
  }
}
</script>

<style lang="scss" scoped>
.exam-detail {
  .back-nav {
    margin-bottom: 16px;
  }

  .exam-info-card {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: var(--radius-lg);
    padding: 32px;
    color: #fff;
    margin-bottom: 24px;

    .exam-main-info {
      margin-bottom: 24px;

      .exam-tags {
        display: flex;
        gap: 8px;
        margin-bottom: 16px;

        .el-tag {
          border: none;
        }
      }

      .exam-title {
        font-size: 28px;
        font-weight: 600;
        margin-bottom: 12px;
      }

      .exam-desc {
        opacity: 0.9;
        line-height: 1.6;
      }
    }

    .exam-stats {
      display: flex;
      gap: 48px;

      .stat-item {
        text-align: center;

        .stat-value {
          font-size: 32px;
          font-weight: 700;
        }

        .stat-label {
          font-size: 13px;
          opacity: 0.8;
          margin-top: 4px;
        }
      }
    }
  }

  .detail-content {
    display: grid;
    grid-template-columns: 1fr 360px;
    gap: 24px;

    @media (max-width: 1200px) {
      grid-template-columns: 1fr;
    }
  }

  .info-card {
    background: var(--bg-primary);
    border-radius: var(--radius-lg);
    padding: 24px;
    margin-bottom: 24px;
    box-shadow: var(--shadow-sm);

    h3 {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 16px;
      font-weight: 600;
      color: var(--text-primary);
      margin-bottom: 20px;
    }

    .time-info {
      .time-item {
        display: flex;
        padding: 12px 0;
        border-bottom: 1px solid var(--border-light);

        &:last-child {
          border-bottom: none;
        }

        .label {
          color: var(--text-secondary);
          width: 100px;
        }

        .value {
          color: var(--text-primary);
          font-weight: 500;
        }
      }
    }

    .exam-rules {
      ul {
        padding-left: 20px;

        li {
          color: var(--text-regular);
          line-height: 2;
        }
      }
    }

    .question-types {
      .type-item {
        margin-bottom: 16px;

        &:last-child {
          margin-bottom: 0;
        }

        .type-info {
          display: flex;
          justify-content: space-between;
          margin-bottom: 8px;

          .type-name {
            color: var(--text-primary);
          }

          .type-count {
            color: var(--text-secondary);
            font-size: 13px;
          }
        }
      }
    }
  }

  .action-card {
    background: var(--bg-primary);
    border-radius: var(--radius-lg);
    padding: 24px;
    margin-bottom: 24px;
    box-shadow: var(--shadow-sm);

    .countdown {
      text-align: center;
      margin-bottom: 24px;

      .countdown-label {
        color: var(--text-secondary);
        margin-bottom: 12px;
      }

      .countdown-time {
        display: flex;
        justify-content: center;
        gap: 12px;

        .time-block {
          background: var(--bg-tertiary);
          padding: 12px 16px;
          border-radius: var(--radius-md);
          font-size: 24px;
          font-weight: 700;
          color: var(--primary-color);

          small {
            font-size: 12px;
            font-weight: 400;
            color: var(--text-secondary);
            margin-left: 2px;
          }
        }
      }
    }

    .action-buttons {
      display: flex;
      flex-direction: column;
      gap: 12px;

      .el-button {
        width: 100%;
        justify-content: center;
        text-align: center;
        padding-left: 20px !important;
        padding-right: 20px !important;
        
        :deep(.el-button__text) {
          width: 100%;
          text-align: center;
        }
      }
    }
  }

  .stats-card {
    background: var(--bg-primary);
    border-radius: var(--radius-lg);
    padding: 24px;
    margin-bottom: 24px;
    box-shadow: var(--shadow-sm);

    h3 {
      font-size: 16px;
      font-weight: 600;
      color: var(--text-primary);
      margin-bottom: 16px;
    }

    .stats-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 12px;

      .stat-box {
        background: var(--bg-tertiary);
        padding: 16px;
        border-radius: var(--radius-md);
        text-align: center;
        transition: all 0.3s;

        &:hover {
          background: var(--primary-light);
          transform: translateY(-2px);
        }

        .stat-number {
          font-size: 28px;
          font-weight: 700;
          color: var(--primary-color);
          margin-bottom: 4px;
        }

        .stat-name {
          font-size: 13px;
          color: var(--text-secondary);
        }
      }
    }
  }

  .history-card,
  .recommend-card {
    background: var(--bg-primary);
    border-radius: var(--radius-lg);
    padding: 24px;
    margin-bottom: 24px;
    box-shadow: var(--shadow-sm);

    h3 {
      font-size: 16px;
      font-weight: 600;
      color: var(--text-primary);
      margin-bottom: 16px;
    }
  }

  .history-list {
    .history-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 12px 0;
      border-bottom: 1px solid var(--border-light);

      &:last-child {
        border-bottom: none;
      }

      .record-score {
        font-size: 18px;
        font-weight: 600;

        &.excellent {
          color: var(--success-color);
        }

        &.pass {
          color: var(--warning-color);
        }

        &.fail {
          color: var(--danger-color);
        }
      }

      .record-time {
        font-size: 13px;
        color: var(--text-secondary);
        margin-top: 4px;
      }
    }
  }

  .recommend-list {
    .recommend-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 12px;
      border-radius: var(--radius-md);
      cursor: pointer;
      transition: background 0.3s;

      &:hover {
        background: var(--bg-tertiary);
      }

      .recommend-name {
        font-size: 14px;
        color: var(--text-primary);
      }
    }
  }

  .rules-content {
    h4 {
      font-size: 15px;
      font-weight: 600;
      color: var(--text-primary);
      margin: 16px 0 12px;

      &:first-child {
        margin-top: 0;
      }
    }

    ol {
      padding-left: 20px;

      li {
        color: var(--text-regular);
        line-height: 2;
      }
    }
  }
}
</style>
