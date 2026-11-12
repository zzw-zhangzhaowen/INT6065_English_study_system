<template>
  <div class="ai-wrong">
    <div class="wrap">
      <div class="head">
        <div>
          <div class="tech-section-title">错题溯源</div>
          <div class="sub">
            不只告诉你正确答案，还告诉你错在哪一步：是规则没记住、题干限定没看见，还是被干扰项带跑。
          </div>
        </div>
        <el-button link @click="refresh">
          <el-icon><Refresh /></el-icon> 换一道
        </el-button>
      </div>

      <div class="layout">
        <!-- 左侧题目列表 -->
        <aside class="list">
          <div
            v-for="(q, i) in questions"
            :key="q.id"
            class="list-item"
            :class="{ active: current && current.id === q.id }"
            @click="select(q)"
          >
            <div class="li-top">
              <span class="li-no">{{ String(i + 1).padStart(2, '0') }}</span>
              <span class="li-know">{{ q.knowledge }}</span>
            </div>
            <div class="li-text">{{ q.question }}</div>
            <div class="li-meta">{{ q.createTime.slice(5, 16) }}</div>
          </div>
        </aside>

        <!-- 右侧详情 -->
        <section class="detail">
          <template v-if="current">
            <div class="q-block">
              <div class="q-know">
                <span class="ai-badge">{{ current.knowledge }}</span>
                <span class="q-time">{{ current.createTime }}</span>
              </div>
              <div class="q-text">{{ current.question }}</div>
              <div class="q-options">
                <div
                  v-for="(opt, i) in current.options"
                  :key="i"
                  class="opt"
                  :class="{
                    correct: letter(i) === current.answer,
                    wrong: letter(i) === current.studentAnswer && current.studentAnswer !== current.answer
                  }"
                >
                  <span class="opt-letter">{{ letter(i) }}</span>
                  <span class="opt-text">{{ opt }}</span>
                  <span v-if="letter(i) === current.answer" class="opt-tag ok">正确答案</span>
                  <span
                    v-else-if="letter(i) === current.studentAnswer"
                    class="opt-tag no"
                  >你的选择</span>
                </div>
              </div>
            </div>

            <AiLoader v-if="loading" :steps="['定位知识点…', '归类错因…', '生成同类题…']" hint="正在解析这道题的考察意图" />

            <template v-else-if="explain">
              <!-- 错因 -->
              <div class="card cause">
                <div class="card-title">
                  <el-icon><Aim /></el-icon>
                  错因定位
                </div>
                <div class="cause-type">{{ explain.reasonType }}</div>
                <div class="cause-detail">{{ explain.reasonDetail }}</div>
                <div v-if="explain.compare" class="compare">
                  <div class="cmp-item">
                    <span class="cmp-label no">你选了</span>
                    <span class="cmp-val">{{ explain.compare.chosen }}</span>
                  </div>
                  <el-icon class="cmp-arrow"><Right /></el-icon>
                  <div class="cmp-item">
                    <span class="cmp-label ok">正确是</span>
                    <span class="cmp-val">{{ explain.compare.correct }}</span>
                  </div>
                </div>
                <div v-if="explain.compare" class="cause-note">{{ explain.compare.note }}</div>
              </div>

              <!-- 知识点卡 -->
              <div v-if="explain.knowledgeCard" class="card knowledge ai-grad-card">
                <div class="card-title">
                  <el-icon><Notebook /></el-icon>
                  知识点卡：{{ explain.knowledgeCard.name }}
                </div>
                <div class="k-def">{{ explain.knowledgeCard.definition }}</div>
                <ul class="k-rules">
                  <li v-for="(r, i) in explain.knowledgeCard.rules" :key="i">{{ r }}</li>
                </ul>
                <div class="k-example">
                  <div class="k-en">{{ explain.knowledgeCard.example.en }}</div>
                  <div class="k-cn">{{ explain.knowledgeCard.example.cn }}</div>
                </div>
              </div>

              <!-- 同类题 -->
              <div v-if="explain.similar.length" class="card similar">
                <div class="card-title">
                  <el-icon><Files /></el-icon>
                  同类题（先自己做，再看解析）
                </div>
                <div v-for="(s, si) in explain.similar" :key="si" class="sim-item">
                  <div class="sim-q">{{ si + 1 }}. {{ s.q }}</div>
                  <div class="sim-opts">
                    <span v-for="(o, oi) in s.options" :key="oi" class="sim-opt">{{ letter(oi) }}. {{ o }}</span>
                  </div>
                  <el-button link type="primary" @click="toggleSim(si)">
                    {{ openSim === si ? '收起解析' : '查看解析' }}
                  </el-button>
                  <div v-if="openSim === si" class="sim-ans">
                    <div class="sim-key">答案：{{ s.answer }}</div>
                    <div class="sim-exp">{{ s.explain }}</div>
                  </div>
                </div>
              </div>

              <div class="next-action">
                <el-icon><Pointer /></el-icon>
                {{ explain.nextAction }}
              </div>
            </template>
          </template>

          <div v-else class="tech-empty">
            <div class="icon"><el-icon><Warning /></el-icon></div>
            <div class="title">选择左侧一道错题</div>
            <div class="desc">我会把错因落到具体语法点上，并配两道同类题。</div>
          </div>
        </section>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import AiLoader from '@/components/ai/AiLoader.vue'
