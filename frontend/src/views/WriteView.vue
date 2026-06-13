<template>
  <div class="bg-white">
    <header class="flex items-center gap-4 px-6 py-3">
      <input v-model="title" type="text"
             class="flex-1 border-none text-[1.4rem] font-semibold outline-none py-1 text-gray-800 placeholder:text-gray-300"
             :class="{ 'animate-shake placeholder:animate-flash-red': shake }" placeholder="请输入标题"/>
      <span v-if="saveStatus" class="text-xs transition-opacity duration-300"
            :class="saveStatus === '已保存' ? 'text-green-500' : 'text-gray-400'">{{ saveStatus }}</span>
      <button @click="doSave(false)" :disabled="!dirty"
              class="px-4 py-[0.45rem] border border-gray-200 rounded-md cursor-pointer text-[0.9rem] transition-colors duration-150"
              :class="dirty ? 'text-gray-600 hover:border-primary hover:text-primary' : 'text-gray-300 cursor-not-allowed'">
        保存
      </button>
      <button @click="publish"
              class="px-5 py-[0.45rem] bg-primary text-white border-none rounded-md cursor-pointer text-[0.9rem] hover:opacity-90 transition-opacity duration-150">
        {{ isAlreadyPublished ? '转草稿' : '发布' }}
      </button>
      <button v-if="isEdit" @click="deleteArticle"
              class="px-5 py-[0.45rem] border border-red-400 text-red-400 bg-transparent rounded-md cursor-pointer text-[0.9rem] hover:bg-red-400 hover:text-white transition-all duration-150">
        删除
      </button>
    </header>

    <MdEditor v-model="content" theme="light" :preview-theme="settings.previewTheme" :code-theme="settings.codeTheme"
              :toolbars="toolbars" :footers="[]" @on-upload-img="uploadImg" style="height: auto; min-height: 400px"/>

    <div class="px-6 py-3 border-t border-gray-100 flex flex-col gap-2.5">
      <!-- 标签 -->
      <div class="flex gap-3 items-center">
        <label class="text-[0.85rem] text-black min-w-[2.5rem]">标签</label>
        <div class="flex flex-wrap gap-1 items-center flex-1">
          <span v-for="name in pickedTags" :key="name"
                class="inline-flex items-center gap-[3px] px-2.5 py-1 border border-transparent rounded-lg text-[0.88rem] transition-all duration-150 pill-ghost">
            <TagIcon :size="14"/> {{ name }}
            <button @click="removeTag(name)" class="bg-transparent border-none text-[0.9rem] p-0 leading-none opacity-60 cursor-pointer hover:opacity-100"
                    :style="{ color: settings.primary }">&times;</button>
          </span>
          <div class="relative">
            <input v-model="tagInput" placeholder="搜索或新建..."
                   class="border-none outline-none text-[0.85rem] py-1 w-[140px] text-gray-800 placeholder:text-gray-300"
                   @input="onTagInput"
                   @focus="showTagDropdown = true; highlightedTagIdx = -1"
                   @keydown="onTagKeydown"/>
            <div v-if="showTagDropdown" class="fixed inset-0 z-[49]" @click="showTagDropdown = false"></div>
            <div v-if="showTagDropdown && (filteredTags.length || tagInput)"
                 class="absolute bottom-8 left-0 bg-white border border-gray-200 rounded-md shadow-[0_-2px_8px_rgba(0,0,0,0.1)] z-50 min-w-[180px] max-h-[200px] overflow-y-auto">
              <div v-for="(t, i) in filteredTags" :key="t.id"
                   class="px-3 py-[0.45rem] cursor-pointer text-[0.85rem] transition-colors duration-75"
                   :class="i === highlightedTagIdx ? 'bg-gray-100' : 'hover:bg-gray-50'"
                   @mousedown.prevent="pickTag(t.name)">{{ t.name }}</div>
              <div v-if="tagInput && !filteredTags.some(t => t.name === tagInput) && !pickedTags.includes(tagInput.trim())"
                   class="px-3 py-[0.45rem] cursor-pointer text-[0.85rem] border-t border-gray-100 italic transition-colors duration-75"
                   :class="filteredTags.length === highlightedTagIdx ? 'bg-gray-100' : 'hover:bg-gray-50'"
                   :style="{ color: settings.primary }" @mousedown.prevent="pickTag(tagInput.trim())">新建"{{ tagInput.trim() }}"</div>
            </div>
          </div>
        </div>
      </div>

      <div class="h-px bg-gray-200"></div>

      <!-- 分类 -->
      <div class="flex gap-3 items-center">
        <label class="text-[0.85rem] text-black min-w-[2.5rem]">分类</label>
        <div class="relative flex-1">
          <input
              v-model="categoryInput"
              placeholder="如 算法/图论 留空默认添加至根分类"
          class="border-none outline-none text-[0.85rem] py-1 w-full placeholder:text-gray-300"
          :class="categoryError ? '!text-red-500' : 'text-gray-800'"
          @input="onCategoryInput"
          @focus="showCatDropdown = true; highlightedCatIdx = -1"
          @keydown="onCategoryKeydown"
          />
          <div v-if="showCatDropdown" class="fixed inset-0 z-[49]" @click="showCatDropdown = false"></div>
          <div
              v-if="showCatDropdown && catSuggestions.length"
              class="absolute bottom-8 left-0 bg-white border border-gray-200 rounded-md shadow-[0_-2px_8px_rgba(0,0,0,0.1)] z-50 min-w-[180px] max-h-[200px] overflow-y-auto">
            <div v-for="(s, i) in catSuggestions" :key="s.fullPath"
                 class="px-3 py-[0.45rem] cursor-pointer text-[0.85rem] transition-colors duration-75"
                 :class="i === highlightedCatIdx ? 'bg-gray-100' : 'hover:bg-gray-50'"
                 @mousedown.prevent="pickCategory(s.fullPath)">{{ s.fullPath }}</div>
          </div>
        </div>
      </div>

      <div class="h-px bg-gray-200"></div>

      <!-- 摘要 -->
      <div class="flex gap-3 items-center">
        <label class="text-[0.85rem] text-black min-w-[2.5rem]">摘要</label>
        <input v-model="summary" placeholder="留空默认截取正文前100字"
               class="border-none outline-none text-[0.85rem] py-1 flex-1 text-gray-800 placeholder:text-gray-300"/>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useSettingsStore } from '@/stores/settings'
