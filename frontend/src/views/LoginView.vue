<template>
  <div class="max-w-[400px] mx-auto my-16 p-8">
    <h2 class="text-center mb-6">登录</h2>
    <form @submit.prevent="handleLogin" class="flex flex-col gap-4">
      <input v-model="username" type="text" placeholder="用户名" required class="px-2.5 py-2 border border-gray-200 rounded-md text-base" />
      <input v-model="password" type="password" placeholder="密码" required class="px-2.5 py-2 border border-gray-200 rounded-md text-base" />
      <p v-if="error" class="text-[#ff5252] text-[0.85rem]">{{ error }}</p>
      <button type="submit" class="py-2.5 bg-primary text-white border-none rounded-md text-base cursor-pointer hover:opacity-85">登录</button>
    </form>
    <p class="text-center mt-4 text-[0.9rem] text-gray-500">没有账号？<router-link to="/register">注册</router-link></p>
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
