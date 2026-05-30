<template>
  <div class="profile" v-if="auth.user">
    <h2>个人信息</h2>
    <div class="info">
      <p><strong>用户名：</strong>{{ auth.user.username }}</p>
      <p><strong>角色：</strong>{{ auth.user.role === 'ADMIN' ? '管理员' : '普通用户' }}</p>
      <p><strong>注册时间：</strong>{{ formatDate(auth.user.createdAt) }}</p>
    </div>
    <router-link to="/profile/edit" class="btn">修改信息</router-link>
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'
const auth = useAuthStore()

function formatDate(d: string) {
  return new Date(d).toLocaleDateString('zh-CN')
}
</script>

<style scoped>
.profile { max-width: 500px; margin: 3rem auto; padding: 2rem; }
h2 { margin-bottom: 1.5rem; }
.info p { margin: 0.8rem 0; font-size: 1.05rem; }
.btn { display: inline-block; margin-top: 1rem; padding: 0.5rem 1.2rem; background: #42b883; color: #fff; border-radius: 6px; text-decoration: none; }
</style>