<template>
  <div class="ai-sentence">
    <div class="wrap">
      <div class="head">
        <div>
          <div class="tech-section-title">长难句拆解</div>
          <div class="sub">
            三刀切开复合句：① 圈出所有谓语，数量 = 分句数 ② 找主句主谓宾 ③ 其余成分挂到被修饰的部分上。
          </div>
        </div>
      </div>

      <section class="input-card">
        <el-input
          v-model="text"
          type="textarea"
          :rows="3"
          resize="none"
          placeholder="粘贴一句读不懂的英文长句"
          class="f-body"
        />
        <div class="row">
          <div class="samples">
            <span class="s-label">示例：</span>
            <span v-for="(s, i) in samples" :key="i" class="s-chip" @click="useSample(s)">
              例句 {{ i + 1 }}
            </span>
          </div>
          <el-button type="primary" :loading="loading" :disabled="!text.trim()" @click="analyze">
            <el-icon><MagicStick /></el-icon> 开始拆解
          </el-button>
        </div>
      </section>

      <AiLoader v-if="loading" :steps="['识别谓语动词…', '定位主句主干…', '挂接修饰成分…']" hint="正在做句法分析" />

      <template v-else-if="result">
        <div class="sent-display">
          <div class="sd-label">原句</div>
          <div class="sd-text">{{ result.text }}</div>
          <div class="sd-meta">
            {{ result.wordCount || result.text.split(/\s+/).length }} 词
            <span v-if="result.clauseCount">· {{ result.clauseCount }} 个分句</span>
            <span v-if="result.matched" class="matched">· 已收录例句，含完整译文</span>
            <span v-else class="unmatched">· 该句未收录，仅做结构分析</span>
          </div>
        </div>

        <section class="two-col">
          <div class="card">
            <div class="card-title">
              <el-icon><Aim /></el-icon> 主干
            </div>
            <div class="backbone">{{ result.backbone }}</div>
            <div v-if="result.subject" class="bb-row">
              <span class="bb-label">主语</span>{{ result.subject }}
            </div>
            <div v-if="result.predicate" class="bb-row">
              <span class="bb-label">谓语</span>{{ result.predicate }}
            </div>
            <div class="bb-tip">
              阅读时先只看主干，能读懂 60% 的意思；剩下的修饰是补充信息，不影响判断。
            </div>
          </div>

          <div class="card">
            <div class="card-title">
              <el-icon><Connection /></el-icon> 修饰成分
            </div>
            <div v-if="!result.modifiers.length" class="none">未识别到明显修饰结构</div>
            <div v-for="(m, i) in result.modifiers" :key="i" class="mod-item">
              <div class="mod-role">{{ m.role }}</div>
              <div v-if="m.part && m.part !== '—'" class="mod-part">{{ m.part }}</div>
              <div class="mod-attach">{{ m.attach }}</div>
            </div>
          </div>
        </section>

        <section v-if="result.translation" class="card trans">
          <div class="card-title">
            <el-icon><ChatLineSquare /></el-icon> 参考译文
          </div>
          <div class="trans-text">{{ result.translation }}</div>
        </section>

        <div class="tip-card">
          <el-icon><Pointer /></el-icon>
          <div>{{ result.tips }}</div>
        </div>
      </template>

      <div v-else-if="!loading" class="tech-empty empty">
        <div class="icon"><el-icon><Reading /></el-icon></div>
        <div class="title">贴一句读不懂的句子</div>
        <div class="desc">也可以点上面的「例句 1/2/3」看完整拆解示例（这三句带参考译文）。</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import AiLoader from '@/components/ai/AiLoader.vue'
import { ai } from '@/ai'
import { bumpUsage } from '@/ai/usage'

const samples = [
  'The assumption that online learning inevitably lowers academic standards, which many educators still hold, has been challenged by a growing body of research.',
  'What makes the reform controversial is not the goal itself but the speed at which it is being implemented.',
  'Students who are encouraged to explain their reasoning to peers tend to retain information longer than those who simply review notes.'
]

const text = ref('')
const loading = ref(false)
const result = ref(null)

function useSample(s) {
  text.value = s
}

// 进入页面先拆一句示例，首屏就有可看的完整结果
onMounted(() => {
  text.value = samples[0]
  analyze()
})

