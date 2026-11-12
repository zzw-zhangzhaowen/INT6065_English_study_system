import request from '@/utils/request'

export const logApi = {
  // 获取日志列表
  getLogs(params) {
    return request({
      url: '/log/page',
      method: 'get',
      params
    })
  },

  // 获取日志详情
  getLogDetail(id) {
    return request({
      url: `/log/${id}`,
      method: 'get'
    })
  },

  // 删除日志
  deleteLog(id) {
    return request({
      url: `/log/${id}`,
      method: 'delete'
    })
  },

  // 批量删除日志
  batchDeleteLogs(ids) {
    return request({
      url: '/log/batch',
      method: 'delete',
      data: ids
    })
  },

  // 清空日志
  clearLogs() {
    return request({
      url: '/log/clear',
      method: 'delete'
    })
  },

  // 清理旧日志
  cleanOldLogs(days) {
    return request({
      url: `/log/clean/${days}`,
      method: 'delete'
    })
  },

  // 获取日志统计
  getLogStats() {
    return request({
      url: '/log/stats',
      method: 'get'
    })
  },

  // 获取当前用户操作日志
  getMyLogs(params) {
    return request({
      url: '/log/my',
      method: 'get',
      params
    })
  },

  // 获取最近操作日志
  getRecentLogs(params) {
    return request({
      url: '/log/recent',
      method: 'get',
      params
    })
  }
}
