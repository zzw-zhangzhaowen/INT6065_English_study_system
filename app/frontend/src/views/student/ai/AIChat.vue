<template>
  <div class="ai-chat">
    <!-- 会话列表 -->
    <aside class="chat-side" :class="{ collapsed: sideCollapsed }">
      <div class="side-head">
        <el-button type="primary" class="new-btn" @click="newSession">
          <el-icon><Plus /></el-icon>
          <span v-if="!sideCollapsed">新对话</span>
        </el-button>
        <el-button link class="fold-btn" @click="sideCollapsed = !sideCollapsed">
          <el-icon><Fold v-if="!sideCollapsed" /><Expand v-else /></el-icon>
        </el-button>
      </div>

      <div class="side-list">
        <div
          v-for="s in sessions"
          :key="s.id"
          class="side-item"
          :class="{ active: s.id === currentId }"
          @click="switchSession(s.id)"
        >
          <el-icon><ChatDotRound /></el-icon>
          <div v-if="!sideCollapsed" class="side-item-body">
            <div class="side-item-title">{{ s.title || '新对话' }}</div>
            <div class="side-item-time">{{ timeAgo(s.at) }}</div>
          </div>
          <el-icon v-if="!sideCollapsed" class="del" @click.stop="removeSession(s.id)"><Delete /></el-icon>
        </div>
        <div v-if="!sessions.length && !sideCollapsed" class="side-empty">还没有对话记录</div>
      </div>

      <div v-if="!sideCollapsed" class="side-foot">
        <div class="mode-row">
          <span class="dot"></span>
          {{ modeText }}
        </div>
        <div class="mode-tip">真 AI 接口已预留，切换只需改一行配置</div>
      </div>
    </aside>

    <!-- 主区 -->
    <section class="chat-main">
      <header class="chat-head">
        <div class="chat-title">
          AI 英语学习助手
          <span class="ai-badge">多轮上下文</span>
        </div>
        <div class="chat-tools">
          <el-button link @click="clearCurrent" :disabled="!messages.length">
            <el-icon><Delete /></el-icon> 清空
          </el-button>
        </div>
      </header>

      <div ref="scrollRef" class="chat-body">
        <!-- 空态 -->
        <div v-if="!messages.length" class="welcome">
          <div class="welcome-badge"><el-icon><MagicStick /></el-icon></div>
          <h2>可以直接提问，也可以贴句子让我诊断</h2>
          <p>
            我内置了 8 个语法知识模块、34 条常见错误规则和一套四级写作评分模型。
            英文句子发给我会做逐句标错，中文问题会按「结论 → 规则 → 例句 → 自测」的结构回答。
          </p>
          <div class="welcome-cards">
            <div v-for="q in starterQuestions" :key="q.text" class="wcard" @click="send(q.text)">
              <div class="wcard-icon"><el-icon><component :is="q.icon" /></el-icon></div>
              <div class="wcard-title">{{ q.title }}</div>
              <div class="wcard-desc">{{ q.desc }}</div>
            </div>
          </div>
        </div>

        <!-- 消息 -->
        <div v-for="(m, i) in messages" :key="i" class="msg" :class="m.role">
          <div class="msg-avatar">
            <el-avatar v-if="m.role === 'user'" :size="34">{{ userInitial }}</el-avatar>
            <div v-else class="ai-avatar">AI</div>
          </div>
          <div class="msg-main">
            <div class="msg-meta">
              <span class="who">{{ m.role === 'user' ? '我' : 'AI 助手' }}</span>
              <span class="when">{{ m.time }}</span>
            </div>
            <div v-if="m.role === 'assistant'" class="msg-bubble ai">
              <MarkdownView :content="m.content" />
              <span v-if="m.streaming" class="caret"></span>
            </div>
            <div v-else class="msg-bubble user">{{ m.content }}</div>
          </div>
        </div>

        <AiLoader v-if="thinking" :steps="thinkSteps" hint="本地规则引擎正在匹配知识库" class="loader" />

        <!-- 追问建议 -->
        <div v-if="!thinking && suggestions.length" class="suggest-row">
          <span class="suggest-label">继续问：</span>
          <span v-for="s in suggestions" :key="s" class="suggest-chip" @click="send(s)">{{ s }}</span>
        </div>
      </div>

      <!-- 输入 -->
      <footer class="chat-input">
        <el-input
          v-model="draft"
          type="textarea"
          :rows="2"
          resize="none"
          placeholder="输入问题，或粘贴一段英文让我逐句诊断（Enter 发送，Shift + Enter 换行）"
          @keydown.enter.exact.prevent="send()"
        />
        <div class="input-bar">
          <div class="input-hint">
            当前：{{ modeText }} · 回答基于内置语料，不确定的内容会明确说明
          </div>
          <el-button type="primary" :loading="thinking" :disabled="!draft.trim()" @click="send()">
            发送
            <el-icon class="el-icon--right"><Promotion /></el-icon>
          </el-button>
        </div>
      </footer>
    </section>
  </div>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import MarkdownView from '@/components/ai/MarkdownView.vue'
