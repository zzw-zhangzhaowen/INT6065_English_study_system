<template>
  <div class="exam-review">
    <!-- 页面头部 -->
    <div class="review-header">
      <div class="header-left">
        <el-button @click="goBack" :icon="ArrowLeft">返回</el-button>
        <h2>{{ examInfo.title }} - 答题回顾</h2>
      </div>
      <div class="header-right">
        <span class="score-info">得分：{{ examInfo.score }} / {{ examInfo.totalScore }}</span>
        <el-tag :type="examInfo.passed ? 'success' : 'danger'">
          {{ examInfo.passed ? '已通过' : '未通过' }}
        </el-tag>
      </div>
    </div>

    <!-- 答题卡 -->
    <div class="answer-sheet-panel">
      <div class="panel-header">
        <span>答题卡</span>
        <div class="legend">
          <span class="legend-item correct"><i></i>正确</span>
          <span class="legend-item wrong"><i></i>错误</span>
          <span class="legend-item unanswered"><i></i>未答</span>
        </div>
      </div>
      <div class="answer-grid">
        <div
          v-for="(q, index) in questions"
          :key="q.id"
          class="answer-item"
          :class="{
            correct: q.isCorrect,
            wrong: !q.isCorrect && q.userAnswer,
            unanswered: !q.userAnswer,
            active: currentIndex === index
          }"
          @click="jumpToQuestion(index)"
        >
          {{ index + 1 }}
        </div>
      </div>
    </div>

    <!-- 题目详情 -->
    <div class="question-detail">
      <div class="question-nav">
        <el-button :disabled="currentIndex === 0" @click="prevQuestion">
          <el-icon><ArrowLeft /></el-icon>上一题
        </el-button>
        <span class="question-progress">{{ currentIndex + 1 }} / {{ questions.length }}</span>
        <el-button :disabled="currentIndex === questions.length - 1" @click="nextQuestion">
          下一题<el-icon><ArrowRight /></el-icon>
        </el-button>
      </div>

      <div class="question-content" v-if="currentQuestion">
        <div class="question-header">
          <el-tag size="small">{{ getQuestionTypeName(currentQuestion.type) }}</el-tag>
          <span class="question-score">{{ currentQuestion.score }}分</span>
          <el-tag :type="currentQuestion.isCorrect ? 'success' : 'danger'" size="small">
            {{ currentQuestion.isCorrect ? '回答正确' : '回答错误' }}
          </el-tag>
        </div>

        <div class="question-stem">
          <span class="question-number">{{ currentIndex + 1 }}.</span>
          {{ currentQuestion.content }}
        </div>

        <!-- 选择题选项 -->
        <div v-if="currentQuestion.options && currentQuestion.options.length > 0" class="options-list">
          <div
            v-for="(option, optIndex) in currentQuestion.options"
            :key="optIndex"
            class="option-item"
            :class="{
              'user-selected': isUserSelected(option.key),
              'correct-answer': isCorrectAnswer(option.key),
              'wrong-selected': isUserSelected(option.key) && !isCorrectAnswer(option.key)
            }"
          >
            <span class="option-key">{{ option.key }}</span>
            <span class="option-content">{{ option.content }}</span>
            <el-icon v-if="isCorrectAnswer(option.key)" class="correct-icon"><CircleCheck /></el-icon>
            <el-icon v-if="isUserSelected(option.key) && !isCorrectAnswer(option.key)" class="wrong-icon"><CircleClose /></el-icon>
          </div>
        </div>

        <!-- 填空题/简答题 -->
        <div v-else class="text-answer">
          <div class="answer-section">
            <h4>你的答案：</h4>
            <div class="answer-box user-answer">
              {{ currentQuestion.userAnswer || '未作答' }}
            </div>
          </div>
          <div class="answer-section">
            <h4>参考答案：</h4>
            <div class="answer-box correct-answer">
              {{ currentQuestion.correctAnswer }}
            </div>
          </div>
        </div>

        <!-- 答案解析 -->
        <div class="answer-analysis" v-if="currentQuestion.analysis">
          <h4><el-icon><InfoFilled /></el-icon>答案解析</h4>
          <div class="analysis-content">{{ currentQuestion.analysis }}</div>
        </div>

        <!-- 加入错题本 -->
        <div class="question-actions" v-if="!currentQuestion.isCorrect">
          <el-button type="warning" @click="addToWrongBook" :loading="addingToWrong">
            <el-icon><Collection /></el-icon>
            {{ currentQuestion.inWrongBook ? '已加入错题本' : '加入错题本' }}
          </el-button>
        </div>
      </div>
    </div>

    <!-- 统计信息 -->
    <div class="review-stats">
      <div class="stat-card">
        <div class="stat-value correct">{{ correctCount }}</div>
        <div class="stat-label">正确</div>
      </div>
      <div class="stat-card">
        <div class="stat-value wrong">{{ wrongCount }}</div>
        <div class="stat-label">错误</div>
      </div>
      <div class="stat-card">
        <div class="stat-value unanswered">{{ unansweredCount }}</div>
        <div class="stat-label">未答</div>
      </div>
      <div class="stat-card">
        <div class="stat-value accuracy">{{ accuracy }}%</div>
        <div class="stat-label">正确率</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, ArrowRight, CircleCheck, CircleClose, InfoFilled, Collection } from '@element-plus/icons-vue'
