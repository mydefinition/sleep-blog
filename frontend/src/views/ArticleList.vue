<template>
  <div class="max-w-[1200px] mx-auto px-4 py-8 flex gap-8 items-start">
    <aside v-if="tagTree.length" class="w-[240px] shrink-0 sticky top-[68px] border-r border-gray-100 pr-4">
      <h3 class="text-[0.9rem] text-gray-800 m-0 mb-2.5">标签列表</h3>
      <div class="flex flex-wrap gap-1.5">
        <span
          v-for="t in tagTree" :key="t.id"
          class="inline-flex items-center gap-[3px] px-2.5 py-1 border border-transparent rounded-md cursor-pointer text-[0.82rem] text-gray-600 transition-all duration-150 filter-tag-hover"
          :class="{ '!bg-primary !text-white': selectedTags.includes(t.id), 'filter-tag-hover': hoveredTag === t.name && !selectedTags.includes(t.id) }"
          @click="selectTag(t.id)"
          @mouseenter="hoveredTag = t.name"
          @mouseleave="hoveredTag = ''"
        ><Tag :size="14" /> {{ t.name }}</span>
      </div>
    </aside>

    <div class="flex-1 min-w-0">
      <div class="flex justify-between items-center mb-4">
        <h2 class="m-0">文章列表</h2>
        <span class="inline-flex items-center gap-1 transition-colors duration-150" :class="{ 'text-primary': search || searchFocused }">
          <input v-model="search" placeholder="搜索标题..." class="px-3 py-1.5 border-0 border-b-2 border-gray-100 text-[0.9rem] outline-none w-[200px] text-gray-800 font-sans bg-transparent transition-[border-color] duration-150 placeholder:text-gray-300 focus:border-b-primary" @focus="searchFocused = true" @blur="searchFocused = false" />
          <Search :size="14" class="text-gray-300 shrink-0" />
        </span>
      </div>
      <div v-if="loading" class="flex justify-center items-center min-h-[40vh]"><span class="inline-block w-8 h-8 border-[3px] border-gray-200 rounded-full animate-spin border-t-primary"></span></div>
      <div v-else-if="errorMsg">{{ errorMsg }}</div>
      <div v-else-if="paged.length === 0" class="text-center py-16 text-gray-400 text-[0.95rem]">—— 暂无文章 QAQ ——</div>
      <div v-else class="flex flex-col">
        <ArticleCard
          v-for="a in paged" :key="a.id" :article="a" :selectedTags="selectedTags" :hoveredTag="hoveredTag"
          @click="(a) => $router.push('/articles/' + a.id)"
          @selectTag="selectTag"
          @hoverTag="(name) => { hoveredTag = name }"
        />
      </div>
    </div>

    <div v-if="totalPages > 1" class="flex justify-center items-center gap-4 mt-6">
      <button :disabled="page <= 1" @click="page--" class="px-4 py-1.5 border border-gray-200 bg-white rounded-md cursor-pointer disabled:opacity-40 disabled:cursor-not-allowed hover:border-primary hover:text-primary transition-colors duration-150">上一页</button>
      <span>{{ page }} / {{ totalPages }}</span>
      <button :disabled="page >= totalPages" @click="page++" class="px-4 py-1.5 border border-gray-200 bg-white rounded-md cursor-pointer disabled:opacity-40 disabled:cursor-not-allowed hover:border-primary hover:text-primary transition-colors duration-150">下一页</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { api } from '@/api'
import ArticleCard from '@/components/ArticleCard.vue'
import { Tag, Search } from 'lucide-vue-next'
import type { Article, Tag as TagType } from '@/types'

const allArticles = ref<Article[]>([]); const loading = ref(true); const errorMsg = ref('')
const page = ref(1); const pageSize = 10
const tagTree = ref<TagType[]>([]); const selectedTags = ref<number[]>([])
const hoveredTag = ref(''); const search = ref(''); const searchFocused = ref(false)

const filtered = computed(() => {
  let arr = allArticles.value
  if (search.value) arr = arr.filter(a => a.title.includes(search.value))
  if (!selectedTags.value.length) return arr
  return arr.filter(a => {
    const ids = (a.tags || []).map(t => t.id)
    return selectedTags.value.every(id => ids.includes(id)) || selectedTags.value.some(id => ids.includes(id))
  })
})

const totalPages = computed(() => Math.ceil(filtered.value.length / pageSize) || 1)
const paged = computed(() => { const start = (page.value - 1) * pageSize; return filtered.value.slice(start, start + pageSize) })

function selectTag(id: number) { const idx = selectedTags.value.indexOf(id); idx >= 0 ? selectedTags.value.splice(idx, 1) : selectedTags.value.push(id); page.value = 1 }

async function loadAll() { loading.value = true; errorMsg.value = ''; try { const res = await api.get('/api/articles?page=1&size=9999'); allArticles.value = res.data.records } catch (e: any) { errorMsg.value = e.message || '加载失败' } finally { loading.value = false } }
async function fetchTags() { try { const res = await api.get('/api/tags'); tagTree.value = res.data } catch {} }

onMounted(() => { fetchTags(); loadAll() })
</script>
