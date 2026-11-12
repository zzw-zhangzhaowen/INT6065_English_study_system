<template>
  <div class="exam-result">
    <!-- 成绩概览 -->
    <div class="result-header" :class="isPassed ? 'passed' : 'failed'">
      <div class="result-icon">
        <el-icon :size="64">
          <CircleCheck v-if="isPassed" />
          <CircleClose v-else />
        </el-icon>
      </div>
      <div class="result-info">
        <h1>{{ isPassed ? '恭喜通过！' : '继续努力！' }}</h1>
        <div class="score-display">
          <span class="score">{{ result.score }}</span>
          <span class="total">/ {{ result.totalScore }}</span>
        </div>
        <p class="pass-line">及格线：{{ result.passScore }}分</p>
      </div>
      <div class="result-stats">
        <div class="stat-item">
          <div class="stat-value">{{ result.correctCount }}</div>
          <div class="stat-label">正确题数</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ result.wrongCount }}</div>
          <div class="stat-label">错误题数</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ result.accuracy }}%</div>
          <div class="stat-label">正确率</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ result.duration }}</div>
          <div class="stat-label">用时(分钟)</div>
        </div>
      </div>
    </div>

    <!-- 详细分析 -->
    <div class="result-content">
      <div class="left-section">
        <!-- 各部分得分 -->
        <div class="card section-scores" v-if="sectionScores.length > 0">
          <h3>各部分得分</h3>
          <div class="score-bars">
            <div v-for="section in sectionScores" :key="section.name" class="score-bar-item">
              <div class="bar-header">
                <span class="section-name">{{ section.name }}</span>
                <span class="section-score">{{ section.score }} / {{ section.total }}</span>
              </div>
              <el-progress
                :percentage="section.total > 0 ? Math.round((section.score / section.total) * 100) : 0"
                :stroke-width="12"
                :color="getProgressColor(section.total > 0 ? section.score / section.total : 0)"
              />
            </div>
          </div>
        </div>
        <div class="card section-scores" v-else>
          <h3>各部分得分</h3>
          <el-empty description="暂无数据" :image-size="80" />
        </div>

        <!-- 能力分析雷达图 -->
        <div class="card ability-analysis" v-if="sectionScores.length > 0">
          <h3>能力分析</h3>
          <div class="radar-chart" ref="radarChartRef"></div>
        </div>
        <div class="card ability-analysis" v-else>
          <h3>能力分析</h3>
          <el-empty description="暂无数据" :image-size="80" />
        </div>

        <!-- 错题分布 -->
        <div class="card wrong-distribution" v-if="result.wrongCount > 0">
          <h3>错题分布</h3>
          <div class="pie-chart" ref="pieChartRef"></div>
        </div>
        <div class="card wrong-distribution" v-else>
          <h3>错题分布</h3>
          <el-empty description="暂无错题" :image-size="80" />
        </div>
      </div>

      <div class="right-section">
        <!-- 操作按钮 -->
        <div class="card action-card">
          <el-button type="primary" size="large" @click="viewWrongQuestions">
            <el-icon><Document /></el-icon>
            查看错题
          </el-button>
          <el-button size="large" @click="reviewExam">
            <el-icon><View /></el-icon>
            试题回顾
          </el-button>
          <el-button size="large" @click="shareResult">
            <el-icon><Share /></el-icon>
            分享成绩
          </el-button>
          <el-button size="large" @click="continueLearning">
            <el-icon><Reading /></el-icon>
            继续学习
          </el-button>
        </div>

        <!-- 排名信息 -->
        <div class="card rank-card">
          <h3>排名信息</h3>
          <div class="rank-info">
            <div class="rank-item">
              <div class="rank-value">{{ result.rank }}</div>
              <div class="rank-label">班级排名</div>
            </div>
            <div class="rank-item">
              <div class="rank-value">{{ result.percentile }}%</div>
              <div class="rank-label">超过考生</div>
            </div>
          </div>
        </div>

        <!-- 学习建议 -->
        <div class="card suggestion-card">
          <h3>学习建议</h3>
          <div class="suggestion-list">
            <div v-for="(suggestion, index) in suggestions" :key="index" class="suggestion-item">
              <el-icon :color="suggestion.color"><InfoFilled /></el-icon>
              <span>{{ suggestion.text }}</span>
            </div>
          </div>
        </div>

        <!-- 推荐练习 -->
        <div class="card recommend-card">
          <h3>推荐练习</h3>
          <div class="recommend-list">
            <div
              v-for="item in recommendPractices"
              :key="item.id"
              class="recommend-item"
              @click="goToPractice(item)"
            >
              <div class="recommend-info">
                <div class="recommend-name">{{ item.name }}</div>
                <div class="recommend-desc">{{ item.description }}</div>
              </div>
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import {
  CircleCheck, CircleClose, Document, View, Share, Reading,
  InfoFilled, ArrowRight
} from '@element-plus/icons-vue'
import { examApi } from '@/api/exam'

