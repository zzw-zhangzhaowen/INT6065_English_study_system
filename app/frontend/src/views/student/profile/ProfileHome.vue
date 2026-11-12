<template>
  <div class="profile-home" v-loading="profileLoading">
    <!-- 个人信息头部 -->
    <div class="profile-header">
      <div class="avatar-section">
        <el-avatar :size="100" :src="getAvatarUrl(userInfo.avatar)" :key="userInfo.avatar">{{ userInfo.nickname?.charAt(0) }}</el-avatar>
        <el-button type="primary" link size="small" @click="changeAvatar">更换头像</el-button>
      </div>
      <div class="info-section">
        <h2>{{ userInfo.nickname }}</h2>
        <div class="user-tags">
          <el-tag>{{ userInfo.level }}</el-tag>
          <el-tag type="success">连续学习{{ userInfo.streak }}天</el-tag>
        </div>
        <div class="user-stats">
          <div class="stat-item">
            <span class="stat-value">{{ userInfo.studyDays }}</span>
            <span class="stat-label">学习天数</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">{{ userInfo.studyHours }}</span>
            <span class="stat-label">学习时长(h)</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">{{ userInfo.points }}</span>
            <span class="stat-label">成就积分</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">{{ userInfo.rank }}</span>
            <span class="stat-label">排名</span>
          </div>
        </div>
      </div>
      <div class="action-section">
        <el-button type="primary" @click="editProfile">编辑资料</el-button>
      </div>
    </div>

    <!-- 功能选项卡 -->
    <el-tabs v-model="activeTab" class="profile-tabs">
      <el-tab-pane label="个人资料" name="info">
        <div class="info-card">
          <h3>基本信息</h3>
          <div class="info-list">
            <div class="info-item">
              <span class="label">用户名</span>
              <span class="value">{{ userInfo.username }}</span>
            </div>
            <div class="info-item">
              <span class="label">邮箱</span>
              <span class="value">{{ userInfo.email }}</span>
            </div>
            <div class="info-item">
              <span class="label">手机号</span>
              <span class="value">{{ userInfo.phone }}</span>
            </div>
            <div class="info-item">
              <span class="label">真实姓名</span>
              <span class="value">{{ userInfo.realName || '未填写' }}</span>
            </div>
            <div class="info-item">
              <span class="label">学校/机构</span>
              <span class="value">{{ userInfo.school || '未填写' }}</span>
            </div>
          </div>
        </div>

        <div class="info-card">
          <h3>学习信息</h3>
          <div class="info-list">
            <div class="info-item">
              <span class="label">当前水平</span>
              <span class="value">{{ userInfo.currentLevel }}</span>
            </div>
            <div class="info-item">
              <span class="label">目标等级</span>
              <span class="value">{{ userInfo.targetLevel }}</span>
            </div>
            <div class="info-item">
              <span class="label">每日目标</span>
              <span class="value">{{ userInfo.dailyGoal }}分钟</span>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <el-tab-pane label="学习报告" name="report">
        <div class="report-overview">
          <div class="overview-cards">
            <div class="overview-card">
              <el-icon :size="32" color="#4080ff"><Timer /></el-icon>
              <div class="card-info">
                <div class="card-value">{{ reportData.totalHours }}</div>
                <div class="card-label">累计学习(小时)</div>
              </div>
            </div>
            <div class="overview-card">
              <el-icon :size="32" color="#52c41a"><Document /></el-icon>
              <div class="card-info">
                <div class="card-value">{{ reportData.totalExams }}</div>
                <div class="card-label">完成考试</div>
              </div>
            </div>
            <div class="overview-card">
              <el-icon :size="32" color="#faad14"><TrendCharts /></el-icon>
              <div class="card-info">
                <div class="card-value">{{ reportData.avgScore }}%</div>
                <div class="card-label">平均正确率</div>
              </div>
            </div>
          </div>
          <div class="chart-section">
            <div class="chart-card">
              <h4>学习时长趋势</h4>
              <div class="chart-container" ref="trendChartRef"></div>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <el-tab-pane label="成就系统" name="achievement">
        <div class="achievement-section">
          <div class="achievement-stats">
            <div class="stat-card">
              <div class="stat-value">{{ achievementData.total }}</div>
              <div class="stat-label">总成就</div>
            </div>
            <div class="stat-card">
              <div class="stat-value">{{ achievementData.unlocked }}</div>
              <div class="stat-label">已解锁</div>
            </div>
            <div class="stat-card">
              <div class="stat-value">{{ achievementData.points }}</div>
              <div class="stat-label">成就点数</div>
            </div>
          </div>
          <div class="achievement-list">
            <h4>已解锁成就</h4>
            <div class="achievement-grid">
              <div v-for="item in achievements" :key="item.id" class="achievement-item" :class="{ locked: !item.unlocked }">
                <div class="achievement-icon">
                  <el-icon :size="32"><Trophy /></el-icon>
                </div>
                <div class="achievement-info">
                  <div class="achievement-name">{{ item.name }}</div>
                  <div class="achievement-desc">{{ item.description }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <el-tab-pane label="账户设置" name="settings">
        <div class="settings-section">
          <div class="settings-card">
            <h3>安全设置</h3>
            <div class="settings-list">
              <div class="settings-item">
                <div class="item-info">
                  <div class="item-title">修改密码</div>
                  <div class="item-desc">定期修改密码可以提高账户安全性</div>
                </div>
                <el-button @click="changePassword">修改</el-button>
              </div>
              <div class="settings-item">
                <div class="item-info">
                  <div class="item-title">绑定邮箱</div>
                  <div class="item-desc">{{ userInfo.email }}</div>
                </div>
                <el-button @click="changeEmail">更换</el-button>
              </div>
              <div class="settings-item">
                <div class="item-info">
                  <div class="item-title">绑定手机</div>
                  <div class="item-desc">{{ userInfo.phone }}</div>
                </div>
                <el-button @click="changePhone">更换</el-button>
              </div>
            </div>
          </div>

          <div class="settings-card">
            <h3>通知设置</h3>
            <div class="settings-list">
              <div class="settings-item">
                <div class="item-info">
                  <div class="item-title">学习提醒</div>
                  <div class="item-desc">每日学习提醒通知</div>
                </div>
                <el-switch v-model="notificationSettings.studyReminder" />
              </div>
              <div class="settings-item">
                <div class="item-info">
                  <div class="item-title">考试提醒</div>
                  <div class="item-desc">考试开始前提醒</div>
                </div>
                <el-switch v-model="notificationSettings.examReminder" />
              </div>
              <div class="settings-item">
                <div class="item-info">
                  <div class="item-title">社区通知</div>
                  <div class="item-desc">回复、点赞等互动通知</div>
                </div>
                <el-switch v-model="notificationSettings.communityNotify" />
              </div>
            </div>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 编辑资料对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑资料" width="500px">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="真实姓名">
          <el-input v-model="editForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="学校/机构">
          <el-input v-model="editForm.school" placeholder="请输入学校或机构名称" />
        </el-form-item>
        <el-form-item label="当前水平">
          <el-select v-model="editForm.currentLevel" placeholder="请选择当前水平" style="width: 100%">
            <el-option label="零基础" value="零基础" />
            <el-option label="CET-4 初级" value="CET-4 初级" />
            <el-option label="CET-4 中级" value="CET-4 中级" />
            <el-option label="CET-4 高级" value="CET-4 高级" />
            <el-option label="CET-6 初级" value="CET-6 初级" />
            <el-option label="CET-6 中级" value="CET-6 中级" />
            <el-option label="CET-6 高级" value="CET-6 高级" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标等级">
          <el-select v-model="editForm.targetLevel" placeholder="请选择目标等级" style="width: 100%">
            <el-option label="CET-4 425分" value="CET-4 425分" />
            <el-option label="CET-4 500分" value="CET-4 500分" />
            <el-option label="CET-4 550分" value="CET-4 550分" />
            <el-option label="CET-6 425分" value="CET-6 425分" />
            <el-option label="CET-6 500分" value="CET-6 500分" />
            <el-option label="CET-6 550分" value="CET-6 550分" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveProfile">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, watch, nextTick } from 'vue'
import * as echarts from 'echarts'
import { Timer, Document, TrendCharts, Trophy } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { userApi } from '@/api/user'
import { useUserStore } from '@/stores/user'
import { studentApi } from '@/api/student'

const userStore = useUserStore()
const activeTab = ref('info')
const trendChartRef = ref()
let trendChart = null
const profileLoading = ref(false)

// 编辑资料对话框
const editDialogVisible = ref(false)
const editForm = reactive({
  realName: '',
  school: '',
  currentLevel: '',
  targetLevel: ''
})

// 处理头像URL
const getAvatarUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
  return `${baseUrl}${url}`
}