import { examApi } from '@/api/exam'
import { learningApi } from '@/api/learning'

const route = useRoute()
const router = useRouter()

const examInfo = ref({
  title: '',
  score: 0,
  totalScore: 0,
  passed: false
})

const questions = ref([])
const currentIndex = ref(0)
const addingToWrong = ref(false)

const currentQuestion = computed(() => questions.value[currentIndex.value])

const correctCount = computed(() => questions.value.filter(q => q.isCorrect).length)
const wrongCount = computed(() => questions.value.filter(q => !q.isCorrect && q.userAnswer).length)
const unansweredCount = computed(() => questions.value.filter(q => !q.userAnswer).length)
const accuracy = computed(() => {
  if (questions.value.length === 0) return 0
  return Math.round((correctCount.value / questions.value.length) * 100)
})

onMounted(() => {
  loadExamReview()
})

const loadExamReview = async () => {
  try {
    const recordId = route.params.id
    const res = await examApi.getExamReview(recordId)
    if (res.data) {
      examInfo.value = {
        title: res.data.title || '考试',
        score: res.data.score || 0,
        totalScore: res.data.totalScore || 100,
        passed: res.data.passed || false
      }
      questions.value = (res.data.questions || []).map(q => {
        // 解析options：后端可能返回JSON字符串或数组
        let options = []
        if (q.options) {
          try {
            const parsed = typeof q.options === 'string' ? JSON.parse(q.options) : q.options
            if (Array.isArray(parsed)) {
              options = parsed.map((opt, index) => {
                if (typeof opt === 'string') {
                  return { key: String.fromCharCode(65 + index), content: opt }
                }
                return opt
              })
            }
          } catch (e) {
            console.error('解析options失败:', e)
          }
        }

        return {
          id: q.id,
          type: q.type || 'SINGLE_CHOICE',
          content: q.content || q.title,
          options,
          // 将数字索引答案转换为字母key格式
          userAnswer: convertAnswerToKey(q.userAnswer),
          correctAnswer: convertAnswerToKey(q.correctAnswer),
          isCorrect: q.isCorrect,
          score: q.score || 0,
          analysis: q.analysis,
          inWrongBook: q.inWrongBook || false
        }
      })
    }
  } catch (error) {
    console.error('加载考试回顾失败:', error)
    ElMessage.error('加载考试回顾失败，请返回重试')
  }
}

