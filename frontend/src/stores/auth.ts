import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { api } from '@/api'

export const useAuthStore = defineStore('auth', () => {
  const user = ref<any>(null)
  const loading = ref(false)

  const isLoggedIn = computed(() => user.value != null)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')

  async function fetchProfile() {
    loading.value = true
    try {
      const res = await api.get('/api/user/profile')
      user.value = res.data
    } catch {
      user.value = null
    } finally {
      loading.value = false
    }
  }

  async function logout() {
    try {
      await api.post('/api/user/logout')
    } catch {
      // ignore
    }
    user.value = null
  }

  return { user, loading, isLoggedIn, isAdmin, fetchProfile, logout }
})