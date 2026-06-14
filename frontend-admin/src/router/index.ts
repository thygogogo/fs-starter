import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/login/index.vue'),
      meta: { requiresAuth: false },
    },
    {
      path: '/',
      component: () => import('@/layouts/MainLayout.vue'),
      redirect: '/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'Dashboard',
          component: () => import('@/views/dashboard/index.vue'),
          meta: { title: '工作台', permission: 'dashboard' },
        },
        {
          path: 'system/account',
          name: 'SystemAccount',
          component: () => import('@/views/system/account/index.vue'),
          meta: { title: '账号管理', permission: 'system:account' },
        },
        {
          path: 'system/permission',
          name: 'SystemPermission',
          component: () => import('@/views/system/permission/index.vue'),
          meta: { title: '权限配置', permission: 'system:permission' },
        },
      ],
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/dashboard',
    },
  ],
})

router.beforeEach(async (to) => {
  const auth = useAuthStore()

  if (to.meta.requiresAuth === false) {
    if (auth.isLoggedIn && to.path === '/login') {
      return '/dashboard'
    }
    return true
  }
  if (!auth.isLoggedIn) {
    return '/login'
  }

  if (auth.isLoggedIn && !auth.userInfo) {
    await auth.fetchUserInfo()
  }

  const requiredPermission = to.meta.permission as string
  if (
    requiredPermission &&
    auth.roleCode !== 'super_admin' &&
    !auth.permissions.includes(requiredPermission)
  ) {
    return '/dashboard'
  }

  return true
})

export default router
