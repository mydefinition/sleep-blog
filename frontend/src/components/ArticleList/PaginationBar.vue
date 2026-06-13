<template>
  <div v-if="totalPages > 1" class="flex justify-center items-center gap-0.5 mt-auto pt-6 select-none">
    <div class="relative flex items-center gap-0.5" @mouseleave="hovered = null">
      <template v-for="(p, i) in nums" :key="i">
        <span v-if="p === '...'" class="w-8 h-8 flex items-center justify-center text-gray-300 text-sm select-none">···</span>
        <button v-else @click="emit('update:page', p)" @mouseenter="hovered = p"
                class="w-8 h-8 flex items-center justify-center rounded cursor-pointer text-sm font-semibold transition-colors duration-150 relative z-10"
                :class="p === page && hovered == null ? 'text-primary' : 'text-gray-500 hover:text-primary'">{{ p }}</button>
      </template>
      <div class="absolute bottom-0 h-0.5 bg-primary rounded-full transition-all duration-200 ease-out" :style="barStyle" />
    </div>
    <span class="inline-flex items-center gap-1 text-sm text-gray-400 ml-4">
      跳至
      <input v-model="jump" type="number" :min="1" :max="totalPages" placeholder="页码"
             class="w-12 px-1 py-1 border-0 border-b border-gray-200 rounded-none text-center text-sm text-gray-700 outline-none [appearance:textfield] [&::-webkit-outer-spin-button]:appearance-none [&::-webkit-inner-spin-button]:appearance-none focus:border-b-primary focus:placeholder:text-transparent transition-colors duration-150"
             @keydown.enter="jumpGo" />
      页
    </span>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

const props = defineProps<{ page: number; totalPages: number }>()
const emit = defineEmits<{ 'update:page': [page: number] }>()

const hovered = ref<number | null>(null)
const jump = ref('')

const nums = computed(() => {
  const tp = props.totalPages
  const cur = props.page
  const nums: (number | '...')[] = []

  if (tp <= 5) {
    for (let i = 1; i <= tp; i++) nums.push(i)
    return nums
  }

  nums.push(1, 2)
  if (cur > 5) nums.push('...')
  for (let i = cur - 2; i <= cur + 2; i++)
    if (i > 2 && i < tp - 1) nums.push(i)
  if (cur + 4 < tp) nums.push('...')
  nums.push(tp - 1, tp)

  return nums
})

const barStyle = computed(() => {
  const target = hovered.value ?? props.page
  const idx = nums.value.indexOf(target)
  const W = 32, G = 2
  return { left: `${(idx === -1 ? 0 : idx) * (W + G)}px`, width: `${W}px` }
})

function jumpGo() {
  const n = Number(jump.value)
  if (Number.isInteger(n) && n >= 1 && n <= props.totalPages) emit('update:page', n)
  jump.value = ''
}
</script>