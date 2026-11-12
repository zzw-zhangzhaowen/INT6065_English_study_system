import request from '@/utils/request'

export const groupApi = {
  // 获取小组列表
  getGroupList(params) {
    return request({
      url: '/group/page',
      method: 'get',
      params
    })
  },

  // 获取小组详情
  getGroupDetail(id) {
    return request({
      url: `/group/${id}`,
      method: 'get'
    })
  },

  // 创建小组
  createGroup(data) {
    return request({
      url: '/group',
      method: 'post',
      data
    })
  },

  // 更新小组
  updateGroup(id, data) {
    return request({
      url: `/group/${id}`,
      method: 'put',
      data
    })
  },

  // 解散小组
  dissolveGroup(id) {
    return request({
      url: `/group/${id}`,
      method: 'delete'
    })
  },

  // 加入小组
  joinGroup(id) {
    return request({
      url: `/group/${id}/join`,
      method: 'post'
    })
  },

  // 退出小组
  leaveGroup(id) {
    return request({
      url: `/group/${id}/leave`,
      method: 'post'
    })
  },

  // 移除成员
  removeMember(groupId, memberId) {
    return request({
      url: `/group/${groupId}/member/${memberId}`,
      method: 'delete'
    })
  },

  // 转让组长
  transferLeader(groupId, newLeaderId) {
    return request({
      url: `/group/${groupId}/transfer/${newLeaderId}`,
      method: 'post'
    })
  },

  // 设置管理员
  setAdmin(groupId, memberId, isAdmin) {
    return request({
      url: `/group/${groupId}/admin/${memberId}`,
      method: 'post',
      params: { isAdmin }
    })
  },

  // 获取我的小组
  getMyGroups() {
    return request({
      url: '/group/my',
      method: 'get'
    })
  },

  // 检查是否是成员
  checkMembership(id) {
    return request({
      url: `/group/${id}/check`,
      method: 'get'
    })
  },

  // 获取推荐小组
  getRecommendedGroups(limit = 10) {
    return request({
      url: '/group/recommend',
      method: 'get',
      params: { limit }
    })
  }
}
