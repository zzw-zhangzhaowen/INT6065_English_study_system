<template>
  <div class="error-page">
    <div class="error-content">
      <div class="error-code">404</div>
      <h2>页面不存在</h2>
      <p>抱歉，您访问的页面不存在或已被移除</p>
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
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
