<template>
  <div class="wrong-practice">
    <!-- 练习头部 -->
    <div class="practice-header">
      <div class="header-left">
        <el-button @click="exitPractice" :icon="ArrowLeft">退出练习</el-button>
        <h2>错题练习</h2>
      </div>
      <div class="header-center">
        <span class="progress-text">{{ currentIndex + 1 }} / {{ questions.length }}</span>
        <el-progress
          :percentage="progressPercent"
          :stroke-width="8"
          :show-text="false"
          style="width: 200px"
        />
      </div>
      <div class="header-right">
        <span class="timer">
          <el-icon><Timer /></el-icon>
          {{ formatTime(elapsedTime) }}
        </span>
      </div>
    </div>

    <!-- 练习内容 -->
    <div class="practice-content" v-if="currentQuestion && !showResult">
      <div class="question-card">
        <div class="question-meta">
          <el-tag size="small">{{ getQuestionTypeName(currentQuestion.originalType || currentQuestion.type) }}</el-tag>
          <el-tag size="small" type="info">{{ currentQuestion.category }}</el-tag>
          <span class="error-count">已错 {{ currentQuestion.errorCount }} 次</span>
        </div>

        <div class="question-stem">
          <span class="question-number">{{ currentIndex + 1 }}.</span>
          {{ currentQuestion.title || currentQuestion.content }}
        </div>

        <!-- 选择题 -->
        <div v-if="['single', 'multiple'].includes(currentQuestion.type)" class="options-list">
          <div
            v-for="(option, index) in currentQuestion.options"
            :key="index"
            class="option-item"
            :class="{
              selected: isSelected(option.key),
              correct: showAnswer && isCorrectAnswer(option.key),
              wrong: showAnswer && isSelected(option.key) && !isCorrectAnswer(option.key)
            }"
            @click="selectOption(option.key)"
          >
            <span class="option-key">{{ option.key }}</span>
            <span class="option-content">{{ option.content }}</span>
            <el-icon v-if="showAnswer && isCorrectAnswer(option.key)" class="result-icon correct">
              <CircleCheck />
            </el-icon>
            <el-icon v-if="showAnswer && isSelected(option.key) && !isCorrectAnswer(option.key)" class="result-icon wrong">
              <CircleClose />
            </el-icon>
          </div>
        </div>

        <!-- 填空题 -->
        <div v-else-if="currentQuestion.type === 'fill'" class="fill-answer">
          <el-input
            v-model="userAnswer"
            placeholder="请输入答案"
            :disabled="showAnswer"
            @keyup.enter="submitAnswer"
          />
        </div>

        <!-- 简答题 -->
        <div v-else class="text-answer">
          <el-input
            v-model="userAnswer"
            type="textarea"
            :rows="4"
            placeholder="请输入答案"
            :disabled="showAnswer"
          />
        </div>

        <!-- 答案解析 -->
        <div v-if="showAnswer" class="answer-analysis">
          <div class="analysis-header">
            <el-icon :color="isCurrentCorrect ? '#52c41a' : '#ff4d4f'">
              <CircleCheck v-if="isCurrentCorrect" />
              <CircleClose v-else />
            </el-icon>
            <span :class="isCurrentCorrect ? 'correct' : 'wrong'">
              {{ isCurrentCorrect ? '回答正确！' : '回答错误' }}
            </span>
          </div>
          <div class="correct-answer">
            <strong>正确答案：</strong>{{ currentQuestion.correctAnswer }}
          </div>
          <div class="analysis-content" v-if="currentQuestion.analysis">
            <strong>解析：</strong>{{ currentQuestion.analysis }}
          </div>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <el-button v-if="!showAnswer" type="primary" size="large" @click="submitAnswer">
          确认答案
        </el-button>
        <el-button v-else type="primary" size="large" @click="nextQuestion">
          {{ currentIndex < questions.length - 1 ? '下一题' : '完成练习' }}
        </el-button>
      </div>
    </div>

    <!-- 练习结果 -->
    <div class="practice-result" v-if="showResult">
      <div class="result-card">
        <div class="result-icon" :class="accuracy >= 60 ? 'success' : 'fail'">
          <el-icon :size="80">
            <CircleCheck v-if="accuracy >= 60" />
            <CircleClose v-else />
          </el-icon>
        </div>
        <h2>练习完成！</h2>
        <div class="result-stats">
          <div class="stat-item">
            <div class="stat-value">{{ questions.length }}</div>
            <div class="stat-label">总题数</div>
          </div>
          <div class="stat-item">
            <div class="stat-value correct">{{ correctCount }}</div>
            <div class="stat-label">正确</div>
          </div>
          <div class="stat-item">
            <div class="stat-value wrong">{{ wrongCount }}</div>
            <div class="stat-label">错误</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ accuracy }}%</div>
            <div class="stat-label">正确率</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ formatTime(elapsedTime) }}</div>
            <div class="stat-label">用时</div>
          </div>
        </div>
        <div class="result-actions">
          <el-button type="primary" size="large" @click="restartPractice">再练一次</el-button>
          <el-button size="large" @click="exitPractice">返回错题本</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Timer, CircleCheck, CircleClose } from '@element-plus/icons-vue'
