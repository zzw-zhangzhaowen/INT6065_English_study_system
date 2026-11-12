<template>
  <div class="classroom-home">
    <div class="page-header">
      <h2>在线课堂</h2>
    </div>

    <!-- 正在直播 -->
    <div class="section" v-if="liveClassrooms.length > 0">
      <h3 class="section-title">
        <span class="live-dot"></span> 正在直播
      </h3>
      <div class="classroom-grid">
        <div v-for="item in liveClassrooms" :key="item.id" class="classroom-card live">
          <div class="card-cover">
            <img v-if="item.coverImage" :src="getFileUrl(item.coverImage)" alt="封面" />
            <div v-else class="cover-placeholder">
              <el-icon :size="40"><VideoCamera /></el-icon>
            </div>
            <el-tag type="danger" class="live-tag">直播中</el-tag>
          </div>
          <div class="card-body">
            <h4 class="card-title">{{ item.classroomName }}</h4>
            <p class="card-desc">{{ item.description || '暂无描述' }}</p>
            <div class="card-meta">
              <span class="teacher-name">
                <el-icon><User /></el-icon> {{ item.teacherName || '教师' }}
              </span>
              <span class="student-count">{{ item.currentStudents || 0 }}人在线</span>
            </div>
            <el-button type="danger" class="join-btn" @click="joinLive(item)">
              进入课堂
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 所有课堂 -->
    <div class="section">
      <h3 class="section-title">全部课堂</h3>
      <div v-if="allLoading" class="loading-area">
        <el-icon class="is-loading" :size="24"><Loading /></el-icon>
        <span>加载中...</span>
      </div>
      <div v-else-if="allClassrooms.length === 0" class="empty-area">
        <el-empty description="暂无课堂" :image-size="80" />
      </div>
      <div v-else class="classroom-grid">
        <div v-for="item in allClassrooms" :key="item.id" class="classroom-card"
             :class="{ ended: item.status === 'ENDED' }">
          <div class="card-cover">
            <img v-if="item.coverImage" :src="getFileUrl(item.coverImage)" alt="封面" />
            <div v-else class="cover-placeholder">
              <el-icon :size="40"><VideoCamera /></el-icon>
            </div>
            <el-tag :type="getStatusType(item.status)" class="status-tag">
              {{ getStatusText(item.status) }}
            </el-tag>
          </div>
          <div class="card-body">
            <h4 class="card-title">{{ item.classroomName }}</h4>
            <p class="card-desc">{{ item.description || '暂无描述' }}</p>
            <div class="card-meta">
              <span class="teacher-name">
                <el-icon><User /></el-icon> {{ item.teacherName || '教师' }}
              </span>
              <span v-if="item.startTime" class="time">{{ formatTime(item.startTime) }}</span>
            </div>
            <el-button v-if="item.status === 'LIVE'" type="danger" class="join-btn" @click="joinLive(item)">
              进入课堂
            </el-button>
            <el-button v-else-if="item.status === 'ENDED' && item.recordUrl" type="primary" plain class="join-btn"
                       @click="$router.push(`/student/classroom/playback/${item.id}`)">
              查看回放
            </el-button>
            <el-button v-else disabled class="join-btn">
              {{ item.status === 'PENDING' ? '等待开始' : '已结束' }}
            </el-button>
          </div>
        </div>
      </div>

      <div v-if="allTotal > allPageSize" class="pagination-wrapper">
        <el-pagination
          v-model:current-page="allPageNum"
          :page-size="allPageSize"
          :total="allTotal"
          layout="prev, pager, next"
          background
          @current-change="loadAllClassrooms"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { VideoCamera, User, Loading } from '@element-plus/icons-vue'
import { classroomApi } from '@/api/classroom'
import { getFileUrl } from '@/utils/file'

const router = useRouter()

const liveClassrooms = ref([])
const allClassrooms = ref([])
const allLoading = ref(false)
const allPageNum = ref(1)
const allPageSize = ref(12)
const allTotal = ref(0)


