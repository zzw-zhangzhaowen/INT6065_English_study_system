<template>
  <div class="error-page">
    <div class="error-content">
      <div class="error-code">403</div>
      <h2>无权限访问</h2>
      <p>抱歉，您没有权限访问此页面</p>
      <div class="error-actions">
        <el-button @click="goBack">返回上一页</el-button>
        <el-button type="primary" @click="goHome">返回首页</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goBack = () => {
  router.back()
}

const goHome = () => {
  router.push('/login')
}
</script>

<style lang="scss" scoped>
.error-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-secondary);
}

.error-content {
  text-align: center;

  .error-code {
    font-size: 120px;
    font-weight: 700;
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    line-height: 1;
    margin-bottom: 24px;
  }

  h2 {
    font-size: 24px;
    font-weight: 600;
    color: var(--text-primary);
    margin-bottom: 12px;
  }

  p {
    color: var(--text-secondary);
    margin-bottom: 32px;
  }

  .error-actions {
    display: flex;
    gap: 16px;
    justify-content: center;
  }
}
</style>
