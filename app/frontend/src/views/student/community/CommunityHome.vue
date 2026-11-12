<template>
  <div class="community-home">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>学习社区</h2>
      <p>与志同道合的学习者交流分享</p>
    </div>

    <!-- 社区导航 -->
    <div class="community-nav">
      <div
        v-for="nav in navItems"
        :key="nav.value"
        class="nav-item"
        :class="{ active: activeNav === nav.value }"
        @click="activeNav = nav.value"
      >
        <el-icon :size="20"><component :is="nav.icon" /></el-icon>
        <span>{{ nav.label }}</span>
      </div>
    </div>

    <div class="community-content">
      <!-- 帖子类标签页 (论坛/问答/经验分享) -->
      <template v-if="activeNav === 'forum' || activeNav === 'qa' || activeNav === 'share'">
        <!-- 左侧主内容 -->
        <div class="main-content">
          <!-- 发帖入口 -->
          <div class="post-entry" @click="showPostDialog = true">
            <el-avatar :size="40" :src="getUserAvatar(userStore.avatar)" :key="avatarKey">
              {{ userStore.username?.charAt(0)?.toUpperCase() || 'U' }}
            </el-avatar>
            <div class="entry-input">{{ postEntryPlaceholder }}</div>
            <el-button type="primary">发帖</el-button>
          </div>

          <!-- 帖子列表 -->
          <div class="post-list">
            <div v-if="posts.length === 0 && !loading" class="empty-tip">暂无帖子，快来发布第一条吧</div>
            <div v-for="post in posts" :key="post.id" class="post-card">
              <div class="post-header">
                <el-avatar :size="40" :src="getUserAvatar(post.avatar)" :key="`post-${post.id}-${avatarKey}`">
                  {{ post.author.charAt(0)?.toUpperCase() || 'U' }}
                </el-avatar>
                <div class="post-author-info">
                  <div class="author-name">{{ post.author }}</div>
                  <div class="post-time">{{ post.time }}</div>
                </div>
                <el-tag v-if="post.isTop" type="danger" size="small">置顶</el-tag>
                <el-tag v-if="post.isEssence" type="warning" size="small">精华</el-tag>
              </div>
              <div class="post-content" @click="viewPost(post)">
                <h3 class="post-title">{{ post.title }}</h3>
                <p class="post-summary">{{ post.summary }}</p>
                <div v-if="post.images && post.images.length" class="post-images">
                  <img v-for="(img, index) in post.images.slice(0, 3)" :key="index" :src="img" />
                </div>
              </div>
              <div class="post-tags">
                <el-tag v-for="tag in post.tags" :key="tag" size="small" type="info">
                  {{ tag }}
                </el-tag>
              </div>
              <div class="post-footer">
                <div class="post-stats">
                  <span class="stat-item" @click="toggleLike(post)">
                    <el-icon :color="post.isLiked ? '#ff4d4f' : ''">
                      <component :is="post.isLiked ? 'StarFilled' : 'Star'" />
                    </el-icon>
                    {{ post.likes }}
                  </span>
                  <span class="stat-item">
                    <el-icon><ChatDotRound /></el-icon>
                    {{ post.comments }}
                  </span>
                  <span class="stat-item">
                    <el-icon><View /></el-icon>
                    {{ post.views }}
                  </span>
                </div>
                <div class="post-actions">
                  <el-button type="primary" link size="small" @click="viewPost(post)">
                    查看详情
                  </el-button>
                </div>
              </div>
            </div>
          </div>

          <!-- 加载更多 -->
          <div class="load-more">
            <el-button :loading="loading" @click="loadMore">加载更多</el-button>
          </div>
        </div>

        <!-- 右侧边栏 -->
        <div class="sidebar">
          <!-- 热门话题 -->
          <div class="sidebar-card">
            <h3>热门话题</h3>
            <div class="hot-topics">
              <div v-for="(topic, index) in hotTopics" :key="topic.id" class="topic-item">
                <span class="topic-rank" :class="{ top: index < 3 }">{{ index + 1 }}</span>
                <span class="topic-name">{{ topic.name }}</span>
                <span class="topic-count">{{ topic.count }}讨论</span>
              </div>
            </div>
          </div>

          <!-- 活跃用户 -->
          <div class="sidebar-card">
            <h3>活跃用户</h3>
            <div class="active-users">
              <div v-for="user in activeUsers" :key="user.id" class="user-item">
                <el-avatar :size="36" :src="getUserAvatar(user.avatar)" :key="`user-${user.id}-${avatarKey}`">
                  {{ user.name.charAt(0)?.toUpperCase() || 'U' }}
                </el-avatar>
                <div class="user-info">
                  <div class="user-name">{{ user.name }}</div>
                  <div class="user-posts">发帖 {{ user.posts }}</div>
                </div>
                <el-button size="small" :type="user.isFollowed ? 'default' : 'primary'" @click="toggleFollow(user)">
                  {{ user.isFollowed ? '已关注' : '关注' }}
                </el-button>
              </div>
            </div>
          </div>

          <!-- 学习小组 -->
          <div class="sidebar-card">
            <h3>推荐小组</h3>
            <div class="group-list">
              <div v-for="group in groups" :key="group.id" class="group-item">
                <div class="group-icon">
                  <el-icon :size="24"><UserFilled /></el-icon>
                </div>
                <div class="group-info">
                  <div class="group-name">{{ group.name }}</div>
                  <div class="group-members">{{ group.members }}成员</div>
                </div>
                <el-button size="small" @click="joinGroup(group)">加入</el-button>
              </div>
            </div>
          </div>
        </div>
      </template>

      <!-- 学习小组标签页 -->
      <div v-else-if="activeNav === 'group'" class="group-content">
        <!-- 我的小组 -->
        <el-card class="group-section">
          <template #header>
            <div style="display:flex;justify-content:space-between;align-items:center">
              <span style="font-weight:600">我的小组</span>
              <el-button type="primary" size="small" @click="showCreateGroupDialog = true">创建小组</el-button>
            </div>
          </template>
          <div v-if="myGroups.length === 0" class="empty-tip">你还没有加入任何小组</div>
          <div v-else class="group-grid">
            <div v-for="group in myGroups" :key="group.id" class="group-card">
              <div class="group-card-icon">
                <el-icon :size="32"><UserFilled /></el-icon>
              </div>
              <div class="group-card-info">
                <div class="group-card-name">{{ group.groupName }}</div>
                <div class="group-card-desc">{{ group.groupIntro || '暂无简介' }}</div>
                <div class="group-card-meta">{{ group.currentMembers || 0 }} 成员</div>
              </div>
              <el-button size="small" @click="handleLeaveGroup(group)">退出</el-button>
            </div>
          </div>
        </el-card>

        <!-- 发现小组 -->
        <el-card class="group-section">
          <template #header>
            <div style="display:flex;justify-content:space-between;align-items:center">
              <span style="font-weight:600">发现小组</span>
              <el-input
                v-model="groupKeyword"
                placeholder="搜索小组"
                style="width:200px"
                clearable
                @keyup.enter="searchGroups"
              >
                <template #append>
                  <el-button @click="searchGroups">搜索</el-button>
                </template>
              </el-input>
            </div>
          </template>
          <div v-if="allGroups.length === 0" class="empty-tip">暂无小组</div>
          <div v-else class="group-grid">
            <div v-for="group in allGroups" :key="group.id" class="group-card">
              <div class="group-card-icon">
                <el-icon :size="32"><UserFilled /></el-icon>
              </div>
              <div class="group-card-info">
                <div class="group-card-name">{{ group.groupName }}</div>
                <div class="group-card-desc">{{ group.groupIntro || '暂无简介' }}</div>
                <div class="group-card-meta">{{ group.currentMembers || 0 }}/{{ group.maxMembers || '不限' }} 成员</div>
              </div>
              <el-button type="primary" size="small" @click="handleJoinGroup(group)">加入</el-button>
            </div>
          </div>
          <div style="text-align:center;margin-top:16px">
            <el-pagination
              v-model:current-page="groupPage"
              :page-size="10"
              :total="groupTotal"
              layout="prev, pager, next"
              @current-change="loadAllGroups"
            />
          </div>
        </el-card>
      </div>

      <!-- 每日打卡标签页 -->
      <div v-else-if="activeNav === 'checkin'" class="checkin-content">
        <!-- 打卡卡片 -->
        <el-card class="checkin-card">
          <div class="checkin-header">
            <div class="date-info">
              <h2>{{ checkinCurrentDate }}</h2>
              <p>{{ checkinWeekDay }}</p>
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
              <template #header><span>打卡统计</span></template>
              <div class="stats-content">
                <div class="checkin-stat-item">
                  <div class="stat-value">{{ checkinStats.totalDays || 0 }}</div>
                  <div class="stat-label">累计打卡</div>
                </div>
                <div class="checkin-stat-item">
                  <div class="stat-value">{{ checkinStats.continuousDays || 0 }}</div>
                  <div class="stat-label">连续打卡</div>
                </div>
                <div class="checkin-stat-item">
                  <div class="stat-value">{{ checkinStats.monthDays || 0 }}</div>
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
                    <span class="current-month">{{ checkinCalendarMonth }}</span>
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
                    v-for="(day, index) in checkinCalendarDays"
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
          <template #header><span>打卡排行榜</span></template>
          <el-table :data="checkinRankingList" stripe>
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
    </div>

    <!-- 发帖弹窗 -->
    <el-dialog v-model="showPostDialog" title="发布帖子" width="600px">
      <el-form :model="postForm" label-position="top">
        <el-form-item label="标题">
          <el-input v-model="postForm.title" placeholder="请输入帖子标题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input
            v-model="postForm.content"
            type="textarea"
            :rows="6"
            placeholder="分享你的学习心得..."
          />
        </el-form-item>
        <el-form-item label="话题标签">
          <el-select v-model="postForm.tags" multiple placeholder="选择话题标签">
            <el-option label="四级备考" value="cet4" />
            <el-option label="六级备考" value="cet6" />
            <el-option label="学习方法" value="method" />
            <el-option label="经验分享" value="experience" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPostDialog = false">取消</el-button>
        <el-button type="primary" @click="submitPost">发布</el-button>
      </template>
    </el-dialog>

    <!-- 创建小组弹窗 -->
    <el-dialog v-model="showCreateGroupDialog" title="创建学习小组" width="500px">
      <el-form :model="groupForm" label-position="top">
        <el-form-item label="小组名称" required>
          <el-input v-model="groupForm.groupName" placeholder="请输入小组名称" />
        </el-form-item>
        <el-form-item label="小组简介">
          <el-input v-model="groupForm.groupIntro" type="textarea" :rows="3" placeholder="介绍一下你的小组..." />
        </el-form-item>
        <el-form-item label="最大人数">
          <el-input-number v-model="groupForm.maxMembers" :min="2" :max="200" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateGroupDialog = false">取消</el-button>
        <el-button type="primary" @click="handleCreateGroup">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ChatDotRound, View, Star, StarFilled, UserFilled,
  ChatLineSquare, QuestionFilled, Share, Clock,
  Check, ArrowLeft, ArrowRight
} from '@element-plus/icons-vue'
import { communityApi } from '@/api/community'
import { groupApi } from '@/api/group'
import { checkinApi } from '@/api/checkin'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