// 将数字索引答案转换为字母（"0"→"A", "1"→"B"）
const convertAnswerToKey = (answer) => {
  if (!answer) return answer
  
  // 处理多选题："0,2" 或 ["0", "2"]
  if (typeof answer === 'string' && answer.includes(',')) {
    return answer.split(',').map(idx => String.fromCharCode(65 + parseInt(idx))).join(',')
  }
  if (Array.isArray(answer)) {
    return answer.map(idx => String.fromCharCode(65 + parseInt(idx)))
  }
  
  // 处理单选题："0" → "A"
  if (/^\d+$/.test(answer)) {
    return String.fromCharCode(65 + parseInt(answer))
  }
  
  // 已经是字母格式，直接返回
  return answer
}

const getQuestionTypeName = (type) => {
  const typeMap = {
    single: '单选题',
    multiple: '多选题',
    fill: '填空题',
    short: '简答题',
    judge: '判断题',
    LISTENING: '听力题',
    READING: '阅读题',
    WRITING: '写作题',
    TRANSLATION: '翻译题',
    CLOZE: '完形填空',
    SINGLE_CHOICE: '单选题',
    MULTIPLE_CHOICE: '多选题'
  }
  return typeMap[type] || '其他'
}

const isUserSelected = (key) => {
  if (!currentQuestion.value) return false
  const answer = currentQuestion.value.userAnswer
  if (Array.isArray(answer)) {
    return answer.includes(key)
  }
  return answer === key
}

const isCorrectAnswer = (key) => {
  if (!currentQuestion.value) return false
  const answer = currentQuestion.value.correctAnswer
  if (Array.isArray(answer)) {
    return answer.includes(key)
  }
  return answer === key
}

const jumpToQuestion = (index) => {
  currentIndex.value = index
}

const prevQuestion = () => {
  if (currentIndex.value > 0) {
    currentIndex.value--
  }
}

const nextQuestion = () => {
  if (currentIndex.value < questions.value.length - 1) {
    currentIndex.value++
  }
}

const addToWrongBook = async () => {
  if (!currentQuestion.value || currentQuestion.value.inWrongBook) return
  addingToWrong.value = true
  try {
    await learningApi.addToWrongBook(currentQuestion.value.id)
    currentQuestion.value.inWrongBook = true
    ElMessage.success('已加入错题本')
  } catch (error) {
    console.error('加入错题本失败:', error)
    ElMessage.error('加入错题本失败')
  } finally {
    addingToWrong.value = false
  }
}

const goBack = () => {
  router.back()
}
</script>

<style lang="scss" scoped>
.exam-review {
  padding: 20px;
  display: grid;
  grid-template-columns: 200px 1fr 200px;
  grid-template-rows: auto 1fr;
  gap: 20px;
  min-height: calc(100vh - 120px);
}

.review-header {
  grid-column: 1 / -1;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 16px 24px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

  .header-left {
    display: flex;
    align-items: center;
    gap: 16px;

    h2 {
      margin: 0;
      font-size: 18px;
    }
  }

  .header-right {
    display: flex;
    align-items: center;
    gap: 16px;

    .score-info {
      font-size: 16px;
      font-weight: 600;
      color: #4080ff;
    }
  }
}

