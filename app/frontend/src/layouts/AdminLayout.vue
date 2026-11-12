<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <aside class="sidebar" :class="{ collapsed: isCollapsed }">
      <div class="sidebar-header">
        <div class="logo">
          <img v-if="systemLogo" :src="getFullUrl(systemLogo)" alt="Logo" class="logo-img" />
          <el-icon v-else :size="32" color="#52c41a"><Setting /></el-icon>
          <span v-show="!isCollapsed" class="logo-text">{{ systemName || '系统管理' }}</span>
        </div>
      </div>

      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapsed"
        :collapse-transition="false"
        router
        class="sidebar-menu"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <template #title>系统仪表板</template>
        </el-menu-item>

        <el-menu-item index="/admin/user">
          <el-icon><User /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>

        <el-menu-item index="/admin/system">
          <el-icon><Setting /></el-icon>
          <template #title>系统配置</template>
        </el-menu-item>

        <el-menu-item index="/admin/log">
          <el-icon><Document /></el-icon>
          <template #title>日志审计</template>
        </el-menu-item>

        <el-menu-item index="/admin/monitor">
          <el-icon><Monitor /></el-icon>
          <template #title>系统监控</template>
        </el-menu-item>
      </el-menu>

      <div class="sidebar-footer">
        <div class="collapse-btn" @click="toggleCollapse">
          <el-icon :size="18">
            <Fold v-if="!isCollapsed" />
            <Expand v-else />
          </el-icon>
        </div>
      </div>
    </aside>

    <!-- 主内容区 -->
    <div class="main-container">
      <header class="header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentRoute.meta?.title">
              {{ currentRoute.meta.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="header-right">
          <!-- 消息通知 -->
          <el-popover placement="bottom" :width="320" trigger="click">
            <template #reference>
              <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="notification-badge">
                <el-button :icon="Bell" circle />
              </el-badge>
            </template>
            <div class="notification-panel">
              <div class="notification-header">
                <span>消息通知</span>
                <el-button type="primary" link size="small" @click="markAllRead">全部已读</el-button>
              </div>
              <el-scrollbar height="300px">
                <div v-if="notifications.length === 0" class="empty-notification">
                  暂无新消息
                </div>
                <div
                  v-for="item in notifications"
                  :key="item.id"
                  class="notification-item"
                  :class="{ unread: !item.read }"
                  @click="handleNotificationClick(item)"
                >
                  <div class="notification-icon">
                    <el-icon><InfoFilled /></el-icon>
                  </div>
                  <div class="notification-content">
                    <div class="notification-title">{{ item.title }}</div>
                    <div class="notification-time">{{ item.time }}</div>
                  </div>
                </div>
              </el-scrollbar>
              <div class="notification-footer">
                <el-button type="primary" link @click="goToMessageCenter">查看全部消息</el-button>
              </div>
            </div>
          </el-popover>

          <el-dropdown trigger="click" @command="handleUserCommand">
            <div class="user-info">
              <el-avatar :size="36" :src="userAvatarUrl">{{ (userInfo.nickname || userInfo.username || '管理员').charAt(0) }}</el-avatar>
              <span class="user-name">{{ userInfo.nickname || userInfo.username || '管理员' }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="settings">系统设置</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <main class="content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import {
  Setting, DataAnalysis, User, Document, Monitor,
  Fold, Expand, Bell, ArrowDown, InfoFilled
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { messageApi } from '@/api/message'
import { adminApi } from '@/api/admin'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapsed = ref(false)
const currentRoute = computed(() => route)
const activeMenu = computed(() => route.path)
const avatarVersion = ref(Date.now())
const systemLogo = ref('')
const systemName = ref('')

const getFullUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
  return `${baseUrl}${url}`
}

const loadPublicConfig = async () => {
  try {
    const res = await adminApi.getPublicConfig()
    systemLogo.value = res.data?.systemLogo || ''
    systemName.value = res.data?.systemName || ''
  } catch (e) {
    // 静默失败
  }
}

// 获取用户信息
const userInfo = computed(() => userStore.userInfo || {})

// 用户头像URL
const userAvatarUrl = computed(() => {
  const rawUrl = userInfo.value?.avatar || ''
  if (!rawUrl) return ''
  const cleanUrl = rawUrl.replace(/([?&])t=\d+(&?)/, (match, p1, p2) => (p1 === '?' && p2 ? '?' : p2 ? p1 : ''))
  const withTs = cleanUrl.includes('?') ? `${cleanUrl}&t=${avatarVersion.value}` : `${cleanUrl}?t=${avatarVersion.value}`
  if (withTs.startsWith('http')) return withTs
  const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
  return `${baseUrl}${withTs}`
})

watch(
  () => userInfo.value?.avatar,
  () => {
    avatarVersion.value = Date.now()
  }
)

// 消息通知数据
const notifications = ref([])
const unreadCount = ref(0)

// 加载通知列表
const loadNotifications = async () => {
  try {
    const res = await messageApi.getInbox({ pageNum: 1, pageSize: 5 })
    if (res.data?.records) {
      notifications.value = res.data.records.map(msg => ({
        id: msg.id,
        title: msg.title,
        time: formatTime(msg.createTime),
        read: msg.isRead === 1,
        content: msg.content
      }))
    } else if (Array.isArray(res.data)) {
      notifications.value = res.data.slice(0, 5).map(msg => ({
        id: msg.id,
        title: msg.title,
        time: formatTime(msg.createTime),
        read: msg.isRead === 1,
        content: msg.content
      }))
    }
  } catch (error) {
    console.error('加载通知失败:', error)
  }
}

// 加载未读消息数量
const loadUnreadCount = async () => {
  try {
    const res = await messageApi.getUnreadCount()
    unreadCount.value = res.data || 0
  } catch (error) {
    console.error('加载未读数量失败:', error)
    unreadCount.value = 0
  }
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  return time.substring(0, 16)
}

// 处理通知点击
const handleNotificationClick = async (item) => {
  if (!item.read) {
    try {
      await messageApi.markAsRead(item.id)
      item.read = true
      await loadUnreadCount()
    } catch (error) {
      console.error('标记已读失败:', error)
    }
  }
  goToMessageCenter()
}

// 全部标记为已读
const markAllRead = async () => {
  try {
    await messageApi.markAllAsRead()
    ElMessage.success('已全部标记为已读')
    await loadNotifications()
    await loadUnreadCount()
  } catch (error) {
    console.error('标记已读失败:', error)
    ElMessage.error('操作失败')
  }
}

// 跳转到消息中心
const goToMessageCenter = () => {
  router.push('/admin/message')
}

const toggleCollapse = () => {
  isCollapsed.value = !isCollapsed.value
}

const handleUserCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/admin/profile')
      break
    case 'settings':
      router.push('/admin/system')
      break
    case 'logout':
      ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        userStore.logout()
        ElMessage.success('退出登录成功')
      }).catch(() => {})
      break
  }
}

