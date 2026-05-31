<template>
  <div
    class="overflow-scroll"
    @mouseenter="onEnter"
    @mouseleave="onLeave"
    ref="container"
  >
    <span ref="textEl" class="text">{{ text }}</span>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

defineProps<{ text: string }>()

const container = ref<HTMLElement | null>(null)
const textEl = ref<HTMLElement | null>(null)
const offset = ref(0)

function onEnter() {
  if (!container.value || !textEl.value) return
  const overflow = textEl.value.offsetWidth - container.value.offsetWidth
  if (overflow > 0) {
    setTimeout(() => { offset.value = -overflow }, 300)
  }
}

function onLeave() {
  offset.value = 0
}
</script>

<style scoped>
.overflow-scroll { width: 100%;
  overflow: hidden;
  -webkit-mask-image: linear-gradient(to right, black calc(100% - 36px), transparent 100%);
  mask-image: linear-gradient(to right, black calc(100% - 36px), transparent 100%);
}

.overflow-scroll:hover {
  -webkit-mask-image: none;
  mask-image: none;
}

.text {
  display: inline-block;
  white-space: nowrap;
  transition: transform 0.6s ease;
  transform: translateX(v-bind(offset + 'px'));
}
</style>
