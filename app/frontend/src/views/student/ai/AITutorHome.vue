<template>
  <div class="ai-home">
    <!-- 顶部主视觉 -->
    <section class="hero tech-fade-up">
      <div class="hero-grid"></div>
      <div class="hero-inner">
        <div class="hero-left">
          <div class="hero-badge">
            <span class="dot"></span>
            本地规则引擎运行中
            <span class="sep">|</span>
            真 AI 接口已预留
          </div>
          <h1>
            AI 智能辅导
            <span class="grad">把每一处错误讲透</span>
          </h1>
          <p class="hero-desc">
            八个能力覆盖「讲、练、改、诊」四件事：对话答疑、作文逐句批改、错题溯源、能力画像、
            七天计划、长难句拆解、词汇速记、口语陪练。所有分析都基于你真实提交的内容，不是模板套话。
          </p>
          <div class="hero-actions">
            <el-button type="primary" size="large" @click="go('/student/ai-tutor/chat')">
              <el-icon><ChatDotRound /></el-icon>
              开始对话
            </el-button>
            <el-button size="large" @click="go('/student/ai-tutor/writing')">
              <el-icon><EditPen /></el-icon>
              批改一篇作文
            </el-button>
          </div>
        </div>

        <div class="hero-right">
          <div class="mini-stat">
            <div class="label">累计调用</div>
            <div class="value">{{ usageTotal }}<small>次</small></div>
          </div>
          <div class="mini-stat">
            <div class="label">批改作文</div>
            <div class="value">{{ usage.writing }}<small>篇</small></div>
          </div>
          <div class="mini-stat">
            <div class="label">能力指数</div>
            <div class="value">{{ profile.overall }}<small>分</small></div>
          </div>
        </div>
      </div>
    </section>

    <!-- 能力矩阵 -->
    <section class="section">
      <div class="tech-section-title">
        能力矩阵
        <span class="ai-badge">8 项</span>
      </div>
      <div class="cap-grid">
        <div
          v-for="(cap, i) in capabilities"
          :key="cap.path"
          class="cap-card"
          :style="{ animationDelay: i * 40 + 'ms' }"
          @click="go(cap.path)"
        >
          <div class="cap-icon" :style="{ background: cap.bg, color: cap.color }">
            <el-icon :size="20"><component :is="cap.icon" /></el-icon>
          </div>
          <div class="cap-body">
            <div class="cap-title">
              {{ cap.name }}
              <span class="cap-tag" :class="cap.priority === 'P0' ? 'p0' : 'p1'">{{ cap.priority }}</span>
            </div>
            <div class="cap-desc">{{ cap.desc }}</div>
            <div class="cap-points">
              <span v-for="p in cap.points" :key="p">{{ p }}</span>
            </div>
          </div>
          <el-icon class="cap-arrow"><ArrowRight /></el-icon>
        </div>
      </div>
    </section>

    <!-- 画像 + 建议 -->
    <section class="section two-col">
      <div class="ai-grad-card panel">
        <div class="panel-head">
          <div class="tech-section-title">AI 能力画像</div>
          <el-button link type="primary" @click="go('/student/ai-tutor/profile')">详细分析</el-button>
        </div>
        <div class="profile-body">
          <RadarChart :data="profile.radar" :size="200" />
          <div class="profile-side">
            <div class="profile-note">{{ profile.narrative }}</div>
            <div class="weak-list">
              <div v-for="a in profile.actions" :key="a.dimension" class="weak-item">
                <div class="weak-top">
                  <span class="weak-name">{{ a.dimension }}</span>
                  <span class="weak-val">{{ a.value }}</span>
                </div>
                <div class="weak-bar"><i :style="{ width: a.value + '%' }"></i></div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="panel">
        <div class="panel-head">
          <div class="tech-section-title">今天该做什么</div>
          <span class="ai-badge">AI 生成</span>
        </div>
        <div class="advice-list">
          <div v-for="(a, i) in advice" :key="i" class="advice-item">
            <div class="advice-index">{{ String(i + 1).padStart(2, '0') }}</div>
            <div class="advice-content">
              <div class="advice-title">{{ a.title }}</div>
              <div class="advice-desc">{{ a.desc }}</div>
            </div>
          </div>
        </div>
        <el-button class="advice-btn" type="primary" plain @click="go('/student/ai-tutor/plan')">
          生成七天学习计划
        </el-button>
      </div>
    </section>

    <!-- 最近使用 -->
    <section class="section">
      <div class="tech-section-title">最近使用</div>
      <div v-if="recent.length" class="recent-list">
        <div v-for="(r, i) in recent" :key="i" class="recent-item" @click="go(typePath(r.type))">
          <el-icon><component :is="typeIcon(r.type)" /></el-icon>
          <span class="recent-title">{{ r.title || typeName(r.type) }}</span>
          <span class="recent-time">{{ timeAgo(r.at) }}</span>
        </div>
      </div>
      <div v-else class="tech-empty">
        <div class="icon"><el-icon><MagicStick /></el-icon></div>
        <div class="title">还没有使用记录</div>
        <div class="desc">从上面的能力卡任选一项开始，用过的记录会自动出现在这里。</div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import RadarChart from '@/components/ai/RadarChart.vue'
