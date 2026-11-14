// ============================================================
// 演示数据库 · 模拟教师端所需的完整业务数据
// 说明：后端未启动时全站自动走这里，保证「打开就能看到东西」
// 数据由稳定种子生成，刷新不会变
// ============================================================

/* ---------------- 班级与学生 ---------------- */
export const CLASSES = [
  { id: 1, name: '2024级英语1班', grade: '2024', major: '英语', count: 32 },
  { id: 2, name: '2023级商务英语2班', grade: '2023', major: '商务英语', count: 28 }
]

const NAMES = [
  '张伟', '李娜', '王芳', '刘洋', '陈静', '杨帆', '赵敏', '黄磊',
  '周涛', '吴倩', '徐鹏', '孙悦', '马超', '朱琳', '胡军', '郭婷',
  '林浩', '何静', '高远', '罗曼', '郑爽', '梁宇', '谢萌', '唐磊',
  '韩雪', '曹阳', '邓超', '冯雨', '曾亮', '彭丹', '蒋文', '董雪'
]

const PINYIN = [
  'zhangwei', 'lina', 'wangfang', 'liuyang', 'chenjing', 'yangfan', 'zhaomin', 'huanglei',
  'zhoutao', 'wuqian', 'xupeng', 'sunyue', 'machao', 'zhulin', 'hujun', 'guoting',
  'linhao', 'hejing', 'gaoyuan', 'luoman', 'zhengshuang', 'liangyu', 'xiemeng', 'tanglei',
  'hanxue', 'caoyang', 'dengchao', 'fengyu', 'zengliang', 'pengdan', 'jiangwen', 'dongxue'
]

// 稳定伪随机
function rnd(seed) {
  let s = seed
  return () => {
    s = (s * 1103515245 + 12345) & 0x7fffffff
    return s / 0x7fffffff
  }
}

export const STUDENTS = NAMES.map((name, i) => {
  const r = rnd(i * 977 + 13)
  const activeOffset = Math.floor(r() * 9)
  return {
    id: 1001 + i,
    username: PINYIN[i],
    realName: name,
    nickname: name,
    className: '2024级英语1班',
    classId: 1,
    level: i % 5 === 0 ? 'CET6' : 'CET4',
    email: `${PINYIN[i]}@stu.edu.cn`,
    phone: `138****${String(1000 + i * 7).slice(-4)}`,
    totalScore: 480 + Math.floor(r() * 180),
    checkinDays: 8 + Math.floor(r() * 40),
    studyHours: Math.round((20 + r() * 90) * 10) / 10,
    lastActiveDays: activeOffset,
    status: 1,
    createTime: `2026-0${(i % 8) + 1}-1${i % 9}T09:00:00`
  }
})

export const STUDENT_MAP = STUDENTS.reduce((acc, s) => {
  acc[s.id] = s
  return acc
}, {})

/* ---------------- 作业 ---------------- */
export const HOMEWORKS = [
  {
    id: 501,
    title: 'Online Learning: Benefits and Challenges',
    description: 'Write a short essay on the advantages and problems of online learning.',
    content: '不少于 120 词，三段式结构，至少使用 4 个不同的连接词。',
    totalScore: 100,
    homeworkType: 'WRITING',
    level: 'CET4',
    deadline: '2026-11-18T23:59:59',
    createTime: '2026-11-10T10:00:00',
    status: '1',
    submittedCount: 24,
    totalCount: 32
  },
  {
    id: 502,
    title: 'Should Students Take Part-time Jobs?',
    description: 'Discuss both sides and give your own view.',
    content: '不少于 120 词，需要给出明确立场。',
    totalScore: 100,
    homeworkType: 'WRITING',
    level: 'CET4',
    deadline: '2026-11-11T23:59:59',
    createTime: '2026-11-03T10:00:00',
    status: '2',
    submittedCount: 20,
    totalCount: 32
  },
  {
    id: 503,
    title: 'Reading in the Digital Age',
    description: 'Is reading still important for college students?',
    content: '不少于 150 词，CET6 难度。',
    totalScore: 100,
    homeworkType: 'WRITING',
    level: 'CET6',
    deadline: '2026-11-25T23:59:59',
    createTime: '2026-11-12T10:00:00',
    status: '1',
    submittedCount: 9,
    totalCount: 32
  },
  {
    id: 504,
    title: 'Campus Morning Exercise Policy',
    description: 'Write an argumentative essay on the compulsory morning exercise policy.',
    content: '不少于 120 词。',
    totalScore: 100,
    homeworkType: 'WRITING',
    level: 'CET4',
    deadline: '2026-10-28T23:59:59',
    createTime: '2026-10-20T10:00:00',
    status: '2',
    submittedCount: 30,
    totalCount: 32
  },
  {
    id: 505,
    title: 'Unit 3 Vocabulary & Grammar Drill',
    description: 'Complete the exercises in Unit 3.',
    content: '选择题 20 道 + 填空 10 道。',
    totalScore: 50,
    homeworkType: 'NORMAL',
    level: 'CET4',
    deadline: '2026-11-20T23:59:59',
    createTime: '2026-11-12T14:00:00',
    status: '1',
    submittedCount: 18,
    totalCount: 32
  },
  {
    id: 506,
    title: 'Group Presentation Preparation',
    description: 'Prepare a 5-minute group presentation.',
    content: '4 人一组，主题自选。',
    totalScore: 100,
    homeworkType: 'SPEAKING',
    level: 'CET4',
    deadline: '2026-11-30T23:59:59',
    createTime: '2026-11-13T09:00:00',
    status: '0',
    submittedCount: 0,
    totalCount: 32
  }
]

/* ---------------- 作文样本（含真实高频错误） ---------------- */
export const ESSAYS = [
  // 0 低分：单段 + 多类硬错
  `Nowadays, more and more student choose to study abroad. I think it is very good for young people. people is easy to find a good job when they have overseas experience. But some student can not afford the cost, so they have to work part time in restaurant. In my opinion, i think the government should give them informations and advices. with the development of society, this problem will be solve by itself. Although it is difficult, but we should try our best to help them.`,

  // 1 中低：主谓一致 + 中式搭配
  `There is many reasons why students feel stressed. First, they have too much homework. Second, their parents very want them to get high score. My roommate he go to library every night and study until twelve o'clock. He said he want to learn more knowledge. I think this is not healthy. we should open the light of our mind and find a balance. Besides, school can not only teach knowledge but also teach how to live.`,

  // 2 中等：结构完整但模板化
  `With the rapid development of technology, online learning become popular among college students. There are several advantages. Firstly, it is convenient, students can study at any time. Secondly, it save money because they do not need to rent a house near school. However, there are also some problems. Some students can not focus on study when they learn at home. In my opinion, online learning is a good supplement but it can not replace the traditional class.`,

  // 3 中上：逻辑清晰，少量错误
  `Many undergraduates now take part-time jobs while studying. This trend has both benefits and drawbacks. On the one hand, a part-time job helps students acquire practical experience that textbooks cannot provide. For instance, working in a marketing team teaches them how to communicate with clients. On the other hand, excessive working hours may undermine academic performance. Some students skip classes to meet their work schedules, which gradually weakens their foundation. In my view, the key lies in balance. Students should limit their working hours to no more than fifteen per week, and give priority to academic tasks during exam periods.`,

  // 4 高分：论证具体，连接词丰富
  `The debate over whether university students should be required to attend morning exercises has drawn considerable attention. Those who support the policy argue that regular exercise improves concentration and reduces anxiety, both of which are crucial for academic performance. However, critics maintain that forcing students to exercise at a fixed hour is counterproductive, since sleep deprivation is a more serious problem than lack of movement. From my perspective, the goal is reasonable but the method is questionable. A more effective approach would be to offer flexible exercise options throughout the day and to provide well-maintained facilities. Only when students perceive the arrangement as helpful rather than compulsory will it produce lasting benefits.`,

  // 5 中等：两段，中式表达
  `Nowadays, alot of people think that reading is not important. According to me, this idea is wrong. Reading can help us to learn knowledge and widen our eyes. When we read a book, we can know different culture and different people. there is many books that can change our life. For example, my teacher he go to read every morning before class. He said reading make him calm. So I think everyone should read more books, not only for exam but also for our life.`,

  // 6 低分：过短
  `i think students should not play computer games. it is waste of time. some student play games all night and then sleep in class. this is very bad. school should stop them.`,

  // 7 高分：句式与词汇层次好
  `Collaboration has become an indispensable part of modern education. Group projects, once regarded as a convenient way to reduce marking, are now valued for the competencies they cultivate. Working in a team forces students to articulate their ideas clearly, negotiate differences, and meet deadlines that depend on others. These are precisely the skills employers expect. Nevertheless, collaboration can fail when responsibilities are unclear. Without explicit role allocation, one or two members usually carry the workload while the rest contribute little. Teachers therefore need to design tasks with individual accountability, for example by requiring each member to submit a short reflection. In short, group work is valuable not because it is easier, but because it is harder.`,

  // 8 高分：切题（在线学习），三段 + 连接词
  `Online learning has moved from an emergency measure to a routine option for college students. Its advantages are evident. To begin with, it removes geographical barriers: a student in a small city can attend lectures delivered by leading scholars. Furthermore, recorded sessions allow learners to revisit difficult points at their own pace, which is almost impossible in a crowded classroom. The challenges, however, are equally real. Without external structure, many students postpone tasks until the deadline, and the absence of face-to-face discussion weakens their ability to defend an argument. In my view, online learning works best as a complement rather than a replacement. A sensible arrangement would combine recorded lectures with weekly seminars, so that flexibility is preserved without sacrificing interaction.`,

  // 9 中等：切题（在线学习），两段，少量错误
  `Online learning become more and more popular in recent years. There is many advantages for students. Firstly, they can study at home and save the time of going to school. Secondly, they can watch the video again when they do not understand. However, some student can not control themselves. They often open the video but play phone at the same time. In my opinion, online learning is useful, but students should make a plan and follow it strictly.`,

  // 10 中上：切题（数字时代阅读），论证具体
  `Some observers argue that reading has lost its value in an age of short videos. The evidence, however, points elsewhere. Reading remains the most efficient way to acquire structured knowledge, because a book forces the reader to follow an argument from premise to conclusion rather than receiving isolated fragments. It also trains sustained attention, a capacity that fragmented media actively erode. What has changed is the format, not the need. Students now read on screens, skim abstracts and discuss passages in group chats. The challenge for teachers is therefore to guide selection rather than to defend a particular medium. In short, reading is not obsolete; it has simply become a skill that requires deliberate practice.`,

  // 11 高分：切题（在线学习的教师角色），角度独特
  `Much of the discussion about online learning focuses on platforms and bandwidth, yet the decisive factor is still the teacher. A recorded lecture can deliver content, but it cannot notice that half the class is lost at the third slide. What effective online courses do instead is restructure the teacher's role: less time on one-way explanation, more time on designing tasks, reviewing drafts and responding to individual confusion. This shift is uncomfortable, because it demands that instructors give up the script they have rehearsed for years. The gain, however, is substantial. When explanations are recorded once and watched repeatedly, classroom hours can be spent on argument and feedback, which is precisely where students fail on their own. Online learning therefore does not diminish teaching; it relocates it.`,

  // 12 高分：切题（在线学习的自律问题），论证集中
  `The central problem with online learning is not technology but attention. In a classroom, attention is maintained by external structure: a fixed timetable, a visible instructor and the mild pressure of being observed. Remove those cues and the responsibility shifts to the learner, who is usually unprepared for it. Students describe this honestly. They open a lecture, answer a message, and discover forty minutes later that the argument has moved on without them. Solutions that merely add surveillance miss the point. What helps is a clearer contract: short units with defined outcomes, weekly submissions that cannot be postponed, and discussion groups small enough that silence is noticed. Autonomy is valuable, but it has to be taught rather than assumed.`
]

