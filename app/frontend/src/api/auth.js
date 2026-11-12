import request from '@/utils/request'

export const authApi = {
  // 登录
  login(data) {
    return request({
      url: '/auth/login',
      method: 'post',
      data
    })
  },

  // 注册
  register(data) {
    return request({
      url: '/auth/register',
      method: 'post',
      data
    })
  },

  // 获取当前用户信息
  getUserInfo() {
    return request({
      url: '/user/info',
      method: 'get'
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

  // 忘记密码 - 发送验证码
  sendResetCode(email) {
    return request({
      url: '/auth/forgot-password',
      method: 'post',
      data: { email }
    })
  },

  // 重置密码
  resetPassword(data) {
    return request({
      url: '/auth/reset-password',
      method: 'post',
      data
    })
  }
}
