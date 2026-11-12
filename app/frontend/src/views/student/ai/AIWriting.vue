<template>
  <div class="ai-writing">
    <div class="wrap">
      <!-- 输入区 -->
      <section class="input-card">
        <div class="input-head">
          <div>
            <div class="tech-section-title">作文批改</div>
            <div class="input-sub">
              逐句标错 + 四维评分 + 原句升级对照。分数由字数、段落、连接词、错误密度等指标算出，每条评分依据都可追溯。
            </div>
          </div>
          <div class="input-actions">
            <el-button link @click="fillSample">填入示例</el-button>
            <el-button link @click="clearAll" :disabled="!text">清空</el-button>
          </div>
        </div>

        <div class="form-row">
          <el-input v-model="title" placeholder="作文题目（用于判断是否切题）" class="f-title" />
          <el-select v-model="level" class="f-level">
            <el-option label="CET-4（要求 120 词）" value="CET4" />
            <el-option label="CET-6（要求 150 词）" value="CET6" />
            <el-option label="专四（要求 200 词）" value="TEM4" />
            <el-option label="雅思（要求 250 词）" value="IELTS" />
          </el-select>
        </div>

        <el-input
          v-model="text"
          type="textarea"
          :rows="8"
          resize="vertical"
          :placeholder="placeholderText"
          class="f-body"
        />

        <div class="submit-bar">
          <div class="counter">
            <span :class="{ warn: wordCount < requirement }">{{ wordCount }}</span>
            / {{ requirement }} 词
            <span class="dot">·</span>
            {{ paragraphCount }} 段
            <span class="dot">·</span>
            {{ sentenceCount }} 句
            <span v-if="!canSubmit" class="need-more">
              还需 {{ MIN_WORDS - wordCount }} 词 / {{ MIN_WORDS - wordCount }} more words
            </span>
          </div>
          <el-button type="primary" :loading="loading" :disabled="!canSubmit" @click="analyze">
            <el-icon><MagicStick /></el-icon>
            开始批改
          </el-button>
        </div>
      </section>

      <AiLoader
        v-if="loading"
        class="loader"
        :steps="['分句与分词…', '扫描 34 条错误规则…', '计算四维评分…', '生成逐句改写建议…']"
        hint="分析在本机完成，不会上传你的作文"
      />

      <!-- 结果 -->
      <template v-if="result && !loading">
        <!-- 总评 -->
        <section class="summary-row">
          <div class="score-card ai-grad-card">
            <div class="score-ring">
              <div class="score-value">
                {{ result.scores.total }}<span>/100</span>
              </div>
            </div>
            <div class="score-meta">
              <div class="band">{{ result.band }}</div>
              <div class="summary-text">{{ result.summary }}</div>
              <div class="score-tags">
                <span>字数 {{ result.wordCount }}</span>
                <span>段落 {{ result.paragraphCount }}</span>
                <span>句数 {{ result.sentenceCount }}</span>
                <span>错误 {{ result.issueCount }}</span>
              </div>
            </div>
          </div>

          <div class="dim-card">
            <div class="dim-head">
              <div class="tech-section-title">四维得分</div>
              <el-button link type="primary" @click="activeTab = 'basis'">看依据</el-button>
            </div>
            <div v-for="d in result.dimensions" :key="d.key" class="dim-item">
              <div class="dim-top">
                <span class="dim-name">{{ d.name }}<small>{{ d.weight }}</small></span>
                <span class="dim-val">{{ d.score }}</span>
              </div>
              <div class="dim-bar"><i :style="{ width: d.score + '%', background: barColor(d.score) }"></i></div>
            </div>
          </div>

          <div class="radar-card">
            <div class="tech-section-title">能力分布</div>
            <RadarChart :data="radarShort" :size="196" />
          </div>
        </section>

        <!-- 详细 -->
        <section class="detail-card">
          <el-tabs v-model="activeTab">
            <el-tab-pane name="sentence">
              <template #label>
                逐句批改
                <span class="tab-badge">{{ result.issueCount }}</span>
              </template>
              <div class="legend-row">
                <span class="lg grammar">语法</span>
                <span class="lg word">用词</span>
                <span class="lg collocation">搭配</span>
                <span class="lg chinglish">中式表达</span>
                <span class="lg punct">标点</span>
                <span class="lg style">表达建议</span>
              </div>

              <div v-if="!result.issueCount" class="tech-empty">
                <div class="icon"><el-icon><CircleCheck /></el-icon></div>
                <div class="title">未检出语言硬错</div>
                <div class="desc">这篇在语法与搭配上是干净的。可以看「升级对照」把表达再抬一档。</div>
              </div>

              <div v-for="s in annotatedSentences" :key="s.index" class="sent-item">
                <div class="sent-index">{{ s.index + 1 }}</div>
                <div class="sent-body">
                  <div class="sent-text">
                    <template v-for="(seg, si) in s.segments" :key="si">
                      <el-tooltip
                        v-if="seg.issue"
                        placement="top"
                        :content="seg.issue.suggestion + ' — ' + seg.issue.explain"
                        :show-after="120"
                      >
                        <span class="hl" :class="seg.issue.type">{{ seg.text }}</span>
                      </el-tooltip>
                      <span v-else>{{ seg.text }}</span>
                    </template>
                  </div>
                  <div v-if="s.issues.length" class="sent-fixes">
                    <div v-for="(it, ii) in s.issues" :key="ii" class="fix-row">
                      <span class="fix-type" :class="it.type">{{ it.typeName }}</span>
                      <span class="fix-del">{{ it.original }}</span>
                      <el-icon class="fix-arrow"><Right /></el-icon>
                      <span class="fix-add">{{ it.suggestion }}</span>
                      <span class="fix-explain">{{ it.explain }}</span>
                    </div>
                  </div>
                  <div v-else class="sent-ok">本句未检出问题</div>
                </div>
              </div>
            </el-tab-pane>

            <el-tab-pane name="upgrade">
              <template #label>
                升级对照
                <span class="tab-badge">{{ result.upgradePairs.length }}</span>
              </template>
              <div v-if="!result.upgradePairs.length" class="tech-empty">
                <div class="icon"><el-icon><Top /></el-icon></div>
                <div class="title">没有可升级的句子</div>
                <div class="desc">说明句子本身没有需要改的地方，或者问题集中在标点这类格式层面。</div>
              </div>
              <div v-for="p in result.upgradePairs" :key="p.index" class="pair-item">
                <div class="pair-head">
                  <span class="pair-no">第 {{ p.index }} 句</span>
                  <span v-for="t in p.types" :key="t" class="pair-type">{{ t }}</span>
                </div>
                <div class="pair-line before">
                  <span class="tag">原句</span>{{ p.original }}
                </div>
                <div class="pair-line after">
                  <span class="tag">改写</span>{{ p.upgraded }}
                </div>
              </div>

              <div v-if="result.upgrades.length" class="upgrade-hint">
                <div class="tech-section-title">可替换的基础表达</div>
                <div class="up-chips">
                  <span v-for="u in result.upgrades" :key="u.word" class="up-chip">
                    {{ u.word }} → <b>{{ u.to }}</b>
                    <i>×{{ u.count }}</i>
                  </span>
                </div>
              </div>
            </el-tab-pane>

            <el-tab-pane name="checklist">
              <template #label>
                改进清单
                <span class="tab-badge">{{ result.checklist.length }}</span>
              </template>
              <div class="check-list">
                <div v-for="(c, i) in result.checklist" :key="i" class="check-item">
                  <div class="check-pri" :class="priClass(c.priority)">{{ c.priority }}</div>
                  <div class="check-body">
                    <div class="check-title">{{ c.title }}</div>
                    <div class="check-detail">{{ c.detail }}</div>
                  </div>
                </div>
              </div>

              <div class="highlight-block">
                <div class="tech-section-title">写得好的地方</div>
                <ul>
                  <li v-for="(h, i) in result.highlights" :key="i">{{ h }}</li>
                </ul>
              </div>
            </el-tab-pane>

            <el-tab-pane name="basis">
              <template #label>评分依据</template>
              <div class="basis-list">
                <div v-for="d in result.dimensions" :key="d.key" class="basis-item">
                  <div class="basis-head">
                    <span class="basis-name">{{ d.name }}</span>
                    <span class="basis-score">{{ d.score }} 分</span>
                    <span class="basis-weight">权重 {{ d.weight }}</span>
                  </div>
                  <ul>
                    <li v-for="(b, i) in d.basis" :key="i">{{ b }}</li>
                  </ul>
                </div>
              </div>
              <div class="basis-note">
                以上每一项都由本次文本的实际统计值算出（字数、段落数、连接词数量、错误条数、词形多样性等），
                换一篇文本指标会变，分数也会变 —— 不是固定模板分。
              </div>
            </el-tab-pane>

            <el-tab-pane name="corrected">
              <template #label>修正稿</template>
              <div class="corrected-box">
                <div class="corrected-head">
                  <span>已按规则修复语法、用词、搭配与格式问题</span>
                  <el-button link type="primary" @click="copyCorrected">
                    <el-icon><DocumentCopy /></el-icon> 复制
                  </el-button>
                </div>
                <div class="corrected-text">{{ result.correctedContent }}</div>
              </div>
              <div class="basis-note">
                修正稿只做「低风险替换」，不改变你的原意与结构。想拿高分需要重写论证，那部分建议对照「改进清单」自己动笔。
              </div>
            </el-tab-pane>
          </el-tabs>
        </section>
      </template>

      <!-- 空态 -->
      <div v-else-if="!loading" class="tech-empty empty-wrap">
        <div class="icon"><el-icon><EditPen /></el-icon></div>
        <div class="title">还没有批改记录</div>
        <div class="desc">
          贴一篇作文进去，或者点右上角「填入示例」看一份带典型错误的样本分析结果。
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'
import RadarChart from '@/components/ai/RadarChart.vue'
import AiLoader from '@/components/ai/AiLoader.vue'
import { ai } from '@/ai'
import { bumpUsage } from '@/ai/usage'
import { textStats } from '@/ai/engine/text'

