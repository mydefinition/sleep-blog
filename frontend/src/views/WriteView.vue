<template>
  <div class="write-page">
    <header class="write-header">
      <input v-model="title" type="text" class="title-input" :class="{ shaking: shake }" placeholder="请输入标题" />
      <button @click="publish" class="btn-publish">{{ isEdit ? '保存' : '发布' }}</button>
    </header>
    <MdEditor v-model="content" theme="light" :preview-theme="settings.previewTheme" :code-theme="settings.codeTheme" :toolbars="toolbars" :footers="[]" @on-upload-img="uploadImg" style="min-height: calc(100vh - 116px)" />
    <div class="meta-bar">
      <div class="meta-row">
        <label>标签</label>
        <div class="tag-area">
          <span v-for="id in picked" :key="id" class="tag-chip">
            {{ getName(id) }}
            <button @click="removeTag(id)" class="chip-x">&times;</button>
          </span>
          <div class="tag-input-wrap">
            <input v-model="tagInput" placeholder="搜索或新建..." class="tag-input" @keydown.enter.prevent="onTagEnter" @focus="showDropdown = true" />
            <div v-if="showDropdown" class="tag-backdrop" @click="showDropdown = false"></div>
            <div v-if="showDropdown && (list.length || tagInput)" class="tag-dropdown">
              <div v-for="t in list" :key="t.id" class="tag-option" @mousedown.prevent="pickTag(t)">{{ t.name }}</div>
              <div v-if="tagInput && !list.some(t => t.name === tagInput)" class="tag-option create" @mousedown.prevent="createTag">新建「{{ tagInput }}」</div>
            </div>
          </div>
        </div>
      </div>
      <div class="meta-divider"></div>
      <div class="meta-row">
        <label>摘要</label>
        <input v-model="summary" placeholder="留空自动生成" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { MdEditor } from 'md-editor-v3'
import { useSettingsStore } from '@/stores/settings'
import { api } from '@/api'
import 'md-editor-v3/lib/style.css'
import type { Tag } from '@/types'

const title = ref('')
const content = ref('')
const summary = ref('')
const shake = ref(false)
const route = useRoute()
const settings = useSettingsStore()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)
const toolbars: any[] = ['bold','italic','strikethrough','|','code','codeRow','|','h1','h2','h3','|','link','image','|','table','katex','|','preview','htmlPreview']

const tags = ref<Tag[]>([])
const picked = ref<number[]>([])
const tagInput = ref('')
const showDropdown = ref(false)
let nextId = -1

const list = computed(() => {
  let arr = tags.value.filter(t => !picked.value.includes(t.id))
  return tagInput.value ? arr.filter(t => t.name.includes(tagInput.value)) : arr
})

function getName(id: number) { return tags.value.find(t => t.id === id)?.name || '' }
function pickTag(t: Tag) { picked.value.push(t.id); tagInput.value = ''; showDropdown.value = false }
function removeTag(id: number) { picked.value = picked.value.filter(x => x !== id) }
function onTagEnter() {
  if (list.value.length) { pickTag(list.value[0]); return }
  if (tagInput.value.trim()) createTag()
}
function createTag() {
  const name = tagInput.value.trim()
  if (!name) return
  tags.value.push({ id: nextId--, name })
  picked.value.push(nextId + 1)
  tagInput.value = ''
}

async function uploadImg(files: File[], callback: (urls: string[]) => void) {
  const urls: string[] = []
  for (const file of files) {
    try {
      const fd = new FormData()
      fd.append('file', file)
      const res = await fetch('/api/images/upload', {
        method: 'POST',
        headers: {},
        body: fd
      })
      const json = await res.json()
      if (json.data?.url) urls.push(json.data.url)
    } catch (e) { console.error('Upload failed', e) }
  }
  if (urls.length) callback(urls)
}

