<template>
  <div class="login-container">
    <!-- 左侧装饰区域 -->
    <div class="login-banner">
      <div class="banner-content">
        <div class="logo">
          <img v-if="systemLogo" :src="getFullUrl(systemLogo)" alt="Logo" class="logo-img" />
          <div v-else class="logo-mark">
            <el-icon :size="30"><Reading /></el-icon>
          </div>
          <div class="logo-text">
            <h1>{{ systemName || '英语等级智能学习系统' }}</h1>
            <p class="slogan">AI 辅助的英语教学与学习平台</p>
          </div>
        </div>
        <div class="features">
          <div class="feature-item">
            <el-icon><Check /></el-icon>
            <span>智能题库，海量真题</span>
          </div>
          <div class="feature-item">
            <el-icon><Check /></el-icon>
            <span>AI 辅导，个性化学习</span>
          </div>
          <div class="feature-item">
            <el-icon><Check /></el-icon>
            <span>在线考试，实时监控</span>
          </div>
          <div class="feature-item">
            <el-icon><Check /></el-icon>
            <span>错题溯源，逐题讲透</span>
          </div>
        </div>
        <div class="banner-tags">
          <span>AI 写作批改</span>
          <span>班级学情简报</span>
          <span>个性化学习计划</span>
        </div>
      </div>
    </div>

    <!-- 右侧登录表单 -->
    <div class="login-form-wrapper">
      <div class="login-form-container">
        <div class="form-header">
          <h2>欢迎登录</h2>
          <p>请输入您的账号信息</p>
        </div>

        <!-- 登录方式切换：当前以账号密码登录为主 -->
        <el-form
          ref="accountFormRef"
          :model="accountForm"
          :rules="accountRules"
          class="login-form"
        >
          <el-form-item prop="username">
            <el-input
              v-model="accountForm.username"
              placeholder="请输入用户名/邮箱/手机号"
              size="large"
              :prefix-icon="User"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="accountForm.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              :prefix-icon="Lock"
              show-password
              @keyup.enter="handleAccountLogin"
            />
          </el-form-item>
          <div class="form-options">
            <el-checkbox v-model="rememberMe">记住我</el-checkbox>
            <router-link to="/forgot-password" class="forgot-link">忘记密码？</router-link>
          </div>
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              class="login-btn"
              :loading="loading"
              @click="handleAccountLogin"
            >
              登 录
            </el-button>
          </el-form-item>
        </el-form>

        <!-- 注册引导 -->
        <div class="register-guide">
          还没有账号？
          <router-link to="/register">立即注册</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Reading, Check } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { redirectAfterLogin } from '@/router'
import { adminApi } from '@/api/admin'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 系统配置
const systemLogo = ref('')
const systemName = ref('')

const getFullUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
  return `${baseUrl}${url}`
}

// 登录状态
const loading = ref(false)
const rememberMe = ref(false)

// 账号密码表单
const accountFormRef = ref()
const accountForm = reactive({
  username: '',
  password: ''
})

const accountRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

// 检查是否已登录
onMounted(() => {
  // 加载系统配置
  adminApi.getPublicConfig().then(res => {
    systemLogo.value = res.data?.systemLogo || ''
    systemName.value = res.data?.systemName || ''
  }).catch(() => {
    // 使用默认配置
    systemLogo.value = ''
    systemName.value = '英语等级智能学习系统'
  })

  if (userStore.isLoggedIn) {
    const redirect = route.query.redirect || redirectAfterLogin(userStore.userRole)
    router.push(redirect)
  }
})

