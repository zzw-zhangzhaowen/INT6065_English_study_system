// ============================================================
// Mock 解析器 · 后端不可用时接管请求
// 只在「网络层失败 / 5xx / 超时」时生效，正常响应不拦截
// ============================================================

import * as db from './db.js'

const ok = data => ({ code: 200, message: 'success', data })
const page = p => ({ code: 200, message: 'success', data: p })

function pickRole(username = '') {
  const u = String(username).toLowerCase()
  // 管理员端已并入教师端：admin 仍可登录，进入的是教师工作台（含系统管理）
  if (u.includes('admin')) return 'ROLE_TEACHER'
  if (u.includes('teacher') || u.includes('t0') || u === 'laoshi') return 'ROLE_TEACHER'
  return 'ROLE_STUDENT'
}

function buildUser(username) {
  const role = pickRole(username)
  if (role === 'ROLE_TEACHER') {
    const isAdminLike = String(username || '').toLowerCase().includes('admin')
    return {
      id: isAdminLike ? 1 : 2001,
      username: username || 'teacher',
      nickname: isAdminLike ? '系统管理员' : '李老师',
      realName: isAdminLike ? '系统管理员' : '李静',
      role,
      avatar: '',
      className: '2024级英语1班',
      title: isAdminLike ? '系统管理员' : '讲师',
      department: '外国语学院'
    }
  }
  const s = db.STUDENTS[0]
  return {
    id: s.id,
    username: username || s.username,
    nickname: s.realName,
    realName: s.realName,
    role,
    avatar: '',
    className: s.className,
    level: s.level
  }
}

/**
 * 尝试用模拟数据响应请求
 * @param {Object} config axios 请求配置
 * @returns {Object|null} 可直接作为 response.data 返回
 */
