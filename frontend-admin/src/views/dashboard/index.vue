<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
</script>

<template>
  <div class="rounded-lg bg-white p-8 shadow-sm border border-slate-100">
    <h1 class="text-2xl font-semibold text-slate-800 mb-2">欢迎使用 FS Starter</h1>
    <p class="text-slate-600 mb-6">
      全栈脚手架已就绪：管理后台 RBAC、微信小程序登录、MinIO 文件上传。
    </p>
    <el-descriptions :column="1" border>
      <el-descriptions-item label="当前用户">
        {{ auth.userInfo?.username || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="角色">
        {{ auth.roleCode || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="下一步">
        使用 scripts/init-project.sh 复制脚手架并替换项目名，或在当前基础上直接开发业务模块。
      </el-descriptions-item>
    </el-descriptions>
  </div>
</template>
