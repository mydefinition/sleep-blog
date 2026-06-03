<template>
  <div class="flex flex-col items-center justify-center text-center" style="min-height: calc(100vh - 52px)">
    <div class="flex flex-col items-center gap-10">
      <h1 class="font-sans text-[2.4rem] font-light text-gray-800 m-0 tracking-[0.06em] goal-text">今日目标 : {{ dailyTask }}</h1>
      <button
        class="px-9 py-2.5 text-white border-none rounded-full text-[0.95rem] font-sans cursor-pointer transition-[opacity,transform] duration-200 hover:opacity-85 hover:-translate-y-px"
        :style="{ background: settings.primary }"
        @click="goRandom"
      >{{ loading ? '加载中...' : '随便看看' }}</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '@/api'
import { useSettingsStore } from '@/stores/settings'
import type { Article } from '@/types'

const router = useRouter()
const settings = useSettingsStore()
const articles = ref<Article[]>([])
const loading = ref(false)
const dailyTask = ref('...')

onMounted(async () => {
  try {
    const res = await api.get('/api/home/daily')
    dailyTask.value = res.data.task
  } catch {
    dailyTask.value = '...'
  }
})

async function goRandom() {
  if (articles.value.length === 0) {
    try {
      loading.value = true
      const res = await api.get('/api/articles?page=1&size=9999')
      articles.value = res.data.records || []
    } catch {
      router.push('/articles')
      return
    } finally {
      loading.value = false
    }
  }
  if (articles.value.length) {
    const idx = Math.floor(Math.random() * articles.value.length)
    router.push('/articles/' + articles.value[idx].id)
  } else {
    router.push('/articles')
  }
}
</script>

<style scoped>
.goal-text::after {
  content: '.';
  @apply inline-block animate-blink;
}
</style>
