<template>
  <div class="home">
    <div class="hero">
      <h1 class="goal-text">今日目标 : 早点睡觉</h1>
      <button class="random-btn" @click="goRandom">{{ loading ? '加载中...' : '随便看看' }}</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '@/api'
import { useSettingsStore } from '@/stores/settings'
import type { Article } from '@/types'

const router = useRouter()
const settings = useSettingsStore()
const articles = ref<Article[]>([])
const loading = ref(false)

async function goRandom() {
  if (articles.value.length === 0) {
    try {
      loading.value = true
      const res = await api.get('/api/articles?page=1&size=9999')
      articles.value = res.data.records || []
    } catch {
      router.push('/articles')
      return
    } finally {
      loading.value = false
    }
  }
  if (articles.value.length) {
    const idx = Math.floor(Math.random() * articles.value.length)
    router.push('/articles/' + articles.value[idx].id)
  } else {
    router.push('/articles')
  }
}
</script>

<style scoped>
.home {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: calc(100vh - 52px);
  text-align: center;
}

.hero {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2.5rem;
}

.goal-text {
  font-family: 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Noto Sans SC', sans-serif;
  font-size: 2.4rem;
  font-weight: 300;
  color: #333;
  margin: 0;
  letter-spacing: 0.06em;
}

.random-btn {
  padding: 0.6rem 2.2rem;
  background: v-bind('settings.primary');
  color: #fff;
  border: none;
  border-radius: 999px;
  font-size: 0.95rem;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  cursor: pointer;
  transition: opacity 0.2s, transform 0.15s;
}

.random-btn:hover {
  opacity: 0.85;
  transform: translateY(-1px);
}
</style>

<style scoped>
.goal-text {
  font-family: 'Hiragino Sans GB', 'Noto Sans SC', sans-serif;
  font-size: 2.4rem;
  font-weight: 300;
  color: #333;
  margin: 0;
  letter-spacing: 0.06em;
}

.goal-text::after {
  content: '.';
  display: inline-block;
  animation: blink 1.2s step-end infinite;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}
</style>