import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { examApi } from '@/api/exam'

export const useExamStore = defineStore('exam', () => {
  // 当前考试记录
  const currentExamRecord = ref(null)
  // 考试题目列表
  const questions = ref([])
  // 用户答案
  const answers = ref({})
  // 剩余时间（秒）
  const remainingTime = ref(0)
  // 考试状态
  const examStatus = ref('idle') // idle, started, submitted

  // 计算属性
  const answeredCount = computed(() => Object.keys(answers.value).length)
  const totalQuestions = computed(() => questions.value.length)
  const progress = computed(() => {
    if (totalQuestions.value === 0) return 0
    return Math.round((answeredCount.value / totalQuestions.value) * 100)
  })

  // 开始考试
  async function startExam(examId) {
    try {
      const res = await examApi.startExam(examId)
      currentExamRecord.value = res.data.record
      questions.value = res.data.questions || []
      remainingTime.value = res.data.remainingTime || 0
      answers.value = {}
      examStatus.value = 'started'
      return res
    } catch (error) {
      throw error
    }
  }

  // 保存答案
  function saveAnswer(questionId, answer) {
    answers.value[questionId] = answer
  }

  // 提交考试
  async function submitExam() {
    if (!currentExamRecord.value) {
      throw new Error('没有进行中的考试')
    }

    try {
      const res = await examApi.submitExam({
        recordId: currentExamRecord.value.id,
        answers: Object.entries(answers.value).map(([questionId, answer]) => ({
          questionId: Number(questionId),
          answer: Array.isArray(answer) ? answer.join(',') : String(answer || ''),
          answerTime: 0
        }))
      })
      examStatus.value = 'submitted'
      return res
    } catch (error) {
      throw error
    }
  }

  // 更新剩余时间
  function updateRemainingTime(time) {
    remainingTime.value = time
  }

  // 重置考试状态
  function resetExam() {
    currentExamRecord.value = null
    questions.value = []
    answers.value = {}
    remainingTime.value = 0
    examStatus.value = 'idle'
  }

  return {
    currentExamRecord,
    questions,
    answers,
    remainingTime,
    examStatus,
    answeredCount,
    totalQuestions,
    progress,
    startExam,
    saveAnswer,
    submitExam,
    updateRemainingTime,
    resetExam
  }
})
