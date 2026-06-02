<template>
  <Navbar />
  <main class="container">
    <router-view />
  </main>
</template>

<script setup lang="ts">
import Navbar from '@/components/Navbar.vue'
import { useSettingsStore } from '@/stores/settings'
import { onMounted } from 'vue'

const settings = useSettingsStore()
onMounted(() => {
  settings.apply()
})
</script>