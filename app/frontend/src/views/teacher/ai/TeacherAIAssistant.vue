<template>
  <div class="teacher-ai">
    <div class="wrap">
      <!-- 头部 -->
      <div class="head">
        <div>
          <div class="tech-page-title">
            <span class="title-icon"><el-icon><DataAnalysis /></el-icon></span>
            AI 教学助手
            <span class="ai-badge">班级提交智能汇总</span>
          </div>
          <div class="tech-page-desc">
            后台每 {{ intervalMinutes }} 分钟静默分析一次学生提交，自动生成班级简报、共性问题、需关注名单与可直接用的讲评稿。
            分析在本机完成，不会把学生作文外传。
          </div>
        </div>
        <div class="head-actions">
          <div class="last-run">
            <span class="dot" :class="{ on: running }"></span>
            上次分析：{{ lastRunText }}
          </div>
          <el-button plain :loading="running" @click="analyze">
            <el-icon><Refresh /></el-icon> 立即分析
          </el-button>
        </div>
      </div>

      <!-- 作业选择 -->
      <section class="selector">
        <span class="s-label">分析范围</span>
        <el-checkbox-group v-model="selected" @change="onSelectChange">
          <el-checkbox v-for="hw in homeworks" :key="hw.id" :label="hw.id" :value="hw.id">
            {{ hw.title }}
            <span class="hw-count">{{ hw.submittedCount }}/{{ hw.totalCount }}</span>
          </el-checkbox>
        </el-checkbox-group>
      </section>

      <AiLoader
        v-if="loading && !digest"
        :steps="['读取提交记录…', '逐篇解析作文…', '聚合共性问题…', '生成讲评稿…']"
        hint="正在分析学生提交"
      />

      <template v-if="digest">
        <!-- 概览 -->
        <section class="stat-row">
          <div class="tech-stat">
            <div class="tech-stat-label">提交率</div>
            <div class="tech-stat-value">{{ digest.submitRate }}<small>%</small></div>
            <div class="tech-stat-trend">
              <span>{{ digest.submittedCount }} / {{ digest.classSize }} 人已提交</span>
            </div>
            <div class="tech-stat-bar"><i :style="{ width: digest.submitRate + '%' }"></i></div>
          </div>
          <div class="tech-stat">
            <div class="tech-stat-label">班级平均分</div>
            <div class="tech-stat-value">{{ digest.avgScore }}<small>分</small></div>
            <div class="tech-stat-trend">
              <span>最高 {{ digest.maxScore }} · 最低 {{ digest.minScore }}</span>
            </div>
            <div class="tech-stat-bar"><i :style="{ width: digest.avgScore + '%' }"></i></div>
          </div>
          <div class="tech-stat">
            <div class="tech-stat-label">及格率</div>
            <div class="tech-stat-value">{{ digest.passRate }}<small>%</small></div>
            <div class="tech-stat-trend">
              <span>按 72 分及格线统计</span>
            </div>
            <div class="tech-stat-bar"><i :style="{ width: digest.passRate + '%' }"></i></div>
          </div>
          <div class="tech-stat">
            <div class="tech-stat-label">需关注学生</div>
            <div class="tech-stat-value">{{ digest.attention.length }}<small>人</small></div>
            <div class="tech-stat-trend">
              <span>低分 / 篇幅不足 / 错误密度高</span>
            </div>
            <div class="tech-stat-bar"><i :style="{ width: Math.min(100, digest.attention.length * 12) + '%' }"></i></div>
          </div>
        </section>

        <!-- 总评 + 分布 -->
        <section class="two-col">
          <div class="panel summary">
            <div class="panel-head">
              <div class="tech-section-title">AI 班级简评</div>
              <span class="ai-badge">已分析 {{ digest.analyzedCount }} 篇</span>
            </div>
            <div class="summary-text">{{ digest.summary }}</div>
            <div class="dim-row">
              <div v-for="d in digest.dimAvg" :key="d.key" class="dim-item">
                <div class="dim-top">
                  <span>{{ d.name }}</span>
                  <b>{{ d.value }}</b>
                </div>
                <div class="dim-bar"><i :style="{ width: d.value + '%' }"></i></div>
              </div>
            </div>
          </div>

          <div class="panel">
            <div class="panel-head">
              <div class="tech-section-title">分数分布</div>
              <span class="muted">共 {{ digest.analyzedCount }} 篇</span>
            </div>
            <div class="dist">
              <div v-for="b in digest.distribution" :key="b.range" class="dist-row">
                <span class="dist-label">{{ b.range }}</span>
                <div class="dist-bar">
                  <i :style="{ width: pct(b.count) + '%' }"></i>
                </div>
                <span class="dist-count">{{ b.count }}</span>
              </div>
            </div>
            <div v-if="digest.trend.length" class="trend">
              <div class="trend-title">各次作业均分</div>
              <div class="trend-items">
                <div v-for="t in digest.trend" :key="t.name" class="trend-item">
                  <span class="t-name">{{ shortTitle(t.name) }}</span>
                  <span class="t-val">{{ t.value }}</span>
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- 共性问题 -->
        <section class="panel">
          <div class="panel-head">
            <div class="tech-section-title">共性问题 Top {{ digest.commonIssues.length }}</div>
            <span class="muted">按涉及学生数排序</span>
          </div>
          <div v-if="!digest.commonIssues.length" class="tech-empty">
            <div class="icon"><el-icon><CircleCheck /></el-icon></div>
            <div class="title">没有集中性问题</div>
            <div class="desc">这批作文的错误分布很分散，讲评可以改成优秀样本拆解。</div>
          </div>
          <div v-else class="issue-grid">
            <div v-for="(it, i) in digest.commonIssues" :key="i" class="issue-card">
              <div class="ic-head">
                <span class="ic-rank">{{ String(i + 1).padStart(2, '0') }}</span>
                <span class="ic-label">{{ it.label }}</span>
                <span class="ic-type">{{ it.typeName }}</span>
                <span class="ic-count">{{ it.studentCount }} 人 / {{ it.count }} 处</span>
              </div>
              <div class="ic-bar">
                <i :style="{ width: Math.min(100, (it.count / Math.max(1, digest.analyzedCount)) * 100 * 2.2) + '%' }"></i>
              </div>
              <div v-if="it.examples && it.examples.length" class="ic-examples">
                <div v-for="(ex, ei) in it.examples.slice(0, 2)" :key="ei" class="ic-ex">
                  <span class="ex-stu">{{ ex.student }}</span>
                  <span class="ex-bad">{{ ex.original }}</span>
                  <el-icon class="ex-arrow"><Right /></el-icon>
                  <span class="ex-good">{{ ex.suggestion }}</span>
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- 关注 + 优秀 -->
        <section class="two-col">
          <div class="panel">
            <div class="panel-head">
              <div class="tech-section-title">需要关注</div>
              <span class="muted">{{ digest.attention.length }} 人</span>
            </div>
            <div v-if="!digest.attention.length" class="empty-line">本次没有明显掉队的学生</div>
            <div v-for="a in digest.attention" :key="a.studentId" class="att-item">
              <div class="att-left">
                <div class="att-avatar" :class="a.level">{{ a.studentName.charAt(0) }}</div>
                <div>
                  <div class="att-name">{{ a.studentName }}</div>
                  <div class="att-score">{{ a.score }} 分</div>
                </div>
              </div>
              <div class="att-reasons">
                <span v-for="(r, i) in a.reasons" :key="i" class="att-tag">{{ r }}</span>
              </div>
            </div>
          </div>

          <div class="panel">
            <div class="panel-head">
              <div class="tech-section-title">可做范例的作文</div>
              <span class="muted">{{ digest.excellent.length }} 篇</span>
            </div>
            <div v-if="!digest.excellent.length" class="empty-line">本次没有 85 分以上的作文</div>
            <div v-for="e in digest.excellent" :key="e.studentId" class="ex-item">
              <div class="ex-top">
                <span class="ex-name">{{ e.studentName }}</span>
                <span class="ex-score">{{ e.score }}</span>
              </div>
              <div class="ex-desc">{{ e.strength }}</div>
            </div>
          </div>
        </section>

        <!-- 单篇预批改 -->
        <section class="panel">
          <div class="panel-head">
            <div class="tech-section-title">单篇 AI 预批改</div>
            <span class="muted">老师只需确认，不用从零看</span>
          </div>
          <div class="pre-row">
            <el-select v-model="preStudent" placeholder="选择学生" class="pre-select" filterable>
              <el-option
                v-for="s in submissionOptions"
                :key="s.id"
                :label="`${s.studentName} · ${s.wordCount} 词`"
                :value="s.id"
              />
            </el-select>
            <el-button type="primary" :loading="preLoading" :disabled="!preStudent" @click="runPreGrade">
              <el-icon><MagicStick /></el-icon> 生成预批改
            </el-button>
            <span class="pre-tip">建议分只做参考，最终分数由老师确认</span>
          </div>

          <template v-if="preResult">
            <div class="pre-result">
              <div class="pre-score">
                <div class="ps-label">建议分</div>
                <div class="ps-value">{{ preResult.suggestedScore }}</div>
                <div class="ps-band">{{ preResult.band }}</div>
              </div>
              <div class="pre-body">
                <div class="pre-dims">
                  <div v-for="d in preResult.analysis.dimensions" :key="d.key" class="pd-item">
                    <span>{{ d.name }}</span>
                    <b>{{ d.score }}</b>
                  </div>
                </div>
                <div class="pre-review">
                  <div class="pr-title">需人工确认</div>
                  <div v-for="(r, i) in preResult.needsReview" :key="i" class="pr-item">
                    <el-icon><Warning /></el-icon>{{ r }}
                  </div>
                </div>
                <div class="pre-actions">
                  <div v-for="(a, i) in preResult.teacherActions" :key="i" class="pa-item">{{ a }}</div>
                </div>
              </div>
            </div>

            <div class="pre-sentences">
              <div class="ps-head">
                <span>逐句标注（可直接复制到评语）</span>
                <el-button link type="primary" @click="copySentences">
                  <el-icon><DocumentCopy /></el-icon> 复制
                </el-button>
              </div>
              <div v-for="s in preResult.analysis.sentences.filter(x => x.issues.length).slice(0, 6)" :key="s.index" class="ps-item">
                <div class="ps-text">{{ s.text }}</div>
                <div v-for="(it, ii) in s.issues" :key="ii" class="ps-fix">
                  <span class="pf-type">{{ it.typeName }}</span>
                  <span class="pf-bad">{{ it.original }}</span>
                  <el-icon class="pf-arrow"><Right /></el-icon>
                  <span class="pf-good">{{ it.suggestion }}</span>
                </div>
              </div>
            </div>
          </template>
        </section>

        <!-- 讲评稿 -->
        <section class="panel">
          <div class="panel-head">
            <div class="tech-section-title">一键生成讲评稿</div>
            <el-button link type="primary" @click="copyScript">
              <el-icon><DocumentCopy /></el-icon> 复制讲评稿
            </el-button>
          </div>
          <div class="script">
            <MarkdownView :content="digest.lectureScript" />
          </div>
        </section>
      </template>

      <div v-else-if="!loading" class="tech-empty">
        <div class="icon"><el-icon><DataAnalysis /></el-icon></div>
        <div class="title">还没有分析结果</div>
        <div class="desc">点击右上角「立即分析」，或等待后台 Agent 自动完成。</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import AiLoader from '@/components/ai/AiLoader.vue'
