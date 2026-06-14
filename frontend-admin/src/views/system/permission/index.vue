<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { usePermissionStore } from '@/stores/permission'
import { getMenuTreeApi } from '@/api/menu'
import type { MenuItem } from '@/api/menu'
import { ElMessage } from 'element-plus'

const permissionStore = usePermissionStore()

const selectedRole = ref('admin')
const checkedMenuIds = ref<number[]>([])
const menuTree = ref<MenuItem[]>([])
const loading = ref(false)

async function loadMenuTree() {
  const res = await getMenuTreeApi()
  menuTree.value = res.data
}

async function handleRoleChange(roleCode: string) {
  selectedRole.value = roleCode
  if (roleCode === 'super_admin') {
    checkedMenuIds.value = collectAllIds(menuTree.value)
  } else {
    loading.value = true
    try {
      checkedMenuIds.value = await permissionStore.getRoleMenuIds(roleCode)
    } finally {
      loading.value = false
    }
  }
}

function collectAllIds(menus: MenuItem[]): number[] {
  const ids: number[] = []
  for (const menu of menus) {
    ids.push(menu.id)
    if (menu.children?.length) {
      ids.push(...collectAllIds(menu.children))
    }
  }
  return ids
}

async function handleSave() {
  await permissionStore.saveRoleMenus(selectedRole.value, checkedMenuIds.value)
  ElMessage.success('权限保存成功')
}

const isSuperAdmin = computed(() => selectedRole.value === 'super_admin')

onMounted(async () => {
  await loadMenuTree()
  await permissionStore.loadRoles()
  handleRoleChange('admin')
})
</script>

<template>
  <div>
    <el-card>
      <template #header>
        <div class="flex items-center justify-between">
          <span>权限配置</span>
          <el-button v-if="!isSuperAdmin" type="primary" @click="handleSave"> 保存配置 </el-button>
        </div>
      </template>

      <el-radio-group v-model="selectedRole" @change="handleRoleChange" class="mb-6">
        <el-radio-button v-for="role in permissionStore.roles" :key="role.code" :value="role.code">
          {{ role.name }}
        </el-radio-button>
      </el-radio-group>

      <el-alert
        v-if="isSuperAdmin"
        title="超级管理员拥有所有权限，无需配置"
        type="info"
        :closable="false"
        class="mb-4"
      />

      <div v-else v-loading="loading">
        <el-checkbox-group v-model="checkedMenuIds">
          <div v-for="menu in menuTree" :key="menu.id" class="mb-4">
            <el-checkbox :value="menu.id" :label="menu.name" class="font-medium text-base" />
            <div v-if="menu.children?.length" class="ml-8 mt-2 grid grid-cols-3 gap-2">
              <el-checkbox
                v-for="child in menu.children"
                :key="child.id"
                :value="child.id"
                :label="child.name"
              />
            </div>
          </div>
        </el-checkbox-group>
      </div>
    </el-card>
  </div>
</template>