// 每份作业配一组主题相近的作文，避免「全部跑题」导致评分失真
const ESSAY_POOLS = {
  501: [8, 11, 9, 2, 12, 9, 0],
  502: [3, 3, 4, 1, 3, 9],
  503: [5, 10, 5, 7, 10],
  504: [4, 4, 7, 3, 4],
  505: [1, 2],
  506: [0, 1]
}

const OPENINGS = [
  'This question often comes up in our campus discussions. ',
  'Last week our teacher asked us to discuss this topic in class. ',
  'In recently years, this question has been discussed by many people. ',
  'When i first came to university, i did not think about this question. ',
  'There is a phenomenon that more and more students pay attention to this issue. ',
  'Personally, I have thought about this topic for quite a long time. ',
  'As we all know, this is a hot topic among student. ',
  'Few issues are discussed as frequently as this one among undergraduates. '
]

function buildContent(studentIndex, homeworkIndex) {
  const hwId = HOMEWORKS[homeworkIndex]?.id
  const pool = ESSAY_POOLS[hwId] || [0, 1, 2]
  const essay = ESSAYS[pool[studentIndex % pool.length]]
  const opening = OPENINGS[(studentIndex * 5 + homeworkIndex * 3) % OPENINGS.length]
  return opening + essay
}

/* ---------------- 提交记录 ---------------- */
const SUBMIT_COUNT = { 501: 24, 502: 20, 503: 9, 504: 30, 505: 18, 506: 0 }

function buildSubmissions() {
  const map = {}
  HOMEWORKS.forEach((hw, hi) => {
    const n = SUBMIT_COUNT[hw.id] || 0
    const list = []
    for (let i = 0; i < n; i++) {
      const stu = STUDENTS[i]
      const daysAgo = (i % 5) + 1
      const d = new Date(Date.UTC(2026, 10, 13 - daysAgo, 9 + (i % 12), (i * 7) % 60))
      list.push({
        id: hw.id * 1000 + i,
        homeworkId: hw.id,
        studentId: stu.id,
        studentName: stu.realName,
        content: buildContent(i, hi),
        submitTime: d.toISOString().slice(0, 19).replace('T', ' '),
        status: i % 7 === 0 ? 'GRADED' : 'SUBMITTED',
        score: i % 7 === 0 ? 70 + ((i * 3) % 25) : null,
        teacherComment: i % 7 === 0 ? '结构完整，注意主谓一致。' : ''
      })
    }
    map[hw.id] = list
  })
  return map
}

export const SUBMISSIONS = buildSubmissions()

/* ---------------- 模拟 API 返回 ---------------- */
export function teacherDashboardStats() {
  return {
    totalStudents: STUDENTS.length,
    totalExams: 8,
    totalQuestions: 1240,
    avgPassRate: 86.4,
    todaySubmissions: 24,
    pendingGrade: 42,
    weekNewStudents: 3,
    activeStudents: 27
  }
}

export function todaySchedule() {
  return [
    { id: 9001, startTime: '08:00', endTime: '09:40', title: '大学英语精读（Unit 5）', className: '2024级英语1班', studentCount: 32 },
    { id: 9002, startTime: '10:00', endTime: '11:40', title: '四级写作专项训练', className: '2024级英语1班', studentCount: 32 },
    { id: 9003, startTime: '14:00', endTime: '15:40', title: '听力强化（短对话）', className: '2023级商务英语2班', studentCount: 28 }
  ]
}

export function learningAlerts() {
  const arr = STUDENTS.slice(0, 5).map((s, i) => ({
    id: s.id,
    realName: s.realName,
    username: s.username,
    message: [
      '连续 5 天未打卡，学习中断',
      '近两次作业均未提交',
      '作文语言准确度连续低于 60 分',
      '本周学习时长仅 1.2 小时',
      '错题重做正确率 38%，知识点未掌握'
    ][i],
    alertReason: '学习异常',
    level: i < 2 ? 'high' : 'mid'
  }))
  return arr
}

export function scoreTrend(days = 30) {
  const dates = []
  const avgScores = []
  const examCounts = []
  const r = rnd(20261113)
  for (let i = Math.min(days, 30); i > 0; i -= Math.max(1, Math.round(days / 10))) {
    const d = new Date(Date.UTC(2026, 10, 13 - i))
    dates.push(`${d.getMonth() + 1}/${d.getDate()}`)
    avgScores.push(Math.round(620 + r() * 70) / 10)
    examCounts.push(10 + Math.floor(r() * 26))
  }
  return { dates, avgScores, examCounts }
}

export function questionStats() {
  return {
    total: 1240,
    typeStats: {
      SINGLE_CHOICE: 520,
      MULTIPLE_CHOICE: 110,
      FILL_BLANK: 180,
      TRUE_FALSE: 90,
      READING: 210,
      LISTENING: 100,
      WRITING: 30
    },
    difficultyStats: { EASY: 420, MEDIUM: 610, HARD: 210 }
  }
}

export function studentPage({ pageNum = 1, pageSize = 10, keyword = '' } = {}) {
  let list = STUDENTS
  if (keyword) {
    list = list.filter(s => s.realName.includes(keyword) || s.username.includes(keyword))
  }
  const start = (pageNum - 1) * pageSize
  return {
    records: list.slice(start, start + pageSize),
    total: list.length,
    current: Number(pageNum),
    size: Number(pageSize)
  }
}

export function studentDetail(id) {
  const sid = Number(id) || 1001
  const s = STUDENT_MAP[sid] || STUDENTS[0]
  const r = rnd(sid)
  const totalExams = 4 + Math.floor(r() * 6)
  const avgScore = 60 + Math.floor(r() * 32)
  const recentExams = [
    { id: sid * 10 + 1, examName: 'CET4 全真模拟（第 6 套）', submitTime: '2026-11-12 20:41:12', score: 82, totalScore: 100, duration: 126, status: 'COMPLETED' },
    { id: sid * 10 + 2, examName: '单元词汇测试 Unit 3', submitTime: '2026-11-08 15:02:33', score: 76, totalScore: 100, duration: 28, status: 'COMPLETED' },
    { id: sid * 10 + 3, examName: '阅读专项训练（篇章）', submitTime: '2026-11-03 19:28:04', score: 71, totalScore: 100, duration: 42, status: 'COMPLETED' },
    { id: sid * 10 + 4, examName: 'CET4 期末模拟卷（A 卷）', submitTime: '2026-10-27 10:15:47', score: 68, totalScore: 100, duration: 121, status: 'COMPLETED' }
  ]
  return {
    ...s,
    studentNo: `2024${String(sid).padStart(6, '0')}`,
    grade: '2024级',
    currentLevel: s.level === 'CET6' ? 'CET-6' : 'CET-4',
    targetLevel: s.level === 'CET6' ? 'CET-6' : 'CET-4',
    studyDays: 8 + Math.floor(r() * 40),
    stats: {
      totalExams,
      avgScore,
      totalStudyTime: Math.round((20 + r() * 90) * 60),
      wrongQuestionCount: 12 + Math.floor(r() * 40),
      noteCount: 3 + Math.floor(r() * 20)
    },
    homeworkSubmitRate: 70 + Math.floor(r() * 28),
    recentScores: [62, 68, 71, 74, 78, 82].map(v => v + Math.floor(r() * 8)),
    weakPoints: ['定语从句', '非谓语动词', '词汇搭配'].slice(0, 1 + Math.floor(r() * 3)),
    recentExams
  }
}

export function homeworkPage({ pageNum = 1, pageSize = 10 } = {}) {
  const start = (pageNum - 1) * pageSize
  return {
    records: HOMEWORKS.slice(start, start + pageSize),
    total: HOMEWORKS.length,
    current: Number(pageNum),
    size: Number(pageSize)
  }
}

export function submissionPage(homeworkId, { pageNum = 1, pageSize = 100 } = {}) {
  const list = SUBMISSIONS[homeworkId] || []
  const start = (pageNum - 1) * pageSize
  return {
    records: list.slice(start, start + pageSize),
    total: list.length,
    current: Number(pageNum),
    size: Number(pageSize)
  }
}

/** 供 AI 汇总使用的结构化输入 */
export function digestInput(homeworkIds = [501]) {
  const homeworks = HOMEWORKS.filter(h => homeworkIds.includes(h.id)).map(h => ({
    id: h.id,
    title: h.title,
    level: h.level,
    submissions: (SUBMISSIONS[h.id] || []).map(s => ({
      studentId: s.studentId,
      studentName: s.studentName,
      content: s.content,
      submitTime: s.submitTime,
      score: s.score
    }))
  }))
  return {
    homeworks,
    students: STUDENTS.map(s => ({ id: s.id, name: s.realName, className: s.className }))
  }
}

/* ---------------- 学生端演示数据 ---------------- */
export function studentDashboard() {
  // 字段名与 views/student/Dashboard.vue 读取的一致，否则页面全是 0
  return {
    studyTime: 42.5,
    examCount: 6,
    correctRate: 78.4,
    streak: 23,
    studyDays: 23,
    homeworkPending: 2,
    homeworkTotal: 8,
    wrongQuestionCount: 34,
    todayTasks: 3
  }
}

// 今日学习任务（学生仪表板「今日任务」卡片）
export function todayPlans() {
  return [
    { id: 9011, planName: '语法专项：主谓一致 + 冠词', planType: 'STUDY', estimatedTime: 30, status: 'IN_PROGRESS' },
    { id: 9012, planName: '长难句拆解 3 句', planType: 'STUDY', estimatedTime: 20, status: 'IN_PROGRESS' },
    { id: 9013, planName: 'CET4 听力小测（第 3 套）', planType: 'EXAM', estimatedTime: 25, status: 'IN_PROGRESS' },
    { id: 9014, planName: '错题重做：虚拟语气', planType: 'STUDY', estimatedTime: 15, status: 'COMPLETED' }
  ]
}