import { ai } from '@/ai'
import { getUsage, totalUsage } from '@/ai/usage'

const router = useRouter()

const usage = ref(getUsage())
const usageTotal = computed(() => totalUsage())
const profile = ref({
  overall: 0,
  radar: [],
  narrative: '正在基于你的打卡与提交记录生成画像…',
  actions: []
})

const capabilities = [
  {
    name: 'AI 对话答疑',
    icon: 'ChatDotRound',
    path: '/student/ai-tutor/chat',
    priority: 'P0',
    color: '#2563eb',
    bg: 'linear-gradient(135deg, rgba(37,99,235,.12), rgba(56,189,248,.10))',
    desc: '多轮上下文 + 知识点结构化讲解，支持贴句子直接诊断',
    points: ['识别 11 类意图', '输出结论先行', '自动给自测题']
  },
  {
    name: '作文逐句批改',
    icon: 'EditPen',
    path: '/student/ai-tutor/writing',
    priority: 'P0',
    color: '#6366f1',
    bg: 'linear-gradient(135deg, rgba(99,102,241,.12), rgba(139,92,246,.10))',
    desc: '四维评分 + 逐句标错 + 原句与升级句对照，分数有依据可追溯',
    points: ['34 条错误规则', '评分依据逐条列出', '一键复制修正稿']
  },
  {
    name: '错题溯源',
    icon: 'Warning',
    path: '/student/ai-tutor/wrong',
    priority: 'P0',
    color: '#d97706',
    bg: 'linear-gradient(135deg, rgba(217,119,6,.12), rgba(245,158,11,.10))',
    desc: '错因归类到具体语法点，配上知识点卡与同类题',
    points: ['错因归类', '知识点卡片', '2 道同类题']
  },
  {
    name: '学习画像',
    icon: 'DataAnalysis',
    path: '/student/ai-tutor/profile',
    priority: 'P1',
    color: '#06b6d4',
    bg: 'linear-gradient(135deg, rgba(6,182,212,.12), rgba(56,189,248,.10))',
    desc: '五维雷达 + 短板排序，每个维度标注样本来源',
    points: ['五维雷达', '短板排序', '可追溯样本']
  },
  {
    name: '七天计划',
    icon: 'Calendar',
    path: '/student/ai-tutor/plan',
    priority: 'P1',
    color: '#0e9f6e',
    bg: 'linear-gradient(135deg, rgba(14,159,110,.12), rgba(16,185,129,.10))',
    desc: '按短板加权排日程，每天明确「做什么 + 产出什么」',
    points: ['按短板加权', '周内含模考', '留出休息日']
  },
  {
    name: '长难句拆解',
    icon: 'Reading',
    path: '/student/ai-tutor/sentence',
    priority: 'P1',
    color: '#8b5cf6',
    bg: 'linear-gradient(135deg, rgba(139,92,246,.12), rgba(168,85,247,.10))',
    desc: '找谓语 → 定主干 → 挂修饰，三刀切开复合句',
    points: ['主干识别', '成分逐层挂', '与真题例句对照']
  },
  {
    name: '词汇速记',
    icon: 'Collection',
    path: '/student/ai-tutor/vocab',
    priority: 'P1',
    color: '#dc2626',
    bg: 'linear-gradient(135deg, rgba(220,38,38,.12), rgba(244,63,94,.10))',
    desc: '词根 + 搭配 + 例句 + 易混词，一卡解决一个词',
    points: ['词根线索', '固定搭配', '易混词对照']
  },
  {
    name: '口语陪练',
    icon: 'Microphone',
    path: '/student/ai-tutor/speaking',
    priority: 'P1',
    color: '#0ea5e9',
    bg: 'linear-gradient(135deg, rgba(14,165,233,.12), rgba(37,99,235,.10))',
    desc: '分项评分 + 中式发音高风险音素提示（当前为文本估算）',
    points: ['五项评分', '逐词发音提醒', '跟读建议']
  }
]

