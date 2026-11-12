import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/auth'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  // 安全解析JSON
  const safeJsonParse = (str, defaultValue) => {
    try {
      if (!str || str === 'undefined' || str === 'null') {
        return defaultValue
      }
      return JSON.parse(str)
    } catch (e) {
      return defaultValue
    }
  }

  // 状态
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(safeJsonParse(localStorage.getItem('userInfo'), null))
  const roles = ref(safeJsonParse(localStorage.getItem('roles'), []))

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const username = computed(() => userInfo.value?.username || '')
  const avatar = computed(() => userInfo.value?.avatar || '')
  const userId = computed(() => userInfo.value?.id || null)
  const userRole = computed(() => {
    if (roles.value.includes('ROLE_ADMIN')) return 'admin'
    if (roles.value.includes('ROLE_TEACHER')) return 'teacher'
    return 'student'
  })

  // 登录
  async function login(loginForm) {
    try {
      const res = await authApi.login(loginForm)
      const { token: newToken, userInfo: user } = res.data

      // 从userInfo中提取角色
      const userRole = user.role || 'ROLE_STUDENT'
      const userRoles = [userRole]

      // 保存到状态
      token.value = newToken
      userInfo.value = user
      roles.value = userRoles

      // 持久化
      localStorage.setItem('token', newToken)
      localStorage.setItem('userInfo', JSON.stringify(user))
      localStorage.setItem('roles', JSON.stringify(userRoles))

      return res
    } catch (error) {
      throw error
    }
  }

  // 注册
  async function register(registerForm) {
    try {
      const res = await authApi.register(registerForm)
      return res
    } catch (error) {
      throw error
    }
  }

  // 获取用户信息
  async function fetchUserInfo() {
    try {
      const res = await authApi.getUserInfo()
      const data = res.data
      const user = data.user || data
      const userRoles = Array.isArray(data.roles) ? data.roles : (data.role ? [data.role] : [])

      userInfo.value = user
      roles.value = userRoles

      localStorage.setItem('userInfo', JSON.stringify(user))
      localStorage.setItem('roles', JSON.stringify(userRoles))

      return res
    } catch (error) {
      throw error
    }
  }

  // 更新用户信息
  function updateUserInfo(info) {
    userInfo.value = { ...userInfo.value, ...info }
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
  }

  // 登出
  function logout() {
    token.value = ''
    userInfo.value = null
    roles.value = []

    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('roles')

    router.push('/login')
  }

  // 检查权限
  function hasRole(role) {
    return roles.value.includes(`ROLE_${role.toUpperCase()}`)
  }

  // 检查是否有任一权限
  function hasAnyRole(roleList) {
    return roleList.some(role => hasRole(role))
  }

  // 检查是否有所有权限
  function hasAllRoles(roleList) {
    return roleList.every(role => hasRole(role))
  }

  return {
    // 状态
    token,
    userInfo,
    roles,
    // 计算属性
    isLoggedIn,
    username,
    avatar,
    userId,
    userRole,
    // 方法
    login,
    register,
    fetchUserInfo,
    updateUserInfo,
    logout,
    hasRole,
    hasAnyRole,
    hasAllRoles
  }
})