// 错题统计
export function wrongStatistics() {
  return { total: 34, notMastered: 21, mastered: 13, reviewRate: 38.2, weekAdded: 6 }
}

export const WRONG_QUESTIONS = [
  {
    id: 7001,
    question: 'If I ______ you, I would accept the offer without hesitation.',
    options: ['am', 'was', 'were', 'be'],
    answer: 'C',
    studentAnswer: 'B',
    knowledge: '虚拟语气',
    analysis: '与现在事实相反的虚拟条件句，be 动词一律用 were。',
    createTime: '2026-11-09 20:12:00'
  },
  {
    id: 7002,
    question: 'The number of applicants ______ increasing steadily.',
    options: ['are', 'is', 'were', 'have been'],
    answer: 'B',
    studentAnswer: 'A',
    knowledge: '主谓一致',
    analysis: '主语是 the number（单数），不是 applicants。',
    createTime: '2026-11-08 19:40:00'
  },
  {
    id: 7003,
    question: 'This is the village ______ I spent my childhood.',
    options: ['which', 'that', 'where', 'when'],
    answer: 'C',
    studentAnswer: 'A',
    knowledge: '定语从句',
    analysis: '先行词是地点，从句成分完整，缺地点状语，用 where。',
    createTime: '2026-11-07 21:05:00'
  },
  {
    id: 7004,
    question: 'By the time we arrived, the film ______.',
    options: ['began', 'has begun', 'had begun', 'was beginning'],
    answer: 'C',
    studentAnswer: 'B',
    knowledge: '时态',
    analysis: '「过去的过去」用过去完成时 had done。',
    createTime: '2026-11-05 18:22:00'
  },
  {
    id: 7005,
    question: 'He is ______ honest man who never breaks a promise.',
    options: ['a', 'an', 'the', '/'],
    answer: 'B',
    studentAnswer: 'A',
    knowledge: '冠词',
    analysis: 'honest 的 h 不发音，首音是元音，用 an。',
    createTime: '2026-11-04 16:10:00'
  }
]

/* ---------------- 考试主数据（对齐 /exam/page，学生端与教师端共用） ---------------- */
export const EXAMS = [
  {
    id: 8801,
    examName: 'CET4 全真模拟（第 6 套）',
    examType: 'mock',
    level: 'CET-4',
    description: '按 2025 年 12 月真题结构组卷：阅读 5 题 + 词汇语法 5 题 + 完形 2 题 + 翻译 1 题 + 写作 1 题，满分 100 分。',
    startTime: '2026-11-11 08:00:00',
    endTime: '2026-11-16 23:59:59',
    duration: 130,
    questionCount: 14,
    totalScore: 100,
    passScore: 60,
    publishStatus: 1,
    enableAntiCheat: 0,
    participantCount: 26,
    createTime: '2026-11-10 10:00:00'
  },
  {
    id: 8802,
    examName: 'CET4 词汇专项测试 Unit 3',
    examType: 'quiz',
    level: 'CET-4',
    description: 'Unit 3 核心词汇 50 词，重点考查词性辨析与固定搭配。',
    startTime: '2026-11-08 14:00:00',
    endTime: '2026-11-08 14:30:00',
    duration: 30,
    questionCount: 14,
    totalScore: 100,
    passScore: 60,
    publishStatus: 1,
    enableAntiCheat: 0,
    participantCount: 30,
    createTime: '2026-11-05 09:20:00'
  },
  {
    id: 8803,
    examName: 'CET4 阅读理解强化（第 2 套）',
    examType: 'practice',
    level: 'CET-4',
    description: '两篇仔细阅读，训练定位与同义替换识别。',
    startTime: '2026-11-05 09:00:00',
    endTime: '2026-11-05 10:00:00',
    duration: 60,
    questionCount: 14,
    totalScore: 100,
    passScore: 60,
    publishStatus: 1,
    enableAntiCheat: 0,
    participantCount: 28,
    createTime: '2026-11-02 15:40:00'
  },
  {
    id: 8804,
    examName: 'CET6 阅读与翻译综合',
    examType: 'mock',
    level: 'CET-6',
    description: 'CET-6 长篇阅读 + 汉译英段落，时长 90 分钟。',
    startTime: '2026-11-20 09:00:00',
    endTime: '2026-11-21 23:59:59',
    duration: 90,
    questionCount: 14,
    totalScore: 100,
    passScore: 60,
    publishStatus: 1,
    enableAntiCheat: 1,
    participantCount: 0,
    createTime: '2026-11-11 11:00:00'
  },
  {
    id: 8805,
    examName: 'IELTS 学术类写作 Task 1',
    examType: 'mock',
    level: 'IELTS',
    description: '图表描述，150 词以上，20 分钟。',
    startTime: '2026-11-22 09:00:00',
    endTime: '2026-11-23 23:59:59',
    duration: 20,
    questionCount: 1,
    totalScore: 9,
    passScore: 6,
    publishStatus: 1,
    enableAntiCheat: 0,
    participantCount: 0,
    createTime: '2026-11-11 11:10:00'
  },
  {
    id: 8806,
    examName: 'IELTS 学术类写作 Task 2',
    examType: 'mock',
    level: 'IELTS',
    description: '议论文，250 词以上，40 分钟。',
    startTime: '2026-11-25 09:00:00',
    endTime: '2026-11-26 23:59:59',
    duration: 40,
    questionCount: 1,
    totalScore: 9,
    passScore: 6,
    publishStatus: 1,
    enableAntiCheat: 0,
    participantCount: 0,
    createTime: '2026-11-11 11:12:00'
  },
  {
    id: 8807,
    examName: 'CET4 听力专项（第 4 套）',
    examType: 'practice',
    level: 'CET-4',
    description: '短篇新闻 + 长对话，训练预读与关键词捕捉。',
    startTime: '2026-11-02 15:00:00',
    endTime: '2026-11-02 15:40:00',
    duration: 40,
    questionCount: 14,
    totalScore: 100,
    passScore: 60,
    publishStatus: 1,
    enableAntiCheat: 0,
    participantCount: 31,
    createTime: '2026-10-30 16:00:00'
  },
  {
    id: 8808,
    examName: 'CET4 期末模拟卷（A 卷）',
    examType: 'mock',
    level: 'CET-4',
    description: '期末统一模拟，考前一周开放。',
    startTime: '2026-12-05 09:00:00',
    endTime: '2026-12-06 23:59:59',
    duration: 130,
    questionCount: 14,
    totalScore: 100,
    passScore: 60,
    publishStatus: 1,
    enableAntiCheat: 1,
    participantCount: 0,
    createTime: '2026-11-12 17:30:00'
  }
]

/* ---------------- 试卷题目（8801 可进入作答，其余同构） ---------------- */
const READING_PASSAGE = `Online courses were once treated as an emergency substitute. Today they are a routine part of university study, and the argument has moved on from whether they work to when they work. Research suggests two conditions matter most. The first is structure: courses that divide the term into short units with weekly deadlines keep far more students engaged than those that simply upload recorded lectures. The second is interaction. Students who discuss material with peers, even briefly, are considerably more likely to complete the course. Neither condition requires expensive technology; both require a teacher willing to redesign the work students do between classes.`

const QUESTION_SEEDS = [
  {
    questionType: 'READING', section: '阅读理解', score: 5,
    title: '在线课程的有效条件',
    content: 'According to the passage, what has changed about the debate on online courses?',
    options: [
      'Whether online courses are cheaper than traditional ones',
      'Whether online courses work at all',
      'When and under what conditions online courses work',
      'Whether students prefer recorded lectures'
    ],
    answer: 'C',
    analysis: '首段末句：the argument has moved on from whether they work to when they work。'
  },
  {
    questionType: 'READING', section: '阅读理解', score: 5,
    title: '在线课程的有效条件',
    content: 'Which condition is mentioned as helping students stay engaged?',
    options: [
      'Short units with weekly deadlines',
      'Long recorded lectures',
      'Fewer assignments',
      'Lower tuition fees'
    ],
    answer: 'A',
    analysis: 'courses that divide the term into short units with weekly deadlines keep far more students engaged。'
  },
  {
    questionType: 'READING', section: '阅读理解', score: 5,
    title: '在线课程的有效条件',
    content: 'What does the passage say about interaction?',
    options: [
      'It is less important than technology',
      'Even brief peer discussion raises completion rates',
      'It only works in face-to-face classes',
      'It should be replaced by recorded videos'
    ],
    answer: 'B',
    analysis: 'Students who discuss material with peers, even briefly, are considerably more likely to complete the course。'
  },
  {
    questionType: 'READING', section: '阅读理解', score: 5,
    title: '在线课程的有效条件',
    content: 'The word "routine" in the first paragraph is closest in meaning to ______.',
    options: ['occasional', 'regular', 'expensive', 'optional'],
    answer: 'B',
    analysis: 'routine 意为「例行的、常规的」，与 regular 最接近。'
  },
  {
    questionType: 'READING', section: '阅读理解', score: 5,
    title: '在线课程的有效条件',
    content: 'What do the two conditions have in common?',
    options: [
      'Both depend on expensive platforms',
      'Both require teachers to redesign students\' work',
      'Both reduce the number of deadlines',
      'Both replace classroom teaching entirely'
    ],
    answer: 'B',
    analysis: 'Neither condition requires expensive technology; both require a teacher willing to redesign the work students do。'
  },
  {
    questionType: 'SINGLE_CHOICE', section: '词汇与语法', score: 5,
    title: '主谓一致',
    content: 'The number of students taking online courses ______ increasing steadily.',
    options: ['are', 'is', 'were', 'have been'],
    answer: 'B',
    analysis: '主语中心词是 the number（单数），谓语用 is。'
  },
  {
    questionType: 'SINGLE_CHOICE', section: '词汇与语法', score: 5,
    title: '虚拟语气',
    content: 'If I ______ you, I would accept the offer without hesitation.',
    options: ['am', 'was', 'were', 'be'],
    answer: 'C',
    analysis: '与现在事实相反的虚拟条件句，be 动词一律用 were。'
  },
  {
    questionType: 'SINGLE_CHOICE', section: '词汇与语法', score: 5,
    title: '非谓语动词',
    content: '______ from the top of the tower, the campus looks like a garden.',
    options: ['Seeing', 'Seen', 'To see', 'Having seen'],
    answer: 'B',
    analysis: 'campus 与 see 是被动关系，用过去分词作状语。'
  },
  {
    questionType: 'SINGLE_CHOICE', section: '词汇与语法', score: 5,
    title: '词义辨析',
    content: 'The new policy will ______ students to choose courses more freely.',
    options: ['enable', 'make', 'let', 'force'],
    answer: 'A',
    analysis: 'enable sb. to do sth. 为固定搭配；make/let 后不接 to。'
  },
  {
    questionType: 'SINGLE_CHOICE', section: '词汇与语法', score: 5,
    title: '定语从句',
    content: 'This is the village ______ I spent my childhood.',
    options: ['which', 'that', 'where', 'when'],
    answer: 'C',
    analysis: '先行词是地点，从句成分完整，缺地点状语，用 where。'
  },
  {
    questionType: 'CLOZE', section: '完形填空', score: 5,
    title: '完形填空（节选）',
    passage: 'Many students believe that reading speed is a gift. In fact it is a skill, and like any skill it improves with __(1)__ practice. The most effective exercise is to read a short text twice: the first time for the main idea, the second time for details you __(2)__.',
    content: '第 (1) 空应填入：',
    options: ['deliberate', 'accidental', 'rare', 'sudden'],
    answer: 'A',
    analysis: 'deliberate practice（刻意练习）是固定搭配，也与「技能靠练」呼应。'
  },
  {
    questionType: 'CLOZE', section: '完形填空', score: 5,
    title: '完形填空（节选）',
    passage: 'Many students believe that reading speed is a gift. In fact it is a skill, and like any skill it improves with __(1)__ practice. The most effective exercise is to read a short text twice: the first time for the main idea, the second time for details you __(2)__.',
    content: '第 (2) 空应填入：',
    options: ['missed', 'miss', 'will miss', 'missing'],
    answer: 'A',
    analysis: '第二遍读的是第一遍「漏掉」的细节，用过去分词 missed 作后置定语。'
  },
  {
    questionType: 'TRANSLATION', section: '翻译', score: 15,
    title: '汉译英',
    content: '将下面这段话译成英文：随着在线课程的普及，学生可以更灵活地安排学习时间。然而，缺乏固定的作息往往导致拖延，因此学校有必要为学生提供明确的学习计划。',
    options: null,
    answer: 'As online courses become more common, students can arrange their study time more flexibly. However, the lack of a fixed routine often leads to procrastination, so it is necessary for schools to provide students with a clear study plan.',
    analysis: '得分点：as 引导伴随变化、arrange time flexibly、lack of a fixed routine、lead to procrastination、it is necessary for sb. to do。'
  },
  {
    questionType: 'WRITING', section: '写作', score: 25,
    title: '短文写作',
    content: 'Directions: Write an essay of about 120 words on the topic "The Benefits and Challenges of Online Learning". You should state your view clearly and support it with at least two reasons.',
    options: null,
    answer: '',
    analysis: '评分维度：内容切题（观点明确、论据具体）、结构逻辑（三段、有连接词）、语言准确（主谓一致、冠词、时态）、词汇丰富（避免 more and more 一类高频套话）。'
  }
]