// 账号密码登录
const handleAccountLogin = async () => {
  if (!accountFormRef.value) return
  await accountFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await userStore.login({
          username: accountForm.username,
          password: accountForm.password
        })
        ElMessage.success('登录成功')

        // 根据角色跳转
        const redirect = route.query.redirect || redirectAfterLogin(userStore.userRole)
        router.push(redirect)
      } catch (error) {
        console.error('登录失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}

</script>

<style lang="scss" scoped>
.login-container {
  display: flex;
  min-height: 100vh;
  position: relative;
  overflow: hidden;
  background: #f6f9ff;

  // 细网格 + 顶部光斑：与内页的科技感底纹保持一致
  &::before {
    content: '';
    position: absolute;
    inset: 0;
    background-image:
      linear-gradient(rgba(37, 99, 235, 0.055) 1px, transparent 1px),
      linear-gradient(90deg, rgba(37, 99, 235, 0.055) 1px, transparent 1px);
    background-size: 44px 44px;
    mask-image: radial-gradient(circle at 50% 40%, #000 0%, transparent 78%);
    pointer-events: none;
  }

  &::after {
    content: '';
    position: absolute;
    inset: 0;
    background:
      radial-gradient(680px 460px at 12% 18%, rgba(37, 99, 235, 0.1), transparent 62%),
      radial-gradient(560px 420px at 88% 82%, rgba(14, 165, 233, 0.1), transparent 62%);
    pointer-events: none;
  }
}

.login-banner {
  flex: 1.1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
  position: relative;
  z-index: 1;

  @media (max-width: 992px) {
    display: none;
  }

  .banner-content {
    color: #16233f;
    max-width: 460px;
    position: relative;
    z-index: 2;

    .logo {
      display: flex;
      align-items: center;
      gap: 14px;
      margin-bottom: 34px;

      .logo-img {
        width: 52px;
        height: 52px;
        object-fit: contain;
        border-radius: 14px;
      }

      .logo-mark {
        width: 52px;
        height: 52px;
        border-radius: 15px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;
        background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 55%, #0ea5e9 100%);
        box-shadow: 0 12px 26px -12px rgba(37, 99, 235, 0.75);
        flex: none;
      }

      .logo-text {
        min-width: 0;

        h1 {
          font-size: 26px;
          font-weight: 800;
          letter-spacing: -0.4px;
          line-height: 1.25;
          background: linear-gradient(92deg, #0f1f3d 10%, #2563eb 60%, #0ea5e9 100%);
          -webkit-background-clip: text;
          background-clip: text;
          color: transparent;
        }

        .slogan {
          margin: 7px 0 0;
          font-size: 12.5px;
          color: #7c8ca8;
          letter-spacing: 0.2px;
        }
      }
    }

    .features {
      .feature-item {
        display: inline-flex;
        align-items: center;
        gap: 10px;
        margin: 0 10px 12px 0;
        padding: 9px 15px 9px 10px;
        font-size: 13px;
        color: #334560;
        border-radius: 12px;
        background: rgba(255, 255, 255, 0.82);
        border: 1px solid rgba(37, 99, 235, 0.12);
        box-shadow: 0 10px 24px -22px rgba(16, 38, 84, 0.7);
        backdrop-filter: blur(6px);

        .el-icon {
          width: 20px;
          height: 20px;
          border-radius: 7px;
          color: #fff;
          background: linear-gradient(135deg, #2563eb, #38bdf8);
          font-size: 12px;
          flex: none;
        }
      }
    }

    .banner-tags {
      margin-top: 22px;
      display: flex;
      gap: 8px;
      flex-wrap: wrap;

      span {
        font-size: 11px;
        padding: 4px 10px;
        border-radius: 20px;
        color: #2563eb;
        background: rgba(37, 99, 235, 0.08);
        border: 1px solid rgba(37, 99, 235, 0.16);
      }
    }

    .banner-illustration {
      display: none;
    }
  }
}

.login-form-wrapper {
  flex: 0.9;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  position: relative;
  z-index: 1;

  @media (max-width: 992px) {
    background: transparent;
  }
}

.login-form-container {
  width: 100%;
  max-width: 430px;
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(37, 99, 235, 0.12);
  border-radius: 20px;
  padding: 40px 36px;
  box-shadow:
    0 1px 0 rgba(255, 255, 255, 0.9) inset,
    0 34px 70px -46px rgba(16, 38, 84, 0.55),
    0 2px 10px -6px rgba(16, 38, 84, 0.16);
  backdrop-filter: blur(12px);

  :deep(.el-input__wrapper) {
    border-radius: 10px;
  }

  :deep(.el-checkbox__inner) {
    border-color: #c3d0e4;
  }

  :deep(.el-checkbox__label) {
    color: #5b6f92;
    font-size: 13px;
  }

  .form-header {
    text-align: center;
    margin-bottom: 32px;

    h2 {
      font-size: 24px;
      font-weight: 600;
      color: var(--text-primary);
      margin-bottom: 8px;
    }

    p {
      color: var(--text-secondary);
    }
  }

  .login-tabs {
    display: flex;
    margin-bottom: 24px;
    border-bottom: 1px solid var(--border-color);

    .tab-item {
      flex: 1;
      text-align: center;
      padding: 12px 0;
      cursor: pointer;
      color: var(--text-secondary);
      font-size: 14px;
      transition: all 0.3s;
      position: relative;

      &:hover {
        color: var(--primary-color);
      }

      &.active {
        color: var(--primary-color);
        font-weight: 500;

        &::after {
          content: '';
          position: absolute;
          bottom: -1px;
          left: 50%;
          transform: translateX(-50%);
          width: 40px;
          height: 2px;
          background: var(--primary-color);
          border-radius: 1px;
        }
      }
    }
  }

  .login-form {
    .el-form-item {
      margin-bottom: 20px;
    }

    .form-options {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 24px;

      .forgot-link {
        font-size: 14px;
        color: var(--primary-color);

        &:hover {
          color: var(--primary-light);
        }
      }
    }

    .code-input {
      display: flex;
      gap: 12px;

      .el-input {
        flex: 1;
      }

      .el-button {
        width: 120px;
      }
    }

    .login-btn {
      width: 100%;
      height: 44px;
      font-size: 16px;
    }
  }

  .third-party-login {
    margin-top: 32px;

    .divider {
      display: flex;
      align-items: center;
      margin-bottom: 24px;

      &::before,
      &::after {
        content: '';
        flex: 1;
        height: 1px;
        background: var(--border-color);
      }

      span {
        padding: 0 16px;
        color: var(--text-secondary);
        font-size: 12px;
      }
    }

    .third-party-icons {
      display: flex;
      justify-content: center;
      gap: 24px;

      .icon-item {
        width: 44px;
        height: 44px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        transition: all 0.3s;

        &.wechat {
          background: #07c160;
          color: #fff;

          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(7, 193, 96, 0.4);
          }
        }

        &.qq {
          background: #12b7f5;
          color: #fff;

          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(18, 183, 245, 0.4);
          }
        }
      }
    }
  }

  .register-guide {
    text-align: center;
    margin-top: 24px;
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
