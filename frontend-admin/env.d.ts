/// <reference types="vite-plus/client" />

interface ImportMetaEnv {
  readonly VITE_DEPLOY_ENV: string
  readonly VITE_API_LOCAL: string
  readonly VITE_API_BASE_URL: string
  readonly VITE_API_PROXY_TARGET: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}

declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, unknown>
  export default component
}
