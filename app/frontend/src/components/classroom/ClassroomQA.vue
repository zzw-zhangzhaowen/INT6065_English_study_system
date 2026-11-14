<template>
  <div class="classroom-qa">
    <!-- 提问输入区 -->
    <div class="qa-input-area">
      <el-input
        v-model="questionContent"
        type="textarea"
        :rows="2"
        placeholder="输入你的问题..."
        maxlength="500"
        show-word-limit
      />
      <div class="qa-input-actions">
        <el-checkbox v-model="isAnonymous" label="匿名提问" size="small" />
        <el-button type="primary" size="small" @click="submitQuestion" :disabled="!questionContent.trim()">
          提问
        </el-button>
      </div>
    </div>

    <!-- 问题列表 -->
    <div class="qa-list">
      <div v-if="questions.length === 0" class="qa-empty">
        <el-icon :size="36" color="#ddd"><ChatDotRound /></el-icon>
        <p>暂无问题，快来提问吧</p>
      </div>

      <div v-for="q in questions" :key="q.id" class="qa-item" :class="{ 'is-closed': q.status === 'CLOSED' }">
        <div class="qa-item-header">
          <div class="qa-user">
            <el-avatar :size="28" :src="q.avatar">{{ (q.username || '?').charAt(0) }}</el-avatar>
            <span class="qa-username">{{ q.username }}</span>
            <el-tag v-if="q.status === 'ANSWERED'" type="success" size="small">已回答</el-tag>
            <el-tag v-else-if="q.status === 'CLOSED'" type="info" size="small">已关闭</el-tag>
            <el-tag v-else type="warning" size="small">待回答</el-tag>
          </div>
          <span class="qa-time">{{ formatTime(q.createTime) }}</span>
        </div>

        <div class="qa-item-content">{{ q.content }}</div>

        <div class="qa-item-actions">
          <el-button text size="small" @click="likeQuestion(q)">
            <el-icon><Star /></el-icon> {{ q.likeCount || 0 }}
          </el-button>
          <el-button text size="small" @click="toggleAnswers(q)">
            <el-icon><ChatDotRound /></el-icon> {{ q.answerCount || 0 }} 回答
          </el-button>
          <el-button v-if="isTeacher && q.status !== 'CLOSED'" text size="small" type="danger" @click="closeQuestion(q)">
            关闭
          </el-button>
        </div>

        <!-- 回答区域 -->
        <div v-if="expandedQuestionId === q.id" class="qa-answers">
          <div v-for="a in q.answers || []" :key="a.id" class="answer-item">
            <div class="answer-header">
              <el-avatar :size="24" :src="a.avatar">{{ (a.username || '?').charAt(0) }}</el-avatar>
              <span class="answer-username">{{ a.username }}</span>
              <el-tag v-if="a.isBest" type="success" size="small">最佳</el-tag>
              <span class="answer-time">{{ formatTime(a.createTime) }}</span>
              <el-button v-if="isTeacher && !a.isBest" text size="small" type="success" @click="markBest(q, a)">
                设为最佳
              </el-button>
            </div>
            <div class="answer-content">{{ a.content }}</div>
          </div>

          <div v-if="(q.answers || []).length === 0" class="no-answers">暂无回答</div>

          <!-- 回答输入 -->
          <div class="answer-input" v-if="q.status !== 'CLOSED'">
            <el-input v-model="answerContents[q.id]" size="small" placeholder="输入回答..." @keyup.enter="submitAnswer(q)" />
            <el-button type="primary" size="small" @click="submitAnswer(q)" :disabled="!answerContents[q.id]?.trim()">
              回答
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { ChatDotRound, Star } from '@element-plus/icons-vue'
import { classroomApi } from '@/api/classroom'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const props = defineProps({
  classroomId: { type: [String, Number], required: true },
  stompClient: { type: Object, default: null },
  wsConnected: { type: Boolean, default: false },
  isTeacher: { type: Boolean, default: false }
})