import AiLoader from '@/components/ai/AiLoader.vue'
import { ai, modeLabel } from '@/ai'
import { bumpUsage } from '@/ai/usage'
import { useUserStore } from '@/stores/user'

const STORE_KEY = 'ai_chat_sessions_v1'

const userStore = useUserStore()
const userInitial = computed(() => (userStore.userInfo?.nickname || 'U').charAt(0))

const sessions = ref([])
const currentId = ref('')
const messages = ref([])
const draft = ref('')
const thinking = ref(false)
const suggestions = ref([])
const sideCollapsed = ref(false)
const scrollRef = ref(null)

const modeText = modeLabel()

const thinkSteps = [
  '识别问题意图…',
  '检索语法知识库…',
  '匹配例句与常见错误…',
  '组织讲解结构…'
]

const starterQuestions = [
  { title: '语法点讲解', desc: '虚拟语气 / 定语从句 / 非谓语', icon: 'Reading', text: '虚拟语气怎么用？' },
  { title: '句子诊断', desc: '贴一句英文，逐处标错', icon: 'EditPen', text: 'i think people is easy to find a job when they have a degree.' },
  { title: '写作提分', desc: '结构、连接词、扣分点', icon: 'Document', text: '四级作文怎么写才能上 12 分？' },
  { title: '备考规划', desc: '按剩余时间倒排', icon: 'Calendar', text: '我还有 4 周考四级，怎么安排复习？' }
]

function load() {
  try {
    const raw = localStorage.getItem(STORE_KEY)
    if (raw) sessions.value = JSON.parse(raw) || []
  } catch (e) {
    sessions.value = []
  }
  if (sessions.value.length) {
    switchSession(sessions.value[0].id)
  } else {
    newSession()
  }
}

function persist() {
  try {
    localStorage.setItem(STORE_KEY, JSON.stringify(sessions.value.slice(0, 20)))
  } catch (e) { /* 忽略 */ }
}

function newSession() {
  const id = `s_${Date.now()}`
  sessions.value.unshift({ id, title: '', at: Date.now(), messages: [] })
  currentId.value = id
  messages.value = []
  suggestions.value = []
  persist()
}

function switchSession(id) {
  currentId.value = id
  const s = sessions.value.find(x => x.id === id)
  messages.value = s ? [...s.messages] : []
  suggestions.value = []
  scrollToBottom()
}

function removeSession(id) {
  sessions.value = sessions.value.filter(s => s.id !== id)
  if (currentId.value === id) {
    if (sessions.value.length) switchSession(sessions.value[0].id)
    else newSession()
  }
  persist()
}

function clearCurrent() {
  messages.value = []
  suggestions.value = []
  syncSession()
}

function syncSession() {
  const s = sessions.value.find(x => x.id === currentId.value)
  if (!s) return
  s.messages = messages.value.map(m => ({ role: m.role, content: m.content, time: m.time }))
  s.at = Date.now()
  if (!s.title && messages.value.length) {
    s.title = messages.value[0].content.slice(0, 18)
  }
  persist()
}

function now() {
  const d = new Date()
  return `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

function timeAgo(at) {
  if (!at) return ''
  const diff = Date.now() - at
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)} 分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)} 小时前`
  return `${Math.floor(diff / 86400000)} 天前`
}

function scrollToBottom() {
  nextTick(() => {
    const el = scrollRef.value
    if (el) el.scrollTop = el.scrollHeight
  })
}

let streamTimer = null

/** 逐字输出，模拟流式（真 AI 接入后换成 provider 的 streamChat） */
function streamInto(msg, fullText) {
  return new Promise(resolve => {
    let i = 0
    msg.content = ''
    msg.streaming = true
    const step = Math.max(2, Math.round(fullText.length / 120))
    streamTimer = setInterval(() => {
      i += step
      msg.content = fullText.slice(0, i)
      if (i % (step * 6) === 0) scrollToBottom()
      if (i >= fullText.length) {
        clearInterval(streamTimer)
        streamTimer = null
        msg.content = fullText
        msg.streaming = false
        resolve()
      }
    }, 16)
  })
}