import { learningApi } from '@/api/learning'

// 将选项统一为 [{key: 'A', content: '...'}, ...] 格式
const normalizeOptions = (raw) => {
  if (!raw) return []
  const arr = typeof raw === 'string' ? JSON.parse(raw) : raw
  if (!Array.isArray(arr) || arr.length === 0) return []
  if (typeof arr[0] === 'object' && arr[0].key) return arr
  return arr.map((text, i) => ({ key: String.fromCharCode(65 + i), content: String(text) }))
}

// 将答案统一为字母字符串
const normalizeAnswer = (answer) => {
  if (answer == null) return ''
  const s = String(answer).trim()
  if (/^[A-Za-z]/.test(s)) return s.toUpperCase()
  return s.split(',').map(v => {
    const n = parseInt(v.trim(), 10)
    return isNaN(n) ? v.trim() : String.fromCharCode(65 + n)
  }).join(',')
}

const route = useRoute()
const router = useRouter()

const questions = ref([])
const currentIndex = ref(0)
const userAnswer = ref('')
const selectedOptions = ref([])
const showAnswer = ref(false)
const showResult = ref(false)
const elapsedTime = ref(0)
const results = ref([])

let timer = null

const currentQuestion = computed(() => questions.value[currentIndex.value])
const progressPercent = computed(() => questions.value.length ? ((currentIndex.value + 1) / questions.value.length) * 100 : 0)

const isCurrentCorrect = computed(() => {
  if (!currentQuestion.value) return false
  const q = currentQuestion.value
  if (['single', 'multiple'].includes(q.type)) {
    const userAns = selectedOptions.value.sort().join(',')
    const correctAns = q.correctAnswer.split(',').sort().join(',')
    return userAns === correctAns
  }
  return userAnswer.value.trim().toLowerCase() === q.correctAnswer.trim().toLowerCase()
})

const correctCount = computed(() => results.value.filter(r => r.correct).length)
const wrongCount = computed(() => results.value.filter(r => !r.correct).length)
const accuracy = computed(() => {
  if (results.value.length === 0) return 0
  return Math.round((correctCount.value / results.value.length) * 100)
})

const getQuestionTypeName = (type) => {
  const types = {
    single: '单选题',
    multiple: '多选题',
    fill: '填空题',
    short: '简答题',
    listening: '听力',
    reading: '阅读',
    writing: '写作',
    translation: '翻译'
  }
  return types[type] || type
}

const formatTime = (seconds) => {
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

const isSelected = (key) => selectedOptions.value.includes(key)

const isCorrectAnswer = (key) => {
  if (!currentQuestion.value) return false
  return currentQuestion.value.correctAnswer.includes(key)
}

const selectOption = (key) => {
  if (showAnswer.value) return
  const q = currentQuestion.value
  if (q.type === 'single') {
    selectedOptions.value = [key]
  } else {
    const index = selectedOptions.value.indexOf(key)
    if (index > -1) {
      selectedOptions.value.splice(index, 1)
    } else {
      selectedOptions.value.push(key)
    }
  }
}

const submitAnswer = () => {
  const q = currentQuestion.value
  if (['single', 'multiple'].includes(q.type) && selectedOptions.value.length === 0) {
    ElMessage.warning('请选择答案')
    return
  }
  if (!['single', 'multiple'].includes(q.type) && !userAnswer.value.trim()) {
    ElMessage.warning('请输入答案')
    return
  }
  showAnswer.value = true
  results.value.push({
    questionId: q.id,
    correct: isCurrentCorrect.value,
    userAnswer: ['single', 'multiple'].includes(q.type)
      ? selectedOptions.value.join(',')
      : userAnswer.value
  })

  // 记录本题练习结果
  learningApi.recordPracticeResult({
    questionId: q.id,
    isCorrect: isCurrentCorrect.value
  }).catch(() => {})
}

const nextQuestion = () => {
  if (currentIndex.value < questions.value.length - 1) {
    currentIndex.value++
    resetAnswer()
  } else {
    finishPractice()
  }
}

const resetAnswer = () => {
  userAnswer.value = ''
  selectedOptions.value = []
  showAnswer.value = false
}

const finishPractice = () => {
  clearInterval(timer)
  showResult.value = true
}

const restartPractice = () => {
  currentIndex.value = 0
  results.value = []
  elapsedTime.value = 0
  showResult.value = false
  resetAnswer()
  startTimer()
}

const exitPractice = async () => {
  if (!showResult.value && currentIndex.value > 0) {
    try {
      await ElMessageBox.confirm('确定要退出练习吗？当前进度将不会保存', '提示', {
        confirmButtonText: '确定退出',
        cancelButtonText: '继续练习',
        type: 'warning'
      })
    } catch {
      return
    }
  }
  clearInterval(timer)
  router.push('/student/learning/wrong')
}

const startTimer = () => {
  timer = setInterval(() => {
    elapsedTime.value++
  }, 1000)
}

const loadQuestions = async () => {
  try {
    const questionId = route.params.id

    let res
    if (questionId) {
      // 有 id 参数说明是相似题练习（路由 /practice/similar/:id）
      res = await learningApi.getSimilarQuestions(questionId)
    } else {
      res = await learningApi.getPracticeQuestions({
        limit: route.query.limit || 10,
        type: route.query.category
      })
    }

    if (res.data && res.data.length > 0) {
      questions.value = res.data.map(q => {
        const options = normalizeOptions(q.options)
        const correctAnswer = normalizeAnswer(q.correctAnswer || q.answer)
        const originalType = q.type || q.questionType || ''
        // 根据实际数据判断答题模式：有选项就是选择题
        let type = originalType
        if (options.length > 0) {
          type = correctAnswer.includes(',') ? 'multiple' : 'single'
        } else if (!['fill', 'short'].includes(type)) {
          type = 'short'
        }
        return { ...q, type, originalType, options, correctAnswer }
      })
      startTimer()
    } else {
      ElMessage.info('暂无错题可练习')
      router.push('/student/learning/wrong')
    }
  } catch (error) {
    console.error('加载错题失败:', error)
    ElMessage.error('加载错题失败，请返回重试')
  }
}

onMounted(() => {
  loadQuestions()
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style lang="scss" scoped>
.wrong-practice {
  min-height: 100vh;
  background: var(--bg-secondary);
  padding-bottom: 40px;
}

.practice-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  margin-bottom: 24px;

  .header-left {
    display: flex;
    align-items: center;
    gap: 16px;

    h2 {
      margin: 0;
      font-size: 18px;
    }
  }

  .header-center {
    display: flex;
    align-items: center;
    gap: 12px;

    .progress-text {
      font-size: 14px;
      color: var(--text-secondary);
    }
  }

  .header-right {
    .timer {
      display: flex;
      align-items: center;
      gap: 6px;
      font-size: 16px;
      font-weight: 500;
      color: var(--primary-color);
    }
  }
}

.practice-content {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 24px;
}

.question-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);

  .question-meta {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 16px;

    .error-count {
      margin-left: auto;
      font-size: 13px;
      color: #ff4d4f;
    }
  }

  .question-stem {
    font-size: 16px;
    line-height: 1.8;
    color: var(--text-primary);
    margin-bottom: 24px;

    .question-number {
      font-weight: 600;
      margin-right: 8px;
    }
  }
}