// 用户信息
const userInfo = ref({
  nickname: userStore.userInfo?.nickname || userStore.userInfo?.username || '学习者',
  avatar: userStore.userInfo?.avatar || '',
  username: userStore.userInfo?.username || '',
  email: userStore.userInfo?.email || '',
  phone: userStore.userInfo?.phone || '',
  realName: userStore.userInfo?.realName || '',
  school: userStore.userInfo?.school || '',
  level: '',
  streak: 0,
  studyDays: 0,
  studyHours: 0,
  points: 0,
  rank: '-',
  currentLevel: '',
  targetLevel: '',
  dailyGoal: 60
})

// 学习报告数据
const reportData = ref({
  totalHours: 0,
  totalExams: 0,
  avgScore: 0
})

// 成就数据
const achievementData = ref({
  total: 0,
  unlocked: 0,
  points: 0
})

// 通知设置 - 从 localStorage 恢复
const savedNotifySettings = JSON.parse(localStorage.getItem('notificationSettings') || 'null')
const notificationSettings = reactive({
  studyReminder: savedNotifySettings?.studyReminder ?? true,
  examReminder: savedNotifySettings?.examReminder ?? true,
  communityNotify: savedNotifySettings?.communityNotify ?? false
})

// 监听通知设置变化，持久化到 localStorage
watch(notificationSettings, (val) => {
  localStorage.setItem('notificationSettings', JSON.stringify(val))
}, { deep: true })