import MarkdownView from '@/components/ai/MarkdownView.vue'
import { ai } from '@/ai'
import { AI_CONFIG } from '@/ai/config'
import { runDigest, readCache, timeAgo } from '@/ai/agent/digestAgent'
import { HOMEWORKS, SUBMISSIONS } from '@/mock/db'

const intervalMinutes = AI_CONFIG.teacher.digestIntervalMinutes

const homeworks = ref(HOMEWORKS.filter(h => h.homeworkType === 'WRITING'))
const selected = ref([homeworks.value[0]?.id || 501])
const digest = ref(null)
const loading = ref(false)
const running = ref(false)
const lastAt = ref(0)

const preStudent = ref(null)
const preResult = ref(null)
const preLoading = ref(false)

let timer = null

const lastRunText = computed(() => (lastAt.value ? timeAgo(lastAt.value) : '尚未分析'))

const submissionOptions = computed(() => {
  const hwId = selected.value[0] || 501
  const list = SUBMISSIONS[hwId] || []
  return list.slice(0, 30).map(s => ({
    id: s.id,
    studentName: s.studentName,
    wordCount: s.content.split(/\s+/).length,
    content: s.content
  }))
})

const pct = count => {
  const max = Math.max(...(digest.value?.distribution || []).map(x => x.count), 1)
  return Math.round((count / max) * 100)
}

