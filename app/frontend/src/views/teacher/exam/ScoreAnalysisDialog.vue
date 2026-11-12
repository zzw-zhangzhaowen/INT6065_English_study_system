<template>
  <el-dialog
    :model-value="modelValue"
    @update:model-value="$emit('update:modelValue', $event)"
    :title="`成绩分析 - ${examName}`"
    width="900px"
    @open="onOpen"
  >
    <div v-loading="loading" class="analysis-content">
      <!-- 统计卡片 -->
      <div class="stats-cards">
        <div class="stat-card">
          <div class="stat-value">{{ stats.totalAttempts || 0 }}</div>
          <div class="stat-label">参考人数</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ stats.completedAttempts || 0 }}</div>
          <div class="stat-label">完成人数</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ stats.avgScore || 0 }}</div>
          <div class="stat-label">平均分</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ stats.passRate || 0 }}%</div>
          <div class="stat-label">通过率</div>
        </div>
      </div>

      <!-- 分数分布图 -->
      <div v-if="distribution.length > 0" class="distribution-chart">
        <h4>分数分布</h4>
        <div class="chart-bars">
          <div v-for="item in distributionDisplay" :key="item.range" class="bar-item">
            <div class="bar-value">{{ item.count }}</div>
            <div class="bar-track">
              <div class="bar-fill" :style="{ height: item.percent + '%' }" :class="item.colorClass"></div>
            </div>
            <div class="bar-label">{{ item.range }}</div>
          </div>
        </div>
      </div>

      <!-- 标签页 -->
      <el-tabs v-model="activeTab" @tab-click="onTabClick">
        <el-tab-pane label="学生成绩" name="students">
          <el-table :data="studentRecords" size="small" max-height="350">
            <el-table-column label="排名" width="65">
              <template #default="{ row }">
                <span :class="{ 'top-rank': row.rank <= 3 }">{{ row.rank }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="realName" label="姓名" width="100" />
            <el-table-column label="得分" width="80" sortable>
              <template #default="{ row }">
                {{ row.obtainedScore }}
              </template>
            </el-table-column>
            <el-table-column label="正确/错误" width="100">
              <template #default="{ row }">
                <span style="color: var(--el-color-success)">{{ row.correctCount || 0 }}</span>
                /
                <span style="color: var(--el-color-danger)">{{ row.wrongCount || 0 }}</span>
              </template>
            </el-table-column>
            <el-table-column label="用时" width="80">
              <template #default="{ row }">
                {{ row.duration ? row.duration + '分' : '—' }}
              </template>
            </el-table-column>
            <el-table-column label="提交时间" min-width="150">
              <template #default="{ row }">
                {{ formatTime(row.submitTime) }}
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="题目分析" name="questions">
          <el-table :data="questionAnalysis" size="small" max-height="350" v-loading="loadingAnalysis">
            <el-table-column label="题号" width="65">
              <template #default="{ row }">
                {{ row.questionOrder || '—' }}
              </template>
            </el-table-column>
            <el-table-column prop="title" label="题目" min-width="180" show-overflow-tooltip />
            <el-table-column label="类型" width="90">
              <template #default="{ row }">
                <el-tag size="small">{{ getTypeName(row.questionType) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="分值" width="65">
              <template #default="{ row }">{{ row.questionScore }}</template>
            </el-table-column>
            <el-table-column label="作答" width="65">
              <template #default="{ row }">{{ row.totalAnswers }}</template>
            </el-table-column>
            <el-table-column label="正确" width="65">
              <template #default="{ row }">{{ row.correctAnswers }}</template>
            </el-table-column>
            <el-table-column label="正确率" width="90" sortable>
              <template #default="{ row }">
                <span :style="{ color: getAccuracyColor(row.accuracy) }">{{ row.accuracy }}%</span>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, computed } from 'vue'
import { examApi } from '@/api/exam'

const props = defineProps({
  modelValue: Boolean,
  examId: Number,
  examName: { type: String, default: '' }
})
defineEmits(['update:modelValue'])

const loading = ref(false)
const loadingAnalysis = ref(false)
const activeTab = ref('students')
const stats = ref({})
const distribution = ref([])
const studentRecords = ref([])
const questionAnalysis = ref([])
const analysisLoaded = ref(false)

const distributionDisplay = computed(() => {
  const ranges = ['90-100', '80-89', '70-79', '60-69', '0-59']
  const colorClasses = ['bar-excellent', 'bar-good', 'bar-medium', 'bar-pass', 'bar-fail']
  const distMap = {}
  distribution.value.forEach(d => { distMap[d.scoreRange] = Number(d.count) || 0 })
  const maxCount = Math.max(...ranges.map(r => distMap[r] || 0), 1)
  return ranges.map((range, i) => ({
    range,
    count: distMap[range] || 0,
    percent: ((distMap[range] || 0) / maxCount) * 100,
    colorClass: colorClasses[i]
  }))
})

const onOpen = async () => {
  analysisLoaded.value = false
  activeTab.value = 'students'
  await loadData()
}

const loadData = async () => {
  if (!props.examId) return
  loading.value = true
  try {
    const [statsRes, recordsRes] = await Promise.all([
      examApi.getExamStats(props.examId),
      examApi.getExamRecordsByExam(props.examId)
    ])
    stats.value = statsRes.data || {}
    distribution.value = statsRes.data?.scoreDistribution || []
    studentRecords.value = recordsRes.data || []
  } catch (e) {
    console.error('加载成绩数据失败', e)
  } finally {
    loading.value = false
  }
}

const onTabClick = (tab) => {
  if (tab.paneName === 'questions' && !analysisLoaded.value) {
    loadQuestionAnalysis()
  }
}

const loadQuestionAnalysis = async () => {
  loadingAnalysis.value = true
  try {
    const res = await examApi.getQuestionAnalysis(props.examId)
    questionAnalysis.value = res.data || []
    analysisLoaded.value = true
  } catch (e) {
    console.error('加载题目分析失败', e)
  } finally {
    loadingAnalysis.value = false
  }
}

const formatTime = (time) => {
  if (!time) return '—'
  return String(time).substring(0, 16).replace('T', ' ')
}

const getTypeName = (type) => {
  const map = {
    SINGLE_CHOICE: '单选题', MULTI_CHOICE: '多选题', TRUE_FALSE: '判断题',
    FILL_BLANK: '填空题', SHORT_ANSWER: '简答题', READING: '阅读理解',
    LISTENING: '听力题', TRANSLATION: '翻译题', WRITING: '写作题', CLOZE: '完形填空'
  }
  return map[type] || type || '—'
}

const getAccuracyColor = (accuracy) => {
  if (accuracy >= 80) return 'var(--el-color-success)'
  if (accuracy >= 60) return 'var(--el-color-warning)'
  return 'var(--el-color-danger)'
}
</script>

<style lang="scss" scoped>
.analysis-content {
  min-height: 300px;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;

  .stat-card {
    text-align: center;
    padding: 16px;
    background: var(--el-fill-color-lighter);
    border-radius: 8px;

    .stat-value {
      font-size: 28px;
      font-weight: 700;
      color: var(--el-color-primary);
    }

    .stat-label {
      font-size: 13px;
      color: var(--el-text-color-secondary);
      margin-top: 4px;
    }
  }
}

.distribution-chart {
  margin-bottom: 24px;

  h4 {
    margin: 0 0 12px;
    font-size: 14px;
    font-weight: 600;
  }

  .chart-bars {
    display: flex;
    justify-content: space-around;
    align-items: flex-end;
    height: 120px;
    gap: 12px;
  }

  .bar-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    flex: 1;

    .bar-value {
      font-size: 12px;
      font-weight: 600;
      margin-bottom: 4px;
    }

    .bar-track {
      width: 100%;
      max-width: 60px;
      height: 80px;
      background: var(--el-fill-color-lighter);
      border-radius: 4px 4px 0 0;
      display: flex;
      align-items: flex-end;
      overflow: hidden;
    }

    .bar-fill {
      width: 100%;
      border-radius: 4px 4px 0 0;
      transition: height 0.3s;
      min-height: 2px;
    }

    .bar-excellent { background: #67c23a; }
    .bar-good { background: #409eff; }
    .bar-medium { background: #e6a23c; }
    .bar-pass { background: #f56c6c; }
    .bar-fail { background: #909399; }

    .bar-label {
      font-size: 12px;
      color: var(--el-text-color-secondary);
      margin-top: 4px;
    }
  }
}

.top-rank {
  font-weight: 700;
  color: var(--el-color-warning);
}
</style>
