import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  // 侧边栏折叠状态
  const sidebarCollapsed = ref(false)

  // 全局加载状态
  const loading = ref(false)

  // 全局错误信息
  const errorMessage = ref('')

  // 主题设置
  const theme = ref(localStorage.getItem('theme') || 'light')

  // 切换侧边栏
  function toggleSidebar() {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }

  // 设置加载状态
  function setLoading(status) {
    loading.value = status
  }

  // 设置错误信息
  function setError(message) {
    errorMessage.value = message
  }

  // 清除错误信息
  function clearError() {
    errorMessage.value = ''
  }

  // 切换主题
  function toggleTheme() {
    theme.value = theme.value === 'light' ? 'dark' : 'light'
    localStorage.setItem('theme', theme.value)
    document.documentElement.setAttribute('data-theme', theme.value)
  }

  // 设置主题
  function setTheme(newTheme) {
    theme.value = newTheme
    localStorage.setItem('theme', newTheme)
    document.documentElement.setAttribute('data-theme', newTheme)
  }

  return {
    sidebarCollapsed,
    loading,
    errorMessage,
    theme,
    toggleSidebar,
    setLoading,
    setError,
    clearError,
    toggleTheme,
    setTheme
  }
})