const advice = computed(() => {
  const acts = profile.value.actions || []
  const list = []
  acts.forEach(a => {
    if (a.tips && a.tips[0]) {
      list.push({ title: `补「${a.dimension}」`, desc: a.tips[0] })
    }
  })
  list.push({
    title: '写一篇并立刻批改',
    desc: '写完立刻过一遍批改，比隔天再改有效得多 —— 记忆还在，改动才有意义。'
  })
  list.push({
    title: '错题当天清',
    desc: '错题当天做 2 道同类题确认，否则一周后基本会重复犯同一个错。'
  })
  return list.slice(0, 4)
})

const recent = computed(() => (usage.value.recent || []).slice(0, 6))

const TYPE_META = {
  chat: { name: 'AI 对话', icon: 'ChatDotRound', path: '/student/ai-tutor/chat' },
  writing: { name: '作文批改', icon: 'EditPen', path: '/student/ai-tutor/writing' },
  wrong: { name: '错题讲解', icon: 'Warning', path: '/student/ai-tutor/wrong' },
  plan: { name: '学习计划', icon: 'Calendar', path: '/student/ai-tutor/plan' },
  longSentence: { name: '长难句拆解', icon: 'Reading', path: '/student/ai-tutor/sentence' },
  vocab: { name: '词汇速记', icon: 'Collection', path: '/student/ai-tutor/vocab' },
  speaking: { name: '口语陪练', icon: 'Microphone', path: '/student/ai-tutor/speaking' },
  profile: { name: '学习画像', icon: 'DataAnalysis', path: '/student/ai-tutor/profile' }
}

const typeName = t => TYPE_META[t]?.name || t
const typeIcon = t => TYPE_META[t]?.icon || 'MagicStick'
const typePath = t => TYPE_META[t]?.path || '/student/ai-tutor'