async function publish() {
  if (!title.value.trim()) { shake.value = true; setTimeout(() => { shake.value = false }, 600); return }
  try {
    // create any locally-added tags on the server first
    for (const id of picked.value) {
      if (id < 0) {
        const name = getName(id)
        const r = await api.post('/api/tags', { name })
        // replace local id with real id
        const idx = picked.value.indexOf(id)
        picked.value[idx] = r.data.id
        const tagIdx = tags.value.findIndex(t => t.id === id)
        if (tagIdx >= 0) tags.value[tagIdx] = r.data
      }
    }
    const body: any = { title: title.value, content: content.value, summary: summary.value || undefined }
    if (picked.value.length) body.tagIds = picked.value
    if (isEdit.value) {
      await api.put('/api/articles/' + route.params.id, body)
    } else {
      await api.post('/api/articles', body)
    }
    router.push('/articles')
  } catch (e: any) { alert(e.message || '发布失败') }
}

onMounted(async () => {
  try { const r = await api.get('/api/tags'); tags.value = r.data || [] } catch {}
  if (isEdit.value) {
    try {
      const r = await api.get('/api/articles/' + route.params.id)
      const a = r.data
      title.value = a.title
      content.value = a.content
      summary.value = a.summary || ''
      picked.value = (a.tags || []).map((t: Tag) => t.id)
    } catch {}
  }
})
</script>

<style scoped>
.write-page { background: #fff; }
.write-header { display: flex; align-items: center; gap: 1rem; padding: 0.8rem 1.5rem; }
.title-input { flex: 1; border: none; font-size: 1.4rem; font-weight: 600; outline: none; padding: 0.3rem 0; color: #333; }
.title-input::placeholder { color: #ccc; }
.title-input.shaking { animation: shake 0.6s ease; }
.title-input.shaking::placeholder { animation: flashRed 0.6s ease; }
.btn-publish { padding: 0.45rem 1.3rem; background: var(--primary); color: #fff; border: none; border-radius: 6px; cursor: pointer; font-size: 0.9rem; }
.btn-publish:hover { opacity: 0.9; }

.meta-bar { padding: 0.8rem 1.5rem; border-top: 1px solid #eee; display: flex; flex-direction: column; gap: 0.6rem; }
.meta-row { display: flex; gap: 0.8rem; align-items: center; }
.meta-row label { font-size: 0.85rem; color: #000; min-width: 2.5rem; }
.meta-row input { flex: 1; border: none; outline: none; font-size: 0.9rem; color: #333; padding: 0.3rem 0; }
.meta-row input::placeholder { color: #ccc; }

.tag-area { display: flex; flex-wrap: wrap; gap: 0.3rem; align-items: center; flex: 1; }
.tag-chip { display: inline-flex; align-items: center; gap: 0.2rem; padding: 0.15rem 0.5rem; background: color-mix(in srgb, v-bind("settings.primary") 15%, white); color: v-bind("settings.primary"); border-radius: 10px; font-size: 0.8rem; }
.chip-x { background: none; border: none; color: v-bind("settings.primary"); cursor: pointer; font-size: 1rem; padding: 0; opacity: 0.7; }
.tag-input-wrap { position: relative; }
.tag-input { border: none; outline: none; font-size: 0.85rem; padding: 0.3rem 0; width: 140px; color: #333; }
.tag-input::placeholder { color: #ccc; }
.tag-backdrop { position: fixed; inset: 0; z-index: 49; }
.tag-dropdown { position: absolute; bottom: 32px; left: 0; background: #fff; border: 1px solid #e0e0e0; border-radius: 6px; box-shadow: 0 -2px 8px rgba(0,0,0,0.1); z-index: 50; min-width: 180px; max-height: 200px; overflow-y: auto; }
.tag-option { padding: 0.45rem 0.8rem; cursor: pointer; font-size: 0.85rem; }
.tag-option:hover { background: #f5f5f5; }
.tag-option.create { color: v-bind("settings.primary"); border-top: 1px solid #eee; font-style: italic; }
.meta-divider { height: 1px; background: #ccc; margin: 0.1rem 0; }

@keyframes shake { 0%,100%{transform:translateX(0)}20%{transform:translateX(-8px)}40%{transform:translateX(8px)}60%{transform:translateX(-4px)}80%{transform:translateX(4px)} }
@keyframes flashRed { 0%,100%{color:#e03131}50%{color:#ff6b6b} }
</style>
<style>
.md-editor-preview-wrapper blockquote {
  border-left: 0.25em solid var(--primary) !important;
}</style>