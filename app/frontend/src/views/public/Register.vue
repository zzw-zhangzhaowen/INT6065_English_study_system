<template>
  <div class="register-container">
    <!-- 左侧装饰区域 -->
    <div class="register-banner">
      <div class="banner-content">
        <div class="logo">
          <img v-if="systemLogo" :src="getFullUrl(systemLogo)" alt="Logo" class="logo-img" />
          <el-icon v-else :size="48"><Reading /></el-icon>
          <h1>{{ systemName || '英语等级智能学习系统' }}</h1>
        </div>
        <div class="welcome-text">
          <h2>加入我们</h2>
          <p>开启您的英语学习之旅</p>
        </div>
      </div>
    </div>

    <!-- 右侧注册表单 -->
    <div class="register-form-wrapper">
      <div class="register-form-container">
        <!-- 步骤指示器 -->
        <el-steps :active="currentStep" align-center class="register-steps">
          <el-step title="选择身份" />
          <el-step title="填写信息" />
          <el-step title="验证邮箱" />
          <el-step title="完成注册" />
        </el-steps>

        <!-- 步骤1：选择用户类型 -->
        <div v-if="currentStep === 0" class="step-content">
          <h3>请选择您的身份</h3>
          <div class="role-cards">
            <div
              class="role-card"
              :class="{ active: registerForm.role === 'student' }"
              @click="registerForm.role = 'student'"
            >
              <div class="role-icon student">
                <el-icon :size="40"><User /></el-icon>
              </div>
              <h4>学生</h4>
              <p>参加考试、学习课程、使用AI辅导</p>
            </div>
            <div
              class="role-card"
              :class="{ active: registerForm.role === 'teacher' }"
              @click="registerForm.role = 'teacher'"
            >
              <div class="role-icon teacher">
                <el-icon :size="40"><Avatar /></el-icon>
              </div>
              <h4>教师</h4>
              <p>管理题库、组织考试、在线教学</p>
            </div>
          </div>
          <el-button type="primary" size="large" class="next-btn" @click="nextStep">
            下一步
          </el-button>
        </div>

        <!-- 步骤2：填写基本信息 -->
        <div v-if="currentStep === 1" class="step-content">
          <h3>填写基本信息</h3>
          <el-form
            ref="infoFormRef"
            :model="registerForm"
            :rules="infoRules"
            label-position="top"
            class="info-form"
          >
            <el-form-item label="用户名" prop="username">
              <el-input
                v-model="registerForm.username"
                placeholder="请输入用户名（4-20位字母数字）"
                size="large"
                :prefix-icon="User"
              />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input
                v-model="registerForm.email"
                placeholder="请输入邮箱地址"
                size="large"
                :prefix-icon="Message"
              />
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input
                v-model="registerForm.phone"
                placeholder="请输入手机号"
                size="large"
                :prefix-icon="Iphone"
              />
            </el-form-item>
            <el-form-item label="密码" prop="password">
              <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="请输入密码（至少6位）"
                size="large"
                :prefix-icon="Lock"
                show-password
              />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                placeholder="请再次输入密码"
                size="large"
                :prefix-icon="Lock"
                show-password
              />
            </el-form-item>

            <!-- 教师额外信息 -->
            <template v-if="registerForm.role === 'teacher'">
              <el-form-item label="真实姓名" prop="realName">
                <el-input
                  v-model="registerForm.realName"
                  placeholder="请输入真实姓名"
                  size="large"
                />
              </el-form-item>
              <el-form-item label="所属机构" prop="institution">
                <el-input
                  v-model="registerForm.institution"
                  placeholder="请输入学校或培训机构名称"
                  size="large"
                />
              </el-form-item>
            </template>
          </el-form>
          <div class="step-buttons">
            <el-button size="large" @click="prevStep">上一步</el-button>
            <el-button type="primary" size="large" @click="nextStep">下一步</el-button>
          </div>
        </div>

        <!-- 步骤3：验证邮箱 -->
        <div v-if="currentStep === 2" class="step-content">
          <h3>验证邮箱</h3>
          <div class="verify-email">
            <div class="email-icon">
              <el-icon :size="64" color="#4080ff"><Message /></el-icon>
            </div>
            <p class="verify-tip">
              验证码已发送至 <strong>{{ registerForm.email }}</strong>
            </p>
            <el-form ref="codeFormRef" :model="registerForm" :rules="codeRules" class="code-form">
              <el-form-item prop="verifyCode">
                <div class="code-input-group">
                  <el-input
                    v-model="registerForm.verifyCode"
                    placeholder="请输入6位验证码"
                    size="large"
                    maxlength="6"
                  />
                  <el-button
                    size="large"
                    :disabled="countdown > 0"
                    @click="resendCode"
                  >
                    {{ countdown > 0 ? `${countdown}s后重发` : '重新发送' }}
                  </el-button>
                </div>
              </el-form-item>
            </el-form>
          </div>
          <div class="step-buttons">
            <el-button size="large" @click="prevStep">上一步</el-button>
            <el-button type="primary" size="large" :loading="loading" @click="verifyEmail">
              验证并注册
            </el-button>
          </div>
        </div>

        <!-- 步骤4：完成注册 -->
        <div v-if="currentStep === 3" class="step-content">
          <div class="success-content">
            <div class="success-icon">
              <el-icon :size="80" color="#52c41a"><CircleCheck /></el-icon>
            </div>
            <h3>注册成功！</h3>
            <p v-if="registerForm.role === 'student'">
              您的账号已创建成功，现在可以开始学习了
            </p>
            <p v-else>
              您的教师账号申请已提交，请等待管理员审核
            </p>
            <el-button type="primary" size="large" @click="goToLogin">
              {{ registerForm.role === 'student' ? '立即登录' : '返回登录' }}
            </el-button>
          </div>
        </div>

        <!-- 登录引导 -->
        <div v-if="currentStep < 3" class="login-guide">
          已有账号？
          <router-link to="/login">立即登录</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Iphone, Message, Reading, Avatar, CircleCheck } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { adminApi } from '@/api/admin'

