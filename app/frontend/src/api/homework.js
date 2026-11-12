import request from '@/utils/request'

const homeworkApi = {
  // ==================== 教师端接口 ====================

  // 创建作业
  createHomework(data) {
    return request({
      url: '/homework',
      method: 'post',
      data
    })
  },

  // 更新作业
  updateHomework(id, data) {
    return request({
      url: `/homework/${id}`,
      method: 'put',
      data
    })
  },

  // 删除作业
  deleteHomework(id) {
    return request({
      url: `/homework/${id}`,
      method: 'delete'
    })
  },

  // 获取教师发布的作业列表
  getTeacherHomeworks(params) {
    return request({
      url: '/homework/teacher/list',
      method: 'get',
      params
    })
  },

  // 获取作业提交列表
  getHomeworkSubmissions(id, params) {
    return request({
      url: `/homework/${id}/submissions`,
      method: 'get',
      params
    })
  },

  // 批改作业
  gradeHomework(submissionId, data) {
    return request({
      url: `/homework/submission/${submissionId}/grade`,
      method: 'post',
      data
    })
  },

  // 获取待批改作业列表
  getPendingHomeworks(params) {
    return request({
      url: '/homework/pending',
      method: 'get',
      params
    })
  },

  // ==================== 学生端接口 ====================

  // 获取学生作业列表
  getStudentHomeworks(params) {
    return request({
      url: '/homework/student/list',
      method: 'get',
      params
    })
  },

  // 提交作业
  submitHomework(id, data) {
    return request({
      url: `/homework/${id}/submit`,
      method: 'post',
      data
    })
  },

  // 获取学生的提交记录
  getStudentSubmissions() {
    return request({
      url: '/homework/student/submissions',
      method: 'get'
    })
  },

  // 获取学生某个作业的提交详情
  getMySubmission(id) {
    return request({
      url: `/homework/${id}/my-submission`,
      method: 'get'
    })
  },

  // ==================== 通用接口 ====================

  // 获取作业详情
  getHomeworkById(id) {
    return request({
      url: `/homework/${id}`,
      method: 'get'
    })
  },

  // 获取作业统计
  getHomeworkStats(id) {
    return request({
      url: `/homework/${id}/stats`,
      method: 'get'
    })
  },

  // 获取作业列表（兼容页面调用）
  getHomeworkList(params) {
    return request({
      url: '/homework/teacher/list',
      method: 'get',
      params
    })
  },

  // 获取提交列表（兼容页面调用）
  getSubmissions(homeworkId, params) {
    return request({
      url: `/homework/${homeworkId}/submissions`,
      method: 'get',
      params
    })
  },

  // 批改提交（兼容页面调用）
  gradeSubmission(submissionId, data) {
    return request({
      url: `/homework/submission/${submissionId}/grade`,
      method: 'post',
      data
    })
  },

  // 获取作业详情（兼容页面调用）
  getHomeworkDetail(id) {
    return request({
      url: `/homework/${id}`,
      method: 'get'
    })
  }
}

export default homeworkApi
export { homeworkApi }
