<template>
  <div class="exam-room">
    <!-- 考试头部 -->
    <div class="exam-header">
      <div class="header-left">
        <h2>{{ exam.name || '加载中...' }}</h2>
        <el-tag type="info">{{ currentSection || '加载中' }}</el-tag>
      </div>
      <div class="header-center">
        <div class="timer" :class="{ warning: remainingTime < 600 }">
          <el-icon><Timer /></el-icon>
          <span>剩余时间：{{ formatTime(remainingTime) }}</span>
        </div>
      </div>
      <div class="header-right">
        <el-button @click="showAnswerCard = true" :disabled="!currentQuestion">
          <el-icon><Grid /></el-icon>
          答题卡
        </el-button>
        <el-button type="primary" @click="confirmSubmit" :disabled="!currentQuestion || answeredCount === 0">
          提交试卷
        </el-button>
      </div>
    </div>

    <!-- 考试主体 -->
    <div class="exam-body">
      <!-- 题目导航侧边栏 -->
      <div class="question-nav">
        <div class="nav-header">
          <h4>题目导航</h4>
          <div class="nav-legend">
            <span class="legend-item"><i class="dot answered"></i>已答</span>
            <span class="legend-item"><i class="dot unanswered"></i>未答</span>
            <span class="legend-item"><i class="dot marked"></i>标记</span>
          </div>
        </div>
        <div class="nav-sections">
          <div v-for="section in sections" :key="section.name" class="nav-section">
            <div class="section-title">{{ section.name }}</div>
            <div class="section-questions">
              <div
                v-for="q in section.questions"
                :key="q.index"
                class="question-dot"
                :class="{
                  active: currentQuestionIndex === q.index,
                  answered: q.answered,
                  marked: q.marked
                }"
                @click="goToQuestion(q.index)"
              >
                {{ q.index + 1 }}
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 题目内容区 -->
      <div class="question-content">
        <div v-if="!currentQuestion" class="loading-state">
          <el-empty description="正在加载考试题目...">
            <template #image>
              <el-icon :size="60"><Loading /></el-icon>
            </template>
          </el-empty>
        </div>
        <div v-else class="question-card">
          <!-- 题目头部 -->
          <div class="question-header">
            <div class="question-info">
              <span class="question-number">第 {{ currentQuestionIndex + 1 }} 题</span>
              <el-tag size="small">{{ currentQuestion.type }}</el-tag>
              <span class="question-score">（{{ currentQuestion.score }}分）</span>
            </div>
            <el-button
              :type="currentQuestion.marked ? 'warning' : 'default'"
              size="small"
              @click="toggleMark"
            >
              <el-icon><Star /></el-icon>
              {{ currentQuestion.marked ? '取消标记' : '标记此题' }}
            </el-button>
          </div>

          <!-- 题目内容 -->
          <div class="question-body">
            <!-- 题目标题（如果有） -->
            <div v-if="currentQuestion.title && currentQuestion.title !== currentQuestion.content" class="question-title">
              {{ currentQuestion.title }}
            </div>
            <!-- 题目内容/题干 -->
            <div class="question-stem">{{ currentQuestion.content || currentQuestion.stem }}</div>

            <!-- 听力音频 -->
            <div v-if="currentQuestion.audio" class="audio-player">
              <audio ref="audioRef" :src="currentQuestion.audio" controls></audio>
            </div>

            <!-- 阅读材料 -->
            <div v-if="currentQuestion.passage" class="reading-passage">
              <h4 class="passage-title">📝 {{ currentQuestion.passageTitle || '阅读材料' }}</h4>
              <div class="passage-content">
                <p v-for="(para, idx) in currentQuestion.passage.split('\n\n')" :key="idx">
                  {{ para }}
                </p>
              </div>
            </div>

            <!-- 选项（单选） -->
            <div v-if="currentQuestion.options && currentQuestion.options.length > 0 && currentQuestion.type !== '多选题'" class="options-list">
              <div
                v-for="(option, index) in currentQuestion.options"
                :key="index"
                class="option-item"
                :class="{ selected: currentQuestion.answer === String(index) }"
                @click="selectOption(index)"
              >
                <span class="option-label">{{ String.fromCharCode(65 + index) }}</span>
                <span class="option-text">{{ option }}</span>
              </div>
            </div>

            <!-- 多选题 -->
            <div v-if="currentQuestion.type === '多选题' && currentQuestion.options" class="options-list">
              <div
                v-for="(option, index) in currentQuestion.options"
                :key="index"
                class="option-item"
                :class="{ selected: currentQuestion.answer?.includes(index) }"
                @click="toggleMultiOption(index)"
              >
                <el-checkbox :model-value="currentQuestion.answer?.includes(index)" />
                <span class="option-label">{{ String.fromCharCode(65 + index) }}</span>
                <span class="option-text">{{ option }}</span>
              </div>
            </div>

            <!-- 翻译题/填空题 -->
            <div v-if="!currentQuestion.options && (currentQuestion.type === '翻译题' || currentQuestion.type === '填空题')" class="translation-area">
              <el-input
                v-model="currentQuestion.answer"
                type="textarea"
                :rows="8"
                placeholder="请在此输入你的翻译..."
                show-word-limit
                :maxlength="1000"
                @input="debouncedSaveAnswer(currentQuestion)"
              />
              <div class="word-count">
                当前字数：{{ currentQuestion.answer?.length || 0 }}
              </div>
            </div>

            <!-- 完形填空（单词填空） -->
            <div v-if="!currentQuestion.options && currentQuestion.type === '完形填空'" class="fill-blank">
              <el-input
                v-model="currentQuestion.answer"
                placeholder="请输入答案"
                size="large"
                @input="debouncedSaveAnswer(currentQuestion)"
              />
            </div>

            <!-- 写作题 -->
            <div v-if="currentQuestion.type === '写作题'" class="essay-area">
              <el-input
                v-model="currentQuestion.answer"
                type="textarea"
                :rows="12"
                placeholder="请在此输入您的作文..."
                show-word-limit
                :maxlength="2000"
                @input="debouncedSaveAnswer(currentQuestion)"
              />
              <div class="word-count">
                当前字数：{{ currentQuestion.answer?.length || 0 }}
              </div>
            </div>
          </div>

          <!-- 题目底部操作 -->
          <div class="question-footer">
            <el-button
              :disabled="currentQuestionIndex === 0"
              @click="prevQuestion"
            >
              <el-icon><ArrowLeft /></el-icon>
              上一题
            </el-button>
            <el-button
              type="primary"
              :disabled="currentQuestionIndex === totalQuestions - 1"
              @click="nextQuestion"
            >
              下一题
              <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 答题卡弹窗 -->
    <el-drawer v-model="showAnswerCard" title="答题卡" size="400px">
      <div class="answer-card-content">
        <div class="card-stats">
          <div class="stat-item">
            <span class="stat-value">{{ answeredCount }}</span>
            <span class="stat-label">已答</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">{{ totalQuestions - answeredCount }}</span>
            <span class="stat-label">未答</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">{{ markedCount }}</span>
            <span class="stat-label">标记</span>
          </div>
        </div>
        <div class="card-sections">
          <div v-for="section in sections" :key="section.name" class="card-section">
            <div class="section-title">{{ section.name }}</div>
            <div class="section-grid">
              <div
                v-for="q in section.questions"
                :key="q.index"
                class="grid-item"
                :class="{
                  active: currentQuestionIndex === q.index,
                  answered: q.answered,
                  marked: q.marked
                }"
                @click="goToQuestion(q.index); showAnswerCard = false"
              >
                {{ q.index + 1 }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-drawer>

    <!-- 提交确认弹窗 -->
    <el-dialog v-model="showSubmitDialog" title="提交确认" width="480px">
      <div class="submit-confirm">
        <el-icon :size="48" color="#faad14"><Warning /></el-icon>
        <h3>确定要提交试卷吗？</h3>
        <div class="submit-stats">
          <p>已答题目：<strong>{{ answeredCount }}</strong> / {{ totalQuestions }}</p>
          <p>未答题目：<strong class="warning">{{ totalQuestions - answeredCount }}</strong></p>
          <p>标记题目：<strong>{{ markedCount }}</strong></p>
        </div>
        <p class="submit-tip">提交后将无法修改答案，请确认无误后再提交</p>
      </div>
      <template #footer>
        <el-button @click="showSubmitDialog = false">继续答题</el-button>
        <el-button type="primary" @click="submitExam">确认提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Timer, Grid, Star, ArrowLeft, ArrowRight, Warning, Loading
} from '@element-plus/icons-vue'
import { examApi } from '@/api/exam'