async function send(text) {
  const q = (text ?? draft.value).trim()
  if (!q || thinking.value) return
  draft.value = ''
  suggestions.value = []

  const userMsg = { role: 'user', content: q, time: now(), streaming: false }
  messages.value.push(userMsg)
  syncSession()
  scrollToBottom()

  thinking.value = true
  try {
    const res = await ai.chat({
      question: q,
      messages: messages.value.filter(m => !m.streaming).map(m => ({ role: m.role, content: m.content }))
    })
    const data = res.data || {}
    thinking.value = false

    const aiMsg = { role: 'assistant', content: '', time: now(), streaming: true }
    messages.value.push(aiMsg)
    scrollToBottom()
    await streamInto(aiMsg, data.content || '（没有取到内容）')
    suggestions.value = data.suggestions || []
    syncSession()
    bumpUsage('chat', q.slice(0, 24))
    scrollToBottom()
  } catch (e) {
    thinking.value = false
    messages.value.push({
      role: 'assistant',
      content: `**出错了**：${e.message || '请求失败'}\n\n可以换一个问题，或检查 ai/config.js 中的模式配置。`,
      time: now(),
      streaming: false
    })
  }
}

onMounted(load)

onBeforeUnmount(() => {
  if (streamTimer) clearInterval(streamTimer)
})

watch(currentId, () => {
  suggestions.value = []
})
</script>

<style scoped lang="scss">
.ai-chat {
  display: flex;
  height: calc(100vh - 112px);
  gap: 14px;
  padding: 16px 20px;
}

/* ---------- 会话列表 ---------- */
.chat-side {
  width: 236px;
  flex: none;
  display: flex;
  flex-direction: column;
  background: #fff;
  border: 1px solid #e7edf8;
  border-radius: 14px;
  box-shadow: 0 10px 30px -24px rgba(16, 38, 84, 0.4);
  overflow: hidden;
  transition: width 0.25s;

  &.collapsed {
    width: 68px;
  }
}

.side-head {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 12px;
  border-bottom: 1px solid #f0f4fa;

  .new-btn {
    flex: 1;
    border-radius: 9px;
  }

  .fold-btn {
    color: #8494ae;
  }
}

.side-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.side-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 9px 10px;
  border-radius: 9px;
  cursor: pointer;
  margin-bottom: 4px;
  transition: background 0.2s;
  color: #64748b;

  &:hover {
    background: #f5f9ff;
  }

  &.active {
    background: linear-gradient(90deg, rgba(37, 99, 235, 0.1), rgba(56, 189, 248, 0.06));
    color: #1d4ed8;
  }

  .side-item-body {
    flex: 1;
    min-width: 0;
  }

  .side-item-title {
    font-size: 12.5px;
    font-weight: 600;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .side-item-time {
    font-size: 10.5px;
    color: #a9b6c9;
    margin-top: 2px;
  }

  .del {
    opacity: 0;
    font-size: 13px;
    color: #b9c6dc;

    &:hover {
      color: #dc2626;
    }
  }

  &:hover .del {
    opacity: 1;
  }
}

.side-empty {
  padding: 30px 10px;
  text-align: center;
  font-size: 12px;
  color: #a9b6c9;
}

.side-foot {
  padding: 12px;
  border-top: 1px solid #f0f4fa;
  background: #fbfdff;

  .mode-row {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 11.5px;
    font-weight: 600;
    color: #1d4ed8;
  }

  .dot {
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background: #0e9f6e;
    box-shadow: 0 0 6px rgba(14, 159, 110, 0.8);
  }

  .mode-tip {
    margin-top: 4px;
    font-size: 10.5px;
    color: #a9b6c9;
    line-height: 1.5;
  }
}

/* ---------- 主区 ---------- */
.chat-main {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  background: #fff;
  border: 1px solid #e7edf8;
  border-radius: 14px;
  box-shadow: 0 10px 30px -24px rgba(16, 38, 84, 0.4);
  overflow: hidden;
}

.chat-head {
  height: 56px;
  flex: none;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 18px;
  border-bottom: 1px solid #f0f4fa;
  background: linear-gradient(180deg, #fbfdff, #fff);
}

.chat-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14.5px;
  font-weight: 700;
  color: #0e1a32;
}

.chat-body {
  flex: 1;
  overflow-y: auto;
  padding: 20px 22px 8px;
  background:
    radial-gradient(700px 380px at 88% -6%, rgba(56, 189, 248, 0.07), transparent 66%),
    #fff;
}