const questions = ref([])
const questionContent = ref('')
const isAnonymous = ref(false)
const expandedQuestionId = ref(null)
const answerContents = reactive({})

// 课堂频道广播：连接可用时同步给其他同学，不可用时不影响本地操作
const broadcast = (action, payload = {}) => {
  if (!props.stompClient || !props.wsConnected) return
  try {
    props.stompClient.publish({
      destination: `/app/classroom/${props.classroomId}/qa`,
      body: JSON.stringify({ action, ...payload })
    })
  } catch (e) {
    console.error('课堂广播失败:', e)
  }
}

const formatTime = (time) => {
  if (!time) return ''
  const str = typeof time === 'string' ? time : time.toString()
  return str.substring(11, 16) || str.substring(0, 16)
}

const loadQuestions = async () => {
  try {
    const res = await classroomApi.getQuestions(props.classroomId)
    if (res.data) {
      questions.value = res.data
    }
  } catch (e) {
    console.error('加载问题列表失败:', e)
  }
}

const submitQuestion = async () => {
  const content = questionContent.value.trim()
  if (!content) return
  const anonymous = isAnonymous.value

  const optimistic = {
    id: Date.now(),
    username: anonymous ? '匿名同学' : (userStore.userInfo?.nickname || userStore.username || '我'),
    avatar: anonymous ? '' : (userStore.avatar || ''),
    content,
    status: 'OPEN',
    likeCount: 0,
    answerCount: 0,
    answers: [],
    createTime: new Date().toISOString()
  }

  // 先落到本地列表，界面立即反馈
  questions.value.unshift(optimistic)
  questionContent.value = ''
  isAnonymous.value = false
  ElMessage.success('提问已提交')

  // 再走接口持久化
  try {
    const res = await classroomApi.createQuestion(props.classroomId, {
      content,
      isAnonymous: anonymous
    })
    if (res?.data?.id) optimistic.id = res.data.id
  } catch (e) {
    console.error('提交问题失败:', e)
  }

  // 课堂频道广播给其他同学
  broadcast('CREATE_QUESTION', { content, isAnonymous: anonymous })
}

const submitAnswer = async (question) => {
  const content = answerContents[question.id]
  if (!content?.trim()) return

  const optimistic = {
    id: Date.now(),
    username: userStore.userInfo?.nickname || userStore.username || '我',
    avatar: userStore.avatar || '',
    content: content.trim(),
    isBest: false,
    createTime: new Date().toISOString()
  }

  if (!question.answers) question.answers = []
  question.answers.push(optimistic)
  question.answerCount = (question.answerCount || 0) + 1
  if (question.status === 'OPEN') question.status = 'ANSWERED'
  answerContents[question.id] = ''
  ElMessage.success('回答已提交')

  try {
    await classroomApi.createAnswer(props.classroomId, question.id, { content: content.trim() })
  } catch (e) {
    console.error('提交回答失败:', e)
  }

  broadcast('CREATE_ANSWER', { questionId: question.id, content: content.trim() })
}

const likeQuestion = async (question) => {
  question.likeCount = (question.likeCount || 0) + 1
  try {
    await classroomApi.likeQuestion(props.classroomId, question.id)
  } catch (e) {
    console.error('点赞失败:', e)
  }
  broadcast('LIKE_QUESTION', { questionId: question.id, likeCount: question.likeCount })
}

const closeQuestion = async (question) => {
  question.status = 'CLOSED'
  ElMessage.success('问题已关闭')
  try {
    await classroomApi.closeQuestion(props.classroomId, question.id)
  } catch (e) {
    console.error('关闭问题失败:', e)
  }
  broadcast('CLOSE_QUESTION', { questionId: question.id })
}

