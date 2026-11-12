<template>
  <div class="student-detail">
    <div class="page-header">
      <el-button @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>
      <h2>学生详情</h2>
    </div>

    <div v-loading="loading" class="detail-content">
      <div class="info-section">
        <div class="student-profile">
          <el-avatar :size="64" :src="student.avatar">
            {{ (student.realName || student.username || '').charAt(0) }}
          </el-avatar>
          <div class="profile-info">
            <h3>{{ student.realName || student.username }}</h3>
            <p>学号：{{ student.studentNo || '-' }}</p>
            <p>邮箱：{{ student.email || '-' }}</p>
            <p>年级：{{ student.grade || '-' }}</p>
            <p>等级：{{ student.currentLevel || '-' }} → {{ student.targetLevel || '-' }}</p>
          </div>
        </div>
      </div>

      <div class="stats-section">
        <h3>学习统计</h3>
        <div class="stats-grid">
          <div class="stat-card">
            <div class="stat-value">{{ student.stats?.totalExams || 0 }}</div>
            <div class="stat-label">考试次数</div>
          </div>
          <div class="stat-card">
            <div class="stat-value" :class="getScoreClass(student.stats?.avgScore)">
              {{ student.stats?.avgScore || 0 }}
            </div>
            <div class="stat-label">平均分</div>
          </div>
          <div class="stat-card">
            <div class="stat-value">{{ formatStudyTime(student.stats?.totalStudyTime) }}</div>
            <div class="stat-label">总学习时长</div>
          </div>
          <div class="stat-card">
            <div class="stat-value">{{ student.stats?.wrongQuestionCount || 0 }}</div>
            <div class="stat-label">错题数</div>
          </div>
          <div class="stat-card">
            <div class="stat-value">{{ student.stats?.noteCount || 0 }}</div>
            <div class="stat-label">笔记数</div>
          </div>
          <div class="stat-card">
            <div class="stat-value">{{ student.studyDays || 0 }}</div>
            <div class="stat-label">学习天数</div>
          </div>
        </div>
      </div>

      <div class="exams-section">
        <h3>最近考试记录</h3>
        <el-table :data="student.recentExams || []" style="width: 100%">
          <el-table-column prop="submitTime" label="提交时间" width="180" />
          <el-table-column prop="score" label="得分" width="100">
            <template #default="{ row }">
              <span :class="getScoreClass(row.score)">{{ row.score }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="totalScore" label="总分" width="100" />
          <el-table-column prop="duration" label="用时(分钟)" width="120" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'COMPLETED' ? 'success' : 'info'" size="small">
                {{ row.status === 'COMPLETED' ? '已完成' : row.status }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { teacherApi } from '@/api/teacher'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const student = ref({})

const goBack = () => {
  router.push('/teacher/student')
}

const loadStudentDetail = async () => {
  const studentId = route.params.id
  if (!studentId) return

  loading.value = true
  try {
    const res = await teacherApi.getStudentDetail(studentId)
    if (res.data) {
      student.value = res.data
    }
  } catch (error) {
    console.error('加载学生详情失败:', error)
    ElMessage.error('加载学生详情失败')
  } finally {
    loading.value = false
  }
}

const getScoreClass = (score) => {
  if (score >= 85) return 'score-excellent'
  if (score >= 60) return 'score-pass'
  return 'score-fail'
}

const formatStudyTime = (minutes) => {
  if (!minutes) return '0h'
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  return hours > 0 ? `${hours}h${mins > 0 ? mins + 'm' : ''}` : `${mins}m`
}

onMounted(() => {
  loadStudentDetail()
})
</script>

<style lang="scss" scoped>
.student-detail {
  .page-header {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 24px;

    h2 {
      font-size: 24px;
      font-weight: 600;
      color: var(--text-primary);
      margin: 0;
    }
  }

  .detail-content {
    .info-section {
      background: var(--bg-primary);
      border-radius: var(--radius-lg);
      padding: 24px;
      margin-bottom: 20px;

      .student-profile {
        display: flex;
        align-items: center;
        gap: 20px;

        .profile-info {
          h3 {
            font-size: 20px;
            margin: 0 0 8px;
          }
          p {
            margin: 4px 0;
            color: var(--text-secondary);
          }
        }
      }
    }

    .stats-section, .exams-section {
      background: var(--bg-primary);
      border-radius: var(--radius-lg);
      padding: 24px;
      margin-bottom: 20px;

      h3 {
        font-size: 18px;
        margin: 0 0 16px;
      }
    }

    .stats-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
      gap: 16px;

      .stat-card {
        background: var(--bg-secondary, #f5f7fa);
        border-radius: var(--radius-md, 8px);
        padding: 16px;
        text-align: center;

        .stat-value {
          font-size: 24px;
          font-weight: 700;
          margin-bottom: 4px;
        }
        .stat-label {
          font-size: 13px;
          color: var(--text-secondary);
        }
      }
    }

    .score-excellent { color: var(--success-color, #67c23a); font-weight: 600; }
    .score-pass { color: var(--warning-color, #e6a23c); font-weight: 600; }
    .score-fail { color: var(--danger-color, #f56c6c); font-weight: 600; }
  }
}
</style>
