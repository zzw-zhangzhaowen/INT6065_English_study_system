import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { resolveMock } from '@/mock'
import router from '@/router'

// 演示模式开关：后端不可用时自动用本地模拟数据接管，保证页面可看可点
// 接上真实后端后设为 false 即可完全走真实接口
const MOCK_FALLBACK = true

// 纯前端演示模式（GitHub Pages / 双击 index.html 打开）：
// 构建时置 VITE_MOCK_ONLY=true，所有请求直接由本地模拟数据应答，
// 不发起任何网络请求，也不产生控制台报错。
const MOCK_ONLY = String(import.meta.env.VITE_MOCK_ONLY || '') === 'true'

// 创建axios实例
const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers['Authorization'] = `Bearer ${userStore.token}`
    }

    // 纯演示模式：用自定义 adapter 直接返回模拟数据，请求不出网
    if (MOCK_ONLY) {
      const mock = resolveMock(config)
      config.adapter = () =>
        Promise.resolve({
          data: mock ?? { code: 200, message: 'success', data: null },
          status: 200,
          statusText: 'OK',
          headers: {},
          config
        })
    }

    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 防止重复弹出401对话框
let isShowingRelogin = false

// 响应拦截器
service.interceptors.response.use(
  response => {
    // blob响应（文件下载）直接返回data，不做code检查
    if (response.config.responseType === 'blob') {
      return response.data
    }

    const res = response.data

    // 如果返回的code不是200，说明有错误
    if (res.code !== 200) {
      // 支持静默模式，不显示错误提示
      if (!response.config.silent) {
        // 401统一处理：弹出重新登录对话框
        if (res.code === 401) {
          if (!isShowingRelogin) {
            isShowingRelogin = true
            ElMessageBox.confirm(
              '登录状态已过期，请重新登录',
              '提示',
              {
                confirmButtonText: '重新登录',
                cancelButtonText: '取消',
                type: 'warning'
              }
            ).then(() => {
              const userStore = useUserStore()
              userStore.logout()
            }).finally(() => {
              isShowingRelogin = false
            })
          }
        } else {
          ElMessage({
            message: res.message || '请求失败',
            type: 'error',
            duration: 5000
          })
        }
      }

      return Promise.reject(new Error(res.message || '请求失败'))
    }

    return res
  },
  error => {
    // HTTP 401 统一处理：与业务层401保持一致
    if (error.response && error.response.status === 401) {
      if (!isShowingRelogin) {
        isShowingRelogin = true
        ElMessageBox.confirm(
          '登录状态已过期，请重新登录',
          '提示',
          {
            confirmButtonText: '重新登录',
            cancelButtonText: '取消',
            type: 'warning'
          }
        ).then(() => {
          const userStore = useUserStore()
          userStore.logout()
        }).finally(() => {
          isShowingRelogin = false
        })
      }
      return Promise.reject(error)
    }

    // ---------- 演示兜底：网络层失败 / 5xx / 超时 → 用本地模拟数据接管 ----------
    // 注意顺序：先尝试接管，接管成功就不再打 error 日志，
    // 否则控制台会先红一片再提示「已接管」，演示时容易被误判成报错。
    const status = error.response?.status
    const networkFail =
      !error.response || status >= 500 || error.code === 'ECONNABORTED' || error.code === 'ERR_NETWORK'
    if (MOCK_FALLBACK && networkFail) {
      const mock = resolveMock(error.config)
      if (mock) {
        if (!error.config?.silent) {
          console.info('[demo] 后端未连接，已使用本地模拟数据：', error.config?.url)
        }
        return Promise.resolve(mock)
      }
    }

    // 静默模式下不输出控制台错误日志
    if (!error.config || !error.config.silent) {
      console.error('响应错误:', error)
    }

    let message = '网络错误，请稍后重试'
    if (error.response) {
      switch (error.response.status) {
        case 400:
          message = '请求参数错误'
          break
        case 403:
          message = '拒绝访问'
          router.push('/403')
          break
        case 404:
          message = '请求的资源不存在'
          break
        case 500:
          message = '服务器内部错误'
          break
        default:
          message = error.response.data?.message || '请求失败'
      }
    } else if (error.code === 'ECONNABORTED') {
      message = '请求超时，请稍后重试'
    }

    // 支持静默模式，不显示错误提示
    if (!error.config || !error.config.silent) {
      ElMessage({
        message,
        type: 'error',
        duration: 5000
      })
    }

    return Promise.reject(error)
  }
)

export default service