import { api } from '@/api'
import { MdEditor } from 'md-editor-v3'
import { Tag as TagIcon } from 'lucide-vue-next'
import 'md-editor-v3/lib/style.css'
import type { Tag, CategoryVO } from '@/types'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const settings = useSettingsStore()

const articleId = ref(0)
const title = ref('')
const content = ref('')
const summary = ref('')
const isAlreadyPublished = ref(false)
const dirty = ref(false)
const saveStatus = ref('')
const shake = ref(false)

const isEdit = computed(() => !!route.params.id)
const auth = useAuthStore()
// toolbars: 去掉截屏按钮
const toolbars: any[] = [
  'bold', 'italic', 'strikethrough', 'title', '-',
  'unordered-list', 'ordered-list', 'task', '-',
  'quote', 'code', 'table', '-',
  'image', 'link', '-',
  'preview', 'htmlPreview',
]

async function deleteArticle() {
  if (!articleId.value) return
  if (!confirm('确定删除这篇文章？此操作不可撤销。')) return
  try {
    await api.delete('/api/article/' + articleId.value)
    router.push('/')
  } catch (e: any) {
    alert(e.message || '删除失败')
  }
}

// ==================== 草稿创建 ====================
const draftCreated = ref(false)

async function ensureDraft() {
  if (articleId.value || draftCreated.value) return
  draftCreated.value = true
  try {
    const r = await api.post('/api/article', {
      title: title.value,
      content: content.value,
      summary: summary.value,
      authorId: auth.user?.id,
      isPublished: 0,
      categoryPath: categoryPath.value,
      tagIds: [],
      tagNames: pickedTags.value,
    })
    articleId.value = r.data.id
  } catch (e: any) {
    draftCreated.value = false
    alert(e.message || '创建草稿失败')
  }
}

