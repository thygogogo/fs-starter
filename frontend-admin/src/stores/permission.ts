import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getRoleListApi, getRoleMenuIdsApi, updateRoleMenusApi } from '@/api/role'
import { getMenuListApi } from '@/api/menu'
import type { RoleItem } from '@/api/role'
import type { MenuItem } from '@/api/menu'

export const usePermissionStore = defineStore('permission', () => {
  const roles = ref<RoleItem[]>([])
  const allMenus = ref<MenuItem[]>([])

  async function loadRoles() {
    try {
      const res = await getRoleListApi()
      roles.value = res.data
    } catch {
      // 拦截器已处理错误提示
    }
  }

  async function loadAllMenus() {
    try {
      const res = await getMenuListApi()
      allMenus.value = res.data
    } catch {
      // 拦截器已处理错误提示
    }
  }

  async function getRoleMenuIds(roleCode: string): Promise<number[]> {
    const res = await getRoleMenuIdsApi(roleCode)
    return res.data
  }

  async function saveRoleMenus(roleCode: string, menuIds: number[]) {
    await updateRoleMenusApi(roleCode, menuIds)
  }

  function hasPermission(userPermissions: string[], permissionKey: string): boolean {
    return userPermissions.includes(permissionKey)
  }

  return {
    roles,
    allMenus,
    loadRoles,
    loadAllMenus,
    getRoleMenuIds,
    saveRoleMenus,
    hasPermission,
  }
})