const shortTitle = t => (t.length > 12 ? t.slice(0, 12) + '…' : t)

function onSelectChange() {
  preResult.value = null
  preStudent.value = null
  analyze()
}

async function analyze() {
  if (!selected.value.length) {
    ElMessage.warning('至少选择一个作业')
    return
  }
  running.value = true
  loading.value = true
  try {
    const d = await runDigest(selected.value)
    digest.value = d
    lastAt.value = d.generatedAt
  } catch (e) {
    ElMessage.error(e.message || '分析失败')
  } finally {
    running.value = false
    loading.value = false
  }
}

async function runPreGrade() {
  const sub = submissionOptions.value.find(s => s.id === preStudent.value)
  if (!sub) return
  preLoading.value = true
  try {
    const res = await ai.teacherPreGrade({
      content: sub.content,
      title: homeworks.value.find(h => h.id === selected.value[0])?.title || '',
      level: 'CET4',
      studentName: sub.studentName
    })
    preResult.value = res.data
  } catch (e) {
    ElMessage.error(e.message || '预批改失败')
  } finally {
    preLoading.value = false
  }
}

async function copyScript() {
  try {
    await navigator.clipboard.writeText(digest.value.lectureScript)
    ElMessage.success('讲评稿已复制')
  } catch (e) {
    ElMessage.warning('浏览器不允许自动复制')
  }
}

