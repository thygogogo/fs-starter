<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessageBox } from 'element-plus'
import {
  HomeIcon,
  Bars3Icon,
  Cog6ToothIcon,
  UserCircleIcon,
} from '@heroicons/vue/24/solid'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const collapsed = ref(false)

const asideWidth = computed(() => (collapsed.value ? '64px' : '220px'))

const activeMenu = computed(() => {
  const path = route.path
  if (path.startsWith('/system/permission')) return '/system/permission'
  if (path.startsWith('/system')) return '/system/account'
  return path
})

interface MenuItem {
  index: string
  title: string
  icon?: object
  permission?: string
  children?: { index: string; title: string; permission: string }[]
}

const menuConfig: MenuItem[] = [
  {
    index: '/dashboard',
    title: '工作台',
    icon: HomeIcon,
    permission: 'dashboard',
  },
  {
    index: 'system-group',
    title: '系统管理',
    icon: Cog6ToothIcon,
    children: [
      { index: '/system/account', title: '账号管理', permission: 'system:account' },
      { index: '/system/permission', title: '权限配置', permission: 'system:permission' },
    ],
  },
]

function hasMenuPermission(permission: string) {
  if (!permission) return true
  if (auth.roleCode === 'super_admin') return true
  return auth.permissions.includes(permission)
}

const filteredMenus = computed(() => {
  return menuConfig
    .map((menu) => {
      if (!menu.children) {
        if (menu.permission && !hasMenuPermission(menu.permission)) return null
        return menu
      }
      const children = menu.children.filter((child) => hasMenuPermission(child.permission))
      if (children.length === 0) return null
      return { ...menu, children }
    })
    .filter(Boolean) as MenuItem[]
})

function toggleCollapse() {
  collapsed.value = !collapsed.value
}

async function handleLogout() {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await auth.logout()
    router.push('/login')
  } catch {
    // 用户取消
  }
}

onMounted(() => {
  if (auth.isLoggedIn) {
    auth.fetchUserInfo()
  }
})
</script>

<template>
  <el-container class="h-screen box-border">
    <el-aside
      :width="asideWidth"
      class="overflow-y-auto border-0 box-border transition-all duration-300"
      style="background-color: var(--sidebar-blue-dark)"
    >
      <div
        class="h-15 flex items-center justify-center gap-2 border-b border-white/10 overflow-hidden"
      >
        <span
          class="w-8 h-8 rounded-md flex items-center justify-center text-white font-bold text-base shrink-0"
          style="background: linear-gradient(135deg, var(--theme-primary), var(--theme-primary-dark))"
          >FS</span
        >
        <span v-show="!collapsed" class="text-white text-base font-semibold whitespace-nowrap"
          >FS Starter</span
        >
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="var(--sidebar-blue-dark)"
        text-color="#ffffffa6"
        active-text-color="#ffffff"
        :collapse="collapsed"
      >
        <template v-for="menu in filteredMenus" :key="menu.index">
          <el-menu-item v-if="!menu.children" :index="menu.index">
            <el-icon><component :is="menu.icon" class="w-5 h-5" /></el-icon>
            <template #title>{{ menu.title }}</template>
          </el-menu-item>
          <el-sub-menu v-else :index="menu.index">
            <template #title>
              <el-icon><component :is="menu.icon" class="w-5 h-5" /></el-icon>
              <span>{{ menu.title }}</span>
            </template>
            <el-menu-item v-for="child in menu.children" :key="child.index" :index="child.index">
              {{ child.title }}
            </el-menu-item>
          </el-sub-menu>
        </template>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header
        class="flex items-center justify-between shadow-[0_1px_4px_rgba(0,0,0,0.05)] !pl-5 !pr-5"
        style="background-color: var(--bg-card); border-bottom: 1px solid var(--border-color)"
      >
        <div class="flex items-center gap-3">
          <Bars3Icon
            class="w-6 h-6 cursor-pointer transition-colors"
            style="color: var(--text-secondary)"
            @click="toggleCollapse"
          />
          <span class="text-base font-medium" style="color: var(--text-primary)">{{
            route.meta.title
          }}</span>
        </div>
        <el-dropdown trigger="hover" @command="handleLogout">
          <div class="flex items-center gap-2 cursor-pointer">
            <UserCircleIcon class="w-8 h-8 text-gray-400" />
            <span class="text-sm" style="color: var(--text-secondary)">{{
              auth.userInfo?.username
            }}</span>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>

      <el-main class="p-5 overflow-y-auto" style="background-color: var(--bg-paper)">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style>
.el-aside {
  --el-menu-bg-color: var(--sidebar-blue-dark);
  --el-menu-hover-bg-color: var(--sidebar-blue-hover);
  --el-menu-active-bg-color: var(--sidebar-blue-hover);
  --el-menu-hover-text-color: #ffffff;
  --el-menu-active-color: #ffffff;
  --el-menu-text-color: rgba(255, 255, 255, 0.65);
  --el-menu-item-hover-fill: var(--sidebar-blue-hover);
  --el-menu-border-color: transparent;
}
.el-aside .el-sub-menu.is-active > .el-sub-menu__title {
  color: rgba(255, 255, 255, 0.85) !important;
  background-color: transparent !important;
}
.el-aside .el-menu {
  border-right: none !important;
  background-color: var(--sidebar-blue-dark) !important;
}
.el-aside .el-menu-item:hover,
.el-aside .el-sub-menu__title:hover {
  background-color: var(--sidebar-blue-hover) !important;
  color: #ffffff !important;
}
.el-aside .el-menu-item.is-active {
  background-color: var(--sidebar-blue-hover) !important;
  color: #ffffff !important;
  font-weight: 600;
  box-shadow: inset 3px 0 0 var(--theme-primary-light);
}
.el-aside .el-sub-menu .el-menu {
  background-color: transparent !important;
}
.el-aside .el-sub-menu .el-menu-item {
  background-color: transparent !important;
}
.el-aside .el-sub-menu .el-menu-item:hover {
  background-color: var(--sidebar-blue-hover) !important;
}
.el-aside .el-sub-menu .el-menu-item.is-active {
  background-color: var(--sidebar-blue-hover) !important;
  color: #ffffff !important;
  font-weight: 600;
  box-shadow: inset 3px 0 0 var(--theme-primary-light);
}
</style>
