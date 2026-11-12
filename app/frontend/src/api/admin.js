import request from '@/utils/request'

export const adminApi = {
  // 获取管理员仪表板统计（兼容旧调用）
  getDashboardStats() {
    return request({
      url: '/dashboard/admin',
      method: 'get'
    })
  },

  // 获取管理员仪表板统计
  getAdminDashboardStats() {
    return request({
      url: '/dashboard/admin',
      method: 'get'
    })
  },

  // 用户管理
  getUserList(params) {
    return request({
      url: '/admin/user/page',
      method: 'get',
      params
    })
  },

  createUser(data) {
    return request({
      url: '/admin/user',
      method: 'post',
      data
    })
  },

  getUserDetail(id) {
    return request({
      url: `/admin/user/${id}`,
      method: 'get'
    })
  },

  updateUser(id, data) {
    return request({
      url: `/admin/user/${id}`,
      method: 'put',
      data
    })
  },

  deleteUser(id) {
    return request({
      url: `/admin/user/${id}`,
      method: 'delete'
    })
  },

  resetUserPassword(id, newPassword) {
    return request({
      url: `/admin/user/${id}/password`,
      method: 'put',
      params: { newPassword }
    })
  },

  toggleUserStatus(id, status) {
    return request({
      url: `/admin/user/${id}/status`,
      method: 'put',
      params: { status }
    })
  },

  // 题库管理
  getQuestionStats() {
    return request({
      url: '/admin/question/stats',
      method: 'get'
    })
  },

  // 考试管理
  getExamStats() {
    return request({
      url: '/admin/exam/stats',
      method: 'get'
    })
  },

  // 系统配置
  getConfigList() {
    return request({
      url: '/admin/config/list',
      method: 'get'
    })
  },

  getConfig(key) {
    return request({
      url: `/admin/config/${key}`,
      method: 'get'
    })
  },

  updateConfig(key, value) {
    return request({
      url: `/admin/config/${key}`,
      method: 'put',
      params: { value }
    })
  },

  updateConfigs(configMap) {
    return request({
      url: '/admin/config/batch',
      method: 'put',
      data: configMap
    })
  },

  // 日志审计
  getLogList(params) {
    return request({
      url: '/log/page',
      method: 'get',
      params
    })
  },

  // 系统监控
  getSystemInfo() {
    return request({
      url: '/monitor/system',
      method: 'get'
    })
  },

  // 获取实时监控数据（CPU、内存）
  getRealtimeMetrics() {
    return request({
      url: '/monitor/realtime',
      method: 'get'
    })
  },

  // 获取磁盘信息
  getDiskInfo() {
    return request({
      url: '/monitor/disk',
      method: 'get'
    })
  },

  // 角色管理
  getRoleList() {
    return request({
      url: '/admin/role/list',
      method: 'get'
    })
  },

  assignRole(userId, roleIds) {
    return request({
      url: `/admin/user/${userId}/roles`,
      method: 'put',
      data: roleIds
    })
  },

  // 获取系统概览（兼容Dashboard页面调用）
  getOverview() {
    return request({
      url: '/dashboard/admin',
      method: 'get'
    })
  },

  // 获取系统状态
  getSystemStatus() {
    return request({
      url: '/monitor/system',
      method: 'get'
    })
  },

  // 获取最近操作记录
  getRecentOperations(params) {
    return request({
      url: '/log/recent',
      method: 'get',
      params
    })
  },

  // 发送测试邮件
  testEmail() {
    return request({
      url: '/admin/config/test-email',
      method: 'post'
    })
  },

  // 获取公开系统配置（Logo、名称），无需管理员权限
  getPublicConfig() {
    return request({
      url: '/auth/public-config',
      method: 'get'
    })
  }
}
