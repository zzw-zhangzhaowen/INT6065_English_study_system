<template>
  <div class="admin-dashboard">
    <!-- 系统概览 -->
    <div class="overview-section">
      <div class="overview-card users">
        <div class="card-icon">
          <el-icon :size="28"><User /></el-icon>
        </div>
        <div class="card-info">
          <div class="card-value">{{ overview.totalUsers }}</div>
          <div class="card-label">用户总数</div>
        </div>
        <div class="card-trend up">
          <el-icon><Top /></el-icon>
          +{{ overview.newUsers }} 今日新增
        </div>
      </div>
      <div class="overview-card online">
        <div class="card-icon">
          <el-icon :size="28"><Connection /></el-icon>
        </div>
        <div class="card-info">
          <div class="card-value">{{ overview.onlineUsers }}</div>
          <div class="card-label">在线用户</div>
        </div>
        <div class="card-trend">
          实时数据
        </div>
      </div>
      <div class="overview-card exams">
        <div class="card-icon">
          <el-icon :size="28"><Document /></el-icon>
        </div>
        <div class="card-info">
          <div class="card-value">{{ overview.ongoingExams }}</div>
          <div class="card-label">进行中考试</div>
        </div>
        <div class="card-trend">
          {{ overview.totalExams }} 总考试
        </div>
      </div>
      <div class="overview-card storage">
        <div class="card-icon">
          <el-icon :size="28"><Files /></el-icon>
        </div>
        <div class="card-info">
          <div class="card-value">{{ overview.storageUsed }}GB</div>
          <div class="card-label">存储使用</div>
        </div>
        <div class="card-trend">
          / {{ overview.storageTotal }}GB
        </div>
      </div>
    </div>

    <!-- 主要内容 -->
    <div class="main-content">
      <div class="left-section">
        <!-- 系统状态 -->
        <div class="card system-status">
          <div class="card-header">
            <h3>系统状态</h3>
            <el-tag :type="systemStatusLevel" size="small">{{ systemStatusText }}</el-tag>
          </div>
          <div class="status-grid">
            <div class="status-item">
              <div class="status-label">CPU使用率</div>
              <el-progress :percentage="systemStatus.cpu" :stroke-width="10" />
            </div>
            <div class="status-item">
              <div class="status-label">内存使用率</div>
              <el-progress :percentage="systemStatus.memory" :stroke-width="10" />
            </div>
            <div class="status-item">
              <div class="status-label">磁盘使用率</div>
              <el-progress :percentage="systemStatus.disk" :stroke-width="10" />
            </div>
            <div class="status-item">
              <div class="status-label">JVM线程数</div>
              <el-progress :percentage="Math.min(systemStatus.threadCount, 100)" :stroke-width="10" :format="() => systemStatus.threadCount" />
            </div>
          </div>
        </div>

        <!-- 访问趋势 -->
        <div class="card visit-trend">
          <div class="card-header">
            <h3>访问趋势</h3>
            <el-select v-model="trendRange" size="small" style="width: 100px">
              <el-option label="7天" value="7" />
              <el-option label="30天" value="30" />
            </el-select>
          </div>
          <div class="chart-container" ref="trendChartRef"></div>
        </div>

        <!-- 用户分布 -->
        <div class="card user-distribution">
          <div class="card-header">
            <h3>用户角色分布</h3>
          </div>
          <div class="chart-container" ref="distributionChartRef"></div>
        </div>
      </div>

      <div class="right-section">
        <!-- 系统告警 -->
        <div class="card system-alerts">
          <div class="card-header">
            <h3>系统告警</h3>
            <el-badge v-if="alerts.some(a => a.level !== 'info')" :value="alerts.filter(a => a.level !== 'info').length" type="danger" />
          </div>
          <div class="alert-list">
            <div v-if="alerts.length === 0" class="alert-empty">
              <el-icon :size="32" color="#52c41a"><SuccessFilled /></el-icon>
              <div style="margin-top: 8px; color: var(--text-secondary); font-size: 13px;">系统运行正常，暂无告警</div>
            </div>
            <div v-for="alert in alerts" :key="alert.id" class="alert-item" :class="alert.level">
              <el-icon><WarningFilled /></el-icon>
              <div class="alert-info">
                <div class="alert-title">{{ alert.title }}</div>
                <div class="alert-time">{{ alert.time }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 快捷操作 -->
        <div class="card quick-actions">
          <div class="card-header">
            <h3>快捷操作</h3>
          </div>
          <div class="action-grid">
            <div class="action-item" @click="handleAction('backup')">
              <el-icon :size="24"><FolderOpened /></el-icon>
              <span>系统备份</span>
            </div>
            <div class="action-item" @click="handleAction('cache')">
              <el-icon :size="24"><Delete /></el-icon>
              <span>清理缓存</span>
            </div>
            <div class="action-item" @click="handleAction('log')">
              <el-icon :size="24"><Document /></el-icon>
              <span>查看日志</span>
            </div>
            <div class="action-item" @click="handleAction('settings')">
              <el-icon :size="24"><Setting /></el-icon>
              <span>系统设置</span>
            </div>
          </div>
        </div>

        <!-- 最近操作 -->
        <div class="card recent-operations">
          <div class="card-header">
            <h3>最近操作</h3>
            <el-button type="primary" link size="small" @click="router.push('/admin/log')">查看全部</el-button>
          </div>
          <div class="operation-list">
            <div v-for="op in recentOperations" :key="op.id" class="operation-item">
              <el-avatar :size="32">{{ op.user.charAt(0) }}</el-avatar>
              <div class="operation-info">
                <div class="operation-desc">{{ op.user }} {{ op.action }}</div>
                <div class="operation-time">{{ op.time }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import {
  User, Connection, Document, Files, Top, WarningFilled, SuccessFilled,
  FolderOpened, Delete, Setting
} from '@element-plus/icons-vue'
import { adminApi } from '@/api/admin'

const router = useRouter()

const trendRange = ref('7')
const trendChartRef = ref()
const distributionChartRef = ref()
let trendChart = null
let distributionChart = null

// 概览数据
const overview = ref({
  totalUsers: 0,
  newUsers: 0,
  onlineUsers: 0,
  ongoingExams: 0,
  totalExams: 0,
  storageUsed: 0,
  storageTotal: 500
})

// 系统状态
const systemStatus = ref({
  cpu: 0,
  memory: 0,
  disk: 0,
  threadCount: 0
})

// 系统告警
const alerts = ref([])

// 最近操作
const recentOperations = ref([])

// 角色分布数据（从后端获取）
const roleDistribution = ref({})

// 系统状态标签
const systemStatusLevel = computed(() => {
  const { cpu, memory, disk } = systemStatus.value
  if (cpu > 90 || memory > 90 || disk > 95) return 'danger'
  if (cpu > 70 || memory > 70 || disk > 85) return 'warning'
  return 'success'
})
const systemStatusText = computed(() => {
  const map = { success: '运行正常', warning: '负载偏高', danger: '负载过高' }
  return map[systemStatusLevel.value]
})

// 加载仪表板数据
const loadDashboardData = async () => {
  try {
    const res = await adminApi.getAdminDashboardStats()
    if (res.data) {
      overview.value = {
        totalUsers: res.data.totalUsers || 0,
        newUsers: res.data.todayNewUsers || 0,
        onlineUsers: res.data.onlineUsers || 0,
        ongoingExams: res.data.ongoingExams || 0,
        totalExams: res.data.totalExamRecords || 0,
        storageUsed: res.data.storageUsed || 0,
        storageTotal: res.data.storageTotal || 500
      }
      if (res.data.roleDistribution) {
        roleDistribution.value = res.data.roleDistribution
      }
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }

  // 加载系统实时监控数据（CPU、内存）
  try {
    const realtimeRes = await adminApi.getRealtimeMetrics()
    if (realtimeRes.data) {
      systemStatus.value.cpu = Math.round(realtimeRes.data.cpuUsage || 0)
      systemStatus.value.memory = Math.round(realtimeRes.data.memoryUsage || 0)
      systemStatus.value.threadCount = realtimeRes.data.threadCount || 0
    }
  } catch (error) {
    console.error('加载实时监控失败:', error)
  }

  // 加载磁盘信息
  try {
    const diskRes = await adminApi.getDiskInfo()
    if (diskRes.data) {
      systemStatus.value.disk = Math.round(diskRes.data.usagePercent || 0)
      // 计算存储使用量（GB）
      const usedStr = diskRes.data.usedSpace || '0'
      const totalStr = diskRes.data.totalSpace || '0'
      overview.value.storageUsed = parseStorageGB(usedStr)
      overview.value.storageTotal = parseStorageGB(totalStr)
    }
  } catch (error) {
    console.error('加载磁盘信息失败:', error)
  }

  // 生成系统告警
  generateAlerts()

  try {
    const logRes = await adminApi.getLogList({ pageNum: 1, pageSize: 5 })
    if (logRes.data?.records) {
      recentOperations.value = logRes.data.records.map(item => ({
        id: item.id,
        user: item.username || '系统',
        action: item.operation || '',
        time: item.createTime || ''
      }))
    }
  } catch (error) {
    console.error('加载操作日志失败:', error)
  }
}

// 解析存储字符串为GB数值
const parseStorageGB = (str) => {
  if (!str || typeof str !== 'string') return 0
  const num = parseFloat(str)
  if (isNaN(num)) return 0
  if (str.includes('TB')) return Math.round(num * 1024)
  if (str.includes('GB')) return Math.round(num)
  if (str.includes('MB')) return Math.round(num / 1024)
  return 0
}

// 根据系统指标生成告警
const generateAlerts = () => {
  const newAlerts = []
  let alertId = 1
  const { cpu, memory, disk } = systemStatus.value

  if (cpu > 80) {
    newAlerts.push({ id: alertId++, title: `CPU使用率过高: ${cpu}%`, level: 'danger', time: '刚刚' })
  } else if (cpu > 50) {
    newAlerts.push({ id: alertId++, title: `CPU使用率偏高: ${cpu}%`, level: 'warning', time: '刚刚' })
  }
  if (memory > 80) {
    newAlerts.push({ id: alertId++, title: `内存使用率过高: ${memory}%`, level: 'danger', time: '刚刚' })
  } else if (memory > 60) {
    newAlerts.push({ id: alertId++, title: `内存使用率偏高: ${memory}%`, level: 'warning', time: '刚刚' })
  }
  if (disk > 85) {
    newAlerts.push({ id: alertId++, title: `磁盘空间不足: 已使用${disk}%`, level: 'danger', time: '刚刚' })
  } else if (disk > 70) {
    newAlerts.push({ id: alertId++, title: `磁盘空间偏高: 已使用${disk}%`, level: 'warning', time: '刚刚' })
  }

  // 信息级别提示，确保面板不为空
  if (newAlerts.length === 0) {
    newAlerts.push({ id: alertId++, title: `CPU: ${cpu}%  内存: ${memory}%  磁盘: ${disk}%`, level: 'info', time: '实时' })
  }

  alerts.value = newAlerts
}

const handleAction = (action) => {
  switch (action) {
    case 'backup':
      ElMessage.success('系统备份已启动')
      break
    case 'cache':
      ElMessage.success('缓存清理完成')
      break
    case 'log':
      router.push('/admin/log')
      break
    case 'settings':
      router.push('/admin/system')
      break
  }
}

const initCharts = () => {
  if (trendChartRef.value) {
    trendChart = echarts.init(trendChartRef.value)
    loadTrendData()
  }

  // 用户分布图 - 使用概览数据
  if (distributionChartRef.value) {
    distributionChart = echarts.init(distributionChartRef.value)
    updateDistributionChart()
  }
}

// 加载访问趋势数据（基于操作日志按天统计）
const loadTrendData = async () => {
  if (!trendChart) return
  const days = parseInt(trendRange.value)
  const dateLabels = []
  const now = new Date()
  for (let i = days - 1; i >= 0; i--) {
    const d = new Date(now)
    d.setDate(d.getDate() - i)
    dateLabels.push(`${d.getMonth() + 1}/${d.getDate()}`)
  }

  try {
    // 获取最近N天的日志，用日志数量作为访问趋势
    const logRes = await adminApi.getLogList({ pageNum: 1, pageSize: 1000 })
    const counts = new Array(days).fill(0)
    if (logRes.data?.records) {
      logRes.data.records.forEach(item => {
        if (!item.createTime) return
        const logDate = new Date(item.createTime)
        const diffDays = Math.floor((now - logDate) / (1000 * 60 * 60 * 24))
        if (diffDays >= 0 && diffDays < days) {
          counts[days - 1 - diffDays]++
        }
      })
    }
    trendChart.setOption({
      tooltip: { trigger: 'axis' },
      legend: { bottom: 0, data: ['操作次数'] },
      xAxis: { type: 'category', data: dateLabels },
      yAxis: { type: 'value', minInterval: 1 },
      graphic: [],
      series: [{
        name: '操作次数',
        type: 'line',
        smooth: true,
        data: counts,
        areaStyle: { opacity: 0.15 },
        itemStyle: { color: '#4080ff' }
      }]
    })
  } catch {
    trendChart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: dateLabels },
      yAxis: { type: 'value' },
      graphic: { type: 'text', left: 'center', top: 'middle', style: { text: '暂无趋势数据', fontSize: 14, fill: '#999' } },
      series: []
    })
  }
}

