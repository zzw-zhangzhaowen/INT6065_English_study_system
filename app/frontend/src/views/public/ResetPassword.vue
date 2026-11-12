<template>
  <div class="reset-password-container">
    <div class="form-wrapper">
      <div class="form-card">
        <div class="form-header">
          <h2>重置密码</h2>
          <p>请设置您的新密码</p>
        </div>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          class="reset-form"
        >
          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入新密码（至少6位）"
              size="large"
              :prefix-icon="Lock"
              show-password
            />
          </el-form-item>
          <el-form-item prop="confirmPassword">
            <el-input
              v-model="form.confirmPassword"
              type="password"
              placeholder="请再次输入新密码"
              size="large"
              :prefix-icon="Lock"
              show-password
            />
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              class="submit-btn"
              :loading="loading"
              @click="handleSubmit"
            >
              确认重置
            </el-button>
          </el-form-item>
        </el-form>

        <!-- 重置成功提示 -->
        <div v-if="success" class="success-tip">
          <el-icon :size="64" color="#52c41a"><CircleCheck /></el-icon>
          <h3>密码重置成功</h3>
          <p>您的密码已成功重置，请使用新密码登录</p>
          <el-button type="primary" size="large" @click="goToLogin">
            立即登录
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Lock, CircleCheck } from '@element-plus/icons-vue'
import { authApi } from '@/api/auth'

const router = useRouter()
const route = useRoute()

const formRef = ref()
const loading = ref(false)
const success = ref(false)

const form = reactive({
  password: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  password: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const token = route.params.token || route.query.token
        const email = route.query.email
        await authApi.resetPassword({
          email,
          code: token,
          newPassword: form.password
        })
        success.value = true
        ElMessage.success('密码重置成功')
      } catch (error) {
        ElMessage.error(error.message || '重置失败，链接可能已过期')
      } finally {
        loading.value = false
      }
    }
  })
}

const goToLogin = () => {
  router.push('/login')
}
</script>

<style lang="scss" scoped>
.reset-password-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.form-wrapper {
  width: 100%;
  max-width: 440px;
}

.form-card {
  background: var(--bg-primary);
  border-radius: var(--radius-xl);
  padding: 48px 40px;
  box-shadow: var(--shadow-lg);

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
      font-size: 14px;
    }
  }

  .reset-form {
    .el-form-item {
      margin-bottom: 24px;
    }

    .submit-btn {
      width: 100%;
      height: 44px;
      font-size: 16px;
    }
  }

  .success-tip {
    text-align: center;
    padding: 20px 0;

    h3 {
      font-size: 20px;
      font-weight: 600;
      color: var(--text-primary);
      margin: 24px 0 12px;
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
}
</style>