async function copySentences() {
  const lines = []
  for (const s of preResult.value.analysis.sentences) {
    if (!s.issues.length) continue
    lines.push(s.text)
    s.issues.forEach(it => lines.push(`  ✗ ${it.original} → ${it.suggestion}（${it.typeName}）`))
    lines.push('')
  }
  try {
    await navigator.clipboard.writeText(lines.join('\n'))
    ElMessage.success('批注已复制')
  } catch (e) {
    ElMessage.warning('浏览器不允许自动复制')
  }
}

onMounted(() => {
  const cached = readCache()
  if (cached?.digest) {
    digest.value = cached.digest
    lastAt.value = cached.at
    if (Array.isArray(cached.homeworkIds) && cached.homeworkIds.length) {
      selected.value = cached.homeworkIds
    }
  }
  if (AI_CONFIG.teacher.autoRun || !digest.value) {
    analyze()
  }

  // 后台定时 Agent：到点静默刷新，页面数字自动更新
  timer = setInterval(async () => {
    if (running.value) return
    try {
      const d = await runDigest(selected.value)
      digest.value = d
      lastAt.value = d.generatedAt
    } catch (e) { /* 静默失败，不打扰老师 */ }
  }, intervalMinutes * 60 * 1000)
})

onBeforeUnmount(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped lang="scss">
.teacher-ai {
  padding: 20px 24px 40px;
}

.wrap {
  max-width: 1320px;
  margin: 0 auto;
}

.head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.title-icon {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, #2563eb, #38bdf8);
  color: #fff;
  font-size: 17px;
  box-shadow: 0 8px 18px -10px rgba(37, 99, 235, 0.9);
}

.head-actions {
  display: flex;
  align-items: center;
  gap: 14px;
}

.last-run {
  display: flex;
  align-items: center;
  gap: 7px;
  font-size: 11.5px;
  color: #8494ae;

  .dot {
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background: #cbd5e1;

    &.on {
      background: #0e9f6e;
      box-shadow: 0 0 8px rgba(14, 159, 110, 0.8);
      animation: pulse 1.4s infinite;
    }
  }
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.35; }
}

/* 选择器 */
.selector {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 12px 18px;
  background: #fff;
  border: 1px solid #e7edf8;
  border-radius: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;

  .s-label {
    font-size: 12px;
    font-weight: 600;
    color: #4e5f7c;
  }

  :deep(.el-checkbox) {
    margin-right: 14px;
  }

  :deep(.el-checkbox__label) {
    font-size: 12.5px;
  }
}

.hw-count {
  font-size: 10.5px;
  color: #a9b6c9;
  margin-left: 5px;
}

/* 统计 */
.stat-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin-bottom: 16px;
}

@media (max-width: 1100px) {
  .stat-row {
    grid-template-columns: repeat(2, 1fr);
  }
}

/* 面板 */
.two-col {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
  margin-bottom: 16px;
}

@media (max-width: 1100px) {
  .two-col {
    grid-template-columns: 1fr;
  }
}

.panel {
  background: #fff;
  border: 1px solid #e7edf8;
  border-radius: 14px;
  padding: 18px 20px;
  box-shadow: 0 12px 32px -28px rgba(16, 38, 84, 0.45);
  margin-bottom: 16px;
}

.panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;

  :deep(.tech-section-title) {
    margin-bottom: 0;
  }
}

.muted {
  font-size: 11px;
  color: #a9b6c9;
}

/* 简评 */
.summary-text {
  font-size: 13px;
  line-height: 1.85;
  color: #4e5f7c;
  padding: 12px 15px;
  border-radius: 10px;
  background: rgba(37, 99, 235, 0.04);
  border-left: 3px solid #2563eb;
}

