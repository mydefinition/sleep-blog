<template>
  <div v-if="article" class="flex gap-10 max-w-[1000px] mx-auto px-4 py-8 items-start">
    <aside
        class="toc-sidebar w-[200px] shrink-0 sticky top-[68px] max-h-[calc(100vh-80px)] overflow-y-auto text-[0.85rem]"
        :style="tocBorderStyle">
      <p class="text-xs text-gray-400 tracking-[0.1em] m-0 mb-2">目录</p>
      <MdCatalog editorId="article-preview" scrollElement="html"/>
    </aside>

    <div class="flex-1 min-w-0 max-w-[680px]" ref="mainRef">
      <!-- 分类路径 -->
      <p v-if="categoryPath" class="text-[0.8rem] text-gray-400 m-0 mb-1">{{ categoryPath }}</p>

      <!-- 标题行：标题 + 编辑删除居右 -->
      <div class="flex items-end justify-between gap-4 mb-2">
        <h1 class="text-[1.8rem] px-[5px] pb-0 inline-block border-b-[3px] border-solid"
            :style="{ borderBottomColor: settings.primary }">{{ article.title }}</h1>
        <div v-if="canEdit" class="flex items-center gap-3 shrink-0 pb-1">
          <router-link :to="'/write/' + article.id"
                       class="inline-flex items-center gap-[3px] no-underline font-semibold text-[0.85rem] font-sans cursor-pointer hover:opacity-80 transition-opacity duration-150"
                       :style="{ color: settings.primary }">
            <Pencil :size="14"/> 编辑
          </router-link>
          <button @click="deleteArticle"
                  class="inline-flex items-center gap-[3px] no-underline font-semibold text-[0.85rem] font-sans cursor-pointer bg-transparent border-none p-0 hover:opacity-80 transition-opacity duration-150"
                  :style="{ color: settings.primary }">
            <Trash2 :size="14"/> 删除
          </button>
        </div>
      </div>

      <!-- 元信息行：日期、作者、标签居右 -->
      <div class="text-gray-400 text-[0.85rem] my-2 mb-4 flex gap-4 items-center flex-wrap">
        <span class="inline-flex items-center gap-1 whitespace-nowrap"><Calendar :size="14"/> {{ formatDate(article.createdAt) }}</span>
        <span class="inline-flex items-center gap-1 whitespace-nowrap"><User :size="14"/> {{ article.authorName }}</span>
        <span v-if="article.updatedAt" class="inline-flex items-center gap-1 whitespace-nowrap">修改于 {{ formatDate(article.updatedAt) }}</span>
        <div v-if="article.tags.length" class="flex flex-wrap gap-2 ml-auto">
          <span v-for="t in article.tags" :key="t.id"
                class="inline-flex items-center gap-[3px] text-[0.85rem] text-gray-500"><Tag :size="14"/> {{ t.name }}</span>
        </div>
      </div>

      <!-- 分隔线 -->
      <div class="flex items-center gap-3 mb-0">
        <span class="flex-1 border-b border-dashed border-gray-200"></span>
        <Scissors :size="16" class="shrink-0 text-gray-300 scale-x-[-1]"/>
      </div>

      <div class="content-wrap">
        <MdPreview id="article-preview" :modelValue="displayContent" :previewTheme="settings.previewTheme"
                   :codeTheme="settings.codeTheme"/>
      </div>

      <nav v-if="neighbors.prev || neighbors.next"
           class="flex justify-between items-start gap-4 mt-10 pt-6 border-t border-gray-100">
        <router-link v-if="neighbors.prev" :to="'/articles/' + neighbors.prev.id"
                     class="inline-flex items-center gap-1.5 max-w-[48%] no-underline text-[0.9rem] transition-opacity duration-150 hover:opacity-70"
                     :style="{ color: settings.primary }">
          <ChevronLeft :size="14"/>
          {{ neighbors.prev.title }}
        </router-link>
        <router-link v-if="neighbors.next" :to="'/articles/' + neighbors.next.id"
                     class="inline-flex items-center gap-1.5 max-w-[48%] no-underline text-[0.9rem] transition-opacity duration-150 hover:opacity-70 ml-auto text-right"
                     :style="{ color: settings.primary }">{{ neighbors.next.title }}
          <ChevronRight :size="14"/>
        </router-link>
      </nav>

      <section class="mt-12 border-t border-gray-100 pt-6">
        <h3 class="mb-4">评论 ({{ comments.length }})</h3>
        <div v-if="auth.isLoggedIn" class="mb-6">
          <textarea v-model="commentText" placeholder="写评论..." rows="3"
                    class="w-full px-2.5 py-2 border border-gray-200 rounded-md resize-y font-sans"></textarea>
          <button @click="postComment" :disabled="!commentText.trim()"
                  class="mt-2 px-5 py-1.5 text-white border-none rounded-md cursor-pointer disabled:opacity-40 disabled:cursor-not-allowed transition-opacity duration-150"
                  :style="{ background: settings.primary }">发表评论</button>
        </div>
        <p v-else>
          <button @click="openAuth('login')" class="text-primary bg-transparent border-none cursor-pointer p-0 text-inherit underline">登录</button>
          后发表评论
        </p>
        <div v-if="comments.length" class="flex flex-col gap-4">
          <div v-for="c in comments" :key="c.id" class="p-3 bg-gray-50 rounded-md">
            <div class="flex gap-3 items-center text-[0.85rem] mb-1">
              <strong>{{ c.username }}</strong>
              <span>{{ formatDate(c.createdAt) }}</span>
              <button v-if="auth.isAdmin"
                      class="ml-auto px-2 py-0.5 border-none bg-[#ff5252] text-white rounded cursor-pointer text-xs"
                      @click="deleteComment(c.id)">删除</button>
            </div>
            <p>{{ c.content }}</p>
          </div>
        </div>
      </section>
    </div>
  </div>

  <div v-else-if="loading" class="flex justify-center items-center min-h-[60vh]"><span
      class="inline-block w-9 h-9 border-[3px] border-gray-200 rounded-full animate-spin border-t-primary"></span></div>