// 监听 tab 切换，在报告 tab 激活时初始化图表
let chartInitialized = false
watch(activeTab, (val) => {
  if (val === 'report' && !chartInitialized) {
    nextTick(() => {
      initChart()
      chartInitialized = true
    })
  }
})

const achievements = ref([])

// 组件挂载
onMounted(() => {
  loadUserInfo()
  loadReportData()
  loadAchievements()
  loadRank()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
})

// 加载用户信息
const loadUserInfo = async () => {
  profileLoading.value = true
  try {
    const res = await userApi.getProfile()
    if (res.data) {
      const data = res.data.user || res.data
      // 处理头像URL，添加时间戳避免缓存
      let avatarUrl = data.avatar || ''
      if (avatarUrl && !avatarUrl.includes('?t=')) {
        avatarUrl = avatarUrl + '?t=' + Date.now()
      }
      const studyDays = data.studyDays || 0
      const streak = data.continuousDays || 0
      // 根据学习天数计算等级
      let level = 'Lv.1 初学者'
      if (studyDays >= 180) level = 'Lv.6 学习大师'
      else if (studyDays >= 90) level = 'Lv.5 学习达人'
      else if (studyDays >= 60) level = 'Lv.4 进阶学者'
      else if (studyDays >= 30) level = 'Lv.3 勤奋学员'
      else if (studyDays >= 7) level = 'Lv.2 坚持学习'

      userInfo.value = {
        ...userInfo.value,
        nickname: data.realName || data.username,
        avatar: avatarUrl,
        username: data.username,
        email: data.email,
        phone: data.phone,
        realName: data.realName,
        school: data.school,
        currentLevel: data.currentLevel,
        targetLevel: data.targetLevel,
        studyDays,
        streak,
        studyHours: Math.floor((data.totalStudyTime || 0) / 60),
        points: data.achievementScore || 0,
        level
      }
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
    ElMessage.error('加载用户信息失败')
  } finally {
    profileLoading.value = false
  }
}

// 加载排名
const loadRank = async () => {
  try {
    const res = await studentApi.getLearningRank()
    if (res.data && Array.isArray(res.data)) {
      const myRank = res.data.findIndex(item => item.userId === userStore.userId)
      userInfo.value.rank = myRank >= 0 ? myRank + 1 : '-'
    }
  } catch (error) {
    console.error('加载排名失败:', error)
  }
}

// 加载学习报告数据
const loadReportData = async () => {
  try {
    const res = await userApi.getLearningStats()
    console.log('学习报告API响应:', res)
    if (res.data) {
      const totalMinutes = res.data.totalStudyTime || 0
      console.log('总学习时长(分钟):', totalMinutes)
      reportData.value = {
        totalHours: totalMinutes > 0 ? Math.floor(totalMinutes / 60) : 0,
        totalExams: res.data.totalExams || res.data.examCount || 0,
        avgScore: res.data.accuracy || res.data.avgScore || 0
      }
      console.log('更新后的reportData:', reportData.value)
    } else {
      console.warn('API返回数据为空:', res)
    }
  } catch (error) {
    console.error('加载学习报告失败:', error)
    ElMessage.error('加载学习报告失败')
  }
}

// 加载成就数据
const loadAchievements = async () => {
  try {
    const res = await userApi.getAchievements()
    if (res.data) {
      if (Array.isArray(res.data)) {
        achievements.value = res.data.map(item => ({
          id: item.id,
          name: item.name || item.achievementName,
          description: item.description,
          unlocked: item.unlocked !== false
        }))
        achievementData.value = {
          total: res.data.length,
          unlocked: res.data.filter(a => a.unlocked !== false).length,
          points: res.data.reduce((sum, a) => sum + (a.points || 0), 0)
        }
      } else {
        achievementData.value = {
          total: res.data.total || 0,
          unlocked: res.data.unlocked || 0,
          points: res.data.points || 0
        }
      }
    }
  } catch (error) {
    console.error('加载成就数据失败:', error)
  }
}

const changeAvatar = () => {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = 'image/*'
  input.onchange = async (e) => {
    const file = e.target.files[0]
    if (file) {
      try {
        const res = await userApi.uploadAvatar(file)
        if (res.data?.url) {
          const avatarUrl = res.data.url
          await userApi.updateProfile({ avatar: avatarUrl })
          const displayUrl = avatarUrl.includes('?') ? avatarUrl + '&t=' + Date.now() : avatarUrl + '?t=' + Date.now()
          userInfo.value.avatar = displayUrl
          userStore.updateUserInfo({ avatar: displayUrl })
          ElMessage.success('头像更新成功')
        }
      } catch (error) {
        console.error('头像上传失败:', error)
        ElMessage.error('头像上传失败')
      }
    }
  }
  input.click()
}

const editProfile = () => {
  editForm.realName = userInfo.value.realName || ''
  editForm.school = userInfo.value.school || ''
  editForm.currentLevel = userInfo.value.currentLevel || ''
  editForm.targetLevel = userInfo.value.targetLevel || ''
  editDialogVisible.value = true
}

const saveProfile = async () => {
  try {
    await userApi.updateProfile({
      realName: editForm.realName,
      school: editForm.school,
      currentLevel: editForm.currentLevel,
      targetLevel: editForm.targetLevel
    })
    userInfo.value.realName = editForm.realName
    userInfo.value.nickname = editForm.realName || userInfo.value.username
    userInfo.value.school = editForm.school
    userInfo.value.currentLevel = editForm.currentLevel
    userInfo.value.targetLevel = editForm.targetLevel
    userStore.updateUserInfo({ realName: editForm.realName, nickname: editForm.realName })
    editDialogVisible.value = false
    ElMessage.success('资料更新成功')
  } catch (error) {
    ElMessage.error(error.message || '更新失败')
  }
}

const changePassword = () => {
  ElMessageBox.prompt('请输入新密码', '修改密码', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    inputType: 'password',
    inputValidator: (val) => val && val.length >= 6 ? true : '密码长度不能少于6位'
  }).then(async ({ value }) => {
    ElMessageBox.prompt('请输入原密码', '验证身份', {
      confirmButtonText: '确认修改',
      cancelButtonText: '取消',
      inputType: 'password'
    }).then(async ({ value: oldPassword }) => {
      try {
        await userApi.changePassword({ oldPassword, newPassword: value })
        ElMessage.success('密码修改成功')
      } catch (error) {
        ElMessage.error(error.message || '密码修改失败')
      }
    }).catch(() => {})
  }).catch(() => {})
}

