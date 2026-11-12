import { createRouter, createWebHashHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  // 公共页面
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/public/Login.vue'),
    meta: { title: '登录', public: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/public/Register.vue'),
    meta: { title: '注册', public: true }
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: () => import('@/views/public/ForgotPassword.vue'),
    meta: { title: '忘记密码', public: true }
  },
  {
    path: '/reset-password/:token',
    name: 'ResetPassword',
    component: () => import('@/views/public/ResetPassword.vue'),
    meta: { title: '重置密码', public: true }
  },

  // 学生端
  {
    path: '/student',
    component: () => import('@/layouts/StudentLayout.vue'),
    redirect: '/student/dashboard',
    meta: { requiresAuth: true, role: 'student' },
    children: [
      {
        path: 'dashboard',
        name: 'StudentDashboard',
        component: () => import('@/views/student/Dashboard.vue'),
        meta: { title: '学习仪表板', role: 'student' }
      },
      {
        path: 'exam',
        name: 'StudentExamList',
        component: () => import('@/views/student/exam/ExamList.vue'),
        meta: { title: '考试中心', role: 'student' }
      },
      {
        path: 'exam/detail/:id',
        name: 'StudentExamDetail',
        component: () => import('@/views/student/exam/ExamDetail.vue'),
        meta: { title: '考试详情', role: 'student' }
      },
      {
        path: 'exam/room/:id',
        name: 'StudentExamRoom',
        component: () => import('@/views/student/exam/ExamRoom.vue'),
        meta: { title: '在线考场', role: 'student', fullscreen: true }
      },
      {
        path: 'exam/result/:id',
        name: 'StudentExamResult',
        component: () => import('@/views/student/exam/ExamResult.vue'),
        meta: { title: '考试结果', role: 'student' }
      },
      {
        path: 'exam/review/:id',
        name: 'StudentExamReview',
        component: () => import('@/views/student/exam/ExamReview.vue'),
        meta: { title: '试题回顾', role: 'student' }
      },
      {
        path: 'learning',
        name: 'StudentLearning',
        component: () => import('@/views/student/learning/LearningCenter.vue'),
        meta: { title: '学习中心', role: 'student' }
      },
      {
        path: 'learning/resource/:id',
        name: 'StudentResourceDetail',
        component: () => import('@/views/student/learning/ResourceDetail.vue'),
        meta: { title: '资源详情', role: 'student' }
      },
      {
        path: 'learning/note/new',
        name: 'StudentNoteNew',
        component: () => import('@/views/student/learning/NoteEdit.vue'),
        meta: { title: '新建笔记', role: 'student' }
      },
      {
        path: 'learning/note/:id',
        name: 'StudentNoteEdit',
        component: () => import('@/views/student/learning/NoteEdit.vue'),
        meta: { title: '编辑笔记', role: 'student' }
      },
      {
        path: 'learning/plan/:id',
        name: 'StudentPlanDetail',
        component: () => import('@/views/student/learning/PlanDetail.vue'),
        meta: { title: '计划详情', role: 'student' }
      },
      {
        path: 'learning/wrong',
        name: 'StudentWrongQuestions',
        component: () => import('@/views/student/learning/WrongQuestions.vue'),
        meta: { title: '错题本', role: 'student' }
      },
      {
        path: 'community',
        name: 'StudentCommunity',
        component: () => import('@/views/student/community/CommunityHome.vue'),
        meta: { title: '学习社区', role: 'student' }
      },
      {
        path: 'community/post/:id',
        name: 'StudentPostDetail',
        component: () => import('@/views/student/community/PostDetail.vue'),
        meta: { title: '帖子详情', role: 'student' }
      },
      {
        path: 'ai-tutor',
        name: 'StudentAITutor',
        component: () => import('@/views/student/ai/AITutorHome.vue'),
        meta: { title: 'AI智能辅导', role: 'student' }
      },
      {
        path: 'ai-tutor/chat',
        name: 'StudentAIChat',
        component: () => import('@/views/student/ai/AIChat.vue'),
        meta: { title: 'AI对话', role: 'student' }
      },
      {
        path: 'ai-tutor/writing',
        name: 'StudentAIWriting',
        component: () => import('@/views/student/ai/AIWriting.vue'),
        meta: { title: '作文批改', role: 'student' }
      },
      {
        path: 'ai-tutor/wrong',
        name: 'StudentAIWrong',
        component: () => import('@/views/student/ai/AIWrong.vue'),
        meta: { title: '错题溯源', role: 'student' }
      },
      {
        path: 'ai-tutor/profile',
        name: 'StudentAIProfile',
        component: () => import('@/views/student/ai/AIProfile.vue'),
        meta: { title: '学习画像', role: 'student' }
      },
      {
        path: 'ai-tutor/plan',
        name: 'StudentAIPlan',
        component: () => import('@/views/student/ai/AIPlan.vue'),
        meta: { title: '学习计划', role: 'student' }
      },
      {
        path: 'ai-tutor/sentence',
        name: 'StudentAILongSentence',
        component: () => import('@/views/student/ai/AILongSentence.vue'),
        meta: { title: '长难句拆解', role: 'student' }
      },
      {
        path: 'ai-tutor/vocab',
        name: 'StudentAIVocab',
        component: () => import('@/views/student/ai/AIVocab.vue'),
        meta: { title: '词汇速记', role: 'student' }
      },
      {
        path: 'ai-tutor/speaking',
        name: 'StudentAISpeaking',
        component: () => import('@/views/student/ai/AISpeaking.vue'),
        meta: { title: '口语陪练', role: 'student' }
      },
      {
        path: 'profile',
        name: 'StudentProfile',
        component: () => import('@/views/student/profile/ProfileHome.vue'),
        meta: { title: '个人中心', role: 'student' }
      },
      {
        path: 'checkin',
        name: 'StudentCheckin',
        component: () => import('@/views/student/checkin/DailyCheckin.vue'),
        meta: { title: '每日打卡', role: 'student' }
      },
      {
        path: 'search',
        name: 'StudentSearch',
        component: () => import('@/views/student/search/SearchResult.vue'),
        meta: { title: '搜索结果', role: 'student' }
      },
      {
        path: 'homework',
        name: 'StudentHomework',
        component: () => import('@/views/student/homework/StudentHomework.vue'),
        meta: { title: '我的作业', role: 'student' }
      },
      {
        path: 'message',
        name: 'StudentMessage',
        component: () => import('@/components/MessageCenter.vue'),
        meta: { title: '消息中心', role: 'student' }
      },
      {
        path: 'classroom',
        name: 'StudentClassroom',
        component: () => import('@/views/student/classroom/ClassroomHome.vue'),
        meta: { title: '在线课堂', role: 'student' }
      },
      {
        path: 'classroom/live/:id',
        name: 'StudentClassroomLive',
        component: () => import('@/views/student/classroom/ClassroomLive.vue'),
        meta: { title: '直播课堂', role: 'student', fullscreen: true }
      },
      {
        path: 'classroom/playback/:id',
        name: 'StudentClassroomPlayback',
        component: () => import('@/views/teacher/classroom/ClassroomPlayback.vue'),
        meta: { title: '课堂回放', role: 'student' }
      },
      {
        path: 'learning/practice/wrong',
        name: 'StudentWrongPractice',
        component: () => import('@/views/student/learning/WrongPractice.vue'),
        meta: { title: '错题练习', role: 'student' }
      },
      {
        path: 'learning/practice/similar/:id',
        name: 'StudentSimilarPractice',
        component: () => import('@/views/student/learning/WrongPractice.vue'),
        meta: { title: '相似题练习', role: 'student' }
      }
    ]
  },

  // 教师端
  {
    path: '/teacher',
    component: () => import('@/layouts/TeacherLayout.vue'),
    redirect: '/teacher/dashboard',
    meta: { requiresAuth: true, role: 'teacher' },
    children: [
      {
        path: 'dashboard',
        name: 'TeacherDashboard',
        component: () => import('@/views/teacher/Dashboard.vue'),
        meta: { title: '教学仪表板', role: 'teacher' }
      },
      {
        path: 'question',
        name: 'TeacherQuestion',
        component: () => import('@/views/teacher/question/QuestionHome.vue'),
        meta: { title: '题库管理', role: 'teacher' }
      },
      {
        path: 'exam',
        name: 'TeacherExam',
        component: () => import('@/views/teacher/exam/ExamManagement.vue'),
        meta: { title: '考试管理', role: 'teacher' }
      },
      {
        path: 'classroom',
        name: 'TeacherClassroom',
        component: () => import('@/views/teacher/classroom/ClassroomHome.vue'),
        meta: { title: '在线课堂', role: 'teacher' }
      },
      {
        path: 'classroom/live/:id',
        name: 'TeacherClassroomLive',
        component: () => import('@/views/teacher/classroom/ClassroomLive.vue'),
        meta: { title: '直播课堂', role: 'teacher', fullscreen: true }
      },
      {
        path: 'classroom/playback/:id',
        name: 'TeacherClassroomPlayback',
        component: () => import('@/views/teacher/classroom/ClassroomPlayback.vue'),
        meta: { title: '课堂回放', role: 'teacher' }
      },
      {
        path: 'resource',
        name: 'TeacherResource',
        component: () => import('@/views/teacher/resource/ResourceHome.vue'),
        meta: { title: '学习资源', role: 'teacher' }
      },
      {
        path: 'student',
        name: 'TeacherStudent',
        component: () => import('@/views/teacher/student/StudentManagement.vue'),
        meta: { title: '学生管理', role: 'teacher' }
      },
      {
        path: 'student/detail/:id',
        name: 'TeacherStudentDetail',
        component: () => import('@/views/teacher/student/StudentDetail.vue'),
        meta: { title: '学生详情', role: 'teacher' }
      },
      {
        path: 'homework',
        name: 'TeacherHomework',
        component: () => import('@/views/teacher/homework/HomeworkManagement.vue'),
        meta: { title: '作业管理', role: 'teacher' }
      },
      {
        path: 'ai-assistant',
        name: 'TeacherAIAssistant',
        component: () => import('@/views/teacher/ai/TeacherAIAssistant.vue'),
        meta: { title: 'AI 教学助手', role: 'teacher' }
      },
      {
        path: 'message',
        name: 'TeacherMessage',
        component: () => import('@/components/MessageCenter.vue'),
        meta: { title: '消息中心', role: 'teacher' }
      },
      {
        path: 'profile',
        name: 'TeacherProfile',
        component: () => import('@/views/teacher/profile/ProfileHome.vue'),
        meta: { title: '个人中心', role: 'teacher' }
      }
    ]
  },

  // 管理员端
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    meta: { requiresAuth: true, role: 'admin' },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '系统仪表板', role: 'admin' }
      },
      {
        path: 'user',
        name: 'AdminUser',
        component: () => import('@/views/admin/user/UserManagement.vue'),
        meta: { title: '用户管理', role: 'admin' }
      },
      {
        path: 'system',
        name: 'AdminSystem',
        component: () => import('@/views/admin/system/SystemConfig.vue'),
        meta: { title: '系统配置', role: 'admin' }
      },
      {
        path: 'log',
        name: 'AdminLog',
        component: () => import('@/views/admin/log/LogAudit.vue'),
        meta: { title: '日志审计', role: 'admin' }
      },
      {
        path: 'monitor',
        name: 'AdminMonitor',
        component: () => import('@/views/admin/monitor/SystemMonitor.vue'),
        meta: { title: '系统监控', role: 'admin' }
      },
      {
        path: 'backup',
        name: 'AdminBackup',
        component: () => import('@/views/admin/backup/BackupManagement.vue'),
        meta: { title: '系统备份', role: 'admin' }
      },
      {
        path: 'photo',
        name: 'AdminPhoto',
        component: () => import('@/views/admin/PhotoManagement.vue'),
        meta: { title: '图片管理', role: 'admin' }
      },
      {
        path: 'message',
        name: 'AdminMessage',
        component: () => import('@/components/MessageCenter.vue'),
        meta: { title: '消息中心', role: 'admin' }
      },
      {
        path: 'profile',
        name: 'AdminProfile',
        component: () => import('@/views/teacher/profile/ProfileHome.vue'),
        meta: { title: '个人中心', role: 'admin' }
      }
    ]
  },

  // 错误页面
  {
    path: '/403',
    name: 'Forbidden',
    component: () => import('@/views/public/403.vue'),
    meta: { title: '无权限访问', public: true }
  },
  {
    path: '/500',
    name: 'ServerError',
    component: () => import('@/views/public/500.vue'),
    meta: { title: '服务器错误', public: true }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/public/404.vue'),
    meta: { title: '页面不存在', public: true }
  },

  // 默认重定向
  {
    path: '/',
    redirect: '/login'
  }
]