async function analyze() {
  loading.value = true
  try {
    const res = await ai.longSentence({ text: text.value })
    result.value = res.data
    bumpUsage('longSentence', '长难句拆解')
  } catch (e) {
    ElMessage.error(e.message || '拆解失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.ai-sentence {
  padding: 20px 24px 32px;
}

.wrap {
  max-width: 1080px;
  margin: 0 auto;
}

.head {
  margin-bottom: 16px;
}

.sub {
  margin-top: 6px;
  font-size: 12.5px;
  color: #7c8ca8;
  line-height: 1.7;
}

.input-card {
  background: #fff;
  border: 1px solid #e7edf8;
  border-radius: 16px;
  padding: 18px 20px;
  box-shadow: 0 14px 36px -28px rgba(16, 38, 84, 0.45);
  margin-bottom: 14px;

  :deep(.el-textarea__inner) {
    font-family: 'Georgia', 'Times New Roman', serif;
    font-size: 14px;
    line-height: 1.85;
    border-radius: 12px;
  }
}

.row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 12px;
  flex-wrap: wrap;
}

.samples {
  display: flex;
  align-items: center;
  gap: 7px;
  flex-wrap: wrap;
}

.s-label {
  font-size: 11.5px;
  color: #a9b6c9;
}

.s-chip {
  font-size: 11.5px;
  padding: 4px 10px;
  border-radius: 20px;
  border: 1px solid #dfe8f7;
  background: #fbfdff;
  color: #4e5f7c;
  cursor: pointer;

  &:hover {
    border-color: rgba(37, 99, 235, 0.45);
    color: #1d4ed8;
    background: #f5f9ff;
  }
}

.sent-display {
  background: linear-gradient(135deg, #0f2a63, #1d4ed8);
  border-radius: 16px;
  padding: 20px 22px;
  margin-bottom: 14px;
  position: relative;
  overflow: hidden;

  &::after {
    content: '';
    position: absolute;
    width: 280px;
    height: 200px;
    right: -60px;
    top: -80px;
    background: radial-gradient(circle, rgba(56, 189, 248, 0.35), transparent 66%);
    filter: blur(20px);
  }

  .sd-label {
    font-size: 10.5px;
    font-weight: 700;
    letter-spacing: 1px;
    color: rgba(214, 232, 255, 0.7);
    margin-bottom: 9px;
  }

  .sd-text {
    position: relative;
    font-family: 'Georgia', serif;
    font-size: 15px;
    line-height: 1.9;
    color: #fff;
  }

  .sd-meta {
    position: relative;
    margin-top: 12px;
    font-size: 11px;
    color: rgba(214, 232, 255, 0.65);

    .matched { color: #7ef0e0; }
    .unmatched { color: #fcd34d; }
  }
}

.two-col {
  display: grid;
  grid-template-columns: 1fr 1.15fr;
  gap: 14px;
  margin-bottom: 14px;
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

.backbone {
  font-family: 'Georgia', serif;
  font-size: 14px;
  line-height: 1.8;
  color: #16233f;
  font-weight: 600;
  padding: 10px 13px;
  border-radius: 9px;
  background: rgba(37, 99, 235, 0.05);
  border-left: 3px solid #2563eb;
  margin-bottom: 11px;
}

.bb-row {
  font-size: 12.5px;
  color: #4e5f7c;
  line-height: 1.8;
  margin-bottom: 6px;

  .bb-label {
    display: inline-block;
    width: 34px;
    font-size: 10.5px;
    font-weight: 700;
    color: #1d4ed8;
    background: rgba(37, 99, 235, 0.08);
    padding: 1px 6px;
    border-radius: 4px;
    margin-right: 8px;
  }
}

.bb-tip {
  margin-top: 12px;
  font-size: 11.5px;
  line-height: 1.7;
  color: #8494ae;
  padding-top: 11px;
  border-top: 1px dashed #eef2f9;
}

.mod-item {
  padding: 10px 0;
  border-bottom: 1px dashed #eef2f9;

  &:last-child {
    border-bottom: none;
  }
}

.mod-role {
  display: inline-block;
  font-size: 10.5px;
  font-weight: 700;
  color: #7c3aed;
  background: rgba(139, 92, 246, 0.1);
  padding: 2px 8px;
  border-radius: 4px;
  margin-bottom: 6px;
}

.mod-part {
  font-family: 'Georgia', serif;
  font-size: 12.5px;
  line-height: 1.75;
  color: #33415c;
  margin-bottom: 4px;
}

.mod-attach {
  font-size: 11.5px;
  color: #8494ae;
  line-height: 1.6;
}

.none {
  font-size: 12px;
  color: #a9b6c9;
}

.trans-text {
  font-size: 13.5px;
  line-height: 1.9;
  color: #33415c;
}

.tip-card {
  display: flex;
  gap: 9px;
  align-items: flex-start;
  padding: 14px 17px;
  border-radius: 12px;
  background: linear-gradient(135deg, rgba(37, 99, 235, 0.06), rgba(56, 189, 248, 0.05));
  border: 1px solid rgba(37, 99, 235, 0.14);
  font-size: 12.5px;
  line-height: 1.8;
  color: #1e40af;
}

.empty {
  background: #fff;
  border: 1px solid #e7edf8;
  border-radius: 16px;
}
</style>