// 导航项
const navItems = [
  { label: '论坛', value: 'forum', icon: ChatLineSquare },
  { label: '问答', value: 'qa', icon: QuestionFilled },
  { label: '学习小组', value: 'group', icon: UserFilled },
  { label: '经验分享', value: 'share', icon: Share },
  { label: '每日打卡', value: 'checkin', icon: Clock }
]

const activeNav = ref('forum')
const loading = ref(false)
const showPostDialog = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const avatarKey = ref(Date.now())

// 帖子表单
const postForm = reactive({
  title: '',
  content: '',
  tags: []
})

// 帖子列表
const posts = ref([])

// 热门话题
const hotTopics = ref([])

// 活跃用户
const activeUsers = ref([])

// 学习小组
const groups = ref([])

// postType 映射
const tabToPostType = { forum: null, qa: 'QUESTION', share: 'SHARE' }
const tabToCreateType = { forum: 'DISCUSSION', qa: 'QUESTION', share: 'SHARE' }

// 发帖入口占位文字
const postEntryPlaceholder = computed(() => {
  const map = { forum: '分享你的学习心得...', qa: '提出你的问题...', share: '分享你的经验...' }
  return map[activeNav.value] || '分享你的学习心得...'
})

// ---- 学习小组状态 ----
const myGroups = ref([])
const allGroups = ref([])
const groupTotal = ref(0)
const groupPage = ref(1)
const groupKeyword = ref('')
const showCreateGroupDialog = ref(false)
const groupForm = reactive({
  groupName: '',
  groupIntro: '',
  maxMembers: 50
})

