<template>
  <div class="plan-detail">
    <div class="page-header">
      <el-button @click="goBack" :icon="ArrowLeft">返回</el-button>
      <h2>{{ plan.name || '学习计划详情' }}</h2>
    </div>

    <div class="plan-content" v-loading="loading">
      <el-alert v-if="loadError" :title="loadError" type="error" show-icon :closable="false" style="margin-bottom: 16px">
        <el-button type="primary" link @click="loadPlan">重新加载</el-button>
      </el-alert>
      <template v-if="!loadError">
      <!-- 计划概览 -->
      <div class="plan-overview">
        <div class="overview-left">
          <div class="plan-info-card">
            <h3>计划信息</h3>
            <div class="info-row">
              <span class="label">计划名称：</span>
              <span>{{ plan.name }}</span>
            </div>
            <div class="info-row">
              <span class="label">目标等级：</span>
              <el-tag>{{ plan.targetLevel }}</el-tag>
            </div>
            <div class="info-row">
              <span class="label">每日学习：</span>
              <span>{{ plan.dailyStudyTime }} 分钟</span>
            </div>
            <div class="info-row">
              <span class="label">开始日期：</span>
              <span>{{ plan.startDate }}</span>
            </div>
            <div class="info-row">
              <span class="label">结束日期：</span>
              <span>{{ plan.endDate }}</span>
            </div>
            <div class="info-row">
              <span class="label">剩余天数：</span>
              <span>{{ remainingDays }} 天</span>
            </div>
          </div>
        </div>

        <div class="overview-right">
          <div class="progress-card">
            <h3>完成进度</h3>
            <el-progress
              type="circle"
              :percentage="plan.progress || 0"
              :width="150"
              :stroke-width="12"
            />
            <p class="progress-text">已完成 {{ plan.progress || 0 }}%</p>
          </div>
        </div>
      </div>

      <!-- 学习任务列表 -->
      <div class="task-section">
        <div class="section-header">
          <h3>学习任务</h3>
          <el-button type="primary" size="small" @click="addTask">
            <el-icon><Plus /></el-icon>
            添加任务
          </el-button>
        </div>

        <div class="task-list">
          <div v-for="task in tasks" :key="task.id" class="task-item">
            <el-checkbox
              v-model="task.completed"
              @change="toggleTask(task)"
            />
            <div class="task-content">
              <span class="task-title" :class="{ completed: task.completed }">
                {{ task.title }}
              </span>
              <span class="task-date">{{ task.dueDate }}</span>
            </div>
            <el-button type="danger" link size="small" @click="deleteTask(task)">
              删除
            </el-button>
          </div>

          <el-empty v-if="tasks.length === 0" description="暂无学习任务" />
        </div>
      </div>

      <!-- 学习记录 -->
      <div class="record-section">
        <h3>学习记录</h3>
        <div class="record-chart" ref="chartRef"></div>
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <el-button type="primary" @click="editPlan">编辑计划</el-button>
        <el-button type="danger" @click="deletePlan">删除计划</el-button>
      </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Plus } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { learningApi } from '@/api/learning'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const plan = ref({})
const tasks = ref([])
const chartRef = ref()
let chart = null
const loadError = ref('')

const remainingDays = computed(() => {
  if (!plan.value.endDate) return 0
  const end = new Date(plan.value.endDate)
  const now = new Date()
  const diff = Math.ceil((end - now) / (1000 * 60 * 60 * 24))
  return diff > 0 ? diff : 0
})

const goBack = () => {
  router.back()
}

const loadPlan = async () => {
  loading.value = true
  loadError.value = ''
  try {
    const id = route.params.id
    if (!id) {
      loadError.value = '计划ID无效'
      ElMessage.error('计划ID无效')
      return
    }
    const res = await learningApi.getPlanDetail(id)
    if (res.data) {
      plan.value = {
        id: res.data.id,
        name: res.data.planName,
        targetLevel: res.data.targetLevel || 'CET4',
        dailyStudyTime: res.data.dailyStudyTime || 60,
        startDate: res.data.startDate?.substring(0, 10) || '',
        endDate: res.data.endDate?.substring(0, 10) || '',
        progress: res.data.progress || 0
      }
    }

    // 加载任务
    loadTasks()
  } catch (error) {
    console.error('加载计划详情失败:', error)
    loadError.value = '加载计划详情失败，请重试'
    ElMessage.error('加载计划详情失败')
  } finally {
    loading.value = false
  }
}

const loadTasks = async () => {
  try {
    const res = await learningApi.getTasksByPlanId(route.params.id)
    if (res.data) {
      tasks.value = res.data.map(item => ({
        id: item.id,
        title: item.title || item.taskName,
        dueDate: item.deadline?.substring(0, 10) || '',
        completed: item.status === 'COMPLETED'
      }))
    }
  } catch (error) {
    console.error('加载任务失败:', error)
    // 如果新接口失败，尝试旧接口
    try {
      const res = await learningApi.getTaskList({ pageNum: 1, pageSize: 50 })
      if (res.data?.records) {
        tasks.value = res.data.records.map(item => ({
          id: item.id,
          title: item.title || item.taskName,
          dueDate: item.deadline?.substring(0, 10) || '',
          completed: item.status === 'COMPLETED'
        }))
      }
    } catch (e) {
      console.error('加载任务失败:', e)
    }
  }
}

