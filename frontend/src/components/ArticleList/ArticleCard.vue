<template>
  <article class="relative px-6 py-[1.4rem] border-b border-gray-100 transition-colors duration-150 cursor-pointer last:border-b-0 group hover:bg-gray-50 hover:[box-shadow:inset_3px_0_0_var(--primary)]" @click="$emit('click', article)">
    <!-- 右上角分类路径 -->
    <span v-if="categoryPath" class="absolute top-4 right-4 text-[0.78rem] text-gray-400 group-hover:text-primary/60 transition-colors duration-150">
      {{ categoryPath }}
    </span>

    <h3 class="m-0 mb-1.5 text-gray-800 transition-colors duration-150 group-hover:text-primary pr-20">{{ article.title }}</h3>
    <p class="text-[0.875rem] text-gray-400 my-4 pl-4 border-l-2 border-dashed border-gray-200 break-words transition-colors duration-150 group-hover:border-l-primary">{{ article.summary || '暂无摘要' }}</p>
    <div class="text-[0.85rem] text-gray-400 flex items-center flex-wrap">
      <span class="inline-flex items-center gap-1 whitespace-nowrap pr-[0.55rem]"><Calendar :size="14" /> {{ formatDate(article.createdAt) }}</span>
      <span class="inline-flex items-center gap-1 whitespace-nowrap px-[0.55rem]"><User :size="14" /> {{ article.authorName }}</span>
      <span class="inline-flex items-center text-gray-400 gap-[2px]" v-if="article.tags.length">
        <span v-for="t in article.tags" :key="t.id"
              class="inline-flex items-center gap-[3px] px-[0.55rem] py-[0.15rem] border rounded-lg text-[0.9rem] transition-all duration-150 cursor-pointer"
              :class="selectedTags.includes(t.id) ? 'pill-solid' : hoveredTag === t.name && !selectedTags.includes(t.id) ? 'pill-outline' : 'border-transparent text-gray-500'"
              @click.stop="$emit('selectTag', t.id)"
              @mouseenter="$emit('hoverTag', t.name)"
              @mouseleave="$emit('hoverTag', '')"
        ><Tag :size="14" /> {{ t.name }}</span>
      </span>
    </div>
  </article>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ArticleSummary } from '@/types'
import { Calendar, User, Tag } from 'lucide-vue-next'

const props = defineProps<{ article: ArticleSummary; selectedTags: number[]; hoveredTag: string }>()
defineEmits<{ click: [article: ArticleSummary]; selectTag: [tagId: number]; hoverTag: [tagName: string] }>()

const categoryPath = computed(() => {
  const c = props.article.category
  if (!c) return ''
  return [c.level1, c.level2, c.level3]
      .filter(Boolean)
      .map(i => i!.name)
      .join(' / ')
})

function formatDate(d: string) { return new Date(d).toISOString().slice(0, 10) }
</script>