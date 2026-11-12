<template>
  <div class="learning-center">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>学习中心</h2>
      <p>丰富的学习资源，助力您的英语提升之路</p>
    </div>

    <!-- 功能选项卡 -->
    <el-tabs v-model="activeTab" class="learning-tabs">
      <el-tab-pane label="学习资源" name="resource">
        <!-- 资源筛选 -->
        <div class="filter-section">
          <div class="filter-tags">
            <span class="filter-label">资源类型：</span>
            <el-check-tag
              v-for="type in resourceTypes"
              :key="type.value"
              :checked="selectedTypes.includes(type.value)"
              @change="toggleType(type.value)"
            >
              {{ type.label }}
            </el-check-tag>
          </div>
          <div class="filter-tags">
            <span class="filter-label">考试等级：</span>
            <el-check-tag
              v-for="level in levels"
              :key="level.value"
              :checked="selectedLevel === level.value"
              @change="changeLevel(level.value)"
            >
              {{ level.label }}
            </el-check-tag>
          </div>
        </div>

        <!-- 资源列表 -->
        <div v-loading="resourceLoading">
          <el-alert v-if="resourceError" :title="resourceError" type="error" show-icon :closable="false" style="margin-bottom: 16px">
            <el-button type="primary" link @click="loadResources">重新加载</el-button>
          </el-alert>
          <div class="resource-grid" v-if="resources.length > 0">
            <div v-for="resource in resources" :key="resource.id" class="resource-card">
              <div class="resource-cover">
                <el-image
                  :src="getResourceCover(resource)"
                  :alt="resource.title"
                  fit="cover"
                  style="width: 100%; height: 100%;"
                >
                  <template #error>
                    <el-image :src="DEFAULT_COVER" fit="cover" style="width: 100%; height: 100%;" />
                  </template>
                </el-image>
                <div class="resource-type-badge">
                  <el-icon v-if="resource.type === 'video'"><VideoPlay /></el-icon>
                  <el-icon v-else-if="resource.type === 'audio'"><Headset /></el-icon>
                  <el-icon v-else><Document /></el-icon>
                </div>
                <div v-if="resource.type === 'video'" class="resource-duration">
                  {{ resource.duration }}
                </div>
              </div>
              <div class="resource-info">
                <h4 class="resource-title">{{ resource.title }}</h4>
                <p class="resource-desc">{{ resource.description }}</p>
                <div class="resource-meta">
                  <el-tag size="small">{{ resource.level }}</el-tag>
                  <span class="view-count">
                    <el-icon><View /></el-icon>
                    {{ resource.views }}
                  </span>
                </div>
              </div>
              <div class="resource-actions">
                <el-button type="primary" @click="viewResource(resource)">
                  {{ resource.type === 'video' ? '播放' : '查看' }}
                </el-button>
                <el-button :icon="resource.favorited ? StarFilled : Star" :type="resource.favorited ? 'warning' : ''" circle @click="toggleFavorite(resource)" />
              </div>
            </div>
          </div>
          <el-empty v-if="!resourceLoading && !resourceError && resources.length === 0" description="暂无学习资源" />
        </div>
      </el-tab-pane>

      <el-tab-pane label="学习笔记" name="note">
        <div class="note-section" v-loading="noteLoading">
          <el-alert v-if="noteError" :title="noteError" type="error" show-icon :closable="false" style="margin-bottom: 16px">
            <el-button type="primary" link @click="loadNotes">重新加载</el-button>
          </el-alert>
          <div class="note-header">
            <el-button type="primary" @click="createNote">
              <el-icon><Plus /></el-icon>
              新建笔记
            </el-button>
            <el-input
              v-model="noteSearch"
              placeholder="搜索笔记"
              :prefix-icon="Search"
              style="width: 240px"
            />
          </div>
          <div class="note-list" v-if="notes.length > 0">
            <div v-for="note in notes" :key="note.id" class="note-card">
              <div class="note-content">
                <h4>{{ note.title }}</h4>
                <p>{{ note.summary }}</p>
                <div class="note-tags">
                  <el-tag v-for="tag in note.tags" :key="tag" size="small" type="info">
                    {{ tag }}
                  </el-tag>
                </div>
              </div>
              <div class="note-meta">
                <span>{{ note.updateTime }}</span>
                <div class="note-actions">
                  <el-button type="primary" link size="small" @click="editNote(note)">编辑</el-button>
                  <el-button type="danger" link size="small" @click="deleteNote(note)">删除</el-button>
                </div>
              </div>
            </div>
          </div>
          <el-empty v-if="!noteLoading && !noteError && notes.length === 0" description="暂无学习笔记" />
        </div>
      </el-tab-pane>

      <el-tab-pane label="学习任务" name="task">
        <div class="task-section" v-loading="taskLoading">
          <el-alert v-if="taskError" :title="taskError" type="error" show-icon :closable="false" style="margin-bottom: 16px">
            <el-button type="primary" link @click="loadTasks">重新加载</el-button>
          </el-alert>
          <div class="task-header">
            <div class="task-stats">
              <div class="stat-item">
                <span class="stat-value">{{ taskStats.total }}</span>
                <span class="stat-label">总任务</span>
              </div>
              <div class="stat-item">
                <span class="stat-value">{{ taskStats.completed }}</span>
                <span class="stat-label">已完成</span>
              </div>
              <div class="stat-item">
                <span class="stat-value">{{ taskStats.pending }}</span>
                <span class="stat-label">进行中</span>
              </div>
            </div>
            <el-button type="primary" @click="createTask">
              <el-icon><Plus /></el-icon>
              新建任务
            </el-button>
          </div>
          <div class="task-list" v-if="tasks.length > 0">
            <div v-for="task in tasks" :key="task.id" class="task-item">
              <el-checkbox
                :model-value="task.status === 'COMPLETED'"
                @change="toggleTaskComplete(task)"
              />
              <div class="task-info">
                <div class="task-title" :class="{ completed: task.status === 'COMPLETED' }">
                  {{ task.title }}
                </div>
                <div class="task-meta">
                  <el-tag :type="getPriorityType(task.priority)" size="small">
                    {{ getPriorityLabel(task.priority) }}
                  </el-tag>
                  <span class="task-deadline">截止：{{ task.deadline }}</span>
                </div>
              </div>
              <el-progress
                :percentage="task.progress"
                :stroke-width="6"
                style="width: 120px"
              />
              <el-button type="danger" link @click="deleteTask(task)">删除</el-button>
            </div>
          </div>
          <el-empty v-if="!taskLoading && !taskError && tasks.length === 0" description="暂无学习任务" />
        </div>
      </el-tab-pane>

      <el-tab-pane label="学习计划" name="plan">
        <div class="plan-section" v-loading="planLoading">
          <el-alert v-if="planError" :title="planError" type="error" show-icon :closable="false" style="margin-bottom: 16px">
            <el-button type="primary" link @click="loadPlans">重新加载</el-button>
          </el-alert>
          <div class="plan-header">
            <h3>我的学习计划</h3>
            <el-button type="primary" @click="createPlan">
              <el-icon><Plus /></el-icon>
              创建计划
            </el-button>
          </div>
          <div class="plan-list" v-if="plans.length > 0">
            <div v-for="plan in plans" :key="plan.id" class="plan-card">
              <div class="plan-info">
                <h4>{{ plan.name }}</h4>
                <p>目标：{{ plan.goal }}</p>
                <div class="plan-time">
                  {{ plan.startDate }} - {{ plan.endDate }}
                </div>
              </div>
              <div class="plan-progress">
                <el-progress
                  type="circle"
                  :percentage="plan.progress"
                  :width="80"
                  :stroke-width="8"
                />
              </div>
              <div class="plan-actions">
                <el-button type="primary" size="small" @click="viewPlanDetail(plan)">查看详情</el-button>
                <el-button size="small" @click="adjustPlan(plan)">调整计划</el-button>
                <el-button type="danger" size="small" @click="deletePlan(plan)">删除</el-button>
              </div>
            </div>
          </div>
          <el-empty v-if="!planLoading && !planError && plans.length === 0" description="暂无学习计划" />
        </div>
      </el-tab-pane>

      <el-tab-pane label="学习报告" name="report">
        <div class="report-section" v-loading="reportLoading">
          <el-alert v-if="reportError" :title="reportError" type="error" show-icon :closable="false" style="margin-bottom: 16px">
            <el-button type="primary" link @click="loadReport">重新加载</el-button>
          </el-alert>
          <div class="report-overview">
            <div class="overview-card">
              <div class="overview-icon">
                <el-icon :size="32"><Timer /></el-icon>
              </div>
              <div class="overview-info">
                <div class="overview-value">{{ reportData.totalStudyTime || 0 }}</div>
                <div class="overview-label">累计学习时长(小时)</div>
              </div>
            </div>
            <div class="overview-card">
              <div class="overview-icon">
                <el-icon :size="32"><Document /></el-icon>
              </div>
              <div class="overview-info">
                <div class="overview-value">{{ reportData.totalExercises || 0 }}</div>
                <div class="overview-label">完成练习题数</div>
              </div>
            </div>
            <div class="overview-card">
              <div class="overview-icon">
                <el-icon :size="32"><TrendCharts /></el-icon>
              </div>
              <div class="overview-info">
                <div class="overview-value">{{ reportData.accuracy || 0 }}%</div>
                <div class="overview-label">平均正确率</div>
              </div>
            </div>
            <div class="overview-card">
              <div class="overview-icon">
                <el-icon :size="32"><Sunny /></el-icon>
              </div>
              <div class="overview-info">
                <div class="overview-value">{{ reportData.consecutiveDays || 0 }}</div>
                <div class="overview-label">连续学习天数</div>
              </div>
            </div>
          </div>
          <div class="report-charts">
            <div class="chart-card">
              <h4>学习时长趋势</h4>
              <div class="chart-container" ref="trendChartRef"></div>
            </div>
            <div class="chart-card">
              <h4>学习内容分布</h4>
              <div class="chart-container" ref="distributionChartRef"></div>
            </div>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, watch, nextTick, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as echarts from 'echarts'
