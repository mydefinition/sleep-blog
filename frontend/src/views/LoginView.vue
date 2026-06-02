<template>
  <div class="form-page">
    <h2>登录</h2>
    <form @submit.prevent="handleLogin">
      <input v-model="username" type="text" placeholder="用户名" required />
      <input v-model="password" type="password" placeholder="密码" required />
      <p v-if="error" class="error">{{ error }}</p>
      <button type="submit">登录</button>
    </form>
    <p class="switch">没有账号？<router-link to="/register">注册</router-link></p>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { api } from '@/api'

const router = useRouter()
const auth = useAuthStore()
const username = ref('')
const password = ref('')
const error = ref('')

async function handleLogin() {
  error.value = ''
  try {
    await api.post('/api/user/login', { username: username.value, password: password.value })
    await auth.fetchProfile()
    router.push('/')
  } catch (e: any) {
    error.value = e.message
  }
}
</script>

<style scoped>
.form-page { max-width: 400px; margin: 4rem auto; padding: 2rem; }
h2 { text-align: center; margin-bottom: 1.5rem; }
form { display: flex; flex-direction: column; gap: 1rem; }
input { padding: 0.6rem; border: 1px solid #ddd; border-radius: 6px; font-size: 1rem; }
button { padding: 0.6rem; background: var(--primary); color: #fff; border: none; border-radius: 6px; font-size: 1rem; cursor: pointer; }
button:hover { opacity: 0.85; }
.error { color: #ff5252; font-size: 0.85rem; }
.switch { text-align: center; margin-top: 1rem; font-size: 0.9rem; color: #666; }
</style>