const title = ref('Online Learning: Benefits and Challenges')
const level = ref('CET4')
const text = ref('')
const loading = ref(false)
const result = ref(null)
const activeTab = ref('sentence')

const requirement = computed(() => ({ CET4: 120, CET6: 150, TEM4: 200, IELTS: 250 }[level.value] || 120))

// 硬性最低词数：低于此值不允许提交批改（防止空稿/灌水稿）
const MIN_WORDS = 100

const stats = computed(() => textStats(text.value))
const wordCount = computed(() => stats.value.wordCount)
const paragraphCount = computed(() => stats.value.paragraphCount)
const sentenceCount = computed(() => stats.value.sentenceCount)

// 达到最低词数才可提交
const canSubmit = computed(() => wordCount.value >= MIN_WORDS)

// 空输入时在输入框内直接说明规则（中英双语）
const placeholderText = computed(() =>
  `请粘贴或输入你的英文作文，不少于 ${MIN_WORDS} 词后可开始批改。\n` +
  `Paste or type your essay here — at least ${MIN_WORDS} words to start grading.`
)

const SAMPLE = `Nowadays, more and more student choose to study abroad. I think it is very good for young people. people is easy to find a good job when they have overseas experience, and they can also learn a lot of new thing from different culture. But some student can not afford the cost, so they have to work part time in restaurant. They must spend much time on their study and their work at the same time, which is very tired for them. In my opinion, i think the government should give them informations and advices. with the development of society, this problem will be solve by itself. Although it is difficult, but we should try our best to help them.`