import {
  VideoPlay, Headset, Document, View, Star, StarFilled, Plus, Search,
  Timer, TrendCharts, Sunny
} from '@element-plus/icons-vue'
import { learningApi } from '@/api/learning'
import { getFileUrl, DEFAULT_COVER } from '@/utils/file'

const router = useRouter()

const activeTab = ref('resource')
const noteSearch = ref('')

// Loading 状态
const resourceLoading = ref(false)
const noteLoading = ref(false)
const taskLoading = ref(false)
const planLoading = ref(false)
const reportLoading = ref(false)

// 错误状态
const resourceError = ref('')
const noteError = ref('')
const taskError = ref('')
const planError = ref('')
const reportError = ref('')

// 资源筛选
const resourceTypes = [
  { label: '全部', value: 'all' },
  { label: '视频', value: 'video' },
  { label: '音频', value: 'audio' },
  { label: '文档', value: 'document' }
]

const levels = [
  { label: '全部', value: 'all' },
  { label: 'CET-4', value: 'cet4' },
  { label: 'CET-6', value: 'cet6' },
  { label: '考研', value: 'postgraduate' },
  { label: '雅思', value: 'ielts' }
]

const selectedTypes = ref(['all'])
const selectedLevel = ref('all')

// 学习资源
const resources = ref([])