const changeEmail = () => {
  ElMessageBox.prompt('请输入新邮箱', '更换邮箱', {
    confirmButtonText: '保存',
    cancelButtonText: '取消',
    inputValue: userInfo.value.email,
    inputValidator: (val) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(val) ? true : '请输入正确的邮箱格式'
  }).then(async ({ value }) => {
    try {
      await userApi.updateProfile({ email: value })
      userInfo.value.email = value
      ElMessage.success('邮箱更新成功')
    } catch (error) {
      ElMessage.error(error.message || '更新失败')
    }
  }).catch(() => {})
}

const changePhone = () => {
  ElMessageBox.prompt('请输入新手机号', '更换手机', {
    confirmButtonText: '保存',
    cancelButtonText: '取消',
    inputValue: userInfo.value.phone,
    inputValidator: (val) => /^1[3-9]\d{9}$/.test(val) ? true : '请输入正确的手机号'
  }).then(async ({ value }) => {
    try {
      await userApi.updateProfile({ phone: value })
      userInfo.value.phone = value
      ElMessage.success('手机号更新成功')
    } catch (error) {
      ElMessage.error(error.message || '更新失败')
    }
  }).catch(() => {})
}

const initChart = () => {
  if (!trendChartRef.value) return
  trendChart = echarts.init(trendChartRef.value)
  loadTrendData()
}

