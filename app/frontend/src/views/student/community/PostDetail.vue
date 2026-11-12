<template>
  <div class="post-detail">
    <!-- 返回按钮 -->
    <div class="page-header">
      <el-button @click="goBack" :icon="ArrowLeft">返回社区</el-button>
    </div>

    <!-- 错误提示 -->
    <el-alert v-if="loadError" :title="loadError" type="error" show-icon :closable="false" style="margin-bottom: 16px">
      <el-button type="primary" link @click="loadPost">重新加载</el-button>
    </el-alert>

    <!-- 帖子内容 -->
    <el-card v-if="!loadError" class="post-card" v-loading="loading">
      <div class="post-header">
        <el-avatar :size="48" :src="getUserAvatar(post.avatar)" :key="avatarKey">
          {{ post.username?.charAt(0)?.toUpperCase() || 'U' }}
        </el-avatar>
        <div class="author-info">
          <div class="author-name">{{ post.username }}</div>
          <div class="post-time">{{ formatTime(post.createTime) }}</div>
        </div>
        <div class="post-tags">
          <el-tag v-if="post.isTopped" type="danger" size="small">置顶</el-tag>
          <el-tag v-if="post.isFeatured" type="warning" size="small">精华</el-tag>
        </div>
      </div>

      <h1 class="post-title">{{ post.title }}</h1>
      <div class="post-content">{{ post.content }}</div>

      <div class="post-footer">
        <div class="post-stats">
          <span class="stat-item" @click="handleLike">
            <el-icon :color="post.isLiked ? '#ff4d4f' : ''">
              <component :is="post.isLiked ? 'StarFilled' : 'Star'" />
            </el-icon>
            {{ post.likeCount || 0 }}
          </span>
          <span class="stat-item">
            <el-icon><ChatDotRound /></el-icon>
            {{ post.replyCount || 0 }}
          </span>
          <span class="stat-item">
            <el-icon><View /></el-icon>
            {{ post.viewCount || 0 }}
          </span>
        </div>
        <div class="post-actions">
          <el-button @click="handleCollect" :type="post.isCollected ? 'warning' : 'default'" size="small">
            <el-icon><Collection /></el-icon>
            {{ post.isCollected ? '已收藏' : '收藏' }}
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 评论区 -->
    <el-card class="reply-card">
      <template #header>
        <div class="reply-header">
          <span>评论 ({{ post.replyCount || total || 0 }})</span>
        </div>
      </template>

      <!-- 发表评论 -->
      <div class="reply-input">
        <el-input
          v-model="replyContent"
          type="textarea"
          :rows="3"
          placeholder="发表你的评论..."
        />
        <div class="reply-actions">
          <el-button type="primary" @click="submitReply" :loading="submitting">
            发表评论
          </el-button>
        </div>
      </div>

      <!-- 评论列表 -->
      <div class="reply-list">
        <div v-for="reply in replies" :key="reply.id" class="reply-item">
          <el-avatar :size="36" :src="getUserAvatar(reply.avatar)" :key="`reply-${reply.id}-${avatarKey}`">
            {{ reply.username?.charAt(0)?.toUpperCase() || 'U' }}
          </el-avatar>
          <div class="reply-content">
            <div class="reply-author">
              <span class="author-name">{{ reply.username }}</span>
              <span class="reply-time">{{ formatTime(reply.createTime) }}</span>
            </div>
            <div class="reply-text">{{ reply.content }}</div>
            <div class="reply-footer">
              <span class="action-item" @click="likeReply(reply)">
                <el-icon><Star /></el-icon>
                {{ reply.likeCount || 0 }}
              </span>
              <span class="action-item" @click="replyTo(reply)">
                <el-icon><ChatDotRound /></el-icon>
                回复
              </span>
            </div>
          </div>
        </div>

        <el-empty v-if="replies.length === 0" description="暂无评论，快来发表第一条评论吧" />
      </div>

      <!-- 分页 -->
      <el-pagination
        v-if="total > pageSize"
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        @current-change="loadReplies"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowLeft, Star, StarFilled, ChatDotRound, View, Collection
} from '@element-plus/icons-vue'
import { communityApi } from '@/api/community'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const submitting = ref(false)
const post = ref({})
const replies = ref([])
const replyContent = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loadError = ref('')
const avatarKey = ref(Date.now())

// 获取头像 URL，添加时间戳避免缓存
const getUserAvatar = (avatar) => {
  if (!avatar) return ''
  if (avatar.startsWith('http://') || avatar.startsWith('https://')) {
    return `${avatar}?t=${avatarKey.value}`
  }
  const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
  return `${baseURL}${avatar.startsWith('/') ? '' : '/'}${avatar}?t=${avatarKey.value}`
}

const goBack = () => {
  router.push('/student/community')
}

const loadPost = async () => {
  loading.value = true
  loadError.value = ''
  try {
    const id = route.params.id
    if (!id) {
      loadError.value = '帖子ID无效'
      ElMessage.error('帖子ID无效')
      return
    }
    const res = await communityApi.getPostDetail(id)
    if (res.data) {
      post.value = res.data
    }
  } catch (error) {
    console.error('加载帖子失败:', error)
    loadError.value = '加载帖子失败，请重试'
    ElMessage.error('加载帖子失败')
  } finally {
    loading.value = false
  }
}