// 学习笔记
const notes = ref([])

// 学习任务
const taskStats = ref({
  total: 0,
  completed: 0,
  pending: 0
})

const tasks = ref([])

// 学习计划
const plans = ref([])

// 学习报告数据
const reportData = ref({
  totalStudyTime: 0,
  totalExercises: 0,
  accuracy: 0,
  consecutiveDays: 0
})

// 图表引用
const trendChartRef = ref()
const distributionChartRef = ref()
let trendChart = null
let distributionChart = null

const toggleType = (type) => {
  if (type === 'all') {
    selectedTypes.value = ['all']
  } else {
    const index = selectedTypes.value.indexOf(type)
    if (index > -1) {
      selectedTypes.value.splice(index, 1)
    } else {
      selectedTypes.value = selectedTypes.value.filter(t => t !== 'all')
      selectedTypes.value.push(type)
    }
    if (selectedTypes.value.length === 0) {
      selectedTypes.value = ['all']
    }
  }
  loadResources()
}

// 切换等级筛选
const changeLevel = (level) => {
  selectedLevel.value = level
  loadResources()
}

// 获取资源封面图片
const getResourceCover = (resource) => {
  return getFileUrl(resource.cover) || DEFAULT_COVER
}

const viewResource = (resource) => {
  router.push(`/student/learning/resource/${resource.id}`)
}