// 首次有实质输入 → 自动创建草稿
watch([title, content], ([t, c]) => {
  if ((t || c) && !articleId.value && !draftCreated.value) {
    ensureDraft()
  }
})

let saveStatusTimer: ReturnType<typeof setTimeout> | null = null

function showSaveStatus(msg: string) {
  saveStatus.value = msg
  if (saveStatusTimer) clearTimeout(saveStatusTimer)
  saveStatusTimer = setTimeout(() => { saveStatus.value = '' }, 2000)
}

async function doSave(auto: boolean) {
  await ensureDraft()
  if (!articleId.value) return
  dirty.value = false
  try {
    await api.put('/api/article/' + articleId.value, {
      id: articleId.value,
      title: title.value,
      content: content.value,
      summary: summary.value,
      authorId: auth.user?.id,
      isPublished: isAlreadyPublished.value ? 1 : 0,
      categoryPath: categoryPath.value,
      tagIds: [],
      tagNames: pickedTags.value,
    })
    if (!auto) showSaveStatus('已保存')
  } catch (e: any) {
    if (!auto) alert(e.message || '保存失败')
    dirty.value = true
  }
}

async function publish() {
  await ensureDraft()
  if (!articleId.value) return
  if (categoryError.value) { alert('分类层级不能超过 3 级'); return }
  dirty.value = false
  try {
    const newPubStatus = isAlreadyPublished.value ? 0 : 1
    await api.put('/api/article/' + articleId.value, {
      id: articleId.value,
      title: title.value,
      content: content.value,
      summary: summary.value,
      authorId: auth.user?.id,
      isPublished: newPubStatus,
      categoryPath: categoryPath.value,
      tagIds: [],
      tagNames: pickedTags.value,
    })
    isAlreadyPublished.value = !isAlreadyPublished.value
    showSaveStatus(newPubStatus ? '已发布' : '已转草稿')
  } catch (e: any) {
    alert(e.message || '操作失败')
    dirty.value = true
  }
}

// ==================== 图片上传 ====================
async function uploadImg(files: File[], callback: (urls: string[]) => void) {
  await ensureDraft()
  if (!articleId.value) return
  const urls: string[] = []
  for (const f of files) {
    try {
      const fd = new FormData()
      fd.append('file', f)
      fd.append('articleId', String(articleId.value))
      const r = await fetch('/api/article/upload', { method: 'POST', body: fd })
      const j = await r.json()
      if (j.code === 200) urls.push(j.data.url)
      else console.error('上传失败', j)
    } catch (e) { console.error('上传异常', e) }
  }
  if (urls.length) callback(urls)
}

// ==================== 标签 ====================
const allTags = ref<Tag[]>([])
const pickedTags = ref<string[]>([])
const tagInput = ref('')
const showTagDropdown = ref(false)
const highlightedTagIdx = ref(-1)

const filteredTags = computed(() =>
    allTags.value.filter(t => !pickedTags.value.includes(t.name) && (!tagInput.value || t.name.includes(tagInput.value)))
)

function pickTag(name: string) {
  if (pickedTags.value.includes(name)) return
  pickedTags.value.push(name)
  tagInput.value = ''
  highlightedTagIdx.value = -1
}
function removeTag(name: string) { pickedTags.value = pickedTags.value.filter(x => x !== name) }

function onTagInput() {
  showTagDropdown.value = true
  highlightedTagIdx.value = -1
}

function onTagKeydown(e: KeyboardEvent) {
  if (!showTagDropdown.value) return
  const hasNew = tagInput.value && !filteredTags.value.some(t => t.name === tagInput.value) && !pickedTags.value.includes(tagInput.value.trim())
  const total = filteredTags.value.length + (hasNew ? 1 : 0)
  if (!total) return

  if (e.key === 'ArrowDown') {
    e.preventDefault()
    highlightedTagIdx.value = Math.min(highlightedTagIdx.value + 1, total - 1)
  } else if (e.key === 'ArrowUp') {
    e.preventDefault()
    highlightedTagIdx.value = Math.max(highlightedTagIdx.value - 1, 0)
  } else if (e.key === 'Enter') {
    e.preventDefault()
    if (highlightedTagIdx.value < filteredTags.value.length) {
      pickTag(filteredTags.value[highlightedTagIdx.value].name)
    } else if (hasNew) {
      pickTag(tagInput.value.trim())
    }
  }
}