.answer-sheet-panel {
  background: white;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

  .panel-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    font-weight: 600;

    .legend {
      display: flex;
      gap: 12px;
      font-size: 12px;
      font-weight: normal;

      .legend-item {
        display: flex;
        align-items: center;
        gap: 4px;

        i {
          width: 12px;
          height: 12px;
          border-radius: 2px;
        }

        &.correct i { background: #52c41a; }
        &.wrong i { background: #ff4d4f; }
        &.unanswered i { background: #d9d9d9; }
      }
    }
  }

  .answer-grid {
    display: grid;
    grid-template-columns: repeat(5, 1fr);
    gap: 8px;

    .answer-item {
      width: 32px;
      height: 32px;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 4px;
      font-size: 12px;
      cursor: pointer;
      transition: all 0.2s;
      background: #f5f5f5;

      &.correct { background: #52c41a; color: white; }
      &.wrong { background: #ff4d4f; color: white; }
      &.unanswered { background: #d9d9d9; color: #666; }
      &.active { box-shadow: 0 0 0 2px #4080ff; }

      &:hover {
        transform: scale(1.1);
      }
    }
  }
}

.question-detail {
  background: white;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

  .question-nav {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    padding-bottom: 16px;
    border-bottom: 1px solid #f0f0f0;

    .question-progress {
      font-size: 14px;
      color: #666;
    }
  }

  .question-content {
    .question-header {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 16px;

      .question-score {
        color: #4080ff;
        font-weight: 500;
      }
    }

    .question-stem {
      font-size: 16px;
      line-height: 1.8;
      margin-bottom: 20px;

      .question-number {
        font-weight: 600;
        color: #4080ff;
      }
    }

    .options-list {
      .option-item {
        display: flex;
        align-items: center;
        padding: 12px 16px;
        margin-bottom: 8px;
        border-radius: 8px;
        background: #f9f9f9;
        transition: all 0.2s;

        .option-key {
          width: 28px;
          height: 28px;
          display: flex;
          align-items: center;
          justify-content: center;
          border-radius: 50%;
          background: white;
          border: 1px solid #d9d9d9;
          margin-right: 12px;
          font-weight: 500;
        }

        .option-content {
          flex: 1;
        }

        .correct-icon {
          color: #52c41a;
          font-size: 20px;
        }

        .wrong-icon {
          color: #ff4d4f;
          font-size: 20px;
        }

        &.correct-answer {
          background: #f6ffed;
          border: 1px solid #b7eb8f;

          .option-key {
            background: #52c41a;
            color: white;
            border-color: #52c41a;
          }
        }

        &.wrong-selected {
          background: #fff2f0;
          border: 1px solid #ffccc7;

          .option-key {
            background: #ff4d4f;
            color: white;
            border-color: #ff4d4f;
          }
        }

        &.user-selected:not(.wrong-selected) {
          .option-key {
            background: #4080ff;
            color: white;
            border-color: #4080ff;
          }
        }
      }
    }

    .text-answer {
      .answer-section {
        margin-bottom: 16px;

        h4 {
          margin: 0 0 8px;
          font-size: 14px;
          color: #666;
        }

        .answer-box {
          padding: 16px;
          border-radius: 8px;
          line-height: 1.8;

          &.user-answer {
            background: #f5f5f5;
            border: 1px solid #d9d9d9;
          }

          &.correct-answer {
            background: #f6ffed;
            border: 1px solid #b7eb8f;
          }
        }
      }
    }

    .answer-analysis {
      margin-top: 24px;
      padding: 16px;
      background: #e6f7ff;
      border-radius: 8px;
      border: 1px solid #91d5ff;

      h4 {
        display: flex;
        align-items: center;
        gap: 8px;
        margin: 0 0 12px;
        color: #1890ff;
      }

      .analysis-content {
        line-height: 1.8;
        color: #333;
      }
    }

    .question-actions {
      margin-top: 24px;
      padding-top: 16px;
      border-top: 1px solid #f0f0f0;
    }
  }
}

.review-stats {
  display: flex;
  flex-direction: column;
  gap: 12px;

  .stat-card {
    background: white;
    border-radius: 8px;
    padding: 16px;
    text-align: center;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

    .stat-value {
      font-size: 24px;
      font-weight: 600;
      margin-bottom: 4px;

      &.correct { color: #52c41a; }
      &.wrong { color: #ff4d4f; }
      &.unanswered { color: #d9d9d9; }
      &.accuracy { color: #4080ff; }
    }

    .stat-label {
      font-size: 12px;
      color: #999;
    }
  }
}

@media (max-width: 1200px) {
  .exam-review {
    grid-template-columns: 1fr;
  }

  .answer-sheet-panel {
    .answer-grid {
      grid-template-columns: repeat(10, 1fr);
    }
  }

  .review-stats {
    flex-direction: row;
    justify-content: space-around;
  }
}
</style>