const toggleFavorite = async (resource) => {
  try {
    // 始终先检查实际收藏状态
    let actuallyFavorited = false
    try {
      const checkRes = await learningApi.isCollected(resource.id)
      actuallyFavorited = checkRes.data === true
    } catch (e) {
      console.error('检查收藏状态失败:', e)
      // 如果检查失败，使用当前UI状态
      actuallyFavorited = resource.favorited
    }
    
    // 更新UI显示实际状态
    resource.favorited = actuallyFavorited
    
    // 根据实际状态执行相应操作
    if (actuallyFavorited) {
      // 已收藏，执行取消收藏
      await learningApi.uncollectResource(resource.id)
      resource.favorited = false
      ElMessage.success('已取消收藏')
    } else {
      // 未收藏，执行收藏
      await learningApi.collectResource(resource.id)
      resource.favorited = true
      ElMessage.success('收藏成功')
    }
  } catch (error) {
    console.error('收藏操作失败:', error)
    ElMessage.error('操作失败，请重试')
  }
}

const createNote = () => {
  router.push('/student/learning/note/new')
}

// 编辑笔记
const editNote = (note) => {
  router.push(`/student/learning/note/${note.id}`)
}

// 删除笔记
const deleteNote = async (note) => {
  try {
    await ElMessageBox.confirm('确定要删除这条笔记吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await learningApi.deleteNote(note.id)
    notes.value = notes.value.filter(n => n.id !== note.id)
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const createTask = async () => {
  try {
    const { value } = await ElMessageBox.prompt('请输入任务名称', '创建任务', {
      confirmButtonText: '创建',
      cancelButtonText: '取消'
    })

    if (!value || !value.trim()) {
      ElMessage.warning('请输入任务名称')
      return
    }

    const taskData = {
      title: value.trim(),
      priority: 'MEDIUM',
      deadline: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]
    }

    await learningApi.createTask(taskData)
    ElMessage.success('任务创建成功')
    await loadTasks()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('创建失败，请重试')
    }
  }
}

const toggleTaskComplete = async (task) => {
  const oldStatus = task.status
  const newStatus = oldStatus === 'COMPLETED' ? 'PENDING' : 'COMPLETED'
  // 立即更新UI
  task.status = newStatus
  task.progress = newStatus === 'COMPLETED' ? 100 : 0
  // 更新本地统计
  if (newStatus === 'COMPLETED') {
    taskStats.value.completed++
    taskStats.value.pending--
  } else {
    taskStats.value.completed--
    taskStats.value.pending++
  }
  try {
    await learningApi.toggleTaskStatus(task.id)
    ElMessage.success(newStatus === 'COMPLETED' ? '已标记为完成' : '已取消完成')
  } catch (error) {
    // 回滚UI
    task.status = oldStatus
    task.progress = oldStatus === 'COMPLETED' ? 100 : 0
    if (newStatus === 'COMPLETED') {
      taskStats.value.completed--
      taskStats.value.pending++
    } else {
      taskStats.value.completed++
      taskStats.value.pending--
    }
    ElMessage.error('操作失败，请重试')
  }
}

const deleteTask = async (task) => {
  try {
    await ElMessageBox.confirm('确定要删除这个任务吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await learningApi.deleteTask(task.id)
    // 立即从列表移除
    tasks.value = tasks.value.filter(t => t.id !== task.id)
    // 更新统计
    taskStats.value.total--
    if (task.status === 'COMPLETED') {
      taskStats.value.completed--
    } else {
      taskStats.value.pending--
    }
    ElMessage.success('任务删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败，请重试')
    }
  }
}

const updateTask = (task) => {
  task.completed = !task.completed
  if (task.completed) {
    task.progress = 100
    taskStats.value.completed++
    taskStats.value.pending--
  } else {
    task.progress = 0
    taskStats.value.completed--
    taskStats.value.pending++
  }
}

const createPlan = async () => {
  ElMessageBox.prompt('请输入计划名称', '创建学习计划', {
    confirmButtonText: '创建',
    cancelButtonText: '取消'
  }).then(async ({ value }) => {
    try {
      const planData = {
        planName: value,
        targetLevel: 'CET-4',
        dailyStudyTime: 60,
        startDate: new Date().toISOString().split('T')[0],
        endDate: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]
      }
      await learningApi.createPlan(planData)
      ElMessage.success('学习计划创建成功')
      await loadPlans()
    } catch (error) {
      ElMessage.error('创建失败，请重试')
    }
  }).catch(() => {})
}

