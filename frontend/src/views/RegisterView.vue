<template>
  <div class="form-page">
    <h2>注册</h2>
    <form @submit.prevent="handleRegister">
      <input v-model="username" type="text" placeholder="用户名" required />
      <input v-model="password" type="password" placeholder="密码" required />
      <p v-if="error" class="error">{{ error }}</p>
      <p v-if="success" class="success">注册成功！<router-link to="/login">去登录</router-link></p>
      <button type="submit">注册</button>
    </form>
    <p class="switch">已有账号？<router-link to="/login">登录</router-link></p>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { api } from '@/api'

const username = ref('')
const password = ref('')
const error = ref('')
const success = ref(false)

async function handleRegister() {
  error.value = ''
  try {
    await api.post('/api/auth/register', { username: username.value, password: password.value })
    success.value = true
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
button { padding: 0.6rem; background: #42b883; color: #fff; border: none; border-radius: 6px; font-size: 1rem; cursor: pointer; }
button:hover { background: #38a572; }
.error { color: #ff5252; font-size: 0.85rem; }
.success { color: #2e7d32; font-size: 0.85rem; }
.switch { text-align: center; margin-top: 1rem; font-size: 0.9rem; color: #666; }
</style>