const router = useRouter()
const route = useRoute()

const showAnswerCard = ref(false)
const showSubmitDialog = ref(false)
const audioRef = ref()
const recordId = ref(null)

// 考试信息
const exam = ref({
  id: route.params.id,
  name: '',
  duration: 130 * 60 // 秒
})

// 剩余时间（初始化为-1表示尚未加载，避免定时器误判为时间到）
const remainingTime = ref(-1)
// 是否正在提交
const submitting = ref(false)

// 当前题目索引
const currentQuestionIndex = ref(0)

// 题目数据
const questions = ref([])

// 加载考试数据
const loadExamData = async () => {
  try {
    // 开始考试，获取考试记录ID
    const startRes = await examApi.startExam(exam.value.id)
    if (startRes.data) {
      recordId.value = startRes.data.recordId || startRes.data.id
      remainingTime.value = startRes.data.remainingSeconds || exam.value.duration
    }

    // 获取考试详情
    const detailRes = await examApi.getExamDetail(exam.value.id)
    if (detailRes.data) {
      // 后端返回的数据结构是 {exam: {...}, questionCount: ..., questionTypes: [...]}
      const examInfo = detailRes.data.exam || detailRes.data
      exam.value.name = examInfo.examName || examInfo.name || '考试'
      exam.value.duration = (examInfo.duration || 130) * 60
      if (!remainingTime.value) {
        remainingTime.value = exam.value.duration
      }
    }

    // 获取考试题目
    const questionsRes = await examApi.getExamQuestions(exam.value.id)
    if (questionsRes.data) {
      questions.value = questionsRes.data.map((q, index) => {
        // 题型映射
        let type = '单选题'
        let section = '综合'
        
        switch (q.questionType) {
          case 'LISTENING':
            type = '听力题'
            section = '听力理解'
            break
          case 'READING':
            type = '阅读题'
            section = '阅读理解'
            break
          case 'WRITING':
            type = '写作题'
            section = '写作'
            break
          case 'TRANSLATION':
            type = '翻译题'
            section = '翻译'
            break
          case 'CLOZE':
            type = '完形填空'
            section = '完形填空'
            break
          case 'SINGLE_CHOICE':
            type = '单选题'
            section = q.section || '综合'
            break
          case 'MULTIPLE_CHOICE':
            type = '多选题'
            section = q.section || '综合'
            break
          default:
            type = q.questionType || '单选题'
            section = q.section || '综合'
        }
        
        return {
          index,
          id: q.questionId || q.id,
          section,
          type,
          title: q.title || '',
          content: q.content || '',
          stem: q.content || q.title || '',
          passage: q.passage || null,
          passageTitle: q.passageTitle || null,
          options: (() => {
            if (!q.options) return null
            if (typeof q.options === 'string') {
              try { return JSON.parse(q.options) } catch { return null }
            }
            return q.options
          })(),
          audio: q.audioUrl || null,
          score: q.score || 1,
          answer: null,
          marked: false
        }
      })
    }
  } catch (error) {
    console.error('加载考试数据失败:', error)
    ElMessage.error('加载考试数据失败，请返回重试')
    router.push('/student/exam')
    return
  }
}

