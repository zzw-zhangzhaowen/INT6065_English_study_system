import request from '@/utils/request'

export const teacherApi = {
  // 获取教师仪表板统计
  getDashboardStats() {
    return request({
      url: '/dashboard/teacher',
      method: 'get'
    })
  },

  // 获取学生列表
  getStudentList(params) {
    return request({
      url: '/teacher/student/page',
      method: 'get',
      params
    })
  },

  // 获取学生详情
  getStudentDetail(id) {
    return request({
      url: `/teacher/student/${id}`,
      method: 'get'
    })
  },

  // 获取今日课程安排
  getTodaySchedule() {
    return request({
      url: '/classroom/today',
      method: 'get'
    })
  },

  // 获取待批改作业
  getPendingHomework(params) {
    return request({
      url: '/homework/pending',
      method: 'get',
      params
    })
  },

  // 获取进行中的考试
  getOngoingExams() {
    return request({
      url: '/exam/ongoing',
      method: 'get'
    })
  },

  // 获取学习预警学生
  getLearningAlerts(params) {
    return request({
      url: '/teacher/student/alerts',
      method: 'get',
      params
    })
  },

  // 获取题库统计
  getQuestionStats() {
    return request({
      url: '/question/stats',
      method: 'get'
    })
  },

  // 获取成绩趋势数据
  getScoreTrend(params) {
    return request({
      url: '/teacher/score/trend',
      method: 'get',
      params
    })
  },

  // 发送消息给学生
  sendMessageToStudent(studentId, data) {
    return request({
      url: `/message/send/${studentId}`,
      method: 'post',
      data
    })
  },

  // 获取班级列表
  getClassList() {
    return request({
      url: '/teacher/class/list',
      method: 'get'
    })
  },

  // 获取今日课程（兼容Dashboard页面调用）
  getTodayClasses() {
    return request({
      url: '/classroom/today',
      method: 'get'
    })
  }
}
