<template>
  <div class="resource-detail">
    <div class="page-header">
      <el-button @click="goBack" :icon="ArrowLeft">返回</el-button>
      <h2>{{ resource.title }}</h2>
    </div>

    <div class="resource-content" v-loading="loading">
      <template v-if="loadError">
        <el-alert :title="loadError" type="error" show-icon :closable="false">
          <el-button type="primary" link @click="loadResource">重新加载</el-button>
        </el-alert>
      </template>
      <template v-else-if="!loading">
      <!-- 资源播放/预览区域 -->
      <div class="resource-player">
        <template v-if="resource.type === 'video'">
          <video
            v-if="resource.fileUrl"
            :src="getFileUrl(resource.fileUrl)"
            controls
            class="video-player"
          ></video>
          <div v-else class="no-resource">
            <el-icon :size="64"><VideoPlay /></el-icon>
            <p>暂无视频资源</p>
          </div>
        </template>

        <template v-else-if="resource.type === 'audio'">
          <div class="audio-container">
            <el-image
              v-if="resource.coverUrl"
              :src="getFileUrl(resource.coverUrl)"
              fit="cover"
              class="audio-cover"
            >
              <template #error>
                <el-image :src="DEFAULT_COVER" fit="cover" class="audio-cover" />
              </template>
            </el-image>
            <el-image v-else :src="DEFAULT_COVER" fit="cover" class="audio-cover" />
            <audio
              v-if="resource.fileUrl"
              :src="getFileUrl(resource.fileUrl)"
              controls
              class="audio-player"
            ></audio>
          </div>
        </template>

        <template v-else>
          <div class="document-preview">
            <el-icon :size="64"><Document /></el-icon>
            <p>{{ resource.title }}</p>
            <el-button type="primary" @click="downloadResource" v-if="resource.fileUrl">
              <el-icon><Download /></el-icon>
              下载文档
            </el-button>
          </div>
        </template>
      </div>

      <!-- 资源信息 -->
      <div class="resource-info-panel">
        <h3>资源信息</h3>
        <div class="info-item">
          <span class="label">资源类型：</span>
          <el-tag>{{ getTypeName(resource.type) }}</el-tag>
        </div>
        <div class="info-item">
          <span class="label">考试等级：</span>
          <el-tag type="success">{{ resource.level }}</el-tag>
        </div>
        <div class="info-item">
          <span class="label">技能类型：</span>
          <span>{{ resource.skillType || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="label">浏览次数：</span>
          <span>{{ resource.viewCount || 0 }}</span>
        </div>
        <div class="info-item">
          <span class="label">点赞数：</span>
          <span>{{ resource.likeCount || 0 }}</span>
        </div>
        <div class="info-item">
          <span class="label">上传时间：</span>
          <span>{{ resource.createTime || '-' }}</span>
        </div>

        <div class="info-description">
          <span class="label">资源描述：</span>
          <p>{{ resource.description || '暂无描述' }}</p>
        </div>

        <div class="action-buttons">
          <el-button type="primary" @click="toggleLike" :icon="liked ? StarFilled : Star">
            {{ liked ? '已收藏' : '收藏' }}
          </el-button>
          <el-button @click="shareResource" :icon="Share">分享</el-button>
        </div>
      </div>
      </template>
    </div>

    <!-- 相关资源推荐 -->
    <div class="related-resources" v-if="relatedResources.length > 0">
      <h3>相关推荐</h3>
      <div class="related-list">
        <div
          v-for="item in relatedResources"
          :key="item.id"
          class="related-item"
          @click="viewResource(item)"
        >
          <el-image
            v-if="item.coverUrl"
            :src="getFileUrl(item.coverUrl)"
            fit="cover"
            class="related-cover"
          >
            <template #error>
              <el-image :src="DEFAULT_COVER" fit="cover" class="related-cover" />
            </template>
          </el-image>
          <el-image v-else :src="DEFAULT_COVER" fit="cover" class="related-cover" />
          <div class="related-info">
            <h4>{{ item.resourceName }}</h4>
            <span>{{ item.level }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowLeft, VideoPlay, Headset, Document, Download,
  Star, StarFilled, Share, Picture
} from '@element-plus/icons-vue'
import { learningApi } from '@/api/learning'
import { getFileUrl, DEFAULT_COVER } from '@/utils/file'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const resource = ref({})
const liked = ref(false)
const relatedResources = ref([])
const loadError = ref('')

const goBack = () => {
  router.back()
}

const getTypeName = (type) => {
  const names = { video: '视频', audio: '音频', document: '文档' }
  return names[type] || type || '文档'
}

const loadResource = async (id = route.params.id) => {
  loading.value = true
  loadError.value = ''
  try {
    if (!id) {
      loadError.value = '资源ID无效'
      ElMessage.error('资源ID无效')
      return
    }
    const res = await learningApi.getResourceDetail(id)
    if (res.data) {
      resource.value = {
        id: res.data.id,
        title: res.data.resourceName,
        type: (res.data.resourceType || 'document').toLowerCase(),
        level: res.data.level || 'CET4',
        skillType: res.data.skillType || '',
        description: res.data.description || '',
        fileUrl: res.data.fileUrl || '',
        coverUrl: res.data.coverUrl || '',
        viewCount: res.data.viewCount || 0,
        likeCount: res.data.likeCount || 0,
        createTime: res.data.createTime?.substring(0, 10) || ''
      }
    }

    // 检查是否已收藏
    try {
      const collectRes = await learningApi.isCollected(id)
      liked.value = collectRes.data === true
    } catch (e) {
      // 忽略
    }

    // 加载相关资源
    loadRelatedResources()
  } catch (error) {
    console.error('加载资源详情失败:', error)
    loadError.value = '加载资源详情失败，请重试'
    ElMessage.error('加载资源详情失败')
  } finally {
    loading.value = false
  }
}

const loadRelatedResources = async () => {
  try {
    const res = await learningApi.getResourceList({
      pageNum: 1,
      pageSize: 4,
      level: resource.value.level
    })
    if (res.data?.records) {
      relatedResources.value = res.data.records
        .filter(item => item.id !== resource.value.id)
        .slice(0, 3)
    }
  } catch (error) {
    console.error('加载相关资源失败:', error)
  }
}

const toggleLike = async () => {
  const wasLiked = liked.value
  // 立即更新UI
  liked.value = !wasLiked
  try {
    if (wasLiked) {
      await learningApi.uncollectResource(resource.value.id)
      ElMessage.success('已取消收藏')
    } else {
      await learningApi.collectResource(resource.value.id)
      ElMessage.success('收藏成功')
    }
  } catch (error) {
    // 回滚UI
    liked.value = wasLiked
    ElMessage.error('操作失败，请重试')
  }
}

const shareResource = () => {
  const url = window.location.href
  navigator.clipboard.writeText(url).then(() => {
    ElMessage.success('链接已复制到剪贴板')
  }).catch(() => {
    ElMessage.info('请手动复制链接分享')
  })
}

const downloadResource = () => {
  if (resource.value.fileUrl) {
    window.open(getFileUrl(resource.value.fileUrl), '_blank')
  }
}


const viewResource = (item) => {
  const targetId = item?.id ?? item?.resourceId
  if (!targetId) {
    ElMessage.warning('该资源暂不可访问')
    return
  }
  if (String(targetId) === String(route.params.id)) {
    return
  }
  router.push(`/student/learning/resource/${targetId}`)
}

watch(
  () => route.params.id,
  (newId, oldId) => {
    if (newId && newId !== oldId) {
      loadResource(newId)
    }
  }
)

onMounted(() => {
  loadResource()
})
</script>

<style lang="scss" scoped>
.resource-detail {
  .page-header {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 24px;

    h2 {
      font-size: 20px;
      font-weight: 600;
      color: var(--text-primary);
      margin: 0;
    }
  }

  .resource-content {
    display: grid;
    grid-template-columns: 1fr 320px;
    gap: 24px;
    margin-bottom: 32px;
  }

  .resource-player {
    background: var(--bg-primary);
    border-radius: var(--radius-lg);
    overflow: hidden;
    min-height: 400px;
    display: flex;
    align-items: center;
    justify-content: center;

    .video-player {
      width: 100%;
      max-height: 500px;
      background: #000;
    }

    .audio-container {
      width: 100%;
      padding: 40px;
      text-align: center;

      .audio-cover {
        width: 200px;
        height: 200px;
        border-radius: 50%;
        margin-bottom: 24px;
      }

      .audio-placeholder {
        width: 200px;
        height: 200px;
        border-radius: 50%;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        display: flex;
        align-items: center;
        justify-content: center;
        margin: 0 auto 24px;
        color: #fff;
      }

      .audio-player {
        width: 100%;
        max-width: 400px;
      }
    }

    .document-preview, .no-resource {
      text-align: center;
      color: var(--text-secondary);

      p {
        margin: 16px 0;
        font-size: 16px;
      }
    }
  }

  .resource-info-panel {
    background: var(--bg-primary);
    border-radius: var(--radius-lg);
    padding: 24px;

    h3 {
      font-size: 16px;
      font-weight: 600;
      margin-bottom: 20px;
      color: var(--text-primary);
    }

    .info-item {
      display: flex;
      align-items: center;
      margin-bottom: 12px;

      .label {
        color: var(--text-secondary);
        width: 80px;
        flex-shrink: 0;
      }
    }

    .info-description {
      margin-top: 20px;
      padding-top: 20px;
      border-top: 1px solid var(--border-color);

      .label {
        color: var(--text-secondary);
        display: block;
        margin-bottom: 8px;
      }

      p {
        color: var(--text-primary);
        line-height: 1.6;
      }
    }

    .action-buttons {
      margin-top: 24px;
      display: flex;
      gap: 12px;
    }
  }

  .related-resources {
    background: var(--bg-primary);
    border-radius: var(--radius-lg);
    padding: 24px;

    h3 {
      font-size: 16px;
      font-weight: 600;
      margin-bottom: 20px;
      color: var(--text-primary);
    }

    .related-list {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 16px;
    }

    .related-item {
      cursor: pointer;
      border-radius: var(--radius-md);
      overflow: hidden;
      background: var(--bg-secondary);
      transition: transform 0.2s;

      &:hover {
        transform: translateY(-4px);
      }

      .related-cover {
        width: 100%;
        height: 120px;
      }

      .related-placeholder {
        width: 100%;
        height: 120px;
        background: #f5f7fa;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #909399;
      }

      .related-info {
        padding: 12px;

        h4 {
          font-size: 14px;
          font-weight: 500;
          margin-bottom: 4px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        span {
          font-size: 12px;
          color: var(--text-secondary);
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .resource-detail {
    .resource-content {
      grid-template-columns: 1fr;
    }

    .related-resources .related-list {
      grid-template-columns: 1fr;
    }
  }
}
</style>
