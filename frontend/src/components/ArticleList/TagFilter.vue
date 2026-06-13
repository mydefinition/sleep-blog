<template>
  <div class="flex flex-wrap gap-1.5">
    <span v-for="t in tags" :key="t.id"
          class="inline-flex items-center gap-[3px] px-2.5 py-1 border rounded-lg cursor-pointer text-[0.82rem] transition-all duration-150"
          :class="selectedTags.includes(t.id)
            ? 'pill-solid'
            : hoveredTag === t.name && !selectedTags.includes(t.id)
              ? 'pill-outline'
              : 'border-transparent text-gray-600'"
          @click="toggle(t.id)"
          @mouseenter="emit('hoverTag', t.name)"
          @mouseleave="emit('hoverTag', '')"
    ><TagIcon :size="14"/> {{ t.name }}</span>
  </div>
</template>

<script setup lang="ts">
import { Tag as TagIcon } from 'lucide-vue-next'
import type { Tag as TagType } from '@/types'

const props = defineProps<{
  tags: TagType[]
  selectedTags: number[]
  hoveredTag: string
}>()

const emit = defineEmits<{
  'update:selectedTags': [ids: number[]]
  hoverTag: [name: string]
}>()

function toggle(id: number) {
  const arr = [...props.selectedTags]
  const i = arr.indexOf(id)
  i >= 0 ? arr.splice(i, 1) : arr.push(id)
  emit('update:selectedTags', arr)
}
</script>