// 阅读题共用同一篇文章（不逐题重复存正文）
QUESTION_SEEDS.forEach(q => {
  if (q.questionType === 'READING') {
    q.passage = READING_PASSAGE
    q.passageTitle = 'Online Learning: Conditions That Matter'
  }
})

export function examQuestions(examId) {
  return QUESTION_SEEDS.map((q, i) => ({
    ...q,
    id: Number(examId) * 100 + i + 1,
    questionId: Number(examId) * 100 + i + 1,
    examId: Number(examId),
    difficulty: i < 5 ? 'MEDIUM' : 'EASY',
    answer: undefined // 作答时不返回答案
  }))
}

/* 考试记录（我的成绩） */
export const EXAM_RECORDS = [
  { id: 9001, examId: 8802, examName: 'CET4 词汇专项测试 Unit 3', level: 'CET-4', obtainedScore: 82, totalScore: 100, correctCount: 12, questionCount: 14, duration: 26, rank: 7, submitTime: '2026-11-08 14:26:00', status: 'FINISHED' },
  { id: 9002, examId: 8803, examName: 'CET4 阅读理解强化（第 2 套）', level: 'CET-4', obtainedScore: 75, totalScore: 100, correctCount: 11, questionCount: 14, duration: 52, rank: 13, submitTime: '2026-11-05 09:52:00', status: 'FINISHED' },
  { id: 9003, examId: 8807, examName: 'CET4 听力专项（第 4 套）', level: 'CET-4', obtainedScore: 88, totalScore: 100, correctCount: 13, questionCount: 14, duration: 38, rank: 3, submitTime: '2026-11-02 15:38:00', status: 'FINISHED' }
]

/* ---------------- 题库（教师端 /question/page） ---------------- */
export const QUESTION_BANK = QUESTION_SEEDS.map((q, i) => ({
  id: 6001 + i,
  title: q.content.length > 46 ? q.content.slice(0, 46) + '…' : q.content,
  content: q.content,
  questionType: q.questionType,
  section: q.section,
  difficulty: ['EASY', 'MEDIUM', 'HARD'][i % 3],
  knowledge: q.title,
  options: q.options ? JSON.stringify(q.options) : null,
  answer: q.answer,
  analysis: q.analysis,
  score: q.score,
  usageCount: 12 + ((i * 7) % 40),
  correctRate: 52 + ((i * 13) % 40),
  createTime: `2026-11-${String(2 + (i % 10)).padStart(2, '0')} 10:00:00`
}))

export const QUESTION_CATEGORIES = [
  { id: 1, name: '听力理解', categoryName: '听力理解', count: 0, parentId: 0, children: [] },
  { id: 2, name: '阅读理解', categoryName: '阅读理解', count: 5, parentId: 0, children: [] },
  { id: 3, name: '词汇与语法', categoryName: '词汇与语法', count: 5, parentId: 0, children: [] },
  { id: 4, name: '完形填空', categoryName: '完形填空', count: 2, parentId: 0, children: [] },
  { id: 5, name: '翻译', categoryName: '翻译', count: 1, parentId: 0, children: [] },
  { id: 6, name: '写作', categoryName: '写作', count: 1, parentId: 0, children: [] }
]

/* ---------------- 学习资源（学习中心 / 教师资源管理） ---------------- */
// 内联 SVG 渐变封面：不依赖任何图片文件，离线/静态部署都能显示
const makeCover = (c1, c2) =>
  `data:image/svg+xml;charset=utf-8,${encodeURIComponent(
    `<svg xmlns="http://www.w3.org/2000/svg" width="320" height="180"><defs><linearGradient id="g" x1="0" y1="0" x2="1" y2="1"><stop offset="0" stop-color="${c1}"/><stop offset="1" stop-color="${c2}"/></linearGradient></defs><rect width="320" height="180" fill="url(#g)"/><circle cx="256" cy="36" r="62" fill="#ffffff" opacity="0.14"/><circle cx="52" cy="152" r="74" fill="#ffffff" opacity="0.09"/></svg>`
  )}`

export const RESOURCES = [
  { id: 3001, resourceName: 'CET4 高频词汇 800（按词频排序）', description: '按近五年真题词频整理，配例句与常见搭配。', resourceType: 'DOCUMENT', level: 'CET-4', coverUrl: makeCover('#2563eb', '#0ea5e9'), viewCount: 1284, downloadCount: 421, duration: '', updateTime: '2026-11-10 09:00:00' },
  { id: 3002, resourceName: '写作模板升级手册：从套话到具体', description: '12 组高频套话的替换方案，附改写前后对照。', resourceType: 'DOCUMENT', level: 'CET-4', coverUrl: makeCover('#7c3aed', '#4f46e5'), viewCount: 966, downloadCount: 358, duration: '', updateTime: '2026-11-09 14:20:00' },
  { id: 3003, resourceName: '长难句拆解 20 讲（视频）', description: '每讲 8 分钟，讲清主干提取与从句识别。', resourceType: 'VIDEO', level: 'CET-4', coverUrl: makeCover('#0f766e', '#14b8a6'), viewCount: 742, downloadCount: 0, duration: '02:40:00', updateTime: '2026-11-08 10:30:00' },
  { id: 3004, resourceName: '阅读理解同义替换词表', description: '按题干与原文的对应关系分组，训练定位速度。', resourceType: 'DOCUMENT', level: 'CET-4', coverUrl: makeCover('#1d4ed8', '#3b82f6'), viewCount: 611, downloadCount: 233, duration: '', updateTime: '2026-11-07 16:00:00' },
  { id: 3005, resourceName: 'IELTS Writing Task 2 论证结构', description: '四段式与让步段写法，含 6.5 与 7.0 分范文对比。', resourceType: 'DOCUMENT', level: 'IELTS', coverUrl: makeCover('#b45309', '#f59e0b'), viewCount: 388, downloadCount: 96, duration: '', updateTime: '2026-11-06 11:15:00' },
  { id: 3006, resourceName: 'CET6 翻译常见中式表达纠错', description: '40 条典型中式翻译及地道改法。', resourceType: 'DOCUMENT', level: 'CET-6', coverUrl: makeCover('#be123c', '#fb7185'), viewCount: 274, downloadCount: 81, duration: '', updateTime: '2026-11-04 09:40:00' }
]

/* ---------------- 分页助手 ---------------- */
function paginate(list, params = {}) {
  const current = Number(params.pageNum || params.current || 1)
  const size = Number(params.pageSize || params.size || 10)
  const start = (current - 1) * size
  return { records: list.slice(start, start + size), total: list.length, current, size }
}

export function examPage(params = {}) {
  let list = [...EXAMS]
  if (params.level) {
    const lv = String(params.level).replace('-', '')
    list = list.filter(e => String(e.level).replace('-', '') === lv)
  }
  if (params.keyword) list = list.filter(e => e.examName.includes(params.keyword))
  return paginate(list, params)
}

export function examDetail(id) {
  const exam = EXAMS.find(e => e.id === Number(id))
  if (!exam) return null
  // 与后端返回结构保持一致：{ exam, questionCount, questionTypes }
  return {
    exam,
    questionCount: exam.questionCount,
    questionTypes: [
      { name: '阅读理解', type: 'READING', count: 5 },
      { name: '词汇与语法', type: 'SINGLE_CHOICE', count: 5 },
      { name: '完形填空', type: 'CLOZE', count: 2 },
      { name: '翻译', type: 'TRANSLATION', count: 1 },
      { name: '写作', type: 'WRITING', count: 1 }
    ]
  }
}

/* 作答运行时：记录本次开考与交卷结果，供成绩页/回顾页读取 */
let runtimeAttempt = null

export function examStart(examId) {
  const exam = EXAMS.find(e => e.id === Number(examId))
  runtimeAttempt = {
    recordId: 9101,
    examId: Number(examId),
    startedAt: Date.now(),
    submitted: false,
    answers: [],
    result: null
  }
  return {
    recordId: runtimeAttempt.recordId,
    examId: Number(examId),
    remainingSeconds: (exam?.duration || 130) * 60,
    startTime: new Date().toISOString().slice(0, 19).replace('T', ' ')
  }
}

const wordLen = s => String(s || '').trim().split(/\s+/).filter(Boolean).length

