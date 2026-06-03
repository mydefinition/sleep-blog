<template>
  <div class="max-w-[400px] mx-auto my-12 p-8">
    <h2 class="text-center mb-6">修改信息</h2>
    <form @submit.prevent="handleUpdate" class="flex flex-col gap-3">
      <label class="text-[0.9rem] text-gray-500">用户名</label>
      <input v-model="username" type="text" placeholder="留空不修改" class="px-2.5 py-2 border border-gray-200 rounded-md text-base" />
      <label class="text-[0.9rem] text-gray-500">新密码</label>
      <input v-model="password" type="password" placeholder="留空不修改" class="px-2.5 py-2 border border-gray-200 rounded-md text-base" />
      <p v-if="error" class="text-[#ff5252] text-[0.85rem]">{{ error }}</p>
      <p v-if="success" class="text-[#2e7d32] text-[0.85rem]">修改成功</p>
      <button type="submit" class="py-2.5 bg-primary text-white border-none rounded-md text-base cursor-pointer mt-2 hover:opacity-85">保存</button>
    </form>
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

async function handleUpdate() {
  error.value = ''
  try {
    await api.put('/api/user/profile', { username: username.value, password: password.value })
    success.value = true
    await auth.fetchProfile()
    setTimeout(() => router.push('/profile'), 1000)
  } catch (e: any) {
    error.value = e.message
  }
}
</script>