</template>

<script setup lang="ts">
import MdPreview from 'md-editor-v3/lib/es/MdPreview.mjs'
import MdCatalog from 'md-editor-v3/lib/es/MdCatalog.mjs'
import {ref, onMounted, onUnmounted, computed, watch, inject} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {api} from '@/api'
import {useAuthStore} from '@/stores/auth'
import {useSettingsStore} from '@/stores/settings'
import {Calendar, User, Pencil, Trash2, Tag, Scissors, ChevronLeft, ChevronRight} from 'lucide-vue-next'
import 'md-editor-v3/lib/style.css'
import type {ArticleDetail, Comment} from '@/types'

const route = useRoute();
const router = useRouter()
const auth = useAuthStore();
const settings = useSettingsStore()
const article = ref<ArticleDetail | null>(null)
const comments = ref<Comment[]>([]);
const commentText = ref('')
const loading = ref(true)
const displayContent = computed(() => article.value?.content || '')
const allArticles = ref<ArticleDetail[]>([])
const openAuth = inject<(tab: 'login' | 'register') => void>('openAuth', () => {})
const neighbors = computed(() => {
  const idx = allArticles.value.findIndex(a => a.id === Number(route.params.id))
  return {
    prev: idx > 0 ? allArticles.value[idx - 1] : null,
    next: idx < allArticles.value.length - 1 ? allArticles.value[idx + 1] : null
  }
})

// SUPER 全可见，ADMIN 只看自己
const canEdit = computed(() => {
  if (!auth.user) return false
  if (auth.user.role === 'SUPER') return true
  return auth.user.role === 'ADMIN' && auth.user.id === article.value?.authorId
})

const categoryPath = computed(() => {
  const c = article.value?.categoryLevel
  if (!c) return ''
  return [c.level1, c.level2, c.level3]
      .filter(Boolean)
      .map(i => i!.name)
      .join(' / ')
})

async function fetchArticle() {
  try {
    const res = await api.get('/api/article/' + route.params.id);
    article.value = res.data
  } catch {
    router.push('/articles')
  } finally {
    loading.value = false
  }
}

async function fetchComments() {
  try {
    const res = await api.get('/api/articles/' + route.params.id + '/comments');
    comments.value = res.data || []
  } catch {
  }
}

async function postComment() {
  if (!commentText.value.trim()) return;
  try {
    await api.post('/api/articles/' + route.params.id + '/comments', {content: commentText.value});
    commentText.value = '';
    await fetchComments()
  } catch (e: any) {
    alert(e.message || '发表失败')
  }
}

async function deleteComment(id: number) {
  if (!confirm('确定删除该评论？')) return;
  try {
    await api.delete('/api/comments/' + id);
    await fetchComments()
  } catch (e: any) {
    alert(e.message || '删除失败')
  }
}

async function deleteArticle() {
  if (!confirm('确定删除这篇文章？')) return;
  try {
    await api.delete('/api/article/' + route.params.id);
    router.push('/articles')
  } catch (e: any) {
    alert(e.message || '删除失败')
  }
}

function formatDate(d: string) {
  return new Date(d).toISOString().slice(0, 10)
}

const mainRef = ref<HTMLElement | null>(null)
const scrollProgress = ref(0)
const progressPercent = computed(() => `${Math.round(scrollProgress.value * 100)}%`)
const tocBorderStyle = computed(() => ({
  borderRight: '2px solid',
  borderImage: `linear-gradient(to bottom, var(--primary) ${progressPercent.value}, #eee ${progressPercent.value}) 1`,
}))

function onScroll() {
  const el = mainRef.value;
  if (!el) return
  const rect = el.getBoundingClientRect()
  const total = rect.height - window.innerHeight
  if (total <= 0) {
    scrollProgress.value = 0;
    return
  }
  scrollProgress.value = Math.min(1, Math.max(0, -rect.top / total))
}

watch(
    () => route.params.id,
    (newId, oldId) => {
      if (newId && newId !== oldId) {
        loading.value = true
        fetchArticle()
        fetchComments()
        window.scrollTo(0, 0)
        scrollProgress.value = 0
      }
    }
)

onMounted(() => {
  fetchArticle();
  fetchComments();
  window.addEventListener('scroll', onScroll, {passive: true})
})
onUnmounted(() => {
  window.removeEventListener('scroll', onScroll)
})
</script>