const router = useRouter()
const route = useRoute()

const radarChartRef = ref()
const pieChartRef = ref()
let radarChart = null
let pieChart = null

// 考试结果
const result = ref({
  score: 0,
  totalScore: 0,
  passScore: 0,
  correctCount: 0,
  wrongCount: 0,
  accuracy: 0,
  duration: 0,
  rank: 0,
  percentile: 0
})

const isPassed = computed(() => result.value.score >= result.value.passScore)

// 各部分得分
const sectionScores = ref([])

// 学习建议
const suggestions = ref([])

// 推荐练习
const recommendPractices = ref([])

// 加载考试结果
const loadExamResult = async () => {
  try {
    const res = await examApi.getExamRecordDetail(route.params.id)
    if (res.data) {
      const record = res.data.record || {}
      const exam = res.data.exam || {}
      const totalQuestions = (record.correctCount || 0) + (record.wrongCount || 0)

      result.value = {
        score: record.obtainedScore || 0,
        totalScore: exam.totalScore || record.totalScore || 100,
        passScore: exam.passScore || 60,
        correctCount: record.correctCount || 0,
        wrongCount: record.wrongCount || 0,
        accuracy: totalQuestions > 0 ? Math.round((record.correctCount / totalQuestions) * 100) : 0,
        duration: record.duration || 0,
        rank: res.data.rank || 0,
        percentile: res.data.percentile || 0
      }

      // 从答题详情中计算各部分得分
      if (res.data.answerDetails && res.data.answerDetails.length > 0) {
        const typeScoreMap = {}
        const typeTotalMap = {}
        const typeCountMap = {}
        const typeWrongMap = {}
        
        res.data.answerDetails.forEach(detail => {
          const question = detail.question
          const ar = detail.answerRecord
          // 优先使用后端返回的questionScore，其次使用question.score，最后使用已得分
          const questionScore = detail.questionScore || question.score || ar?.obtainedScore || 0
          
          if (question && ar) {
            const typeName = getQuestionTypeChinese(question.questionType)
            if (!typeScoreMap[typeName]) {
              typeScoreMap[typeName] = 0
              typeTotalMap[typeName] = 0
              typeCountMap[typeName] = 0
              typeWrongMap[typeName] = 0
            }
            typeScoreMap[typeName] += Number(ar.obtainedScore || 0)
            typeTotalMap[typeName] += Number(questionScore)
            typeCountMap[typeName] += 1
            typeWrongMap[typeName] += ar.isCorrect === 0 ? 1 : 0
          }
        })
        
        sectionScores.value = Object.keys(typeScoreMap).map(name => ({
          name,
          score: Math.round(typeScoreMap[name] * 10) / 10,
          total: Math.round(typeTotalMap[name] * 10) / 10,
          count: typeCountMap[name],
          wrongCount: typeWrongMap[name]
        }))
      }

      // 生成学习建议
      suggestions.value = []
      sectionScores.value.forEach(section => {
        const ratio = section.total > 0 ? section.score / section.total : 0
        if (ratio < 0.6) {
          suggestions.value.push({ text: `${section.name}部分得分率较低，建议加强练习`, color: '#ff4d4f' })
        } else if (ratio < 0.8) {
          suggestions.value.push({ text: `${section.name}部分还有提升空间，继续努力`, color: '#faad14' })
        } else {
          suggestions.value.push({ text: `${section.name}部分表现良好，继续保持`, color: '#52c41a' })
        }
      })

      // 推荐练习基于薄弱环节
      recommendPractices.value = sectionScores.value
        .filter(s => s.total > 0 && (s.score / s.total) < 0.8)
        .slice(0, 3)
        .map((s, i) => ({
          id: i + 1,
          name: `${s.name}专项训练`,
          description: `针对${s.name}薄弱环节强化练习`
        }))
      if (recommendPractices.value.length === 0) {
        recommendPractices.value = [{ id: 1, name: '综合练习', description: '巩固已掌握知识' }]
      }
    }
  } catch (error) {
    console.error('加载考试结果失败:', error)
    ElMessage.error('加载考试结果失败，请返回重试')
  }
}

