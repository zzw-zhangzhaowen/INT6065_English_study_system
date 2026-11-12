import request from '@/utils/request'

export const communityApi = {
  // 获取帖子列表
  getPostList(params) {
    return request({
      url: '/community/post/page',
      method: 'get',
      params
    })
  },

  // 获取帖子详情
  getPostDetail(id) {
    return request({
      url: `/community/post/${id}`,
      method: 'get'
    })
  },

  // 创建帖子
  createPost(data) {
    return request({
      url: '/community/post',
      method: 'post',
      data
    })
  },

  // 更新帖子
  updatePost(id, data) {
    return request({
      url: `/community/post/${id}`,
      method: 'put',
      data
    })
  },

  // 删除帖子
  deletePost(id) {
    return request({
      url: `/community/post/${id}`,
      method: 'delete'
    })
  },

  // 点赞帖子
  likePost(id) {
    return request({
      url: `/community/post/${id}/like`,
      method: 'post'
    })
  },

  // 收藏帖子
  collectPost(id) {
    return request({
      url: `/community/post/${id}/collect`,
      method: 'post'
    })
  },

  // 置顶帖子
  topPost(id, isTop) {
    return request({
      url: `/community/post/${id}/top`,
      method: 'post',
      params: { isTop }
    })
  },

  // 设为精华
  featurePost(id, isFeatured) {
    return request({
      url: `/community/post/${id}/feature`,
      method: 'post',
      params: { isFeatured }
    })
  },

  // 获取回复列表
  getReplyList(postId, params) {
    return request({
      url: `/community/reply/page`,
      method: 'get',
      params: {
        postId,
        ...params
      }
    })
  },

  // 创建回复
  createReply(data) {
    return request({
      url: '/community/reply',
      method: 'post',
      data
    })
  },

  // 删除回复
  deleteReply(id) {
    return request({
      url: `/community/reply/${id}`,
      method: 'delete'
    })
  },

  // 点赞回复
  likeReply(id) {
    return request({
      url: `/community/reply/${id}/like`,
      method: 'post'
    })
  },

  // 获取帖子列表（兼容页面调用）
  getPosts(params) {
    return request({
      url: '/community/post/page',
      method: 'get',
      params
    })
  },

  // 切换点赞状态（兼容页面调用）
  toggleLike(postId) {
    return request({
      url: `/community/post/${postId}/like`,
      method: 'post'
    })
  },

  // 获取热门话题
  getHotTopics(limit = 10) {
    return request({
      url: '/community/hot-topics',
      method: 'get',
      params: { limit }
    })
  },

  // 获取活跃用户
  getActiveUsers(limit = 10) {
    return request({
      url: '/community/active-users',
      method: 'get',
      params: { limit }
    })
  },

  // 取消收藏帖子（后端为toggle接口，POST即可切换状态）
  uncollectPost(id) {
    return request({
      url: `/community/post/${id}/collect`,
      method: 'post'
    })
  },

  // 取消点赞帖子（后端为toggle接口，POST即可切换状态）
  unlikePost(id) {
    return request({
      url: `/community/post/${id}/like`,
      method: 'post'
    })
  },

  // 关注用户
  followUser(userId) {
    return request({
      url: `/user/${userId}/follow`,
      method: 'post'
    })
  },

  // 取消关注用户
  unfollowUser(userId) {
    return request({
      url: `/user/${userId}/follow`,
      method: 'delete'
    })
  }
}
