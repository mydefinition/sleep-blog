<template>
  <Navbar />
  <main>
    <router-view />
  </main>
</template>

<script setup lang="ts">
import Navbar from '@/components/Navbar.vue'
import { useSettingsStore } from '@/stores/settings'
import { onMounted, onBeforeUnmount  } from 'vue'

const settings = useSettingsStore()
onMounted(() => {
  settings.apply()
})

let timer: ReturnType<typeof setInterval>

// 心跳机制
onMounted(() => {
  timer = setInterval(() => fetch('/api/heartbeat').catch(() => {}), 60 * 1000)
})

onBeforeUnmount(() => clearInterval(timer))
</script>
