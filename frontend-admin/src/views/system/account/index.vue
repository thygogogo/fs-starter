<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { usePermissionStore } from '@/stores/permission'
import { useAuthStore } from '@/stores/auth'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getAdminListApi,
  createAdminApi,
  updateAdminApi,
  deleteAdminApi,
  resetPasswordApi,
  toggleAdminStatusApi,
} from '@/api/admin'
import type { AdminItem } from '@/api/admin'
import IdCell from '@/components/IdCell.vue'
import PropTableColumn from '@/components/PropTableColumn.vue'
import { formatDateTime } from '@/utils/format-datetime'
import { formatEmpty } from '@/utils/format-empty'

const permissionStore = usePermissionStore()
const authStore = useAuthStore()
const isSuperAdmin = computed(() => authStore.roleCode === 'super_admin')

const tableData = ref<AdminItem[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)

const searchForm = ref({
  username: '',
  phone: '',
  status: '' as number | string,
})

const dialogVisible = ref(false)
const dialogType = ref<'create' | 'edit'>('create')
const formData = ref<{
  id?: number
  username: string
  nickname: string
  phone: string
  roleCode: string
  status: number
  password: string
  confirmPassword: string
}>({
  username: '',
  nickname: '',
  phone: '',
  roleCode: 'admin',
  status: 1,
  password: '',
  confirmPassword: '',
})

const resetDialogVisible = ref(false)
const resetAccountId = ref<number | string>('')
const resetNickname = ref('')
const resetForm = ref({ password: '', confirmPassword: '' })

const roleOptions = computed(() =>
  permissionStore.roles
    .filter((role) => role.code !== 'super_admin')
    .map((role) => ({
      label: role.name,
      value: role.code,
    })),
)

