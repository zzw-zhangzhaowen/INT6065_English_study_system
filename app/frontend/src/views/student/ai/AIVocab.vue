<template>
  <div class="ai-vocab">
    <div class="wrap">
      <div class="head">
        <div>
          <div class="tech-section-title">词汇速记</div>
          <div class="sub">
            一张卡解决一个词：词性、释义、固定搭配、例句、词根线索、易混词。点卡片翻面，先自己想再看释义。
          </div>
        </div>
        <div class="head-actions">
          <el-input v-model="query" placeholder="查一个词" class="q-input" @keyup.enter="lookup">
            <template #append>
              <el-button @click="lookup"><el-icon><Search /></el-icon></el-button>
            </template>
          </el-input>
          <el-button plain :loading="loading" @click="load">
            <el-icon><Refresh /></el-icon> 换一批
          </el-button>
        </div>
      </div>

      <AiLoader v-if="loading" :steps="['抽取词卡…', '匹配搭配与例句…']" hint="正在生成速记卡" />

      <template v-else>
        <div class="card-grid">
          <div
            v-for="(c, i) in cards"
            :key="c.word + i"
            class="vcard"
            :class="{ flipped: flipped[i] }"
            @click="flip(i)"
          >
            <div class="vcard-inner">
              <div class="face front">
                <div class="word">{{ c.word }}</div>
                <div class="pos">{{ c.pos }}</div>
                <div class="hint-line">点击查看释义与搭配</div>
                <div class="corner">#{{ String(i + 1).padStart(2, '0') }}</div>
              </div>
              <div class="face back">
                <div class="b-word">{{ c.word }}</div>
                <div class="b-cn">{{ c.pos }} {{ c.cn }}</div>
                <div class="b-row">
                  <span class="b-label">搭配</span>{{ c.collocation }}
                </div>
                <div class="b-ex">{{ c.example }}</div>
                <div class="b-root">{{ c.root }}</div>
                <div v-if="confused(c.word)" class="b-conf">
                  <b>易混</b> {{ confused(c.word).note }}
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="foot-note">
          <el-icon><InfoFilled /></el-icon>
          内置离线词库仅收录约 40 个四级高频词。查不到的词我不会编造释义 —— 接上真 AI 后可直接查任意词。
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import AiLoader from '@/components/ai/AiLoader.vue'
import { ai } from '@/ai'
import { bumpUsage } from '@/ai/usage'
import { confusableOf } from '@/ai/corpus/vocab'

const cards = ref([])
const flipped = ref({})
const loading = ref(false)
const query = ref('')

const confused = w => confusableOf(w)

function flip(i) {
  flipped.value = { ...flipped.value, [i]: !flipped.value[i] }
}

async function load() {
  loading.value = true
  try {
    const res = await ai.vocabCards({ count: 8, seed: Date.now() })
    cards.value = res.data.cards || []
    flipped.value = {}
    bumpUsage('vocab', `${cards.value.length} 张词卡`)
  } catch (e) {
    ElMessage.error(e.message || '生成失败')
  } finally {
    loading.value = false
  }
}

async function lookup() {
  const w = query.value.trim()
  if (!w) return
  const res = await ai.vocabCards({ word: w })
  const list = res.data.cards || []
  if (res.data.matched) {
    cards.value = list
    flipped.value = { 0: true }
    ElMessage.success('已找到该词')
  } else {
    ElMessage.warning(`词库里没有「${w}」，已返回一批高频词卡`)
    cards.value = list
    flipped.value = {}
  }
  bumpUsage('vocab', w)
  query.value = ''
}

onMounted(load)
</script>

<style scoped lang="scss">
.ai-vocab {
  padding: 20px 24px 32px;
}

.wrap {
  max-width: 1180px;
  margin: 0 auto;
}

.head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;
  flex-wrap: wrap;
}