// ---- 每日打卡状态 ----
const isCheckedIn = ref(false)
const checkinLoading = ref(false)
const checkinStats = reactive({
  totalDays: 0,
  continuousDays: 0,
  monthDays: 0
})
const checkinRankingList = ref([])
const checkedDates = ref([])
const currentCalendarDate = ref(new Date())
const weekDays = ['日', '一', '二', '三', '四', '五', '六']

const checkinCurrentDate = computed(() => {
  const now = new Date()
  return `${now.getFullYear()}年${now.getMonth() + 1}月${now.getDate()}日`
})

const checkinWeekDay = computed(() => {
  const now = new Date()
  return `星期${weekDays[now.getDay()]}`
})

const checkinCalendarMonth = computed(() => {
  const date = currentCalendarDate.value
  return `${date.getFullYear()}年${date.getMonth() + 1}月`
})

const checkinCalendarDays = computed(() => {
  const date = currentCalendarDate.value
  const year = date.getFullYear()
  const month = date.getMonth()
  const firstDay = new Date(year, month, 1)
  const lastDay = new Date(year, month + 1, 0)
  const startDay = firstDay.getDay()
  const totalDaysInMonth = lastDay.getDate()
  const days = []
  const today = new Date()
  const prevMonthLastDay = new Date(year, month, 0).getDate()
  for (let i = startDay - 1; i >= 0; i--) {
    days.push({ day: prevMonthLastDay - i, otherMonth: true, checked: false, isToday: false })
  }
  for (let i = 1; i <= totalDaysInMonth; i++) {
    const dateStr = `${year}-${String(month + 1).padStart(2, '0')}-${String(i).padStart(2, '0')}`
    const isToday = today.getFullYear() === year && today.getMonth() === month && today.getDate() === i
    days.push({ day: i, otherMonth: false, checked: checkedDates.value.includes(dateStr), isToday })
  }
  const remaining = 42 - days.length
  for (let i = 1; i <= remaining; i++) {
    days.push({ day: i, otherMonth: true, checked: false, isToday: false })
  }
  return days
})