export function examSubmit({ examId, recordId, answers = [] } = {}) {
  const eid = Number(examId)
  let score = 0
  let correct = 0
  let objective = 0

  for (const a of answers) {
    const idx = Number(a.questionId) - eid * 100 - 1
    const q = QUESTION_SEEDS[idx]
    if (!q) continue
    const raw = String(a.answer ?? '').trim()
    if (q.questionType === 'READING' || q.questionType === 'SINGLE_CHOICE' || q.questionType === 'CLOZE') {
      objective += 1
      // 前端提交的是选项下标（0/1/2/3），统一转成字母再比对
      const letter = /^\d+$/.test(raw) ? String.fromCharCode(65 + Number(raw)) : raw.toUpperCase()
      if (letter && letter === String(q.answer).toUpperCase()) {
        score += q.score
        correct += 1
      }
    } else if (q.questionType === 'TRANSLATION') {
      const n = wordLen(raw)
      score += n >= 28 ? q.score : Math.round(q.score * Math.min(1, n / 28))
    } else if (q.questionType === 'WRITING') {
      const n = wordLen(raw)
      score += n >= 110 ? q.score : Math.round(q.score * Math.min(1, n / 110))
    }
  }

  const exam = EXAMS.find(e => e.id === eid)
  const totalScore = exam?.totalScore || 100
  const wrong = Math.max(0, objective - correct)
  const accuracy = objective ? Math.round((correct / objective) * 100) : 0
  const result = {
    id: Number(recordId) || 9101,
    recordId: Number(recordId) || 9101,
    examId: eid,
    examName: exam?.examName || '考试',
    level: exam?.level || 'CET-4',
    obtainedScore: Math.min(totalScore, score),
    score: Math.min(totalScore, score),
    totalScore,
    passScore: exam?.passScore || 60,
    correctCount: correct,
    wrongCount: wrong,
    questionCount: QUESTION_SEEDS.length,
    duration: Math.max(1, Math.round((Date.now() - (runtimeAttempt?.startedAt || Date.now())) / 60000)),
    rank: 6,
    percentile: 82,
    submitTime: new Date().toISOString().slice(0, 19).replace('T', ' '),
    status: 'FINISHED'
  }
  if (runtimeAttempt) {
    runtimeAttempt.submitted = true
    runtimeAttempt.answers = answers
    runtimeAttempt.result = result
  }
  return result
}

export function examRecordDetail(recordId) {
  if (runtimeAttempt && runtimeAttempt.result && Number(recordId) === runtimeAttempt.recordId) {
    return runtimeAttempt.result
  }
  return EXAM_RECORDS.find(r => r.id === Number(recordId)) || {
    id: Number(recordId),
    examId: 8802,
    examName: 'CET4 全真模拟（第 6 套）',
    obtainedScore: 82,
    totalScore: 100,
    passScore: 60,
    correctCount: 12,
    wrongCount: 2,
    duration: 46,
    rank: 7,
    percentile: 78,
    submitTime: '2026-11-11 15:42:18',
    status: 'FINISHED'
  }
}


export function examRecordPage(params = {}) {
  return paginate([...EXAM_RECORDS], params)
}

export function questionPage(params = {}) {
  let list = [...QUESTION_BANK]
  if (params.keyword) list = list.filter(q => q.title.includes(params.keyword) || q.content.includes(params.keyword))
  if (params.questionType) list = list.filter(q => q.questionType === params.questionType)
  return paginate(list, params)
}

export function resourcePage(params = {}) {
  let list = [...RESOURCES]
  if (params.level) {
    const lv = String(params.level).replace('-', '').toUpperCase()
    list = list.filter(r => String(r.level).replace('-', '').toUpperCase() === lv)
  }
  if (params.resourceType) list = list.filter(r => r.resourceType === String(params.resourceType).toUpperCase())
  if (params.keyword) list = list.filter(r => r.resourceName.includes(params.keyword))
  return paginate(list, params)
}

/* ============================================================
 * 在线课堂
 * ============================================================ */

export const CLASSROOMS = [
  {
    id: 9001,
    classroomName: 'CET4 听力精讲：长对话信息定位',
    description: '以 2025 年 12 月真题为素材，逐题拆解长对话中的时间、地点、态度三类关键信息。',
    teacherName: '李静',
    status: 'LIVE',
    startTime: '2026-11-13 10:00:00',
    currentStudents: 38,
    coverImage: ''
  },
  {
    id: 9002,
    classroomName: '写作提分：三段式结构到衔接手段',
    description: '从评分标准出发，演示如何用衔接词把模板句改写成连贯段落。',
    teacherName: '李静',
    status: 'LIVE',
    startTime: '2026-11-13 09:30:00',
    currentStudents: 24,
    coverImage: ''
  },
  {
    id: 9003,
    classroomName: 'IELTS 口语 Part 2 话题卡速构',
    description: '1 分钟准备、2 分钟陈述的实操训练，含话题卡拆分与扩展思路。',
    teacherName: '王敏',
    status: 'PENDING',
    startTime: '2026-11-14 19:00:00',
    currentStudents: 0,
    coverImage: ''
  },
  {
    id: 9004,
    classroomName: '词汇速记：词根词缀拆解示范',
    description: '用 20 个高频词根串起 CET4 核心词汇，演示可复用的记忆路径。',
    teacherName: '李静',
    status: 'PENDING',
    startTime: '2026-11-15 15:30:00',
    currentStudents: 0,
    coverImage: ''
  },
  {
    id: 9005,
    classroomName: 'CET4 阅读：仔细阅读题型定位',
    description: '题干关键词回文定位与干扰项特征分析，配套真题演练。',
    teacherName: '王敏',
    status: 'ENDED',
    startTime: '2026-11-10 14:00:00',
    currentStudents: 0,
    coverImage: ''
  },
  {
    id: 9006,
    classroomName: '错题讲评：翻译与写作共性问题',
    description: '汇总本班近两周作业中暴露的时态、搭配与中式表达问题，集中讲评。',
    teacherName: '李静',
    status: 'ENDED',
    startTime: '2026-11-08 16:00:00',
    currentStudents: 0,
    coverImage: ''
  }
]

export function classroomLive() {
  return CLASSROOMS.filter(c => c.status === 'LIVE')
}

export function classroomPage(params = {}) {
  let list = [...CLASSROOMS]
  if (params.status) list = list.filter(c => c.status === String(params.status).toUpperCase())
  if (params.keyword) list = list.filter(c => c.classroomName.includes(params.keyword))
  return paginate(list, params)
}

/* ============================================================
 * 学习社区
 * ============================================================ */

export const COMMUNITY_POSTS = [
  {
    id: 7001,
    title: '作文从 12 分到 14 分，我把 AI 批改的建议落成了三张清单',
    content: '连续两周用 AI 批改同一类作文，把它指出的错误归类后发现：时态错误占了一半、衔接词反复只用 and/but。于是整理了三张自查清单，第二次写同类题目时分数直接上了一个档。',
    author: '张伟',
    authorName: '张伟',
    avatar: '',
    likes: 46,
    likeCount: 46,
    comments: 12,
    commentCount: 12,
    replyCount: 12,
    viewCount: 328,
    createTime: '2026-11-12 21:40:00',
    tags: ['写作', 'AI批改'],
    isTop: true,
    isTopped: true,
    isEssence: true,
    isFeatured: true,
    isLiked: false
  },
  {
    id: 7002,
    title: 'CET4 听力总在长对话丢分，有没有同款？',
    content: '每次听到数字和时间就断片，做完对答案发现不是没听到，而是记不下来。想问问大家做笔记时都记什么。',
    author: '李思远',
    authorName: '李思远',
    avatar: '',
    likes: 28,
    likeCount: 28,
    comments: 19,
    commentCount: 19,
    replyCount: 19,
    viewCount: 214,
    createTime: '2026-11-12 19:05:00',
    tags: ['听力', 'CET4'],
    isTop: false,
    isTopped: false,
    isEssence: false,
    isLiked: true
  },
  {
    id: 7003,
    title: '分享一个我用了两周的每日打卡节奏',
    content: '早上 15 分钟词汇、中午 20 分钟听力、晚上 30 分钟阅读加错题回顾。打卡连续 14 天了，最大的感受是任务拆小之后更容易坚持。',
    author: '陈雨桐',
    authorName: '陈雨桐',
    avatar: '',
    likes: 63,
    likeCount: 63,
    comments: 8,
    commentCount: 8,
    replyCount: 8,
    viewCount: 402,
    createTime: '2026-11-11 22:18:00',
    tags: ['打卡', '学习计划'],
    isTop: false,
    isTopped: false,
    isEssence: true,
    isFeatured: true,
    isLiked: false
  },
  {
    id: 7004,
    title: 'IELTS 口语 Part 2 话题卡，我的 1 分钟准备法',
    content: '把话题卡拆成 who / what / why / feeling 四格，每格只写两个关键词，剩下的靠连接句串起来。练了十套之后基本不会卡壳。',
    author: '王思琪',
    authorName: '王思琪',
    avatar: '',
    likes: 51,
    likeCount: 51,
    comments: 15,
    commentCount: 15,
    replyCount: 15,
    viewCount: 356,
    createTime: '2026-11-10 20:30:00',
    tags: ['口语', 'IELTS'],
    isTop: false,
    isTopped: false,
    isEssence: false,
    isLiked: false
  },
  {
    id: 7005,
    title: '错题本按知识点重排之后，复习效率明显变高',
    content: '原来按时间顺序堆着，回看时找不到规律。后来按虚拟语气、非谓语、定语从句分组，一次只攻一类，两天就能感觉出差别。',
    author: '刘子涵',
    authorName: '刘子涵',
    avatar: '',
    likes: 37,
    likeCount: 37,
    comments: 6,
    commentCount: 6,
    replyCount: 6,
    viewCount: 189,
    createTime: '2026-11-09 21:12:00',
    tags: ['错题本', '语法'],
    isTop: false,
    isTopped: false,
    isEssence: false,
    isLiked: false
  },
  {
    id: 7006,
    title: '求组队：一起刷 12 月 CET4 真题，每晚八点',
    content: '打算每晚八点做一套听力加阅读，做完在群里对答案、互相讲错题。希望能凑够五个人。',
    author: '赵一鸣',
    authorName: '赵一鸣',
    avatar: '',
    likes: 22,
    likeCount: 22,
    comments: 23,
    commentCount: 23,
    replyCount: 23,
    viewCount: 267,
    createTime: '2026-11-09 18:44:00',
    tags: ['组队', 'CET4'],
    isTop: false,
    isTopped: false,
    isEssence: false,
    isLiked: false
  }
]

// 社区页按 username 渲染作者名，这里统一补上中文名，避免显示「匿名用户」
COMMUNITY_POSTS.forEach(p => { p.username = p.username || p.authorName || p.author })

