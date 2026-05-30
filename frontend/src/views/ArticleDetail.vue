<template>
  <div class="article-detail" v-if="article">
    <!-- 左侧目录 -->
    <aside class="toc-sidebar" :style="{ '--progress': progressPercent }">
      <p class="toc-title">目录</p>
      <MdCatalog editorId="article-preview" scrollElement="html" />
    </aside>

    <!-- 正文区域 -->
    <div class="article-main" ref="mainRef">
      <h1>{{ article.title }}</h1>

      <!-- 元信息 -->
      <div class="meta">
        <span class="card-date">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M8 2v4"/><path d="M16 2v4"/><rect width="18" height="18" x="3" y="4" rx="2"/><path d="M3 10h18"/><path d="M8 14h.01"/><path d="M12 14h.01"/><path d="M16 14h.01"/><path d="M8 18h.01"/><path d="M12 18h.01"/><path d="M16 18h.01"/></svg>
          {{ formatDate(article.createdAt) }}
        </span>
        <span class="card-author">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
          {{ article.authorName }}
        </span>
        <span v-if="article.updatedAt" class="card-updated">修改于 {{ formatDate(article.updatedAt) }}</span>
        <router-link v-if="auth.isAdmin" :to="'/write/' + article.id" class="edit-link"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 3a2.85 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5Z"/></svg> 编辑</router-link>
        <button v-if="auth.isAdmin" @click="deleteArticle" class="del-link"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 6h18"/><path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"/><path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"/></svg> 删除</button>
      </div>

      <!-- 标签 -->
      <div class="tags" v-if="article.tags.length">
        <span v-for="t in article.tags" :key="t.id" class="tag"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12.586 2.586A2 2 0 0 0 11.172 2H4a2 2 0 0 0-2 2v7.172a2 2 0 0 0 .586 1.414l8.704 8.704a2.426 2.426 0 0 0 3.42 0l6.58-6.58a2.426 2.426 0 0 0 0-3.42z"/><circle cx="7.5" cy="7.5" r=".5" fill="currentColor"/></svg> {{ t.name }}</span>
      </div>

      <!-- 分隔线 -->
      <div class="cut-line"><span></span><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#ccc" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="transform: scaleX(-1)"><circle cx="6" cy="6" r="3"/><path d="M8.12 8.12 12 12"/><path d="M20 4 8.12 15.88"/><circle cx="6" cy="18" r="3"/><path d="M14.8 14.8 20 20"/></svg></div>

      <!-- Markdown 正文 -->
      <div class="content-wrap">
        <MdPreview
          id="article-preview"
          :modelValue="displayContent"
          :previewTheme="settings.previewTheme"
          :codeTheme="settings.codeTheme"
        />
      </div>

      <!-- 评论区 -->
      <section class="comments">
        <h3>评论 ({{ comments.length }})</h3>
        <div v-if="auth.isLoggedIn" class="comment-form">
          <textarea v-model="commentText" placeholder="写评论..." rows="3"></textarea>
          <button @click="postComment" :disabled="!commentText.trim()">发表评论</button>
        </div>
        <p v-else><router-link to="/login">登录</router-link>后发表评论</p>
        <div v-if="comments.length" class="comment-list">
          <div v-for="c in comments" :key="c.id" class="comment-item">
            <div class="comment-head">
              <strong>{{ c.username }}</strong>
              <span>{{ formatDate(c.createdAt) }}</span>
              <button v-if="auth.isAdmin" class="btn-del" @click="deleteComment(c.id)">删除</button>
            </div>
            <p>{{ c.content }}</p>
          </div>
        </div>
      </section>
    </div>
  </div>

  <!-- 加载中 -->
  <div v-else-if="loading" class="loading-wrap"><span class="spinner"></span></div>
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

// 正文（剥离冗余首行标题）
const displayContent = computed(() => {
  if (!article.value) return ''
  return article.value.content || ''
})

async function fetchArticle() {
  const res = await api.get('/api/articles/' + route.params.id)
  article.value = res.data
  loading.value = false
}

async function fetchComments() {
  const res = await api.get('/api/articles/' + route.params.id + '/comments')
  comments.value = res.data
}

async function postComment() {
  if (!commentText.value.trim()) return
  await api.post('/api/articles/' + route.params.id + '/comments', { content: commentText.value })
  commentText.value = ''
  fetchComments()
}

async function deleteComment(id: number) {
  await api.delete('/api/comments/' + id)
  fetchComments()
}

async function deleteArticle() {
  if (!confirm('确定要删除这篇文章吗？此操作不可恢复！')) return
  try {
    await api.delete('/api/articles/' + article.value!.id)
    router.push('/articles')
  } catch (e: any) {
    alert(e.message || '删除失败')
  }
}

function formatDate(d: string) {
  return new Date(d).toISOString().slice(0, 10)
}

onMounted(() => {
  fetchArticle()
  fetchComments()
})

