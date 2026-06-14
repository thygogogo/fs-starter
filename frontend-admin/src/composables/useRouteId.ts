import { computed } from 'vue'
import { useRoute } from 'vue-router'

/** 从路由读取业务主键，保持 string 避免雪花 ID 精度丢失 */
export function useRouteId(paramName = 'id') {
  const route = useRoute()
  return computed(() => String(route.params[paramName] ?? ''))
}
