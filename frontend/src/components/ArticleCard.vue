<template>
  <article class="card" @click="$emit('click', article)">
    <h3 class="card-title">{{ article.title }}</h3>
    <p class="summary">{{ article.summary || '暂无摘要' }}</p>
    <div class="card-footer">
      <span class="card-date">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M8 2v4"/><path d="M16 2v4"/><rect width="18" height="18" x="3" y="4" rx="2"/><path d="M3 10h18"/><path d="M8 14h.01"/><path d="M12 14h.01"/><path d="M16 14h.01"/><path d="M8 18h.01"/><path d="M12 18h.01"/><path d="M16 18h.01"/></svg>
        {{ formatDate(article.createdAt) }}
      </span>
      <span class="card-author">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
        {{ article.authorName }}
      </span>
      <span class="card-tags" v-if="article.tags.length">
        <span
          v-for="t in article.tags"
          :key="t.id"
          class="card-tag"
          :class="{ active: selectedTags.includes(t.id), hovered: hoveredTag === t.name }"
          @click.stop="$emit('selectTag', t.id)"
          @mouseenter="$emit('hoverTag', t.name)"
          @mouseleave="$emit('hoverTag', '')"
        >
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12.586 2.586A2 2 0 0 0 11.172 2H4a2 2 0 0 0-2 2v7.172a2 2 0 0 0 .586 1.414l8.704 8.704a2.426 2.426 0 0 0 3.42 0l6.58-6.58a2.426 2.426 0 0 0 0-3.42z"/><circle cx="7.5" cy="7.5" r=".5" fill="currentColor"/></svg> {{ t.name }}
        </span>
      </span>
    </div>
  </article>
</template>

<script setup lang="ts">
import type { Article } from '@/types'

defineProps<{
  article: Article
  selectedTags: number[]
  hoveredTag: string
}>()

defineEmits<{
  click: [article: Article]
  selectTag: [tagId: number]
  hoverTag: [tagName: string]
}>()

function formatDate(d: string) { return new Date(d).toISOString().slice(0, 10) }
</script>

<style scoped>
.card { padding: 1.4rem 1.5rem; border-bottom: 1px solid #eee; transition: background 0.15s; cursor: pointer; }
.card:hover { background: #fafafa; box-shadow: inset 3px 0 0 var(--primary); }
.card:hover .summary { border-left-color: var(--primary); }
.card:last-child { border-bottom: none; }

.card-title { margin: 0 0 0.4rem; color: #333; transition: color 0.15s; }
.card:hover .card-title { color: var(--primary); }

.summary { color: #777; font-size: 0.875rem; margin: 1rem 0; padding-left: 1rem; border-left: 2px dashed #ddd; word-break: break-word; }

.card-footer { font-size: 0.85rem; color: #999; display: flex; align-items: center; flex-wrap: wrap; }
.card-date, .card-author { display: inline-flex; align-items: center; gap: 0.3rem; white-space: nowrap; }
.card-date { padding-right: 0.55rem; }
.card-author { padding: 0 0.55rem; }

.card-tags { display: inline-flex; align-items: center; color: #999; }
.card-tag { display: inline-flex; align-items: center; gap: 3px; padding: 0.15rem 0.55rem; border-radius: 10px; font-size: 0.85rem; color: #888; transition: all 0.15s; cursor: pointer; }
.card-tag + .card-tag { margin-left: 4px; }
.card-tag:hover, .card-tag.hovered { color: var(--primary); outline: 1px solid var(--primary); }
.card-tag.active { background: var(--primary); color: #fff; }
</style>