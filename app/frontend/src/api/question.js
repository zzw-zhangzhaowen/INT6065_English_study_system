import request from '@/utils/request'

export const questionApi = {
  // 获取题目列表
  getQuestionList(params) {
    return request({
      url: '/question/page',
      method: 'get',
      params
    })
  },

  // 获取题目详情
  getQuestionDetail(id) {
    return request({
      url: `/question/${id}`,
      method: 'get'
    })
  },

  // 创建题目
  createQuestion(data) {
    return request({
      url: '/question',
      method: 'post',
      data
    })
  },

  // 更新题目
  updateQuestion(id, data) {
    return request({
      url: `/question/${id}`,
      method: 'put',
      data
    })
  },

  // 删除题目
  deleteQuestion(id) {
    return request({
      url: `/question/${id}`,
      method: 'delete'
    })
  },

  // 批量导入题目
  importQuestions(file) {
    const formData = new FormData()
    formData.append('file', file)
    return request({
      url: '/question/import',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 获取题目分类
  getCategories() {
    return request({
      url: '/question-category/list',
      method: 'get'
    })
  },

  // 创建分类
  createCategory(data) {
    return request({
      url: '/question-category',
      method: 'post',
      data
    })
  },

  // 更新分类
  updateCategory(id, data) {
    return request({
      url: `/question-category/${id}`,
      method: 'put',
      data
    })
  },

  // 删除分类
  deleteCategory(id) {
    return request({
      url: `/question-category/${id}`,
      method: 'delete'
    })
  },

  // 随机获取练习题
  getRandomQuestions(params) {
    return request({
      url: '/question/random',
      method: 'get',
      params
    })
  },

  // 导出题目
  exportQuestions(params) {
    return request({
      url: '/question/export',
      method: 'get',
      params,
      responseType: 'blob'
    })
  },

  // 下载导入模板
  downloadTemplate() {
    return request({
      url: '/question/template',
      method: 'get',
      responseType: 'blob'
    })
  },

  // 获取分类树
  getCategoryTree() {
    return request({
      url: '/question-category/tree',
      method: 'get'
    })
  },

  // 批量删除题目
  batchDelete(ids) {
    return request({
      url: '/question/batch',
      method: 'delete',
      data: ids
    })
  },

  // 获取题库统计
  getStats() {
    return request({
      url: '/question/stats',
      method: 'get'
    })
  }
}
