<template>
  <div class="teacher-profile" v-loading="loading">
    <div class="profile-header card">
      <div class="avatar-wrap">
        <el-avatar :size="88" :src="avatarUrl">{{ (profile.nickname || profile.username || '教').charAt(0) }}</el-avatar>
        <el-button type="primary" link size="small" @click="changeAvatar">更换头像</el-button>
      </div>
      <div class="base-info">
        <h2>{{ profile.nickname || profile.realName || profile.username || '教师' }}</h2>
        <div class="meta-row">
          <span>用户名：{{ profile.username || '-' }}</span>
          <span>角色：教师</span>
        </div>
      </div>
      <div class="action-wrap">
        <el-button type="primary" @click="openEdit">编辑资料</el-button>
      </div>
    </div>

    <div class="overview-grid">
      <div class="stat-card">
        <div class="label">学生总数</div>
        <div class="value">{{ stats.totalStudents }}</div>
      </div>
      <div class="stat-card">
        <div class="label">考试总数</div>
        <div class="value">{{ stats.totalExams }}</div>
      </div>
      <div class="stat-card">
        <div class="label">题库试题</div>
        <div class="value">{{ stats.totalQuestions }}</div>
      </div>
      <div class="stat-card">
        <div class="label">平均通过率</div>
        <div class="value">{{ stats.avgPassRate }}%</div>
      </div>
    </div>

    <div class="content-grid">
      <div class="card">
        <div class="card-title">基本信息</div>
        <div class="info-item"><span class="k">真实姓名</span><span class="v">{{ profile.realName || '未填写' }}</span></div>
        <div class="info-item"><span class="k">邮箱</span><span class="v">{{ profile.email || '未填写' }}</span></div>
        <div class="info-item"><span class="k">手机号</span><span class="v">{{ profile.phone || '未填写' }}</span></div>
        <div class="info-item"><span class="k">学校/机构</span><span class="v">{{ profile.school || '未填写' }}</span></div>
      </div>

      <div class="card">
        <div class="card-title">账户安全</div>
        <div class="security-row">
          <div>
            <div class="row-title">登录密码</div>
            <div class="row-desc">建议定期修改密码以提升账号安全性</div>
          </div>
          <el-button @click="changePassword">修改密码</el-button>
        </div>
      </div>
    </div>

    <el-dialog v-model="editVisible" title="编辑资料" width="520px">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="真实姓名">
          <el-input v-model="editForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="editForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="editForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="学校/机构">
          <el-input v-model="editForm.school" placeholder="请输入学校/机构" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="saveProfile">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { userApi } from '@/api/user'
import { teacherApi } from '@/api/teacher'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const loading = ref(false)
const editVisible = ref(false)

const profile = ref({
  username: '',
  nickname: '',
  realName: '',
  email: '',
  phone: '',
  avatar: '',
  school: ''
})

const stats = ref({
  totalStudents: 0,
  totalExams: 0,
  totalQuestions: 0,
  avgPassRate: 0
})

const editForm = reactive({
  realName: '',
  email: '',
  phone: '',
  school: ''
})

const avatarUrl = computed(() => {
  const url = profile.value.avatar || ''
  if (!url) return ''
  const withTs = url.includes('?') ? `${url}&t=${Date.now()}` : `${url}?t=${Date.now()}`
  if (withTs.startsWith('http')) return withTs
  const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
  return `${baseUrl}${withTs}`
})

const loadProfile = async () => {
  const res = await userApi.getProfile()
  const data = res.data?.user || res.data || {}
  profile.value = {
    username: data.username || '',
    nickname: data.realName || data.username || '',
    realName: data.realName || '',
    email: data.email || '',
    phone: data.phone || '',
    avatar: data.avatar || '',
    school: data.school || ''
  }
}

const loadStats = async () => {
  try {
    const res = await teacherApi.getDashboardStats()
    const data = res.data || {}
    stats.value = {
      totalStudents: data.totalStudents || 0,
      totalExams: data.totalExams || 0,
      totalQuestions: data.totalQuestions || 0,
      avgPassRate: data.avgPassRate || 0
    }
  } catch (error) {
    stats.value = { totalStudents: 0, totalExams: 0, totalQuestions: 0, avgPassRate: 0 }
  }
}

const openEdit = () => {
  editForm.realName = profile.value.realName || ''
  editForm.email = profile.value.email || ''
  editForm.phone = profile.value.phone || ''
  editForm.school = profile.value.school || ''
  editVisible.value = true
}

