<template>
  <div class="ai-plan">
    <div class="wrap">
      <div class="head">
        <div>
          <div class="tech-section-title">学习计划生成</div>
          <div class="sub">
            按你的短板加权编排，每天有明确的「做什么」和「产出什么」。计划里留了休息日 —— 排满的计划一定会失败。
          </div>
        </div>
      </div>

      <section class="param-card">
        <div class="param-row">
          <div class="param">
            <div class="p-label">计划天数</div>
            <el-slider v-model="days" :min="3" :max="14" :step="1" show-stops :marks="{ 3: '3天', 7: '7天', 14: '14天' }" />
          </div>
          <div class="param narrow">
            <div class="p-label">每天可投入</div>
            <el-select v-model="minutes">
              <el-option label="45 分钟" :value="45" />
              <el-option label="60 分钟" :value="60" />
              <el-option label="90 分钟" :value="90" />
              <el-option label="120 分钟" :value="120" />
            </el-select>
          </div>
          <div class="param narrow">
            <div class="p-label">目标考试</div>
            <el-select v-model="target">
              <el-option label="CET-4" value="CET4" />
              <el-option label="CET-6" value="CET6" />
            </el-select>
          </div>
        </div>
        <div class="param-actions">
          <el-button type="primary" :loading="loading" @click="generate">
            <el-icon><MagicStick /></el-icon> 生成计划
          </el-button>
          <el-button v-if="result" @click="copyPlan">
            <el-icon><DocumentCopy /></el-icon> 复制为文本
          </el-button>
        </div>
      </section>

      <AiLoader v-if="loading" :steps="['读取短板维度…', '分配每日任务…', '插入模考与复盘日…']" hint="正在编排日程" />

      <template v-else-if="result">
        <div class="plan-note">
          <el-icon><InfoFilled /></el-icon>
          <div>
            <div>{{ result.focusNote }}</div>
            <div class="rule">{{ result.rule }}</div>
          </div>
        </div>

        <section class="timeline">
          <div v-for="d in result.plan" :key="d.day" class="day-card" :class="{ rest: d.rest }">
            <div class="day-head">
              <div class="day-badge">
                <span class="d-num">D{{ d.day }}</span>
                <span class="d-date">{{ d.date }} {{ d.weekday }}</span>
              </div>
              <div class="day-focus">{{ d.focus }}</div>
              <div class="day-min">{{ d.minutes }} 分钟</div>
            </div>
            <div class="day-body">
              <ul class="tasks">
                <li v-for="(t, i) in d.tasks" :key="i">
                  <span class="ck"></span>{{ t }}
                </li>
              </ul>
              <div class="day-output">
                <span class="out-label">产出</span>{{ d.output }}
              </div>
            </div>
          </div>
        </section>

        <div class="total">合计 {{ result.totalMinutes }} 分钟 · 共 {{ result.plan.length }} 天</div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import AiLoader from '@/components/ai/AiLoader.vue'
import { ai } from '@/ai'
import { bumpUsage } from '@/ai/usage'

const days = ref(7)
const minutes = ref(90)
const target = ref('CET4')
const loading = ref(false)
const result = ref(null)

async function generate() {
  loading.value = true
  try {
    const res = await ai.plan({
      days: days.value,
      minutes: minutes.value,
      weakness: target.value === 'CET4' ? ['语法准确度', '写作结构'] : ['长难句', '词汇深度']
    })
    result.value = res.data
    bumpUsage('plan', `${days.value} 天计划`)
  } catch (e) {
    ElMessage.error(e.message || '生成失败')
  } finally {
    loading.value = false
  }
}

// 进入页面先按默认参数生成一份，避免首屏空白
onMounted(generate)

async function copyPlan() {
  const r = result.value
  const text = [
    `学习计划（${r.plan.length} 天，合计 ${r.totalMinutes} 分钟）`,
    r.focusNote,
    '',
    ...r.plan.map(d => [
      `D${d.day} ${d.date} ${d.weekday} · ${d.focus}（${d.minutes} 分钟）`,
      ...d.tasks.map(t => `  - ${t}`),
      `  产出：${d.output}`
    ].join('\n'))
  ].join('\n')
  try {
    await navigator.clipboard.writeText(text)
    ElMessage.success('计划已复制')
  } catch (e) {
    ElMessage.warning('浏览器不允许自动复制')
  }
}
</script>