const loadReplies = async () => {
  try {
    const res = await communityApi.getReplyList(route.params.id, {
      pageNum: currentPage.value,
      pageSize: pageSize.value
    })
    if (res.data) {
      replies.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('加载评论失败:', error)
  }
}

const handleLike = async () => {
  const wasLiked = post.value.isLiked
  const oldCount = post.value.likeCount || 0
  // 乐观更新
  post.value.isLiked = !wasLiked
  post.value.likeCount = wasLiked ? oldCount - 1 : oldCount + 1
  try {
    // 后端是toggle模式，统一调用likePost
    await communityApi.likePost(route.params.id)
    ElMessage.success(wasLiked ? '已取消点赞' : '点赞成功')
  } catch (error) {
    // 回滚
    post.value.isLiked = wasLiked
    post.value.likeCount = oldCount
    console.error('点赞失败:', error)
    ElMessage.error('操作失败，请重试')
  }
}

const handleCollect = async () => {
  const wasCollected = post.value.isCollected
  // 乐观更新
  post.value.isCollected = !wasCollected
  try {
    // 后端是toggle模式，统一调用collectPost
    await communityApi.collectPost(route.params.id)
    ElMessage.success(wasCollected ? '已取消收藏' : '收藏成功')
  } catch (error) {
    // 回滚
    post.value.isCollected = wasCollected
    console.error('收藏操作失败:', error)
    ElMessage.error('操作失败，请重试')
  }
}

const submitReply = async () => {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }

  submitting.value = true
  try {
    await communityApi.createReply({
      postId: route.params.id,
      content: replyContent.value
    })
    ElMessage.success('评论发表成功')
    replyContent.value = ''
    loadReplies()
    post.value.replyCount = (post.value.replyCount || 0) + 1
  } catch (error) {
    console.error('发表评论失败:', error)
    ElMessage.error('发表评论失败')
  } finally {
    submitting.value = false
  }
}

const likeReply = async (reply) => {
  const wasLiked = reply.isLiked
  const oldCount = reply.likeCount || 0
  // 乐观更新
  reply.isLiked = !wasLiked
  reply.likeCount = wasLiked ? oldCount - 1 : oldCount + 1
  try {
    await communityApi.likeReply(reply.id)
  } catch (error) {
    // 回滚
    reply.isLiked = wasLiked
    reply.likeCount = oldCount
    console.error('点赞失败:', error)
    ElMessage.error('操作失败，请重试')
  }
}

const replyTo = (reply) => {
  replyContent.value = `@${reply.username} `
}

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

onMounted(() => {
  loadPost()
  loadReplies()
})
</script>

<style lang="scss" scoped>
.post-detail {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;

  .page-header {
    margin-bottom: 20px;
  }

  .post-card {
    margin-bottom: 20px;

    .post-header {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 20px;

      .author-info {
        flex: 1;

        .author-name {
          font-weight: 600;
          color: var(--text-primary);
        }

        .post-time {
          font-size: 12px;
          color: var(--text-placeholder);
        }
      }

      .post-tags {
        display: flex;
        gap: 8px;
      }
    }

    .post-title {
      font-size: 24px;
      font-weight: 600;
      color: var(--text-primary);
      margin-bottom: 20px;
    }

    .post-content {
      font-size: 15px;
      line-height: 1.8;
      color: var(--text-regular);
      margin-bottom: 24px;
    }

    .post-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding-top: 16px;
      border-top: 1px solid var(--border-light);

      .post-stats {
        display: flex;
        gap: 24px;

        .stat-item {
          display: flex;
          align-items: center;
          gap: 6px;
          color: var(--text-secondary);
          cursor: pointer;

          &:hover {
            color: var(--primary-color);
          }
        }
      }
    }
  }

  .reply-card {
    .reply-header {
      font-weight: 600;
    }

    .reply-input {
      margin-bottom: 24px;

      .reply-actions {
        display: flex;
        justify-content: flex-end;
        margin-top: 12px;
      }
    }

    .reply-list {
      .reply-item {
        display: flex;
        gap: 12px;
        padding: 16px 0;
        border-bottom: 1px solid var(--border-light);

        &:last-child {
          border-bottom: none;
        }

        .reply-content {
          flex: 1;

          .reply-author {
            display: flex;
            align-items: center;
            gap: 12px;
            margin-bottom: 8px;

            .author-name {
              font-weight: 500;
              color: var(--text-primary);
            }

            .reply-time {
              font-size: 12px;
              color: var(--text-placeholder);
            }
          }

          .reply-text {
            font-size: 14px;
            line-height: 1.6;
            color: var(--text-regular);
            margin-bottom: 8px;
          }

          .reply-footer {
            display: flex;
            gap: 16px;

            .action-item {
              display: flex;
              align-items: center;
              gap: 4px;
              font-size: 12px;
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

    .el-pagination {
      justify-content: center;
      margin-top: 20px;
    }
  }
}
</style>
