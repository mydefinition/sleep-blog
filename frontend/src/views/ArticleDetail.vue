<template>
  <div v-if="article" class="flex gap-10 max-w-[1000px] mx-auto px-4 py-8 items-start">
    <aside class="w-[200px] shrink-0 sticky top-[68px] max-h-[calc(100vh-80px)] overflow-y-auto text-[0.85rem] toc-sidebar">
      <p class="text-xs text-gray-400 tracking-[0.1em] m-0 mb-2">目录</p>
      <MdCatalog editorId="article-preview" scrollElement="html" />
    </aside>

    <div class="flex-1 min-w-0 max-w-[680px]" ref="mainRef">
      <h1 class="text-[1.8rem] mb-2 px-[5px] pb-2 inline-block border-b-[3px] title-underline">{{ article.title }}</h1>

      <div class="text-gray-400 text-[0.85rem] my-2 mb-4 flex gap-4 items-center flex-wrap">
        <span class="inline-flex items-center gap-1 whitespace-nowrap">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M8 2v4"/><path d="M16 2v4"/><rect width="18" height="18" x="3" y="4" rx="2"/><path d="M3 10h18"/><path d="M8 14h.01"/><path d="M12 14h.01"/><path d="M16 14h.01"/><path d="M8 18h.01"/><path d="M12 18h.01"/><path d="M16 18h.01"/></svg>
          {{ formatDate(article.createdAt) }}
        </span>
        <span class="inline-flex items-center gap-1 whitespace-nowrap">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
          {{ article.authorName }}
        </span>
        <span v-if="article.updatedAt" class="inline-flex items-center gap-1 whitespace-nowrap">修改于 {{ formatDate(article.updatedAt) }}</span>
        <router-link v-if="auth.isAdmin" :to="'/write/' + article.id" class="edit-link"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 3a2.85 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5Z"/></svg> 编辑</router-link>
        <button v-if="auth.isAdmin" @click="deleteArticle" class="del-link"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 6h18"/><path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"/><path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"/></svg> 删除</button>
      </div>

      <div v-if="article.tags.length" class="flex flex-wrap gap-4 mb-4">
        <span v-for="t in article.tags" :key="t.id" class="inline-flex items-center gap-[3px] text-[0.85rem] text-gray-500"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12.586 2.586A2 2 0 0 0 11.172 2H4a2 2 0 0 0-2 2v7.172a2 2 0 0 0 .586 1.414l8.704 8.704a2.426 2.426 0 0 0 3.42 0l6.58-6.58a2.426 2.426 0 0 0 0-3.42z"/><circle cx="7.5" cy="7.5" r=".5" fill="currentColor"/></svg> {{ t.name }}</span>
      </div>

      <div class="flex items-center gap-3 mb-0">
        <span class="flex-1 border-b border-dashed border-gray-200"></span>
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#ccc" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="transform: scaleX(-1)" class="shrink-0"><circle cx="6" cy="6" r="3"/><path d="M8.12 8.12 12 12"/><path d="M20 4 8.12 15.88"/><circle cx="6" cy="18" r="3"/><path d="M14.8 14.8 20 20"/></svg>
      </div>

      <div class="content-wrap">
        <MdPreview
          id="article-preview"
          :modelValue="displayContent"
          :previewTheme="settings.previewTheme"
          :codeTheme="settings.codeTheme"
        />
      </div>

      <nav v-if="neighbors.prev || neighbors.next" class="flex justify-between items-start gap-4 mt-10 pt-6 border-t border-gray-100">
        <router-link v-if="neighbors.prev" :to="'/articles/' + neighbors.prev.id" class="nav-prev">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m15 18-6-6 6-6"/></svg>
          {{ neighbors.prev.title }}
        </router-link>
        <router-link v-if="neighbors.next" :to="'/articles/' + neighbors.next.id" class="nav-next">
          {{ neighbors.next.title }}
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m9 18 6-6-6-6"/></svg>
        </router-link>
      </nav>

      <section class="mt-12 border-t border-gray-100 pt-6">
        <h3 class="mb-4">评论 ({{ comments.length }})</h3>
        <div v-if="auth.isLoggedIn" class="mb-6">
          <textarea v-model="commentText" placeholder="写评论..." rows="3" class="w-full px-2.5 py-2 border border-gray-200 rounded-md resize-y font-sans"></textarea>
          <button @click="postComment" :disabled="!commentText.trim()" class="mt-2 px-5 py-1.5 bg-primary text-white border-none rounded-md cursor-pointer disabled:opacity-40 disabled:cursor-not-allowed">发表评论</button>
        </div>
        <p v-else><router-link to="/login">登录</router-link>后发表评论</p>
        <div v-if="comments.length" class="flex flex-col gap-4">
          <div v-for="c in comments" :key="c.id" class="p-3 bg-gray-50 rounded-md">
            <div class="flex gap-3 items-center text-[0.85rem] mb-1">
              <strong>{{ c.username }}</strong>
              <span>{{ formatDate(c.createdAt) }}</span>
              <button v-if="auth.isAdmin" class="ml-auto px-2 py-0.5 border-none bg-[#ff5252] text-white rounded cursor-pointer text-xs" @click="deleteComment(c.id)">删除</button>
            </div>
            <p>{{ c.content }}</p>
          </div>
        </div>
      </section>
    </div>
  </div>

  <div v-else-if="loading" class="flex justify-center items-center min-h-[60vh]"><span class="spinner"></span></div>
