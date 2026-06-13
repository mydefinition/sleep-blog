<template>
  <div class="max-w-[1200px] mx-auto px-4 py-8 flex gap-8 items-start">
    <aside class="w-[220px] shrink-0 sticky top-[68px] border-r border-gray-100 pr-4">
      <h3 class="text-[1rem] text-gray-800 m-0 mb-3 font-semibold">
        分类 <span class="text-[0.8rem] font-normal" :style="{ color: settings.primary }">CATEGORY</span>
      </h3>
      <div class="flex items-center cursor-pointer select-none py-0.5" @click="onCategorySelect(0)">
        <span
            class="truncate inline-block px-3 py-[3px] rounded-full border border-transparent text-[0.88rem] transition-colors duration-150"
            :class="categoryId === 0 ? 'pill-solid font-semibold' : 'pill-ghost'"
        >全部</span>
      </div>
      <div v-if="categoryTree.length === 0" class="text-[0.82rem] text-gray-400 pl-3">暂无分类</div>
      <CategoryTree
          v-for="node in categoryTree" :key="node.id"
          :node="node" :selectedId="categoryId" :depth="0"
          @select="onCategorySelect"
      />
    </aside>

    <div class="flex-1 min-w-0 flex flex-col">
      <div class="flex justify-between items-center mb-4">
        <h2 class="m-0 font-semibold text-[1.2rem]">
          文章列表 <span class="text-[1rem] font-normal" :style="{ color: settings.primary }">ARTICLES</span>
        </h2>
        <span class="inline-flex items-center gap-1 text-gray-300 opacity-35 cursor-not-allowed relative group/tooltip">
          <input disabled placeholder="全文搜索 coming soon..."
                 class="px-3 py-1.5 border-0 border-b-2 border-gray-100 text-[0.9rem] outline-none w-[200px] text-gray-800 font-sans bg-transparent placeholder:text-gray-300 cursor-not-allowed" />
          <Search :size="14" class="shrink-0" />
          <span class="hidden group-hover/tooltip:block absolute top-[calc(100%+8px)] left-1/2 -translate-x-1/2 bg-gray-700 text-white px-3 py-1.5 rounded-md text-xs whitespace-nowrap z-[300] pointer-events-none after:content-[''] after:absolute after:bottom-full after:left-1/2 after:-translate-x-1/2 after:border-[6px] after:border-transparent after:border-b-gray-700">全文搜索功能开发中，敬请期待</span>
        </span>
      </div>

      <div v-if="loading" class="flex justify-center items-center min-h-[40vh]">
        <span class="inline-block w-8 h-8 border-[3px] border-gray-200 rounded-full animate-spin border-t-primary"></span>
      </div>
      <div v-else-if="errorMsg" class="text-[#ff5252] text-center py-8">{{ errorMsg }}</div>
      <div v-else-if="articles.length === 0" class="text-center py-16 text-gray-400 text-[0.95rem]">
        —— 暂无文章 QAQ ——
      </div>
      <div v-else>
        <ArticleCard
            v-for="a in articles" :key="a.id" :article="a"
            :selectedTags="selectedTags" :hoveredTag="hoveredTag"
            @click="(a) => $router.push('/articles/' + a.id)"
            @selectTag="selectTag"
            @hoverTag="(name) => { hoveredTag = name }"
        />
      </div>

      <PaginationBar :page="page" :totalPages="totalPages" @update:page="page = $event" />
    </div>

    <aside class="w-[200px] shrink-0 sticky top-[68px] border-l border-gray-100 pl-4">
      <h3 class="text-[1rem] text-gray-800 m-0 mb-2.5 font-semibold flex justify-between items-center">
        <span>
          标签 <span class="text-[0.8rem] font-normal" :style="{ color: settings.primary }">TAGS</span>
        </span>
        <span class="inline-flex gap-1.5 items-center">
          <button class="mode-btn" :class="{ active: andMode && selectedTags.length }" :disabled="!selectedTags.length"
                  @click="andMode = true">&&</button>
          <button class="mode-btn" :class="{ active: !andMode && selectedTags.length }" :disabled="!selectedTags.length"
                  @click="andMode = false">||</button>
        </span>
      </h3>
      <TagFilter
          :tags="allTags"
          :hoveredTag="hoveredTag"
          v-model:selected-tags="selectedTags"
          @hoverTag="(name) => { hoveredTag = name }"
      />
    </aside>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { api } from '@/api'
import { useSettingsStore } from '@/stores/settings'
import { Search } from 'lucide-vue-next'
import ArticleCard from '@/components/ArticleList/ArticleCard.vue'
import PaginationBar from '@/components/ArticleList/PaginationBar.vue'
import TagFilter from '@/components/ArticleList/TagFilter.vue'
import CategoryTree from '@/components/CategoryTree.vue'
import type { ArticleSummary, Tag as TagType, CategoryVO } from '@/types'

const articles = ref<ArticleSummary[]>([])
const allTags = ref<TagType[]>([])
const categoryTree = ref<CategoryVO[]>([])
const loading = ref(true)
const errorMsg = ref('')
const page = ref(1)
const total = ref(0)
const pageSize = 10
const selectedTags = ref<number[]>([])
const andMode = ref(false)
const categoryId = ref(0)
const hoveredTag = ref('')
const settings = useSettingsStore()

const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize)))

function selectTag(id: number) {
  const arr = [...selectedTags.value]
  const i = arr.indexOf(id)
  i >= 0 ? arr.splice(i, 1) : arr.push(id)
  selectedTags.value = arr
  page.value = 1
}

function onCategorySelect(id: number) {
  categoryId.value = id
  page.value = 1
}

async function fetchArticles() {
  loading.value = true
  errorMsg.value = ''
  try {
    const p = new URLSearchParams()
    p.set('page', String(page.value))
    p.set('size', String(pageSize))
    p.set('and', String(andMode.value))
    p.set('categoryId', String(categoryId.value))
    selectedTags.value.forEach(id => p.append('tagIds', String(id)))
    const res = await api.get('/api/query/articles?' + p.toString())
    articles.value = res.data.items || []
    total.value = res.data.total || 0
  } catch (e: any) {
    errorMsg.value = e.message || '加载失败'
  } finally {
    loading.value = false
  }
}

watch([selectedTags, andMode, categoryId, page], () => fetchArticles())

onMounted(async () => {
  const [catRes, tagRes] = await Promise.allSettled([
    api.get('/api/query/categories'),
    api.get('/api/query/tags'),
  ])
  if (catRes.status === 'fulfilled') categoryTree.value = catRes.value.data?.children || []
  if (tagRes.status === 'fulfilled') allTags.value = tagRes.value.data || []
  fetchArticles()
})
</script>