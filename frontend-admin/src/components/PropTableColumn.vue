<script setup lang="ts">
import { computed, useAttrs, useSlots } from 'vue'
import { tableEmptyFormatter } from '@/utils/format-empty'

defineOptions({ inheritAttrs: false })

const attrs = useAttrs()
const slots = useSlots()

const formatter = computed(() =>
  attrs.prop != null && attrs.prop !== '' && !slots.default ? tableEmptyFormatter : undefined,
)
</script>

<template>
  <el-table-column v-bind="$attrs" :formatter="formatter">
    <template v-if="$slots.default" #default="scope">
      <slot v-bind="scope" />
    </template>
  </el-table-column>
</template>
