import request from '@/utils/request'

export const learningApi = {
  // ========== 文件上传 ==========
  uploadFile(file, subDir) {
    const formData = new FormData()
    formData.append('file', file)
    if (subDir) formData.append('subDir', subDir)
    return request({
      url: '/file/upload',
      method: 'post',
      data: formData,
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },

  uploadImage(file) {
    const formData = new FormData()
    formData.append('file', file)
    return request({
      url: '/file/upload/image',
      method: 'post',
      data: formData,
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },

  uploadVideo(file) {
    const formData = new FormData()
    formData.append('file', file)
    return request({
      url: '/file/upload/video',
      method: 'post',
      data: formData,
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },

  uploadAudio(file) {
    const formData = new FormData()
    formData.append('file', file)
    return request({
      url: '/file/upload/audio',
      method: 'post',
      data: formData,
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },

  uploadDocument(file) {
    const formData = new FormData()
    formData.append('file', file)
    return request({
      url: '/file/upload/document',
      method: 'post',
      data: formData,
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },

  // ========== 学习资源 ==========
  getResourceList(params) {
    return request({
      url: '/learning/resource/page',
      method: 'get',
      params
    })
  },

  getResourceDetail(id) {
    return request({
      url: `/learning/resource/${id}`,
      method: 'get'
    })
  },

  createResource(data) {
    return request({
      url: '/learning/resource',
      method: 'post',
      data
    })
  },

  updateResource(id, data) {
    return request({
      url: `/learning/resource/${id}`,
      method: 'put',
      data
    })
  },

  deleteResource(id) {
    return request({
      url: `/learning/resource/${id}`,
      method: 'delete'
    })
  },

  batchDeleteResource(ids) {
    return request({
      url: '/learning/resource/batch',
      method: 'delete',
      data: ids
    })
  },

  updateResourceStatus(id, status) {
    return request({
      url: `/learning/resource/${id}/status`,
      method: 'put',
      params: { status }
    })
  },

  downloadResource(id) {
    return request({
      url: `/learning/resource/${id}/download`,
      method: 'get'
    })
  },

  likeResource(id) {
    return request({
      url: `/learning/resource/${id}/like`,
      method: 'post'
    })
  },

  collectResource(resourceId) {
    return request({
      url: `/learning/resource/${resourceId}/collect`,
      method: 'post'
    })
  },

  uncollectResource(resourceId) {
    return request({
      url: `/learning/resource/${resourceId}/collect`,
      method: 'delete'
    })
  },

  isCollected(resourceId) {
    return request({
      url: `/learning/resource/${resourceId}/is-collected`,
      method: 'get'
    })
  },

  getPopularResources() {
    return request({
      url: '/learning/resource/popular',
      method: 'get'
    })
  },

  // 兼容页面调用
  getResources(params) {
    return request({
      url: '/learning/resource/page',
      method: 'get',
      params
    })
  },

  // ========== 资源分类 ==========
  getCategoryTree() {
    return request({
      url: '/resource-category/tree',
      method: 'get'
    })
  },

  getCategoryList() {
    return request({
      url: '/resource-category/list',
      method: 'get'
    })
  },

  createCategory(data) {
    return request({
      url: '/resource-category',
      method: 'post',
      data
    })
  },

  updateCategory(id, data) {
    return request({
      url: `/resource-category/${id}`,
      method: 'put',
      data
    })
  },

  deleteCategory(id) {
    return request({
      url: `/resource-category/${id}`,
      method: 'delete'
    })
  },

  // ========== 错题本 ==========
  getWrongQuestions(params) {
    return request({
      url: '/wrong-question/page',
      method: 'get',
      params
    })
  },

  getWrongQuestionDetail(id) {
    return request({
      url: `/wrong-question/${id}`,
      method: 'get'
    })
  },

  markAsMastered(id, masteryLevel = 3) {
    return request({
      url: `/wrong-question/${id}/mastery`,
      method: 'put',
      params: { masteryLevel }
    })
  },

  deleteWrongQuestion(id) {
    return request({
      url: `/wrong-question/${id}`,
      method: 'delete'
    })
  },

  getWrongQuestionStatistics() {
    return request({
      url: '/wrong-question/statistics',
      method: 'get'
    })
  },

  // ========== 学习笔记 ==========
  getNoteList(params) {
    return request({
      url: '/study-note/page',
      method: 'get',
      params
    })
  },

  getNoteDetail(id) {
    return request({
      url: `/study-note/${id}`,
      method: 'get'
    })
  },

  createNote(data) {
    return request({
      url: '/study-note',
      method: 'post',
      data
    })
  },

  updateNote(id, data) {
    return request({
      url: `/study-note/${id}`,
      method: 'put',
      data
    })
  },

  deleteNote(id) {
    return request({
      url: `/study-note/${id}`,
      method: 'delete'
    })
  },

  // ========== 学习计划 ==========
  getPlanList(params) {
    return request({
      url: '/study-plan/page',
      method: 'get',
      params
    })
  },

  getPlanDetail(id) {
    return request({
      url: `/study-plan/${id}`,
      method: 'get'
    })
  },

  createPlan(data) {
    return request({
      url: '/study-plan',
      method: 'post',
      data
    })
  },

  updatePlan(id, data) {
    return request({
      url: `/study-plan/${id}`,
      method: 'put',
      data
    })
  },

  deletePlan(id) {
    return request({
      url: `/study-plan/${id}`,
      method: 'delete'
    })
  },

  updatePlanProgress(id, progress) {
    return request({
      url: `/study-plan/${id}/progress`,
      method: 'put',
      params: { progress }
    })
  },

  // 兼容页面调用
  getNotes(params) {
    return request({
      url: '/study-note/page',
      method: 'get',
      params
    })
  },

  getWrongStats() {
    return request({
      url: '/wrong-question/statistics',
      method: 'get'
    })
  },

  removeWrongQuestion(id) {
    return request({
      url: `/wrong-question/${id}`,
      method: 'delete'
    })
  },

  getLearningReport() {
    return request({
      url: '/dashboard/student',
      method: 'get'
    })
  },

  getLearningTrend(params = { days: 7 }) {
    return request({
      url: '/dashboard/trend',
      method: 'get',
      params
    })
  },

  getLearningDistribution() {
    return request({
      url: '/wrong-question/statistics',
      method: 'get'
    })
  },

  // ========== 学习任务 ==========
  getTaskList(params) {
    return request({
      url: '/study-task/page',
      method: 'get',
      params
    })
  },

  getTasksByPlanId(planId) {
    return request({
      url: `/study-task/plan/${planId}`,
      method: 'get'
    })
  },

  getTaskDetail(id) {
    return request({
      url: `/study-task/${id}`,
      method: 'get'
    })
  },

  createTask(data) {
    return request({
      url: '/study-task',
      method: 'post',
      data
    })
  },

  updateTask(id, data) {
    return request({
      url: `/study-task/${id}`,
      method: 'put',
      data
    })
  },

  deleteTask(id) {
    return request({
      url: `/study-task/${id}`,
      method: 'delete'
    })
  },

  getTaskStatistics() {
    return request({
      url: '/study-task/statistics',
      method: 'get'
    })
  },

  toggleTaskStatus(id) {
    return request({
      url: `/study-task/${id}/toggle`,
      method: 'put'
    })
  },

  getPracticeQuestions(params) {
    return request({
      url: '/wrong-question/practice',
      method: 'get',
      params
    })
  },

  getSimilarQuestions(id, params) {
    return request({
      url: `/wrong-question/${id}/similar`,
      method: 'get',
      params
    })
  },

  recordPracticeResult(data) {
    return request({
      url: '/wrong-question/practice/result',
      method: 'post',
      data
    })
  },

  addToWrongBook(questionId) {
    return request({
      url: '/wrong-question',
      method: 'post',
      params: { questionId }
    })
  }
}