const loadTrendData = async () => {
  // 默认显示数据
  let xData = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
  let yData = [0, 0, 0, 0, 0, 0, 0]

  try {
    const res = await studentApi.getLearningTrend({ days: 7 })
    console.log('学习趋势API响应:', res)
    if (res.data) {
      const trendSource = Array.isArray(res.data)
        ? res.data
        : (Array.isArray(res.data?.trend) ? res.data.trend : null)

      if (trendSource && trendSource.length > 0) {
        xData = trendSource.map(item => item.date || item.label)
        yData = trendSource.map(item => Number(item.minutes ?? item.value ?? 0))
        console.log('从trend数据提取:', { xData, yData })
      } else if (Array.isArray(res.data?.labels) && Array.isArray(res.data?.values)) {
        xData = res.data.labels
        yData = res.data.values.map(v => Number(v || 0))
        console.log('从labels/values提取:', { xData, yData })
      } else {
        console.warn('无法解析趋势数据:', res.data)
      }
    }
  } catch (error) {
    console.error('加载趋势数据失败:', error)
  }

  if (trendChart) {
    console.log('设置图表选项，xData长度:', xData.length, 'yData长度:', yData.length)
    trendChart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: xData },
      yAxis: { type: 'value', name: '分钟' },
      series: [{
        data: yData,
        type: 'bar',
        itemStyle: {
          color: '#5B9BD5'
        }
      }]
    })
    console.log('图表已更新')
  } else {
    console.warn('trendChart未初始化')
  }
}

const handleResize = () => {
  trendChart?.resize()
}
</script>