const getPriorityType = (priority) => {
  const types = { 'HIGH': 'danger', 'MEDIUM': 'warning', 'LOW': 'info' }
  return types[priority] || 'info'
}

const getPriorityLabel = (priority) => {
  const labels = { 'HIGH': '高', 'MEDIUM': '中', 'LOW': '低' }
  return labels[priority] || priority
}

// 初始化图表（使用API数据）
const initCharts = async () => {
  // 获取学习趋势数据
  let trendData = { labels: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'], values: [0, 0, 0, 0, 0, 0, 0] }
  let distributionData = [
    { value: 0, name: '未掌握' },
    { value: 0, name: '基本掌握' },
    { value: 0, name: '已掌握' }
  ]
  try {
    const trendRes = await learningApi.getLearningTrend({ days: 7 })
    const trendSource = Array.isArray(trendRes.data)
      ? trendRes.data
      : (Array.isArray(trendRes.data?.trend) ? trendRes.data.trend : null)

    if (trendSource && trendSource.length > 0) {
      trendData.labels = trendSource.map(item => item.date || item.label)
      trendData.values = trendSource.map(item => Number(item.minutes ?? item.value ?? 0))
    } else if (Array.isArray(trendRes.data?.labels) && Array.isArray(trendRes.data?.values)) {
      trendData.labels = trendRes.data.labels
      trendData.values = trendRes.data.values.map(v => Number(v || 0))
    }
  } catch (e) {
    console.warn('获取学习趋势失败，使用默认数据')
  }
  try {
    const distRes = await learningApi.getLearningDistribution()
    if (distRes.data) {
      distributionData = [
        { value: Number(distRes.data.notMastered || 0), name: '未掌握' },
        { value: Number(distRes.data.basicMastered || 0), name: '基本掌握' },
        { value: Number(distRes.data.mastered || 0), name: '已掌握' }
      ]
    }
  } catch (e) {
    console.warn('获取学习分布失败，使用默认数据')
  }

  if (trendChartRef.value) {
    trendChart = echarts.init(trendChartRef.value)
    trendChart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: {
        type: 'category',
        data: trendData.labels
      },
      yAxis: { type: 'value', name: '小时' },
      series: [{
        data: trendData.values,
        type: 'line',
        smooth: true,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 128, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 128, 255, 0.05)' }
          ])
        }
      }]
    })
  }

  if (distributionChartRef.value) {
    distributionChart = echarts.init(distributionChartRef.value)
    distributionChart.setOption({
      tooltip: { trigger: 'item' },
      legend: { bottom: 0 },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        data: distributionData
      }]
    })
  }
}

const handleResize = () => {
  trendChart?.resize()
  distributionChart?.resize()
}

// 加载学习资源
const loadResources = async () => {
  resourceLoading.value = true
  resourceError.value = ''
  try {
    const params = { pageNum: 1, pageSize: 20 }

    // 资源类型筛选
    if (!selectedTypes.value.includes('all') && selectedTypes.value.length > 0) {
      // 转换类型名称：video -> 视频, audio -> 音频, document -> 文档
      const typeMap = { video: '视频', audio: '音频', document: '文档' }
      params.resourceType = selectedTypes.value.map(t => typeMap[t] || t).join(',')
    }

    // 等级筛选
    if (selectedLevel.value && selectedLevel.value !== 'all') {
      const levelMap = { cet4: 'CET4', cet6: 'CET6', postgraduate: '考研', ielts: 'IELTS' }
      params.level = levelMap[selectedLevel.value] || selectedLevel.value
    }

    const res = await learningApi.getResourceList(params)
    if (res.data?.records) {
      resources.value = res.data.records.map(item => ({
        id: item.id,
        title: item.resourceName,
        description: item.description || '',
        type: (item.resourceType || 'document').toLowerCase(),
        level: item.level || 'CET-4',
        cover: item.coverUrl || '',
        duration: item.duration || '',
        views: item.viewCount || 0,
        favorited: false // 默认未收藏，点击时实时检查
      }))
    }
  } catch (error) {
    console.error('加载资源失败:', error)
    resourceError.value = '加载学习资源失败，请重试'
    ElMessage.error('加载学习资源失败')
  } finally {
    resourceLoading.value = false
  }
}

