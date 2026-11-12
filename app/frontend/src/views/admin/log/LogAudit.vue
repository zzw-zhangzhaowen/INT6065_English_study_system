<template>
  <div class="log-audit">
    <div class="page-header">
      <h2>日志审计</h2>
      <div class="header-actions">
        <el-button @click="exportLogs">
          <el-icon><Download /></el-icon>
          导出日志
        </el-button>
        <el-dropdown @command="handleCleanCommand">
          <el-button type="danger" plain>
            清理日志 <el-icon class="el-icon--right"><ArrowDown /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="30">清理30天前</el-dropdown-item>
              <el-dropdown-item command="90">清理90天前</el-dropdown-item>
              <el-dropdown-item command="180">清理180天前</el-dropdown-item>
              <el-dropdown-item command="all" divided>清空所有日志</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-value">{{ stats.totalCount || 0 }}</div>
        <div class="stat-label">日志总数</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.todayCount || 0 }}</div>
        <div class="stat-label">今日日志</div>
      </div>
      <div class="stat-card success">
        <div class="stat-value">{{ stats.successCount || 0 }}</div>
        <div class="stat-label">成功操作</div>
      </div>
      <div class="stat-card danger">
        <div class="stat-value">{{ stats.failCount || 0 }}</div>
        <div class="stat-label">失败操作</div>
      </div>
    </div>

    <div class="log-content">
      <div class="filter-bar">
        <el-input
          v-model="filterUsername"
          placeholder="操作用户"
          :prefix-icon="User"
          clearable
          style="width: 160px"
        />
        <el-input
          v-model="filterOperation"
          placeholder="操作描述"
          :prefix-icon="Search"
          clearable
          style="width: 200px"
        />
        <el-select v-model="filterStatus" placeholder="操作状态" clearable style="width: 120px">
          <el-option label="全部" value="" />
          <el-option label="成功" :value="1" />
          <el-option label="失败" :value="0" />
        </el-select>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD HH:mm:ss"
          :default-time="[new Date(0, 0, 0, 0, 0, 0), new Date(0, 0, 0, 23, 59, 59)]"
          style="width: 280px"
        />
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
        <el-button @click="resetFilters">重置</el-button>
      </div>

      <el-table :data="logs" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="createTime" label="时间" width="180" />
        <el-table-column prop="operation" label="操作描述" min-width="150" show-overflow-tooltip />
        <el-table-column prop="username" label="操作用户" width="120" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ipAddress" label="IP地址" width="140" />
        <el-table-column prop="executeTime" label="耗时" width="90" align="center">
          <template #default="{ row }">
            <span v-if="row.executeTime != null">{{ row.executeTime }}ms</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80" align="center">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="viewDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[20, 50, 100, 200]"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>

    <!-- 日志详情弹窗 -->
    <el-dialog v-model="showDetailDialog" title="日志详情" width="640px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="日志ID" :span="1">{{ currentLog.id }}</el-descriptions-item>
        <el-descriptions-item label="操作时间" :span="1">{{ currentLog.createTime }}</el-descriptions-item>
        <el-descriptions-item label="操作描述" :span="2">{{ currentLog.operation }}</el-descriptions-item>
        <el-descriptions-item label="操作用户" :span="1">{{ currentLog.username || '-' }}</el-descriptions-item>
        <el-descriptions-item label="操作状态" :span="1">
          <el-tag :type="currentLog.status === 1 ? 'success' : 'danger'" size="small">
            {{ currentLog.status === 1 ? '成功' : '失败' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="IP地址" :span="1">{{ currentLog.ipAddress || '-' }}</el-descriptions-item>
        <el-descriptions-item label="执行耗时" :span="1">
          {{ currentLog.executeTime != null ? currentLog.executeTime + 'ms' : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="请求方法" :span="2">{{ currentLog.method || '-' }}</el-descriptions-item>
        <el-descriptions-item label="浏览器" :span="1">{{ currentLog.browser || '-' }}</el-descriptions-item>
        <el-descriptions-item label="操作系统" :span="1">{{ currentLog.os || '-' }}</el-descriptions-item>
        <el-descriptions-item v-if="currentLog.params" label="请求参数" :span="2">
          <div class="log-params">{{ currentLog.params }}</div>
        </el-descriptions-item>
        <el-descriptions-item v-if="currentLog.errorMsg" label="错误信息" :span="2">
          <span class="error-text">{{ currentLog.errorMsg }}</span>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Download, Search, User, ArrowDown } from '@element-plus/icons-vue'
import { logApi } from '@/api/log'

const filterUsername = ref('')
const filterOperation = ref('')
const filterStatus = ref('')
const dateRange = ref(null)
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const loading = ref(false)
const showDetailDialog = ref(false)
const currentLog = ref({})
const logs = ref([])
const stats = reactive({
  totalCount: 0,
  todayCount: 0,
  successCount: 0,
  failCount: 0
})

// 加载日志列表
const loadLogs = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value
    }
    if (filterUsername.value) params.username = filterUsername.value
    if (filterOperation.value) params.operation = filterOperation.value
    if (filterStatus.value !== '' && filterStatus.value !== null) params.status = filterStatus.value
    if (dateRange.value && dateRange.value.length === 2) {
      params.startTime = dateRange.value[0]
      params.endTime = dateRange.value[1]
    }

    const res = await logApi.getLogs(params)
    if (res.data) {
      logs.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('加载日志失败:', error)
    ElMessage.error('加载日志失败')
  } finally {
    loading.value = false
  }
}

// 加载统计数据
const loadStats = async () => {
  try {
    const res = await logApi.getLogStats()
    if (res.data) {
      Object.assign(stats, res.data)
    }
  } catch (error) {
    console.error('加载统计失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadLogs()
}

// 重置筛选
const resetFilters = () => {
  filterUsername.value = ''
  filterOperation.value = ''
  filterStatus.value = ''
  dateRange.value = null
  currentPage.value = 1
  loadLogs()
}

// 分页
const handlePageChange = () => {
  loadLogs()
}

const handleSizeChange = () => {
  currentPage.value = 1
  loadLogs()
}

// 格式化时间用于导出
const formatDateTime = (val) => {
  if (!val) return ''
  if (typeof val === 'string') return val
  const d = new Date(val)
  if (isNaN(d.getTime())) return String(val)
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

// CSV 字段转义
const escapeCsvField = (val) => {
  const str = val == null ? '' : String(val)
  if (str.includes(',') || str.includes('"') || str.includes('\n')) {
    return '"' + str.replace(/"/g, '""') + '"'
  }
  return str
}

// 导出日志
const exportLogs = () => {
  if (logs.value.length === 0) {
    ElMessage.warning('暂无日志可导出')
    return
  }
  const headers = ['ID', '时间', '操作描述', '操作用户', '状态', 'IP地址', '耗时(ms)', '请求方法']
  let csvContent = headers.map(escapeCsvField).join(',') + '\n'
  logs.value.forEach(log => {
    const row = [
      log.id,
      formatDateTime(log.createTime),
      log.operation,
      log.username || '-',
      log.status === 1 ? '成功' : '失败',
      log.ipAddress || '-',
      log.executeTime != null ? log.executeTime : '-',
      log.method || '-'
    ]
    csvContent += row.map(escapeCsvField).join(',') + '\n'
  })
  const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8' })
  const url = window.URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `系统日志_${new Date().toISOString().slice(0, 10)}.csv`
  a.click()
  window.URL.revokeObjectURL(url)
  ElMessage.success('日志导出成功')
}

// 查看详情
const viewDetail = (row) => {
  currentLog.value = { ...row }
  showDetailDialog.value = true
}

// 清理日志
const handleCleanCommand = (command) => {
  if (command === 'all') {
    ElMessageBox.confirm('确定要清空所有日志吗？此操作不可恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      try {
        await logApi.clearLogs()
        ElMessage.success('日志已清空')
        loadLogs()
        loadStats()
      } catch (error) {
        ElMessage.error('清空日志失败')
      }
    }).catch(() => {})
  } else {
    const days = parseInt(command)
    ElMessageBox.confirm(`确定要清理 ${days} 天前的日志吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      try {
        const res = await logApi.cleanOldLogs(days)
        const count = res.data?.deletedCount || 0
        ElMessage.success(`已清理 ${count} 条旧日志`)
        loadLogs()
        loadStats()
      } catch (error) {
        ElMessage.error('清理日志失败')
      }
    }).catch(() => {})
  }
}

onMounted(() => {
  loadLogs()
  loadStats()
})
</script>

<style lang="scss" scoped>
.log-audit {
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
      gap: 12px;
    }
  }

  .stats-row {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 16px;
    margin-bottom: 24px;

    .stat-card {
      background: var(--bg-primary);
      border-radius: var(--radius-lg);
      padding: 20px;
      text-align: center;

      .stat-value {
        font-size: 28px;
        font-weight: 700;
        color: var(--text-primary);
        margin-bottom: 4px;
      }

      .stat-label {
        font-size: 13px;
        color: var(--text-secondary);
      }

      &.success .stat-value {
        color: var(--el-color-success);
      }

      &.danger .stat-value {
        color: var(--el-color-danger);
      }
    }
  }

  .log-content {
    background: var(--bg-primary);
    border-radius: var(--radius-lg);
    padding: 24px;

    .filter-bar {
      display: flex;
      gap: 12px;
      margin-bottom: 20px;
      flex-wrap: wrap;
    }

    .pagination-wrapper {
      display: flex;
      justify-content: center;
      padding-top: 24px;
    }
  }
}

.log-params {
  max-height: 200px;
  overflow-y: auto;
  word-break: break-all;
  font-family: monospace;
  font-size: 13px;
  background: var(--el-fill-color-light);
  padding: 8px;
  border-radius: 4px;
}

.error-text {
  color: var(--el-color-danger);
}
</style>