const router = useRouter()
const userStore = useUserStore()

const currentStep = ref(0)
const loading = ref(false)
const countdown = ref(0)
const systemLogo = ref('')
const systemName = ref('')

const getFullUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
  return `${baseUrl}${url}`
}

onMounted(() => {
  adminApi.getPublicConfig().then(res => {
    systemLogo.value = res.data?.systemLogo || ''
    systemName.value = res.data?.systemName || ''
  }).catch(() => {})
})

const infoFormRef = ref()
const codeFormRef = ref()

const registerForm = reactive({
  role: 'student',
  username: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: '',
  realName: '',
  institution: '',
  verifyCode: ''
})

// 验证密码一致性
const validateConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const infoRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 4, max: 20, message: '用户名长度为4-20位', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  institution: [
    { required: true, message: '请输入所属机构', trigger: 'blur' }
  ]
}

const codeRules = {
  verifyCode: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码为6位', trigger: 'blur' }
  ]
}

const nextStep = async () => {
  if (currentStep.value === 0) {
    if (!registerForm.role) {
      ElMessage.warning('请选择您的身份')
      return
    }
    currentStep.value++
  } else if (currentStep.value === 1) {
    if (!infoFormRef.value) return
    await infoFormRef.value.validate(async (valid) => {
      if (valid) {
        // 直接进行注册，跳过邮箱验证步骤
        await doRegister()
      }
    })
  }
}

const prevStep = () => {
  currentStep.value--
}

const sendVerifyCode = () => {
  ElMessage.info('邮箱验证功能开发中')
  countdown.value = 60
  const timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(timer)
    }
  }, 1000)
}

const resendCode = () => {
  sendVerifyCode()
}

// 执行注册
const doRegister = async () => {
  loading.value = true
  try {
    await userStore.register({
      username: registerForm.username,
      password: registerForm.password,
      email: registerForm.email,
      phone: registerForm.phone,
      realName: registerForm.realName,
      roleType: registerForm.role.toUpperCase()
    })
    ElMessage.success('注册成功')
    // 跳到完成步骤
    currentStep.value = 3
  } catch (error) {
    console.error('注册失败:', error)
  } finally {
    loading.value = false
  }
}