// 加载学习笔记
const loadNotes = async () => {
  noteLoading.value = true
  noteError.value = ''
  try {
    const res = await learningApi.getNoteList({ pageNum: 1, pageSize: 10 })
    if (res.data?.records) {
      notes.value = res.data.records.map(item => ({
        id: item.id,
        title: item.title,
        summary: item.content?.substring(0, 50) || '',
        tags: item.tags ? item.tags.split(',') : [],
        updateTime: item.updateTime?.substring(0, 10) || ''
      }))
    }
  } catch (error) {
    console.error('加载笔记失败:', error)
    noteError.value = '加载学习笔记失败，请重试'
    ElMessage.error('加载学习笔记失败')
  } finally {
    noteLoading.value = false
  }
}

// 加载学习计划
const loadPlans = async () => {
  planLoading.value = true
  planError.value = ''
  try {
    const res = await learningApi.getPlanList({ pageNum: 1, pageSize: 10 })
    if (res.data?.records) {
      plans.value = res.data.records.map(item => ({
        id: item.id,
        name: item.planName,
        goal: item.targetLevel || '',
        startDate: item.startDate?.substring(0, 10) || '',
        endDate: item.endDate?.substring(0, 10) || '',
        progress: item.progress || 0
      }))
    }
  } catch (error) {
    console.error('加载学习计划失败:', error)
    planError.value = '加载学习计划失败，请重试'
    ElMessage.error('加载学习计划失败')
  } finally {
    planLoading.value = false
  }
}

// 加载学习任务
const loadTasks = async () => {
  taskLoading.value = true
  taskError.value = ''
  try {
    const res = await learningApi.getTaskList({ pageNum: 1, pageSize: 20 })
    if (res.data?.records) {
      tasks.value = res.data.records.map(item => ({
        id: item.id,
        title: item.title,
        description: item.description || '',
        priority: item.priority,
        status: item.status,
        progress: item.progress || 0,
        deadline: item.deadline || ''
      }))
    }

    // 加载统计数据
    try {
      const statsRes = await learningApi.getTaskStatistics()
      if (statsRes.data) {
        taskStats.value = {
          total: statsRes.data.total || 0,
          completed: statsRes.data.completed || 0,
          pending: statsRes.data.pending || 0
        }
      }
    } catch (e) {
      // 统计加载失败时根据本地数据计算
      taskStats.value = {
        total: tasks.value.length,
        completed: tasks.value.filter(t => t.status === 'COMPLETED').length,
        pending: tasks.value.filter(t => t.status !== 'COMPLETED').length
      }
    }
  } catch (error) {
    console.error('加载学习任务失败:', error)
    taskError.value = '加载学习任务失败，请重试'
    ElMessage.error('加载学习任务失败')
  } finally {
    taskLoading.value = false
  }
}

// 加载学习报告
const loadReport = async () => {
  reportLoading.value = true
  reportError.value = ''
  try {
    const res = await learningApi.getLearningReport()
    if (res.data) {
      reportData.value = {
        totalStudyTime: res.data.totalStudyTime || 0,
        totalExercises: res.data.totalExercises || 0,
        accuracy: res.data.accuracy || 0,
        consecutiveDays: res.data.consecutiveDays || 0
      }
    }
  } catch (error) {
    console.error('加载学习报告失败:', error)
    reportError.value = '加载学习报告失败，请重试'
    ElMessage.error('加载学习报告失败')
  } finally {
    reportLoading.value = false
  }
}

// 查看计划详情
const viewPlanDetail = (plan) => {
  router.push(`/student/learning/plan/${plan.id}`)
}