const sampleTitles = [
  'Online Learning: Benefits and Challenges',
  'Should Students Take Part-time Jobs?',
  'Reading in the Digital Age'
]

function fillSample() {
  text.value = SAMPLE
  title.value = sampleTitles[Math.floor(Math.random() * sampleTitles.length)]
  ElMessage.success('已填入一份带典型错误的示例作文')
}

function clearAll() {
  text.value = ''
  result.value = null
}

function barColor(score) {
  if (score >= 85) return 'linear-gradient(90deg,#0e9f6e,#34d399)'
  if (score >= 72) return 'linear-gradient(90deg,#2563eb,#38bdf8)'
  if (score >= 60) return 'linear-gradient(90deg,#d97706,#fbbf24)'
  return 'linear-gradient(90deg,#dc2626,#f87171)'
}

function priClass(p) {
  return { 高: 'p-high', 中: 'p-mid', 低: 'p-low' }[p] || 'p-low'
}

/** 把句子切成「普通文本 / 问题片段」的片段序列 */
function buildSegments(sentence, issues) {
  const marks = []
  for (const it of issues) {
    const i = sentence.indexOf(it.original)
    if (i >= 0) marks.push({ ...it, at: i, len: it.original.length })
  }
  marks.sort((a, b) => a.at - b.at)

  const segs = []
  let cursor = 0
  for (const m of marks) {
    if (m.at < cursor) continue
    if (m.at > cursor) segs.push({ text: sentence.slice(cursor, m.at), issue: null })
    segs.push({ text: sentence.slice(m.at, m.at + m.len), issue: m })
    cursor = m.at + m.len
  }
  if (cursor < sentence.length) segs.push({ text: sentence.slice(cursor), issue: null })
  return segs.length ? segs : [{ text: sentence, issue: null }]
}