// 初始化加载消息通知
let notificationTimer = null

onMounted(() => {
  loadNotifications()
  loadUnreadCount()
  loadPublicConfig()

  // 每 30 秒刷新一次未读数量
  notificationTimer = setInterval(() => {
    loadUnreadCount()
  }, 30000)
})

onUnmounted(() => {
  if (notificationTimer) {
    clearInterval(notificationTimer)
    notificationTimer = null
  }
})
</script>

<style lang="scss" scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background-color: #f4f7fd;
  background-image:
    radial-gradient(720px 460px at 8% 4%, rgba(37, 99, 235, 0.07), transparent 62%),
    radial-gradient(620px 420px at 92% 94%, rgba(14, 165, 233, 0.07), transparent 62%),
    linear-gradient(rgba(37, 99, 235, 0.045) 1px, transparent 1px),
    linear-gradient(90deg, rgba(37, 99, 235, 0.045) 1px, transparent 1px);
  background-size: auto, auto, 34px 34px, 34px 34px;
  background-attachment: fixed;
}

.sidebar {
  width: 240px;
  background: var(--bg-primary);
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
  transition: width 0.3s;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 100;

  &.collapsed {
    width: 64px;

    .logo-text {
      display: none;
    }
  }

  .sidebar-header {
    height: 60px;
    display: flex;
    align-items: center;
    padding: 0 16px;
    border-bottom: 1px solid var(--border-light);

    .logo {
      display: flex;
      align-items: center;
      gap: 12px;
      overflow: hidden;

      .logo-img {
        width: 32px;
        height: 32px;
        flex-shrink: 0;
        object-fit: contain;
        border-radius: 4px;
      }

      .logo-text {
        font-size: 16px;
        font-weight: 600;
        color: var(--text-primary);
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
    }
  }

  .sidebar-menu {
    flex: 1;
    border-right: none;
    overflow-y: auto;

    &:not(.el-menu--collapse) {
      width: 100%;
    }
  }

  .sidebar-footer {
    padding: 12px;
    border-top: 1px solid var(--border-light);

    .collapse-btn {
      width: 100%;
      height: 36px;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      border-radius: var(--radius-sm);
      color: var(--text-secondary);
      transition: all 0.3s;

      &:hover {
        background: var(--bg-tertiary);
        color: var(--primary-color);
      }
    }
  }
}

.main-container {
  flex: 1;
  margin-left: 240px;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  transition: margin-left 0.3s;

  .sidebar.collapsed + & {
    margin-left: 64px;
  }
}

.header {
  height: 60px;
  background: var(--bg-primary);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 0;
  z-index: 99;

  .header-right {
    display: flex;
    align-items: center;
    gap: 16px;

    .user-info {
      display: flex;
      align-items: center;
      gap: 8px;
      cursor: pointer;
      padding: 4px 8px;
      border-radius: var(--radius-md);

      &:hover {
        background: var(--bg-tertiary);
      }

      .user-name {
        font-size: 14px;
        color: var(--text-primary);
      }
    }
  }
}

.content {
  flex: 1;
  padding: 24px;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

// 消息通知样式
.notification-panel {
  .notification-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px;
    border-bottom: 1px solid var(--el-border-color-light);
    font-weight: 600;
  }

  .empty-notification {
    text-align: center;
    padding: 40px 20px;
    color: var(--el-text-color-secondary);
  }

  .notification-item {
    display: flex;
    padding: 12px;
    cursor: pointer;
    transition: background-color 0.2s;
    border-bottom: 1px solid var(--el-border-color-lighter);

    &:hover {
      background-color: var(--el-fill-color-light);
    }

    &.unread {
      background-color: var(--el-color-primary-light-9);

      .notification-title {
        font-weight: 600;
      }
    }

    .notification-icon {
      margin-right: 12px;
      color: var(--el-color-primary);
      font-size: 20px;
    }

    .notification-content {
      flex: 1;
      min-width: 0;
    }

    .notification-title {
      font-size: 14px;
      line-height: 1.5;
      margin-bottom: 4px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .notification-time {
      font-size: 12px;
      color: var(--el-text-color-secondary);
    }
  }

  .notification-footer {
    padding: 8px;
    text-align: center;
    border-top: 1px solid var(--el-border-color-light);
  }
}
</style>