// 调整计划
const adjustPlan = async (plan) => {
  ElMessageBox.prompt('请输入新的目标等级', '调整计划', {
    confirmButtonText: '保存',
    cancelButtonText: '取消',
    inputValue: plan.goal
  }).then(async ({ value }) => {
    try {
      await learningApi.updatePlan(plan.id, { targetLevel: value })
      plan.goal = value
      ElMessage.success('计划调整成功')
    } catch (error) {
      ElMessage.error('调整失败，请重试')
    }
  }).catch(() => {})
}

// 删除计划
const deletePlan = async (plan) => {
  try {
    await ElMessageBox.confirm('确定要删除这个学习计划吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await learningApi.deletePlan(plan.id)
    plans.value = plans.value.filter(p => p.id !== plan.id)
    ElMessage.success('计划删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败，请重试')
    }
  }
}

// 监听tab切换，在report tab激活时初始化图表
let chartsInitialized = false
watch(activeTab, async (newTab) => {
  if (newTab === 'report' && !chartsInitialized) {
    await nextTick()
    initCharts()
    chartsInitialized = true
  }
})

// 笔记搜索防抖
let noteSearchTimer = null
watch(noteSearch, (val) => {
  clearTimeout(noteSearchTimer)
  noteSearchTimer = setTimeout(async () => {
    noteLoading.value = true
    try {
      const res = await learningApi.getNoteList({ pageNum: 1, pageSize: 10, keyword: val || null })
      if (res.data?.records) {
        notes.value = res.data.records.map(item => ({
          id: item.id,
          title: item.title,
          summary: item.content?.substring(0, 50) || '',
          tags: item.tags ? item.tags.split(',') : [],
          updateTime: item.updateTime?.substring(0, 10) || ''
        }))
      }
    } catch (e) {
      console.error('搜索笔记失败:', e)
    } finally {
      noteLoading.value = false
    }
  }, 500)
})

onMounted(() => {
  loadResources()
  loadNotes()
  loadPlans()
  loadTasks()
  loadReport()
  // 如果初始tab就是report，初始化图表
  if (activeTab.value === 'report') {
    nextTick(() => {
      initCharts()
      chartsInitialized = true
    })
  }
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (noteSearchTimer) clearTimeout(noteSearchTimer)
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
  distributionChart?.dispose()
})
</script>

