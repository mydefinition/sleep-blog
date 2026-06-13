<template>
  <div>
    <div
        class="flex items-center cursor-pointer select-none py-0.5"
        :style="{ paddingLeft: (depth * 20 + 12) + 'px' }"
        @click="onClick"
    >
      <span
          class="truncate inline-block px-3 py-[3px] rounded-full border border-transparent text-[0.88rem] transition-colors duration-150"
          :class="selectedId === node.id
          ? 'pill-solid font-semibold'
          : isAncestor
            ? 'pill-ghost'
            : 'text-gray-600 hover:text-primary'"
      >{{ node.name }}</span>
    </div>
    <template v-if="expanded && node.children">
      <CategoryTree
          v-for="child in node.children"
          :key="child.id"
          :node="child"
          :selectedId="selectedId"
          :depth="depth + 1"
          @select="emit('select', $event)"
      />
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { CategoryVO } from '@/types'

const props = defineProps<{
  node: CategoryVO
  selectedId: number
  depth: number
}>()

const emit = defineEmits<{ select: [id: number] }>()

const expanded = computed(() => isInSubtree(props.node, props.selectedId))

const isAncestor = computed(() =>
    !props.node.children ? false : props.node.children.some(c => isInSubtree(c, props.selectedId))
)

function isInSubtree(node: CategoryVO, target: number): boolean {
  if (node.id === target) return true
  if (!node.children) return false
  return node.children.some(c => isInSubtree(c, target))
}

function onClick() {
  emit('select', props.node.id)
}
</script>