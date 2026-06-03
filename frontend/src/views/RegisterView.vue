<template>
  <div class="max-w-[400px] mx-auto my-16 p-8">
    <h2 class="text-center mb-6">注册</h2>
    <form @submit.prevent="handleRegister" class="flex flex-col gap-4">
      <input v-model="username" type="text" placeholder="用户名" required class="px-2.5 py-2 border border-gray-200 rounded-md text-base" />
      <input v-model="password" type="password" placeholder="密码" required class="px-2.5 py-2 border border-gray-200 rounded-md text-base" />
      <p v-if="error" class="text-[#ff5252] text-[0.85rem]">{{ error }}</p>
      <p v-if="success" class="text-[#2e7d32] text-[0.85rem]">注册成功，跳转中...</p>
      <button type="submit" class="py-2.5 bg-primary text-white border-none rounded-md text-base cursor-pointer hover:opacity-85">注册</button>
    </form>
    <p class="text-center mt-4 text-[0.9rem] text-gray-500">已有账号？<router-link to="/login">登录</router-link></p>
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
const success = ref(false)

async function handleRegister() {
  error.value = ''
  try {
    await api.post('/api/user/register', { username: username.value, password: password.value })
    success.value = true
    await auth.fetchProfile()
    setTimeout(() => router.push('/'), 1000)
  } catch (e: any) {
    error.value = e.message
  }
}
</script>
