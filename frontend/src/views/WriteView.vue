<template>
  <div class="bg-white">
    <header class="flex items-center gap-4 px-6 py-3">
      <input v-model="title" type="text"
             class="flex-1 border-none text-[1.4rem] font-semibold outline-none py-1 text-gray-800 placeholder:text-gray-300"
             :class="{ 'animate-shake placeholder:animate-flash-red': shake }" placeholder="请输入标题"/>
      <button @click="publish"
              class="px-5 py-[0.45rem] bg-primary text-white border-none rounded-md cursor-pointer text-[0.9rem] hover:opacity-90 transition-opacity duration-150">
        {{ isEdit ? '保存' : '发布' }}
      </button>
    </header>
    <MdEditor v-model="content" theme="light" :preview-theme="settings.previewTheme" :code-theme="settings.codeTheme"
              :toolbars="toolbars" :footers="[]" @on-upload-img="uploadImg" style="min-height: calc(100vh - 116px)"/>
    <div class="px-6 py-3 border-t border-gray-100 flex flex-col gap-2.5">
      <div class="flex gap-3 items-center">
        <label class="text-[0.85rem] text-black min-w-[2.5rem]">标签</label>
        <div class="flex flex-wrap gap-1 items-center flex-1">
          <span v-for="id in picked" :key="id"
                class="inline-flex items-center gap-1 px-2 py-[0.15rem] rounded-[10px] text-xs"
                :style="{ background: `color-mix(in srgb, ${settings.primary} 15%, white)`, color: settings.primary }">
            {{ getName(id) }}
            <button @click="removeTag(id)" class="bg-transparent border-none text-base p-0 opacity-70 cursor-pointer"
                    :style="{ color: settings.primary }">&times;</button>
          </span>
          <div class="relative">
            <input v-model="tagInput" placeholder="搜索或新建..."
                   class="border-none outline-none text-[0.85rem] py-1 w-[140px] text-gray-800 placeholder:text-gray-300"
                   @keydown.enter.prevent="onTagEnter" @focus="showDropdown = true"/>
            <div v-if="showDropdown" class="fixed inset-0 z-[49]" @click="showDropdown = false"></div>
            <div v-if="showDropdown && (list.length || tagInput)"
                 class="absolute bottom-8 left-0 bg-white border border-gray-200 rounded-md shadow-[0_-2px_8px_rgba(0,0,0,0.1)] z-50 min-w-[180px] max-h-[200px] overflow-y-auto">
              <div v-for="t in list" :key="t.id"
                   class="px-3 py-[0.45rem] cursor-pointer text-[0.85rem] hover:bg-gray-100 transition-colors duration-100"
                   @mousedown.prevent="pickTag(t)">{{ t.name }}
              </div>
              <div v-if="tagInput && !list.some(t => t.name === tagInput)"
                   class="px-3 py-[0.45rem] cursor-pointer text-[0.85rem] border-t border-gray-100 italic hover:bg-gray-100 transition-colors duration-100"
                   :style="{ color: settings.primary }" @mousedown.prevent="createTag">新建「{{ tagInput }}」
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="h-px bg-gray-300 my-px"></div>
      <div class="flex gap-3 items-center">
        <label class="text-[0.85rem] text-black min-w-[2.5rem]">摘要</label>
        <input v-model="summary" placeholder="留空自动生成"
               class="flex-1 border-none outline-none text-[0.9rem] text-gray-800 py-1 placeholder:text-gray-300"/>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {ref, computed, onMounted} from 'vue'
import {useRouter, useRoute} from 'vue-router'
import {MdEditor} from 'md-editor-v3'
import {useSettingsStore} from '@/stores/settings'
import {api} from '@/api'
import 'md-editor-v3/lib/style.css'
import type {Tag} from '@/types'

const title = ref('');
const content = ref('');
const summary = ref('');
const shake = ref(false)
const route = useRoute();
const settings = useSettingsStore();
const router = useRouter()
const isEdit = computed(() => !!route.params.id)
const toolbars: any[] = ['bold', 'italic', 'strikethrough', '|', 'code', 'codeRow', '|', 'h1', 'h2', 'h3', '|', 'link', 'image', '|', 'table', 'katex', '|', 'preview', 'htmlPreview']

const tags = ref<Tag[]>([]);
const picked = ref<number[]>([]);
const tagInput = ref('');
const showDropdown = ref(false);
let nextId = -1
const list = computed(() => {
  let arr = tags.value.filter(t => !picked.value.includes(t.id));
  return tagInput.value ? arr.filter(t => t.name.includes(tagInput.value)) : arr
})

function getName(id: number) {
  return tags.value.find(t => t.id === id)?.name || ''
}

function pickTag(t: Tag) {
  picked.value.push(t.id);
  tagInput.value = '';
  showDropdown.value = false
}

function removeTag(id: number) {
  picked.value = picked.value.filter(x => x !== id)
}

function onTagEnter() {
  if (list.value.length) {
    pickTag(list.value[0]);
    return
  }
  if (tagInput.value.trim()) createTag()
}

function createTag() {
  const name = tagInput.value.trim();
  if (!name) return;
  tags.value.push({id: nextId--, name});
  picked.value.push(nextId + 1);
  tagInput.value = ''
}

async function uploadImg(files: File[], callback: (urls: string[]) => void) {
  const urls: string[] = [];
  for (const file of files) {
    try {
      const fd = new FormData();
      fd.append('file', file);
      const res = await fetch('/api/images/upload', {method: 'POST', headers: {}, body: fd});
      const json = await res.json();
      if (json.data?.url) urls.push(json.data.url)
    } catch (e) {
      console.error('Upload failed', e)
    }
  }
  if (urls.length) callback(urls)
}

async function publish() {
  if (!title.value.trim()) {
    shake.value = true;
    setTimeout(() => {
      shake.value = false
    }, 600);
    return
  }
  try {
    for (const id of picked.value) {
      if (id < 0) {
        const name = getName(id);
        const r = await api.post('/api/tags', {name});
        const idx = picked.value.indexOf(id);
        picked.value[idx] = r.data.id;
        const tagIdx = tags.value.findIndex(t => t.id === id);
        if (tagIdx >= 0) tags.value[tagIdx] = r.data
      }
    }
    const body: any = {title: title.value, content: content.value, summary: summary.value || undefined};
    if (picked.value.length) body.tagIds = picked.value
    if (isEdit.value) {
      await api.put('/api/articles/' + route.params.id, body)
    } else {
      await api.post('/api/articles', body)
    }
    router.push('/articles')
  } catch (e: any) {
    alert(e.message || '发布失败')
  }
}

onMounted(async () => {
  try {
    const r = await api.get('/api/tags');
    tags.value = r.data || []
  } catch {
  }
  ;
  if (isEdit.value) {
    try {
      const r = await api.get('/api/articles/' + route.params.id);
      const a = r.data;
      title.value = a.title;
      content.value = a.content;
      summary.value = a.summary || '';
      picked.value = (a.tags || []).map((t: Tag) => t.id)
    } catch {
    }
  }
})
</script>