import { ai } from '@/ai'
import { bumpUsage } from '@/ai/usage'
import { WRONG_QUESTIONS } from '@/mock/db'

const questions = ref(WRONG_QUESTIONS)
const current = ref(null)
const explain = ref(null)
const loading = ref(false)
const openSim = ref(-1)

const letter = i => String.fromCharCode(65 + i)

function toggleSim(i) {
  openSim.value = openSim.value === i ? -1 : i
}

async function select(q) {
  current.value = q
  explain.value = null
  openSim.value = -1
  loading.value = true
  try {
    const idx = ch => (ch ? ch.charCodeAt(0) - 65 : -1)
    const ansText = q.options[idx(q.answer)] || ''
    const mineText = q.options[idx(q.studentAnswer)] || ''
    const res = await ai.explainWrong({
      question: q.question,
      knowledge: q.knowledge,
      correctAnswer: `${q.answer}. ${ansText}`,
      studentAnswer: `${q.studentAnswer}. ${mineText}`
    })
    explain.value = res.data
    bumpUsage('wrong', q.knowledge)
  } catch (e) {
    explain.value = {
      reasonType: '解析失败',
      reasonDetail: e.message || '请稍后重试',
      similar: []
    }
  } finally {
    loading.value = false
  }
}

function refresh() {
  const idx = Math.floor(Math.random() * questions.value.length)
  select(questions.value[idx])
}

onMounted(() => {
  if (questions.value.length) select(questions.value[0])
})
</script>

<style scoped lang="scss">
.ai-wrong {
  padding: 20px 24px 32px;
}

.wrap {
  max-width: 1240px;
  margin: 0 auto;
}

.head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 16px;
}

.sub {
  margin-top: 6px;
  font-size: 12.5px;
  color: #7c8ca8;
  line-height: 1.7;
}

.layout {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 14px;
}

@media (max-width: 1000px) {
  .layout {
    grid-template-columns: 1fr;
  }
}

/* 列表 */
.list {
  background: #fff;
  border: 1px solid #e7edf8;
  border-radius: 14px;
  padding: 8px;
  height: fit-content;
  box-shadow: 0 10px 30px -26px rgba(16, 38, 84, 0.4);
}

.list-item {
  padding: 11px 12px;
  border-radius: 10px;
  cursor: pointer;
  transition: background 0.2s;
  margin-bottom: 4px;

  &:hover {
    background: #f7faff;
  }

  &.active {
    background: linear-gradient(90deg, rgba(37, 99, 235, 0.1), rgba(56, 189, 248, 0.05));
    box-shadow: inset 0 0 0 1px rgba(37, 99, 235, 0.18);
  }
}

.li-top {
  display: flex;
  align-items: center;
  gap: 7px;
  margin-bottom: 5px;
}

.li-no {
  font-size: 10px;
  font-weight: 800;
  color: #b9c9e2;
  font-variant-numeric: tabular-nums;
}

.li-know {
  font-size: 10.5px;
  padding: 1px 7px;
  border-radius: 4px;
  background: rgba(37, 99, 235, 0.08);
  color: #1d4ed8;
  font-weight: 600;
}

.li-text {
  font-size: 12.5px;
  line-height: 1.6;
  color: #33415c;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.li-meta {
  margin-top: 5px;
  font-size: 10.5px;
  color: #b3c0d4;
}

/* 详情 */
.detail {
  min-width: 0;
}

.q-block,
.card {
  background: #fff;
  border: 1px solid #e7edf8;
  border-radius: 14px;
  padding: 18px 20px;
  margin-bottom: 14px;
  box-shadow: 0 10px 30px -26px rgba(16, 38, 84, 0.4);
}

