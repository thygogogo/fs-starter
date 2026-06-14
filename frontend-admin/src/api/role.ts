import request from '@/utils/request'

export interface RoleItem {
  id: number
  code: string
  name: string
  description: string
  status: number
}

/** 角色列表 */
export function getRoleListApi() {
  return request.get<any, { code: number; data: RoleItem[] }>('/admin/system/role/list')
}

/** 获取角色关联的菜单 ID 列表 */
export function getRoleMenuIdsApi(code: string) {
  return request.get<any, { code: number; data: number[] }>(`/admin/system/role/${code}/menus`)
}

/** 更新角色菜单关联 */
export function updateRoleMenusApi(code: string, menuIds: number[]) {
  return request.put<any, { code: number }>(`/admin/system/role/${code}/menus`, menuIds)
}