const annotatedSentences = computed(() => {
  if (!result.value) return []
  return result.value.sentences.map(s => ({
    ...s,
    segments: buildSegments(s.text, s.issues)
  }))
})

const RADAR_SHORT = ['内容', '结构', '语言', '词汇']
const radarShort = computed(() =>
  (result.value?.radar || []).map((r, i) => ({ ...r, name: RADAR_SHORT[i] || r.name }))
)

async function analyze() {
  if (!text.value.trim()) return
  // 硬性词数门槛：不足 100 词直接拦下
  if (wordCount.value < MIN_WORDS) {
    ElMessage.warning(`作文不少于 ${MIN_WORDS} 词才能批改，当前 ${wordCount.value} 词，还需 ${MIN_WORDS - wordCount.value} 词`)
    return
  }
  loading.value = true
  result.value = null
  try {
    const res = await ai.correctWriting({
      content: text.value,
      title: title.value,
      level: level.value
    })
    result.value = res.data
    activeTab.value = 'sentence'
    bumpUsage('writing', title.value || '未命名作文')
  } catch (e) {
    ElMessage.error(e.message || '批改失败')
  } finally {
    loading.value = false
  }
}

async function copyCorrected() {
  try {
    await navigator.clipboard.writeText(result.value.correctedContent)
    ElMessage.success('修正稿已复制')
  } catch (e) {
    ElMessage.warning('浏览器不允许自动复制，请手动选择文本')
  }
}
</script>

<style scoped lang="scss">
.ai-writing {
  padding: 20px 24px 32px;
}

.wrap {
  max-width: 1240px;
  margin: 0 auto;
}

/* ---------- 输入 ---------- */
.input-card {
  background: #fff;
  border: 1px solid #e7edf8;
  border-radius: 16px;
  padding: 20px 22px;
  box-shadow: 0 14px 36px -26px rgba(16, 38, 84, 0.45);
}

.input-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 14px;
}

.input-sub {
  margin-top: 6px;
  font-size: 12.5px;
  line-height: 1.7;
  color: #7c8ca8;
  max-width: 720px;
}

.input-actions {
  display: flex;
  gap: 4px;
  flex: none;
}

.form-row {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}

.f-title {
  flex: 1;
}

.f-level {
  width: 210px;
  flex: none;
}

.f-body {
  :deep(.el-textarea__inner) {
    font-size: 13.5px;
    line-height: 1.85;
    border-radius: 12px;
    font-family: 'Georgia', 'Times New Roman', serif;
  }
}

