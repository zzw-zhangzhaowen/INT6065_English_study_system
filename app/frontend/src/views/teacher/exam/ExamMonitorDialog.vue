<template>
  <el-dialog
    :model-value="modelValue"
    @update:model-value="$emit('update:modelValue', $event)"
    :title="`考试监控 - ${examName}`"
    width="900px"
    @open="onOpen"
    @close="onClose"
  >
    <div v-loading="loading" class="monitor-content">
      <!-- 顶部信息栏 -->
      <div class="monitor-header">
        <div class="header-item">
          <span class="header-label">考试时长</span>
          <span class="header-value">{{ monitorData.exam?.duration || 0 }}分钟</span>
        </div>
        <div class="header-item">
          <span class="header-label">剩余时间</span>
          <span class="header-value countdown" :class="{ urgent: remainingMinutes <= 10 }">{{ remainingTimeText }}</span>
        </div>
        <div class="header-item">
          <span class="header-label">参考人数</span>
          <span class="header-value">{{ monitorData.summary?.total || 0 }}</span>
        </div>
        <div class="header-item">
          <span class="header-label">答题中</span>
          <span class="header-value" style="color: var(--el-color-primary)">{{ monitorData.summary?.inProgress || 0 }}</span>
        </div>
        <div class="header-item">
          <span class="header-label">已交卷</span>
          <span class="header-value" style="color: var(--el-color-success)">{{ monitorData.summary?.completed || 0 }}</span>
        </div>
      </div>

      <!-- 学生列表 -->
      <el-table :data="students" size="small" max-height="450">
        <el-table-column prop="realName" label="姓名" width="100">
          <template #default="{ row }">
            {{ row.realName || row.username || '未知' }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 'COMPLETED' ? 'success' : ''" size="small">
              {{ row.status === 'COMPLETED' ? '已交卷' : '答题中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="答题进度" width="110">
          <template #default="{ row }">
            <span>{{ row.answeredCount || 0 }} / {{ row.totalQuestions || 0 }}</span>
            <el-progress
              :percentage="row.totalQuestions > 0 ? Math.round((row.answeredCount || 0) / row.totalQuestions * 100) : 0"
              :stroke-width="4"
              :show-text="false"
              style="margin-top: 2px"
            />
          </template>
        </el-table-column>
        <el-table-column label="得分" width="70">
          <template #default="{ row }">
            {{ row.status === 'COMPLETED' ? row.obtainedScore : '—' }}
          </template>
        </el-table-column>
        <el-table-column label="作弊警告" width="90">
          <template #default="{ row }">
            <span :style="{ color: row.cheatWarningCount > 0 ? 'var(--el-color-danger)' : '', fontWeight: row.cheatWarningCount > 0 ? '700' : '' }">
              {{ row.cheatWarningCount || 0 }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="开始时间" min-width="150">
          <template #default="{ row }">
            {{ formatTime(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column label="提交时间" min-width="150">
          <template #default="{ row }">
            {{ row.submitTime ? formatTime(row.submitTime) : '—' }}
          </template>
        </el-table-column>
      </el-table>

      <div class="monitor-footer">
        <span class="auto-refresh-hint">每 15 秒自动刷新</span>
        <el-button size="small" @click="loadMonitorData">手动刷新</el-button>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, computed, onBeforeUnmount, watch } from 'vue'
import { examApi } from '@/api/exam'

const props = defineProps({
  modelValue: Boolean,
  examId: Number,
  examName: { type: String, default: '' }
})
defineEmits(['update:modelValue'])

const loading = ref(false)
const monitorData = ref({})
const students = ref([])
let refreshTimer = null

const remainingMinutes = computed(() => {
  const exam = monitorData.value.exam
  if (!exam?.endTime) return 999
  const end = new Date(exam.endTime)
  const now = new Date()
  return Math.max(0, Math.round((end - now) / 60000))
})

const remainingTimeText = computed(() => {
  const mins = remainingMinutes.value
  if (mins >= 999) return '—'
  if (mins <= 0) return '已结束'
  const h = Math.floor(mins / 60)
  const m = mins % 60
  return h > 0 ? `${h}小时${m}分钟` : `${m}分钟`
})

const onOpen = () => {
  loadMonitorData()
  startAutoRefresh()
}

const onClose = () => {
  stopAutoRefresh()
}

const startAutoRefresh = () => {
  stopAutoRefresh()
  refreshTimer = setInterval(() => {
    loadMonitorData()
  }, 15000)
}

const stopAutoRefresh = () => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
    refreshTimer = null
  }
}

const loadMonitorData = async () => {
  if (!props.examId) return
  loading.value = true
  try {
    const res = await examApi.getExamMonitor(props.examId)
    monitorData.value = res.data || {}
    students.value = res.data?.students || []
  } catch (e) {
    console.error('加载监控数据失败', e)
  } finally {
    loading.value = false
  }
}

const formatTime = (time) => {
  if (!time) return '—'
  return String(time).substring(0, 16).replace('T', ' ')
}

// 组件卸载时清除定时器
onBeforeUnmount(() => {
  stopAutoRefresh()
})

// 弹窗关闭时也清除
watch(() => props.modelValue, (val) => {
  if (!val) stopAutoRefresh()
})
</script>

<style lang="scss" scoped>
.monitor-content {
  min-height: 200px;
}

.monitor-header {
  display: flex;
  justify-content: space-around;
  padding: 16px;
  background: var(--el-fill-color-lighter);
  border-radius: 8px;
  margin-bottom: 16px;

  .header-item {
    text-align: center;

    .header-label {
      display: block;
      font-size: 12px;
      color: var(--el-text-color-secondary);
      margin-bottom: 4px;
    }

    .header-value {
      font-size: 20px;
      font-weight: 700;

      &.countdown {
        color: var(--el-color-primary);
      }

      &.urgent {
        color: var(--el-color-danger);
        animation: blink 1s infinite;
      }
    }
  }
}

@keyframes blink {
  50% { opacity: 0.5; }
}

.monitor-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;

  .auto-refresh-hint {
    font-size: 12px;
    color: var(--el-text-color-secondary);
  }
}
</style>
