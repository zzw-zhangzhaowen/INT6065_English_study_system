<template>
  <div class="system-monitor">
    <div class="page-header">
      <h2>系统监控</h2>
      <div class="header-actions">
        <el-tag type="success" size="large">系统运行正常</el-tag>
        <span class="update-time">最后更新：{{ lastUpdateTime }}</span>
        <el-button :icon="Refresh" circle @click="refreshAll" :loading="loading" />
      </div>
    </div>

    <!-- 核心指标卡片 -->
    <div class="metrics-row">
      <div class="metric-card">
        <div class="metric-header">
          <h3>CPU 使用率</h3>
          <span class="metric-value" :class="getStatusClass(metrics.cpuUsage)">{{ metrics.cpuUsage }}%</span>
        </div>
        <el-progress :percentage="metrics.cpuUsage" :stroke-width="12" :color="getProgressColor(metrics.cpuUsage)" />
        <div class="metric-detail">处理器核心数：{{ cpuInfo.availableProcessors || '-' }}</div>
      </div>
      <div class="metric-card">
        <div class="metric-header">
          <h3>JVM 内存使用率</h3>
          <span class="metric-value" :class="getStatusClass(metrics.memoryUsage)">{{ metrics.memoryUsage }}%</span>
        </div>
        <el-progress :percentage="metrics.memoryUsage" :stroke-width="12" :color="getProgressColor(metrics.memoryUsage)" />
        <div class="metric-detail">{{ memoryInfo.used || '-' }} / {{ memoryInfo.max || '-' }}</div>
      </div>
      <div class="metric-card">
        <div class="metric-header">
          <h3>磁盘使用率</h3>
          <span class="metric-value" :class="getStatusClass(diskOverview.usagePercent)">{{ diskOverview.usagePercent }}%</span>
        </div>
        <el-progress :percentage="diskOverview.usagePercent" :stroke-width="12" :color="getProgressColor(diskOverview.usagePercent)" />
        <div class="metric-detail">{{ diskOverview.usedSpace || '-' }} / {{ diskOverview.totalSpace || '-' }}</div>
      </div>
      <div class="metric-card">
        <div class="metric-header">
          <h3>活跃线程数</h3>
          <span class="metric-value">{{ metrics.threadCount }}</span>
        </div>
        <el-progress :percentage="Math.min(metrics.threadCount, 100)" :stroke-width="12" color="#409eff" />
        <div class="metric-detail">峰值：{{ jvmThread.peak || '-' }} / 守护线程：{{ jvmThread.daemon || '-' }}</div>
      </div>
    </div>

    <!-- 实时监控图表 -->
    <div class="realtime-charts">
      <div class="chart-card">
        <h3>CPU 使用率趋势</h3>
        <div class="chart-container" ref="cpuChartRef"></div>
      </div>
      <div class="chart-card">
        <h3>内存使用率趋势</h3>
        <div class="chart-container" ref="memoryChartRef"></div>
      </div>
    </div>

    <!-- JVM 信息 + Redis 信息 -->
    <div class="info-row">
      <div class="info-card">
        <h3>JVM 信息</h3>
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="JVM 名称">{{ jvmInfo.name || '-' }}</el-descriptions-item>
          <el-descriptions-item label="JVM 版本">{{ jvmInfo.version || '-' }}</el-descriptions-item>
          <el-descriptions-item label="运行时长">{{ jvmInfo.uptime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="启动时间">{{ formatDate(jvmInfo.startTime) }}</el-descriptions-item>
          <el-descriptions-item label="堆内存(已用/最大)">{{ jvmHeap.used || '-' }} / {{ jvmHeap.max || '-' }}</el-descriptions-item>
          <el-descriptions-item label="堆内存使用率">
            <el-progress :percentage="jvmHeap.usagePercent || 0" :stroke-width="8" :color="getProgressColor(jvmHeap.usagePercent || 0)" style="width: 120px" />
          </el-descriptions-item>
          <el-descriptions-item label="非堆内存(已用)">{{ jvmNonHeap.used || '-' }}</el-descriptions-item>
          <el-descriptions-item label="非堆内存(已提交)">{{ jvmNonHeap.committed || '-' }}</el-descriptions-item>
        </el-descriptions>
        <div v-if="jvmGc.length > 0" class="gc-info">
          <h4>GC 信息</h4>
          <el-tag v-for="gc in jvmGc" :key="gc.name" size="small" class="gc-tag">
            {{ gc.name }}：{{ gc.collectionCount }}次 / {{ gc.collectionTime }}
          </el-tag>
        </div>
      </div>

      <div class="info-card">
        <h3>Redis 信息</h3>
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="连接状态">
            <el-tag :type="redisInfo.status === 'connected' ? 'success' : 'danger'" size="small">
              {{ redisInfo.status === 'connected' ? '已连接' : '未连接' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="Redis 版本">{{ redisInfo.version || '-' }}</el-descriptions-item>
          <el-descriptions-item label="运行模式">{{ redisInfo.mode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="运行天数">{{ redisInfo.uptimeInDays || '-' }} 天</el-descriptions-item>
          <el-descriptions-item label="已用内存">{{ redisInfo.usedMemory || '-' }}</el-descriptions-item>
          <el-descriptions-item label="内存峰值">{{ redisInfo.usedMemoryPeak || '-' }}</el-descriptions-item>
          <el-descriptions-item label="连接客户端数">{{ redisInfo.connectedClients || '-' }}</el-descriptions-item>
          <el-descriptions-item label="总连接数">{{ redisInfo.totalConnectionsReceived || '-' }}</el-descriptions-item>
        </el-descriptions>
        <div v-if="redisInfo.error" class="redis-error">
          <el-alert :title="redisInfo.error" type="error" :closable="false" show-icon />
        </div>
      </div>
    </div>

    <!-- 系统信息 + 磁盘详情 -->
    <div class="info-row">
      <div class="info-card">
        <h3>系统信息</h3>
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="操作系统">{{ systemInfo.osName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="系统版本">{{ systemInfo.osVersion || '-' }}</el-descriptions-item>
          <el-descriptions-item label="系统架构">{{ systemInfo.osArch || '-' }}</el-descriptions-item>
          <el-descriptions-item label="Java 版本">{{ systemInfo.javaVersion || '-' }}</el-descriptions-item>
          <el-descriptions-item label="Java 厂商">{{ systemInfo.javaVendor || '-' }}</el-descriptions-item>
          <el-descriptions-item label="Java Home">{{ systemInfo.javaHome || '-' }}</el-descriptions-item>
          <el-descriptions-item label="工作目录" :span="2">{{ systemInfo.userDir || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <div class="info-card">
        <h3>磁盘信息</h3>
        <el-table :data="diskList" size="small" style="width: 100%">
          <el-table-column prop="path" label="盘符" width="80" />
          <el-table-column prop="total" label="总容量" width="100" />
          <el-table-column prop="used" label="已使用" width="100" />
          <el-table-column prop="free" label="可用" width="100" />
          <el-table-column label="使用率" min-width="140">
            <template #default="{ row }">
              <el-progress :percentage="row.usagePercent || 0" :stroke-width="8" :color="getProgressColor(row.usagePercent || 0)" />
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <!-- 最近异常操作 -->
    <div class="alert-section">
      <div class="section-header">
        <h3>最近异常操作</h3>
        <el-button type="primary" link @click="$router.push('/admin/log')">查看全部日志</el-button>
      </div>
      <el-table :data="recentErrors" style="width: 100%" size="small">
        <el-table-column prop="createTime" label="时间" width="180" />
        <el-table-column prop="operation" label="操作描述" min-width="150" show-overflow-tooltip />
        <el-table-column prop="username" label="操作用户" width="120" />
        <el-table-column prop="method" label="请求方法" min-width="200" show-overflow-tooltip />
        <el-table-column prop="errorMsg" label="错误信息" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="error-text">{{ row.errorMsg || '-' }}</span>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { monitorApi } from '@/api/monitor'
import { logApi } from '@/api/log'

const loading = ref(false)
const lastUpdateTime = ref('')
const cpuChartRef = ref()
const memoryChartRef = ref()
let cpuChart = null
let memoryChart = null
let refreshTimer = null
let realtimeTimer = null

// 实时指标
const metrics = reactive({
  cpuUsage: 0,
  memoryUsage: 0,
  threadCount: 0
})

// CPU 信息
const cpuInfo = reactive({
  availableProcessors: 0,
  systemLoadAverage: 0,
  processCpuLoad: '-',
  systemCpuLoad: '-'
})

// 内存信息
const memoryInfo = reactive({
  total: '-',
  used: '-',
  free: '-',
  max: '-',
  usagePercent: 0
})

// 磁盘概览
const diskOverview = reactive({
  totalSpace: '-',
  usedSpace: '-',
  freeSpace: '-',
  usagePercent: 0
})
const diskList = ref([])

// JVM 信息
const jvmInfo = reactive({ name: '', version: '', uptime: '', startTime: null })
const jvmHeap = reactive({ used: '-', max: '-', usagePercent: 0 })
const jvmNonHeap = reactive({ used: '-', committed: '-' })
const jvmThread = reactive({ active: 0, daemon: 0, peak: 0, totalStarted: 0 })
const jvmGc = ref([])

// Redis 信息
const redisInfo = reactive({
  status: 'disconnected',
  version: '',
  mode: '',
  connectedClients: '',
  usedMemory: '',
  usedMemoryPeak: '',
  uptimeInDays: '',
  totalConnectionsReceived: '',
  error: ''
})

// 系统信息
const systemInfo = reactive({
  osName: '',
  osVersion: '',
  osArch: '',
  javaVersion: '',
  javaVendor: '',
  javaHome: '',
  userDir: ''
})

// 最近异常操作
const recentErrors = ref([])

// 图表历史数据
const chartHistory = reactive({
  timestamps: [],
  cpuData: [],
  memoryData: []
})
const MAX_CHART_POINTS = 30

// 格式化日期
const formatDate = (val) => {
  if (!val) return '-'
  const d = new Date(val)
  if (isNaN(d.getTime())) return String(val)
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

const getStatusClass = (value) => {
  if (value >= 80) return 'danger'
  if (value >= 60) return 'warning'
  return 'normal'
}

const getProgressColor = (value) => {
  if (value >= 80) return '#ff4d4f'
  if (value >= 60) return '#faad14'
  return '#52c41a'
}

// 加载 CPU 信息
const loadCpuInfo = async () => {
  try {
    const res = await monitorApi.getCpuInfo()
    if (res.data) {
      Object.assign(cpuInfo, res.data)
    }
  } catch (e) {
    console.error('加载CPU信息失败:', e)
  }
}

// 加载内存信息
const loadMemoryInfo = async () => {
  try {
    const res = await monitorApi.getMemoryInfo()
    if (res.data) {
      Object.assign(memoryInfo, res.data)
    }
  } catch (e) {
    console.error('加载内存信息失败:', e)
  }
}

// 加载磁盘信息
const loadDiskInfo = async () => {
  try {
    const res = await monitorApi.getDiskInfo()
    if (res.data) {
      diskOverview.totalSpace = res.data.totalSpace || '-'
      diskOverview.usedSpace = res.data.usedSpace || '-'
      diskOverview.freeSpace = res.data.freeSpace || '-'
      diskOverview.usagePercent = res.data.usagePercent || 0
      diskList.value = res.data.disks || []
    }
  } catch (e) {
    console.error('加载磁盘信息失败:', e)
  }
}

// 加载 JVM 信息
const loadJvmInfo = async () => {
  try {
    const res = await monitorApi.getJvmInfo()
    if (res.data) {
      jvmInfo.name = res.data.name || ''
      jvmInfo.version = res.data.version || ''
      jvmInfo.uptime = res.data.uptime || ''
      jvmInfo.startTime = res.data.startTime || null

      if (res.data.heapMemory) {
        Object.assign(jvmHeap, res.data.heapMemory)
      }
      if (res.data.nonHeapMemory) {
        Object.assign(jvmNonHeap, res.data.nonHeapMemory)
      }
      if (res.data.thread) {
        Object.assign(jvmThread, res.data.thread)
      }
      if (res.data.gc) {
        jvmGc.value = res.data.gc
      }
    }
  } catch (e) {
    console.error('加载JVM信息失败:', e)
  }
}

// 加载 Redis 信息
const loadRedisInfo = async () => {
  try {
    const res = await monitorApi.getRedisInfo()
    if (res.data) {
      Object.assign(redisInfo, res.data)
    }
  } catch (e) {
    console.error('加载Redis信息失败:', e)
  }
}

// 加载系统信息
const loadSystemInfo = async () => {
  try {
    const res = await monitorApi.getSystemInfo()
    if (res.data) {
      Object.assign(systemInfo, res.data)
    }
  } catch (e) {
    console.error('加载系统信息失败:', e)
  }
}

// 加载最近异常操作（status=0 表示失败）
const loadRecentErrors = async () => {
  try {
    const res = await logApi.getLogs({ pageNum: 1, pageSize: 10, status: 0 })
    if (res.data?.records) {
      recentErrors.value = res.data.records
    }
  } catch (e) {
    console.error('加载异常操作失败:', e)
  }
}

// 加载实时指标并更新图表
const loadRealtimeData = async () => {
  try {
    const res = await monitorApi.getRealtimeData()
    if (res.data) {
      metrics.cpuUsage = res.data.cpuUsage || 0
      metrics.memoryUsage = res.data.memoryUsage || 0
      metrics.threadCount = res.data.threadCount || 0

      // 更新图表数据
      const now = new Date()
      const timeLabel = `${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}:${String(now.getSeconds()).padStart(2, '0')}`

      chartHistory.timestamps.push(timeLabel)
      chartHistory.cpuData.push(metrics.cpuUsage)
      chartHistory.memoryData.push(metrics.memoryUsage)

      // 保持最多 MAX_CHART_POINTS 个数据点
      if (chartHistory.timestamps.length > MAX_CHART_POINTS) {
        chartHistory.timestamps.shift()
        chartHistory.cpuData.shift()
        chartHistory.memoryData.shift()
      }

      updateCharts()
    }
  } catch (e) {
    console.error('加载实时数据失败:', e)
  }
}

// 更新图表
const updateCharts = () => {
  if (cpuChart) {
    cpuChart.setOption({
      xAxis: { data: chartHistory.timestamps },
      series: [{ data: chartHistory.cpuData }]
    })
  }
  if (memoryChart) {
    memoryChart.setOption({
      xAxis: { data: chartHistory.timestamps },
      series: [{ data: chartHistory.memoryData }]
    })
  }
}

// 初始化图表
const initCharts = () => {
  const commonOption = (name, color) => ({
    tooltip: {
      trigger: 'axis',
      formatter: (params) => {
        const p = params[0]
        return `${p.axisValue}<br/>${p.seriesName}：${p.value}%`
      }
    },
    grid: { top: 20, right: 20, bottom: 30, left: 50 },
    xAxis: {
      type: 'category',
      data: chartHistory.timestamps,
      axisLabel: { fontSize: 11 }
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 100,
      axisLabel: { formatter: '{value}%' }
    },
    series: [{
      name,
      type: 'line',
      smooth: true,
      symbol: 'none',
      lineStyle: { width: 2, color },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: color + '40' },
          { offset: 1, color: color + '05' }
        ])
      },
      data: name === 'CPU' ? chartHistory.cpuData : chartHistory.memoryData
    }]
  })

  if (cpuChartRef.value) {
    cpuChart = echarts.init(cpuChartRef.value)
    cpuChart.setOption(commonOption('CPU', '#ff6b6b'))
  }
  if (memoryChartRef.value) {
    memoryChart = echarts.init(memoryChartRef.value)
    memoryChart.setOption(commonOption('内存', '#4ecdc4'))
  }
}

const handleResize = () => {
  cpuChart?.resize()
  memoryChart?.resize()
}

// 刷新所有数据
const refreshAll = async () => {
  loading.value = true
  try {
    await Promise.all([
      loadCpuInfo(),
      loadMemoryInfo(),
      loadDiskInfo(),
      loadJvmInfo(),
      loadRedisInfo(),
      loadSystemInfo(),
      loadRealtimeData(),
      loadRecentErrors()
    ])
    lastUpdateTime.value = new Date().toLocaleString()
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  initCharts()
  window.addEventListener('resize', handleResize)
  await refreshAll()

  // 每 5 秒刷新实时数据（图表）
  realtimeTimer = setInterval(loadRealtimeData, 5000)
  // 每 30 秒刷新全量数据
  refreshTimer = setInterval(async () => {
    await Promise.all([
      loadCpuInfo(),
      loadMemoryInfo(),
      loadDiskInfo(),
      loadJvmInfo(),
      loadRedisInfo(),
      loadRecentErrors()
    ])
    lastUpdateTime.value = new Date().toLocaleString()
  }, 30000)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  cpuChart?.dispose()
  memoryChart?.dispose()
  if (refreshTimer) clearInterval(refreshTimer)
  if (realtimeTimer) clearInterval(realtimeTimer)
})
</script>

<style lang="scss" scoped>
.system-monitor {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;

    h2 {
      font-size: 24px;
      font-weight: 600;
      color: var(--text-primary);
    }

    .header-actions {
      display: flex;
      align-items: center;
      gap: 16px;

      .update-time {
        font-size: 13px;
        color: var(--text-secondary);
      }
    }
  }

  .metrics-row {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
    margin-bottom: 24px;

    @media (max-width: 1200px) {
      grid-template-columns: repeat(2, 1fr);
    }

    .metric-card {
      background: var(--bg-primary);
      border-radius: var(--radius-lg);
      padding: 24px;

      .metric-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;

        h3 {
          font-size: 14px;
          font-weight: 500;
          color: var(--text-secondary);
        }

        .metric-value {
          font-size: 24px;
          font-weight: 700;

          &.normal { color: var(--el-color-success); }
          &.warning { color: var(--el-color-warning); }
          &.danger { color: var(--el-color-danger); }
        }
      }

      .metric-detail {
        margin-top: 8px;
        font-size: 12px;
        color: var(--text-secondary);
      }
    }
  }

  .realtime-charts {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 24px;
    margin-bottom: 24px;

    @media (max-width: 992px) {
      grid-template-columns: 1fr;
    }

    .chart-card {
      background: var(--bg-primary);
      border-radius: var(--radius-lg);
      padding: 24px;

      h3 {
        font-size: 16px;
        font-weight: 600;
        color: var(--text-primary);
        margin-bottom: 16px;
      }

      .chart-container {
        height: 260px;
      }
    }
  }

  .info-row {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 24px;
    margin-bottom: 24px;

    @media (max-width: 992px) {
      grid-template-columns: 1fr;
    }

    .info-card {
      background: var(--bg-primary);
      border-radius: var(--radius-lg);
      padding: 24px;

      h3 {
        font-size: 16px;
        font-weight: 600;
        color: var(--text-primary);
        margin-bottom: 16px;
      }

      h4 {
        font-size: 14px;
        font-weight: 500;
        color: var(--text-primary);
        margin: 16px 0 8px;
      }

      .gc-tag {
        margin-right: 8px;
        margin-bottom: 4px;
      }

      .redis-error {
        margin-top: 12px;
      }
    }
  }

  .alert-section {
    background: var(--bg-primary);
    border-radius: var(--radius-lg);
    padding: 24px;

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;

      h3 {
        font-size: 16px;
        font-weight: 600;
        color: var(--text-primary);
      }
    }

    .error-text {
      color: var(--el-color-danger);
    }
  }
}
</style>
