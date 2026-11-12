import request from '@/utils/request'

export const monitorApi = {
  // 获取系统概览
  getOverview() {
    return request({
      url: '/monitor/overview',
      method: 'get'
    })
  },

  // 获取JVM信息
  getJvmInfo() {
    return request({
      url: '/monitor/jvm',
      method: 'get'
    })
  },

  // 获取内存信息
  getMemoryInfo() {
    return request({
      url: '/monitor/memory',
      method: 'get'
    })
  },

  // 获取CPU信息
  getCpuInfo() {
    return request({
      url: '/monitor/cpu',
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

  // 获取系统信息
  getSystemInfo() {
    return request({
      url: '/monitor/system',
      method: 'get'
    })
  },

  // 获取Redis信息
  getRedisInfo() {
    return request({
      url: '/monitor/redis',
      method: 'get'
    })
  },

  // 获取实时监控数据
  getRealtimeData() {
    return request({
      url: '/monitor/realtime',
      method: 'get'
    })
  },

  // 获取服务器状态（兼容页面调用）
  getServerStatus() {
    return request({
      url: '/monitor/system',
      method: 'get'
    })
  },

  // 获取告警列表
  getAlerts(params) {
    return request({
      url: '/monitor/overview',
      method: 'get',
      params
    })
  }
}
