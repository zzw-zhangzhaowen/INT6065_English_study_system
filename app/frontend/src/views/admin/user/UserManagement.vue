<template>
  <div class="user-management">
    <div class="page-header">
      <h2>用户管理</h2>
      <div class="header-actions">
        <el-button @click="importUsers">
          <el-icon><Upload /></el-icon>
          批量导入
        </el-button>
        <el-button type="primary" @click="addUser">
          <el-icon><Plus /></el-icon>
          添加用户
        </el-button>
      </div>
    </div>

    <div class="management-content">
      <!-- 筛选栏 -->
      <div class="filter-bar">
        <div class="filter-left">
          <el-select v-model="filterRole" placeholder="用户角色" clearable style="width: 120px">
            <el-option label="全部" value="" />
            <el-option label="学生" value="student" />
            <el-option label="教师" value="teacher" />
            <el-option label="管理员" value="admin" />
          </el-select>
          <el-select v-model="filterStatus" placeholder="状态" clearable style="width: 100px">
            <el-option label="全部" value="" />
            <el-option label="正常" value="active" />
            <el-option label="禁用" value="disabled" />
          </el-select>
        </div>
        <div class="filter-right">
          <el-input v-model="searchKeyword" placeholder="搜索用户名/邮箱/手机" :prefix-icon="Search" clearable style="width: 240px" />
        </div>
      </div>

      <!-- 用户表格 -->
      <el-table :data="users" style="width: 100%" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="110" />
        <el-table-column label="头像" width="80">
          <template #default="{ row }">
            <el-avatar :size="32" :src="getAvatarPreviewUrl(row.avatar)">{{ (row.username || '?').charAt(0) }}</el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" min-width="220" show-overflow-tooltip />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="role" label="角色" width="120">
          <template #default="{ row }">
            <el-tag :type="getRoleType(row.role)" size="small">{{ getRoleName(row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'danger'" size="small">
              {{ row.status === 'active' ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="150" />
        <el-table-column prop="lastLogin" label="最近登录" width="150" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="editUser(row)">编辑</el-button>
            <el-button type="primary" link size="small" @click="resetPassword(row)">重置密码</el-button>
            <el-button :type="row.status === 'active' ? 'danger' : 'success'" link size="small" @click="toggleStatus(row)">
              {{ row.status === 'active' ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 批量操作 -->
      <div v-if="selectedUsers.length > 0" class="batch-actions">
        <span>已选择 {{ selectedUsers.length }} 项</span>
        <el-button size="small" @click="batchExport">导出</el-button>
        <el-button type="danger" size="small" @click="batchDisable">批量禁用</el-button>
      </div>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          background
        />
      </div>
    </div>

    <!-- 添加/编辑用户弹窗 -->
    <el-dialog v-model="showUserDialog" :title="isEdit ? '编辑用户' : '添加用户'" width="500px">
      <el-form :model="userForm" label-width="80px">
        <el-form-item label="头像">
          <div class="avatar-upload">
            <el-avatar v-if="userForm.avatar" :src="getAvatarPreviewUrl(userForm.avatar)" :size="80" />
            <el-avatar v-else :size="80" :icon="User" />
            <el-button type="primary" size="small" @click="selectAvatar" :loading="avatarUploading" style="margin-left: 16px;">
              上传头像
            </el-button>
          </div>
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="userForm.username" placeholder="请输入用户名" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="userForm.realName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="userForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="userForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="userForm.roleIds" multiple style="width: 100%" placeholder="请选择角色">
            <el-option v-for="role in roleOptions" :key="role.id" :label="role.roleName" :value="role.id" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="!isEdit" label="密码">
          <el-input v-model="userForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showUserDialog = false">取消</el-button>
        <el-button type="primary" @click="saveUser">保存</el-button>
      </template>
    </el-dialog>

    <!-- 批量导入对话框 -->
    <el-dialog v-model="showImportDialog" title="批量导入用户" width="720px" :close-on-click-modal="false">
      <el-steps :active="importRows.length ? 2 : 1" align-center finish-status="success" style="margin-bottom: 20px;">
        <el-step title="下载模板" description="按模板格式填写用户信息" />
        <el-step title="上传文件" description="选择填好的 CSV 文件" />
        <el-step title="确认导入" description="核对数据后批量写入" />
      </el-steps>

      <div class="import-hint">
        <el-alert type="info" :closable="false" show-icon>
          <template #title>
            第一列「用户名」与「邮箱」为必填项，角色可填 学生 / 教师 / 管理员（留空默认学生），初始密码统一为 123456。
          </template>
        </el-alert>
      </div>

      <div class="import-toolbar">
        <el-button @click="downloadImportTemplate">
          <el-icon><Download /></el-icon>
          下载导入模板
        </el-button>
        <el-upload
          :auto-upload="false"
          :show-file-list="false"
          accept=".csv,text/csv"
          :on-change="handleImportFile"
          class="import-uploader"
        >
          <el-button type="primary" plain>
            <el-icon><Upload /></el-icon>
            选择 CSV 文件
          </el-button>
        </el-upload>
        <span v-if="importFileName" class="import-filename">{{ importFileName }}（{{ importRows.length }} 条待导入）</span>
      </div>

      <el-table v-if="importRows.length" :data="importRows" height="300" border size="small">
        <el-table-column type="index" label="#" width="50" />
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="realName" label="姓名" min-width="100" />
        <el-table-column prop="email" label="邮箱" min-width="170" />
        <el-table-column prop="phone" label="手机号" min-width="120" />
        <el-table-column prop="roleName" label="角色" width="90" />
        <el-table-column label="校验" width="90">
          <template #default="{ row }">
            <el-tag :type="row.valid ? 'success' : 'danger'" size="small">{{ row.valid ? '可导入' : '有缺失' }}</el-tag>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-else-if="importFileName" description="文件中没有可导入的数据行" :image-size="70" />

      <template #footer>
        <el-button @click="showImportDialog = false">取消</el-button>
        <el-button
          type="primary"
          :loading="importing"
          :disabled="!importableRows.length"
          @click="confirmImport"
        >
          确认导入 {{ importableRows.length }} 条
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Upload, Download, Search, User } from '@element-plus/icons-vue'
import { adminApi } from '@/api/admin'
import { learningApi } from '@/api/learning'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const filterRole = ref('')
const filterStatus = ref('')
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const selectedUsers = ref([])
const showUserDialog = ref(false)
const isEdit = ref(false)
const loading = ref(false)
const editingUserId = ref(null)

// 用户列表
const users = ref([])

// 用户表单
const userForm = reactive({
  username: '',
  realName: '',
  email: '',
  phone: '',
  roleIds: [],
  password: '',
  avatar: ''
})

const roleOptions = ref([])
const avatarUploading = ref(false)

const loadRoleOptions = async () => {
  try {
    const res = await adminApi.getRoleList()
    roleOptions.value = Array.isArray(res.data) ? res.data : []
  } catch (error) {
    console.error('加载角色列表失败:', error)
    ElMessage.error('加载角色列表失败')
  }
}

const getRoleType = (role) => {
  const types = { student: '', teacher: 'success', admin: 'warning' }
  return types[role] || ''
}

const getRoleName = (role) => {
  const names = { student: '学生', teacher: '教师', admin: '管理员' }
  return names[role] || role
}

const getAvatarPreviewUrl = (avatar) => {
  if (!avatar) return ''
  if (avatar.startsWith('http') || avatar.startsWith('data:') || avatar.startsWith('blob:')) {
    return avatar
  }
  const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
  const normalizedAvatar = avatar.startsWith('/') ? avatar : `/${avatar}`
  return `${baseUrl}${normalizedAvatar}`
}

// 选择头像
const selectAvatar = () => {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = 'image/*'
  input.onchange = async (e) => {
    const file = e.target.files[0]
    if (file) {
      avatarUploading.value = true
      try {
        const res = await learningApi.uploadImage(file)
        if (res.data?.url) {
          userForm.avatar = res.data.url
          ElMessage.success('头像上传成功')
        }
      } catch (error) {
        ElMessage.error('头像上传失败')
      } finally {
        avatarUploading.value = false
      }
    }
  }
  input.click()
}

// 加载用户列表
const loadUsers = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value
    }
    if (filterRole.value) params.role = filterRole.value
    if (filterStatus.value) params.status = filterStatus.value === 'active' ? 1 : 0
    if (searchKeyword.value) params.keyword = searchKeyword.value

    const res = await adminApi.getUserList(params)
    if (res.data) {
      users.value = (res.data.records || []).map(item => {
        const roles = Array.isArray(item.roles) ? item.roles : []
        const role = roles.includes('ROLE_ADMIN') ? 'admin' : (roles.includes('ROLE_TEACHER') ? 'teacher' : 'student')
        return {
          id: item.id,
          username: item.username,
          avatar: item.avatar || '',
          email: item.email || '',
          phone: item.phone ? item.phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2') : '',
          role,
          status: item.status === 1 ? 'active' : 'disabled',
          createTime: item.createTime?.substring(0, 10) || '',
          lastLogin: item.lastLoginTime?.replace('T', ' ') || ''
        }
      })
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('加载用户列表失败:', error)
    ElMessage.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

const handleSelectionChange = (selection) => {
  selectedUsers.value = selection
}

/* ---------------- 批量导入 ---------------- */
const showImportDialog = ref(false)
const importRows = ref([])
const importFileName = ref('')
const importing = ref(false)

const IMPORT_TEMPLATE = [
  ['用户名', '姓名', '邮箱', '手机号', '角色'],
  ['zhangsan', '张三', 'zhangsan@edu.cn', '13800000001', '学生'],
  ['lisi', '李四', 'lisi@edu.cn', '13800000002', '教师']
]

const ROLE_NAME_MAP = {
  '学生': 'ROLE_STUDENT', 'student': 'ROLE_STUDENT',
  '教师': 'ROLE_TEACHER', 'teacher': 'ROLE_TEACHER',
  '管理员': 'ROLE_ADMIN', 'admin': 'ROLE_ADMIN'
}

const importableRows = computed(() => importRows.value.filter(r => r.valid))

const downloadImportTemplate = () => {
  const content = '\ufeff' + IMPORT_TEMPLATE.map(r => r.join(',')).join('\n')
  const blob = new Blob([content], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = '用户批量导入模板.csv'
  a.click()
  URL.revokeObjectURL(url)
  ElMessage.success('模板已下载')
}

// 解析一行 CSV（支持引号包裹的字段）
const parseCsvLine = (line) => {
  const cells = []
  let cur = ''
  let inQuote = false
  for (let i = 0; i < line.length; i++) {
    const ch = line[i]
    if (inQuote) {
      if (ch === '"' && line[i + 1] === '"') { cur += '"'; i++ }
      else if (ch === '"') inQuote = false
      else cur += ch
    } else if (ch === '"') {
      inQuote = true
    } else if (ch === ',') {
      cells.push(cur.trim()); cur = ''
    } else {
      cur += ch
    }
  }
  cells.push(cur.trim())
  return cells
}

const handleImportFile = async (file) => {
  const raw = file?.raw || file
  if (!raw) return
  try {
    const text = await raw.text()
    const lines = text.replace(/^\ufeff/, '').split(/\r?\n/).filter(l => l.trim())
    if (lines.length < 2) {
      ElMessage.warning('文件中没有数据行')
      importRows.value = []
      importFileName.value = raw.name
      return
    }
    const header = parseCsvLine(lines[0]).map(h => h.replace(/\s/g, ''))
    const idx = (name) => header.findIndex(h => h.includes(name))
    const iUser = idx('用户名')
    const iName = idx('姓名')
    const iMail = idx('邮箱')
    const iPhone = idx('手机')
    const iRole = idx('角色')

    importRows.value = lines.slice(1).map((line, i) => {
      const cells = parseCsvLine(line)
      const username = (iUser >= 0 ? cells[iUser] : '') || ''
      const email = (iMail >= 0 ? cells[iMail] : '') || ''
      const roleLabel = (iRole >= 0 ? cells[iRole] : '') || '学生'
      const roleCode = ROLE_NAME_MAP[roleLabel] || ROLE_NAME_MAP['学生']
      const roleName = roleCode === 'ROLE_ADMIN' ? '管理员' : (roleCode === 'ROLE_TEACHER' ? '教师' : '学生')
      const missing = []
      if (!username) missing.push('用户名')
      if (!email) missing.push('邮箱')
      return {
        lineNo: i + 2,
        username,
        realName: (iName >= 0 ? cells[iName] : '') || '',
        email,
        phone: (iPhone >= 0 ? cells[iPhone] : '') || '',
        roleCode,
        roleName,
        valid: missing.length === 0,
        missing
      }
    })
    importFileName.value = raw.name
    const bad = importRows.value.filter(r => !r.valid).length
    if (bad) {
      ElMessage.warning(`解析到 ${importRows.value.length} 条数据，其中 ${bad} 条缺少必填项`)
    } else {
      ElMessage.success(`解析到 ${importRows.value.length} 条数据`)
    }
  } catch (e) {
    console.error('解析导入文件失败:', e)
    ElMessage.error('文件解析失败，请确认为 CSV 格式')
  }
}

const confirmImport = async () => {
  const rows = importableRows.value
  if (!rows.length) return
  importing.value = true
  let success = 0
  const failures = []
  for (const row of rows) {
    try {
      const roleIds = roleOptions.value
        .filter(r => r.roleCode === row.roleCode)
        .map(r => r.id)
      await adminApi.createUser({
        username: row.username,
        realName: row.realName,
        email: row.email,
        phone: row.phone,
        password: '123456',
        roleIds: roleIds.length ? roleIds : [3]
      })
      success++
    } catch (e) {
      failures.push(row.username)
    }
  }
  importing.value = false
  if (success && !failures.length) {
    ElMessage.success(`成功导入 ${success} 个用户`)
  } else if (success) {
    ElMessage.warning(`成功导入 ${success} 个，失败 ${failures.length} 个：${failures.slice(0, 3).join('、')}`)
  } else {
    ElMessage.error('导入失败，请检查数据格式')
  }
  if (success) {
    showImportDialog.value = false
    importRows.value = []
    importFileName.value = ''
    loadUsers()
  }
}

const importUsers = () => {
  importRows.value = []
  importFileName.value = ''
  showImportDialog.value = true
}

const addUser = () => {
  isEdit.value = false
  editingUserId.value = null
  Object.assign(userForm, {
    username: '',
    realName: '',
    email: '',
    phone: '',
    roleIds: [],
    password: '',
    avatar: ''
  })
  showUserDialog.value = true
}

const editUser = async (row) => {
  try {
    isEdit.value = true
    editingUserId.value = row.id
    const res = await adminApi.getUserDetail(row.id)
    const detail = res.data || {}
    const roleCodes = Array.isArray(detail.roles) ? detail.roles : []
    const matchedRoleIds = roleOptions.value
      .filter(role => roleCodes.includes(role.roleCode))
      .map(role => role.id)

    Object.assign(userForm, {
      username: detail.username || row.username,
      realName: detail.realName || '',
      email: detail.email || '',
      phone: detail.phone || '',
      roleIds: matchedRoleIds,
      password: '',
      avatar: detail.avatar || ''
    })
    showUserDialog.value = true
  } catch (error) {
    console.error('加载用户详情失败:', error)
    ElMessage.error('加载用户详情失败')
  }
}

const refreshAfterAction = async () => {
  await loadUsers()
}

const resetPassword = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要重置用户 ${row.username} 的密码吗？重置后密码为 123456`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await adminApi.resetUserPassword(row.id, '123456')
    ElMessage.success('密码已重置为 123456')
    await refreshAfterAction()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('重置密码失败:', error)
      ElMessage.error('重置密码失败')
    }
  }
}

const toggleStatus = async (row) => {
  const action = row.status === 'active' ? '禁用' : '启用'
  const newStatus = row.status === 'active' ? 0 : 1
  try {
    await ElMessageBox.confirm(`确定要${action}用户 ${row.username} 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await adminApi.toggleUserStatus(row.id, newStatus)
    await refreshAfterAction()
    ElMessage.success(`${action}成功`)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
    }
  }
}

const saveUser = async () => {
  if (!userForm.username) {
    ElMessage.warning('请输入用户名')
    return
  }
  if (!userForm.email) {
    ElMessage.warning('请输入邮箱')
    return
  }
  if (!isEdit.value && !userForm.password) {
    ElMessage.warning('请输入密码')
    return
  }
  if (!userForm.roleIds.length) {
    ElMessage.warning('请选择角色')
    return
  }

  try {
    if (isEdit.value) {
      await adminApi.updateUser(editingUserId.value, {
        realName: userForm.realName,
        email: userForm.email,
        phone: userForm.phone,
        avatar: userForm.avatar,
        roleIds: userForm.roleIds
      })

      if (Number(editingUserId.value) === Number(userStore.userInfo?.id)) {
        userStore.updateUserInfo({
          realName: userForm.realName,
          nickname: userForm.realName || userStore.userInfo?.username,
          email: userForm.email,
          phone: userForm.phone,
          avatar: userForm.avatar
        })
      }
    } else {
      await adminApi.createUser({
        username: userForm.username,
        password: userForm.password,
        realName: userForm.realName,
        email: userForm.email,
        phone: userForm.phone,
        avatar: userForm.avatar,
        roleIds: userForm.roleIds
      })
    }
    ElMessage.success('保存成功')
    showUserDialog.value = false
    await refreshAfterAction()
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败')
  }
}

const batchExport = () => {
  if (selectedUsers.value.length === 0) {
    ElMessage.warning('请先选择要导出的用户')
    return
  }
  // 导出为CSV格式
  let csvContent = '用户名,邮箱,手机号,角色,状态,创建时间,最后登录\n'
  selectedUsers.value.forEach(user => {
    csvContent += `${user.username},${user.email},${user.phone},${getRoleName(user.role)},${user.status === 'active' ? '正常' : '禁用'},${user.createTime},${user.lastLogin}\n`
  })
  const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8' })
  const url = window.URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = '用户列表.csv'
  a.click()
  window.URL.revokeObjectURL(url)
  ElMessage.success('导出成功')
}

const batchDisable = async () => {
  try {
    await ElMessageBox.confirm(`确定要禁用选中的 ${selectedUsers.value.length} 个用户吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    // 批量禁用
    for (const user of selectedUsers.value) {
      await adminApi.toggleUserStatus(user.id, 0)
    }
    ElMessage.success('批量禁用成功')
    await refreshAfterAction()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量禁用失败:', error)
    }
  }
}

// 监听筛选条件变化
watch([filterRole, filterStatus, currentPage, pageSize], () => {
  loadUsers()
})

// 搜索防抖
let searchTimer = null
watch(searchKeyword, () => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => {
    currentPage.value = 1
    loadUsers()
  }, 500)
})

onMounted(async () => {
  await loadRoleOptions()
  loadUsers()
})
</script>

<style lang="scss" scoped>
.user-management {
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

    .header-actions {
      display: flex;
      gap: 12px;
    }
  }

  .management-content {
    background: var(--bg-primary);
    border-radius: var(--radius-lg);
    padding: 24px;

    .filter-bar {
      display: flex;
      justify-content: space-between;
      margin-bottom: 20px;

      .filter-left {
        display: flex;
        gap: 12px;
      }
    }

    .batch-actions {
      display: flex;
      align-items: center;
      gap: 16px;
      padding: 16px;
      background: var(--bg-secondary);
      border-radius: var(--radius-md);
      margin-top: 16px;

      span {
        color: var(--text-secondary);
      }
    }

    .pagination-wrapper {
      display: flex;
      justify-content: center;
      padding-top: 24px;
    }
  }

  .import-hint {
    margin-bottom: 16px;

    :deep(.el-alert__title) {
      line-height: 1.6;
    }
  }

  .import-toolbar {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 16px;
    flex-wrap: wrap;

    .import-uploader {
      :deep(.el-upload) {
        display: inline-block;
      }
    }

    .import-filename {
      color: var(--text-secondary);
      font-size: 13px;
    }
  }
}
</style>
