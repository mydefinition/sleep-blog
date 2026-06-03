<template>
  <article class="px-6 py-[1.4rem] border-b border-gray-100 transition-[background] duration-150 cursor-pointer last:border-b-0 card-root" @click="$emit('click', article)">
    <h3 class="m-0 mb-1.5 text-gray-800 transition-colors duration-150 card-title">{{ article.title }}</h3>
    <p class="text-[0.875rem] text-gray-400 my-4 pl-4 border-l-2 border-dashed border-gray-200 break-words summary">{{ article.summary || '暂无摘要' }}</p>
    <div class="text-[0.85rem] text-gray-400 flex items-center flex-wrap">
      <span class="inline-flex items-center gap-1 whitespace-nowrap pr-[0.55rem]">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M8 2v4"/><path d="M16 2v4"/><rect width="18" height="18" x="3" y="4" rx="2"/><path d="M3 10h18"/><path d="M8 14h.01"/><path d="M12 14h.01"/><path d="M16 14h.01"/><path d="M8 18h.01"/><path d="M12 18h.01"/><path d="M16 18h.01"/></svg>
        {{ formatDate(article.createdAt) }}
      </span>
      <span class="inline-flex items-center gap-1 whitespace-nowrap px-[0.55rem]">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
        {{ article.authorName }}
      </span>
      <span class="inline-flex items-center text-gray-400" v-if="article.tags.length">
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
.card-root:hover {
  @apply bg-gray-50;
  box-shadow: inset 3px 0 0 var(--primary);
}
.card-root:hover .summary {
  border-left-color: var(--primary);
}
.card-root:hover .card-title {
  color: var(--primary);
}

.card-tag {
  @apply inline-flex items-center gap-[3px] px-[0.55rem] py-[0.15rem] rounded-[10px] text-[0.85rem] text-gray-500 transition-all duration-150 cursor-pointer;
}
.card-tag + .card-tag { margin-left: 4px; }
.card-tag:hover, .card-tag.hovered {
  color: var(--primary);
  outline: 1px solid var(--primary);
}
.card-tag.active {
  background: var(--primary);
  @apply text-white;
}
</style>