.sub {
  margin-top: 6px;
  font-size: 12.5px;
  color: #7c8ca8;
  line-height: 1.7;
  max-width: 620px;
}

.head-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.q-input {
  width: 200px;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(248px, 1fr));
  gap: 14px;
}

.vcard {
  perspective: 1200px;
  height: 218px;
  cursor: pointer;
}

.vcard-inner {
  position: relative;
  width: 100%;
  height: 100%;
  transition: transform 0.6s cubic-bezier(0.4, 0, 0.2, 1);
  transform-style: preserve-3d;
}

.vcard.flipped .vcard-inner {
  transform: rotateY(180deg);
}

.face {
  position: absolute;
  inset: 0;
  border-radius: 14px;
  backface-visibility: hidden;
  -webkit-backface-visibility: hidden;
  overflow: hidden;
}

.front {
  background: linear-gradient(150deg, #12305f, #1d4ed8 62%, #2b7fd4);
  color: #fff;
  padding: 20px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  box-shadow: 0 18px 40px -26px rgba(16, 58, 140, 0.9);

  &::after {
    content: '';
    position: absolute;
    width: 160px;
    height: 160px;
    right: -50px;
    bottom: -60px;
    border-radius: 50%;
    background: radial-gradient(circle, rgba(56, 189, 248, 0.45), transparent 66%);
  }

  .word {
    font-size: 24px;
    font-weight: 800;
    letter-spacing: -0.6px;
    font-family: 'Georgia', serif;
  }

  .pos {
    margin-top: 5px;
    font-size: 11.5px;
    color: rgba(214, 232, 255, 0.7);
    font-family: var(--tech-font-mono);
  }

  .hint-line {
    margin-top: 18px;
    font-size: 11px;
    color: rgba(214, 232, 255, 0.55);
  }

  .corner {
    position: absolute;
    right: 16px;
    top: 14px;
    font-size: 10.5px;
    color: rgba(214, 232, 255, 0.4);
    font-variant-numeric: tabular-nums;
  }
}

.back {
  background: #fff;
  border: 1px solid #e7edf8;
  transform: rotateY(180deg);
  padding: 15px 16px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 12px 30px -24px rgba(16, 38, 84, 0.6);
  overflow-y: auto;

  .b-word {
    font-family: 'Georgia', serif;
    font-size: 15px;
    font-weight: 800;
    color: #0e1a32;
  }

  .b-cn {
    margin-top: 3px;
    font-size: 12.5px;
    color: #1d4ed8;
    font-weight: 600;
  }

  .b-row {
    margin-top: 8px;
    font-size: 11.5px;
    color: #4e5f7c;
    line-height: 1.6;
  }

  .b-label {
    font-size: 10px;
    font-weight: 700;
    color: #0e9f6e;
    background: rgba(14, 159, 110, 0.1);
    padding: 1px 6px;
    border-radius: 4px;
    margin-right: 6px;
  }

  .b-ex {
    margin-top: 8px;
    padding: 7px 10px;
    border-radius: 7px;
    background: #f7faff;
    font-family: 'Georgia', serif;
    font-size: 11.5px;
    line-height: 1.65;
    color: #33415c;
  }

  .b-root {
    margin-top: 8px;
    font-size: 10.5px;
    color: #a9b6c9;
    line-height: 1.5;
  }

  .b-conf {
    margin-top: 7px;
    font-size: 10.5px;
    color: #92400e;
    background: #fffbeb;
    border-radius: 6px;
    padding: 5px 8px;
    line-height: 1.55;

    b {
      color: #b45309;
    }
  }
}

.foot-note {
  margin-top: 18px;
  display: flex;
  gap: 8px;
  align-items: flex-start;
  padding: 12px 15px;
  border-radius: 10px;
  background: rgba(37, 99, 235, 0.04);
  border: 1px solid rgba(37, 99, 235, 0.12);
  font-size: 11.5px;
  line-height: 1.7;
  color: #5b6f92;
}
</style>
