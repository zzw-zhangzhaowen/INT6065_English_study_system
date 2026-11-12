import request from '@/utils/request'

export const messageApi = {
  // 发送消息给学生
  sendMessage(studentId, data) {
    return request({
      url: `/message/send/${studentId}`,
      method: 'post',
      data
    })
  },

  // 获取收件箱消息
  getInbox(params) {
    return request({
      url: '/message/inbox',
      method: 'get',
      params
    })
  },

  // 获取发件箱消息
  getOutbox(params) {
    return request({
      url: '/message/outbox',
      method: 'get',
      params
    })
  },

  // 获取消息详情
  getMessageDetail(id) {
    return request({
      url: `/message/${id}`,
      method: 'get'
    })
  },

  // 标记消息为已读
  markAsRead(id) {
    return request({
      url: `/message/${id}/read`,
      method: 'put'
    })
  },

  // 批量标记为已读
  markAllAsRead() {
    return request({
      url: '/message/read/batch',
      method: 'put'
    })
  },

  // 删除消息
  deleteMessage(id) {
    return request({
      url: `/message/${id}`,
      method: 'delete'
    })
  },

  // 获取未读消息数量
  getUnreadCount() {
    return request({
      url: '/message/unread/count',
      method: 'get'
    })
  },

  // 广播消息给所有学生
  broadcastMessage(data) {
    return request({
      url: '/message/broadcast',
      method: 'post',
      data
    })
  }
}
