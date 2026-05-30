<template>
  <div class="article-list">
    <aside class="tag-panel" v-if="tagTree.length">
      <h3>标签列表
        <span class="mode-btns">
          <button class="mode-btn" :class="{ active: andMode && selectedTags.length }" :disabled="!selectedTags.length" @click="andMode = true">&amp;&amp;</button>
          <button class="mode-btn" :class="{ active: !andMode && selectedTags.length }" :disabled="!selectedTags.length" @click="andMode = false">||</button>
        </span>
      </h3>
      <div class="tag-filter">
        <span v-for="t in tagTree" :key="t.id" class="filter-tag" :class="{ active: selectedTags.includes(t.id), hovered: hoveredTag === t.name }" @click="selectTag(t.id)" @mouseenter="hoveredTag = t.name" @mouseleave="hoveredTag = ''"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12.586 2.586A2 2 0 0 0 11.172 2H4a2 2 0 0 0-2 2v7.172a2 2 0 0 0 .586 1.414l8.704 8.704a2.426 2.426 0 0 0 3.42 0l6.58-6.58a2.426 2.426 0 0 0 0-3.42z"/><circle cx="7.5" cy="7.5" r=".5" fill="currentColor"/></svg> {{ t.name }}</span>
      </div>
    </aside>

    <div class="main-content">
      <div class="list-header">
        <h2>文章列表</h2>
        <span class="search-wrap" :class="{ active: search || searchFocused }">
          <input v-model="search" placeholder="搜索标题..." class="search-input" @focus="searchFocused = true" @blur="searchFocused = false" />
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"/><path d="M21 21l-4.35-4.35"/></svg>
        </span>
      </div>
      <div v-if="loading" class="loading-wrap"><span class="spinner"></span></div>
      <div v-else-if="errorMsg">{{ errorMsg }}</div>
      <div v-else-if="paged.length === 0" class="empty">—— 暂无文章 QAQ ——</div>
      <div v-else class="cards">
        <ArticleCard
          v-for="a in paged"
          :key="a.id"
          :article="a"
          :selectedTags="selectedTags"
          :hoveredTag="hoveredTag"
          @click="(a) => $router.push('/articles/' + a.id)"
          @selectTag="selectTag"
          @hoverTag="(name) => { hoveredTag = name }"
        />
      </div>
    </div>

    <div class="pagination" v-if="totalPages > 1">
      <button :disabled="page <= 1" @click="page--">上一页</button>
      <span>{{ page }} / {{ totalPages }}</span>
      <button :disabled="page >= totalPages" @click="page++">下一页</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { api } from '@/api'
import { useSettingsStore } from '@/stores/settings'
import ArticleCard from '@/components/ArticleCard.vue'
import type { Article, Tag } from '@/types'

const allArticles = ref<Article[]>([])
const loading = ref(true)
const errorMsg = ref('')
const page = ref(1)
const pageSize = 10
const tagTree = ref<Tag[]>([])
const selectedTags = ref<number[]>([])
const hoveredTag = ref('')
const andMode = ref(false)
const search = ref('')
const searchFocused = ref(false)
const settings = useSettingsStore()

const filtered = computed(() => {
  let arr = allArticles.value
  if (search.value) arr = arr.filter(a => a.title.includes(search.value))
  if (!selectedTags.value.length) return arr
  return arr.filter(a => {
    const ids = (a.tags || []).map(t => t.id)
    return andMode.value
      ? selectedTags.value.every(id => ids.includes(id))
      : selectedTags.value.some(id => ids.includes(id))
  })
})

const totalPages = computed(() => Math.ceil(filtered.value.length / pageSize) || 1)
const paged = computed(() => {
  const start = (page.value - 1) * pageSize
  return filtered.value.slice(start, start + pageSize)
})

function selectTag(id: number) {
  const idx = selectedTags.value.indexOf(id)
  idx >= 0 ? selectedTags.value.splice(idx, 1) : selectedTags.value.push(id)
  page.value = 1
}

async function loadAll() {
  loading.value = true; errorMsg.value = ''
  try {
    const res = await api.get('/api/articles?page=1&size=9999')
    allArticles.value = res.data.records
  } catch (e: any) {
    errorMsg.value = e.message || '加载失败'
  } finally {
    loading.value = false
  }
}

async function fetchTags() {
  try { const res = await api.get('/api/tags'); tagTree.value = res.data } catch {}
}

onMounted(() => { fetchTags(); loadAll() })
</script>

<style scoped>
.article-list { max-width: 1200px; margin: 0 auto; padding: 2rem 1rem; display: flex; gap: 2rem; align-items: flex-start; }
.tag-panel { width: 240px; flex-shrink: 0; position: sticky; top: 68px; border-right: 1px solid #eee; padding-right: 1rem; }
.tag-panel h3 { font-size: 0.9rem; color: #333; margin: 0 0 0.6rem; display: flex; justify-content: space-between; align-items: center; }
.main-content { flex: 1; min-width: 0; }

.tag-filter { display: flex; flex-wrap: wrap; gap: 0.35rem; }
.filter-tag { display: inline-flex; align-items: center; gap: 3px; padding: 0.25rem 0.6rem; border: 1px solid transparent; border-radius: 6px; cursor: pointer; font-size: 0.82rem; color: #555; transition: all 0.15s; }
.filter-tag:hover, .filter-tag.hovered { background: color-mix(in srgb, var(--primary) 10%, white); color: var(--primary); }
.filter-tag.active { background: var(--primary); color: #fff; }

.list-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; }
.list-header h2 { margin: 0; }
.search-wrap { display: inline-flex; align-items: center; gap: 0.3rem; }
.search-wrap svg { color: #ccc; flex-shrink: 0; transition: color 0.15s; }
.search-wrap.active svg { color: var(--primary); }
.search-input { padding: 0.4rem 0.8rem; border: none; border-bottom: 2px solid #eee; font-size: 0.9rem; outline: none; width: 200px; color: #333; font-family: inherit; background: transparent; transition: border-color 0.15s; }
.search-input:focus { border-bottom-color: var(--primary); }
.search-input::placeholder { color: #ccc; }

.cards { display: flex; flex-direction: column; }

.pagination { display: flex; justify-content: center; align-items: center; gap: 1rem; margin-top: 1.5rem; }
.pagination button { padding: 0.4rem 1rem; border: 1px solid #ddd; background: #fff; border-radius: 6px; cursor: pointer; }
.pagination button:disabled { opacity: 0.4; cursor: not-allowed; }
.pagination button:hover:not(:disabled) { border-color: var(--primary); color: var(--primary); }

.loading-wrap { display: flex; justify-content: center; align-items: center; min-height: 40vh; }
.spinner { width: 32px; height: 32px; border: 3px solid #e0e0e0; border-top-color: var(--primary); border-radius: 50%; animation: spin 0.7s linear infinite; }
.empty { text-align: center; padding: 4rem 0; color: #bbb; font-size: 0.95rem; }

.mode-btns { display: inline-flex; gap: 6px; align-items: center; }
.mode-btn { background: none; border: none; padding: 0; width: 30px; text-align: center; cursor: pointer; font-size: 1.15rem; color: #bbb; font-family: 'Times New Roman', 'STIX Two Text', 'Latin Modern Math', serif; font-weight: 700; line-height: 1; transition: color 0.15s; }
.mode-btn:hover { color: #999; }
.mode-btn.active { color: var(--primary); }
.mode-btn:last-child { position: relative; top: -3px; }
.mode-btn:disabled { opacity: 0.3; cursor: not-allowed; color: #ddd; }

@keyframes spin { to { transform: rotate(360deg); } }
</style>