// 获取头像 URL，添加时间戳避免缓存
const getUserAvatar = (avatar) => {
  if (!avatar) return ''
  // 如果是完整URL，直接返回
  if (avatar.startsWith('http://') || avatar.startsWith('https://')) {
    return `${avatar}?t=${avatarKey.value}`
  }
  // 如果是相对路径，添加基础URL
  const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
  return `${baseURL}${avatar.startsWith('/') ? '' : '/'}${avatar}?t=${avatarKey.value}`
}

// 加载帖子列表
const loadPosts = async (append = false) => {
  loading.value = true
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      postType: tabToPostType[activeNav.value] || null
    }
    const res = await communityApi.getPostList(params)
    if (res.data?.records) {
      const newPosts = res.data.records.map(item => ({
        id: item.id,
        author: item.username || '匿名用户',
        avatar: item.avatar || '',
        time: formatTime(item.createTime),
        title: item.title,
        summary: item.content?.substring(0, 150) || '',
        tags: item.tags || [],
        likes: item.likeCount || 0,
        comments: item.replyCount || 0,
        views: item.viewCount || 0,
        isTop: item.isTopped === 1,
        isEssence: item.isFeatured === 1,
        isLiked: item.isLiked || false,
        images: []
      }))
      if (append) {
        posts.value = [...posts.value, ...newPosts]
      } else {
        posts.value = newPosts
      }
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('加载帖子失败:', error)
    ElMessage.error('加载帖子失败')
  } finally {
    loading.value = false
  }
}

