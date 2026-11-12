<template>
  <div class="ai-speaking">
    <div class="wrap">
      <div class="head">
        <div>
          <div class="tech-section-title">口语陪练</div>
          <div class="sub">
            先用文字打底：把想说的话写下来说顺，再开口练。评测分数基于文本长度与音素特征估算 —— 没有采集音频，我不会假装做了声学分析。
          </div>
        </div>
        <span class="ai-badge">文本估算模式</span>
      </div>

      <section class="input-card">
        <div class="topic-row">
          <span class="t-label">话题</span>
          <span
            v-for="t in topics"
            :key="t"
            class="t-chip"
            :class="{ active: topic === t }"
            @click="useTopic(t)"
          >{{ t }}</span>
          <el-button link type="primary" class="fill-btn" @click="fillSample">填入示例</el-button>
        </div>

        <el-input
          v-model="text"
          type="textarea"
          :rows="5"
          resize="vertical"
          placeholder="写下你要说的英文内容（建议 40 词以上）"
          class="f-body"
        />

        <div class="action-row">
          <div class="rec">
            <el-button disabled>
              <el-icon><Microphone /></el-icon> 录音评测
            </el-button>
            <span class="rec-tip">音频通道已预留（MediaRecorder + 语音接口），当前版本不采集音频</span>
          </div>
          <div class="right">
            <span class="wc">{{ wordCount }} 词</span>
            <el-button type="primary" :loading="loading" :disabled="!text.trim()" @click="evaluate">
              <el-icon><MagicStick /></el-icon> 生成评测
            </el-button>
          </div>
        </div>
      </section>

      <AiLoader v-if="loading" :steps="['统计语段长度…', '扫描音素特征…', '生成发音提示…']" hint="正在生成评测报告" />

      <template v-else-if="result">
        <section class="score-row">
          <div class="total-card ai-grad-card">
            <div class="tc-label">综合得分</div>
            <div class="tc-value">{{ result.total }}</div>
            <div class="tc-sub">{{ result.wordCount }} 词 · 约 {{ result.duration }} 秒</div>
          </div>
          <div class="sub-scores">
            <div v-for="s in subScores" :key="s.key" class="ss-item">
              <div class="ss-top">
                <span class="ss-name">{{ s.name }}</span>
                <span class="ss-val">{{ s.value }}</span>
              </div>
              <div class="ss-bar"><i :style="{ width: s.value + '%' }"></i></div>
            </div>
          </div>
        </section>

        <section class="two-col">
          <div class="card">
            <div class="card-title">
              <el-icon><Headset /></el-icon> 发音重点
            </div>
            <div v-for="(w, i) in result.wordTips" :key="i" class="tip-item">
              <span v-if="w.word" class="tip-word">{{ w.word }}</span>
              <div class="tip-text">{{ w.tip }}</div>
            </div>
          </div>

          <div class="card">
            <div class="card-title">
              <el-icon><Pointer /></el-icon> 下一步怎么练
            </div>
            <ul class="sug-list">
              <li v-for="(s, i) in result.suggestions" :key="i">{{ s }}</li>
            </ul>
            <div class="practice">
              <div class="p-title">10 分钟练习结构</div>
              <div class="p-row"><span>3 分钟</span>Shadowing 跟读（延迟 0.5 秒跟）</div>
              <div class="p-row"><span>3 分钟</span>录音回听，只改 1 个问题</div>
              <div class="p-row"><span>4 分钟</span>不看稿自述 1 分钟</div>
            </div>
          </div>
        </section>

        <div class="note">
          <el-icon><InfoFilled /></el-icon>
          {{ result.note }}
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import AiLoader from '@/components/ai/AiLoader.vue'
import { ai } from '@/ai'
import { bumpUsage } from '@/ai/usage'
import { words } from '@/ai/engine/text'

const topics = [
  'Describe your morning routine',
  'Explain why you chose your major',
  'Give your view on online learning',
  'Introduce a book that changed you'
]

