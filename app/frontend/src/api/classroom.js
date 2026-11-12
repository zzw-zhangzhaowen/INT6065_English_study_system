import request from '@/utils/request'

export const classroomApi = {
  // 获取课堂列表
  getClassroomList(params) {
    return request({
      url: '/classroom/page',
      method: 'get',
      params
    })
  },

  // 获取课堂详情
  getClassroomDetail(id) {
    return request({
      url: `/classroom/${id}`,
      method: 'get'
    })
  },

  // 创建课堂（教师）
  createClassroom(data) {
    return request({
      url: '/classroom',
      method: 'post',
      data
    })
  },

  // 更新课堂（教师）
  updateClassroom(id, data) {
    return request({
      url: `/classroom/${id}`,
      method: 'put',
      data
    })
  },

  // 删除课堂（教师）
  deleteClassroom(id) {
    return request({
      url: `/classroom/${id}`,
      method: 'delete'
    })
  },

  // 开始课堂（教师）
  startClassroom(id) {
    return request({
      url: `/classroom/${id}/start`,
      method: 'post'
    })
  },

  // 结束课堂（教师）
  endClassroom(id) {
    return request({
      url: `/classroom/${id}/end`,
      method: 'post'
    })
  },

  // 保存录播地址
  saveRecordUrl(id, recordUrl) {
    return request({
      url: `/classroom/${id}/record-url`,
      method: 'post',
      data: { recordUrl }
    })
  },

  // 加入课堂（学生）
  joinClassroom(id) {
    return request({
      url: `/classroom/${id}/join`,
      method: 'post'
    })
  },

  // 离开课堂（学生）
  leaveClassroom(id) {
    return request({
      url: `/classroom/${id}/leave`,
      method: 'post'
    })
  },

  // 签到
  signIn(id) {
    return request({
      url: `/classroom/${id}/sign`,
      method: 'post'
    })
  },

  // 教师发起签到
  activateSignIn(id) {
    return request({
      url: `/classroom/${id}/sign/activate`,
      method: 'post'
    })
  },

  // 获取教师的课堂列表
  getTeacherClassrooms() {
    return request({
      url: '/classroom/teacher',
      method: 'get'
    })
  },

  // 获取学生参与的课堂
  getStudentClassrooms(params) {
    return request({
      url: '/classroom/student',
      method: 'get',
      params
    })
  },

  // 获取正在直播的课堂
  getLiveClassrooms() {
    return request({
      url: '/classroom/live',
      method: 'get'
    })
  },

  // 获取我的课堂列表（兼容页面调用）
  getMyClassrooms(params) {
    return request({
      url: '/classroom/teacher',
      method: 'get',
      params
    })
  },

  // 获取录播列表
  getRecordings(params) {
    return request({
      url: '/classroom/page',
      method: 'get',
      params: { ...params, status: 'ENDED' }
    })
  },

  // 获取今日课程
  getTodayClassrooms() {
    return request({
      url: '/classroom/today',
      method: 'get'
    })
  },

  // 获取课堂消息历史
  getClassroomMessages(id, params) {
    return request({
      url: `/classroom/${id}/messages`,
      method: 'get',
      params
    })
  },

  // 获取签到列表
  getSignInList(id) {
    return request({
      url: `/classroom/${id}/sign/list`,
      method: 'get'
    })
  },

  // 获取签到状态
  getSignInStatus(id) {
    return request({
      url: `/classroom/${id}/sign/status`,
      method: 'get'
    })
  },

  // ==================== 问答 ====================

  // 获取课堂问题列表
  getQuestions(classroomId) {
    return request({
      url: `/classroom/${classroomId}/questions`,
      method: 'get'
    })
  },

  // 创建问题
  createQuestion(classroomId, data) {
    return request({
      url: `/classroom/${classroomId}/questions`,
      method: 'post',
      data
    })
  },

  // 获取问题的回答列表
  getAnswers(classroomId, questionId) {
    return request({
      url: `/classroom/${classroomId}/questions/${questionId}/answers`,
      method: 'get'
    })
  },

  // 回答问题
  createAnswer(classroomId, questionId, data) {
    return request({
      url: `/classroom/${classroomId}/questions/${questionId}/answers`,
      method: 'post',
      data
    })
  },

  // 点赞问题
  likeQuestion(classroomId, questionId) {
    return request({
      url: `/classroom/${classroomId}/questions/${questionId}/like`,
      method: 'post'
    })
  },

  // 关闭问题
  closeQuestion(classroomId, questionId) {
    return request({
      url: `/classroom/${classroomId}/questions/${questionId}/close`,
      method: 'post'
    })
  },

  // 标记最佳回答
  markBestAnswer(classroomId, answerId) {
    return request({
      url: `/classroom/${classroomId}/answers/${answerId}/best`,
      method: 'post'
    })
  },

  // ==================== 投票 ====================

  // 获取课堂投票列表
  getPolls(classroomId) {
    return request({
      url: `/classroom/${classroomId}/polls`,
      method: 'get'
    })
  },

  // 创建投票
  createPoll(classroomId, data) {
    return request({
      url: `/classroom/${classroomId}/polls`,
      method: 'post',
      data
    })
  },

  // 获取投票详情
  getPollDetail(classroomId, pollId) {
    return request({
      url: `/classroom/${classroomId}/polls/${pollId}`,
      method: 'get'
    })
  },

  // 投票
  vote(classroomId, pollId, data) {
    return request({
      url: `/classroom/${classroomId}/polls/${pollId}/vote`,
      method: 'post',
      data
    })
  },

  // 关闭投票
  closePoll(classroomId, pollId) {
    return request({
      url: `/classroom/${classroomId}/polls/${pollId}/close`,
      method: 'post'
    })
  },

  // 检查是否已投票
  hasVoted(classroomId, pollId) {
    return request({
      url: `/classroom/${classroomId}/polls/${pollId}/voted`,
      method: 'get'
    })
  }
}