// 加载推荐小组
const loadGroups = async () => {
  try {
    const res = await groupApi.getRecommendedGroups(5)
    if (res.data) {
      groups.value = res.data.map(item => ({
        id: item.id,
        name: item.groupName,
        members: item.currentMembers || 0
      }))
    }
  } catch (error) {
    console.error('加载小组失败:', error)
  }
}

// 加载热门话题
const loadHotTopics = async () => {
  try {
    const res = await communityApi.getHotTopics()
    if (res?.data && Array.isArray(res.data)) {
      hotTopics.value = res.data.slice(0, 10).map(item => ({
        id: item.id || Math.random(),
        name: item.name || item.topic || '无标题',
        count: item.count || item.postCount || 0
      }))
    } else {
      // 默认热门话题
      hotTopics.value = [
        { id: 1, name: '四级备考经验分享', count: 128 },
        { id: 2, name: '六级学习方法', count: 95 },
        { id: 3, name: '词汇记忆技巧', count: 87 },
        { id: 4, name: '听力提升忆录', count: 76 }
      ]
    }
  } catch (error) {
    console.error('加载热门话题失败:', error)
    // 错误时使用默认数据
    hotTopics.value = [
      { id: 1, name: '四级备考经验分享', count: 128 },
      { id: 2, name: '六级学习方法', count: 95 },
      { id: 3, name: '词汇记忆技巧', count: 87 },
      { id: 4, name: '听力提升忆录', count: 76 }
    ]
  }
}

// 加载活跃用户
const loadActiveUsers = async () => {
  try {
    const res = await communityApi.getActiveUsers()
    if (res?.data && Array.isArray(res.data)) {
      activeUsers.value = res.data.slice(0, 8).map(item => ({
        id: item.id || item.userId || Math.random(),
        name: item.username || item.nickname || '匿名用户',
        avatar: item.avatar || '',
        posts: item.postCount || 0,
        isFollowed: item.isFollowed || false
      }))
    } else {
      // 默认活跃用户
      activeUsers.value = [
        { id: 1, name: 'student1', avatar: '', posts: 156, isFollowed: false },
        { id: 2, name: '学习达人', avatar: '', posts: 142, isFollowed: false },
        { id: 3, name: '英语爱好者', avatar: '', posts: 128, isFollowed: false },
        { id: 4, name: 'CET4大神', avatar: '', posts: 115, isFollowed: false }
      ]
    }
  } catch (error) {
    console.error('加载活跃用户失败:', error)
    // 错误时使用默认数据
    activeUsers.value = [
      { id: 1, name: 'student1', avatar: '', posts: 156, isFollowed: false },
      { id: 2, name: '学习达人', avatar: '', posts: 142, isFollowed: false },
      { id: 3, name: '英语爱好者', avatar: '', posts: 128, isFollowed: false },
      { id: 4, name: 'CET4大神', avatar: '', posts: 115, isFollowed: false }
    ]
  }
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  const hours = Math.floor(diff / (1000 * 60 * 60))
  if (hours < 1) return '刚刚'
  if (hours < 24) return `${hours}小时前`
  const days = Math.floor(hours / 24)
  if (days < 7) return `${days}天前`
  return time.substring(0, 10)
}

const viewPost = (post) => {
  router.push(`/student/community/post/${post.id}`)
}

const toggleLike = async (post) => {
  const wasLiked = post.isLiked
  const oldLikes = post.likes
  // 乐观更新
  post.isLiked = !wasLiked
  post.likes = wasLiked ? oldLikes - 1 : oldLikes + 1
  try {
    const res = await communityApi.likePost(post.id)
    // 后端返回的是 toggle 后的状态，确保一致
    if (res.data && typeof res.data.isLiked !== 'undefined') {
      post.isLiked = res.data.isLiked
    }
    ElMessage.success(post.isLiked ? '点赞成功' : '已取消点赞')
  } catch (error) {
    // 回滚
    post.isLiked = wasLiked
    post.likes = oldLikes
    console.error('点赞失败:', error)
    ElMessage.error('操作失败，请重试')
  }
}

const loadMore = () => {
  if (loading.value) return
  if (posts.value.length >= total.value) {
    ElMessage.info('没有更多帖子了')
    return
  }
  currentPage.value++
  loadPosts(true)
}

