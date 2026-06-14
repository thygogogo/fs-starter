import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { RoleCode } from '@/types/permission'
import { loginApi, getUserInfoApi, logoutApi } from '@/api/auth'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref<{
    username: string
    nickname: string
    avatar: string
    roleCode: RoleCode
    permissions: string[]
  } | null>(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  const isLoggedIn = computed(() => !!token.value)
  const roleCode = computed(() => userInfo.value?.roleCode || 'admin')
  const permissions = computed(() => userInfo.value?.permissions || [])

  async function login(username: string, password: string): Promise<boolean> {
    // 清除过期会话，避免旧 token 随登录请求发出，或旧请求 401 误清新会话
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')

    try {
      const res = await loginApi({ username, password })
      const data = res.data
      if (!data?.token) {
        return false
      }
      token.value = data.token
      userInfo.value = {
        username: data.username,
        nickname: data.nickname,
        avatar: data.avatar,
        roleCode: data.roleCode as RoleCode,
        permissions: data.permissions || [],
      }
      localStorage.setItem('token', data.token)
      localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
      return true
    } catch {
      return false
    }
  }

  async function fetchUserInfo() {
    try {
      const res = await getUserInfoApi()
      const data = res.data
      userInfo.value = {
        username: data.username,
        nickname: data.nickname,
        avatar: data.avatar,
        roleCode: data.roleCode as RoleCode,
        permissions: data.permissions || [],
      }
      localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
    } catch {
      // 非 401 错误（如网络抖动）不登出，避免临时故障导致用户被踢出
      // 401 已由 request.ts 拦截器处理
    }
  }

  async function logout() {
    try {
      await logoutApi()
    } catch {
      // 忽略退出接口异常
    }
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  return { token, userInfo, isLoggedIn, roleCode, permissions, login, fetchUserInfo, logout }
})