<style lang="scss" scoped>
.profile-home {
  .profile-header {
    display: flex;
    align-items: center;
    gap: 32px;
    padding: 32px;
    background: #87CEEB;
    border-radius: var(--radius-lg);
    color: #fff;
    margin-bottom: 24px;

    .avatar-section {
      text-align: center;

      .el-button {
        color: #fff;
        margin-top: 8px;
      }
    }

    .info-section {
      flex: 1;

      h2 {
        font-size: 24px;
        font-weight: 600;
        margin-bottom: 12px;
      }

      .user-tags {
        display: flex;
        gap: 8px;
        margin-bottom: 16px;

        .el-tag {
          border: none;
        }
      }

      .user-stats {
        display: flex;
        gap: 32px;

        .stat-item {
          text-align: center;

          .stat-value {
            font-size: 24px;
            font-weight: 700;
          }

          .stat-label {
            font-size: 13px;
            opacity: 0.9;
          }
        }
      }
    }
  }

  .profile-tabs {
    background: var(--bg-primary);
    border-radius: var(--radius-lg);
    padding: 24px;

    :deep(.el-tabs__header) {
      margin-bottom: 24px;
    }
  }

  .info-card {
    background: var(--bg-secondary);
    border-radius: var(--radius-lg);
    padding: 24px;
    margin-bottom: 20px;

    h3 {
      font-size: 16px;
      font-weight: 600;
      color: var(--text-primary);
      margin-bottom: 20px;
    }

    .info-list {
      .info-item {
        display: flex;
        padding: 12px 0;
        border-bottom: 1px solid var(--border-light);

        &:last-child {
          border-bottom: none;
        }

        .label {
          width: 120px;
          color: var(--text-secondary);
        }

        .value {
          flex: 1;
          color: var(--text-primary);
        }
      }
    }
  }

  .report-overview {
    .overview-cards {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 20px;
      margin-bottom: 24px;

      .overview-card {
        display: flex;
        align-items: center;
        gap: 16px;
        padding: 24px;
        background: var(--bg-secondary);
        border-radius: var(--radius-lg);

        .card-info {
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
      }
    }

    .chart-card {
      background: var(--bg-secondary);
      border-radius: var(--radius-lg);
      padding: 24px;

      h4 {
        font-size: 15px;
        font-weight: 600;
        color: var(--text-primary);
        margin-bottom: 16px;
      }

      .chart-container {
        height: 300px;
      }
    }
  }

  .achievement-section {
    .achievement-stats {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 20px;
      margin-bottom: 24px;

      .stat-card {
        text-align: center;
        padding: 24px;
        background: var(--bg-secondary);
        border-radius: var(--radius-lg);

        .stat-value {
          font-size: 32px;
          font-weight: 700;
          color: var(--primary-color);
        }

        .stat-label {
          font-size: 13px;
          color: var(--text-secondary);
          margin-top: 4px;
        }
      }
    }

    .achievement-list {
      h4 {
        font-size: 16px;
        font-weight: 600;
        color: var(--text-primary);
        margin-bottom: 16px;
      }

      .achievement-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
        gap: 16px;

        .achievement-item {
          display: flex;
          align-items: center;
          gap: 16px;
          padding: 20px;
          background: var(--bg-secondary);
          border-radius: var(--radius-lg);

          &.locked {
            opacity: 0.5;

            .achievement-icon {
              background: var(--bg-tertiary);
              color: var(--text-placeholder);
            }
          }

          .achievement-icon {
            width: 56px;
            height: 56px;
            border-radius: 50%;
            background: #90EE90;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #fff;
          }

          .achievement-info {
            .achievement-name {
              font-size: 15px;
              font-weight: 600;
              color: var(--text-primary);
              margin-bottom: 4px;
            }

            .achievement-desc {
              font-size: 13px;
              color: var(--text-secondary);
            }
          }
        }
      }
    }
  }

  .settings-section {
    .settings-card {
      background: var(--bg-secondary);
      border-radius: var(--radius-lg);
      padding: 24px;
      margin-bottom: 20px;

      h3 {
        font-size: 16px;
        font-weight: 600;
        color: var(--text-primary);
        margin-bottom: 20px;
      }

      .settings-list {
        .settings-item {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 16px 0;
          border-bottom: 1px solid var(--border-light);

          &:last-child {
            border-bottom: none;
          }

          .item-info {
            .item-title {
              font-size: 14px;
              font-weight: 500;
              color: var(--text-primary);
              margin-bottom: 4px;
            }

            .item-desc {
              font-size: 13px;
              color: var(--text-secondary);
            }
          }
        }
      }
    }
  }
}
</style>