const submitPost = async () => {
  if (!postForm.title || !postForm.content) {
    ElMessage.warning('请填写完整信息')
    return
  }
  try {
    await communityApi.createPost({
      title: postForm.title,
      content: postForm.content,
      tags: postForm.tags,
      postType: tabToCreateType[activeNav.value] || 'DISCUSSION'
    })
    ElMessage.success('发布成功')
    showPostDialog.value = false
    postForm.title = ''
    postForm.content = ''
    postForm.tags = []
    currentPage.value = 1
    loadPosts()
  } catch (error) {
    console.error('发布失败:', error)
    ElMessage.error('发布失败，请重试')
  }
}

const joinGroup = async (group) => {
  try {
    await groupApi.joinGroup(group.id)
    ElMessage.success('加入成功')
    group.joined = true
  } catch (error) {
    console.error('加入失败:', error)
    ElMessage.error('加入失败')
  }
}

const toggleFollow = async (user) => {
  const wasFollowed = user.isFollowed
  // 乐观更新
  user.isFollowed = !wasFollowed
  try {
    if (wasFollowed) {
      await communityApi.unfollowUser(user.id)
      ElMessage.success('已取消关注')
    } else {
      await communityApi.followUser(user.id)
      ElMessage.success('关注成功')
    }
  } catch (error) {
    // 回滚
    user.isFollowed = wasFollowed
    console.error('操作失败:', error)
    ElMessage.error('操作失败，请重试')
  }
}

// ---- 学习小组方法 ----
const loadMyGroups = async () => {
  try {
    const res = await groupApi.getMyGroups()
    myGroups.value = res.data || []
  } catch (error) {
    console.error('加载我的小组失败:', error)
  }
}

const loadAllGroups = async () => {
  try {
    const res = await groupApi.getGroupList({
      pageNum: groupPage.value,
      pageSize: 10,
      keyword: groupKeyword.value || undefined
    })
    if (res.data?.records) {
      allGroups.value = res.data.records
      groupTotal.value = res.data.total || 0
    } else if (Array.isArray(res.data)) {
      allGroups.value = res.data
      groupTotal.value = res.data.length
    }
  } catch (error) {
    console.error('加载小组列表失败:', error)
  }
}

const handleJoinGroup = async (group) => {
  try {
    await groupApi.joinGroup(group.id)
    ElMessage.success('加入成功')
    loadMyGroups()
    loadAllGroups()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '加入失败')
  }
}

const handleLeaveGroup = async (group) => {
  try {
    await groupApi.leaveGroup(group.id)
    ElMessage.success('已退出小组')
    loadMyGroups()
    loadAllGroups()
  } catch (error) {
    ElMessage.error('退出失败')
  }
}

const handleCreateGroup = async () => {
  if (!groupForm.groupName) {
    ElMessage.warning('请输入小组名称')
    return
  }
  try {
    await groupApi.createGroup({
      groupName: groupForm.groupName,
      groupIntro: groupForm.groupIntro,
      maxMembers: groupForm.maxMembers
    })
    ElMessage.success('创建成功')
    showCreateGroupDialog.value = false
    groupForm.groupName = ''
    groupForm.groupIntro = ''
    groupForm.maxMembers = 50
    loadMyGroups()
    loadAllGroups()
  } catch (error) {
    ElMessage.error('创建失败')
  }
}

const searchGroups = () => {
  groupPage.value = 1
  loadAllGroups()
}

// ---- 每日打卡方法 ----
const handleCheckin = async () => {
  checkinLoading.value = true
  try {
    const res = await checkinApi.checkin()
    isCheckedIn.value = true
    ElMessage.success(res.data?.message || '打卡成功！')
    fetchCheckinStats()
    fetchCheckinCalendar()
    fetchCheckinRanking()
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
  }
}

const fetchCheckinStats = async () => {
  try {
    const res = await checkinApi.getStats()
    Object.assign(checkinStats, res.data)
  } catch (error) {
    console.error('获取统计失败:', error)
  }
}

