<template>
  <div class="forgot-password-container">
    <div class="form-wrapper">
      <div class="form-card">
        <div class="form-header">
          <router-link to="/login" class="back-link">
            <el-icon><ArrowLeft /></el-icon>
            返回登录
          </router-link>
          <h2>忘记密码</h2>
          <p>请输入您的邮箱，我们将发送重置密码链接</p>
        </div>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          class="forgot-form"
        >
          <el-form-item prop="email">
            <el-input
              v-model="form.email"
              placeholder="请输入注册邮箱"
              size="large"
              :prefix-icon="Message"
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
              发送重置链接
            </el-button>
          </el-form-item>
        </el-form>

        <!-- 发送成功提示 -->
        <div v-if="sent" class="success-tip">
          <el-icon :size="48" color="#52c41a"><CircleCheck /></el-icon>
          <h3>邮件已发送</h3>
          <p>重置密码链接已发送至 <strong>{{ form.email }}</strong></p>
          <p class="sub-tip">请在30分钟内完成密码重置</p>
          <el-button type="primary" link @click="resend">没收到邮件？重新发送</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Message, ArrowLeft, CircleCheck } from '@element-plus/icons-vue'
import { authApi } from '@/api/auth'

const formRef = ref()
const loading = ref(false)
const sent = ref(false)

const form = reactive({
  email: ''
})

const rules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await authApi.sendResetCode(form.email)
        sent.value = true
        ElMessage.success('重置邮件已发送')
      } catch (error) {
        ElMessage.error(error.message || '发送失败，请重试')
      } finally {
        loading.value = false
      }
    }
  })
}

const resend = () => {
  sent.value = false
  handleSubmit()
}
</script>

<style lang="scss" scoped>
.forgot-password-container {
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
    margin-bottom: 32px;

    .back-link {
      display: inline-flex;
      align-items: center;
      gap: 4px;
      color: var(--text-secondary);
      font-size: 14px;
      margin-bottom: 24px;

      &:hover {
        color: var(--primary-color);
      }
    }

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

  .forgot-form {
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
      margin: 16px 0 12px;
    }

    p {
      color: var(--text-secondary);
      margin-bottom: 8px;

      strong {
        color: var(--text-primary);
      }
    }

    .sub-tip {
      font-size: 13px;
      color: var(--text-placeholder);
      margin-bottom: 24px;
    }
  }
}
</style>
