import request from '@/utils/request'

export interface LoginParams {
  username: string
  password: string
}

export interface LoginResult {
  token: string
  userId: number
  username: string
  nickname: string
  avatar: string
  roleCode: string
  permissions: string[]
}

/** 管理员登录 */
export function loginApi(data: LoginParams) {
  return request.post<any, { code: number; msg: string; data: LoginResult }>(
    '/admin/auth/login',
    data,
  )
}

/** 获取当前用户信息 */
export function getUserInfoApi() {
  return request.get<any, { code: number; msg: string; data: LoginResult }>('/admin/auth/info')
}

/** 退出登录 */
export function logoutApi() {
  return request.post<any, { code: number; msg: string }>('/admin/auth/logout')
}