export function communityPostPage(params = {}) {
  let list = [...COMMUNITY_POSTS]
  if (params.keyword) {
    const k = String(params.keyword)
    list = list.filter(p => p.title.includes(k) || p.content.includes(k))
  }
  if (params.topic || params.tag) {
    const t = String(params.topic || params.tag)
    list = list.filter(p => (p.tags || []).includes(t))
  }
  return paginate(list, params)
}

export const HOT_TOPICS = [
  { id: 1, name: 'CET4 听力提分', topic: 'CET4 听力提分', count: 128, postCount: 128 },
  { id: 2, name: 'AI 批改怎么用', topic: 'AI 批改怎么用', count: 96, postCount: 96 },
  { id: 3, name: '写作衔接词', topic: '写作衔接词', count: 78, postCount: 78 },
  { id: 4, name: 'IELTS 口语 Part2', topic: 'IELTS 口语 Part2', count: 64, postCount: 64 },
  { id: 5, name: '每日打卡', topic: '每日打卡', count: 57, postCount: 57 }
]

export const ACTIVE_USERS = [
  { id: 1001, userId: 1001, username: 'zhangwei', nickname: '张伟', name: '张伟', avatar: '', postCount: 18, checkinDate: '2026-11-13' },
  { id: 1003, userId: 1003, username: 'chenyutong', nickname: '陈雨桐', name: '陈雨桐', avatar: '', postCount: 15, checkinDate: '2026-11-13' },
  { id: 1004, userId: 1004, username: 'wangsiqi', nickname: '王思琪', name: '王思琪', avatar: '', postCount: 12, checkinDate: '2026-11-12' },
  { id: 1002, userId: 1002, username: 'lisiyuan', nickname: '李思远', name: '李思远', avatar: '', postCount: 9, checkinDate: '2026-11-12' },
  { id: 1005, userId: 1005, username: 'liuzihan', nickname: '刘子涵', name: '刘子涵', avatar: '', postCount: 7, checkinDate: '2026-11-11' }
]

// 活跃用户列表同样优先展示中文姓名
ACTIVE_USERS.forEach(u => { if (u.nickname) u.username = u.nickname })

export const GROUPS = [
  {
    id: 601,
    groupName: 'CET4 十二月冲刺组',
    name: 'CET4 十二月冲刺组',
    groupIntro: '每晚八点真题打卡，做完对答案并互相讲错题。',
    currentMembers: 42,
    members: 42,
    maxMembers: 60,
    joined: false
  },
  {
    id: 602,
    groupName: 'IELTS 口语互练',
    name: 'IELTS 口语互练',
    groupIntro: '每周两次语音互练，按话题卡轮流提问与点评。',
    currentMembers: 28,
    members: 28,
    maxMembers: 40,
    joined: true
  },
  {
    id: 603,
    groupName: '写作批改互助',
    name: '写作批改互助',
    groupIntro: '提交作文后互相按四维标准打分，再用 AI 批改结果对照。',
    currentMembers: 35,
    members: 35,
    maxMembers: 50,
    joined: false
  },
  {
    id: 604,
    groupName: '词汇词根党',
    name: '词汇词根党',
    groupIntro: '每天一个词根，分享衍生的高频词与例句。',
    currentMembers: 51,
    members: 51,
    maxMembers: 80,
    joined: false
  }
]

export function groupRecommend(limit = 5) {
  return GROUPS.slice(0, limit)
}

export function groupMy() {
  return GROUPS.filter(g => g.joined)
}

export function groupPage(params = {}) {
  return paginate([...GROUPS], params)
}

/* ============================================================
 * 系统管理（原管理员端，现已并入教师端）
 * ============================================================ */

const ROLE_NAMES = { ROLE_ADMIN: '系统管理员', ROLE_TEACHER: '教师', ROLE_STUDENT: '学生' }

export const ADMIN_USERS = STUDENTS.slice(0, 17).map((s, i) => ({
  id: s.id,
  username: s.username,
  realName: s.realName,
  nickname: s.nickname,
  avatar: '',
  email: s.email,
  phone: s.phone,
  className: s.className,
  role: 'student',
  roleName: '学生',
  roles: ['ROLE_STUDENT'],
  status: i % 11 === 5 ? 0 : 1,
  createTime: s.createTime,
  lastLogin: `2026-11-${String(12 - (i % 4)).padStart(2, '0')} ${String(9 + (i % 10)).padStart(2, '0')}:${String((i * 7) % 60).padStart(2, '0')}:00`
})).concat([
  {
    id: 2001,
    username: 'teacher',
    realName: '李静',
    nickname: '李老师',
    avatar: '',
    email: 'lijing@edu.cn',
    phone: '13800002001',
    role: 'teacher',
    roleName: '教师',
    roles: ['ROLE_TEACHER'],
    status: 1,
    createTime: '2026-01-08T09:00:00',
    lastLogin: '2026-11-13 08:42:00'
  },
  {
    id: 2002,
    username: 'teacher2',
    realName: '王敏',
    nickname: '王老师',
    avatar: '',
    email: 'wangmin@edu.cn',
    phone: '13800002002',
    role: 'teacher',
    roleName: '教师',
    roles: ['ROLE_TEACHER'],
    status: 1,
    createTime: '2026-02-11T09:00:00',
    lastLogin: '2026-11-12 19:20:00'
  },
  {
    id: 1,
    username: 'teacher',
    realName: '系统管理员',
    nickname: '系统管理员',
    avatar: '',
    email: 'admin@edu.cn',
    phone: '13800000001',
    role: 'teacher',
    roleName: '系统管理员',
    roles: ['ROLE_TEACHER'],
    status: 1,
    createTime: '2026-01-01T00:00:00',
    lastLogin: '2026-11-13 08:05:00'
  }
])

export function adminUserPage(params = {}) {
  let list = [...ADMIN_USERS]
  if (params.keyword) {
    const k = String(params.keyword)
    list = list.filter(u => u.username.includes(k) || u.realName.includes(k) || u.email.includes(k))
  }
  if (params.role) list = list.filter(u => u.role === String(params.role).toLowerCase())
  if (params.status !== undefined && params.status !== '' && params.status !== null) {
    list = list.filter(u => String(u.status) === String(params.status))
  }
  return paginate(list, params)
}

export const OPERATION_LOGS = [
  { id: 90001, username: 'teacher', operation: '发布考试：CET4 全真模拟（第 6 套）', module: '考试管理', ipAddress: '192.168.1.24', status: 1, createTime: '2026-11-13 08:42:11', executeTime: 132 },
  { id: 90002, username: 'teacher', operation: '批改作文：Online Learning: Benefits and Challenges', module: 'AI 教学助手', ipAddress: '192.168.1.24', status: 1, createTime: '2026-11-13 08:31:07', executeTime: 864 },
  { id: 90003, username: 'student', operation: '提交试卷：CET4 全真模拟（第 6 套）', module: '在线考试', ipAddress: '192.168.1.88', status: 1, createTime: '2026-11-13 08:20:35', executeTime: 2210 },
  { id: 90004, username: 'student', operation: 'AI 作文批改', module: 'AI 智能辅导', ipAddress: '192.168.1.88', status: 1, createTime: '2026-11-13 08:12:44', executeTime: 1180 },
  { id: 90005, username: 'teacher', operation: '修改系统配置：考试默认时长', module: '系统配置', ipAddress: '192.168.1.10', status: 1, createTime: '2026-11-13 08:05:18', executeTime: 96 },
  { id: 90006, username: 'teacher2', operation: '创建课堂：IELTS 口语 Part 2 话题卡速构', module: '在线课堂', ipAddress: '192.168.1.31', status: 1, createTime: '2026-11-12 20:41:02', executeTime: 208 },
  { id: 90007, username: 'student', operation: '每日打卡', module: '学习打卡', ipAddress: '192.168.1.88', status: 1, createTime: '2026-11-12 19:03:51', executeTime: 44 },
  { id: 90008, username: 'unknown', operation: '登录失败：密码错误', module: '用户认证', ipAddress: '203.0.113.45', status: 0, createTime: '2026-11-12 18:22:09', executeTime: 38, method: 'POST /api/auth/login', errorMsg: 'BadCredentialsException：用户名或密码错误' },
  { id: 90016, username: 'student', operation: '提交作文批改', module: 'AI 智能辅导', ipAddress: '192.168.1.88', status: 0, createTime: '2026-11-12 17:48:31', executeTime: 30, method: 'POST /api/ai/writing/grade', errorMsg: 'BusinessException：作文少于 100 词，未达到批改门槛' },
  { id: 90017, username: 'teacher2', operation: '上传听力音频', module: '资源管理', ipAddress: '192.168.1.31', status: 0, createTime: '2026-11-11 10:12:55', executeTime: 412, method: 'POST /api/file/upload', errorMsg: 'MaxUploadSizeExceededException：文件大小超过 20MB 限制' },
  { id: 90009, username: 'teacher', operation: '导入题库：CET4 阅读 12 题', module: '题库管理', ipAddress: '192.168.1.24', status: 1, createTime: '2026-11-12 16:10:33', executeTime: 1420 },
  { id: 90010, username: 'student', operation: '收藏学习资源：CET4 听力高频场景词', module: '学习中心', ipAddress: '192.168.1.64', status: 1, createTime: '2026-11-12 15:47:12', executeTime: 61 },
  { id: 90011, username: 'teacher', operation: '导出操作日志（近 7 天）', module: '日志审计', ipAddress: '192.168.1.10', status: 1, createTime: '2026-11-12 14:05:00', executeTime: 530 },
  { id: 90012, username: 'teacher2', operation: '编辑作业：Unit 5 写作练习', module: '作业管理', ipAddress: '192.168.1.31', status: 1, createTime: '2026-11-12 11:32:47', executeTime: 178 },
  { id: 90013, username: 'student', operation: '查看错题解析：虚拟语气', module: '错题本', ipAddress: '192.168.1.88', status: 1, createTime: '2026-11-12 10:15:26', executeTime: 72 },
  { id: 90014, username: 'teacher', operation: '结束课堂：CET4 阅读：仔细阅读题型定位', module: '在线课堂', ipAddress: '192.168.1.24', status: 1, createTime: '2026-11-11 16:02:14', executeTime: 88 },
  { id: 90015, username: 'teacher', operation: '数据备份：db-2026-11-11.sql', module: '系统备份', ipAddress: '192.168.1.10', status: 1, createTime: '2026-11-11 02:00:00', executeTime: 8420 }
]

export function operationLogPage(params = {}) {
  let list = [...OPERATION_LOGS]
  if (params.keyword) {
    const k = String(params.keyword)
    list = list.filter(l => l.operation.includes(k) || l.username.includes(k))
  }
  if (params.status !== undefined && params.status !== '' && params.status !== null) {
    list = list.filter(l => String(l.status) === String(params.status))
  }
  return paginate(list, params)
}