// 每个话题配一段可直接练的示例答案，方便上手时先看结构
const SAMPLE_ANSWERS = {
  'Describe your morning routine': `I usually get up at half past six, which gives me enough time before the first class. I spend about twenty minutes reading something in English, usually a short article, and I write down three new words from it. After that I have breakfast with my roommates and we talk about our plans for the day. I have to admit that I am not a morning person, so I keep the routine very simple and I never check my phone before breakfast.`,
  'Explain why you chose your major': `I chose computer science mainly because I liked solving puzzles when I was younger, and programming felt like a natural extension of that. In high school I built a small website for our class, and the experience of seeing something work on screen was surprisingly satisfying. My parents were a little worried about the workload, but they supported me in the end. What I did not expect was how much writing and explaining the major involves, and that has turned out to be the part I enjoy most.`,
  'Give your view on online learning': `I think online learning works well as a supplement rather than a replacement. The obvious advantage is flexibility: I can pause a recorded lecture when a concept is difficult and go back to it later. However, without a fixed timetable I tend to postpone tasks, and the absence of discussion makes it harder to test whether I really understand something. My own solution is to keep two or three fixed study sessions each week and to discuss the material with a classmate afterwards.`,
  'Introduce a book that changed you': `The book that influenced me most is a collection of essays about how people make decisions. I read it in my first year, at a point when I was choosing between two very different paths. What stayed with me was the argument that most decisions are reversible, and that treating them as permanent makes people far too cautious. Since then I have been more willing to try things and correct them later. I still keep the book on my desk, mostly as a reminder rather than for the content itself.`
}

const topic = ref(topics[0])
const text = ref('')
const loading = ref(false)
const result = ref(null)

function fillSample() {
  text.value = SAMPLE_ANSWERS[topic.value] || ''
}

// 进入页面先填一段示例并评测一次，首屏就有可看的评分
onMounted(() => {
  fillSample()
  evaluate()
})

const wordCount = computed(() => words(text.value).length)

const subScores = computed(() => {
  if (!result.value) return []
  return [
    { key: 'accuracy', name: '准确度', value: result.value.accuracy },
    { key: 'fluency', name: '流利度', value: result.value.fluency },
    { key: 'intonation', name: '语调', value: result.value.intonation },
    { key: 'speed', name: '语速', value: result.value.speed }
  ]
})

function useTopic(t) {
  topic.value = t
  text.value = ''
}

async function evaluate() {
  loading.value = true
  try {
    const res = await ai.speaking({ text: text.value, topic: topic.value })
    result.value = res.data
    bumpUsage('speaking', topic.value)
  } catch (e) {
    ElMessage.error(e.message || '评测失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.ai-speaking {
  padding: 20px 24px 32px;
}

.wrap {
  max-width: 1080px;
  margin: 0 auto;
}

.head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 14px;
  margin-bottom: 16px;
}

.sub {
  margin-top: 6px;
  font-size: 12.5px;
  color: #7c8ca8;
  line-height: 1.7;
  max-width: 700px;
}

.input-card {
  background: #fff;
  border: 1px solid #e7edf8;
  border-radius: 16px;
  padding: 18px 20px;
  box-shadow: 0 14px 36px -28px rgba(16, 38, 84, 0.45);
  margin-bottom: 14px;

  :deep(.el-textarea__inner) {
    font-family: 'Georgia', serif;
    font-size: 13.5px;
    line-height: 1.85;
    border-radius: 12px;
  }
}

.topic-row {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 13px;
}

.t-label {
  font-size: 11.5px;
  color: #a9b6c9;
}

.fill-btn {
  margin-left: auto;
  font-size: 12px;
}

.t-chip {
  font-size: 11.5px;
  padding: 5px 11px;
  border-radius: 20px;
  border: 1px solid #dfe8f7;
  background: #fbfdff;
  color: #4e5f7c;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    border-color: rgba(37, 99, 235, 0.4);
  }

  &.active {
    color: #1d4ed8;
    background: rgba(37, 99, 235, 0.09);
    border-color: rgba(37, 99, 235, 0.35);
    font-weight: 600;
  }
}

.action-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  margin-top: 13px;
  flex-wrap: wrap;
}

.rec {
  display: flex;
  align-items: center;
  gap: 10px;
}

.rec-tip {
  font-size: 10.5px;
  color: #a9b6c9;
  max-width: 320px;
  line-height: 1.5;
}

.right {
  display: flex;
  align-items: center;
  gap: 12px;

  .wc {
    font-size: 11.5px;
    color: #8494ae;
  }
}