const getQuestionTypeChinese = (type) => {
  const map = {
    SINGLE_CHOICE: '单选题', MULTI_CHOICE: '多选题', TRUE_FALSE: '判断题',
    FILL_BLANK: '填空题', SHORT_ANSWER: '简答题', READING: '阅读理解',
    LISTENING: '听力', TRANSLATION: '翻译', WRITING: '写作', CLOZE: '完形填空'
  }
  return map[type] || type || '其他'
}

const getProgressColor = (ratio) => {
  if (ratio >= 0.8) return '#52c41a'
  if (ratio >= 0.6) return '#4080ff'
  if (ratio >= 0.4) return '#faad14'
  return '#ff4d4f'
}

const viewWrongQuestions = () => {
  router.push('/student/learning/wrong')
}

const reviewExam = () => {
  router.push(`/student/exam/review/${route.params.id}`)
}

const shareResult = () => {
  const shareText = `我在英语等级智能学习系统完成了考试，获得了${result.value.score}分！`
  if (navigator.clipboard) {
    navigator.clipboard.writeText(shareText).then(() => {
      ElMessage.success('成绩信息已复制到剪贴板，快去分享吧！')
    }).catch(() => {
      ElMessage.info(shareText)
    })
  } else {
    ElMessage.info(shareText)
  }
}

const continueLearning = () => {
  router.push('/student/learning')
}

const goToPractice = (item) => {
  if (item.name.includes('综合练习')) {
    router.push('/student/learning')
  } else if (item.name.includes('错题')) {
    router.push('/student/learning/practice/wrong')
  } else {
    router.push('/student/learning')
  }
}

// 初始化雷达图
const initRadarChart = () => {
  if (!radarChartRef.value) return
  radarChart = echarts.init(radarChartRef.value)

  // 使用真实的各部分得分数据
  const indicators = sectionScores.value.length > 0
    ? sectionScores.value.map(s => ({ name: s.name, max: s.total || 100 }))
    : [{ name: '综合', max: 100 }]
  const values = sectionScores.value.length > 0
    ? sectionScores.value.map(s => s.score)
    : [result.value.score]

  const option = {
    radar: {
      indicator: indicators,
      radius: '60%'
    },
    series: [
      {
        type: 'radar',
        data: [
          {
            value: values,
            name: '本次考试',
            areaStyle: {
              color: 'rgba(64, 128, 255, 0.3)'
            },
            lineStyle: {
              color: '#4080ff'
            }
          }
        ]
      }
    ],
    legend: {
      bottom: 0
    }
  }
  radarChart.setOption(option)
}

// 初始化饼图
const initPieChart = () => {
  if (!pieChartRef.value) return
  pieChart = echarts.init(pieChartRef.value)

  const colors = ['#4080ff', '#52c41a', '#faad14', '#ff4d4f', '#722ed1', '#13c2c2']
  // 使用真实的各部分错题分布
  const pieData = sectionScores.value.length > 0
    ? sectionScores.value.map((s, i) => {
        return { value: s.wrongCount || 0, name: s.name, itemStyle: { color: colors[i % colors.length] } }
      }).filter(d => d.value > 0)
    : [{ value: result.value.wrongCount || 1, name: '错题', itemStyle: { color: '#ff4d4f' } }]

  const option = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      bottom: 0
    },
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false
        },
        data: pieData
      }
    ]
  }
  pieChart.setOption(option)
}

