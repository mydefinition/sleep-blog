<template>
  <div class="flex flex-col items-center justify-center text-center" style="min-height: calc(100vh - 52px)">
    <div class="flex flex-col items-center gap-10">
      <h1 class="font-sans text-[2.4rem] font-light text-gray-800 m-0 tracking-[0.06em] after:content-['.'] after:animate-blink">今日目标 : {{ dailyTask }}</h1>
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

const router = useRouter()
const settings = useSettingsStore()
const loading = ref(false)
const dailyTask = ref('...')

onMounted(async () => {
  try {
    const res = await api.get('/api/home/daily')
    dailyTask.value = res.data.dailyTask
  } catch {
    dailyTask.value = '...'
  }
})

async function goRandom() {
  loading.value = true
  try {
    const res = await api.get('/api/home/random')
    router.push('/articles/' + res.data)
  } catch {
    router.push('/articles')
  } finally {
    loading.value = false
  }
}
</script>