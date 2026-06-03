<template>
  <div
    class="w-full overflow-hidden overflow-scroll-mask hover:[mask-image:none] hover:[-webkit-mask-image:none]"
    @mouseenter="onEnter"
    @mouseleave="onLeave"
    ref="container"
  >
    <span ref="textEl" class="inline-block whitespace-nowrap transition-transform duration-[600ms]" :style="{ transform: `translateX(${offset}px)` }">{{ text }}</span>
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
