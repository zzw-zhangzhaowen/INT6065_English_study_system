import request from '@/utils/request'

export const examApi = {
  // 获取考试列表
  getExamList(params) {
    return request({
      url: '/exam/page',
      method: 'get',
      params
    })
  },

  // 获取考试详情
  getExamDetail(id, options = {}) {
    return request({
      url: `/exam/${id}`,
      method: 'get',
      silent: options.silent || false
    })
  },

  // 开始考试
  startExam(examId) {
    return request({
      url: '/exam/start',
      method: 'post',
      params: { examId }
    })
  },

  // 获取考试题目列表
  getExamQuestions(examId) {
    return request({
      url: `/exam/${examId}/questions`,
      method: 'get'
    })
  },

  // 保存单题答案(实时保存)
  saveAnswer(recordId, data) {
    return request({
      url: `/exam/record/${recordId}/answer`,
      method: 'post',
      data
    })
  },

  // 提交考试
  submitExam(data) {
    return request({
      url: '/exam/submit',
      method: 'post',
      data
    })
  },

  // 获取考试记录列表
  getExamRecords(params) {
    return request({
      url: '/exam/record/page',
      method: 'get',
      params
    })
  },

  // 获取考试记录详情
  getExamRecordDetail(recordId) {
    return request({
      url: `/exam/record/${recordId}`,
      method: 'get'
    })
  },

  // 创建考试（教师）
  createExam(data) {
    return request({
      url: '/exam',
      method: 'post',
      data
    })
  },

  // 更新考试（教师）
  updateExam(id, data) {
    return request({
      url: `/exam/${id}`,
      method: 'put',
      data
    })
  },

  // 删除考试（教师）
  deleteExam(id) {
    return request({
      url: `/exam/${id}`,
      method: 'delete'
    })
  },

  // 发布考试（教师）
  publishExam(id) {
    return request({
      url: `/exam/${id}/publish`,
      method: 'post'
    })
  },

  // 获取考试统计（教师）
  getExamStats(id) {
    return request({
      url: `/exam/${id}/stats`,
      method: 'get'
    })
  },

  // 获取可用考试列表（兼容页面调用）
  getAvailableExams(params) {
    return request({
      url: '/exam/page',
      method: 'get',
      params: { ...params, status: 1 }
    })
  },

  // 获取考试结果（兼容页面调用）
  getExamResult(recordId) {
    return request({
      url: `/exam/record/${recordId}`,
      method: 'get'
    })
  },

  // 获取进行中的考试
  getOngoingExams() {
    return request({
      url: '/exam/ongoing',
      method: 'get'
    })
  },

  // 获取教师的考试列表
  getTeacherExams(params) {
    return request({
      url: '/exam/teacher',
      method: 'get',
      params
    })
  },

  // 获取考试回顾详情
  getExamReview(recordId) {
    return request({
      url: `/exam/review/${recordId}`,
      method: 'get'
    })
  },

  // ==================== 题目管理 ====================

  // 添加考试题目
  addExamQuestion(examId, data) {
    return request({
      url: `/exam/${examId}/question`,
      method: 'post',
      params: data
    })
  },

  // 批量添加考试题目
  batchAddExamQuestions(examId, questions) {
    return request({
      url: `/exam/${examId}/questions/batch`,
      method: 'post',
      data: questions
    })
  },

  // 移除考试题目
  removeExamQuestion(examId, questionId) {
    return request({
      url: `/exam/${examId}/question/${questionId}`,
      method: 'delete'
    })
  },

  // ==================== 成绩分析 ====================

  // 获取考试学生成绩列表
  getExamRecordsByExam(examId) {
    return request({
      url: `/exam/${examId}/records`,
      method: 'get'
    })
  },

  // 获取考试题目正确率分析
  getQuestionAnalysis(examId) {
    return request({
      url: `/exam/${examId}/question-analysis`,
      method: 'get'
    })
  },

  // ==================== 考试监控 ====================

  // 获取考试监控数据
  getExamMonitor(examId) {
    return request({
      url: `/exam/${examId}/monitor`,
      method: 'get'
    })
  }
}
