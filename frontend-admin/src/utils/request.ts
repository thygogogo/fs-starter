import axios from 'axios'
import type { AxiosInstance, InternalAxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import router from '@/router'

const isLocal = import.meta.env.VITE_API_LOCAL === 'true'

/** 与 axios baseURL 一致；测试 https://lekai.fun/admin，生产 https://lbyz.top/admin */
export function getApiBaseUrl(): string {
  return isLocal ? 'http://localhost:8889' : import.meta.env.VITE_API_BASE_URL || '/admin'
}

const baseURL = getApiBaseUrl()

const service: AxiosInstance = axios.create({
  baseURL,
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' },
})

function shouldHandleUnauthorized(config: InternalAxiosRequestConfig | undefined): boolean {
  if (!config) return true
  const requestToken = config.headers?.Authorization
  if (!requestToken) return true
  const authStore = useAuthStore()
  return requestToken === authStore.token
}

// 请求拦截器：注入 token
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const authStore = useAuthStore()
    const isLoginRequest = config.url?.includes('/auth/login')
    if (authStore.token && !isLoginRequest) {
      config.headers.Authorization = authStore.token
    }
    return config
  },
  (error) => Promise.reject(error),
)

// 响应拦截器：统一处理
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data
    // 后端统一响应体 { code, msg, data, traceId, timestamp }
    if (res.code !== 200) {
      // 未登录或 token 过期
      if (res.code === 401 || res.code === 4011 || res.code === 4012) {
        if (shouldHandleUnauthorized(response.config)) {
          const authStore = useAuthStore()
          void authStore.logout()
          if (router.currentRoute.value.path !== '/login') {
            void router.push('/login')
          }
        }
      }
      ElMessage.error(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
    return res
  },
  (error) => {
    if (error.response) {
      const { status } = error.response
      if (status === 401) {
        if (shouldHandleUnauthorized(error.config)) {
          const authStore = useAuthStore()
          void authStore.logout()
          if (router.currentRoute.value.path !== '/login') {
            void router.push('/login')
          }
        }
      } else if (status === 403) {
        ElMessage.error('无权限访问')
      } else if (status === 413) {
        ElMessage.error('图片太大')
      } else {
        ElMessage.error(error.response.data?.msg || '服务器异常')
      }
    } else {
      ElMessage.error('网络异常')
    }
    return Promise.reject(error)
  },
)

export default service