/* ---------- 空态 ---------- */
.welcome {
  max-width: 720px;
  margin: 16px auto 0;
  text-align: center;

  .welcome-badge {
    width: 54px;
    height: 54px;
    margin: 0 auto 14px;
    border-radius: 16px;
    display: grid;
    place-items: center;
    font-size: 24px;
    color: #2563eb;
    background: linear-gradient(135deg, rgba(37, 99, 235, 0.12), rgba(56, 189, 248, 0.1));
    border: 1px solid rgba(37, 99, 235, 0.18);
  }

  h2 {
    font-size: 18px;
    font-weight: 750;
    color: #0e1a32;
    letter-spacing: -0.2px;
  }

  p {
    margin: 10px auto 22px;
    max-width: 560px;
    font-size: 12.5px;
    line-height: 1.8;
    color: #6b7a99;
  }
}

.welcome-cards {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
  text-align: left;
}

.wcard {
  padding: 13px 14px;
  border-radius: 12px;
  border: 1px solid #e7edf8;
  background: #fbfdff;
  cursor: pointer;
  transition: all 0.22s;

  &:hover {
    border-color: rgba(37, 99, 235, 0.3);
    background: #f5f9ff;
    transform: translateY(-2px);
  }

  .wcard-icon {
    color: #2563eb;
    margin-bottom: 7px;
  }

  .wcard-title {
    font-size: 13px;
    font-weight: 700;
    color: #16233f;
  }

  .wcard-desc {
    font-size: 11.5px;
    color: #8494ae;
    margin-top: 3px;
    line-height: 1.55;
  }
}

/* ---------- 消息 ---------- */
.msg {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;

  &.user {
    flex-direction: row-reverse;

    .msg-main {
      align-items: flex-end;
    }

    .msg-meta {
      justify-content: flex-end;
    }
  }
}

.msg-avatar {
  flex: none;
  padding-top: 2px;
}

.ai-avatar {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  display: grid;
  place-items: center;
  font-size: 11px;
  font-weight: 800;
  color: #fff;
  background: linear-gradient(135deg, #2563eb, #38bdf8);
  box-shadow: 0 6px 14px -6px rgba(37, 99, 235, 0.8);
}

.msg-main {
  display: flex;
  flex-direction: column;
  max-width: min(760px, 78%);
}

.msg-meta {
  display: flex;
  gap: 8px;
  align-items: center;
  margin-bottom: 5px;
  padding: 0 2px;

  .who {
    font-size: 11.5px;
    font-weight: 700;
    color: #4e5f7c;
  }

  .when {
    font-size: 10.5px;
    color: #b3c0d4;
  }
}

.msg-bubble {
  padding: 12px 15px;
  border-radius: 12px;
  font-size: 13.5px;
  line-height: 1.75;

  &.ai {
    background: #f8fbff;
    border: 1px solid #e7edf8;
    color: #33415c;
    border-top-left-radius: 4px;
  }

  &.user {
    background: linear-gradient(135deg, #2563eb, #3b82f6);
    color: #fff;
    border-top-right-radius: 4px;
    white-space: pre-wrap;
    word-break: break-word;
    box-shadow: 0 10px 22px -14px rgba(37, 99, 235, 0.9);
  }
}

.caret {
  display: inline-block;
  width: 6px;
  height: 14px;
  background: #2563eb;
  vertical-align: -2px;
  margin-left: 2px;
  animation: blink 0.9s steps(2) infinite;
}

@keyframes blink {
  0%, 49% { opacity: 1; }
  50%, 100% { opacity: 0; }
}

.loader {
  margin-bottom: 16px;
}

/* ---------- 追问 ---------- */
.suggest-row {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  padding: 4px 0 18px;
}

.suggest-label {
  font-size: 11.5px;
  color: #a9b6c9;
}

.suggest-chip {
  font-size: 11.5px;
  padding: 5px 11px;
  border-radius: 20px;
  border: 1px solid #dfe8f7;
  background: #fff;
  color: #4e5f7c;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    border-color: rgba(37, 99, 235, 0.45);
    color: #1d4ed8;
    background: #f5f9ff;
  }
}

/* ---------- 输入 ---------- */
.chat-input {
  flex: none;
  padding: 12px 18px 14px;
  border-top: 1px solid #f0f4fa;
  background: #fff;

  :deep(.el-textarea__inner) {
    border-radius: 12px;
    padding: 11px 14px;
    font-size: 13.5px;
    line-height: 1.7;
    resize: none;
  }
}

.input-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 9px;
  gap: 12px;
}

.input-hint {
  font-size: 11px;
  color: #a9b6c9;
  line-height: 1.5;
}
</style>
