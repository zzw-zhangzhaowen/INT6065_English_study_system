import request from '@/utils/request'

export const aiApi = {
  // AI对话
  chat(message) {
    return request({
      url: '/ai/chat',
      method: 'post',
      data: { message }
    })
  },

  // 获取对话历史
  getChatHistory(params) {
    return request({
      url: '/ai/chat/history',
      method: 'get',
      params
    })
  },

  // 清空对话历史
  clearChatHistory(sessionId) {
    return request({
      url: '/ai/chat/history',
      method: 'delete',
      params: { sessionId }
    })
  },

  // 作文批改
  correctWriting(data) {
    return request({
      url: '/ai/writing/correct',
      method: 'post',
      data
    })
  },

  // 获取作文批改历史
  getWritingHistory(params) {
    return request({
      url: '/ai/writing/history',
      method: 'get',
      params
    })
  },

  // 获取作文批改详情
  getWritingDetail(id) {
    return request({
      url: `/ai/writing/${id}`,
      method: 'get'
    })
  },

  // 口语评测
  evaluatePronunciation(data) {
    return request({
      url: '/ai/pronunciation/evaluate',
      method: 'post',
      data: {
        audioUrl: data.audioUrl || data.audio,
        referenceText: data.referenceText || data.text
      }
    })
  },

  // 获取口语评测历史
  getPronunciationHistory(params) {
    return request({
      url: '/ai/pronunciation/history',
      method: 'get',
      params
    })
  },

  // 获取口语评测详情
  getPronunciationDetail(id) {
    return request({
      url: `/ai/pronunciation/${id}`,
      method: 'get'
    })
  },

  // 智能推荐学习内容
  getRecommendations(params) {
    return request({
      url: '/ai/learning-path/recommend',
      method: 'get',
      params
    })
  },

  // 语法检查
  checkGrammar(text) {
    return request({
      url: '/ai/grammar/check',
      method: 'post',
      data: { text }
    })
  },

  // 获取会话列表
  getSessionList() {
    return request({
      url: '/ai/chat/sessions',
      method: 'get'
    })
  },

  // 获取AI使用统计
  getUsageStats(params) {
    return request({
      url: '/ai/stats',
      method: 'get',
      params
    })
  },

  // 获取最近使用记录
  getRecentUsage(params) {
    return request({
      url: '/ai/recent',
      method: 'get',
      params
    })
  },

  // 发送消息（兼容AIChat页面调用）
  sendMessage(message, sessionId) {
    return request({
      url: '/ai/chat',
      method: 'post',
      data: { message, sessionId }
    })
  },

  // 获取消息历史（兼容AIChat页面调用）
  getMessages(sessionId) {
    return request({
      url: '/ai/chat/history',
      method: 'get',
      params: { sessionId }
    })
  },

  // 创建新会话
  createSession() {
    return request({
      url: '/ai/chat/sessions',
      method: 'post'
    })
  },

  // 删除会话
  deleteSession(sessionId) {
    return request({
      url: `/ai/chat/sessions/${sessionId}`,
      method: 'delete'
    })
  },

  // 提交作文（兼容AIWriting页面调用）
  submitWriting(data) {
    return request({
      url: '/ai/writing/correct',
      method: 'post',
      data
    })
  },

  // 获取作文列表（兼容AIWriting页面调用）
  getWritings(params) {
    return request({
      url: '/ai/writing/history',
      method: 'get',
      params
    })
  }
}