const saveProfile = async () => {
  try {
    await userApi.updateProfile({
      realName: editForm.realName,
      email: editForm.email,
      phone: editForm.phone,
      school: editForm.school
    })
    profile.value.realName = editForm.realName
    profile.value.nickname = editForm.realName || profile.value.username
    profile.value.email = editForm.email
    profile.value.phone = editForm.phone
    profile.value.school = editForm.school
    userStore.updateUserInfo({
      realName: editForm.realName,
      nickname: editForm.realName,
      email: editForm.email,
      phone: editForm.phone
    })
    editVisible.value = false
    ElMessage.success('资料更新成功')
  } catch (error) {
    ElMessage.error(error.message || '资料更新失败')
  }
}

const changeAvatar = () => {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = 'image/*'
  input.onchange = async (e) => {
    const file = e.target.files?.[0]
    if (!file) return
    try {
      const uploadRes = await userApi.uploadAvatar(file)
      const newAvatar = uploadRes.data?.url
      if (!newAvatar) return
      await userApi.updateProfile({ avatar: newAvatar })
      profile.value.avatar = newAvatar
      const avatarWithTs = newAvatar.includes('?') ? `${newAvatar}&t=${Date.now()}` : `${newAvatar}?t=${Date.now()}`
      userStore.updateUserInfo({ avatar: avatarWithTs })
      ElMessage.success('头像更新成功')
    } catch (error) {
      ElMessage.error(error.message || '头像上传失败')
    }
  }
  input.click()
}

const changePassword = () => {
  ElMessageBox.prompt('请输入新密码', '修改密码', {
    confirmButtonText: '下一步',
    cancelButtonText: '取消',
    inputType: 'password',
    inputValidator: val => (val && val.length >= 6 ? true : '密码长度不能少于6位')
  }).then(async ({ value: newPassword }) => {
    ElMessageBox.prompt('请输入原密码', '验证身份', {
      confirmButtonText: '确认修改',
      cancelButtonText: '取消',
      inputType: 'password'
    }).then(async ({ value: oldPassword }) => {
      try {
        await userApi.changePassword({ oldPassword, newPassword })
        ElMessage.success('密码修改成功')
      } catch (error) {
        ElMessage.error(error.message || '密码修改失败')
      }
    }).catch(() => {})
  }).catch(() => {})
}

onMounted(async () => {
  loading.value = true
  try {
    await Promise.all([loadProfile(), loadStats()])
  } catch (error) {
    ElMessage.error('个人中心加载失败')
  } finally {
    loading.value = false
  }
})
</script>

<style lang="scss" scoped>
.teacher-profile {
  .card {
    background: var(--bg-primary);
    border-radius: var(--radius-lg);
    box-shadow: var(--shadow-sm);
    padding: 24px;
    margin-bottom: 20px;
  }

  .profile-header {
    display: flex;
    align-items: center;
    gap: 20px;

    .avatar-wrap {
      text-align: center;
    }

    .base-info {
      flex: 1;

      h2 {
        margin: 0 0 10px;
        font-size: 22px;
      }

      .meta-row {
        display: flex;
        gap: 18px;
        color: var(--text-secondary);
        font-size: 14px;
      }
    }
  }

  .overview-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 16px;
    margin-bottom: 20px;

    .stat-card {
      background: var(--bg-primary);
      border-radius: var(--radius-lg);
      box-shadow: var(--shadow-sm);
      padding: 18px;

      .label {
        color: var(--text-secondary);
        font-size: 13px;
      }

      .value {
        font-size: 28px;
        margin-top: 8px;
        font-weight: 700;
        color: var(--text-primary);
      }
    }
  }

  .content-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 20px;

    .card-title {
      font-size: 16px;
      font-weight: 600;
      margin-bottom: 14px;
    }

    .info-item {
      display: flex;
      padding: 12px 0;
      border-bottom: 1px solid var(--border-light);

      &:last-child {
        border-bottom: none;
      }

      .k {
        width: 110px;
        color: var(--text-secondary);
      }

      .v {
        flex: 1;
      }
    }

    .security-row {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .row-title {
        font-weight: 600;
      }

      .row-desc {
        color: var(--text-secondary);
        font-size: 13px;
        margin-top: 6px;
      }
    }
  }

  @media (max-width: 1200px) {
    .overview-grid {
      grid-template-columns: repeat(2, 1fr);
    }
    .content-grid {
      grid-template-columns: 1fr;
    }
  }
}
</style>
