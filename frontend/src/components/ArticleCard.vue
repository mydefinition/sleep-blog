<template>
  <article class="px-6 py-[1.4rem] border-b border-gray-100 transition-colors duration-150 cursor-pointer last:border-b-0 group hover:bg-gray-50 hover:[box-shadow:inset_3px_0_0_var(--primary)]" @click="$emit('click', article)">
    <h3 class="m-0 mb-1.5 text-gray-800 transition-colors duration-150 group-hover:text-primary">{{ article.title }}</h3>
    <p class="text-[0.875rem] text-gray-400 my-4 pl-4 border-l-2 border-dashed border-gray-200 break-words transition-colors duration-150 group-hover:border-l-primary">{{ article.summary || '暂无摘要' }}</p>
    <div class="text-[0.85rem] text-gray-400 flex items-center flex-wrap">
      <span class="inline-flex items-center gap-1 whitespace-nowrap pr-[0.55rem]"><Calendar :size="14" /> {{ formatDate(article.createdAt) }}</span>
      <span class="inline-flex items-center gap-1 whitespace-nowrap px-[0.55rem]"><User :size="14" /> {{ article.authorName }}</span>
      <span class="inline-flex items-center text-gray-400 gap-[2px]" v-if="article.tags.length">
        <span
          v-for="t in article.tags"
          :key="t.id"
          class="inline-flex items-center gap-[3px] px-[0.55rem] py-[0.15rem] rounded-[10px] text-[0.85rem] text-gray-500 transition-all duration-150 cursor-pointer hover:text-primary hover:[outline:1px_solid_var(--primary)]"
          :class="{ '!bg-primary !text-white': selectedTags.includes(t.id), 'text-primary !outline !outline-1 outline-primary': hoveredTag === t.name && !selectedTags.includes(t.id) }"
          @click.stop="$emit('selectTag', t.id)"
          @mouseenter="$emit('hoverTag', t.name)"
          @mouseleave="$emit('hoverTag', '')"
        ><Tag :size="14" /> {{ t.name }}</span>
      </span>
    </div>
  </article>
</template>

<script setup lang="ts">
import type { Article } from '@/types'
import { Calendar, User, Tag } from 'lucide-vue-next'

defineProps<{ article: Article; selectedTags: number[]; hoveredTag: string }>()
defineEmits<{ click: [article: Article]; selectTag: [tagId: number]; hoverTag: [tagName: string] }>()

function formatDate(d: string) { return new Date(d).toISOString().slice(0, 10) }
</script>