<style lang="scss" scoped>
.learning-center {
  .page-header {
    margin-bottom: 24px;

    h2 {
      font-size: 24px;
      font-weight: 600;
      color: var(--text-primary);
      margin-bottom: 8px;
    }

    p {
      color: var(--text-secondary);
    }
  }

  .learning-tabs {
    background: var(--bg-primary);
    border-radius: var(--radius-lg);
    padding: 24px;

    :deep(.el-tabs__header) {
      margin-bottom: 24px;
    }
  }

  .filter-section {
    margin-bottom: 24px;

    .filter-tags {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 12px;

      .filter-label {
        color: var(--text-secondary);
        font-size: 14px;
      }
    }
  }

  .resource-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 20px;
  }

  .resource-card {
    background: var(--bg-secondary);
    border-radius: var(--radius-lg);
    overflow: hidden;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-4px);
      box-shadow: var(--shadow-md);
    }

    .resource-cover {
      position: relative;
      height: 160px;
      overflow: hidden;
      background: var(--bg-tertiary, #f5f5f5);

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .image-placeholder {
        width: 100%;
        height: 100%;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        gap: 8px;
        background: #f5f7fa;
        color: #909399;

        span {
          font-size: 12px;
        }
      }

      .resource-type-badge {
        position: absolute;
        top: 12px;
        left: 12px;
        width: 32px;
        height: 32px;
        background: rgba(0, 0, 0, 0.6);
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;
      }

      .resource-duration {
        position: absolute;
        bottom: 12px;
        right: 12px;
        background: rgba(0, 0, 0, 0.6);
        color: #fff;
        padding: 4px 8px;
        border-radius: 4px;
        font-size: 12px;
      }
    }

    .resource-info {
      padding: 16px;

      .resource-title {
        font-size: 15px;
        font-weight: 600;
        color: var(--text-primary);
        margin-bottom: 8px;
      }

      .resource-desc {
        font-size: 13px;
        color: var(--text-secondary);
        margin-bottom: 12px;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
      }

      .resource-meta {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .view-count {
          display: flex;
          align-items: center;
          gap: 4px;
          font-size: 12px;
          color: var(--text-placeholder);
        }
      }
    }

    .resource-actions {
      padding: 0 16px 16px;
      display: flex;
      gap: 8px;

      .el-button:first-child {
        flex: 1;
      }
    }
  }

  .note-section {
    .note-header {
      display: flex;
      justify-content: space-between;
      margin-bottom: 24px;
    }

    .note-list {
      display: grid;
      gap: 16px;
    }

    .note-card {
      background: var(--bg-secondary);
      border-radius: var(--radius-md);
      padding: 20px;

      .note-content {
        h4 {
          font-size: 16px;
          font-weight: 600;
          color: var(--text-primary);
          margin-bottom: 8px;
        }

        p {
          font-size: 14px;
          color: var(--text-secondary);
          margin-bottom: 12px;
        }

        .note-tags {
          display: flex;
          gap: 8px;
        }
      }

      .note-meta {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-top: 16px;
        padding-top: 16px;
        border-top: 1px solid var(--border-light);
        font-size: 13px;
        color: var(--text-placeholder);
      }
    }
  }

  .task-section {
    .task-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 24px;

      .task-stats {
        display: flex;
        gap: 32px;

        .stat-item {
          .stat-value {
            font-size: 24px;
            font-weight: 700;
            color: var(--primary-color);
          }

          .stat-label {
            font-size: 13px;
            color: var(--text-secondary);
          }
        }
      }
    }

    .task-list {
      .task-item {
        display: flex;
        align-items: center;
        gap: 16px;
        padding: 16px;
        background: var(--bg-secondary);
        border-radius: var(--radius-md);
        margin-bottom: 12px;

        .task-info {
          flex: 1;

          .task-title {
            font-size: 14px;
            color: var(--text-primary);
            margin-bottom: 8px;

            &.completed {
              text-decoration: line-through;
              color: var(--text-placeholder);
            }
          }

          .task-meta {
            display: flex;
            align-items: center;
            gap: 12px;

            .task-deadline {
              font-size: 12px;
              color: var(--text-secondary);
            }
          }
        }
      }
    }
  }

  .plan-section {
    .plan-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 24px;

      h3 {
        font-size: 16px;
        font-weight: 600;
        color: var(--text-primary);
      }
    }

    .plan-list {
      display: grid;
      gap: 16px;
    }

    .plan-card {
      display: flex;
      align-items: center;
      gap: 24px;
      padding: 24px;
      background: var(--bg-secondary);
      border-radius: var(--radius-lg);

      .plan-info {
        flex: 1;

        h4 {
          font-size: 16px;
          font-weight: 600;
          color: var(--text-primary);
          margin-bottom: 8px;
        }

        p {
          font-size: 14px;
          color: var(--text-secondary);
          margin-bottom: 8px;
        }

        .plan-time {
          font-size: 13px;
          color: var(--text-placeholder);
        }
      }

      .plan-actions {
        display: flex;
        flex-direction: column;
        gap: 8px;
      }
    }
  }

  .report-section {
    .report-overview {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 20px;
      margin-bottom: 24px;

      @media (max-width: 1200px) {
        grid-template-columns: repeat(2, 1fr);
      }

      .overview-card {
        display: flex;
        align-items: center;
        gap: 16px;
        padding: 20px;
        background: var(--bg-secondary);
        border-radius: var(--radius-lg);

        .overview-icon {
          width: 56px;
          height: 56px;
          border-radius: var(--radius-md);
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          display: flex;
          align-items: center;
          justify-content: center;
          color: #fff;
        }

        .overview-info {
          .overview-value {
            font-size: 24px;
            font-weight: 700;
            color: var(--text-primary);
          }

          .overview-label {
            font-size: 13px;
            color: var(--text-secondary);
          }
        }
      }
    }

    .report-charts {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 20px;

      @media (max-width: 992px) {
        grid-template-columns: 1fr;
      }

      .chart-card {
        background: var(--bg-secondary);
        border-radius: var(--radius-lg);
        padding: 20px;

        h4 {
          font-size: 15px;
          font-weight: 600;
          color: var(--text-primary);
          margin-bottom: 16px;
        }

        .chart-container {
          height: 280px;
        }
      }
    }
  }
}
</style>
