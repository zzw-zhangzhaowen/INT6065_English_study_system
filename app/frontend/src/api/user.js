import request from '@/utils/request'

export const userApi = {
  // 获取用户资料
  getProfile() {
    return request({
      url: '/user/info',
      method: 'get'
    })
  },

  // 更新用户资料
  updateProfile(data) {
    return request({
      url: '/user/info',
      method: 'put',
      data
    })
  },

  // 修改密码
  changePassword(data) {
    return request({
      url: '/user/password',
      method: 'put',
      data
    })
  },

  // 上传头像
  uploadAvatar(file) {
    const formData = new FormData()
    formData.append('file', file)
    return request({
      url: '/file/upload/avatar',
      method: 'post',
      data: formData
    })
  },

  // 获取学习统计
  getLearningStats() {
    return request({
      url: '/user/statistics',
      method: 'get'
    })
  },

  // 获取用户信息（兼容页面调用）
  getUserInfo() {
    return request({
      url: '/user/info',
      method: 'get'
    })
  },

  // 获取成就列表
  getAchievements() {
    return request({
      url: '/user/achievements',
      method: 'get'
    })
  },

  // 获取用户列表（管理员）
  getUserList(params) {
    return request({
      url: '/admin/user/page',
      method: 'get',
      params
    })
  },

  // 创建用户（管理员）
  createUser(data) {
    return request({
      url: '/admin/user',
      method: 'post',
      data
    })
  },

  // 更新用户（管理员）
  updateUser(id, data) {
    return request({
      url: `/admin/user/${id}`,
      method: 'put',
      data
    })
  },

  // 重置用户密码（管理员）
  resetPassword(id, newPassword = '123456') {
    return request({
      url: `/admin/user/${id}/password`,
      method: 'put',
      data: { newPassword }
    })
  },

  // 切换用户状态（管理员）
  toggleStatus(id) {
    return request({
      url: `/admin/user/${id}/status`,
      method: 'put'
    })
  }
}
