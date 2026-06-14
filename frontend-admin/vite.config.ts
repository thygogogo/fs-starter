import { fileURLToPath, URL } from 'node:url'

import { defineConfig, loadEnv } from 'vite-plus'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import tailwindcss from '@tailwindcss/vite'

const env = loadEnv('development', process.cwd(), '')
const proxyTarget = env.VITE_API_PROXY_TARGET || 'http://106.54.22.84:8889'

// https://vite.dev/config/
export default defineConfig({
  staged: {
    '*': 'vp check --fix',
  },
  fmt: {
    semi: false,
    singleQuote: true,
  },
  lint: {
    plugins: ['eslint', 'typescript', 'unicorn', 'oxc', 'vue'],
    env: {
      browser: true,
    },
    categories: {
      correctness: 'error',
    },
    options: {
      typeAware: true,
      typeCheck: true,
    },
  },
  plugins: [vue(), vueDevTools(), tailwindcss()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    proxy: {
      '/api': {
        target: proxyTarget,
        changeOrigin: true,
        rewrite: (path: string) => path.replace(/^\/api/, ''),
      },
    },
  },
})
