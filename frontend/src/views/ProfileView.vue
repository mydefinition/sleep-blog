<template>
  <div v-if="auth.user" class="max-w-[500px] mx-auto my-12 p-8">
    <h2 class="mb-6">个人信息</h2>
    <div class="info">
      <p class="my-3 text-[1.05rem]"><strong>用户名：</strong>{{ auth.user.username }}</p>
      <p class="my-3 text-[1.05rem]"><strong>角色：</strong>{{ auth.user.role === 'ADMIN' ? '管理员' : '普通用户' }}</p>
      <p class="my-3 text-[1.05rem]"><strong>注册时间：</strong>{{ formatDate(auth.user.createdAt) }}</p>
    </div>
    <router-link to="/profile/edit" class="inline-block mt-4 px-5 py-2 bg-primary text-white rounded-md no-underline">修改信息</router-link>
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'
const auth = useAuthStore()

function formatDate(d: string) {
  return new Date(d).toLocaleDateString('zh-CN')
}
</script>
