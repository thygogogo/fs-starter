<script setup lang="ts">
import { computed } from 'vue'
import { ElMessage } from 'element-plus'
import { DocumentCopy } from '@element-plus/icons-vue'
import { shouldTruncateId, truncateMiddle } from '@/utils/format-id'

const props = defineProps<{
  id: number | string | null | undefined
}>()

const fullId = computed(() => (props.id == null ? '' : String(props.id)))
const displayId = computed(() => truncateMiddle(props.id))
const needsTooltip = computed(() => shouldTruncateId(props.id))

async function handleCopy() {
  if (!fullId.value) return
  try {
    await navigator.clipboard.writeText(fullId.value)
    ElMessage.success('已复制')
  } catch {
    ElMessage.error('复制失败')
  }
}
</script>

<template>
  <div class="inline-flex items-center gap-1">
    <el-tooltip v-if="needsTooltip" :content="fullId" placement="top">
      <span class="cursor-default font-mono">{{ displayId }}</span>
    </el-tooltip>
    <span v-else class="font-mono">{{ fullId }}</span>
    <el-button
      v-if="fullId"
      type="primary"
      link
      :icon="DocumentCopy"
      class="!ml-0 !p-0"
      @click.stop="handleCopy"
    />
  </div>
</template>
