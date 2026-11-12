import request from '@/utils/request'

export const checkinApi = {
  // 每日打卡
  checkin() {
    return request({
      url: '/checkin',
      method: 'post'
    })
  },

  // 检查今日是否已打卡
  checkToday() {
    return request({
      url: '/checkin/today',
      method: 'get'
    })
  },

  // 获取打卡日历
  getCalendar(params) {
    return request({
      url: '/checkin/calendar',
      method: 'get',
      params
    })
  },

  // 获取打卡统计
  getStats() {
    return request({
      url: '/checkin/stats',
      method: 'get'
    })
  },

  // 获取打卡排行榜
  getRanking(params) {
    return request({
      url: '/checkin/ranking',
      method: 'get',
      params
    })
  },

  // 更新今日学习数据
  updateStudyData(data) {
    return request({
      url: '/checkin/study-data',
      method: 'put',
      data
    })
  }
}