const addTask = async () => {
  const { value } = await ElMessageBox.prompt('请输入任务名称', '添加任务', {
    confirmButtonText: '添加',
    cancelButtonText: '取消'
  }).catch(() => ({ value: null }))

  if (!value || !value.trim()) return

  try {
    await learningApi.createTask({
      title: value.trim(),
      planId: Number(route.params.id),
      deadline: plan.value.endDate
    })
    ElMessage.success('任务添加成功')
    await loadTasks()
    // 更新计划进度
    const completedCount = tasks.value.filter(t => t.completed).length
    const totalCount = tasks.value.length
    if (totalCount > 0) {
      plan.value.progress = Math.round((completedCount / totalCount) * 100)
    }
  } catch (error) {
    ElMessage.error('添加失败，请重试')
  }
}

const toggleTask = async (task) => {
  const wasCompleted = task.completed
  // 立即更新UI
  task.completed = !wasCompleted
  try {
    await learningApi.toggleTaskStatus(task.id)
    ElMessage.success(task.completed ? '任务已完成' : '已取消完成')
    // 更新计划进度
    const completedCount = tasks.value.filter(t => t.completed).length
    const totalCount = tasks.value.length
    if (totalCount > 0) {
      const newProgress = Math.round((completedCount / totalCount) * 100)
      plan.value.progress = newProgress
      // 同步进度到后端
      try {
        await learningApi.updatePlanProgress(plan.value.id, newProgress)
      } catch (e) {
        // 忽略进度同步失败
      }
    }
  } catch (error) {
    // 回滚UI
    task.completed = wasCompleted
    ElMessage.error('更新失败，请重试')
  }
}

const deleteTask = async (task) => {
  try {
    await ElMessageBox.confirm('确定要删除这个任务吗？', '提示', {
      type: 'warning'
    })
    await learningApi.deleteTask(task.id)
    tasks.value = tasks.value.filter(t => t.id !== task.id)
    ElMessage.success('任务删除成功')
    // 更新计划进度
    const completedCount = tasks.value.filter(t => t.completed).length
    const totalCount = tasks.value.length
    if (totalCount > 0) {
      plan.value.progress = Math.round((completedCount / totalCount) * 100)
    } else {
      plan.value.progress = 0
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败，请重试')
    }
  }
}

const editPlan = async () => {
  const { value } = await ElMessageBox.prompt('请输入新的目标等级', '编辑计划', {
    confirmButtonText: '保存',
    cancelButtonText: '取消',
    inputValue: plan.value.targetLevel
  }).catch(() => ({ value: null }))

  if (!value) return

  try {
    await learningApi.updatePlan(plan.value.id, { targetLevel: value })
    plan.value.targetLevel = value
    ElMessage.success('计划更新成功')
  } catch (error) {
    ElMessage.error('更新失败，请重试')
  }
}

const deletePlan = async () => {
  try {
    await ElMessageBox.confirm('确定要删除这个学习计划吗？删除后无法恢复。', '警告', {
      type: 'warning',
      confirmButtonText: '确定删除',
      cancelButtonText: '取消'
    })
    await learningApi.deletePlan(plan.value.id)
    ElMessage.success('计划删除成功')
    router.back()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败，请重试')
    }
  }
}

const initChart = () => {
  if (!chartRef.value) return

  chart = echarts.init(chartRef.value)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: {
      type: 'value',
      name: '学习时长(分钟)'
    },
    series: [{
      data: [60, 45, 80, 55, 70, 90, 65],
      type: 'bar',
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#409EFF' },
          { offset: 1, color: '#79bbff' }
        ])
      }
    }]
  })
}

const handleResize = () => {
  chart?.resize()
}

onMounted(() => {
  loadPlan()
  setTimeout(initChart, 100)
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chart?.dispose()
})
</script>

<style lang="scss" scoped>
.plan-detail {
  .page-header {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 24px;

    h2 {
      font-size: 24px;
      font-weight: 600;
      color: var(--text-primary);
      margin: 0;
    }
  }

  .plan-content {
    .plan-overview {
      display: grid;
      grid-template-columns: 1fr 300px;
      gap: 24px;
      margin-bottom: 24px;

      .plan-info-card, .progress-card {
        background: var(--bg-primary);
        border-radius: var(--radius-lg);
        padding: 24px;

        h3 {
          font-size: 16px;
          font-weight: 600;
          margin-bottom: 16px;
          color: var(--text-primary);
        }
      }

      .info-row {
        display: flex;
        margin-bottom: 12px;

        .label {
          color: var(--text-secondary);
          width: 100px;
        }
      }

      .progress-card {
        display: flex;
        flex-direction: column;
        align-items: center;

        .progress-text {
          margin-top: 16px;
          color: var(--text-secondary);
        }
      }
    }

    .task-section, .record-section {
      background: var(--bg-primary);
      border-radius: var(--radius-lg);
      padding: 24px;
      margin-bottom: 24px;

      .section-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;

        h3 {
          font-size: 16px;
          font-weight: 600;
          color: var(--text-primary);
          margin: 0;
        }
      }

      h3 {
        font-size: 16px;
        font-weight: 600;
        margin-bottom: 16px;
        color: var(--text-primary);
      }
    }

    .task-list {
      .task-item {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 12px;
        border-bottom: 1px solid var(--border-color);

        &:last-child {
          border-bottom: none;
        }

        .task-content {
          flex: 1;
          display: flex;
          justify-content: space-between;

          .task-title {
            &.completed {
              text-decoration: line-through;
              color: var(--text-secondary);
            }
          }

          .task-date {
            color: var(--text-secondary);
            font-size: 13px;
          }
        }
      }
    }

    .record-chart {
      height: 300px;
    }

    .action-buttons {
      display: flex;
      gap: 12px;
    }
  }
}
</style>