const fetchCheckinCalendar = async () => {
  try {
    const date = currentCalendarDate.value
    const res = await checkinApi.getCalendar({
      year: date.getFullYear(),
      month: date.getMonth() + 1
    })
    // 兼容 LocalDate 序列化为数组 [2025, 1, 15] 或字符串 "2025-01-15"
    checkedDates.value = (res.data || []).map(item => {
      const d = item.checkinDate || item
      if (Array.isArray(d)) {
        return `${d[0]}-${String(d[1]).padStart(2, '0')}-${String(d[2]).padStart(2, '0')}`
      }
      return String(d)
    })
  } catch (error) {
    console.error('获取日历失败:', error)
  }
}

const fetchCheckinRanking = async () => {
  try {
    const res = await checkinApi.getRanking({ limit: 10 })
    checkinRankingList.value = res.data || []
  } catch (error) {
    console.error('获取排行榜失败:', error)
  }
}

const prevMonth = () => {
  const date = currentCalendarDate.value
  currentCalendarDate.value = new Date(date.getFullYear(), date.getMonth() - 1, 1)
  fetchCheckinCalendar()
}

const nextMonth = () => {
  const date = currentCalendarDate.value
  currentCalendarDate.value = new Date(date.getFullYear(), date.getMonth() + 1, 1)
  fetchCheckinCalendar()
}

// 监听导航变化
watch(activeNav, (val) => {
  currentPage.value = 1
  if (['forum', 'qa', 'share'].includes(val)) {
    loadPosts()
  } else if (val === 'group') {
    loadMyGroups()
    loadAllGroups()
  } else if (val === 'checkin') {
    fetchTodayStatus()
    fetchCheckinStats()
    fetchCheckinCalendar()
    fetchCheckinRanking()
  }
})

onMounted(() => {
  loadPosts()
  loadGroups()
  loadHotTopics()
  loadActiveUsers()
})
</script>

