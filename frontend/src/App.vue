<template>
  <Navbar @open-auth="authModal?.open($event)" />
  <main>
    <router-view />
  </main>
  <AuthModal ref="authModal" />
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue'
import Navbar from '@/components/Navbar.vue'
import AuthModal from '@/components/AuthModal.vue'
import { provide } from 'vue'
const authModal = ref<InstanceType<typeof AuthModal> | null>(null)

provide('openAuth', (tab: 'login' | 'register') => authModal.value?.open(tab))

let timer: ReturnType<typeof setInterval>

// 心跳
onMounted(() => {
  timer = setInterval(() => fetch('/api/heartbeat').catch(() => {}), 60 * 1000)
})

onBeforeUnmount(() => clearInterval(timer))
</script>