.submit-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 12px;
}

.counter {
  font-size: 12px;
  color: #8494ae;

  span {
    font-weight: 700;
    color: #0e1a32;
    font-variant-numeric: tabular-nums;

    &.warn {
      color: #d97706;
    }

    &.need-more {
      margin-left: 8px;
      padding: 1px 8px;
      border-radius: 10px;
      font-weight: 600;
      color: #b45309;
      background: rgba(251, 191, 36, 0.16);
    }
  }

  .dot {
    margin: 0 6px;
    color: #cfd9e8;
  }
}

.loader {
  margin-top: 16px;
}

/* ---------- 总评 ---------- */
.summary-row {
  display: grid;
  grid-template-columns: 1.2fr 1.1fr 0.8fr;
  gap: 14px;
  margin-top: 16px;
}

@media (max-width: 1180px) {
  .summary-row {
    grid-template-columns: 1fr 1fr;
  }
}

.score-card {
  display: flex;
  gap: 20px;
  align-items: center;
  padding: 20px 22px;
  background: #fff;
  border-radius: 16px;
  border: 1px solid #e7edf8;
}

.score-ring {
  flex: none;
  width: 104px;
  height: 104px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  background:
    radial-gradient(circle at center, #fff 58%, transparent 59%),
    conic-gradient(#2563eb 0deg, #38bdf8 240deg, #e8effb 240deg);
  border: 1px solid #e7edf8;

  .score-value {
    font-size: 30px;
    font-weight: 850;
    color: #0e1a32;
    letter-spacing: -1.4px;
    line-height: 1;
    font-variant-numeric: tabular-nums;

    span {
      font-size: 10px;
      font-weight: 600;
      color: #a9b6c9;
      letter-spacing: 0;
      margin-left: 2px;
    }
  }
}

.score-meta {
  flex: 1;
  min-width: 0;

  .band {
    display: inline-block;
    font-size: 11px;
    font-weight: 700;
    padding: 2px 9px;
    border-radius: 20px;
    color: #1d4ed8;
    background: rgba(37, 99, 235, 0.1);
    margin-bottom: 8px;
  }

  .summary-text {
    font-size: 12.5px;
    line-height: 1.75;
    color: #4e5f7c;
  }

  .score-tags {
    margin-top: 10px;
    display: flex;
    gap: 6px;
    flex-wrap: wrap;

    span {
      font-size: 10.5px;
      padding: 2px 8px;
      border-radius: 5px;
      background: #f4f8ff;
      color: #5b6f92;
      border: 1px solid #e8effb;
    }
  }
}

.dim-card,
.radar-card {
  background: #fff;
  border: 1px solid #e7edf8;
  border-radius: 16px;
  padding: 18px 20px;
  box-shadow: 0 10px 30px -24px rgba(16, 38, 84, 0.4);
}

.dim-head {
  display: flex;
  align-items: center;
  justify-content: space-between;

  :deep(.tech-section-title) {
    margin-bottom: 0;
  }
}

.dim-item {
  margin-top: 11px;
}

.dim-top {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  margin-bottom: 5px;

  .dim-name {
    color: #4e5f7c;
    font-weight: 600;

    small {
      color: #b3c0d4;
      margin-left: 5px;
      font-weight: 500;
    }
  }

  .dim-val {
    font-weight: 750;
    color: #0e1a32;
    font-variant-numeric: tabular-nums;
  }
}

.dim-bar {
  height: 6px;
  border-radius: 4px;
  background: #eef3fb;
  overflow: hidden;

  i {
    display: block;
    height: 100%;
    border-radius: 4px;
    transition: width 0.7s cubic-bezier(0.22, 1, 0.36, 1);
  }
}

.radar-card {
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* ---------- 详情 ---------- */
.detail-card {
  margin-top: 16px;
  background: #fff;
  border: 1px solid #e7edf8;
  border-radius: 16px;
  padding: 8px 22px 22px;
  box-shadow: 0 14px 36px -28px rgba(16, 38, 84, 0.45);
}

.tab-badge {
  display: inline-block;
  margin-left: 5px;
  min-width: 16px;
  height: 16px;
  line-height: 16px;
  padding: 0 5px;
  border-radius: 8px;
  font-size: 10px;
  font-weight: 700;
  color: #fff;
  background: #2563eb;
  text-align: center;
}

.legend-row {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 14px;
}

.lg {
  font-size: 10.5px;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 600;

  &.grammar { color: #b91c1c; background: rgba(220, 38, 38, 0.09); }
  &.word { color: #b45309; background: rgba(217, 119, 6, 0.1); }
  &.collocation { color: #7c3aed; background: rgba(139, 92, 246, 0.1); }
  &.chinglish { color: #be185d; background: rgba(219, 39, 119, 0.09); }
  &.punct { color: #0369a1; background: rgba(14, 165, 233, 0.1); }
  &.style { color: #475569; background: rgba(100, 116, 139, 0.1); }
}

/* 句子 */
.sent-item {
  display: flex;
  gap: 12px;
  padding: 13px 0;
  border-bottom: 1px dashed #eef2f9;

  &:last-child {
    border-bottom: none;
  }
}

.sent-index {
  flex: none;
  width: 22px;
  height: 22px;
  border-radius: 6px;
  display: grid;
  place-items: center;
  font-size: 10.5px;
  font-weight: 700;
  color: #8494ae;
  background: #f2f6fc;
  margin-top: 1px;
}

.sent-body {
  flex: 1;
  min-width: 0;
}

.sent-text {
  font-size: 13.5px;
  line-height: 1.85;
  color: #33415c;
  font-family: 'Georgia', 'Times New Roman', serif;
}

.hl {
  cursor: help;
  border-radius: 3px;
  padding: 0 2px;
  border-bottom: 1.5px solid;

  &.grammar { background: rgba(220, 38, 38, 0.1); border-color: #dc2626; }
  &.word { background: rgba(217, 119, 6, 0.12); border-color: #d97706; }
  &.collocation { background: rgba(139, 92, 246, 0.12); border-color: #8b5cf6; }
  &.chinglish { background: rgba(219, 39, 119, 0.1); border-color: #db2777; }
  &.punct { background: rgba(14, 165, 233, 0.12); border-color: #0ea5e9; }
  &.style { background: rgba(100, 116, 139, 0.12); border-color: #64748b; }
}

.sent-fixes {
  margin-top: 9px;
}

.fix-row {
  display: flex;
  align-items: baseline;
  gap: 7px;
  font-size: 12px;
  line-height: 1.7;
  flex-wrap: wrap;
  padding: 4px 0;
}

.fix-type {
  font-size: 10px;
  font-weight: 700;
  padding: 1px 6px;
  border-radius: 4px;
  background: #f2f6fc;
  color: #5b6f92;
}

.fix-del {
  color: #dc2626;
  text-decoration: line-through;
  font-family: 'Georgia', serif;
}

.fix-arrow {
  color: #b9c6dc;
  font-size: 11px;
}

.fix-add {
  color: #0e9f6e;
  font-weight: 650;
  font-family: 'Georgia', serif;
}

.fix-explain {
  color: #8494ae;
  font-size: 11.5px;
  flex-basis: 100%;
  padding-left: 2px;
}

.sent-ok {
  margin-top: 5px;
  font-size: 11.5px;
  color: #a9b6c9;
}

/* 对照 */
.pair-item {
  padding: 14px 0;
  border-bottom: 1px dashed #eef2f9;

  &:last-child {
    border-bottom: none;
  }
}

.pair-head {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 8px;
}

.pair-no {
  font-size: 11.5px;
  font-weight: 700;
  color: #4e5f7c;
}

.pair-type {
  font-size: 10px;
  padding: 1px 6px;
  border-radius: 4px;
  background: #f2f6fc;
  color: #5b6f92;
}

.pair-line {
  font-size: 13px;
  line-height: 1.8;
  padding: 9px 12px;
  border-radius: 9px;
  margin-bottom: 7px;
  font-family: 'Georgia', serif;

  .tag {
    font-family: var(--tech-font);
    font-size: 10px;
    font-weight: 700;
    padding: 1px 6px;
    border-radius: 4px;
    margin-right: 9px;
    vertical-align: 1px;
  }

  &.before {
    background: #fdf5f5;
    border: 1px solid #f8dede;
    color: #7f1d1d;

    .tag { background: #fee2e2; color: #b91c1c; }
  }

  &.after {
    background: #f2fbf6;
    border: 1px solid #d6f0e0;
    color: #14532d;

    .tag { background: #d1fae5; color: #0e9f6e; }
  }
}

.upgrade-hint {
  margin-top: 16px;
}

.up-chips {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.up-chip {
  font-size: 11.5px;
  padding: 4px 10px;
  border-radius: 6px;
  background: #f7faff;
  border: 1px solid #e8effb;
  color: #5b6f92;

  b { color: #1d4ed8; }

  i {
    color: #b3c0d4;
    font-style: normal;
    margin-left: 4px;
  }
}

/* 清单 */
.check-list {
  margin-top: 4px;
}

.check-item {
  display: flex;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px dashed #eef2f9;
}

.check-pri {
  flex: none;
  width: 30px;
  height: 22px;
  border-radius: 5px;
  display: grid;
  place-items: center;
  font-size: 10.5px;
  font-weight: 800;
  margin-top: 1px;

  &.p-high { color: #b91c1c; background: #fee2e2; }
  &.p-mid { color: #b45309; background: #fef3c7; }
  &.p-low { color: #1d4ed8; background: #e0ecff; }
}

.check-title {
  font-size: 13px;
  font-weight: 700;
  color: #16233f;
  margin-bottom: 3px;
}

.check-detail {
  font-size: 12.5px;
  line-height: 1.75;
  color: #6b7a99;
}

.highlight-block {
  margin-top: 18px;

  ul {
    padding-left: 18px;
    margin: 0;
  }

  li {
    font-size: 12.5px;
    line-height: 1.9;
    color: #4e5f7c;
  }
}

/* 依据 */
.basis-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 12px;
}

.basis-item {
  padding: 14px 16px;
  border-radius: 12px;
  background: #fbfdff;
  border: 1px solid #e7edf8;

  .basis-head {
    display: flex;
    align-items: baseline;
    gap: 8px;
    margin-bottom: 8px;
  }

  .basis-name {
    font-size: 13px;
    font-weight: 700;
    color: #16233f;
  }

  .basis-score {
    font-size: 15px;
    font-weight: 800;
    color: #1d4ed8;
    font-variant-numeric: tabular-nums;
  }

  .basis-weight {
    font-size: 10.5px;
    color: #a9b6c9;
    margin-left: auto;
  }

  ul {
    padding-left: 16px;
    margin: 0;
  }

  li {
    font-size: 12px;
    line-height: 1.8;
    color: #5b6f92;
  }
}

.basis-note {
  margin-top: 14px;
  padding: 11px 14px;
  border-radius: 10px;
  background: rgba(37, 99, 235, 0.04);
  border: 1px solid rgba(37, 99, 235, 0.12);
  font-size: 11.5px;
  line-height: 1.7;
  color: #5b6f92;
}

/* 修正稿 */
.corrected-box {
  border: 1px solid #e7edf8;
  border-radius: 12px;
  overflow: hidden;
}

.corrected-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 14px;
  background: #f7faff;
  border-bottom: 1px solid #e7edf8;
  font-size: 11.5px;
  color: #5b6f92;
}

.corrected-text {
  padding: 14px 16px;
  font-size: 13.5px;
  line-height: 2;
  color: #33415c;
  font-family: 'Georgia', 'Times New Roman', serif;
  white-space: pre-wrap;
}

.empty-wrap {
  margin-top: 20px;
  background: #fff;
  border: 1px solid #e7edf8;
  border-radius: 16px;
}
</style>