function timeAgo(at) {
  if (!at) return ''
  const diff = Date.now() - at
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)} 分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)} 小时前`
  return `${Math.floor(diff / 86400000)} 天前`
}

function go(path) {
  router.push(path)
}

onMounted(async () => {
  try {
    const res = await ai.learningProfile({
      checkinDays: usage.value.chat + 12,
      aiChats: usage.value.chat,
      writingCount: usage.value.writing,
      wrongCount: 5,
      examScores: [72, 78]
    })
    profile.value = res.data
  } catch (e) {
    console.warn('画像生成失败', e)
  }
})
</script>

<style scoped lang="scss">
.ai-home {
  padding: 24px;
  max-width: 1280px;
  margin: 0 auto;
}

/* ---------- Hero ---------- */
.hero {
  position: relative;
  border-radius: 18px;
  overflow: hidden;
  background: linear-gradient(135deg, #0f2a63 0%, #1747a8 52%, #1d6fd4 100%);
  padding: 34px 36px;
  box-shadow: 0 30px 60px -34px rgba(16, 58, 140, 0.7);
}

.hero-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(255, 255, 255, 0.09) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.09) 1px, transparent 1px);
  background-size: 30px 30px;
  mask-image: radial-gradient(560px 320px at 82% 12%, #000 10%, transparent 72%);
  -webkit-mask-image: radial-gradient(560px 320px at 82% 12%, #000 10%, transparent 72%);
}

.hero::after {
  content: '';
  position: absolute;
  width: 420px;
  height: 320px;
  right: -80px;
  top: -120px;
  background: radial-gradient(circle, rgba(56, 189, 248, 0.45), transparent 66%);
  filter: blur(26px);
}

.hero-inner {
  position: relative;
  display: flex;
  gap: 32px;
  align-items: flex-end;
  justify-content: space-between;
  flex-wrap: wrap;
}

.hero-left {
  flex: 1;
  min-width: 320px;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  height: 26px;
  padding: 0 12px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.18);
  color: #cfe3ff;
  font-size: 11.5px;
  font-weight: 600;
  letter-spacing: 0.2px;

  .dot {
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background: #4ade80;
    box-shadow: 0 0 8px #4ade80;
    animation: pulse 1.8s infinite;
  }

  .sep {
    opacity: 0.4;
  }
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.35; }
}

.hero h1 {
  margin: 16px 0 0;
  font-size: 30px;
  font-weight: 800;
  letter-spacing: -0.8px;
  color: #fff;
  line-height: 1.3;

  .grad {
    display: block;
    font-size: 21px;
    font-weight: 700;
    letter-spacing: -0.3px;
    background: linear-gradient(100deg, #8ecbff, #7ef0e0);
    -webkit-background-clip: text;
    background-clip: text;
    -webkit-text-fill-color: transparent;
    margin-top: 4px;
  }
}

.hero-desc {
  margin-top: 14px;
  font-size: 13px;
  line-height: 1.85;
  color: rgba(226, 238, 255, 0.82);
  max-width: 640px;
}

.hero-actions {
  margin-top: 22px;
  display: flex;
  gap: 12px;

  :deep(.el-button--default) {
    background: rgba(255, 255, 255, 0.12);
    border: 1px solid rgba(255, 255, 255, 0.26);
    color: #eaf3ff;

    &:hover {
      background: rgba(255, 255, 255, 0.2);
      border-color: rgba(255, 255, 255, 0.4);
      color: #fff;
    }
  }
}

.hero-right {
  display: flex;
  gap: 12px;
}

.mini-stat {
  min-width: 96px;
  padding: 14px 16px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.16);
  backdrop-filter: blur(10px);

  .label {
    font-size: 11.5px;
    color: rgba(214, 232, 255, 0.75);
    margin-bottom: 6px;
  }

  .value {
    font-size: 22px;
    font-weight: 800;
    color: #fff;
    letter-spacing: -0.8px;
    font-variant-numeric: tabular-nums;

    small {
      font-size: 11px;
      font-weight: 600;
      margin-left: 3px;
      opacity: 0.6;
    }
  }
}

/* ---------- 区块 ---------- */
.section {
  margin-top: 26px;
}

.two-col {
  display: grid;
  grid-template-columns: 1.15fr 1fr;
  gap: 16px;
}

@media (max-width: 1080px) {
  .two-col {
    grid-template-columns: 1fr;
  }
}

/* ---------- 能力卡 ---------- */
.cap-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 14px;
}

.cap-card {
  display: flex;
  gap: 14px;
  align-items: flex-start;
  padding: 18px;
  border-radius: 14px;
  background: #fff;
  border: 1px solid #e7edf8;
  box-shadow: 0 8px 24px -20px rgba(16, 38, 84, 0.3);
  cursor: pointer;
  transition: all 0.28s cubic-bezier(0.22, 1, 0.36, 1);
  position: relative;
  overflow: hidden;

  &:hover {
    transform: translateY(-3px);
    border-color: rgba(37, 99, 235, 0.28);
    box-shadow: 0 20px 40px -24px rgba(37, 99, 235, 0.55);

    .cap-arrow {
      transform: translateX(3px);
      color: #2563eb;
    }
  }
}

.cap-icon {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  display: grid;
  place-items: center;
  flex: none;
}

.cap-body {
  flex: 1;
  min-width: 0;
}

.cap-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14.5px;
  font-weight: 700;
  color: #0e1a32;
}

.cap-tag {
  font-size: 9.5px;
  font-weight: 800;
  padding: 1px 6px;
  border-radius: 4px;
  letter-spacing: 0.4px;

  &.p0 {
    color: #1d4ed8;
    background: rgba(37, 99, 235, 0.1);
  }

  &.p1 {
    color: #7c3aed;
    background: rgba(139, 92, 246, 0.1);
  }
}

.cap-desc {
  margin-top: 6px;
  font-size: 12.5px;
  line-height: 1.65;
  color: #6b7a99;
}

.cap-points {
  margin-top: 10px;
  display: flex;
  gap: 6px;
  flex-wrap: wrap;

  span {
    font-size: 10.5px;
    padding: 2px 7px;
    border-radius: 5px;
    background: #f4f8ff;
    color: #5b6f92;
    border: 1px solid #e8effb;
  }
}

.cap-arrow {
  color: #b9c6dc;
  transition: all 0.25s;
  margin-top: 3px;
}

/* ---------- 面板 ---------- */
.panel {
  background: #fff;
  border: 1px solid #e7edf8;
  border-radius: 14px;
  padding: 18px 20px;
  box-shadow: 0 10px 30px -22px rgba(16, 38, 84, 0.35);
}

.panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 6px;

  :deep(.tech-section-title) {
    margin-bottom: 0;
  }
}

.profile-body {
  display: flex;
  gap: 16px;
  align-items: center;
  margin-top: 8px;
}

.profile-side {
  flex: 1;
  min-width: 0;
}

.profile-note {
  font-size: 12.5px;
  line-height: 1.75;
  color: #4e5f7c;
  margin-bottom: 12px;
}

.weak-item {
  margin-bottom: 10px;

  .weak-top {
    display: flex;
    justify-content: space-between;
    font-size: 12px;
    color: #6b7a99;
    margin-bottom: 5px;
  }

  .weak-val {
    font-weight: 700;
    color: #0e1a32;
    font-variant-numeric: tabular-nums;
  }

  .weak-bar {
    height: 5px;
    border-radius: 3px;
    background: #eef3fb;
    overflow: hidden;

    i {
      display: block;
      height: 100%;
      border-radius: 3px;
      background: linear-gradient(90deg, #2563eb, #38bdf8);
    }
  }
}

/* ---------- 建议 ---------- */
.advice-list {
  margin-top: 10px;
}

.advice-item {
  display: flex;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px dashed #eef2f9;

  &:last-child {
    border-bottom: none;
  }
}

.advice-index {
  font-size: 11px;
  font-weight: 800;
  color: #b9c9e2;
  font-variant-numeric: tabular-nums;
  padding-top: 2px;
}

.advice-title {
  font-size: 13px;
  font-weight: 650;
  color: #16233f;
  margin-bottom: 3px;
}

.advice-desc {
  font-size: 12px;
  line-height: 1.65;
  color: #6b7a99;
}

.advice-btn {
  width: 100%;
  margin-top: 14px;
  border-radius: 10px;
}

/* ---------- 最近 ---------- */
.recent-list {
  background: #fff;
  border: 1px solid #e7edf8;
  border-radius: 14px;
  overflow: hidden;
}

.recent-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 13px 18px;
  border-bottom: 1px solid #f2f6fc;
  cursor: pointer;
  transition: background 0.2s;

  &:last-child {
    border-bottom: none;
  }

  &:hover {
    background: #f7faff;
  }

  .el-icon {
    color: #2563eb;
  }

  .recent-title {
    flex: 1;
    font-size: 13px;
    color: #16233f;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .recent-time {
    font-size: 11.5px;
    color: #a9b6c9;
  }
}
</style>