.q-know {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.q-time {
  font-size: 11px;
  color: #b3c0d4;
}

.q-text {
  font-size: 14.5px;
  line-height: 1.8;
  color: #16233f;
  font-weight: 600;
  margin-bottom: 14px;
}

.opt {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 9px 12px;
  border-radius: 9px;
  border: 1px solid #eef2f9;
  margin-bottom: 7px;
  font-size: 13px;
  color: #4e5f7c;
  transition: all 0.2s;

  &.correct {
    border-color: rgba(14, 159, 110, 0.4);
    background: rgba(14, 159, 110, 0.06);
    color: #0f5132;
  }

  &.wrong {
    border-color: rgba(220, 38, 38, 0.35);
    background: rgba(220, 38, 38, 0.05);
    color: #7f1d1d;
    text-decoration: line-through;
  }
}

.opt-letter {
  width: 20px;
  height: 20px;
  border-radius: 5px;
  display: grid;
  place-items: center;
  font-size: 11px;
  font-weight: 700;
  background: #f2f6fc;
  color: #5b6f92;
  flex: none;
}

.opt-tag {
  margin-left: auto;
  font-size: 10px;
  font-weight: 700;
  padding: 1px 7px;
  border-radius: 4px;

  &.ok {
    color: #0e9f6e;
    background: rgba(14, 159, 110, 0.12);
  }

  &.no {
    color: #dc2626;
    background: rgba(220, 38, 38, 0.1);
  }
}

.card-title {
  display: flex;
  align-items: center;
  gap: 7px;
  font-size: 13.5px;
  font-weight: 700;
  color: #16233f;
  margin-bottom: 12px;

  .el-icon {
    color: #2563eb;
  }
}

.cause-type {
  display: inline-block;
  font-size: 11.5px;
  font-weight: 700;
  color: #b45309;
  background: #fef3c7;
  padding: 3px 10px;
  border-radius: 5px;
  margin-bottom: 9px;
}

.cause-detail {
  font-size: 13px;
  line-height: 1.8;
  color: #4e5f7c;
}

.compare {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 14px;
  padding: 12px 14px;
  border-radius: 10px;
  background: #fbfdff;
  border: 1px dashed #e0e9f7;
}

.cmp-item {
  display: flex;
  align-items: center;
  gap: 7px;
}

.cmp-label {
  font-size: 10.5px;
  font-weight: 700;
  padding: 1px 7px;
  border-radius: 4px;

  &.no {
    color: #b91c1c;
    background: #fee2e2;
  }

  &.ok {
    color: #0e9f6e;
    background: #d1fae5;
  }
}

.cmp-val {
  font-size: 13px;
  font-weight: 650;
  color: #16233f;
}

.cmp-arrow {
  color: #b9c6dc;
}

.cause-note {
  margin-top: 10px;
  font-size: 12px;
  line-height: 1.7;
  color: #8494ae;
}

.k-def {
  font-size: 13px;
  line-height: 1.8;
  color: #4e5f7c;
  margin-bottom: 10px;
}

.k-rules {
  padding-left: 18px;
  margin: 0 0 12px;

  li {
    font-size: 12.5px;
    line-height: 1.85;
    color: #5b6f92;
  }
}

.k-example {
  padding: 11px 14px;
  border-radius: 10px;
  background: rgba(37, 99, 235, 0.04);
  border-left: 3px solid #2563eb;
}

.k-en {
  font-family: 'Georgia', serif;
  font-size: 13px;
  color: #16233f;
  line-height: 1.7;
}

.k-cn {
  margin-top: 4px;
  font-size: 12px;
  color: #8494ae;
}

.sim-item {
  padding: 13px 0;
  border-bottom: 1px dashed #eef2f9;

  &:last-child {
    border-bottom: none;
  }
}

.sim-q {
  font-size: 13px;
  line-height: 1.7;
  color: #16233f;
  margin-bottom: 7px;
}

.sim-opts {
  display: flex;
  gap: 14px;
  flex-wrap: wrap;
  margin-bottom: 6px;
}

.sim-opt {
  font-size: 12.5px;
  color: #5b6f92;
}

.sim-ans {
  margin-top: 6px;
  padding: 10px 13px;
  border-radius: 9px;
  background: #f2fbf6;
  border: 1px solid #d6f0e0;
}

.sim-key {
  font-size: 12.5px;
  font-weight: 700;
  color: #0e9f6e;
  margin-bottom: 4px;
}

.sim-exp {
  font-size: 12px;
  line-height: 1.75;
  color: #33624a;
}

.next-action {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  border-radius: 12px;
  background: linear-gradient(135deg, rgba(37, 99, 235, 0.06), rgba(56, 189, 248, 0.05));
  border: 1px solid rgba(37, 99, 235, 0.14);
  font-size: 12.5px;
  color: #1d4ed8;
  line-height: 1.7;
}
</style>
