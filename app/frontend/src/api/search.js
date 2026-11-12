import request from '@/utils/request'

const searchApi = {
  // 综合搜索 — 聚合题目、资源、帖子的真实数据
  async globalSearch(params) {
    const keyword = params.keyword || ''
    if (!keyword.trim()) {
      return { code: 200, data: { records: [], total: 0 } }
    }

    const results = []

    // 并行搜索题目、资源、帖子
    const [questionsRes, resourcesRes, postsRes] = await Promise.allSettled([
      request({ url: '/question/page', method: 'get', params: { keyword, pageNum: 1, pageSize: 5 }, silent: true }),
      request({ url: '/learning/resource/page', method: 'get', params: { keyword, pageNum: 1, pageSize: 5 }, silent: true }),
      request({ url: '/community/post/page', method: 'get', params: { keyword, pageNum: 1, pageSize: 5 }, silent: true })
    ])

    if (questionsRes.status === 'fulfilled' && questionsRes.value?.data?.records) {
      questionsRes.value.data.records.forEach(q => {
        results.push({
          id: q.id,
          type: 'question',
          typeText: '题目',
          title: q.title || q.content || '题目',
          description: q.questionType || '',
          time: ''
        })
      })
    }

    if (resourcesRes.status === 'fulfilled' && resourcesRes.value?.data?.records) {
      resourcesRes.value.data.records.forEach(r => {
        results.push({
          id: r.id,
          type: 'resource',
          typeText: '资源',
          title: r.title || r.name || '资源',
          description: r.description || '',
          time: ''
        })
      })
    }

    if (postsRes.status === 'fulfilled' && postsRes.value?.data?.records) {
      postsRes.value.data.records.forEach(p => {
        results.push({
          id: p.id,
          type: 'post',
          typeText: '帖子',
          title: p.title || '帖子',
          description: p.content ? p.content.substring(0, 50) : '',
          time: ''
        })
      })
    }

    return {
      code: 200,
      data: {
        records: results,
        total: results.length
      }
    }
  },

  // 搜索题目
  searchQuestions(params) {
    return request({ url: '/question/page', method: 'get', params, silent: true })
  },

  // 搜索学习资源
  searchResources(params) {
    return request({ url: '/learning/resource/page', method: 'get', params, silent: true })
  },

  // 搜索社区帖子
  searchPosts(params) {
    return request({ url: '/community/post/page', method: 'get', params, silent: true })
  },

  // 搜索用户（暂无后端接口）
  searchUsers(params) {
    return Promise.resolve({ code: 200, data: [] })
  },

  // 获取搜索历史（使用 localStorage）
  getSearchHistory() {
    try {
      const history = JSON.parse(localStorage.getItem('searchHistory') || '[]')
      return Promise.resolve({ code: 200, data: history })
    } catch {
      return Promise.resolve({ code: 200, data: [] })
    }
  },

  // 保存搜索历史（使用 localStorage）
  saveSearchHistory(keyword) {
    try {
      const history = JSON.parse(localStorage.getItem('searchHistory') || '[]')
      const filtered = history.filter(k => k !== keyword)
      filtered.unshift(keyword)
      localStorage.setItem('searchHistory', JSON.stringify(filtered.slice(0, 10)))
    } catch { /* ignore */ }
    return Promise.resolve({ code: 200, message: '保存成功' })
  }
}

export default searchApi
export { searchApi }