</template>

<script setup lang="ts">
import MdPreview from 'md-editor-v3/lib/es/MdPreview.mjs'
import MdCatalog from 'md-editor-v3/lib/es/MdCatalog.mjs'
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { api } from '@/api'
import { useAuthStore } from '@/stores/auth'
import { useSettingsStore } from '@/stores/settings'
import 'md-editor-v3/lib/style.css'
import type { Article, Comment } from '@/types'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const settings = useSettingsStore()
const article = ref<Article | null>(null)
const comments = ref<Comment[]>([])
const commentText = ref('')
const loading = ref(true)
const displayContent = computed(() => article.value?.content || '')

const allArticles = ref<Article[]>([])
const neighbors = computed(() => {
  const idx = allArticles.value.findIndex(a => a.id === Number(route.params.id))
  return {
    prev: idx < allArticles.value.length - 1 ? allArticles.value[idx + 1] : null,
    next: idx > 0 ? allArticles.value[idx - 1] : null,
  }
})

async function fetchArticle() {
  try {
    const res = await api.get('/api/articles/' + route.params.id)
    article.value = res.data
  } catch {
    router.push('/articles')
  } finally {
    loading.value = false
  }
}

async function fetchAll() {
  try {
    const res = await api.get('/api/articles?page=1&size=9999')
    allArticles.value = res.data.records || []
  } catch {}
}

async function fetchComments() {
  try {
    const res = await api.get('/api/articles/' + route.params.id + '/comments')
    comments.value = res.data || []
  } catch {}
}

async function postComment() {
  if (!commentText.value.trim()) return
  try {
    await api.post('/api/articles/' + route.params.id + '/comments', { content: commentText.value })
    commentText.value = ''
    await fetchComments()
  } catch (e: any) {
    alert(e.message || '发表失败')
  }
}

async function deleteComment(id: number) {
  if (!confirm('确定删除该评论？')) return
  try {
    await api.delete('/api/comments/' + id)
    await fetchComments()
  } catch (e: any) {
    alert(e.message || '删除失败')
  }
}

async function deleteArticle() {
  if (!confirm('确定删除这篇文章？')) return
  try {
    await api.delete('/api/articles/' + route.params.id)
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

function onScroll() {
  const el = mainRef.value
  if (!el) return
  const rect = el.getBoundingClientRect()
  const total = rect.height - window.innerHeight
  if (total <= 0) { scrollProgress.value = 0; return }
  scrollProgress.value = Math.min(1, Math.max(0, -rect.top / total))
}

onMounted(() => {
  fetchArticle()
  fetchAll()
  fetchComments()
  window.addEventListener('scroll', onScroll, { passive: true })
})

onUnmounted(() => {
  window.removeEventListener('scroll', onScroll)
})
</script>

<style scoped>
/* toc sidebar with progress gradient border */
.toc-sidebar {
  border-right: 2px solid;
  border-image: linear-gradient(
    to bottom,
    v-bind('settings.primary') var(--progress),
    #eee var(--progress)
  ) 1;
}

.title-underline {
  border-bottom-color: v-bind('settings.primary');
}

/* edit / delete links */
.edit-link, .del-link {
  @apply inline-flex items-center gap-[3px] no-underline font-semibold text-[0.85rem] font-sans cursor-pointer bg-transparent border-none p-0;
  color: v-bind('settings.primary');
}
.edit-link { @apply ml-auto; }
.del-link { @apply ml-3; }
.edit-link:hover, .del-link:hover { @apply opacity-80; }

/* toc overrides */
.toc-sidebar :deep(.md-editor-catalog-link) { @apply text-gray-600; }
.toc-sidebar :deep(.md-editor-catalog-active > span) { color: v-bind('settings.primary'); }
.toc-sidebar :deep(.md-editor-catalog-link span:hover) { color: v-bind('settings.primary'); }
.toc-sidebar :deep(.md-editor-catalog-indicator) { background: v-bind('settings.primary'); }

/* content */
.content-wrap :deep(blockquote) { border-left-color: v-bind('settings.primary'); }
.content-wrap :deep(.md-editor) { @apply h-auto border-none; }
.content-wrap :deep(.md-editor-preview-wrapper) { @apply p-0; }

/* nav */
.nav-prev, .nav-next {
  @apply inline-flex items-center gap-1.5 max-w-[48%] no-underline text-[0.9rem] transition-opacity duration-150;
  color: v-bind('settings.primary');
}
.nav-prev:hover, .nav-next:hover { @apply opacity-70; }
.nav-next { @apply ml-auto text-right; }

/* comment button */
.comment-form button {
  background: v-bind('settings.primary');
}

/* spinner */
.spinner {
  @apply w-9 h-9 border-[3px] border-gray-200 rounded-full animate-spin;
  border-top-color: v-bind('settings.primary');
}
</style>
