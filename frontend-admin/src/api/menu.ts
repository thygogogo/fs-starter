import request from '@/utils/request'

export interface MenuItem {
  id: number
  parentId: number
  name: string
  permissionKey: string
  type: string
  path: string
  icon: string
  sort: number
  status: number
  children?: MenuItem[]
}

/** 菜单列表（平铺） */
export function getMenuListApi() {
  return request.get<any, { code: number; data: MenuItem[] }>('/admin/system/menu/list')
}

/** 菜单树 */
export function getMenuTreeApi() {
  return request.get<any, { code: number; data: MenuItem[] }>('/admin/system/menu/tree')
}

/** 新增菜单 */
export function createMenuApi(data: Partial<MenuItem>) {
  return request.post<any, { code: number }>('/admin/system/menu', data)
}

/** 编辑菜单 */
export function updateMenuApi(data: Partial<MenuItem>) {
  return request.put<any, { code: number }>('/admin/system/menu', data)
}

/** 删除菜单 */
export function deleteMenuApi(id: number | string) {
  return request.delete<any, { code: number }>(`/admin/system/menu/${id}`)
}