const markBest = async (question, answer) => {
  try {
    await classroomApi.markBestAnswer(props.classroomId, answer.id)
    // 更新本地状态
    if (question.answers) {
      question.answers.forEach(a => { a.isBest = a.id === answer.id })
    }
    ElMessage.success('已标记为最佳回答')
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const toggleAnswers = async (question) => {
  if (expandedQuestionId.value === question.id) {
    expandedQuestionId.value = null
    return
  }
  expandedQuestionId.value = question.id
  try {
    const res = await classroomApi.getAnswers(props.classroomId, question.id)
    if (res.data) {
      question.answers = res.data
    }
  } catch (e) {
    console.error('加载回答失败:', e)
  }
}

// 处理WebSocket推送的问答消息
const handleQaMessage = (data) => {
  switch (data.action) {
    case 'CREATE_QUESTION':
      if (data.data) {
        // 避免重复
        const exists = questions.value.find(q => q.id === data.data.id)
        if (!exists) {
          questions.value.unshift(data.data)
        }
      }
      break
    case 'CREATE_ANSWER':
      if (data.data) {
        const question = questions.value.find(q => q.id === data.data.questionId)
        if (question) {
          question.answerCount = (question.answerCount || 0) + 1
          if (question.status === 'OPEN') question.status = 'ANSWERED'
          if (expandedQuestionId.value === question.id) {
            if (!question.answers) question.answers = []
            const exists = question.answers.find(a => a.id === data.data.id)
            if (!exists) {
              question.answers.push(data.data)
            }
          }
        }
      }
      break
    case 'LIKE_QUESTION':
      if (data.questionId) {
        const question = questions.value.find(q => q.id === data.questionId)
        if (question) {
          question.likeCount = data.likeCount
        }
      }
      break
    case 'CLOSE_QUESTION':
      if (data.questionId) {
        const question = questions.value.find(q => q.id === data.questionId)
        if (question) {
          question.status = 'CLOSED'
        }
      }
      break
  }
}

defineExpose({ handleQaMessage, loadQuestions })

onMounted(() => {
  loadQuestions()
})
</script>

<style lang="scss" scoped>
.classroom-qa {
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.qa-input-area {
  padding: 12px;
  border-bottom: 1px solid #f0f0f0;

  .qa-input-actions {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 8px;
  }
}

.qa-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px 12px;
}

.qa-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
  color: #ccc;

  p {
    margin-top: 8px;
    font-size: 13px;
  }
}

.qa-item {
  padding: 12px;
  margin-bottom: 8px;
  background: #fafafa;
  border-radius: 8px;
  transition: background 0.2s;

  &:hover {
    background: #f5f5f5;
  }

  &.is-closed {
    opacity: 0.6;
  }

  .qa-item-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .qa-user {
      display: flex;
      align-items: center;
      gap: 8px;

      .qa-username {
        font-size: 13px;
        font-weight: 600;
        color: #333;
      }
    }

    .qa-time {
      font-size: 11px;
      color: #999;
    }
  }

  .qa-item-content {
    margin: 8px 0;
    font-size: 14px;
    color: #333;
    line-height: 1.6;
  }

  .qa-item-actions {
    display: flex;
    gap: 4px;
  }
}

.qa-answers {
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px solid #eee;

  .answer-item {
    padding: 8px;
    margin-bottom: 6px;
    background: #fff;
    border-radius: 6px;

    .answer-header {
      display: flex;
      align-items: center;
      gap: 6px;
      margin-bottom: 4px;

      .answer-username {
        font-size: 12px;
        font-weight: 600;
        color: #555;
      }

      .answer-time {
        font-size: 11px;
        color: #999;
        margin-left: auto;
      }
    }

    .answer-content {
      font-size: 13px;
      color: #444;
      line-height: 1.5;
      padding-left: 30px;
    }
  }

  .no-answers {
    text-align: center;
    color: #ccc;
    font-size: 12px;
    padding: 12px 0;
  }

  .answer-input {
    display: flex;
    gap: 8px;
    margin-top: 8px;
  }
}
</style>
