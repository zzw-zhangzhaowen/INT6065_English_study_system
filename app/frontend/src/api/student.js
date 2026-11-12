import request from '@/utils/request'

export const studentApi = {
  // 获取学生仪表盘统计
  getDashboardStats() {
    return request({
      url: '/dashboard/student',
      method: 'get'
    })
  },

  // 获取今日任务
  getTodayTasks() {
    return request({
      url: '/study-plan/today',
      method: 'get'
    })
  },

  // 获取最近考试
  getRecentExams(params) {
    return request({
      url: '/exam/record/page',
      method: 'get',
      params: { ...params, pageSize: 5 }
    })
  },

  // 获取社区动态
  getCommunityFeeds(params) {
    return request({
      url: '/community/post/page',
      method: 'get',
      params: { ...params, pageSize: 5 }
    })
  },

  // 切换任务完成状态
  toggleTaskComplete(taskId) {
    return request({
      url: `/study-task/${taskId}/toggle`,
      method: 'put'
    })
  },

  // 获取学习进度
  getLearningProgress() {
    return request({
      url: '/user/statistics',
      method: 'get'
    })
  },

  // 获取学习趋势
  getLearningTrend(params) {
    return request({
      url: '/dashboard/trend',
      method: 'get',
      params
    })
  },

  // 获取推荐资源
  getRecommendedResources() {
    return request({
      url: '/learning/resource/popular',
      method: 'get'
    })
  },

  // 获取学习排名
  getLearningRank() {
    return request({
      url: '/checkin/ranking',
      method: 'get'
    })
  }
}