// ==================== 分类 ====================
const categoryTree = ref<CategoryVO[]>([])
const categoryInput = ref('')
const showCatDropdown = ref(false)
const highlightedCatIdx = ref(-1)

const categoryPath = computed(() => categoryInput.value.trim() || '')

const categoryError = computed(() => categoryInput.value.split('/').filter(Boolean).length > 3)

interface CatSuggestion { fullPath: string; existing: boolean }

const catSuggestions = computed((): CatSuggestion[] => {
  const raw = categoryInput.value
  const parts = raw.split('/')
  const search = parts[parts.length - 1] || ''
  const prefix = parts.slice(0, -1).filter(Boolean)
  const prefixPath = prefix.join('/')

  let nodes = categoryTree.value
  for (const seg of prefix) {
    const found = nodes.find(c => c.name === seg)
    nodes = found?.children || []
  }

  return nodes
      .filter(c => !search || c.name.toLowerCase().includes(search.toLowerCase()))
      .map(c => {
        const fp = prefixPath ? prefixPath + '/' + c.name : c.name
        return { fullPath: fp, existing: true }
      })
})

function onCategoryInput() { showCatDropdown.value = true; highlightedCatIdx.value = -1 }
function pickCategory(path: string) { categoryInput.value = path; showCatDropdown.value = false }

function onCategoryKeydown(e: KeyboardEvent) {
  if (!showCatDropdown.value || !catSuggestions.value.length) return
  if (e.key === 'ArrowDown') {
    e.preventDefault()
    highlightedCatIdx.value = Math.min(highlightedCatIdx.value + 1, catSuggestions.value.length - 1)
  } else if (e.key === 'ArrowUp') {
    e.preventDefault()
    highlightedCatIdx.value = Math.max(highlightedCatIdx.value - 1, 0)
  } else if (e.key === 'Enter') {
    e.preventDefault()
    if (highlightedCatIdx.value >= 0 && highlightedCatIdx.value < catSuggestions.value.length) {
      pickCategory(catSuggestions.value[highlightedCatIdx.value].fullPath)
    }
  }
}

// ==================== 生命周期 ====================
let autoSaveTimer: ReturnType<typeof setInterval> | null = null

onMounted(async () => {
  if (isEdit.value) {
    try {
      const r = await api.get('/api/article/' + route.params.id)
      const a = r.data
      articleId.value = a.id
      title.value = a.title; content.value = a.content; summary.value = a.summary || ''
      isAlreadyPublished.value = a.isPublished === 1
      pickedTags.value = (a.tags || []).map((t: Tag) => t.name)
      if (a.categoryLevel) categoryInput.value = [a.categoryLevel.level1, a.categoryLevel.level2, a.categoryLevel.level3].filter(Boolean).map((i: any) => i.name).join('/')
      draftCreated.value = true // 编辑模式已有文章
    } catch { /* ignore */ }
  }
  // 新文章：不自动创建，等用户输入后由 ensureDraft() 创建

  try {
    const [tagRes, catRes] = await Promise.allSettled([api.get('/api/query/tags'), api.get('/api/query/categories')])
    if (tagRes.status === 'fulfilled') allTags.value = tagRes.value.data || []
    if (catRes.status === 'fulfilled') categoryTree.value = catRes.value.data?.children || []
  } catch { /* ignore */ }
  dirty.value = false
  autoSaveTimer = setInterval(() => { if (dirty.value && articleId.value) doSave(true) }, 5 * 60 * 1000)
})

watch([title, content, summary, categoryInput, pickedTags], () => { dirty.value = true }, { deep: true })

onBeforeUnmount(() => {
  if (autoSaveTimer) clearInterval(autoSaveTimer)
  if (saveStatusTimer) clearTimeout(saveStatusTimer)
})
</script>