export function resolveMock(config) {
  if (!config) return null
  const url = String(config.url || '').split('?')[0]
  const method = String(config.method || 'get').toLowerCase()
  let body = {}
  try {
    body = typeof config.data === 'string' ? JSON.parse(config.data || '{}') : (config.data || {})
  } catch (e) {
    body = {}
  }
  const params = config.params || {}

  /* ---------------- 认证 ---------------- */
  if (url === '/auth/login' && method === 'post') {
    const user = buildUser(body.username)
    return ok({ token: `mock-token-${Date.now()}`, userInfo: user })
  }
  if (url === '/auth/register' && method === 'post') {
    return ok({ id: Date.now(), username: body.username })
  }
  if (url === '/user/info' && method === 'get') {
    const cached = typeof localStorage !== 'undefined' ? localStorage.getItem('userInfo') : null
    let u = { id: 1042, username: 'student', nickname: '张伟', realName: '张伟', role: 'ROLE_STUDENT', className: '2024级英语1班' }
    try {
      if (cached) u = JSON.parse(cached)
    } catch (e) { /* ignore */ }
    return ok({ user: u, roles: [u.role] })
  }
  if (url.startsWith('/auth/forgot-password') || url.startsWith('/auth/reset-password')) {
    return ok({ success: true })
  }

  /* ---------------- 在线课堂 ---------------- */
  if (url === '/classroom/live') return ok(db.classroomLive())
  if (url === '/classroom/page') return page(db.classroomPage(params))
  if (url === '/classroom/teacher') return ok(db.CLASSROOMS.filter(c => c.teacherName === '李静'))
  if (url === '/classroom/student') return ok(db.CLASSROOMS)
  if (/^\/classroom\/\d+$/.test(url) && method === 'get') {
    return ok(db.CLASSROOMS.find(c => c.id === Number(url.split('/').pop())) || db.CLASSROOMS[0])
  }

  /* ---------------- 学习社区 ---------------- */
  if (url === '/community/post/page') return page(db.communityPostPage(params))
  if (url === '/community/post' && method === 'post') {
    return ok({ id: Date.now(), ...body, author: body.author || '张伟', likes: 0, comments: 0, viewCount: 0 })
  }
  if (url === '/community/hot-topics') return ok(db.HOT_TOPICS)
  if (url === '/community/active-users') return ok(db.ACTIVE_USERS)
  if (/^\/community\/post\/\d+$/.test(url) && method === 'get') {
    const id = Number(url.split('/').pop())
    return ok(db.COMMUNITY_POSTS.find(p => p.id === id) || db.COMMUNITY_POSTS[0])
  }
  if (/^\/community\/post\/\d+\/like$/.test(url)) return ok({ success: true })
  if (/^\/community\/post\/\d+\/comment/.test(url)) return ok({ success: true })

  /* ---------------- 学习小组 ---------------- */
  if (url === '/group/recommend') return ok(db.groupRecommend(Number(params.limit) || 5))
  if (url === '/group/my') return ok(db.groupMy())
  if (url === '/group/page') return page(db.groupPage(params))
  if (/^\/group\/\d+\/join$/.test(url)) return ok({ success: true })
  if (/^\/group\/\d+\/quit$/.test(url)) return ok({ success: true })
  if (/^\/community\/user\/\d+\/(follow|unfollow)$/.test(url)) return ok({ success: true })

  /* ---------------- 系统管理（原管理员端） ---------------- */
  if (url === '/admin/user/page') return page(db.adminUserPage(params))
  if (url === '/admin/role/list') {
    return ok([
      { id: 1, roleCode: 'ROLE_ADMIN', roleName: '系统管理员' },
      { id: 2, roleCode: 'ROLE_TEACHER', roleName: '教师' },
      { id: 3, roleCode: 'ROLE_STUDENT', roleName: '学生' }
    ])
  }
  if (url === '/admin/config/list') return ok(db.SYSTEM_CONFIG)
  if (url.startsWith('/admin/config/') && method === 'post') return ok({ success: true })
  if (url === '/admin/question/stats') return ok({ total: 14, byType: { READING: 5, SINGLE_CHOICE: 5, CLOZE: 2, TRANSLATION: 1, WRITING: 1 } })
  if (url === '/admin/exam/stats') return ok({ total: 8, ongoing: 1, finished: 3 })
  if (url === '/dashboard/admin') {
    return ok({
      totalUsers: db.STUDENTS.length + 12,
      todayNewUsers: 3,
      onlineUsers: db.MONITOR_DATA.overview.onlineUsers,
      ongoingExams: 1,
      totalExamRecords: 96,
      storageUsed: 186,
      storageTotal: 512,
      roleDistribution: [
        { name: '学生', value: db.STUDENTS.length },
        { name: '教师', value: 12 }
      ]
    })
  }

  if (url === '/log/page') return page(db.operationLogPage(params))
  if (url === '/log/recent') return ok(db.OPERATION_LOGS.slice(0, 6))
  if (url === '/log/stats') {
    const failed = db.OPERATION_LOGS.filter(l => l.status === 0).length
    return ok({
      totalCount: db.OPERATION_LOGS.length,
      todayCount: db.OPERATION_LOGS.filter(l => String(l.createTime).startsWith('2026-11-13')).length,
      successCount: db.OPERATION_LOGS.length - failed,
      failCount: failed
    })
  }

  if (url === '/monitor/cpu') return ok(db.MONITOR_DATA.cpu)
  if (url === '/monitor/memory') return ok(db.MONITOR_DATA.memory)
  if (url === '/monitor/disk') return ok(db.MONITOR_DATA.disk)
  if (url === '/monitor/system') return ok(db.MONITOR_DATA.system)
  if (url === '/monitor/overview') return ok(db.MONITOR_DATA.overview)
  if (url === '/monitor/realtime') return ok(db.MONITOR_DATA.realtime)
  if (url === '/monitor/realtime/history') return ok(db.MONITOR_DATA.realtimeHistory)
  if (url === '/monitor/jvm') return ok(db.MONITOR_DATA.jvm)
  if (url === '/monitor/redis') return ok(db.MONITOR_DATA.redis)

  if (url === '/backup/list') {
    return ok([
      { filename: 'db-2026-11-11.sql', size: '18.4 MB', createTime: '2026-11-11 02:00:00' },
      { filename: 'db-2026-11-04.sql', size: '17.9 MB', createTime: '2026-11-04 02:00:00' },
      { filename: 'db-2026-10-28.sql', size: '17.2 MB', createTime: '2026-10-28 02:00:00' }
    ])
  }

  /* ---------------- 教师端 ---------------- */
  if (url === '/dashboard/teacher') return ok(db.teacherDashboardStats())
  if (url === '/classroom/today') return ok(db.todaySchedule())
  if (url === '/homework/pending') {
    const list = db.HOMEWORKS.filter(h => h.status === '1')
    return page({ records: list, total: list.length, current: 1, size: 10 })
  }
  if (url === '/exam/ongoing') {
    return ok([
      { id: 8801, title: 'CET4 全真模拟（第 6 套）', participantCount: 26, remainingMinutes: 45 }
    ])
  }

  /* ---------------- 考试（学生端 + 教师端共用 /exam/page） ---------------- */
  if (url === '/exam/page') return page(db.examPage(params))
  if (url === '/exam/start' && method === 'post') return ok(db.examStart(body.examId))
  if (url === '/exam/submit' && method === 'post') return ok(db.examSubmit(body))
  if (url === '/exam/record/page') return page(db.examRecordPage(params))
  if (url.startsWith('/exam/record/') && method === 'get') {
    return ok(db.examRecordDetail(url.split('/').pop()))
  }
  if (/^\/exam\/\d+\/questions$/.test(url)) {
    return ok(db.examQuestions(Number(url.split('/')[2])))
  }
  if (/^\/exam\/\d+\/stats$/.test(url)) {
    const id = Number(url.split('/')[2])
    const exam = db.EXAMS.find(e => e.id === id)
    return ok({
      totalCount: 32,
      participantCount: exam?.participantCount || 0,
      submittedCount: exam?.participantCount || 0,
      avgScore: 76.4,
      maxScore: 94,
      minScore: 51,
      passRate: 82.1
    })
  }
  if (/^\/exam\/\d+$/.test(url) && method === 'get') {
    const detail = db.examDetail(url.split('/').pop())
    return detail ? ok(detail) : ok({})
  }

  /* ---------------- 题库 ---------------- */
  if (url === '/question/page') return page(db.questionPage(params))
  if (url === '/question-category/list') return ok(db.QUESTION_CATEGORIES)
  if (url === '/question-category/tree') return ok(db.QUESTION_CATEGORIES)
  if (/^\/question\/\d+$/.test(url) && method === 'get') {
    const q = db.QUESTION_BANK.find(x => x.id === Number(url.split('/').pop()))
    return ok(q || {})
  }

  /* ---------------- 学习资源 ---------------- */
  if (url === '/learning/resource/page') return page(db.resourcePage(params))
  if (/^\/learning\/resource\/\d+$/.test(url) && method === 'get') {
    const r = db.RESOURCES.find(x => x.id === Number(url.split('/').pop()))
    return ok(r || {})
  }

  if (url === '/teacher/student/alerts') return ok(db.learningAlerts())
  if (url === '/question/stats') return ok(db.questionStats())
  if (url === '/teacher/score/trend') return ok(db.scoreTrend(params.days || 30))
  if (url === '/teacher/student/page') return page(db.studentPage(params))
  if (url === '/teacher/class/list') return ok(db.CLASSES)
  if (/^\/teacher\/student\/\d+$/.test(url)) {
    const id = Number(url.split('/').pop())
    return ok(db.studentDetail(id))
  }

  /* ---------------- 作业 ---------------- */
  if (url === '/homework/teacher/list') return page(db.homeworkPage(params))
  if (/^\/homework\/\d+\/submissions$/.test(url)) {
    const id = Number(url.split('/')[2])
    return page(db.submissionPage(id, params))
  }
  if (/^\/homework\/\d+$/.test(url) && method === 'get') {
    const id = Number(url.split('/').pop())
    const hw = db.HOMEWORKS.find(h => h.id === id)
    return ok(hw || {})
  }
  if (/^\/homework\/\d+\/stats$/.test(url)) {
    const id = Number(url.split('/')[2])
    const hw = db.HOMEWORKS.find(h => h.id === id)
    return ok({
      totalCount: hw?.totalCount || 32,
      submittedCount: hw?.submittedCount || 0,
      gradedCount: Math.round((hw?.submittedCount || 0) * 0.3),
      avgScore: 76.5
    })
  }
  if (url === '/homework/student/list') return page(db.homeworkPage(params))
  if (url === '/homework/student/submissions') {
    const list = (db.SUBMISSIONS[501] || []).slice(0, 5)
    return ok(list)
  }

  /* ---------------- 学生端 ---------------- */
  if (url === '/dashboard/student') return ok(db.studentDashboard())
  if (url === '/study-plan/page') {
    const records = db.todayPlans()
    return page({ records, total: records.length, current: 1, size: 10 })
  }
  // 必须放在通用 /wrong 分支之前，否则会被错题列表数据覆盖
  if (url === '/wrong-question/statistics') return ok(db.wrongStatistics())
  if (url === '/student/homework') return page(db.homeworkPage(params))
  if (url.includes('/wrong') && method === 'get') {
    return page({ records: db.WRONG_QUESTIONS, total: db.WRONG_QUESTIONS.length, current: 1, size: 20 })
  }
  if (url === '/checkin/record' || url === '/checkin/records') {
    return ok({ checkinDays: 23, continuousDays: 12, todayChecked: false })
  }
  if (url === '/exam/student/list') {
    return page({ records: [
      { id: 8801, title: 'CET4 全真模拟（第 6 套）', totalScore: 710, duration: 130, status: 'NOT_STARTED', startTime: '2026-11-15 09:00:00' },
      { id: 8802, title: '单元词汇测试 Unit 3', totalScore: 100, duration: 30, status: 'FINISHED', score: 82, startTime: '2026-11-08 14:00:00' }
    ], total: 2, current: 1, size: 10 })
  }

  /* ---------------- 兜底 ---------------- */
  // 未覆盖的接口：GET 返回空数组，写操作返回成功，保证页面不崩
  if (method === 'get') {
    return ok([])
  }
  return ok({ success: true })
}

export default resolveMock