const totalQuestions = computed(() => questions.value.length)
const currentQuestion = computed(() => questions.value[currentQuestionIndex.value])
const currentSection = computed(() => currentQuestion.value?.section || '')

// 分组题目
const sections = computed(() => {
  const sectionMap = {}
  questions.value.forEach((q, index) => {
    if (!sectionMap[q.section]) {
      sectionMap[q.section] = {
        name: q.section,
        questions: []
      }
    }
    sectionMap[q.section].questions.push({
      index,
      answered: q.answer !== null && q.answer !== '' && !(Array.isArray(q.answer) && q.answer.length === 0),
      marked: q.marked
    })
  })
  return Object.values(sectionMap)
})

const answeredCount = computed(() => {
  return questions.value.filter(q => q.answer !== null && q.answer !== '' && !(Array.isArray(q.answer) && q.answer.length === 0)).length
})

const markedCount = computed(() => {
  return questions.value.filter(q => q.marked).length
})

// 格式化时间
const formatTime = (seconds) => {
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  const s = seconds % 60
  return `${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`
}

// 选择选项
const selectOption = (index) => {
  // 将答案存储为字符串，方便后端处理
  questions.value[currentQuestionIndex.value].answer = String(index)
  debouncedSaveAnswer(questions.value[currentQuestionIndex.value])
}