.dim-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px 20px;
  margin-top: 14px;
}

.dim-item {
  .dim-top {
    display: flex;
    justify-content: space-between;
    font-size: 12px;
    color: #5b6f92;
    margin-bottom: 5px;

    b {
      color: #0e1a32;
      font-variant-numeric: tabular-nums;
    }
  }

  .dim-bar {
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

/* 分布 */
.dist-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.dist-label {
  width: 62px;
  flex: none;
  font-size: 11.5px;
  color: #5b6f92;
  font-variant-numeric: tabular-nums;
}

.dist-bar {
  flex: 1;
  height: 16px;
  border-radius: 4px;
  background: #f2f6fc;
  overflow: hidden;

  i {
    display: block;
    height: 100%;
    border-radius: 4px;
    background: linear-gradient(90deg, #2563eb, #38bdf8);
    transition: width 0.6s ease;
  }
}

.dist-count {
  width: 24px;
  text-align: right;
  font-size: 11.5px;
  font-weight: 700;
  color: #0e1a32;
  font-variant-numeric: tabular-nums;
}

.trend {
  margin-top: 14px;
  padding-top: 12px;
  border-top: 1px dashed #eef2f9;
}

.trend-title {
  font-size: 11.5px;
  color: #8494ae;
  margin-bottom: 8px;
}

.trend-items {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.trend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 10px;
  border-radius: 6px;
  background: #f7faff;
  border: 1px solid #eaf1fb;
  font-size: 11px;
  color: #5b6f92;

  .t-val {
    font-weight: 700;
    color: #1d4ed8;
  }
}

/* 共性问题 */
.issue-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 12px;
}

.issue-card {
  border: 1px solid #eef2f9;
  border-radius: 12px;
  padding: 14px 15px;
  background: #fdfeff;
}

.ic-head {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 9px;
}

.ic-rank {
  font-size: 11px;
  font-weight: 800;
  color: #b9c9e2;
  font-variant-numeric: tabular-nums;
}

.ic-label {
  font-size: 13px;
  font-weight: 700;
  color: #16233f;
}

.ic-type {
  font-size: 10px;
  padding: 1px 6px;
  border-radius: 4px;
  background: #f2f6fc;
  color: #5b6f92;
}

.ic-count {
  margin-left: auto;
  font-size: 10.5px;
  color: #d97706;
  background: #fef3c7;
  padding: 2px 7px;
  border-radius: 4px;
  font-weight: 600;
}

.ic-bar {
  height: 4px;
  border-radius: 2px;
  background: #eef3fb;
  overflow: hidden;
  margin-bottom: 10px;

  i {
    display: block;
    height: 100%;
    background: linear-gradient(90deg, #f59e0b, #fbbf24);
  }
}

.ic-ex {
  display: flex;
  align-items: center;
  gap: 7px;
  font-size: 11.5px;
  flex-wrap: wrap;
  padding: 5px 0;
  border-top: 1px dashed #f2f6fc;

  &:first-child {
    border-top: none;
  }
}

.ex-stu {
  font-size: 10px;
  color: #a9b6c9;
  background: #f4f7fc;
  padding: 1px 6px;
  border-radius: 4px;
}

.ex-bad {
  color: #b91c1c;
  text-decoration: line-through;
  font-family: 'Georgia', serif;
}

.ex-arrow {
  color: #cbd5e1;
  font-size: 10px;
}

.ex-good {
  color: #0e9f6e;
  font-weight: 600;
  font-family: 'Georgia', serif;
}

/* 关注 */
.att-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 10px 0;
  border-bottom: 1px dashed #eef2f9;
  flex-wrap: wrap;

  &:last-child {
    border-bottom: none;
  }
}

.att-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.att-avatar {
  width: 32px;
  height: 32px;
  border-radius: 9px;
  display: grid;
  place-items: center;
  font-size: 13px;
  font-weight: 700;
  color: #fff;
  background: #94a3b8;

  &.high {
    background: linear-gradient(135deg, #dc2626, #f87171);
  }

  &.mid {
    background: linear-gradient(135deg, #d97706, #fbbf24);
  }
}

.att-name {
  font-size: 13px;
  font-weight: 600;
  color: #16233f;
}

.att-score {
  font-size: 10.5px;
  color: #a9b6c9;
}

.att-reasons {
  display: flex;
  gap: 5px;
  flex-wrap: wrap;
  justify-content: flex-end;
  max-width: 58%;
}

.att-tag {
  font-size: 10.5px;
  padding: 2px 7px;
  border-radius: 4px;
  background: #fdf2f2;
  color: #b91c1c;
  border: 1px solid #fbe0e0;
}

/* 优秀 */
.ex-item {
  padding: 11px 0;
  border-bottom: 1px dashed #eef2f9;

  &:last-child {
    border-bottom: none;
  }
}

.ex-top {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.ex-name {
  font-size: 13px;
  font-weight: 700;
  color: #16233f;
}

.ex-score {
  font-size: 11px;
  font-weight: 700;
  color: #0e9f6e;
  background: rgba(14, 159, 110, 0.1);
  padding: 1px 7px;
  border-radius: 4px;
}

.ex-desc {
  font-size: 12px;
  line-height: 1.7;
  color: #6b7a99;
}

.empty-line {
  font-size: 12px;
  color: #a9b6c9;
  padding: 14px 0;
}

/* 预批改 */
.pre-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 14px;
}

.pre-select {
  width: 260px;
}

.pre-tip {
  font-size: 11px;
  color: #a9b6c9;
}

.pre-result {
  display: flex;
  gap: 18px;
  padding: 16px 18px;
  border-radius: 12px;
  background: linear-gradient(135deg, rgba(37, 99, 235, 0.05), rgba(56, 189, 248, 0.04));
  border: 1px solid rgba(37, 99, 235, 0.14);
  margin-bottom: 14px;
}

.pre-score {
  flex: none;
  text-align: center;
  padding-right: 18px;
  border-right: 1px dashed rgba(37, 99, 235, 0.2);

  .ps-label {
    font-size: 11px;
    color: #8494ae;
  }

  .ps-value {
    font-size: 34px;
    font-weight: 850;
    color: #1d4ed8;
    letter-spacing: -1.4px;
    line-height: 1.2;
    font-variant-numeric: tabular-nums;
  }

  .ps-band {
    font-size: 11px;
    font-weight: 700;
    color: #0e9f6e;
  }
}

.pre-body {
  flex: 1;
  min-width: 0;
}

.pre-dims {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  margin-bottom: 10px;

  .pd-item {
    font-size: 11.5px;
    color: #5b6f92;

    b {
      color: #16233f;
      margin-left: 5px;
      font-variant-numeric: tabular-nums;
    }
  }
}

.pr-title {
  font-size: 11.5px;
  font-weight: 700;
  color: #b45309;
  margin-bottom: 5px;
}

.pr-item {
  display: flex;
  align-items: flex-start;
  gap: 6px;
  font-size: 12px;
  line-height: 1.7;
  color: #7c5a12;

  .el-icon {
    margin-top: 3px;
    font-size: 12px;
  }
}

.pre-actions {
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px dashed rgba(37, 99, 235, 0.16);
}

.pa-item {
  font-size: 12px;
  line-height: 1.8;
  color: #33415c;
}

.pre-sentences {
  border: 1px solid #eef2f9;
  border-radius: 12px;
  overflow: hidden;
}

.ps-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 15px;
  background: #f7faff;
  border-bottom: 1px solid #eef2f9;
  font-size: 11.5px;
  color: #5b6f92;
}

.ps-item {
  padding: 11px 15px;
  border-bottom: 1px dashed #f2f6fc;

  &:last-child {
    border-bottom: none;
  }
}

.ps-text {
  font-family: 'Georgia', serif;
  font-size: 12.5px;
  line-height: 1.75;
  color: #33415c;
  margin-bottom: 5px;
}

.ps-fix {
  display: flex;
  align-items: center;
  gap: 7px;
  font-size: 11.5px;
  padding: 2px 0;
  flex-wrap: wrap;
}

.pf-type {
  font-size: 10px;
  padding: 1px 6px;
  border-radius: 4px;
  background: #f2f6fc;
  color: #5b6f92;
}

.pf-bad {
  color: #b91c1c;
  text-decoration: line-through;
  font-family: 'Georgia', serif;
}

.pf-arrow {
  color: #cbd5e1;
  font-size: 10px;
}

.pf-good {
  color: #0e9f6e;
  font-weight: 600;
  font-family: 'Georgia', serif;
}

/* 讲评稿 */
.script {
  padding: 16px 20px;
  border-radius: 12px;
  background: #fcfdff;
  border: 1px solid #eef2f9;
  max-height: 520px;
  overflow-y: auto;
}
</style>