<style scoped lang="scss">
.ai-plan {
  padding: 20px 24px 32px;
}

.wrap {
  max-width: 1000px;
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

.param-card {
  background: #fff;
  border: 1px solid #e7edf8;
  border-radius: 16px;
  padding: 20px 24px 18px;
  box-shadow: 0 14px 36px -28px rgba(16, 38, 84, 0.45);
  margin-bottom: 16px;
}

.param-row {
  display: flex;
  gap: 26px;
  align-items: flex-end;
  flex-wrap: wrap;
}

.param {
  flex: 1;
  min-width: 260px;
  padding-bottom: 16px;

  &.narrow {
    flex: none;
    min-width: 150px;
  }
}

.p-label {
  font-size: 12px;
  font-weight: 600;
  color: #4e5f7c;
  margin-bottom: 10px;
}

.param-actions {
  display: flex;
  gap: 10px;
  padding-top: 14px;
  border-top: 1px dashed #eef2f9;
}

.plan-note {
  display: flex;
  gap: 9px;
  padding: 13px 16px;
  border-radius: 12px;
  background: rgba(37, 99, 235, 0.05);
  border: 1px solid rgba(37, 99, 235, 0.14);
  font-size: 12.5px;
  line-height: 1.75;
  color: #1e40af;
  margin-bottom: 16px;

  .rule {
    margin-top: 5px;
    color: #5b6f92;
    font-size: 12px;
  }
}

.timeline {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.day-card {
  background: #fff;
  border: 1px solid #e7edf8;
  border-radius: 14px;
  overflow: hidden;
  transition: all 0.25s;

  &:hover {
    border-color: rgba(37, 99, 235, 0.26);
    box-shadow: 0 14px 32px -24px rgba(37, 99, 235, 0.5);
  }

  &.rest {
    background: #fbfdff;
    border-style: dashed;
  }
}

.day-head {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 12px 18px;
  background: linear-gradient(90deg, #f8fbff, #fdfeff);
  border-bottom: 1px solid #f2f6fc;
}

.day-badge {
  display: flex;
  align-items: baseline;
  gap: 8px;
  flex: none;

  .d-num {
    font-size: 13px;
    font-weight: 800;
    color: #1d4ed8;
    font-variant-numeric: tabular-nums;
  }

  .d-date {
    font-size: 11px;
    color: #a9b6c9;
  }
}

.day-focus {
  flex: 1;
  font-size: 13.5px;
  font-weight: 700;
  color: #16233f;
}

.day-min {
  font-size: 11.5px;
  color: #5b6f92;
  background: #f2f6fc;
  padding: 2px 9px;
  border-radius: 20px;
}

.day-body {
  padding: 13px 18px 15px;
}

.tasks {
  list-style: none;
  padding: 0;
  margin: 0 0 10px;

  li {
    display: flex;
    align-items: flex-start;
    gap: 9px;
    font-size: 12.5px;
    line-height: 1.85;
    color: #4e5f7c;
  }

  .ck {
    width: 13px;
    height: 13px;
    border-radius: 3px;
    border: 1.5px solid #cfdcef;
    flex: none;
    margin-top: 4px;
  }
}

.day-output {
  font-size: 12px;
  color: #5b6f92;
  padding: 8px 12px;
  border-radius: 8px;
  background: #f7faff;

  .out-label {
    font-size: 10.5px;
    font-weight: 700;
    color: #1d4ed8;
    background: rgba(37, 99, 235, 0.1);
    padding: 1px 7px;
    border-radius: 4px;
    margin-right: 8px;
  }
}

.total {
  margin-top: 14px;
  text-align: right;
  font-size: 11.5px;
  color: #a9b6c9;
}
</style>