.score-row {
  display: grid;
  grid-template-columns: 240px 1fr;
  gap: 14px;
  margin-bottom: 14px;
}

@media (max-width: 900px) {
  .score-row {
    grid-template-columns: 1fr;
  }
}

.total-card {
  background: linear-gradient(150deg, #12305f, #1d4ed8 70%);
  border-radius: 16px;
  padding: 22px 24px;
  color: #fff;
  position: relative;
  overflow: hidden;

  &::after {
    content: '';
    position: absolute;
    width: 180px;
    height: 180px;
    right: -60px;
    bottom: -80px;
    border-radius: 50%;
    background: radial-gradient(circle, rgba(56, 189, 248, 0.4), transparent 66%);
  }

  .tc-label {
    font-size: 11.5px;
    color: rgba(214, 232, 255, 0.72);
  }

  .tc-value {
    font-size: 46px;
    font-weight: 850;
    letter-spacing: -2px;
    line-height: 1.1;
    margin-top: 4px;
    font-variant-numeric: tabular-nums;
  }

  .tc-sub {
    position: relative;
    margin-top: 8px;
    font-size: 11px;
    color: rgba(214, 232, 255, 0.62);
  }
}

.sub-scores {
  background: #fff;
  border: 1px solid #e7edf8;
  border-radius: 16px;
  padding: 18px 22px;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 14px 22px;

  .ss-top {
    display: flex;
    justify-content: space-between;
    font-size: 12px;
    margin-bottom: 6px;
  }

  .ss-name {
    color: #4e5f7c;
    font-weight: 600;
  }

  .ss-val {
    font-weight: 750;
    color: #0e1a32;
    font-variant-numeric: tabular-nums;
  }

  .ss-bar {
    height: 6px;
    border-radius: 4px;
    background: #eef3fb;
    overflow: hidden;

    i {
      display: block;
      height: 100%;
      border-radius: 4px;
      background: linear-gradient(90deg, #2563eb, #38bdf8);
    }
  }
}

.two-col {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}

@media (max-width: 900px) {
  .two-col {
    grid-template-columns: 1fr;
  }
}

.card {
  background: #fff;
  border: 1px solid #e7edf8;
  border-radius: 14px;
  padding: 17px 19px;
  box-shadow: 0 10px 30px -26px rgba(16, 38, 84, 0.4);
}

.card-title {
  display: flex;
  align-items: center;
  gap: 7px;
  font-size: 13px;
  font-weight: 700;
  color: #16233f;
  margin-bottom: 12px;

  .el-icon {
    color: #2563eb;
  }
}

.tip-item {
  padding: 10px 0;
  border-bottom: 1px dashed #eef2f9;

  &:last-child {
    border-bottom: none;
  }
}

.tip-word {
  display: inline-block;
  font-family: 'Georgia', serif;
  font-size: 12.5px;
  font-weight: 700;
  color: #1d4ed8;
  background: rgba(37, 99, 235, 0.08);
  padding: 1px 8px;
  border-radius: 5px;
  margin-bottom: 5px;
}

.tip-text {
  font-size: 12.5px;
  line-height: 1.75;
  color: #4e5f7c;
}

.sug-list {
  padding-left: 17px;
  margin: 0;

  li {
    font-size: 12.5px;
    line-height: 1.85;
    color: #4e5f7c;
    margin-bottom: 4px;
  }
}

.practice {
  margin-top: 14px;
  padding: 12px 14px;
  border-radius: 10px;
  background: #f7faff;
  border: 1px solid #eaf1fb;

  .p-title {
    font-size: 11.5px;
    font-weight: 700;
    color: #16233f;
    margin-bottom: 8px;
  }

  .p-row {
    font-size: 11.5px;
    color: #5b6f92;
    line-height: 1.9;

    span {
      display: inline-block;
      width: 52px;
      font-weight: 700;
      color: #2563eb;
      font-variant-numeric: tabular-nums;
    }
  }
}

.note {
  margin-top: 14px;
  display: flex;
  gap: 8px;
  align-items: flex-start;
  padding: 12px 15px;
  border-radius: 10px;
  background: rgba(217, 119, 6, 0.05);
  border: 1px solid rgba(217, 119, 6, 0.16);
  font-size: 11.5px;
  line-height: 1.7;
  color: #92400e;
}
</style>