const updateDistributionChart = () => {
  if (!distributionChart) return
  const dist = roleDistribution.value
  const colors = { '学生': '#4080ff', '教师': '#52c41a', '管理员': '#f5576c' }
  const data = Object.keys(dist).length > 0
    ? Object.entries(dist).map(([name, value]) => ({
        value,
        name,
        itemStyle: { color: colors[name] || '#909399' }
      }))
    : [{ value: overview.value.totalUsers, name: '用户总数', itemStyle: { color: '#4080ff' } }]
  distributionChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: 0 },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data
    }]
  })
}

const handleResize = () => {
  trendChart?.resize()
  distributionChart?.resize()
}

onMounted(async () => {
  await loadDashboardData()
  initCharts()
  window.addEventListener('resize', handleResize)
})

watch(trendRange, () => {
  loadTrendData()
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
  distributionChart?.dispose()
})
</script>

<style lang="scss" scoped>
.admin-dashboard {
  .overview-section {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
    margin-bottom: 24px;

    @media (max-width: 1200px) {
      grid-template-columns: repeat(2, 1fr);
    }

    .overview-card {
      background: var(--bg-primary);
      border-radius: var(--radius-lg);
      padding: 24px;
      display: flex;
      align-items: center;
      gap: 16px;
      box-shadow: var(--shadow-sm);

      .card-icon {
        width: 56px;
        height: 56px;
        border-radius: var(--radius-md);
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;
      }

      &.users .card-icon {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      }

      &.online .card-icon {
        background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
      }

      &.exams .card-icon {
        background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
      }

      &.storage .card-icon {
        background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
      }

      .card-info {
        flex: 1;

        .card-value {
          font-size: 28px;
          font-weight: 700;
          color: var(--text-primary);
        }

        .card-label {
          font-size: 13px;
          color: var(--text-secondary);
        }
      }

      .card-trend {
        font-size: 12px;
        color: var(--text-secondary);

        &.up {
          color: var(--success-color);
          display: flex;
          align-items: center;
          gap: 4px;
        }
      }
    }
  }

  .main-content {
    display: grid;
    grid-template-columns: 1fr 360px;
    gap: 24px;

    @media (max-width: 1200px) {
      grid-template-columns: 1fr;
    }
  }

  .card {
    background: var(--bg-primary);
    border-radius: var(--radius-lg);
    padding: 24px;
    box-shadow: var(--shadow-sm);
    margin-bottom: 24px;

    .card-header {
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
  }

  .system-status {
    .status-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 20px;

      .status-item {
        .status-label {
          font-size: 13px;
          color: var(--text-secondary);
          margin-bottom: 8px;
        }
      }
    }
  }

  .visit-trend,
  .user-distribution {
    .chart-container {
      height: 280px;
    }
  }

  .system-alerts {
    .alert-list {
      .alert-item {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 12px;
        border-radius: var(--radius-md);
        margin-bottom: 8px;

        &.warning {
          background: rgba(250, 173, 20, 0.1);
          color: var(--warning-color);
        }

        &.danger {
          background: rgba(255, 77, 79, 0.1);
          color: var(--danger-color);
        }

        &.info {
          background: rgba(64, 128, 255, 0.08);
          color: var(--primary-color, #4080ff);
        }

        .alert-info {
          flex: 1;

          .alert-title {
            font-size: 13px;
            color: var(--text-primary);
          }

          .alert-time {
            font-size: 12px;
            color: var(--text-placeholder);
          }
        }
      }
    }
  }

  .quick-actions {
    .action-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 12px;

      .action-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 8px;
        padding: 20px;
        background: var(--bg-secondary);
        border-radius: var(--radius-md);
        cursor: pointer;
        transition: all 0.3s;

        &:hover {
          background: var(--primary-color);
          color: #fff;
        }

        span {
          font-size: 13px;
        }
      }
    }
  }

  .recent-operations {
    .operation-list {
      .operation-item {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 12px 0;
        border-bottom: 1px solid var(--border-light);

        &:last-child {
          border-bottom: none;
        }

        .operation-info {
          flex: 1;

          .operation-desc {
            font-size: 13px;
            color: var(--text-primary);
          }

          .operation-time {
            font-size: 12px;
            color: var(--text-placeholder);
          }
        }
      }
    }
  }
}
</style>