const verifyEmail = async () => {
  if (!codeFormRef.value) return
  await codeFormRef.value.validate(async (valid) => {
    if (valid) {
      await doRegister()
    }
  })
}

const goToLogin = () => {
  router.push('/login')
}
</script>

<style lang="scss" scoped>
.register-container {
  display: flex;
  min-height: 100vh;
  background: var(--bg-primary);
}

.register-banner {
  flex: 0 0 400px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;

  @media (max-width: 992px) {
    display: none;
  }

  .banner-content {
    color: #fff;
    text-align: center;

    .logo {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 16px;
      margin-bottom: 60px;

      .logo-img {
        width: 48px;
        height: 48px;
        object-fit: contain;
        border-radius: 8px;
      }

      h1 {
        font-size: 24px;
        font-weight: 600;
      }
    }

    .welcome-text {
      h2 {
        font-size: 32px;
        font-weight: 600;
        margin-bottom: 12px;
      }

      p {
        font-size: 16px;
        opacity: 0.9;
      }
    }
  }
}

.register-form-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  background: var(--bg-secondary);
  overflow-y: auto;
}

.register-form-container {
  width: 100%;
  max-width: 560px;
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  padding: 48px 40px;
  box-shadow: var(--shadow-lg);

  .register-steps {
    margin-bottom: 40px;

    :deep(.el-step__title) {
      font-size: 13px;
    }
  }

  .step-content {
    h3 {
      text-align: center;
      font-size: 20px;
      font-weight: 600;
      color: var(--text-primary);
      margin-bottom: 32px;
    }
  }

  .role-cards {
    display: flex;
    gap: 24px;
    margin-bottom: 32px;

    .role-card {
      flex: 1;
      padding: 32px 24px;
      border: 2px solid var(--border-color);
      border-radius: var(--radius-lg);
      text-align: center;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        border-color: var(--primary-light);
      }

      &.active {
        border-color: var(--primary-color);
        background: rgba(64, 128, 255, 0.05);
      }

      .role-icon {
        width: 80px;
        height: 80px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        margin: 0 auto 16px;

        &.student {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          color: #fff;
        }

        &.teacher {
          background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
          color: #fff;
        }
      }

      h4 {
        font-size: 18px;
        font-weight: 600;
        color: var(--text-primary);
        margin-bottom: 8px;
      }

      p {
        font-size: 13px;
        color: var(--text-secondary);
      }
    }
  }

  .next-btn {
    width: 100%;
    height: 44px;
    font-size: 16px;
  }

  .info-form {
    .el-form-item {
      margin-bottom: 20px;
    }
  }

  .step-buttons {
    display: flex;
    gap: 16px;
    margin-top: 32px;

    .el-button {
      flex: 1;
      height: 44px;
    }
  }

  .verify-email {
    text-align: center;

    .email-icon {
      margin-bottom: 24px;
    }

    .verify-tip {
      color: var(--text-secondary);
      margin-bottom: 32px;

      strong {
        color: var(--text-primary);
      }
    }

    .code-form {
      max-width: 400px;
      margin: 0 auto;

      .code-input-group {
        display: flex;
        gap: 12px;

        .el-input {
          flex: 1;
        }

        .el-button {
          width: 120px;
        }
      }
    }
  }

  .success-content {
    text-align: center;
    padding: 40px 0;

    .success-icon {
      margin-bottom: 24px;
    }

    h3 {
      font-size: 24px;
      font-weight: 600;
      color: var(--text-primary);
      margin-bottom: 12px;
    }

    p {
      color: var(--text-secondary);
      margin-bottom: 32px;
    }

    .el-button {
      width: 200px;
      height: 44px;
    }
  }

  .login-guide {
    text-align: center;
    margin-top: 32px;
    padding-top: 24px;
    border-top: 1px solid var(--border-color);
    color: var(--text-secondary);
    font-size: 14px;

    a {
      color: var(--primary-color);
      font-weight: 500;

      &:hover {
        color: var(--primary-light);
      }
    }
  }
}
</style>