export const SYSTEM_CONFIG = {
  systemName: '英语等级智能学习与考试系统',
  systemLogo: '',
  copyright: '© 2026 English Learning System',
  icp: '京 ICP 备 2026000000 号',
  examDefaultDuration: 120,
  examSwitchWarning: 3,
  examAutoSubmit: true,
  examScoreDecimal: 1,
  uploadMaxSize: 20,
  uploadAllowTypes: 'jpg,png,pdf,docx,mp3',
  smtpHost: 'smtp.edu.cn',
  smtpPort: 465,
  senderEmail: 'noreply@edu.cn',
  senderPassword: ''
}

export const MONITOR_DATA = {
  cpu: {
    model: 'Intel Core i7-12700 @ 2.10GHz',
    availableProcessors: 12,
    usagePercent: 32,
    systemLoadAverage: 1.24,
    processCpuLoad: '18%',
    systemCpuLoad: '32%'
  },
  memory: { total: '3.4 GB', used: '3.4 GB', max: '8.0 GB', usagePercent: 43, free: '4.6 GB' },
  disk: {
    totalSpace: '512 GB',
    usedSpace: '186 GB',
    freeSpace: '326 GB',
    usagePercent: 36,
    disks: [
      { path: 'C:\\', total: '400 GB', used: '142 GB', free: '258 GB', usagePercent: 36 },
      { path: 'D:\\', total: '112 GB', used: '44 GB', free: '68 GB', usagePercent: 39 }
    ]
  },
  overview: {
    onlineUsers: 47,
    todayActive: 128,
    totalStudents: 32,
    totalTeachers: 2,
    examCount: 8,
    questionCount: 1240
  },
  system: {
    osName: 'Windows 11',
    osVersion: '10.0.22631',
    osArch: 'amd64',
    javaVersion: '17.0.9',
    javaVendor: 'Eclipse Adoptium',
    javaHome: 'C:\\Program Files\\Java\\jdk-17.0.9',
    userDir: 'D:\\edu\\english-system\\app\\backend',
    startTime: '2026-11-12 02:00:00',
    runDays: 1
  },
  jvm: {
    name: 'OpenJDK 64-Bit Server VM',
    version: '17.0.9+9',
    uptime: '1 天 6 小时 12 分钟',
    startTime: '2026-11-12 02:00:00',
    heapMemory: { used: '420 MB', max: '1024 MB', usagePercent: 41 },
    nonHeapMemory: { used: '118 MB', committed: '256 MB' },
    thread: { active: 48, daemon: 22, peak: 63, totalStarted: 1846 },
    gc: [
      { name: 'G1 Young Generation', collectionCount: 42, collectionTime: '1.24s' },
      { name: 'G1 Old Generation', collectionCount: 2, collectionTime: '0.42s' }
    ]
  },
  redis: {
    status: 'connected',
    version: '7.2.3',
    mode: 'standalone',
    uptimeInDays: 1,
    usedMemory: '12.4 MB',
    usedMemoryPeak: '18.6 MB',
    connectedClients: 8,
    totalConnectionsReceived: 2641,
    error: ''
  },
  realtime: { cpuUsage: 32, memoryUsage: 43, threadCount: 48 },
  realtimeHistory: Array.from({ length: 24 }, (_, i) => ({
    cpu: 24 + ((i * 7) % 22),
    memory: 40 + ((i * 5) % 16)
  }))
}


/* ============================================================
   补充数据集合 · 覆盖学习中心 / 消息 / 系统管理等模块
   ============================================================ */

export const RESOURCE_CATEGORIES = [
  { id: 401, name: 'CET-4 真题', categoryName: 'CET-4 真题', parentId: 0, level: 1, resourceCount: 24, sortOrder: 1, children: [] },
  { id: 402, name: 'CET-6 真题', categoryName: 'CET-6 真题', parentId: 0, level: 1, resourceCount: 12, sortOrder: 2, children: [] },
  { id: 403, name: '写作专项', categoryName: '写作专项', parentId: 0, level: 1, resourceCount: 18, sortOrder: 3, children: [] },
  { id: 404, name: '听力训练', categoryName: '听力训练', parentId: 0, level: 1, resourceCount: 15, sortOrder: 4, children: [] },
  { id: 405, name: '口语话题', categoryName: '口语话题', parentId: 0, level: 1, resourceCount: 9, sortOrder: 5, children: [] },
  { id: 406, name: '核心词汇', categoryName: '核心词汇', parentId: 403, level: 2, resourceCount: 11, sortOrder: 6, children: [] },
  { id: 407, name: '长难句解析', categoryName: '长难句解析', parentId: 403, level: 2, resourceCount: 7, sortOrder: 7, children: [] }
]

export const STUDY_NOTES = [
  {
    id: 601, title: '虚拟语气三种常见考点整理',
    content: 'if 引导的虚拟语气：过去式表示与现在相反；should have done 表示与过去相反；would rather 句型注意省略结构。重点整理了 "wish + had done" 与 "if + had done" 的互换。',
    type: 'NOTE', tags: '语法,虚拟语气,CET-4', userId: 1001, userName: '张伟',
    createTime: '2026-11-10 20:14:00', updateTime: '2026-11-12 09:30:00', viewCount: 46
  },
  {
    id: 602, title: '写作 Task 2 让步段的三种写法',
    content: '1) Although..., ...  2) While it is true that..., ...  3) Admittedly, ...; nevertheless, ...。老师批改时强调让步段必须落在具体论据上，不能空谈。',
    type: 'NOTE', tags: '写作,Task 2,模板', userId: 1001, userName: '张伟',
    createTime: '2026-11-09 21:05:00', updateTime: '2026-11-11 16:42:00', viewCount: 72
  },
  {
    id: 603, title: '听力场景词：机场值机高频表达',
    content: 'check in / boarding pass / gate / baggage drop / aisle seat / window seat / connecting flight —— 整理了 20 个机场场景常见词与例句。',
    type: 'NOTE', tags: '听力,场景词', userId: 1002, userName: '李娜',
    createTime: '2026-11-08 19:22:00', updateTime: '2026-11-10 08:15:00', viewCount: 58
  },
  {
    id: 604, title: '长难句拆解练习记录（第 3—8 讲）',
    content: '主句先行是关键：先把所有 that/which/who 引导的修饰成分括起来，剩下的就是主干。练习了 6 讲，识别准确率从 60% 提升到 85%。',
    type: 'NOTE', tags: '长难句,练习', userId: 1003, userName: '王强',
    createTime: '2026-11-07 22:40:00', updateTime: '2026-11-09 07:20:00', viewCount: 39
  },
  {
    id: 605, title: '四级高频词根词缀速记',
    content: '-spect- 看（inspect/respect）、-duc- 引导（introduce/conduct）、-tain- 持有（maintain/retain）。词根词缀推词义比死记更快。',
    type: 'NOTE', tags: '词汇,词根词缀', userId: 1004, userName: '刘洋',
    createTime: '2026-11-06 18:55:00', updateTime: '2026-11-08 11:10:00', viewCount: 91
  }
]

export const STUDY_TASKS = [
  { id: 701, title: '完成 CET-4 模拟卷（第 6 套）听力部分', type: 'EXAM', status: 'PENDING', progress: 0, deadline: '2026-11-15 23:59:00', planId: 801, userId: 1001, createTime: '2026-11-13 09:00:00' },
  { id: 702, title: '背诵四级高频词 30 个并造句', type: 'STUDY', status: 'IN_PROGRESS', progress: 60, deadline: '2026-11-15 23:59:00', planId: 801, userId: 1001, createTime: '2026-11-13 09:00:00' },
  { id: 703, title: '口语陪练：科技类话题 90 秒', type: 'SPEAKING', status: 'PENDING', progress: 0, deadline: '2026-11-16 23:59:00', planId: 801, userId: 1001, createTime: '2026-11-13 09:00:00' },
  { id: 704, title: '提交作文：Online Learning 的利弊', type: 'WRITING', status: 'COMPLETED', progress: 100, deadline: '2026-11-14 23:59:00', planId: 802, userId: 1001, createTime: '2026-11-12 09:00:00' },
  { id: 705, title: '错题本复习：定语从句 8 题', type: 'REVIEW', status: 'PENDING', progress: 0, deadline: '2026-11-16 23:59:00', planId: 802, userId: 1001, createTime: '2026-11-12 09:00:00' },
  { id: 706, title: '长难句拆解第 9—12 讲', type: 'STUDY', status: 'IN_PROGRESS', progress: 25, deadline: '2026-11-18 23:59:00', planId: 803, userId: 1002, createTime: '2026-11-11 09:00:00' },
  { id: 707, title: '听力精听 Day 12', type: 'LISTENING', status: 'PENDING', progress: 0, deadline: '2026-11-17 23:59:00', planId: 803, userId: 1002, createTime: '2026-11-11 09:00:00' }
]

export const STUDY_PLANS = [
  { id: 801, planName: '四级冲刺 · 第 1 周', goal: '完成两套真题 + 核心词汇一轮', startDate: '2026-11-13', endDate: '2026-11-19', status: 'IN_PROGRESS', totalTasks: 5, completedTasks: 1, userId: 1001, createTime: '2026-11-13 08:30:00' },
  { id: 802, planName: '写作专项强化', goal: '掌握四段式结构与让步段写法', startDate: '2026-11-09', endDate: '2026-11-16', status: 'IN_PROGRESS', totalTasks: 4, completedTasks: 2, userId: 1001, createTime: '2026-11-09 08:30:00' },
  { id: 803, planName: '听力长难句双线提升', goal: '听力正确率提升到 70%', startDate: '2026-11-11', endDate: '2026-11-20', status: 'IN_PROGRESS', totalTasks: 6, completedTasks: 1, userId: 1002, createTime: '2026-11-11 08:30:00' },
  { id: 804, planName: '口语话题库积累', goal: '覆盖 20 个高频话题', startDate: '2026-10-28', endDate: '2026-11-11', status: 'COMPLETED', totalTasks: 8, completedTasks: 8, userId: 1003, createTime: '2026-10-28 08:30:00' }
]