// 多选切换
const toggleMultiOption = (index) => {
  const q = questions.value[currentQuestionIndex.value]
  if (!q.answer) q.answer = []
  const idx = q.answer.indexOf(index)
  if (idx > -1) {
    q.answer.splice(idx, 1)
  } else {
    q.answer.push(index)
  }
  debouncedSaveAnswer(q)
}

// 防抖自动保存答案
let saveTimer = null
const debouncedSaveAnswer = (q) => {
  if (saveTimer) clearTimeout(saveTimer)
  saveTimer = setTimeout(async () => {
    if (!recordId.value) return
    try {
      const answer = Array.isArray(q.answer) ? q.answer.join(',') : (q.answer || '')
      await examApi.saveAnswer(recordId.value, { questionId: q.id, answer })
    } catch (e) {
      console.warn('自动保存答案失败:', e)
    }
  }, 1000)
}

// 标记题目
const toggleMark = () => {
  questions.value[currentQuestionIndex.value].marked = !questions.value[currentQuestionIndex.value].marked
}

// 跳转题目
const goToQuestion = (index) => {
  currentQuestionIndex.value = index
}

const prevQuestion = () => {
  if (currentQuestionIndex.value > 0) {
    currentQuestionIndex.value--
  }
}

const nextQuestion = () => {
  if (currentQuestionIndex.value < totalQuestions.value - 1) {
    currentQuestionIndex.value++
  }
}

// 确认提交
const confirmSubmit = () => {
  showSubmitDialog.value = true
}

// 提交考试
const submitExam = async () => {
  if (submitting.value) return
  submitting.value = true
  showSubmitDialog.value = false
  
  // 验证recordId是否存在
  if (!recordId.value) {
    ElMessage.error('考试记录ID丢失，无法提交')
    submitting.value = false
    return
  }
  
  try {
    const answers = questions.value.map(q => ({
      questionId: q.id,
      answer: Array.isArray(q.answer) ? q.answer.join(',') : (q.answer || ''),
      answerTime: 0
    }))

    await examApi.submitExam({
      examId: exam.value.id,
      recordId: recordId.value,
      answers
    })
    ElMessage.success('试卷提交成功')
    router.push(`/student/exam/result/${recordId.value}`)
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('提交失败，请重试')
  } finally {
    submitting.value = false
  }
}

// 倒计时
let timer = null
onMounted(async () => {
  await loadExamData()
  // 数据加载完成后再启动计时器，避免remainingTime为初始值时误判
  timer = setInterval(() => {
    if (remainingTime.value > 0) {
      remainingTime.value--
    } else if (remainingTime.value === 0) {
      clearInterval(timer)
      ElMessageBox.alert('考试时间已到，系统将自动提交您的试卷', '时间到', {
        confirmButtonText: '确定',
        callback: () => {
          submitExam()
        }
      })
    }
    // remainingTime === -1 时（尚未加载），不做任何操作
  }, 1000)

  // 防止页面刷新
  window.addEventListener('beforeunload', handleBeforeUnload)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
  if (saveTimer) clearTimeout(saveTimer)
  window.removeEventListener('beforeunload', handleBeforeUnload)
})

const handleBeforeUnload = (e) => {
  e.preventDefault()
  e.returnValue = ''
}
</script>

