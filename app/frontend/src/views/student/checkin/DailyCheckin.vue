<template>
  <div class="daily-checkin">
    <!-- 打卡卡片 -->
    <el-card class="checkin-card">
      <div class="checkin-header">
        <div class="date-info">
          <h2>{{ currentDate }}</h2>
          <p>{{ weekDay }}</p>
        </div>
        <div class="checkin-action">
          <el-button
            :type="isCheckedIn ? 'success' : 'primary'"
            size="large"
            round
            :disabled="isCheckedIn"
            :loading="checkinLoading"
            @click="handleCheckin"
          >
            <el-icon v-if="isCheckedIn && !checkinLoading"><Check /></el-icon>
            {{ isCheckedIn ? '今日已打卡' : '立即打卡' }}
          </el-button>
        </div>
      </div>
    </el-card>

    <el-row :gutter="20">
      <!-- 打卡统计 -->
      <el-col :span="8">
        <el-card class="stats-card">
          <template #header>
            <span>打卡统计</span>
          </template>
          <div class="stats-content">
            <div class="stat-item">
              <div class="stat-value">{{ stats.totalDays || 0 }}</div>
              <div class="stat-label">累计打卡</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ stats.continuousDays || 0 }}</div>
              <div class="stat-label">连续打卡</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ stats.monthDays || 0 }}</div>
              <div class="stat-label">本月打卡</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 打卡日历 -->
      <el-col :span="16">
        <el-card class="calendar-card">
          <template #header>
            <div class="calendar-header">
              <span>打卡日历</span>
              <div class="month-nav">
                <el-button link @click="prevMonth">
                  <el-icon><ArrowLeft /></el-icon>
                </el-button>
                <span class="current-month">{{ calendarMonth }}</span>
                <el-button link @click="nextMonth">
                  <el-icon><ArrowRight /></el-icon>
                </el-button>
              </div>
            </div>
          </template>
          <div class="calendar-grid">
            <div class="weekday-header">
              <span v-for="day in weekDays" :key="day">{{ day }}</span>
            </div>
            <div class="days-grid">
              <div
                v-for="(day, index) in calendarDays"
                :key="index"
                class="day-cell"
                :class="{
                  'other-month': day.otherMonth,
                  'checked': day.checked,
                  'today': day.isToday
                }"
              >
                <span class="day-number">{{ day.day }}</span>
                <el-icon v-if="day.checked" class="check-icon"><Check /></el-icon>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 打卡排行榜 -->
    <el-card class="ranking-card">
      <template #header>
        <span>打卡排行榜</span>
      </template>
      <el-table :data="rankingList" stripe>
        <el-table-column label="排名" width="80" align="center">
          <template #default="{ $index }">
            <span :class="['rank-badge', `rank-${$index + 1}`]">{{ $index + 1 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="nickname" label="用户" />
        <el-table-column prop="totalDays" label="累计打卡" width="120" align="center">
          <template #default="{ row }">
            {{ row.totalDays }} 天
          </template>
        </el-table-column>
        <el-table-column prop="continuousDays" label="连续打卡" width="120" align="center">
          <template #default="{ row }">
            {{ row.continuousDays }} 天
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Check, ArrowLeft, ArrowRight } from '@element-plus/icons-vue'
import { checkinApi } from '@/api/checkin'

const isCheckedIn = ref(false)
const checkinLoading = ref(false)
const stats = reactive({
  totalDays: 0,
  continuousDays: 0,
  monthDays: 0
})
const rankingList = ref([])
const checkedDates = ref([])
const currentCalendarDate = ref(new Date())

const weekDays = ['日', '一', '二', '三', '四', '五', '六']

const currentDate = computed(() => {
  const now = new Date()
  return `${now.getFullYear()}年${now.getMonth() + 1}月${now.getDate()}日`
})

const weekDay = computed(() => {
  const now = new Date()
  return `星期${weekDays[now.getDay()]}`
})

const calendarMonth = computed(() => {
  const date = currentCalendarDate.value
  return `${date.getFullYear()}年${date.getMonth() + 1}月`
})

const calendarDays = computed(() => {
  const date = currentCalendarDate.value
  const year = date.getFullYear()
  const month = date.getMonth()

  const firstDay = new Date(year, month, 1)
  const lastDay = new Date(year, month + 1, 0)
  const startDay = firstDay.getDay()
  const totalDays = lastDay.getDate()

  const days = []
  const today = new Date()

  // 上月的日期
  const prevMonthLastDay = new Date(year, month, 0).getDate()
  for (let i = startDay - 1; i >= 0; i--) {
    days.push({
      day: prevMonthLastDay - i,
      otherMonth: true,
      checked: false,
      isToday: false
    })
  }

  // 本月的日期
  for (let i = 1; i <= totalDays; i++) {
    const dateStr = `${year}-${String(month + 1).padStart(2, '0')}-${String(i).padStart(2, '0')}`
    const isToday = today.getFullYear() === year && today.getMonth() === month && today.getDate() === i
    days.push({
      day: i,
      otherMonth: false,
      checked: checkedDates.value.includes(dateStr),
      isToday
    })
  }

  // 下月的日期
  const remaining = 42 - days.length
  for (let i = 1; i <= remaining; i++) {
    days.push({
      day: i,
      otherMonth: true,
      checked: false,
      isToday: false
    })
  }

  return days
})

const handleCheckin = async () => {
  checkinLoading.value = true
  try {
    const res = await checkinApi.checkin()
    isCheckedIn.value = true
    ElMessage.success(res.data?.message || '打卡成功！')
    fetchStats()
    fetchCalendar()
    fetchRanking()
  } catch (error) {
    console.error('打卡失败:', error)
    ElMessage.error(error.response?.data?.message || '打卡失败，请重试')
  } finally {
    checkinLoading.value = false
  }
}

const fetchTodayStatus = async () => {
  try {
    const res = await checkinApi.checkToday()
    isCheckedIn.value = res.data?.hasCheckedIn || false
  } catch (error) {
    console.error('获取打卡状态失败:', error)
    ElMessage.error('获取打卡状态失败')
  }
}

const fetchStats = async () => {
  try {
    const res = await checkinApi.getStats()
    Object.assign(stats, res.data)
  } catch (error) {
    console.error('获取统计失败:', error)
    ElMessage.error('获取打卡统计失败')
  }
}

const fetchCalendar = async () => {
  try {
    const date = currentCalendarDate.value
    const res = await checkinApi.getCalendar({
      year: date.getFullYear(),
      month: date.getMonth() + 1
    })
    // 后端返回DailyCheckin对象列表，提取日期并统一为 YYYY-MM-DD 格式
    checkedDates.value = (res.data || []).map(item => {
      const d = item.checkinDate || item
      // 兼容 LocalDate 序列化为数组 [2025, 1, 15] 或字符串 "2025-01-15"
      if (Array.isArray(d)) {
        return `${d[0]}-${String(d[1]).padStart(2, '0')}-${String(d[2]).padStart(2, '0')}`
      }
      return String(d)
    })
  } catch (error) {
    console.error('获取日历失败:', error)
  }
}

const fetchRanking = async () => {
  try {
    const res = await checkinApi.getRanking({ limit: 10 })
    rankingList.value = res.data || []
  } catch (error) {
    console.error('获取排行榜失败:', error)
  }
}

const prevMonth = () => {
  const date = currentCalendarDate.value
  currentCalendarDate.value = new Date(date.getFullYear(), date.getMonth() - 1, 1)
  fetchCalendar()
}

const nextMonth = () => {
  const date = currentCalendarDate.value
  currentCalendarDate.value = new Date(date.getFullYear(), date.getMonth() + 1, 1)
  fetchCalendar()
}

onMounted(() => {
  fetchTodayStatus()
  fetchStats()
  fetchCalendar()
  fetchRanking()
})
</script>

<style scoped>
.daily-checkin {
  padding: 20px;
}

.checkin-card {
  margin-bottom: 20px;
}

.checkin-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.date-info h2 {
  margin: 0 0 5px 0;
  font-size: 24px;
}

.date-info p {
  margin: 0;
  color: #909399;
}

.stats-card, .calendar-card, .ranking-card {
  margin-bottom: 20px;
}

.stats-content {
  display: flex;
  justify-content: space-around;
  text-align: center;
}

.stat-item .stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
}

