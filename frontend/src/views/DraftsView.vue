<template>
  <div class="max-w-[900px] mx-auto px-4 py-8">
    <div class="flex justify-between items-center mb-4">
      <h2 class="m-0 font-semibold text-[1.2rem]">
        我的文章 <span class="text-[1rem] font-normal" :style="{ color: settings.primary }">MY ARTICLES</span>
      </h2>
      <span class="inline-flex items-center gap-1.5">
        <button
            class="px-3 py-1 text-[0.8rem] font-sans cursor-pointer rounded-full transition-all duration-150 border-none"
            :class="filterPublished === 0 ? 'bg-primary text-white' : 'bg-gray-100 text-gray-400 hover:text-gray-600'"
            @click="switchFilter(0)">
          草稿
        </button>
        <button
            class="px-3 py-1 text-[0.8rem] font-sans cursor-pointer rounded-full transition-all duration-150 border-none"
            :class="filterPublished === 1 ? 'bg-primary text-white' : 'bg-gray-100 text-gray-400 hover:text-gray-600'"
            @click="switchFilter(1)">
          已发布
        </button>
      </span>
    </div>

    <div v-if="loading" class="flex justify-center items-center min-h-[40vh]">
      <span class="inline-block w-8 h-8 border-[3px] border-gray-200 rounded-full animate-spin border-t-primary"></span>
    </div>
    <div v-else-if="errorMsg" class="text-[#ff5252] text-center py-8">{{ errorMsg }}</div>
    <div v-else-if="articles.length === 0" class="text-center py-16 text-gray-400 text-[0.95rem]">
      —— {{ filterPublished === 0 ? '暂无草稿' : '暂无已发布文章' }} QAQ ——
    </div>
    <div v-else>
      <ArticleCard
          v-for="a in articles" :key="a.id" :article="a"
          :selectedTags="[]" hoveredTag=""
          @click="(a) => $router.push('/write/' + a.id)"
      />
    </div>

    <PaginationBar :page="page" :totalPages="totalPages" @update:page="page = $event" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { api } from '@/api'
import { useAuthStore } from '@/stores/auth'
import { useSettingsStore } from '@/stores/settings'
import ArticleCard from '@/components/ArticleList/ArticleCard.vue'
import PaginationBar from '@/components/ArticleList/PaginationBar.vue'
import type { ArticleSummary } from '@/types'

const auth = useAuthStore()
const settings = useSettingsStore()

const articles = ref<ArticleSummary[]>([])
const loading = ref(false)
const errorMsg = ref('')
const page = ref(1)
const total = ref(0)
const pageSize = 10
const filterPublished = ref(0)

const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize)))

function switchFilter(v: number) {
  if (filterPublished.value === v) return
  filterPublished.value = v
  page.value = 1
}

async function fetchArticles() {
  loading.value = true
  errorMsg.value = ''
  const userId = auth.user?.id
  const isPub = filterPublished.value
  try {
    const url = `/api/query/articles/${userId}?isPublished=${isPub}&page=${page.value}&size=${pageSize}`
    const res = await api.get(url)
    articles.value = res.data.items || []
    total.value = res.data.total || 0
  } catch (e: any) {
    errorMsg.value = e.message || '加载失败'
  } finally {
    loading.value = false
  }
}

watch([page, filterPublished], () => fetchArticles())

onMounted(() => fetchArticles())
</script>