const handleResize = () => {
  radarChart?.resize()
  pieChart?.resize()
}

onMounted(async () => {
  await loadExamResult()
  await nextTick()
  initRadarChart()
  initPieChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  radarChart?.dispose()
  pieChart?.dispose()
})
</script>

<style lang="scss" scoped>
.exam-result {
  .result-header {
    display: flex;
    align-items: center;
    gap: 32px;
    padding: 40px;
    border-radius: var(--radius-lg);
    margin-bottom: 24px;
    color: #fff;

    &.passed {
      background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
    }

    &.failed {
      background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
    }

    .result-icon {
      flex-shrink: 0;
    }

    .result-info {
      flex: 1;

      h1 {
        font-size: 28px;
        font-weight: 600;
        margin-bottom: 12px;
      }

      .score-display {
        .score {
          font-size: 56px;
          font-weight: 700;
        }

        .total {
          font-size: 24px;
          opacity: 0.8;
        }
      }

      .pass-line {
        margin-top: 8px;
        opacity: 0.9;
      }
    }

    .result-stats {
      display: flex;
      gap: 32px;

      .stat-item {
        text-align: center;

        .stat-value {
          font-size: 28px;
          font-weight: 700;
        }

        .stat-label {
          font-size: 13px;
          opacity: 0.9;
          margin-top: 4px;
        }
      }
    }
  }

  .result-content {
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
    margin-bottom: 24px;
    box-shadow: var(--shadow-sm);

    h3 {
      font-size: 16px;
      font-weight: 600;
      color: var(--text-primary);
      margin-bottom: 20px;
    }
  }

  .section-scores {
    .score-bars {
      .score-bar-item {
        margin-bottom: 20px;

        &:last-child {
          margin-bottom: 0;
        }

        .bar-header {
          display: flex;
          justify-content: space-between;
          margin-bottom: 8px;

          .section-name {
            color: var(--text-primary);
          }

          .section-score {
            color: var(--text-secondary);
            font-size: 13px;
          }
        }
      }
    }
  }

  .ability-analysis {
    .radar-chart {
      height: 300px;
    }
  }

  .wrong-distribution {
    .pie-chart {
      height: 280px;
    }
  }

  .action-card {
    display: flex;
    flex-direction: column;
    gap: 12px;

    .el-button {
      width: 100%;
      justify-content: flex-start;
      gap: 8px;
    }
  }

  .rank-card {
    .rank-info {
      display: flex;
      justify-content: space-around;

      .rank-item {
        text-align: center;

        .rank-value {
          font-size: 32px;
          font-weight: 700;
          color: var(--primary-color);
        }

        .rank-label {
          font-size: 13px;
          color: var(--text-secondary);
          margin-top: 4px;
        }
      }
    }
  }

  .suggestion-card {
    .suggestion-list {
      .suggestion-item {
        display: flex;
        align-items: flex-start;
        gap: 12px;
        padding: 12px 0;
        border-bottom: 1px solid var(--border-light);

        &:last-child {
          border-bottom: none;
        }

        span {
          flex: 1;
          font-size: 14px;
          color: var(--text-regular);
          line-height: 1.5;
        }
      }
    }
  }

  .recommend-card {
    .recommend-list {
      .recommend-item {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 16px;
        border-radius: var(--radius-md);
        background: var(--bg-secondary);
        margin-bottom: 12px;
        cursor: pointer;
        transition: all 0.3s;

        &:last-child {
          margin-bottom: 0;
        }

        &:hover {
          background: var(--bg-tertiary);
          transform: translateX(4px);
        }

        .recommend-info {
          .recommend-name {
            font-size: 14px;
            font-weight: 500;
            color: var(--text-primary);
            margin-bottom: 4px;
          }

          .recommend-desc {
            font-size: 12px;
            color: var(--text-secondary);
          }
        }

        .el-icon {
          color: var(--text-placeholder);
        }
      }
    }
  }
}
</style>