.stat-item .stat-label {
  color: #909399;
  margin-top: 5px;
}

.calendar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.month-nav {
  display: flex;
  align-items: center;
  gap: 10px;
}

.current-month {
  min-width: 100px;
  text-align: center;
}

.calendar-grid {
  padding: 10px;
}

.weekday-header {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  text-align: center;
  padding: 10px 0;
  border-bottom: 1px solid #ebeef5;
  font-weight: bold;
  color: #606266;
}

.days-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 5px;
  padding-top: 10px;
}

.day-cell {
  aspect-ratio: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  position: relative;
  cursor: default;
}

.day-cell.other-month {
  color: #c0c4cc;
}

.day-cell.today {
  background: #ecf5ff;
  border: 1px solid #409eff;
}

.day-cell.checked {
  background: #67c23a;
  color: white;
}

.day-cell.checked .check-icon {
  position: absolute;
  bottom: 2px;
  font-size: 12px;
}

.day-number {
  font-size: 14px;
}

.rank-badge {
  display: inline-block;
  width: 24px;
  height: 24px;
  line-height: 24px;
  text-align: center;
  border-radius: 50%;
  background: #909399;
  color: white;
  font-size: 12px;
}

.rank-badge.rank-1 {
  background: #f7ba2a;
}

.rank-badge.rank-2 {
  background: #c0c4cc;
}

.rank-badge.rank-3 {
  background: #cd7f32;
}
</style>