async function fetchList() {
  loading.value = true
  try {
    const res = await getAdminListApi({
      page: currentPage.value,
      size: pageSize.value,
      username: searchForm.value.username || undefined,
      phone: searchForm.value.phone || undefined,
      status: searchForm.value.status !== '' ? Number(searchForm.value.status) : undefined,
    })
    tableData.value = res.data.records
    total.value = Number(res.data.total) || 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  currentPage.value = 1
  fetchList()
}

function handleReset() {
  searchForm.value = { username: '', phone: '', status: '' }
  currentPage.value = 1
  fetchList()
}

let lastPageSize = pageSize.value

function handlePaginationChange(_page: number, size: number) {
  if (size !== lastPageSize) {
    lastPageSize = size
    if (currentPage.value !== 1) {
      currentPage.value = 1
      return
    }
  }
  fetchList()
}

function handleCreate() {
  dialogType.value = 'create'
  formData.value = {
    username: '',
    nickname: '',
    phone: '',
    roleCode: roleOptions.value[0]?.value ?? 'admin',
    status: 1,
    password: '',
    confirmPassword: '',
  }
  dialogVisible.value = true
}

function handleEdit(account: AdminItem) {
  dialogType.value = 'edit'
  formData.value = {
    id: account.id,
    username: account.username,
    nickname: account.nickname || '',
    phone: account.phone || '',
    roleCode: account.roleCode,
    status: account.status,
    password: '',
    confirmPassword: '',
  }
  dialogVisible.value = true
}

async function handleSave() {
  if (!formData.value.username || !formData.value.nickname) {
    ElMessage.warning('请填写必填项')
    return
  }

  if (dialogType.value === 'create') {
    if (!formData.value.password) {
      ElMessage.warning('请输入密码')
      return
    }
    if (formData.value.password !== formData.value.confirmPassword) {
      ElMessage.warning('两次输入的密码不一致')
      return
    }
    await createAdminApi({
      username: formData.value.username,
      password: formData.value.password,
      nickname: formData.value.nickname,
      phone: formData.value.phone,
      roleCode: formData.value.roleCode,
    })
    ElMessage.success('账号创建成功')
  } else {
    await updateAdminApi({
      id: formData.value.id!,
      nickname: formData.value.nickname,
      phone: formData.value.phone,
      roleCode: formData.value.roleCode,
    })
    ElMessage.success('账号更新成功')
  }
  dialogVisible.value = false
  fetchList()
}

function handleDelete(account: AdminItem) {
  ElMessageBox.confirm('确定要删除该账号吗？', '提示', {
    type: 'warning',
  }).then(async () => {
    await deleteAdminApi(account.id)
    ElMessage.success('删除成功')
    fetchList()
  })
}

function handleResetPassword(account: AdminItem) {
  resetAccountId.value = account.id
  resetNickname.value = account.nickname || account.username
  resetForm.value = { password: '', confirmPassword: '' }
  resetDialogVisible.value = true
}

async function handleResetPasswordSave() {
  if (!resetForm.value.password) {
    ElMessage.warning('请输入新密码')
    return
  }
  if (resetForm.value.password !== resetForm.value.confirmPassword) {
    ElMessage.warning('两次输入的密码不一致')
    return
  }
  await resetPasswordApi(resetAccountId.value, resetForm.value.password)
  ElMessage.success('密码重置成功')
  resetDialogVisible.value = false
}

async function handleToggleStatus(account: AdminItem) {
  const isActive = account.status === 1
  const action = isActive ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(`确定要${action}账号「${account.username}」吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await toggleAdminStatusApi(account.id)
    ElMessage.success(`账号已${action}`)
    fetchList()
  } catch {
    // 用户取消或请求失败（失败时 request 拦截器已提示）
  }
}

onMounted(() => {
  permissionStore.loadRoles()
  fetchList()
})
</script>

<template>
  <div>
    <el-card>
      <template #header>账号管理</template>

      <el-form :model="searchForm" inline class="mb-4">
        <el-form-item label="用户名">
          <el-input
            v-model="searchForm.username"
            placeholder="请输入用户名"
            clearable
            class="!w-40"
          />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable class="!w-44" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable class="!w-28">
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="mb-4">
        <el-button v-if="isSuperAdmin" type="primary" @click="handleCreate">新增账号</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" stripe>
        <el-table-column label="ID" width="150">
          <template #default="{ row }">
            <IdCell :id="row.id" />
          </template>
        </el-table-column>
        <PropTableColumn prop="username" label="用户名" />
        <PropTableColumn prop="nickname" label="昵称" />
        <PropTableColumn prop="phone" label="手机号" />
        <el-table-column prop="roleCode" label="角色">
          <template #default="{ row }">
            {{
              permissionStore.roles.find((r) => r.code === row.roleCode)?.name ||
              formatEmpty(row.roleCode)
            }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link @click="handleResetPassword(row)">重置密码</el-button>
            <el-button
              :type="row.status === 1 ? 'danger' : 'success'"
              link
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="mt-4 flex justify-end">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 30]"
          layout="total, sizes, prev, pager, next"
          @change="handlePaginationChange"
        />
      </div>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'create' ? '新增账号' : '编辑账号'"
      width="500"
    >
      <el-form :model="formData" label-width="80px">
        <el-form-item label="用户名" required>
          <el-input
            v-model="formData.username"
            :disabled="dialogType === 'edit'"
            placeholder="请输入用户名"
          />
        </el-form-item>
        <el-form-item label="昵称" required>
          <el-input v-model="formData.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <template v-if="dialogType === 'create'">
          <el-form-item label="密码" required>
            <el-input
              v-model="formData.password"
              type="password"
              show-password
              placeholder="请输入密码"
            />
          </el-form-item>
          <el-form-item label="确认密码" required>
            <el-input
              v-model="formData.confirmPassword"
              type="password"
              show-password
              placeholder="请再次输入密码"
            />
          </el-form-item>
        </template>
        <el-form-item label="角色" required>
          <el-select v-model="formData.roleCode" placeholder="请选择角色">
            <el-option
              v-for="option in roleOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="resetDialogVisible" :title="`重置密码 - ${resetNickname}`" width="420">
      <el-form :model="resetForm" label-width="80px">
        <el-form-item label="新密码" required>
          <el-input
            v-model="resetForm.password"
            type="password"
            show-password
            placeholder="请输入新密码"
          />
        </el-form-item>
        <el-form-item label="确认密码" required>
          <el-input
            v-model="resetForm.confirmPassword"
            type="password"
            show-password
            placeholder="请再次输入新密码"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resetDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleResetPasswordSave">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>
