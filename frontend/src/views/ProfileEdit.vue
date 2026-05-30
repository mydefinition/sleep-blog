<template>
  <div class="form-page">
    <h2>修改信息</h2>
    <form @submit.prevent="handleUpdate">
      <label>用户名</label>
      <input v-model="username" type="text" placeholder="留空不修改" />
      <label>新密码</label>
      <input v-model="password" type="password" placeholder="留空不修改" />
      <p v-if="error" class="error">{{ error }}</p>
      <p v-if="success" class="success">修改成功</p>
      <button type="submit">保存</button>
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

<style scoped>
.form-page { max-width: 400px; margin: 3rem auto; padding: 2rem; }
h2 { text-align: center; margin-bottom: 1.5rem; }
form { display: flex; flex-direction: column; gap: 0.8rem; }
label { font-size: 0.9rem; color: #666; }
input { padding: 0.6rem; border: 1px solid #ddd; border-radius: 6px; font-size: 1rem; }
button { padding: 0.6rem; background: #42b883; color: #fff; border: none; border-radius: 6px; font-size: 1rem; cursor: pointer; margin-top: 0.5rem; }
button:hover { background: #38a572; }
.error { color: #ff5252; font-size: 0.85rem; }
.success { color: #2e7d32; font-size: 0.85rem; }
</style>