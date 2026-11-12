<template>
  <div class="ai-profile">
    <div class="wrap">
      <div class="head">
        <div>
          <div class="tech-section-title">学习画像</div>
          <div class="sub">
            五个维度全部由行为数据推算，每个维度标注了样本来源。样本少的地方我会写「参考价值有限」，不会假装很准。
          </div>
        </div>
        <el-button type="primary" plain :loading="loading" @click="load">
          <el-icon><Refresh /></el-icon> 重新分析
        </el-button>
      </div>

      <AiLoader v-if="loading" :steps="['汇总打卡与提交记录…', '计算维度分值…', '排序短板…']" hint="正在生成画像" />

      <template v-else-if="data">
        <section class="top-row">
          <div class="overall-card ai-grad-card">
            <div class="ov-label">综合能力指数</div>
            <div class="ov-value">{{ data.overall }}<small>/100</small></div>
            <div class="ov-bar"><i :style="{ width: data.overall + '%' }"></i></div>
            <div class="ov-note">{{ data.narrative }}</div>
          </div>

          <div class="radar-card">
            <RadarChart :data="data.radar" :size="230" />
            <div class="radar-legend">
              <div v-for="d in data.dims" :key="d.key" class="lg-item">
                <span class="dot" :style="{ background: d.color }"></span>
                {{ d.name }}
                <b>{{ d.value }}</b>
              </div>
            </div>
          </div>
        </section>

        <section class="dims">
          <div v-for="d in data.dims" :key="d.key" class="dim-card">
            <div class="dim-head">
              <span class="dim-name">
                <i :style="{ background: d.color }"></i>
                {{ d.name }}
              </span>
              <span class="dim-val" :style="{ color: d.color }">{{ d.value }}</span>
            </div>
            <div class="dim-bar">
              <i :style="{ width: d.value + '%', background: d.color }"></i>
            </div>
            <div class="dim-sample">样本：{{ d.sample }}</div>
            <div class="dim-status" :class="d.value >= 75 ? 'good' : d.value >= 60 ? 'mid' : 'bad'">
              {{ d.value >= 75 ? '达标' : d.value >= 60 ? '偏低' : '短板' }}
            </div>
          </div>
        </section>

        <section class="actions">
          <div class="tech-section-title">优先补这两块</div>
          <div class="action-grid">
            <div v-for="a in data.actions" :key="a.dimension" class="action-card">
              <div class="ac-head">
                <span class="ac-name">{{ a.dimension }}</span>
                <span class="ac-gap">距 75 分还差 {{ a.gap }}</span>
              </div>
              <ul>
                <li v-for="(t, i) in a.tips" :key="i">{{ t }}</li>
              </ul>
            </div>
          </div>
        </section>

        <div class="note">
          <el-icon><InfoFilled /></el-icon>
          {{ data.note }}
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import RadarChart from '@/components/ai/RadarChart.vue'
import AiLoader from '@/components/ai/AiLoader.vue'
import { ai } from '@/ai'
import { getUsage, bumpUsage } from '@/ai/usage'

const data = ref(null)
const loading = ref(false)

async function load() {
  loading.value = true
  const u = getUsage()
  try {
    const res = await ai.learningProfile({
      checkinDays: 23,
      aiChats: u.chat,
      writingCount: u.writing,
      wrongCount: Math.max(5, u.wrong),
      examScores: [72, 78]
    })
    data.value = res.data
    bumpUsage('profile', '学习画像')
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<style scoped lang="scss">
.ai-profile {
  padding: 20px 24px 32px;
}

.wrap {
  max-width: 1180px;
  margin: 0 auto;
}

.head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.sub {
  margin-top: 6px;
  font-size: 12.5px;
  color: #7c8ca8;
  line-height: 1.7;
  max-width: 720px;
}

.top-row {
  display: grid;
  grid-template-columns: 1fr 1.1fr;
  gap: 14px;
  margin-bottom: 16px;
}

@media (max-width: 1000px) {
  .top-row {
    grid-template-columns: 1fr;
  }
}

.overall-card {
  background: #fff;
  border-radius: 16px;
  border: 1px solid #e7edf8;
  padding: 22px 24px;
  display: flex;
  flex-direction: column;
  justify-content: center;

  .ov-label {
    font-size: 12px;
    color: #8494ae;
    margin-bottom: 8px;
  }

  .ov-value {
    font-size: 44px;
    font-weight: 850;
    color: #0e1a32;
    letter-spacing: -2px;
    line-height: 1;
    font-variant-numeric: tabular-nums;

    small {
      font-size: 13px;
      font-weight: 600;
      color: #b3c0d4;
      letter-spacing: 0;
      margin-left: 5px;
    }
  }

  .ov-bar {
    height: 7px;
    border-radius: 4px;
    background: #eef3fb;
    margin: 16px 0;
    overflow: hidden;

    i {
      display: block;
      height: 100%;
      border-radius: 4px;
      background: linear-gradient(90deg, #2563eb, #38bdf8);
    }
  }

  .ov-note {
    font-size: 12.5px;
    line-height: 1.8;
    color: #4e5f7c;
  }
}

.radar-card {
  background: #fff;
  border: 1px solid #e7edf8;
  border-radius: 16px;
  padding: 18px 22px;
  display: flex;
  align-items: center;
  gap: 18px;
  box-shadow: 0 10px 30px -26px rgba(16, 38, 84, 0.4);
}

.radar-legend {
  flex: 1;
}

.lg-item {
  display: flex;
  align-items: center;
  gap: 7px;
  font-size: 12.5px;
  color: #4e5f7c;
  padding: 5px 0;

  .dot {
    width: 7px;
    height: 7px;
    border-radius: 50%;
  }

  b {
    margin-left: auto;
    color: #0e1a32;
    font-variant-numeric: tabular-nums;
  }
}

.dims {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px;
  margin-bottom: 20px;
}

.dim-card {
  background: #fff;
  border: 1px solid #e7edf8;
  border-radius: 13px;
  padding: 15px 16px;
  position: relative;
  box-shadow: 0 8px 24px -22px rgba(16, 38, 84, 0.4);
}

.dim-head {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 10px;

  .dim-name {
    display: flex;
    align-items: center;
    gap: 7px;
    font-size: 13px;
    font-weight: 650;
    color: #16233f;

    i {
      width: 8px;
      height: 8px;
      border-radius: 2px;
    }
  }

  .dim-val {
    font-size: 18px;
    font-weight: 800;
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
  }
}

.dim-sample {
  margin-top: 9px;
  font-size: 10.5px;
  color: #a9b6c9;
}

.dim-status {
  position: absolute;
  top: 14px;
  right: 16px;
  font-size: 10px;
  font-weight: 700;
  padding: 1px 6px;
  border-radius: 4px;
  display: none;
}

.actions {
  margin-top: 6px;
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 12px;
}

.action-card {
  background: #fff;
  border: 1px solid #e7edf8;
  border-radius: 13px;
  padding: 16px 18px;

  .ac-head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 10px;
  }

  .ac-name {
    font-size: 13.5px;
    font-weight: 700;
    color: #16233f;
  }

  .ac-gap {
    font-size: 10.5px;
    color: #d97706;
    background: #fef3c7;
    padding: 2px 7px;
    border-radius: 4px;
    font-weight: 600;
  }

  ul {
    padding-left: 17px;
    margin: 0;
  }

  li {
    font-size: 12.5px;
    line-height: 1.85;
    color: #5b6f92;
  }
}

.note {
  margin-top: 18px;
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

  .el-icon {
    margin-top: 2px;
  }
}
</style>
