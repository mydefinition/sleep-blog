<template>
  <Navbar />
  <main class="container">
    <router-view />
  </main>
</template>

<script setup lang="ts">
import Navbar from '@/components/Navbar.vue'
import { useAuthStore } from '@/stores/auth'
import { onMounted } from 'vue'

const auth = useAuthStore()
onMounted(() => {
  if (auth.token) {
    auth.fetchProfile()
  }
})
</script>