<style lang="scss" scoped>
.community-home {
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

  .community-nav {
    display: flex;
    gap: 12px;
    margin-bottom: 24px;
    padding: 16px;
    background: var(--bg-primary);
    border-radius: var(--radius-lg);

    .nav-item {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 10px 20px;
      border-radius: var(--radius-md);
      cursor: pointer;
      color: var(--text-secondary);
      transition: all 0.3s;

      &:hover {
        background: var(--bg-tertiary);
        color: var(--primary-color);
      }

      &.active {
        background: var(--primary-color);
        color: #fff;
      }
    }
  }

  .community-content {
    display: grid;
    grid-template-columns: 1fr 320px;
    gap: 24px;

    @media (max-width: 1200px) {
      grid-template-columns: 1fr;
    }
  }

  .post-entry {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 20px;
    background: var(--bg-primary);
    border-radius: var(--radius-lg);
    margin-bottom: 24px;
    cursor: pointer;

    .entry-input {
      flex: 1;
      padding: 12px 16px;
      background: var(--bg-secondary);
      border-radius: var(--radius-md);
      color: var(--text-placeholder);
    }
  }

  .post-list {
    .empty-tip {
      text-align: center;
      color: var(--text-placeholder);
      padding: 40px 0;
    }

    .post-card {
      background: var(--bg-primary);
      border-radius: var(--radius-lg);
      padding: 24px;
      margin-bottom: 16px;
      box-shadow: var(--shadow-sm);

      .post-header {
        display: flex;
        align-items: center;
        gap: 12px;
        margin-bottom: 16px;

        .post-author-info {
          flex: 1;

          .author-name {
            font-weight: 500;
            color: var(--text-primary);
          }

          .post-time {
            font-size: 12px;
            color: var(--text-placeholder);
          }
        }
      }

      .post-content {
        cursor: pointer;

        .post-title {
          font-size: 16px;
          font-weight: 600;
          color: var(--text-primary);
          margin-bottom: 8px;

          &:hover {
            color: var(--primary-color);
          }
        }

        .post-summary {
          font-size: 14px;
          color: var(--text-secondary);
          line-height: 1.6;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }

        .post-images {
          display: flex;
          gap: 8px;
          margin-top: 12px;

          img {
            width: 120px;
            height: 80px;
            object-fit: cover;
            border-radius: var(--radius-sm);
          }
        }
      }

      .post-tags {
        display: flex;
        gap: 8px;
        margin: 12px 0;
      }

      .post-footer {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding-top: 12px;
        border-top: 1px solid var(--border-light);

        .post-stats {
          display: flex;
          gap: 24px;

          .stat-item {
            display: flex;
            align-items: center;
            gap: 4px;
            font-size: 13px;
            color: var(--text-secondary);
            cursor: pointer;

            &:hover {
              color: var(--primary-color);
            }
          }
        }
      }
    }
  }

  .load-more {
    text-align: center;
    padding: 24px 0;
  }

  .sidebar {
    .sidebar-card {
      background: var(--bg-primary);
      border-radius: var(--radius-lg);
      padding: 20px;
      margin-bottom: 20px;

      h3 {
        font-size: 15px;
        font-weight: 600;
        color: var(--text-primary);
        margin-bottom: 16px;
      }
    }

    .hot-topics {
      .topic-item {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 10px 0;
        border-bottom: 1px solid var(--border-light);

        &:last-child {
          border-bottom: none;
        }

        .topic-rank {
          width: 20px;
          height: 20px;
          display: flex;
          align-items: center;
          justify-content: center;
          border-radius: 4px;
          font-size: 12px;
          font-weight: 600;
          background: var(--bg-tertiary);
          color: var(--text-secondary);

          &.top {
            background: var(--primary-color);
            color: #fff;
          }
        }

        .topic-name {
          flex: 1;
          font-size: 14px;
          color: var(--text-primary);
        }

        .topic-count {
          font-size: 12px;
          color: var(--text-placeholder);
        }
      }
    }

    .active-users {
      .user-item {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 10px 0;

        .user-info {
          flex: 1;

          .user-name {
            font-size: 14px;
            font-weight: 500;
            color: var(--text-primary);
          }

          .user-posts {
            font-size: 12px;
            color: var(--text-placeholder);
          }
        }
      }
    }

    .group-list {
      .group-item {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 10px 0;

        .group-icon {
          width: 40px;
          height: 40px;
          border-radius: var(--radius-md);
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          display: flex;
          align-items: center;
          justify-content: center;
          color: #fff;
        }

        .group-info {
          flex: 1;

          .group-name {
            font-size: 14px;
            font-weight: 500;
            color: var(--text-primary);
          }

          .group-members {
            font-size: 12px;
            color: var(--text-placeholder);
          }
        }
      }
    }
  }

  // 学习小组标签页
  .group-content {
    grid-column: 1 / -1;

    .group-section {
      margin-bottom: 20px;
    }

    .group-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
      gap: 16px;
    }

    .group-card {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 16px;
      background: var(--bg-secondary);
      border-radius: var(--radius-md);

      .group-card-icon {
        width: 48px;
        height: 48px;
        border-radius: var(--radius-md);
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;
        flex-shrink: 0;
      }

      .group-card-info {
        flex: 1;
        min-width: 0;

        .group-card-name {
          font-weight: 500;
          color: var(--text-primary);
        }

        .group-card-desc {
          font-size: 12px;
          color: var(--text-secondary);
          margin-top: 4px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .group-card-meta {
          font-size: 12px;
          color: var(--text-placeholder);
          margin-top: 4px;
        }
      }
    }

    .empty-tip {
      text-align: center;
      color: var(--text-placeholder);
      padding: 24px 0;
    }
  }

  // 每日打卡标签页
  .checkin-content {
    grid-column: 1 / -1;

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

    .checkin-stat-item .stat-value {
      font-size: 32px;
      font-weight: bold;
      color: #409eff;
    }

    .checkin-stat-item .stat-label {
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

      &.other-month {
        color: #c0c4cc;
      }

      &.today {
        background: #ecf5ff;
        border: 1px solid #409eff;
      }

      &.checked {
        background: #67c23a;
        color: white;

        .check-icon {
          position: absolute;
          bottom: 2px;
          font-size: 12px;
        }
      }
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

      &.rank-1 { background: #f7ba2a; }
      &.rank-2 { background: #c0c4cc; }
      &.rank-3 { background: #cd7f32; }
    }
  }
}
</style>
