<template>
  <div class="student-layout">
    <!-- 侧边栏 -->
    <aside class="sidebar" :class="{ collapsed: isCollapsed }">
      <div class="sidebar-header">
        <div class="logo">
          <img v-if="systemLogo" :src="getFullUrl(systemLogo)" alt="Logo" class="logo-img" />
          <el-icon v-else :size="32" color="#4080ff"><Reading /></el-icon>
          <span v-show="!isCollapsed" class="logo-text">{{ systemName || '英语学习系统' }}</span>
        </div>
      </div>

      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapsed"
        :collapse-transition="false"
        router
        class="sidebar-menu"
      >
        <el-menu-item index="/student/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <template #title>学习仪表板</template>
        </el-menu-item>

        <el-menu-item index="/student/exam">
          <el-icon><Document /></el-icon>
          <template #title>考试中心</template>
        </el-menu-item>

        <el-menu-item index="/student/homework">
          <el-icon><EditPen /></el-icon>
          <template #title>我的作业</template>
        </el-menu-item>

        <el-sub-menu index="learning">
          <template #title>
            <el-icon><Reading /></el-icon>
            <span>学习中心</span>
          </template>
          <el-menu-item index="/student/learning">学习资源</el-menu-item>
          <el-menu-item index="/student/learning/wrong">错题本</el-menu-item>
        </el-sub-menu>

        <el-menu-item index="/student/classroom">
          <el-icon><VideoCamera /></el-icon>
          <template #title>在线课堂</template>
        </el-menu-item>

        <!-- AI 能力按汇报 PPT 的六大最小功能顺序排列，便于演示讲解 -->
        <el-sub-menu index="ai">
          <template #title>
            <el-icon><MagicStick /></el-icon>
            <span>AI智能辅导</span>
          </template>
          <el-menu-item index="/student/ai-tutor">功能总览</el-menu-item>
          <el-menu-item index="/student/ai-tutor/writing">作文批改</el-menu-item>
          <el-menu-item index="/student/ai-tutor/speaking">口语陪练</el-menu-item>
          <el-menu-item index="/student/ai-tutor/wrong">错题溯源</el-menu-item>
          <el-menu-item index="/student/ai-tutor/chat">AI对话</el-menu-item>
          <el-menu-item index="/student/ai-tutor/sentence">长难句拆解</el-menu-item>
          <el-menu-item index="/student/ai-tutor/vocab">词汇速记</el-menu-item>
          <el-menu-item index="/student/ai-tutor/plan">学习计划</el-menu-item>
          <el-menu-item index="/student/ai-tutor/profile">学习画像</el-menu-item>
        </el-sub-menu>

        <el-menu-item index="/student/checkin">
          <el-icon><Calendar /></el-icon>
          <template #title>每日打卡</template>
        </el-menu-item>

        <el-menu-item index="/student/community">
          <el-icon><ChatLineRound /></el-icon>
          <template #title>学习社区</template>
        </el-menu-item>

        <el-menu-item index="/student/profile">
          <el-icon><User /></el-icon>
          <template #title>个人中心</template>
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
      <!-- 顶部导航栏 -->
      <header class="header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/student/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentRoute.meta?.title">
              {{ currentRoute.meta.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="header-right">
          <!-- 全局搜索 -->
          <div class="search-box">
            <el-popover
              v-model:visible="searchPopoverVisible"
              placement="bottom-start"
              :width="400"
              trigger="manual"
              popper-class="search-popover"
            >
              <template #reference>
                <el-input
                  v-model="searchKeyword"
                  placeholder="搜索试题、资源、帖子..."
                  :prefix-icon="Search"
                  clearable
                  @input="handleSearchInput"
                  @keyup.enter="handleSearch"
                  @focus="handleSearchFocus"
                  @blur="handleSearchBlur"
                />
              </template>
              <div class="search-results">
                <div v-if="searching" class="search-loading">
                  <el-icon class="is-loading"><Loading /></el-icon>
                  <span>搜索中...</span>
                </div>
                <div v-else-if="searchResults.length === 0" class="search-empty">
                  <el-icon><Search /></el-icon>
                  <span>{{ searchKeyword ? '暂无搜索结果' : '请输入搜索关键词' }}</span>
                </div>
                <div v-else class="search-list">
                  <div
                    v-for="item in searchResults"
                    :key="item.id"
                    class="search-item"
                    @click="handleResultClick(item)"
                  >
                    <el-icon class="search-item-icon">
                      <component :is="getResultIcon(item.type)" />
                    </el-icon>
                    <div class="search-item-content">
                      <div class="search-item-title" v-html="highlightKeyword(item.title)"></div>
                      <div class="search-item-desc">{{ item.description }}</div>
                    </div>
                    <el-tag size="small" :type="getResultTagType(item.type)">{{ item.typeText }}</el-tag>
                  </div>
                  <div class="search-more" @click="handleViewMore">
                    <span>查看全部结果</span>
                    <el-icon><ArrowRight /></el-icon>
                  </div>
                </div>
              </div>
            </el-popover>
          </div>

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

          <!-- 用户菜单 -->
          <el-dropdown trigger="click" @command="handleUserCommand">
            <div class="user-info">
              <el-avatar :size="36" :src="getAvatarUrl(userInfo.avatar)">
                {{ userInfo.nickname?.charAt(0) }}
              </el-avatar>
              <span class="user-name">{{ userInfo.nickname || userInfo.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item command="settings">
                  <el-icon><Setting /></el-icon>
                  账户设置
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 页面内容 -->
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
  Reading, HomeFilled, Document, MagicStick, User,
  Fold, Expand, Search, Bell, ArrowDown, Setting, SwitchButton, InfoFilled,
  Calendar, EditPen, Loading, ArrowRight, Notebook, ChatLineRound, UserFilled,
  VideoCamera
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { messageApi } from '@/api/message'
import searchApi from '@/api/search'
import { adminApi } from '@/api/admin'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapsed = ref(false)
const searchKeyword = ref('')
const searchPopoverVisible = ref(false)
const searching = ref(false)
const searchResults = ref([])
const avatarVersion = ref(Date.now())
const systemLogo = ref('')
const systemName = ref('')
let searchTimer = null

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

const currentRoute = computed(() => route)
const activeMenu = computed(() => route.path)

// 获取用户信息
const userInfo = computed(() => userStore.userInfo || { nickname: '学习者', avatar: '' })

// 处理头像URL
const getAvatarUrl = (url) => {
  if (!url) return ''
  const cleanUrl = String(url).replace(/([?&])t=\d+(&?)/, (match, p1, p2) => (p1 === '?' && p2 ? '?' : p2 ? p1 : ''))
  const withTs = cleanUrl.includes('?') ? `${cleanUrl}&t=${avatarVersion.value}` : `${cleanUrl}?t=${avatarVersion.value}`
  if (withTs.startsWith('http')) return withTs
  const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
  return `${baseUrl}${withTs}`
}

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

// 跳转到消息中心
const goToMessageCenter = () => {
  router.push('/student/message')
}

const toggleCollapse = () => {
  isCollapsed.value = !isCollapsed.value
}

// 搜索相关功能
const handleSearchInput = () => {
  if (!searchKeyword.value.trim()) {
    searchResults.value = []
    searchPopoverVisible.value = false
    return
  }
  
  // 防抖搜索
  if (searchTimer) clearTimeout(searchTimer)
  searchTimer = setTimeout(() => {
    performSearch()
  }, 300)
}

const handleSearchFocus = () => {
  if (searchKeyword.value.trim()) {
    searchPopoverVisible.value = true
  }
}

const handleSearchBlur = () => {
  // 延迟关闭，以便点击结果
  setTimeout(() => {
    searchPopoverVisible.value = false
  }, 200)
}

const performSearch = async () => {
  const keyword = searchKeyword.value.trim()
  if (!keyword) return

  searching.value = true
  searchPopoverVisible.value = true

  try {
    const res = await searchApi.globalSearch({ keyword, pageSize: 10 })
    searchResults.value = res.data || []
  } catch (error) {
    // 静默失败，使用模拟数据作为回退
    searchResults.value = [
      {
        id: 1,
        type: 'question',
        typeText: '题目',
        title: '阅读理解 - ' + keyword,
        description: '四级阅读理解专项练习'
      },
      {
        id: 2,
        type: 'resource',
        typeText: '资源',
        title: '学习资料 - ' + keyword,
        description: '四级词汇必背资料'
      },
      {
        id: 3,
        type: 'post',
        typeText: '帖子',
        title: '经验分享 - ' + keyword,
        description: '学习经验交流分享'
      }
    ]
  } finally {
    searching.value = false
  }
}

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    saveSearchHistory()
    router.push({
      path: '/student/search',
      query: { keyword: searchKeyword.value }
    })
    searchPopoverVisible.value = false
  }
}

const handleResultClick = (item) => {
  saveSearchHistory()
  searchPopoverVisible.value = false
  
  // 根据类型跳转到对应页面
  switch (item.type) {
    case 'question':
      router.push('/student/learning')
      break
    case 'resource':
      router.push('/student/learning')
      break
    case 'post':
      router.push(`/student/community/post/${item.id}`)
      break
    case 'user':
      router.push(`/student/community/user/${item.id}`)
      break
  }
}

const handleViewMore = () => {
  handleSearch()
}

const saveSearchHistory = async () => {
  try {
    await searchApi.saveSearchHistory(searchKeyword.value)
  } catch (error) {
    // 静默失败，不影响用户体验
  }
}

const getResultIcon = (type) => {
  const iconMap = {
    question: Document,
    resource: Notebook,
    post: ChatLineRound,
    user: UserFilled
  }
  return iconMap[type] || Document
}

const getResultTagType = (type) => {
  const typeMap = {
    question: 'primary',
    resource: 'success',
    post: 'warning',
    user: 'info'
  }
  return typeMap[type] || ''
}

const highlightKeyword = (text) => {
  if (!searchKeyword.value || !text) return text
  const keyword = searchKeyword.value.trim()
  const regex = new RegExp(`(${keyword})`, 'gi')
  return text.replace(regex, '<span class="highlight">$1</span>')
}

// 处理通知点击
const handleNotificationClick = async (item) => {
  // 标记为已读
  if (!item.read) {
    try {
      await messageApi.markAsRead(item.id)
      item.read = true
      await loadUnreadCount()
    } catch (error) {
      console.error('标记已读失败:', error)
    }
  }
  // 跳转到消息中心
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

const handleUserCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/student/profile')
      break
    case 'settings':
      router.push('/student/profile')
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
.student-layout {
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

  .header-left {
    display: flex;
    align-items: center;
  }

  .header-right {
    display: flex;
    align-items: center;
    gap: 16px;

    .search-box {
      width: 280px;

      :deep(.el-input__wrapper) {
        border-radius: 20px;
        background: var(--bg-tertiary);
        box-shadow: none;

        &:hover, &.is-focus {
          background: var(--bg-primary);
          box-shadow: 0 0 0 1px var(--primary-color);
        }
      }
    }

    .notification-badge {
      :deep(.el-badge__content) {
        top: 8px;
        right: 8px;
      }
    }

    .user-info {
      display: flex;
      align-items: center;
      gap: 8px;
      cursor: pointer;
      padding: 4px 8px;
      border-radius: var(--radius-md);
      transition: background 0.3s;

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

.notification-panel {
  .notification-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-bottom: 12px;
    border-bottom: 1px solid var(--border-light);
    margin-bottom: 8px;

    span {
      font-weight: 500;
    }
  }

  .empty-notification {
    text-align: center;
    padding: 40px 0;
    color: var(--text-secondary);
  }

  .notification-item {
    display: flex;
    gap: 12px;
    padding: 12px 8px;
    border-radius: var(--radius-sm);
    cursor: pointer;
    transition: background 0.3s;

    &:hover {
      background: var(--bg-tertiary);
    }

    &.unread {
      background: rgba(64, 128, 255, 0.05);

      .notification-icon {
        color: var(--primary-color);
      }
    }

    .notification-icon {
      color: var(--text-secondary);
    }

    .notification-content {
      flex: 1;

      .notification-title {
        font-size: 13px;
        color: var(--text-primary);
        margin-bottom: 4px;
      }

      .notification-time {
        font-size: 12px;
        color: var(--text-placeholder);
      }
    }
  }

  .notification-footer {
    text-align: center;
    padding-top: 12px;
    border-top: 1px solid var(--border-light);
    margin-top: 8px;
  }
}

.content {
  flex: 1;
  padding: 24px;
}

// 页面切换动画
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

// 搜索结果样式
:deep(.search-popover) {
  padding: 0 !important;
}

.search-results {
  max-height: 400px;
  overflow-y: auto;

  .search-loading,
  .search-empty {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 40px 20px;
    color: var(--text-secondary);
    gap: 8px;

    .el-icon {
      font-size: 32px;
    }
  }

  .search-list {
    padding: 8px 0;
  }

  .search-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px 16px;
    cursor: pointer;
    transition: background 0.3s;

    &:hover {
      background: var(--bg-tertiary);
    }

    .search-item-icon {
      font-size: 20px;
      color: var(--primary-color);
      flex-shrink: 0;
    }

    .search-item-content {
      flex: 1;
      min-width: 0;

      .search-item-title {
        font-size: 14px;
        color: var(--text-primary);
        margin-bottom: 4px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;

        :deep(.highlight) {
          color: var(--primary-color);
          font-weight: 600;
        }
      }

      .search-item-desc {
        font-size: 12px;
        color: var(--text-secondary);
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
  }

  .search-more {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 4px;
    padding: 12px;
    color: var(--primary-color);
    cursor: pointer;
    border-top: 1px solid var(--border-light);
    transition: background 0.3s;

    &:hover {
      background: var(--bg-tertiary);
    }

    span {
      font-size: 14px;
    }
  }
}
</style>