const getStatusType = (status) => {
  const map = { LIVE: 'danger', PENDING: 'warning', ENDED: 'info' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { LIVE: '直播中', PENDING: '待开始', ENDED: '已结束' }
  return map[status] || status
}

const formatTime = (time) => {
  if (!time) return ''
  const str = typeof time === 'string' ? time : time.toString()
  return str.substring(0, 16).replace('T', ' ')
}

const loadLiveClassrooms = async () => {
  try {
    const res = await classroomApi.getLiveClassrooms()
    if (res.data) {
      liveClassrooms.value = Array.isArray(res.data) ? res.data : []
    }
  } catch (e) {
    console.error('加载直播课堂失败:', e)
  }
}

const loadAllClassrooms = async () => {
  allLoading.value = true
  try {
    const res = await classroomApi.getClassroomList({
      pageNum: allPageNum.value,
      pageSize: allPageSize.value,
      filterByClass: true
    })
    if (res.data?.records) {
      allClassrooms.value = res.data.records
      allTotal.value = res.data.total || 0
    } else if (Array.isArray(res.data)) {
      allClassrooms.value = res.data
      allTotal.value = res.data.length
    }
  } catch (e) {
    console.error('加载课堂列表失败:', e)
  } finally {
    allLoading.value = false
  }
}

const joinLive = (item) => {
  router.push(`/student/classroom/live/${item.id}`)
}

onMounted(() => {
  loadLiveClassrooms()
  loadAllClassrooms()
})
</script>

<style lang="scss" scoped>
.classroom-home {
  .page-header {
    margin-bottom: 24px;

    h2 {
      font-size: 24px;
      font-weight: 600;
      color: var(--text-primary);
    }
  }

  .section {
    margin-bottom: 32px;
  }

  .section-title {
    font-size: 16px;
    font-weight: 600;
    color: var(--text-primary);
    margin-bottom: 16px;
    display: flex;
    align-items: center;
    gap: 8px;

    .live-dot {
      width: 8px;
      height: 8px;
      border-radius: 50%;
      background: #ff4d4f;
      animation: pulse 1.5s infinite;
    }
  }

  @keyframes pulse {
    0%, 100% { opacity: 1; }
    50% { opacity: 0.4; }
  }

  .classroom-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 20px;
  }

  .classroom-card {
    background: var(--bg-primary);
    border-radius: var(--radius-lg, 12px);
    overflow: hidden;
    transition: transform 0.2s, box-shadow 0.2s;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
    }

    &.live {
      border: 2px solid #ff4d4f;
    }

    &.ended {
      opacity: 0.75;
    }

    .card-cover {
      position: relative;
      aspect-ratio: 16 / 9;
      background: #f0f0f0;
      overflow: hidden;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .cover-placeholder {
        width: 100%;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: rgba(255, 255, 255, 0.6);
      }

      .live-tag, .status-tag {
        position: absolute;
        top: 8px;
        right: 8px;
      }
    }

    .card-body {
      padding: 16px;

      .card-title {
        font-size: 15px;
        font-weight: 600;
        color: var(--text-primary);
        margin-bottom: 6px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .card-desc {
        font-size: 13px;
        color: var(--text-secondary);
        margin-bottom: 12px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .card-meta {
        display: flex;
        justify-content: space-between;
        align-items: center;
        font-size: 12px;
        color: var(--text-secondary);
        margin-bottom: 12px;

        .teacher-name {
          display: flex;
          align-items: center;
          gap: 4px;
        }
      }

      .join-btn {
        width: 100%;
      }
    }
  }

  .loading-area {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    padding: 60px 0;
    color: var(--text-secondary);
  }

  .empty-area {
    padding: 40px 0;
  }

  .pagination-wrapper {
    display: flex;
    justify-content: center;
    padding-top: 24px;
  }
}
</style>
