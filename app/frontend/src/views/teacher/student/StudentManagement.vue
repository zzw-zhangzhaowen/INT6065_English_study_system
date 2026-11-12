<template>
  <div class="student-management">
    <div class="page-header">
      <h2>学生管理</h2>
      <el-button type="primary" @click="exportStudents">
        <el-icon><Download /></el-icon>
        导出数据
      </el-button>
    </div>

    <div class="management-content">
      <div class="filter-bar">
        <el-select v-model="filterClass" placeholder="选择班级" clearable style="width: 150px">
          <el-option label="全部班级" value="" />
          <el-option
            v-for="cls in classList"
            :key="cls.id"
            :label="cls.name"
            :value="cls.id"
          />
        </el-select>
        <el-input v-model="searchKeyword" placeholder="搜索学生姓名" :prefix-icon="Search" clearable style="width: 200px" />
      </div>

      <el-table v-loading="loading" :data="students" style="width: 100%">
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="studentId" label="学号" width="120" />
        <el-table-column prop="class" label="班级" width="120" />
        <el-table-column prop="examCount" label="考试次数" width="100" />
        <el-table-column prop="avgScore" label="平均分" width="100">
          <template #default="{ row }">
            <span :class="getScoreClass(row.avgScore)">{{ row.avgScore }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="studyHours" label="学习时长" width="100" />
        <el-table-column prop="lastLogin" label="最近登录" width="150" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="viewDetail(row)">详情</el-button>
            <el-button type="primary" link size="small" @click="sendMessage(row)">消息</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          background
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Download, Search } from '@element-plus/icons-vue'
import { teacherApi } from '@/api/teacher'

const router = useRouter()
const filterClass = ref('')
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const loading = ref(false)

const students = ref([])
const classList = ref([])

const loadClassList = async () => {
  try {
    const res = await teacherApi.getClassList()
    if (res.data) {
      classList.value = res.data
    }
  } catch (error) {
    console.error('加载班级列表失败:', error)
  }
}

const loadStudents = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value
    }
    if (filterClass.value) params.classId = filterClass.value
    if (searchKeyword.value) params.keyword = searchKeyword.value

    const res = await teacherApi.getStudentList(params)
    if (res.data) {
      students.value = (res.data.records || []).map(item => ({
        id: item.id,
        name: item.realName || item.username,
        studentId: item.studentNo || item.id,
        class: item.className || '-',
        examCount: item.examCount || 0,
        avgScore: item.avgScore || 0,
        studyHours: (item.studyMinutes ? Math.round(item.studyMinutes / 60) + 'h' : '0h'),
        lastLogin: item.lastLoginTime || '-'
      }))
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('加载学生列表失败:', error)
    ElMessage.error('加载学生列表失败')
  } finally {
    loading.value = false
  }
}

const getScoreClass = (score) => {
  if (score >= 85) return 'score-excellent'
  if (score >= 60) return 'score-pass'
  return 'score-fail'
}

const escapeCsvField = (value) => {
  const str = String(value ?? '')
  if (str.includes(',') || str.includes('"') || str.includes('\n')) {
    return '"' + str.replace(/"/g, '""') + '"'
  }
  return str
}

const exportStudents = () => {
  if (students.value.length === 0) {
    ElMessage.warning('暂无数据可导出')
    return
  }
  const headers = ['姓名', '学号', '班级', '考试次数', '平均分', '学习时长', '最近登录']
  let csvContent = headers.join(',') + '\n'
  students.value.forEach(s => {
    const row = [s.name, s.studentId, s.class, s.examCount, s.avgScore, s.studyHours, s.lastLogin]
    csvContent += row.map(escapeCsvField).join(',') + '\n'
  })
  const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8' })
  const url = window.URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = '学生数据.csv'
  a.click()
  window.URL.revokeObjectURL(url)
  ElMessage.success('导出成功')
}

const viewDetail = (row) => {
  router.push(`/teacher/student/detail/${row.id}`)
}

const sendMessage = async (row) => {
  ElMessageBox.prompt('请输入消息内容', `向 ${row.name} 发送消息`, {
    confirmButtonText: '发送',
    cancelButtonText: '取消',
    inputType: 'textarea'
  }).then(async ({ value }) => {
    if (!value?.trim()) {
      ElMessage.warning('请输入消息内容')
      return
    }
    try {
      await teacherApi.sendMessageToStudent(row.id, {
        title: `来自教师的消息`,
        content: value
      })
      ElMessage.success('消息发送成功')
    } catch (error) {
      console.error('发送消息失败:', error)
      ElMessage.error('发送消息失败')
    }
  }).catch(() => {})
}

watch(filterClass, () => {
  currentPage.value = 1
  loadStudents()
})

watch([currentPage, pageSize], () => {
  loadStudents()
})

let searchTimer = null
watch(searchKeyword, () => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => {
    currentPage.value = 1
    loadStudents()
  }, 500)
})

onMounted(() => {
  loadClassList()
  loadStudents()
})

onUnmounted(() => {
  clearTimeout(searchTimer)
})
</script>

<style lang="scss" scoped>
.student-management {
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
  }

  .management-content {
    background: var(--bg-primary);
    border-radius: var(--radius-lg);
    padding: 24px;

    .filter-bar {
      display: flex;
      gap: 12px;
      margin-bottom: 20px;
    }

    .pagination-wrapper {
      display: flex;
      justify-content: center;
      padding-top: 24px;
    }

    .score-excellent { color: var(--success-color); font-weight: 600; }
    .score-pass { color: var(--warning-color); font-weight: 600; }
    .score-fail { color: var(--danger-color); font-weight: 600; }
  }
}
</style>
