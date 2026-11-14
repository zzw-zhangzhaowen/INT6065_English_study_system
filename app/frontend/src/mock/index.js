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
  if (String(url).includes('classroom')) console.log('[MOCKDBG]', method, url)

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
    const cid = Number(url.split('/').pop())
    const found = db.CLASSROOMS.find(c => c.id === cid) || db.CLASSROOMS[0]
    const signs = db.CLASSROOM_SIGN_INS.filter(s => s.classroomId === cid || cid === 1)
    return ok({
      classroom: { ...found, id: cid, status: 'LIVE', currentStudents: 38, startTime: '2026-11-13 08:00:00' },
      participants: signs.map(s => ({
        id: s.userId, username: s.userName, nickname: s.userName, avatar: '',
        role: 'student', joinTime: s.signInTime
      }))
    })
  }

  /* ---------------- 学习社区 ---------------- */
  if (url === '/community/post/page') return page(db.communityPostPage(params))
  if (/^\/community\/post\/\d+$/.test(url) && method === 'get') {
    const id = Number(url.split('/').pop())
    const p = db.COMMUNITY_POSTS.find(x => x.id === id) || db.COMMUNITY_POSTS[0]
    if (!p) return ok({})
    return ok({
      ...p,
      username: p.authorName || p.author || '同学',
      isCollected: false,
      replies: db.COMMUNITY_REPLIES.filter(r => r.postId === id)
    })
  }
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
  if (url === '/admin/user' && method === 'post') {
    const roleIds = Array.isArray(body.roleIds) ? body.roleIds.map(Number) : []
    const roleCode = roleIds.includes(1) ? 'ROLE_ADMIN' : (roleIds.includes(2) ? 'ROLE_TEACHER' : 'ROLE_STUDENT')
    const role = roleCode === 'ROLE_ADMIN' ? 'admin' : (roleCode === 'ROLE_TEACHER' ? 'teacher' : 'student')
    const now = new Date()
    const pad = n => String(n).padStart(2, '0')
    const created = {
      id: Date.now(),
      username: body.username || `user${Date.now()}`,
      realName: body.realName || body.username || '',
      nickname: body.realName || body.username || '',
      avatar: body.avatar || '',
      email: body.email || '',
      phone: body.phone || '',
      className: '',
      role,
      roleName: role === 'admin' ? '管理员' : (role === 'teacher' ? '教师' : '学生'),
      roles: [roleCode],
      status: 1,
      createTime: `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())} ${pad(now.getHours())}:${pad(now.getMinutes())}:00`,
      lastLogin: ''
    }
    db.ADMIN_USERS.unshift(created)
    return ok(created)
  }
  {
    const m = url.match(/^\/admin\/user\/(\d+)(\/password|\/status)?$/)
    if (m) {
      const id = Number(m[1])
      const sub = m[2] || ''
      const target = db.ADMIN_USERS.find(u => Number(u.id) === id)
      if (!target) return { code: 404, message: '用户不存在', data: null }
      if (sub === '/password') {
        return ok({ success: true, message: '密码已重置' })
      }
      if (sub === '/status') {
        target.status = target.status === 1 ? 0 : 1
        return ok(target)
      }
      if (method === 'get') return ok(target)
      if (method === 'put') {
        const roleIds = Array.isArray(body.roleIds) ? body.roleIds.map(Number) : []
        if (roleIds.length) {
          const roleCode = roleIds.includes(1) ? 'ROLE_ADMIN' : (roleIds.includes(2) ? 'ROLE_TEACHER' : 'ROLE_STUDENT')
          target.role = roleCode === 'ROLE_ADMIN' ? 'admin' : (roleCode === 'ROLE_TEACHER' ? 'teacher' : 'student')
          target.roleName = target.role === 'admin' ? '管理员' : (target.role === 'teacher' ? '教师' : '学生')
          target.roles = [roleCode]
        }
        if (body.realName !== undefined) target.realName = body.realName
        if (body.email !== undefined) target.email = body.email
        if (body.phone !== undefined) target.phone = body.phone
        if (body.avatar !== undefined) target.avatar = body.avatar
        return ok(target)
      }
      if (method === 'delete') {
        const idx = db.ADMIN_USERS.indexOf(target)
        if (idx >= 0) db.ADMIN_USERS.splice(idx, 1)
        return ok({ success: true })
      }
    }
  }
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
    const record = db.examRecordDetail(url.split('/').pop())
    const exam = db.EXAMS.find(e => e.id === record.examId) || db.EXAMS[0] || {}
    const questions = db.examQuestions(record.examId || 8802).slice(0, 8)
    const answerDetails = questions.map((q, i) => ({
      questionId: q.id,
      questionScore: q.score || 5,
      question: { id: q.id, content: q.content, questionType: q.questionType || 'SINGLE_CHOICE', score: q.score || 5 },
      answerRecord: { id: 9500 + i, isCorrect: i < 6 ? 1 : 0, obtainedScore: i < 6 ? (q.score || 5) : 0 }
    }))
    return ok({
      record,
      exam: { id: exam.id || record.examId, examName: exam.title || exam.examName || record.examName, totalScore: record.totalScore || 100 },
      answerDetails,
      rank: record.rank || 7,
      percentile: record.percentile || 78,
      wrongQuestions: questions.slice(6).map((q, i) => ({ id: 9600 + i, content: q.content, questionType: q.questionType }))
    })
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
      passRate: 82.1,
      scoreDistribution: [
        { range: '0-59', count: 2 }, { range: '60-69', count: 5 },
        { range: '70-79', count: 11 }, { range: '80-89', count: 9 },
        { range: '90-100', count: 5 }
      ]
    })
  }
  if (/^\/exam\/\d+\/records$/.test(url) && method === 'get') {
    const names = ['张伟', '李思远', '陈欣怡', '王梓涵', '赵明轩', '周雨桐', '刘子墨', '孙嘉懿']
    return ok(names.map((name, i) => ({
      id: 9200 + i, rank: i + 1, realName: name, studentNo: `2024010${101 + i}`,
      obtainedScore: 94 - i * 3, totalScore: 100,
      correctCount: 14 - Math.floor(i / 2), wrongCount: Math.floor(i / 2),
      duration: 28 + i * 3, submitTime: `2026-11-13 09:${String(12 + i * 2).padStart(2, '0')}:00`,
      status: 'FINISHED'
    })))
  }
  if (/^\/exam\/\d+\/question-analysis$/.test(url) && method === 'get') {
    const qs = db.examQuestions(Number(url.split('/')[2]))
    return ok(qs.map((q, i) => ({
      questionId: q.id, questionOrder: i + 1,
      title: (q.content || '').slice(0, 40) || `第 ${i + 1} 题`,
      questionType: q.questionType || 'SINGLE_CHOICE',
      questionScore: q.score || 5,
      totalAnswers: 32,
      correctAnswers: Math.max(8, 30 - i * 2),
      accuracy: Math.max(28, 94 - i * 6)
    })))
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

  /* ============================================================
     补充路由 · 课堂互动 / 学习中心 / 消息 / 系统管理 / 打卡 / 备份
     ============================================================ */
  const paged = (records, params = {}) => ({
    records,
    total: records.length,
    current: Number(params.pageNum || params.current || 1),
    size: Number(params.pageSize || params.size || 10)
  })

  /* ---------------- 课堂 ---------------- */
  if (url === '/classroom') {
    if (method === 'post') {
      const created = {
        id: Date.now(), classroomName: body.classroomName || '新建课堂',
        description: body.description || '', teacherName: '王老师',
        status: 'READY', startTime: body.startTime || '', currentStudents: 0, coverImage: ''
      }
      db.CLASSROOMS.unshift(created)
      return ok(created)
    }
  }
  {
    const m = url.match(/^\/classroom\/(\d+)(\/[a-z-]+)?(\/[a-z-]+)?(\/[a-z-]+)?$/)
    if (m) {
      const id = Number(m[1])
      const s1 = m[2] || ''
      const s2 = m[3] || ''
      const s3 = m[4] || ''
      const found = db.CLASSROOMS.find(c => c.id === id) || db.CLASSROOMS[0]
      const base = { ...found, id }

      // 课堂详情
      if (!s1 && method === 'get') {
        const signs = db.CLASSROOM_SIGN_INS.filter(s => s.classroomId === id || id === 1)
        return ok({
          classroom: { ...base, status: 'LIVE', currentStudents: 38, startTime: '2026-11-13 08:00:00' },
          participants: signs.map(s => ({
            id: s.userId, username: s.userName, nickname: s.userName, avatar: '',
            role: 'student', joinTime: s.signInTime
          }))
        })
      }
      if (!s1 && method === 'put') {
        Object.assign(base, body)
        return ok(base)
      }
      if (!s1 && method === 'delete') {
        const idx = db.CLASSROOMS.indexOf(found)
        if (idx >= 0) db.CLASSROOMS.splice(idx, 1)
        return ok({ success: true })
      }

      // 课堂消息
      if (s1 === '/messages' && method === 'get') {
        const msgs = db.CLASSROOM_MESSAGES.filter(m2 => m2.classroomId === id || id === 1)
        return page(paged(msgs, params))
      }
      // 签到
      if (s1 === '/sign' && s2 === '/list' && method === 'get') {
        return ok(db.CLASSROOM_SIGN_INS.filter(s => s.classroomId === id || id === 1))
      }
      if (s1 === '/sign' && s2 === '/status' && method === 'get') {
        return ok({ activated: true, total: 32, signedCount: 28, status: 'ACTIVATED' })
      }
      if (s1 === '/sign' && s2 === '/activate' && method === 'post') {
        return ok({ success: true, activated: true })
      }
      if (s1 === '/sign' && method === 'post') {
        return ok({ success: true, signInTime: new Date().toISOString() })
      }
      // 生命周期
      if (s1 === '/start' && method === 'post') return ok({ ...base, status: 'LIVE' })
      if (s1 === '/end' && method === 'post') return ok({ ...base, status: 'ENDED' })
      if (s1 === '/join' && method === 'post') return ok({ success: true, joinTime: new Date().toISOString() })
      if (s1 === '/leave' && method === 'post') return ok({ success: true })
      if (s1 === '/record-url' && method === 'post') return ok({ success: true, recordUrl: body.recordUrl || '' })

      // 问答
      if (s1 === '/questions') {
        const list = db.CLASSROOM_QUESTIONS.filter(q => q.classroomId === id || id === 1)
        if (method === 'get') return ok(list)
        if (method === 'post') {
          const created = {
            id: Date.now(), classroomId: id, username: '我', avatar: '',
            content: body.content || '', status: 'OPEN', likeCount: 0, answerCount: 0,
            createTime: new Date().toISOString(), answers: []
          }
          db.CLASSROOM_QUESTIONS.unshift(created)
          return ok(created)
        }
      }
      {
        const qm = url.match(/^\/classroom\/(\d+)\/questions\/(\d+)(\/[a-z-]+)?$/)
        if (qm) {
          const q = db.CLASSROOM_QUESTIONS.find(x => x.id === Number(qm[2])) || db.CLASSROOM_QUESTIONS[0]
          const sub = qm[3] || ''
          if (sub === '/answers') {
            if (method === 'get') return ok(q.answers || [])
            if (method === 'post') {
              const ans = {
                id: Date.now(), username: '我', avatar: '', content: body.content || '',
                isBest: false, createTime: new Date().toISOString()
              }
              if (!q.answers) q.answers = []
              q.answers.push(ans)
              q.answerCount = (q.answerCount || 0) + 1
              if (q.status === 'OPEN') q.status = 'ANSWERED'
              return ok(ans)
            }
          }
          if (sub === '/like' && method === 'post') {
            q.likeCount = (q.likeCount || 0) + 1
            return ok(q)
          }
          if (sub === '/close' && method === 'post') {
            q.status = 'CLOSED'
            return ok(q)
          }
        }
        const am = url.match(/^\/classroom\/(\d+)\/answers\/(\d+)\/best$/)
        if (am && method === 'post') {
          const q = db.CLASSROOM_QUESTIONS[0]
          if (q && q.answers) q.answers.forEach(a => { a.isBest = a.id === Number(am[2]) })
          return ok({ success: true })
        }
      }

      // 投票
      if (s1 === '/polls') {
        const list = db.CLASSROOM_POLLS.filter(p => p.classroomId === id || id === 1)
        if (!s2 && method === 'get') return ok(list)
        if (!s2 && method === 'post') {
          const created = {
            id: Date.now(), classroomId: id, title: body.title || '',
            pollType: body.pollType || 'SINGLE', anonymous: !!body.anonymous,
            status: 'ACTIVE', totalVotes: 0, createTime: new Date().toISOString(),
            options: (body.options || []).map((c, i) => ({ id: i + 1, content: c, voteCount: 0 }))
          }
          db.CLASSROOM_POLLS.unshift(created)
          return ok(created)
        }
      }
      {
        const pm = url.match(/^\/classroom\/(\d+)\/polls\/(\d+)(\/[a-z-]+)?$/)
        if (pm) {
          const p = db.CLASSROOM_POLLS.find(x => x.id === Number(pm[2])) || db.CLASSROOM_POLLS[0]
          const sub = pm[3] || ''
          if (!sub && method === 'get') return ok(p)
          if (sub === '/vote' && method === 'post') {
            const ids = Array.isArray(body.optionIds) ? body.optionIds : []
            p.totalVotes = (p.totalVotes || 0) + ids.length
            p.options?.forEach(o => { if (ids.includes(o.id)) o.voteCount = (o.voteCount || 0) + 1 })
            return ok(p)
          }
          if (sub === '/close' && method === 'post') {
            p.status = 'CLOSED'
            return ok(p)
          }
        }
      }
    }
  }

  /* ---------------- 学习资源 ---------------- */
  if (url === '/resource-category/tree') return ok(db.RESOURCE_CATEGORIES)
  if (url === '/resource-category/list') return ok(db.RESOURCE_CATEGORIES)
  if (url === '/resource-category' && method === 'post') {
    const created = { id: Date.now(), categoryName: body.categoryName || '新分类', parentId: 0, level: 1, resourceCount: 0 }
    db.RESOURCE_CATEGORIES.push(created)
    return ok(created)
  }
  {
    const m = url.match(/^\/resource-category\/(\d+)$/)
    if (m) {
      const c = db.RESOURCE_CATEGORIES.find(x => x.id === Number(m[1]))
      if (method === 'put') { if (c) Object.assign(c, body); return ok(c || {}) }
      if (method === 'delete') {
        const i = db.RESOURCE_CATEGORIES.indexOf(c)
        if (i >= 0) db.RESOURCE_CATEGORIES.splice(i, 1)
        return ok({ success: true })
      }
    }
  }
  if (url === '/learning/resource' && method === 'post') {
    const created = {
      id: Date.now(), resourceName: body.resourceName || body.name || '新建资源',
      description: body.description || '', resourceType: body.resourceType || 'DOCUMENT',
      level: body.level || 'CET-4', coverUrl: '', viewCount: 0, downloadCount: 0,
      duration: '', updateTime: new Date().toISOString().slice(0, 19).replace('T', ' ')
    }
    db.RESOURCES.unshift(created)
    return ok(created)
  }
  if (url === '/learning/resource/batch' && method === 'post') {
    return ok({ success: true, affected: (body.ids || []).length })
  }

  /* ---------------- 学习笔记 ---------------- */
  if (url === '/study-note/page') {
    let list = [...db.STUDY_NOTES]
    if (params.keyword) {
      const k = String(params.keyword)
      list = list.filter(n => n.title.includes(k) || n.content.includes(k))
    }
    return page(paged(list, params))
  }
  if (url === '/study-note' && method === 'post') {
    const created = {
      id: Date.now(), title: body.title || '未命名笔记', content: body.content || '',
      type: body.type || 'NOTE', tags: body.tags || [], userId: 1001, userName: '张伟',
      createTime: new Date().toISOString().slice(0, 19).replace('T', ' '),
      updateTime: new Date().toISOString().slice(0, 19).replace('T', ' '), viewCount: 0
    }
    db.STUDY_NOTES.unshift(created)
    return ok(created)
  }
  {
    const m = url.match(/^\/study-note\/(\d+)$/)
    if (m) {
      const n = db.STUDY_NOTES.find(x => x.id === Number(m[1])) || db.STUDY_NOTES[0]
      if (method === 'get') return ok(n)
      if (method === 'put') { Object.assign(n, body, { updateTime: new Date().toISOString().slice(0, 19).replace('T', ' ') }); return ok(n) }
      if (method === 'delete') {
        const i = db.STUDY_NOTES.indexOf(n)
        if (i >= 0) db.STUDY_NOTES.splice(i, 1)
        return ok({ success: true })
      }
    }
  }

  /* ---------------- 学习计划 / 任务 ---------------- */
  if (url === '/study-plan' && method === 'post') {
    const created = {
      id: Date.now(), planName: body.planName || '新建计划', goal: body.goal || '',
      startDate: body.startDate || '', endDate: body.endDate || '',
      status: 'IN_PROGRESS', totalTasks: 0, completedTasks: 0, userId: 1001,
      createTime: new Date().toISOString().slice(0, 19).replace('T', ' ')
    }
    db.STUDY_PLANS.unshift(created)
    return ok(created)
  }
  {
    const m = url.match(/^\/study-plan\/(\d+)\/progress$/)
    if (m && method === 'put') {
      const p = db.STUDY_PLANS.find(x => x.id === Number(m[1]))
      if (p) Object.assign(p, body)
      return ok(p || {})
    }
  }
  if (url === '/study-task/page') return page(paged(db.STUDY_TASKS, params))
  if (url === '/study-task/statistics') {
    return ok({ total: db.STUDY_TASKS.length, completed: db.STUDY_TASKS.filter(t => t.status === 'COMPLETED').length, inProgress: db.STUDY_TASKS.filter(t => t.status === 'IN_PROGRESS').length, pending: db.STUDY_TASKS.filter(t => t.status === 'PENDING').length, completionRate: 43 })
  }
  if (url === '/study-task' && method === 'post') {
    const created = {
      id: Date.now(), title: body.title || '新建任务', type: body.type || 'STUDY',
      status: 'PENDING', progress: 0, deadline: body.deadline || '', planId: body.planId || null,
      userId: 1001, createTime: new Date().toISOString().slice(0, 19).replace('T', ' ')
    }
    db.STUDY_TASKS.unshift(created)
    return ok(created)
  }
  {
    const pm = url.match(/^\/study-task\/plan\/(\d+)$/)
    if (pm && method === 'get') {
      const list = db.STUDY_TASKS.filter(t => t.planId === Number(pm[1]))
      return ok(list.length ? list : db.STUDY_TASKS.slice(0, 5))
    }
    const m = url.match(/^\/study-task\/(\d+)(\/toggle)?$/)
    if (m) {
      const t = db.STUDY_TASKS.find(x => x.id === Number(m[1])) || db.STUDY_TASKS[0]
      if (m[2] === '/toggle' && method === 'put') {
        t.status = t.status === 'COMPLETED' ? 'PENDING' : 'COMPLETED'
        t.progress = t.status === 'COMPLETED' ? 100 : t.progress
        return ok(t)
      }
      if (!m[2] && method === 'get') return ok(t)
      if (!m[2] && method === 'put') { Object.assign(t, body); return ok(t) }
      if (!m[2] && method === 'delete') {
        const i = db.STUDY_TASKS.indexOf(t)
        if (i >= 0) db.STUDY_TASKS.splice(i, 1)
        return ok({ success: true })
      }
    }
  }
  if (url === '/dashboard/trend' && method === 'get') {
    const days = Array.from({ length: 14 }, (_, i) => {
      const d = new Date(2026, 10, 1 + i)
      const pad = n => String(n).padStart(2, '0')
      return {
        date: `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`,
        studyMinutes: 35 + ((i * 17) % 55),
        questionCount: 20 + ((i * 13) % 45),
        correctRate: 68 + ((i * 5) % 22),
        words: 15 + ((i * 9) % 40)
      }
    })
    return ok(days)
  }

  /* ---------------- 文件上传 ---------------- */
  if (url.startsWith('/file/upload')) {
    const name = body?.filename || `upload_${Date.now()}.png`
    return ok({
      success: true,
      url: `/upload/${name}`,
      filename: name,
      size: body?.size || 0,
      originalFilename: name
    })
  }

  /* ---------------- 站内消息 ---------------- */
  if (url === '/message/inbox') {
    let list = db.MESSAGES.filter(m => m.receiverId === 1001 || m.type === 'SYSTEM')
    if (params.keyword) {
      const k = String(params.keyword)
      list = list.filter(m => m.title.includes(k) || m.content.includes(k))
    }
    return page(paged(list, params))
  }
  if (url === '/message/outbox') {
    return page(paged(db.MESSAGES.filter(m => m.senderId === 2001), params))
  }
  if (url === '/message/unread/count') {
    return ok(db.MESSAGES.filter(m => m.receiverId === 1001 && m.isRead === 0).length)
  }
  if (url === '/message/read/batch' && method === 'put') {
    db.MESSAGES.forEach(m => { if (m.receiverId === 1001) m.isRead = 1 })
    return ok({ success: true })
  }
  if (url === '/message/broadcast' && method === 'post') {
    const created = {
      id: Date.now(), title: body.title || '系统通知', content: body.content || '',
      type: 'NOTICE', senderId: 2001, senderName: '王老师',
      receiverId: 0, receiverName: '全体学生', isRead: 0,
      createTime: new Date().toISOString().slice(0, 19).replace('T', ' ')
    }
    db.MESSAGES.unshift(created)
    return ok(created)
  }
  {
    const m = url.match(/^\/message\/send\/(\d+)$/)
    if (m && method === 'post') {
      const created = {
        id: Date.now(), title: body.title || '私信', content: body.content || '',
        type: body.type || 'NOTICE', senderId: 2001, senderName: '王老师',
        receiverId: Number(m[1]), receiverName: '学生', isRead: 0,
        createTime: new Date().toISOString().slice(0, 19).replace('T', ' ')
      }
      db.MESSAGES.unshift(created)
      return ok(created)
    }
    const m2 = url.match(/^\/message\/(\d+)(\/read)?$/)
    if (m2) {
      const msg = db.MESSAGES.find(x => x.id === Number(m2[1])) || db.MESSAGES[0]
      if (m2[2] === '/read' && method === 'put') { msg.isRead = 1; return ok(msg) }
      if (!m2[2] && method === 'get') return ok(msg)
      if (!m2[2] && method === 'delete') {
        const i = db.MESSAGES.indexOf(msg)
        if (i >= 0) db.MESSAGES.splice(i, 1)
        return ok({ success: true })
      }
    }
  }

  /* ---------------- 系统图片 ---------------- */
  if (url === '/photo/list') return ok(db.PHOTOS)
  if (url === '/photo/upload' && method === 'post') {
    const created = { filename: body?.filename || `photo_${Date.now()}.png`, url: `/photo/${body?.filename || 'photo.png'}`, size: body?.size || 0 }
    db.PHOTOS.push(created)
    return ok(created)
  }
  if (url === '/photo/replace' && method === 'post') {
    const old = params.oldFilename
    const i = db.PHOTOS.findIndex(p => p.filename === old)
    if (i >= 0) db.PHOTOS[i] = { ...db.PHOTOS[i], filename: body?.filename || db.PHOTOS[i].filename, size: body?.size || db.PHOTOS[i].size }
    return ok(db.PHOTOS[i] || {})
  }
  if (url === '/photo/delete' && method === 'delete') {
    const i = db.PHOTOS.findIndex(p => p.filename === params.filename)
    if (i >= 0) db.PHOTOS.splice(i, 1)
    return ok({ success: true })
  }

  /* ---------------- 每日打卡 ---------------- */
  if (url === '/checkin' && method === 'post') {
    const now = new Date()
    const pad = n => String(n).padStart(2, '0')
    const created = {
      id: Date.now(), userId: 1001, userName: '张伟',
      checkinDate: `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())}`,
      checkinTime: `${pad(now.getHours())}:${pad(now.getMinutes())}:${pad(now.getSeconds())}`,
      duration: 30, content: body.content || '今日学习打卡', continuousDays: 6
    }
    db.CHECKIN_LOGS.unshift(created)
    return ok({ success: true, message: `打卡成功，已连续打卡 ${created.continuousDays} 天`, continuousDays: 6, checkinDays: db.CHECKIN_LOGS.length })
  }
  if (url === '/checkin/today') {
    const now = new Date()
    const pad = n => String(n).padStart(2, '0')
    const todayStr = `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())}`
    const hit = db.CHECKIN_LOGS.find(c => c.checkinDate === todayStr)
    return ok({
      hasCheckedIn: !!hit,
      checked: !!hit,
      checkinTime: hit ? hit.checkinTime : null,
      continuousDays: hit ? hit.continuousDays : 0
    })
  }
  if (url === '/checkin/calendar') {
    const year = Number(params.year) || new Date().getFullYear()
    const month = Number(params.month) || new Date().getMonth() + 1
    const daysInMonth = new Date(year, month, 0).getDate()
    const pad = n => String(n).padStart(2, '0')
    const days = []
    for (let i = 1; i <= daysInMonth; i++) {
      const dateStr = `${year}-${pad(month)}-${pad(i)}`
      const hit = db.CHECKIN_LOGS.find(c => c.checkinDate === dateStr)
      days.push({
        date: dateStr,
        checkinDate: dateStr,
        checked: !!hit,
        checkinTime: hit ? hit.checkinTime : null
      })
    }
    return ok(days)
  }
  if (url === '/checkin/stats') {
    const logged = db.CHECKIN_LOGS.length
    return ok({
      totalDays: 23,
      continuousDays: 12,
      monthDays: 16,
      thisMonth: 16,
      totalMinutes: 780,
      longestStreak: 18,
      loggedRecords: logged
    })
  }
  if (url === '/checkin/ranking') {
    const list = db.CHECKIN_LOGS.slice(0, 10).map((c, i) => ({
      rank: i + 1,
      userId: c.userId,
      nickname: c.userName,
      userName: c.userName,
      totalDays: 23 - i,
      continuousDays: 12 - i,
      checkinDays: 23 - i,
      totalMinutes: 780 - i * 40
    }))
    return ok(list)
  }
  if (url === '/checkin/study-data' && method === 'put') return ok({ success: true })

  /* ---------------- 备份管理 ---------------- */
  if (url === '/backup/list') return page(paged(db.BACKUPS, params))
  if (url === '/backup' && method === 'post') {
    const now = new Date()
    const pad = n => String(n).padStart(2, '0')
    const stamp = `${now.getFullYear()}${pad(now.getMonth() + 1)}${pad(now.getDate())}_${pad(now.getHours())}${pad(now.getMinutes())}${pad(now.getSeconds())}`
    const created = {
      id: Date.now(), filename: `backup_${stamp}.zip`, size: 41943040,
      type: 'MANUAL', status: 'SUCCESS',
      createTime: `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())} ${pad(now.getHours())}:${pad(now.getMinutes())}:00`,
      duration: 45
    }
    db.BACKUPS.unshift(created)
    return ok(created)
  }
  {
    const m = url.match(/^\/backup\/restore\/(.+)$/)
    if (m && method === 'post') return ok({ success: true, filename: m[1], restoredAt: new Date().toISOString() })
    const d = url.match(/^\/backup\/download\/(.+)$/)
    if (d && method === 'get') return ok({ success: true, filename: d[1], url: `/backup/${d[1]}` })
    const del = url.match(/^\/backup\/(.+)$/)
    if (del && method === 'delete') {
      const i = db.BACKUPS.findIndex(b => b.filename === del[1])
      if (i >= 0) db.BACKUPS.splice(i, 1)
      return ok({ success: true })
    }
  }
  if (url === '/backup/upload-restore' && method === 'post') {
    return ok({ success: true, filename: body?.filename || 'uploaded_backup.zip' })
  }

  /* ---------------- 社区互动 ---------------- */
  if (url === '/community/reply' && method === 'post') {
    const created = {
      id: Date.now(), postId: Number(body.postId) || 1, userId: 1001, userName: '张伟',
      content: body.content || '', likeCount: 0, isLiked: false,
      createTime: new Date().toISOString().slice(0, 19).replace('T', ' ')
    }
    db.COMMUNITY_REPLIES.unshift(created)
    return ok(created)
  }
  if (url === '/community/reply/page') {
    let list = [...db.COMMUNITY_REPLIES]
    if (params.postId) list = list.filter(r => r.postId === Number(params.postId))
    return page(paged(list, params))
  }
  {
    const m = url.match(/^\/community\/reply\/(\d+)(\/like)?$/)
    if (m) {
      const r = db.COMMUNITY_REPLIES.find(x => x.id === Number(m[1])) || db.COMMUNITY_REPLIES[0]
      if (m[2] === '/like' && method === 'post') {
        r.likeCount = (r.likeCount || 0) + 1
        r.isLiked = true
        return ok(r)
      }
      if (!m[2] && method === 'delete') {
        const i = db.COMMUNITY_REPLIES.indexOf(r)
        if (i >= 0) db.COMMUNITY_REPLIES.splice(i, 1)
        return ok({ success: true })
      }
    }
  }
  {
    const m = url.match(/^\/user\/(\d+)\/follow$/)
    if (m) {
      const uid = Number(m[1])
      if (method === 'post') {
        if (!db.FOLLOWED_USERS.includes(uid)) db.FOLLOWED_USERS.push(uid)
        return ok({ success: true, following: true, followerCount: db.FOLLOWED_USERS.length })
      }
      if (method === 'delete') {
        const i = db.FOLLOWED_USERS.indexOf(uid)
        if (i >= 0) db.FOLLOWED_USERS.splice(i, 1)
        return ok({ success: true, following: false, followerCount: db.FOLLOWED_USERS.length })
      }
    }
  }

  /* ---------------- 学习小组 ---------------- */
  {
    const m = url.match(/^\/group\/(\d+)\/(join|leave|check)$/)
    if (m) {
      if (m[2] === 'check') return ok({ joined: true, memberCount: 6 })
      if (m[2] === 'join') return ok({ success: true, memberCount: 7 })
      return ok({ success: true, memberCount: 5 })
    }
    const mm = url.match(/^\/group\/(\d+)\/(admin|member)\/(\d+)$/)
    if (mm && method === 'post') return ok({ success: true })
    const tr = url.match(/^\/group\/(\d+)\/transfer\/(\d+)$/)
    if (tr && method === 'post') return ok({ success: true })
    const g = url.match(/^\/group\/(\d+)$/)
    if (g && method === 'get') return ok(db.GROUPS.find(x => x.id === Number(g[1])) || db.GROUPS[0])
  }
  if (url === '/group' && method === 'post') {
    const created = {
      id: Date.now(), groupName: body.groupName || '新建小组', description: body.description || '',
      memberCount: 1, maxMembers: 10, status: 'ACTIVE', createTime: new Date().toISOString().slice(0, 19).replace('T', ' ')
    }
    db.GROUPS.unshift(created)
    return ok(created)
  }

  /* ---------------- 日志清理 ---------------- */
  if (url === '/log/clear' && method === 'delete') {
    db.OPERATION_LOGS.length = 0
    return ok({ success: true, cleared: true })
  }
  if (url === '/log/batch' && method === 'delete') {
    const ids = body.ids || []
    for (let i = db.OPERATION_LOGS.length - 1; i >= 0; i--) {
      if (ids.includes(db.OPERATION_LOGS[i].id)) db.OPERATION_LOGS.splice(i, 1)
    }
    return ok({ success: true, deleted: ids.length })
  }
  {
    const m = url.match(/^\/log\/clean\/(\d+)$/)
    if (m && method === 'delete') return ok({ success: true, cleaned: 12 })
  }
  if (url === '/log/my') return page(paged(db.OPERATION_LOGS.slice(0, 8), params))

  /* ---------------- 创建类（考试 / 作业 / 题目） ---------------- */
  if (url === '/exam' && method === 'post') {
    const created = {
      id: Date.now(), examName: body.examName || '新建考试', description: body.description || '',
      examType: 'CUSTOM', duration: Number(body.duration) || 90, totalScore: 100,
      passScore: 60, questionCount: 0, status: 'NOT_STARTED',
      startTime: body.startTime || '', createdBy: '王老师'
    }
    db.EXAMS.unshift(created)
    return ok(created)
  }
  if (url === '/homework' && method === 'post') {
    const created = {
      id: Date.now(), title: body.title || '新建作业', description: body.description || '',
      homeworkType: body.homeworkType || 'WRITING', totalScore: 100, dueDate: body.dueDate || '',
      status: 'PUBLISHED', createdBy: '王老师', createdAt: new Date().toISOString().slice(0, 19).replace('T', ' ')
    }
    db.HOMEWORKS.unshift(created)
    return ok(created)
  }
  if (url === '/question' && method === 'post') {
    const created = {
      id: Date.now(), content: body.content || '新题目', questionType: body.questionType || 'READING',
      level: body.level || 'CET-4', options: body.options || [], answer: body.answer || '',
      analysis: body.analysis || '', difficulty: body.difficulty || 'MEDIUM', createdAt: new Date().toISOString().slice(0, 19).replace('T', ' ')
    }
    db.QUESTION_BANK.unshift(created)
    return ok(created)
  }
  if (url === '/question-category' && method === 'post') {
    const created = { id: Date.now(), categoryName: body.categoryName || '新分类', parentId: 0, questionCount: 0 }
    db.QUESTION_CATEGORIES.push(created)
    return ok(created)
  }

  /* ---------------- 管理员 / 认证 补充 ---------------- */
  {
    const m = url.match(/^\/admin\/user\/(\d+)\/roles$/)
    if (m && method === 'put') return ok({ success: true, userId: Number(m[1]), roleIds: body.roleIds || [] })
  }
  if (url === '/auth/public-config') {
    return ok({
      systemName: '英语等级智能学习与考试系统',
      version: '2.0.0',
      registerEnabled: true,
      passwordMinLength: 6,
      icp: 'INT6065 教学项目',
      copyright: '© 2026 外国语学院'
    })
  }
  if (url === '/user/password' && method === 'put') return ok({ success: true })
  if (url === '/user/statistics' && method === 'get') {
    return ok({
      studyDays: 23, totalStudyMinutes: 780, questionCount: 486, correctRate: 76,
      essayCount: 8, speakingCount: 12, continuousDays: 12, level: 'CET-4'
    })
  }
  if (url === '/monitor/overview') return ok(db.MONITOR_DATA.overview)
  if (url === '/monitor/realtime') return ok(db.MONITOR_DATA.realtime)
  if (url === '/monitor/history') return ok(db.MONITOR_DATA.realtimeHistory)

  /* ---------------- 兜底 ---------------- */
  // 未覆盖的接口：GET 返回空数组，写操作返回成功，保证页面不崩
  if (method === 'get') {
    return ok([])
  }
  return ok({ success: true })
}

export default resolveMock