// 目录侧进度条
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
  fetchComments()
  window.addEventListener('scroll', onScroll, { passive: true })
})

onUnmounted(() => {
  window.removeEventListener('scroll', onScroll)
})

</script>

<style scoped>
/* ====== 整体布局 ====== */
.article-detail {
  display: flex;
  gap: 2.5rem;
  max-width: 1000px;
  margin: 0 auto;
  padding: 2rem 1rem;
  align-items: flex-start;
}

/* ====== 左侧目录 ====== */
.toc-sidebar {
  width: 200px;
  flex-shrink: 0;
  position: sticky;
  top: 68px;
  max-height: calc(100vh - 80px);
  overflow-y: auto;
  font-size: 0.85rem;
}

.toc-title {
  font-size: 0.8rem;
  color: #999;
  letter-spacing: 0.1em;
  margin: 0 0 0.5rem 0;
}

.toc-sidebar :deep(.md-editor-catalog-link) { color: #555; }
.toc-sidebar :deep(.md-editor-catalog-active > span) { color: v-bind('settings.primary'); }
.toc-sidebar :deep(.md-editor-catalog-link span:hover) { color: v-bind('settings.primary'); }
.toc-sidebar :deep(.md-editor-catalog-indicator) { background: v-bind('settings.primary'); }

.toc-sidebar {
  width: 200px;
  flex-shrink: 0;
  position: sticky;
  top: 68px;
  max-height: calc(100vh - 80px);
  overflow-y: auto;
  font-size: 0.85rem;
  border-right: 2px solid;
  border-image: linear-gradient(
    to bottom,
    v-bind('settings.primary') var(--progress),
    #eee var(--progress)
  ) 1;
}

/* ====== 正文 ====== */
.article-main { flex: 1; min-width: 0; max-width: 680px; }

h1 { font-size: 1.8rem; margin-bottom: 0.5rem; padding: 0 5px 0.5rem 5px; display: inline-block; border-bottom: 3px solid v-bind("settings.primary"); }

.meta { color: #999; font-size: 0.85rem; margin: 0.5rem 0 1rem 0; display: flex; gap: 1rem; align-items: center; flex-wrap: wrap; }
.card-date, .card-author, .card-updated { display: inline-flex; align-items: center; gap: 0.3rem; white-space: nowrap; }
.edit-link, .del-link {
  display: inline-flex; align-items: center; gap: 3px;
  color: v-bind("settings.primary"); text-decoration: none;
  font-weight: 600; font-size: 0.85rem; font-family: inherit;
  cursor: pointer; background: none; border: none; padding: 0;
}
.edit-link { margin-left: auto; }
.del-link { margin-left: 0.8rem; }
.edit-link:hover, .del-link:hover { opacity: 0.8; }

.tags { margin-bottom: 1rem; display: flex; flex-wrap: wrap; gap: 1rem; }
.tag { display: inline-flex; align-items: center; gap: 3px; font-size: 0.85rem; color: #888; }

.cut-line { display: flex; align-items: center; gap: 0.8rem; margin-bottom: 0; }
.cut-line span { flex: 1; border-bottom: 1px dashed #ddd; }
.cut-line svg { flex-shrink: 0; }

/* ====== Markdown 预览 ====== */
.content-wrap :deep(blockquote) { border-left-color: v-bind('settings.primary'); } /* 引用框跟随主题色 */
.content-wrap :deep(.md-editor) { height: auto; border: none; }
.content-wrap :deep(.md-editor-preview-wrapper) { padding: 0; }

/* ====== 评论区 ====== */
.comments { margin-top: 3rem; border-top: 1px solid #eee; padding-top: 1.5rem; }
.comments h3 { margin-bottom: 1rem; }
.comment-form { margin-bottom: 1.5rem; }
.comment-form textarea { width: 100%; padding: 0.6rem; border: 1px solid #ddd; border-radius: 6px; resize: vertical; font-family: inherit; }
.comment-form button { margin-top: 0.5rem; padding: 0.4rem 1.2rem; background: v-bind("settings.primary"); color: #fff; border: none; border-radius: 6px; cursor: pointer; }
.comment-form button:disabled { opacity: 0.4; cursor: not-allowed; }
.comment-list { display: flex; flex-direction: column; gap: 1rem; }
.comment-item { padding: 0.8rem; background: #fafafa; border-radius: 6px; }
.comment-head { display: flex; gap: 0.8rem; align-items: center; font-size: 0.85rem; margin-bottom: 0.3rem; }
.btn-del { margin-left: auto; padding: 0.1rem 0.5rem; border: none; background: #ff5252; color: #fff; border-radius: 4px; cursor: pointer; font-size: 0.75rem; }

/* ====== 加载中 ====== */
.loading-wrap { display: flex; justify-content: center; align-items: center; min-height: 60vh; }
.spinner { width: 36px; height: 36px; border: 3px solid #e0e0e0; border-top-color: v-bind("settings.primary"); border-radius: 50%; animation: spin 0.7s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }

</style>