.options-list {
  .option-item {
    display: flex;
    align-items: center;
    padding: 14px 16px;
    margin-bottom: 12px;
    border: 2px solid var(--border-light);
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.3s;

    &:hover:not(.correct):not(.wrong) {
      border-color: var(--primary-color);
      background: rgba(64, 128, 255, 0.05);
    }

    &.selected {
      border-color: var(--primary-color);
      background: rgba(64, 128, 255, 0.1);
    }

    &.correct {
      border-color: #52c41a;
      background: rgba(82, 196, 26, 0.1);
    }

    &.wrong {
      border-color: #ff4d4f;
      background: rgba(255, 77, 79, 0.1);
    }

    .option-key {
      width: 28px;
      height: 28px;
      line-height: 28px;
      text-align: center;
      border-radius: 50%;
      background: var(--bg-secondary);
      font-weight: 500;
      margin-right: 12px;
    }

    .option-content {
      flex: 1;
    }

    .result-icon {
      font-size: 20px;
      &.correct { color: #52c41a; }
      &.wrong { color: #ff4d4f; }
    }
  }
}

.fill-answer, .text-answer {
  margin-bottom: 20px;
}

.answer-analysis {
  margin-top: 24px;
  padding: 16px;
  background: var(--bg-secondary);
  border-radius: 8px;

  .analysis-header {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 12px;
    font-size: 16px;
    font-weight: 500;

    .correct { color: #52c41a; }
    .wrong { color: #ff4d4f; }
  }

  .correct-answer {
    margin-bottom: 8px;
    color: var(--text-primary);
  }

  .analysis-content {
    color: var(--text-secondary);
    line-height: 1.6;
  }
}

.action-buttons {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

.practice-result {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 200px);
  padding: 24px;

  .result-card {
    background: white;
    border-radius: 16px;
    padding: 48px;
    text-align: center;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
    max-width: 600px;
    width: 100%;

    .result-icon {
      margin-bottom: 24px;

      &.success { color: #52c41a; }
      &.fail { color: #ff4d4f; }
    }

    h2 {
      margin: 0 0 32px;
      font-size: 24px;
    }

    .result-stats {
      display: flex;
      justify-content: center;
      gap: 32px;
      margin-bottom: 40px;

      .stat-item {
        .stat-value {
          font-size: 28px;
          font-weight: 600;
          color: var(--text-primary);

          &.correct { color: #52c41a; }
          &.wrong { color: #ff4d4f; }
        }

        .stat-label {
          font-size: 14px;
          color: var(--text-secondary);
          margin-top: 4px;
        }
      }
    }

    .result-actions {
      display: flex;
      justify-content: center;
      gap: 16px;
    }
  }
}
</style>