export const MESSAGES = [
  { id: 2001, title: '作文批改反馈：Online Learning: Benefits and Challenges', content: '张伟同学，你这篇作文结构清晰，四段式用得比较稳。问题在于第三段论据展开不足，两个论点都只停在一句话。建议每个论点补一个具体场景或数据支撑。另外 conclusion 段的套话偏多，试试回扣题干关键词。', type: 'ASSIGNMENT', senderId: 2001, senderName: '王老师', receiverId: 1001, receiverName: '张伟', isRead: 0, createTime: '2026-11-13 08:31:07' },
  { id: 2002, title: '口语陪练报告已生成', content: '你本周完成了 4 次口语陪练，流利度平均 72 分、发音准确度 68 分。th 音和词尾辅音仍是主要失分点，建议每天跟读 5 分钟。', type: 'SYSTEM', senderId: 1, senderName: '系统', receiverId: 1001, receiverName: '张伟', isRead: 0, createTime: '2026-11-12 20:10:00' },
  { id: 2003, title: '作业已批改', content: '本次作业共 3 题，你答对 2 题。第 3 题考查定语从句引导词的选择，注意 that 与 which 的区分。', type: 'ASSIGNMENT', senderId: 2001, senderName: '王老师', receiverId: 1001, receiverName: '张伟', isRead: 1, createTime: '2026-11-11 15:42:18' },
  { id: 2004, title: '考试提醒：四级模拟卷（第 6 套）', content: '你报名的四级模拟卷将于 2026-11-20 08:00 开放，考试时长 130 分钟，共 14 题，满分 100 分，60 分及格。请提前登录熟悉考试流程。', type: 'EXAM', senderId: 1, senderName: '系统', receiverId: 1001, receiverName: '张伟', isRead: 1, createTime: '2026-11-10 09:05:00' },
  { id: 2005, title: '课堂通知：周四口语工作坊', content: '本周四下午 15:30 在语音教室 3 开展口语工作坊，主题是 Part 2 话题卡的三分钟结构，请携带一本笔记本。', type: 'NOTICE', senderId: 2001, senderName: '王老师', receiverId: 1001, receiverName: '张伟', isRead: 1, createTime: '2026-11-09 10:22:00' },
  { id: 2006, title: '社区互动：你的笔记被点赞 12 次', content: '你发布的《虚拟语气三种常见考点整理》获得了 12 次点赞和 3 条评论，快去看看大家都在讨论什么。', type: 'SYSTEM', senderId: 1, senderName: '系统', receiverId: 1002, receiverName: '李娜', isRead: 0, createTime: '2026-11-13 07:45:00' },
  { id: 2007, title: '学习小组邀请', content: '「四级冲刺小组」邀请你加入，成员目前 6 人，每周完成 3 次共学打卡。', type: 'NOTICE', senderId: 2002, senderName: '李老师', receiverId: 1003, receiverName: '王强', isRead: 1, createTime: '2026-11-12 13:18:00' },
  { id: 2008, title: '课程资源更新通知', content: '「长难句拆解 20 讲」已更新第 11—14 讲，新增 12 个真题例句拆解。', type: 'SYSTEM', senderId: 1, senderName: '系统', receiverId: 1004, receiverName: '刘洋', isRead: 1, createTime: '2026-11-08 16:30:00' }
]

export const PHOTOS = [
  { filename: 'default-avatar.png', url: '/photo/default-avatar.png', size: 48213 },
  { filename: 'login-bg.jpg', url: '/photo/login-bg.jpg', size: 1287406 },
  { filename: 'campus-banner.jpg', url: '/photo/campus-banner.jpg', size: 964512 },
  { filename: 'library-cover.png', url: '/photo/library-cover.png', size: 231044 },
  { filename: 'empty-state.svg', url: '/photo/empty-state.svg', size: 4180 },
  { filename: 'classroom-icon.png', url: '/photo/classroom-icon.png', size: 38255 }
]

export const BACKUPS = [
  { id: 901, filename: 'backup_20261113_020000.zip', size: 41943040, type: 'AUTO', status: 'SUCCESS', createTime: '2026-11-13 02:00:00', duration: 42 },
  { id: 902, filename: 'backup_20261112_020000.zip', size: 41250816, type: 'AUTO', status: 'SUCCESS', createTime: '2026-11-12 02:00:00', duration: 39 },
  { id: 903, filename: 'backup_20261111_020000.zip', size: 40804352, type: 'AUTO', status: 'SUCCESS', createTime: '2026-11-11 02:00:00', duration: 41 },
  { id: 904, filename: 'manual_backup_20261110.zip', size: 42598400, type: 'MANUAL', status: 'SUCCESS', createTime: '2026-11-10 16:24:00', duration: 51 },
  { id: 905, filename: 'backup_20261109_020000.zip', size: 39845888, type: 'AUTO', status: 'SUCCESS', createTime: '2026-11-09 02:00:00', duration: 37 },
  { id: 906, filename: 'backup_20261108_020000.zip', size: 39157760, type: 'AUTO', status: 'FAILED', createTime: '2026-11-08 02:00:00', duration: 12 }
]

export const CHECKIN_LOGS = [
  { id: 1001, userId: 1001, userName: '张伟', checkinDate: '2026-11-13', checkinTime: '08:12:33', duration: 45, content: '背单词 30 个 + 听力精听 1 篇', continuousDays: 5 },
  { id: 1002, userId: 1001, userName: '张伟', checkinDate: '2026-11-12', checkinTime: '21:04:18', duration: 60, content: '完成一套真题的阅读部分', continuousDays: 4 },
  { id: 1003, userId: 1001, userName: '张伟', checkinDate: '2026-11-11', checkinTime: '07:45:02', duration: 30, content: '口语跟读 10 分钟', continuousDays: 3 },
  { id: 1004, userId: 1001, userName: '张伟', checkinDate: '2026-11-10', checkinTime: '22:16:47', duration: 50, content: '长难句拆解 3 讲', continuousDays: 2 },
  { id: 1005, userId: 1002, userName: '李娜', checkinDate: '2026-11-13', checkinTime: '09:20:11', duration: 40, content: '写作素材整理', continuousDays: 12 },
  { id: 1006, userId: 1003, userName: '王强', checkinDate: '2026-11-13', checkinTime: '06:58:40', duration: 55, content: '真题听力两套', continuousDays: 8 },
  { id: 1007, userId: 1004, userName: '刘洋', checkinDate: '2026-11-12', checkinTime: '19:30:05', duration: 25, content: '词汇复习', continuousDays: 3 }
]

export const CLASSROOM_MESSAGES = [
  { id: 1101, classroomId: 1, senderId: 2001, senderName: '王老师', content: '同学们好，今天我们练习 Part 2 的三分钟结构。', messageType: 'TEXT', sendTime: '2026-11-13 08:00:12' },
  { id: 1102, classroomId: 1, senderId: 1001, senderName: '张伟', content: '老师，Part 2 的卡片有提示词吗？', messageType: 'TEXT', sendTime: '2026-11-13 08:02:41' },
  { id: 1103, classroomId: 1, senderId: 2001, senderName: '王老师', content: '有的，题目下方会给出 3 个追问点，请务必覆盖全部。', messageType: 'TEXT', sendTime: '2026-11-13 08:03:15' },
  { id: 1104, classroomId: 1, senderId: 1002, senderName: '李娜', content: '三分钟是不是要讲满？', messageType: 'TEXT', sendTime: '2026-11-13 08:04:02' },
  { id: 1105, classroomId: 1, senderId: 2001, senderName: '王老师', content: '建议讲到 2 分 30 秒以上，留出时间总结。', messageType: 'TEXT', sendTime: '2026-11-13 08:04:38' },
  { id: 1106, classroomId: 1, senderId: 1003, senderName: '王强', content: '收到，谢谢老师！', messageType: 'TEXT', sendTime: '2026-11-13 08:05:10' }
]

export const CLASSROOM_QUESTIONS = [
  { id: 1201, classroomId: 1, username: '张伟', avatar: '', content: 'Part 2 的三分钟结构应该怎么分配时间？', status: 'ANSWERED', likeCount: 6, answerCount: 2, createTime: '2026-11-13 08:06:20', answers: [
    { id: 1301, username: '王老师', avatar: '', content: '建议 30 秒读题与构思，2 分钟主体展开，30 秒总结。每段之间用 First / Another thing / To sum up 过渡。', isBest: true, createTime: '2026-11-13 08:07:05' },
    { id: 1302, username: '李娜', avatar: '', content: '补充一下：如果第一个观点展开不开，可以先说观点再举例，不要卡在构思上。', isBest: false, createTime: '2026-11-13 08:08:33' }
  ] },
  { id: 1202, classroomId: 1, username: '李娜', avatar: '', content: '考官更看重内容还是流利度？', status: 'OPEN', likeCount: 3, answerCount: 0, createTime: '2026-11-13 08:09:44', answers: [] },
  { id: 1203, classroomId: 1, username: '王强', avatar: '', content: '有没有推荐的万能理由？', status: 'OPEN', likeCount: 1, answerCount: 0, createTime: '2026-11-13 08:11:12', answers: [] }
]

export const CLASSROOM_POLLS = [
  { id: 1401, classroomId: 1, title: '今天的课堂重点你觉得是哪个？', pollType: 'SINGLE', anonymous: false, status: 'ACTIVE', totalVotes: 8, createTime: '2026-11-13 08:10:00', options: [
    { id: 1, content: '三分钟结构的时间分配', voteCount: 5 },
    { id: 2, content: '追问点的展开技巧', voteCount: 2 },
    { id: 3, content: '衔接词的使用', voteCount: 1 }
  ] },
  { id: 1402, classroomId: 1, title: '你更希望增加哪类练习？', pollType: 'MULTIPLE', anonymous: true, status: 'CLOSED', totalVotes: 12, createTime: '2026-11-13 08:20:00', options: [
    { id: 1, content: '一对一口语陪练', voteCount: 7 },
    { id: 2, content: '限时答题', voteCount: 4 },
    { id: 3, content: '范文精读', voteCount: 6 }
  ] }
]

export const CLASSROOM_SIGN_INS = [
  { id: 1501, classroomId: 1, userId: 1001, userName: '张伟', signInTime: '2026-11-13 08:01:05', status: 'SIGNED' },
  { id: 1502, classroomId: 1, userId: 1002, userName: '李娜', signInTime: '2026-11-13 08:01:22', status: 'SIGNED' },
  { id: 1503, classroomId: 1, userId: 1003, userName: '王强', signInTime: '2026-11-13 08:02:10', status: 'SIGNED' },
  { id: 1504, classroomId: 1, userId: 1004, userName: '刘洋', signInTime: '', status: 'ABSENT' }
]

export const COMMUNITY_REPLIES = [
  { id: 1601, postId: 7001, userId: 1002, userName: '李娜', username: '李娜', avatar: '', content: '补充一点：would rather 后面接动词原形，不能用过去式。', likeCount: 8, isLiked: false, createTime: '2026-11-12 21:30:00' },
  { id: 1602, postId: 7001, userId: 1003, userName: '王强', username: '王强', avatar: '', content: '同意，顺便提醒 wish + had done 结构里谓语要用过去分词。', likeCount: 5, isLiked: false, createTime: '2026-11-12 22:10:00' },
  { id: 1603, postId: 7002, userId: 1001, userName: '张伟', username: '张伟', avatar: '', content: '这个结构我总是混，老师讲的那张对照表很有用。', likeCount: 3, isLiked: false, createTime: '2026-11-11 19:05:00' }
]

export const FOLLOWED_USERS = [1002, 1003]