const router = createRouter({
  // hash 模式：静态托管（GitHub Pages）和本地双击 index.html 都不需要服务端重写，
  // 刷新、直达子路由都不会 404
  history: createWebHashHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 英语等级智能学习与考试系统` : '英语等级智能学习与考试系统'

  // 公共页面直接放行
  if (to.meta.public) {
    next()
    return
  }

  // 获取用户状态
  const userStore = useUserStore()

  // 需要登录验证
  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!userStore.isLoggedIn) {
      // 未登录，跳转到登录页
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
      return
    }

    // 检查角色权限
    const requiredRole = to.meta.role || to.matched.find(record => record.meta.role)?.meta.role
    if (requiredRole) {
      const userRole = userStore.userRole

      // 管理员可以访问所有页面
      if (userRole === 'admin') {
        next()
        return
      }

      // 教师只能访问教师页面
      if (userRole === 'teacher' && requiredRole === 'teacher') {
        next()
        return
      }

      // 学生只能访问学生页面
      if (userRole === 'student' && requiredRole === 'student') {
        next()
        return
      }

      // 权限不足
      next('/403')
      return
    }
  }

  next()
})

// 登录后根据角色跳转
export function redirectAfterLogin(userRole) {
  switch (userRole) {
    case 'admin':
      return '/admin/dashboard'
    case 'teacher':
      return '/teacher/dashboard'
    default:
      return '/student/dashboard'
  }
}

export default router
