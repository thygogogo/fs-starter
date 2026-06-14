import request from '@/utils/request'

export interface AdminItem {
  id: number
  username: string
  nickname: string
  phone: string
  avatar: string
  roleCode: string
  status: number
  createTime: string
}

export interface AdminListParams {
  page: number
  size: number
  username?: string
  phone?: string
  status?: number | string
}

export interface AdminCreateData {
  username: string
  password: string
  nickname?: string
  phone?: string
  roleCode: string
}

export interface AdminUpdateData {
  id: number
  nickname?: string
  phone?: string
  roleCode?: string
}

/** 管理员分页列表 */
export function getAdminListApi(params: AdminListParams) {
  return request.get<any, { code: number; data: { records: AdminItem[]; total: number } }>(
    '/admin/system/admin/list',
    { params },
  )
}

/** 创建管理员 */
export function createAdminApi(data: AdminCreateData) {
  return request.post<any, { code: number }>('/admin/system/admin', data)
}

/** 更新管理员 */
export function updateAdminApi(data: AdminUpdateData) {
  return request.put<any, { code: number }>('/admin/system/admin', data)
}

/** 删除管理员 */
export function deleteAdminApi(id: number | string) {
  return request.delete<any, { code: number }>(`/admin/system/admin/${id}`)
}

/** 重置密码 */
export function resetPasswordApi(id: number | string, password: string) {
  return request.put<any, { code: number }>(`/admin/system/admin/${id}/reset-password`, {
    password,
  })
}

/** 切换启用/禁用 */
export function toggleAdminStatusApi(id: number | string) {
  return request.put<any, { code: number }>(`/admin/system/admin/${id}/toggle-status`)
}
