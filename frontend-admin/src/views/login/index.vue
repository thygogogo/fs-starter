<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'
import { UserIcon, LockClosedIcon, StarIcon } from '@heroicons/vue/24/solid'

const router = useRouter()
const auth = useAuthStore()

const form = reactive({
  username: '',
  password: '',
})

const loading = ref(false)

async function handleLogin() {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入账号和密码')
    return
  }
  loading.value = true
  try {
    const ok = await auth.login(form.username, form.password)
    if (ok) {
      ElMessage.success('登录成功')
      await router.replace('/dashboard')
    } else {
      ElMessage.error('账号或密码错误')
    }
  } catch {
    ElMessage.error('登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <!-- Left panel - branding -->
    <div class="login-left">
      <div class="login-left-content">
        <div class="login-logo">
          <StarIcon class="logo-star" />
          <h1 class="login-title">FS Starter</h1>
          <p class="login-subtitle">Fullstack Scaffold</p>
        </div>
      </div>
      <!-- Decorative circles -->
      <div class="decor-circle decor-circle-1"></div>
      <div class="decor-circle decor-circle-2"></div>
      <div class="decor-circle decor-circle-3"></div>
    </div>

    <!-- Right panel - login form -->
    <div class="login-right">
      <div class="login-form-wrapper">
        <div class="login-form-header">
          <h2 class="form-title">欢迎回来</h2>
          <p class="form-desc">请登录您的账号以继续</p>
        </div>
        <el-form :model="form" @submit.prevent="handleLogin" class="login-form">
          <el-form-item>
            <el-input
              v-model="form.username"
              placeholder="请输入账号"
              size="large"
              class="login-input"
            >
              <template #prefix>
                <UserIcon class="w-5 h-5 text-gray-400" />
              </template>
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              show-password
              class="login-input"
            >
              <template #prefix>
                <LockClosedIcon class="w-5 h-5 text-gray-400" />
              </template>
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              class="login-btn"
              native-type="submit"
            >
              登 录
            </el-button>
          </el-form-item>
        </el-form>
        <div class="login-tip">请输入管理员分配的账号密码</div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  display: flex;
  min-height: 100vh;
  background: var(--bg-paper);
}

/* Left panel */
.login-left {
  position: relative;
  flex: 1;
  background-color: #3a4119;
  background-image:
    radial-gradient(ellipse 95px 75px at 10% 12%, #5c6b2e 0%, transparent 68%),
    radial-gradient(ellipse 80px 65px at 32% 6%, #4d3f1a 0%, transparent 62%),
    radial-gradient(ellipse 105px 80px at 65% 10%, #6b7340 0%, transparent 68%),
    radial-gradient(ellipse 75px 90px at 90% 22%, #2f3612 0%, transparent 62%),
    radial-gradient(ellipse 115px 85px at 20% 35%, #556130 0%, transparent 68%),
    radial-gradient(ellipse 85px 95px at 55% 32%, #5a4a22 0%, transparent 62%),
    radial-gradient(ellipse 100px 75px at 82% 42%, #4b5320 0%, transparent 68%),
    radial-gradient(ellipse 70px 85px at 6% 52%, #3d4518 0%, transparent 62%),
    radial-gradient(ellipse 125px 95px at 42% 52%, #6b5a2a 0%, transparent 68%),
    radial-gradient(ellipse 90px 105px at 72% 62%, #5c6b2e 0%, transparent 62%),
    radial-gradient(ellipse 100px 80px at 25% 70%, #2f3612 0%, transparent 68%),
    radial-gradient(ellipse 85px 75px at 52% 78%, #4d3f1a 0%, transparent 62%),
    radial-gradient(ellipse 95px 90px at 86% 85%, #556130 0%, transparent 68%),
    radial-gradient(ellipse 70px 60px at 12% 88%, #6b7340 0%, transparent 62%),
    radial-gradient(ellipse 110px 85px at 38% 20%, #3a4119 0%, transparent 68%),
    radial-gradient(ellipse 65px 70px at 78% 28%, #5a6328 0%, transparent 62%);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  padding: 40px;
}

.login-left::before {
  content: '';
  position: absolute;
  inset: 0;
  background-image:
    radial-gradient(ellipse 55px 45px at 48% 16%, #4d3f1a 0%, transparent 58%),
    radial-gradient(ellipse 50px 60px at 16% 42%, #5c6b2e 0%, transparent 55%),
    radial-gradient(ellipse 60px 50px at 74% 38%, #3d4518 0%, transparent 58%),
    radial-gradient(ellipse 45px 55px at 92% 58%, #6b7340 0%, transparent 55%),
    radial-gradient(ellipse 58px 48px at 28% 58%, #5a4a22 0%, transparent 58%),
    radial-gradient(ellipse 52px 62px at 62% 48%, #2f3612 0%, transparent 55%),
    radial-gradient(ellipse 48px 52px at 8% 28%, #556130 0%, transparent 58%),
    radial-gradient(ellipse 56px 44px at 44% 68%, #4b5320 0%, transparent 55%),
    radial-gradient(ellipse 50px 58px at 68% 78%, #6b5a2a 0%, transparent 58%),
    radial-gradient(ellipse 54px 46px at 34% 88%, #3a4119 0%, transparent 55%),
    radial-gradient(ellipse 48px 54px at 58% 14%, #5a6328 0%, transparent 58%),
    radial-gradient(ellipse 52px 50px at 84% 68%, #4d3f1a 0%, transparent 55%);
  pointer-events: none;
  z-index: 1;
}

.login-left-content {
  position: relative;
  z-index: 2;
  color: white;
  max-width: 420px;
}

.login-logo {
  margin-bottom: 60px;
}

.logo-star {
  width: 80px;
  height: 80px;
  color: #ffd700;
  margin-bottom: 24px;
}

.login-title {
  font-size: 36px;
  font-weight: 700;
  margin: 0 0 8px 0;
  letter-spacing: 2px;
}

.login-subtitle {
  font-size: 14px;
  opacity: 0.7;
  margin: 0;
  letter-spacing: 1px;
  text-transform: uppercase;
}

/* Decorative circles */
.decor-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.05);
}

.decor-circle-1 {
  width: 400px;
  height: 400px;
  top: -100px;
  right: -100px;
  animation: float 8s ease-in-out infinite;
}

.decor-circle-2 {
  width: 300px;
  height: 300px;
  bottom: -80px;
  left: -80px;
  animation: float 10s ease-in-out infinite reverse;
}

.decor-circle-3 {
  width: 200px;
  height: 200px;
  top: 50%;
  right: -50px;
  animation: float 6s ease-in-out infinite;
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-20px);
  }
}

/* Right panel */
.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.login-form-wrapper {
  width: 100%;
  max-width: 420px;
}

.login-form-header {
  margin-bottom: 40px;
}

.form-title {
  font-size: 32px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 8px 0;
}

.form-desc {
  font-size: 15px;
  color: var(--text-secondary);
  margin: 0;
}

.login-form {
  margin-bottom: 24px;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: 10px;
  box-shadow: 0 0 0 1px var(--border-color) inset;
  padding: 4px 12px;
  transition: all 0.3s ease;
}

.login-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--text-secondary) inset;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px var(--theme-primary) inset;
}

.login-options {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.login-options :deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: var(--theme-primary);
  border-color: var(--theme-primary);
}

.login-options :deep(.el-checkbox__input.is-checked + .el-checkbox__label) {
  color: var(--theme-primary);
}

.forgot-link {
  font-size: 14px;
  color: var(--theme-primary);
  text-decoration: none;
  transition: color 0.2s;
}

.forgot-link:hover {
  color: var(--theme-primary-dark);
}

.login-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 10px;
  background: linear-gradient(135deg, var(--theme-primary-dark) 0%, var(--theme-primary) 100%);
  border: none;
  letter-spacing: 4px;
  transition: all 0.3s ease;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(22, 119, 255, 0.35);
}

.login-btn:active {
  transform: translateY(0);
}

.login-tip {
  text-align: center;
  font-size: 13px;
  color: var(--text-secondary);
  padding: 16px;
  background: var(--theme-primary-bg);
  border-radius: 8px;
}

/* Responsive */
@media (max-width: 1024px) {
  .login-left {
    display: none;
  }

  .login-right {
    flex: 1;
  }
}

@media (max-width: 480px) {
  .login-right {
    padding: 20px;
  }

  .form-title {
    font-size: 26px;
  }
}
</style>