<style lang="scss" scoped>
.exam-room {
  min-height: 100vh;
  background: var(--bg-secondary);
  display: flex;
  flex-direction: column;

  .exam-header {
    height: 60px;
    background: var(--bg-primary);
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 24px;
    box-shadow: var(--shadow-sm);
    position: sticky;
    top: 0;
    z-index: 100;

    .header-left {
      display: flex;
      align-items: center;
      gap: 12px;

      h2 {
        font-size: 16px;
        font-weight: 600;
        color: var(--text-primary);
      }
    }

    .header-center {
      .timer {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 18px;
        font-weight: 600;
        color: var(--text-primary);

        &.warning {
          color: var(--danger-color);
          animation: blink 1s infinite;
        }
      }
    }

    .header-right {
      display: flex;
      gap: 12px;
    }
  }

  .exam-body {
    flex: 1;
    display: flex;
    padding: 24px;
    gap: 24px;
  }

  .question-nav {
    width: 240px;
    background: var(--bg-primary);
    border-radius: var(--radius-lg);
    padding: 20px;
    height: fit-content;
    position: sticky;
    top: 84px;

    .nav-header {
      margin-bottom: 16px;

      h4 {
        font-size: 14px;
        font-weight: 600;
        color: var(--text-primary);
        margin-bottom: 12px;
      }

      .nav-legend {
        display: flex;
        gap: 12px;
        font-size: 12px;
        color: var(--text-secondary);

        .legend-item {
          display: flex;
          align-items: center;
          gap: 4px;

          .dot {
            width: 12px;
            height: 12px;
            border-radius: 2px;
            background: var(--bg-tertiary);
            border: 1px solid var(--border-color);

            &.answered {
              background: var(--primary-color);
              border-color: var(--primary-color);
            }

            &.marked {
              background: var(--warning-color);
              border-color: var(--warning-color);
            }
          }
        }
      }
    }

    .nav-section {
      margin-bottom: 16px;

      .section-title {
        font-size: 13px;
        color: var(--text-secondary);
        margin-bottom: 8px;
      }

      .section-questions {
        display: flex;
        flex-wrap: wrap;
        gap: 6px;

        .question-dot {
          width: 28px;
          height: 28px;
          display: flex;
          align-items: center;
          justify-content: center;
          border-radius: 4px;
          font-size: 12px;
          cursor: pointer;
          background: var(--bg-tertiary);
          color: var(--text-secondary);
          transition: all 0.3s;

          &:hover {
            background: var(--primary-light);
            color: #fff;
          }

          &.active {
            background: var(--primary-color);
            color: #fff;
          }

          &.answered {
            background: var(--primary-color);
            color: #fff;
          }

          &.marked {
            background: var(--warning-color);
            color: #fff;
          }
        }
      }
    }
  }

  .question-content {
    flex: 1;

    .question-card {
      background: var(--bg-primary);
      border-radius: var(--radius-lg);
      padding: 32px;

      .question-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 24px;
        padding-bottom: 16px;
        border-bottom: 1px solid var(--border-light);

        .question-info {
          display: flex;
          align-items: center;
          gap: 12px;

          .question-number {
            font-size: 16px;
            font-weight: 600;
            color: var(--text-primary);
          }

          .question-score {
            color: var(--text-secondary);
            font-size: 14px;
          }
        }
      }

      .question-body {
        .question-title {
          font-size: 14px;
          color: var(--text-secondary);
          margin-bottom: 8px;
          padding: 8px 12px;
          background: var(--bg-secondary);
          border-radius: var(--radius-sm);
          border-left: 3px solid var(--primary-color);
        }
        
        .question-stem {
          font-size: 16px;
          color: var(--text-primary);
          line-height: 1.8;
          margin-bottom: 24px;
          font-weight: 500;
        }

        .audio-player {
          margin-bottom: 24px;

          audio {
            width: 100%;
          }
        }

        .reading-passage {
          background: var(--bg-secondary);
          border-radius: var(--radius-md);
          padding: 20px;
          margin-bottom: 24px;
          max-height: 400px;
          overflow-y: auto;

          .passage-title {
            font-size: 16px;
            font-weight: 600;
            color: var(--primary-color);
            margin-bottom: 16px;
            padding-bottom: 12px;
            border-bottom: 2px solid var(--border-color);
          }

          .passage-content {
            font-size: 14px;
            line-height: 1.8;
            color: var(--text-regular);
            
            p {
              margin-bottom: 12px;
              text-indent: 2em;
              
              &:last-child {
                margin-bottom: 0;
              }
            }
          }
        }

        .options-list {
          .option-item {
            display: flex;
            align-items: flex-start;
            gap: 12px;
            padding: 16px;
            border: 1px solid var(--border-color);
            border-radius: var(--radius-md);
            margin-bottom: 12px;
            cursor: pointer;
            transition: all 0.3s;

            &:hover {
              border-color: var(--primary-light);
              background: rgba(64, 128, 255, 0.05);
            }

            &.selected {
              border-color: var(--primary-color);
              background: rgba(64, 128, 255, 0.1);

              .option-label {
                background: var(--primary-color);
                color: #fff;
              }
            }

            .option-label {
              width: 28px;
              height: 28px;
              display: flex;
              align-items: center;
              justify-content: center;
              border-radius: 50%;
              background: var(--bg-tertiary);
              font-size: 14px;
              font-weight: 500;
              color: var(--text-secondary);
              flex-shrink: 0;
            }

            .option-text {
              flex: 1;
              font-size: 14px;
              color: var(--text-primary);
              line-height: 1.6;
            }
          }
        }

        .fill-blank {
          max-width: 600px;
        }

        .translation-area {
          .word-count {
            margin-top: 8px;
            text-align: right;
            font-size: 13px;
            color: var(--text-secondary);
          }
        }

        .essay-area {
          .word-count {
            margin-top: 8px;
            text-align: right;
            font-size: 13px;
            color: var(--text-secondary);
          }
        }
      }

      .question-footer {
        display: flex;
        justify-content: space-between;
        margin-top: 32px;
        padding-top: 24px;
        border-top: 1px solid var(--border-light);
      }
    }
  }

  .answer-card-content {
    .card-stats {
      display: flex;
      justify-content: space-around;
      padding: 20px;
      background: var(--bg-secondary);
      border-radius: var(--radius-md);
      margin-bottom: 24px;

      .stat-item {
        text-align: center;

        .stat-value {
          font-size: 24px;
          font-weight: 700;
          color: var(--text-primary);
        }

        .stat-label {
          font-size: 13px;
          color: var(--text-secondary);
          margin-top: 4px;
        }
      }
    }

    .card-section {
      margin-bottom: 20px;

      .section-title {
        font-size: 14px;
        font-weight: 500;
        color: var(--text-primary);
        margin-bottom: 12px;
      }

      .section-grid {
        display: grid;
        grid-template-columns: repeat(8, 1fr);
        gap: 8px;

        .grid-item {
          aspect-ratio: 1;
          display: flex;
          align-items: center;
          justify-content: center;
          border-radius: 4px;
          font-size: 13px;
          cursor: pointer;
          background: var(--bg-tertiary);
          color: var(--text-secondary);
          transition: all 0.3s;

          &:hover {
            background: var(--primary-light);
            color: #fff;
          }

          &.active {
            background: var(--primary-color);
            color: #fff;
          }

          &.answered {
            background: var(--primary-color);
            color: #fff;
          }

          &.marked {
            background: var(--warning-color);
            color: #fff;
          }
        }
      }
    }
  }

  .submit-confirm {
    text-align: center;
    padding: 20px 0;

    h3 {
      font-size: 18px;
      font-weight: 600;
      color: var(--text-primary);
      margin: 16px 0;
    }

    .submit-stats {
      background: var(--bg-secondary);
      border-radius: var(--radius-md);
      padding: 16px;
      margin-bottom: 16px;

      p {
        margin: 8px 0;
        color: var(--text-regular);

        strong {
          color: var(--primary-color);

          &.warning {
            color: var(--danger-color);
          }
        }
      }
    }

    .submit-tip {
      font-size: 13px;
      color: var(--text-secondary);
    }
  }
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}
</style>
