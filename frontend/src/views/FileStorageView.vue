<template>
  <div class="max-w-[1100px] mx-auto px-4 py-8">
    <div class="flex-1 min-w-0">
      <div class="flex justify-between items-center mb-2 flex-wrap gap-4">
        <h2 class="m-0">小仓库</h2>
        <div v-if="auth.isAdmin" class="flex items-center gap-2">
          <input type="file" ref="fileInput" @change="onFileSelected" hidden />
          <button @click="triggerUpload" :disabled="uploading" class="btn-upload">
            {{ uploading ? '上传中...' : '上传 📄' }}
          </button>
          <button @click="toggleMkdir" class="btn-mkdir" :class="{ on: showMkdir }">新建 📁</button>
          <span class="mkdir-wrap" :class="{ open: showMkdir, active: mkdirName || mkdirFocused }">
            <input
              v-model="mkdirName"
              placeholder="文件夹名称"
              class="mkdir-input"
              @keyup.enter="doMkdir"
              @focus="mkdirFocused = true"
              @blur="mkdirFocused = false"
              ref="mkdirInput"
            />
            <button @click="doMkdir" :disabled="!mkdirName.trim()" class="btn-mkdir-confirm">确定</button>
          </span>
        </div>
      </div>

      <div class="py-2 text-[0.9rem] text-gray-400">
        <span v-for="(part, i) in breadcrumbParts" :key="i">
          <a v-if="i < breadcrumbParts.length - 1" href="#" @click.prevent="navigateTo(i)" class="text-gray-400 no-underline hover:text-primary">{{ part.name }}</a>
          <span v-else class="text-primary font-medium">{{ part.name }}</span>
        </span>
      </div>

      <div v-if="loading" class="flex justify-center min-h-[30vh] items-center"><span class="spinner"></span></div>
      <div v-else-if="errorMsg" class="text-[#ff5252] text-center py-8">{{ errorMsg }}</div>

      <table v-else class="w-full table-fixed border-collapse text-[0.9rem]">
        <tbody>
          <tr v-if="parentId !== null" class="row-folder" @click="currentDirId = parentId">
            <td class="!w-8">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="9 14 4 9 9 4"/><path d="M20 20v-7a4 4 0 0 0-4-4H4"/></svg>
            </td>
            <td class="name overflow-hidden whitespace-nowrap text-gray-800 font-medium">上级目录</td>
            <td class="meta size !w-[70px] text-right"></td>
            <td class="user-icon !w-[18px]"></td>
            <td class="meta col-user !w-[100px]"></td>
            <td class="user-icon !w-[18px]"></td>
            <td class="meta col-date !w-[90px]"></td>
            <td v-if="auth.isAdmin" class="col-del !w-[50px] text-center"></td>
          </tr>
          <tr v-for="n in currentChildren" :key="n.id"
              :class="n.isDir ? 'row-folder' : 'row-file'"
              @click="n.isDir ? currentDirId = n.id : downloadFile(n.id)">
            <td class="!w-8">{{ n.isDir ? '📁' : '📄' }}</td>
            <td class="name overflow-hidden whitespace-nowrap"><OverflowScroll :text="n.name" /></td>
            <td class="meta size !w-[70px] text-right">{{ n.isDir ? '' : formatSize(n.size!) }}</td>
            <td class="user-icon !w-[18px] text-gray-400">
              <svg v-if="!n.isDir" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
            </td>
            <td class="meta col-user !w-[100px]">
              <OverflowScroll v-if="!n.isDir" :text="n.uploaderName || ''" />
            </td>
            <td class="user-icon !w-[18px] text-gray-400">
              <svg v-if="!n.isDir" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M8 2v4"/><path d="M16 2v4"/><rect width="18" height="18" x="3" y="4" rx="2"/><path d="M3 10h18"/><path d="M8 14h.01"/><path d="M12 14h.01"/><path d="M16 14h.01"/><path d="M8 18h.01"/><path d="M12 18h.01"/><path d="M16 18h.01"/></svg>
            </td>
            <td class="meta col-date !w-[90px]">
              <span v-if="!n.isDir">{{ n.uploadAt }}</span>
            </td>
            <td v-if="auth.isAdmin" class="col-del !w-[50px] text-center">
              <button class="btn-del" @click.stop="deleteNode(n.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-if="!loading && currentChildren.length === 0 && parentId === null" class="text-center py-12 text-gray-400 text-[0.9rem]">—— 此目录为空 ——</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue'
import { api } from '@/api'
import { useAuthStore } from '@/stores/auth'
import OverflowScroll from '@/components/OverflowScroll.vue'

interface FileTreeNode {
  id: number
  name: string
  isDir: boolean
  size?: number
  hash?: string
  uploaderName?: string
  uploadAt?: string
  path: string
  children?: FileTreeNode[]
}

const auth = useAuthStore()
const loading = ref(true)
const errorMsg = ref('')

const currentDirId = ref(0)

const nodeMap = ref(new Map<number, FileTreeNode>())
const pathIdMap = ref(new Map<string, number>())

const fileInput = ref<HTMLInputElement | null>(null)
const uploading = ref(false)

const showMkdir = ref(false)
const mkdirName = ref('')
const mkdirFocused = ref(false)
const mkdirInput = ref<HTMLInputElement | null>(null)
function toggleMkdir() {
  showMkdir.value = !showMkdir.value
  if (showMkdir.value) {
    nextTick(() => {
      mkdirInput.value?.focus()
      mkdirFocused.value = true
    })
  } else {
    mkdirName.value = ''
  }
}

const currentChildren = computed(() => {
  const node = nodeMap.value.get(currentDirId.value)
  if (!node || !node.children) return []
  return node.children
})

const parentId = computed(() => {
  if (currentDirId.value === 0) return null
  const node = nodeMap.value.get(currentDirId.value)
  if (!node || !node.path) return null
  const parentPath = node.path.substring(0, node.path.lastIndexOf('/'))
  if (!parentPath) return 0
  return pathIdMap.value.get(parentPath) ?? 0
})

interface Crumb { name: string; id: number }
const breadcrumbParts = computed<Crumb[]>(() => {
  const parts: Crumb[] = [{ name: '根目录', id: 0 }]
  const node = nodeMap.value.get(currentDirId.value)
  if (!node || !node.path) return parts
  const segs = node.path.split('/').filter(Boolean)
  let cum = ''
  for (const seg of segs) {
    cum += '/' + seg
    const id = pathIdMap.value.get(cum)
    parts.push({ name: seg, id: id ?? 0 })
  }
  return parts
})

function navigateTo(index: number) {
  currentDirId.value = breadcrumbParts.value[index].id
}

async function loadTree() {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await api.get('/api/files')
    const tree: FileTreeNode[] = res.data || []
    const map = new Map<number, FileTreeNode>()
    const pathMap = new Map<string, number>()
    map.set(0, { id: 0, name: '', isDir: true, path: '', children: tree })
    indexTree(tree, map, pathMap)
    nodeMap.value = map
    pathIdMap.value = pathMap
  } catch (e: any) {
    errorMsg.value = e.message || '加载失败'
  } finally {
    loading.value = false
  }
}

function indexTree(nodes: FileTreeNode[], map: Map<number, FileTreeNode>, pathMap: Map<string, number>) {
  for (const n of nodes) {
    map.set(n.id, n)
    if (n.path) pathMap.set(n.path, n.id)
    if (n.children) indexTree(n.children, map, pathMap)
  }
}

function triggerUpload() { fileInput.value?.click() }
let pendingFile: File | null = null
function onFileSelected(e: Event) {
  const input = e.target as HTMLInputElement
  const f = input.files?.[0]
  if (f) { pendingFile = f; doUpload() }
}
async function doUpload() {
  if (!pendingFile) return
  uploading.value = true
  try {
    const fd = new FormData()
    fd.append('file', pendingFile)
    fd.append('localId', String(currentDirId.value))
    await fetch('/api/files/upload', {
      method: 'POST',
      body: fd
    })
    if (fileInput.value) fileInput.value.value = ''
    pendingFile = null
    await loadTree()
  } catch (e: any) {
    alert(e.message || '上传失败')
  } finally {
    uploading.value = false
  }
}

async function doMkdir() {
  if (!mkdirName.value.trim()) return
  try {
    await api.post('/api/files/mkdir', { name: mkdirName.value.trim(), localId: currentDirId.value })
    mkdirName.value = ''
    showMkdir.value = false
    await loadTree()
  } catch (e: any) {
    alert(e.message || '创建失败')
  }
}

async function deleteNode(id: number) {
  const node = nodeMap.value.get(id)
  const msg = node?.isDir ? '确定删除文件夹 "' + node.name + '" 及其所有内容？' : '确定删除文件 "' + (node?.name ?? '') + '"？'
  if (!confirm(msg)) return
  try {
    await api.delete('/api/files/' + id)
    await loadTree()
  } catch (e: any) {
    alert(e.message || '删除失败')
  }
}

function downloadFile(id: number) {
  window.open('/api/files/' + id + '/download', '_blank')
}

function formatSize(bytes: number): string {
  if (!bytes) return '0 B'
  const units = ['B', 'KiB', 'MiB', 'GiB']
  let i = 0
  let size = bytes
  while (size >= 1024 && i < units.length - 1) { size /= 1024; i++ }
  return size.toFixed(i === 0 ? 0 : 1) + ' ' + units[i]
}

onMounted(loadTree)
</script>

<style scoped>
/* breadcrumb */
.breadcrumb span::after { content: ' / '; color: #ccc; }
.breadcrumb span:last-child::after { content: ''; }

/* buttons */
.btn-upload {
  @apply px-4 py-1.5 bg-primary text-white border-none rounded-md cursor-pointer text-[0.85rem] font-sans disabled:opacity-40 disabled:cursor-not-allowed;
}

.btn-mkdir {
  @apply px-3.5 py-1 bg-transparent text-primary border-[1.5px] border-primary rounded-md cursor-pointer text-[0.85rem] font-sans transition-[background,color,border-radius] duration-200 hover:bg-primary hover:text-white;
}
.btn-mkdir.on {
  @apply bg-primary text-white rounded-[20px] border-primary;
}

.mkdir-wrap {
  @apply inline-flex items-center gap-1.5 max-w-0 overflow-hidden opacity-0 transition-[max-width,opacity] duration-300;
}
.mkdir-wrap.open { @apply max-w-[280px] opacity-100; }
.mkdir-input {
  @apply w-[160px] px-2.5 py-1.5 border-0 border-b-2 border-gray-100 text-[0.9rem] outline-none text-gray-800 font-sans bg-transparent transition-[border-color] duration-150;
}
.mkdir-input::placeholder { @apply text-gray-300; }
.mkdir-input:focus { border-bottom-color: var(--primary); }
.mkdir-wrap.active .mkdir-input { border-bottom-color: var(--primary); }
.btn-mkdir-confirm {
  @apply px-3 py-1 bg-transparent text-primary border-[1.5px] border-primary rounded-md text-xs font-sans cursor-pointer whitespace-nowrap transition-[background,color,border-radius,opacity] duration-200;
}
.btn-mkdir-confirm:hover:not(:disabled) {
  @apply bg-primary text-white rounded-[20px];
}
.btn-mkdir-confirm:disabled { @apply opacity-35 cursor-not-allowed; }

/* table */
.file-table td {
  @apply px-2.5 py-2 border-b border-gray-100 border-t border-gray-100 overflow-hidden;
}
.file-table td:first-child { @apply !w-8; }

/* table row hover */
.row-folder { @apply cursor-pointer; }
.row-file { @apply cursor-pointer; }

.row-folder .name, .row-file .name { @apply text-gray-800 transition-colors duration-200; }
.row-folder .name { @apply font-medium; }

.row-folder td:first-child, .row-file td:first-child { @apply border-l-[3px] border-transparent transition-[border-color] duration-200; }
.row-folder:hover td, .row-file:hover td { @apply bg-gray-50; }
.row-folder:hover .name, .row-file:hover .name { color: var(--primary); }
.row-folder:hover td:first-child, .row-file:hover td:first-child { border-left-color: var(--primary); }

/* meta */
.meta { @apply text-gray-400 text-[0.85rem] whitespace-nowrap; }
.size { @apply text-right; }
.user-icon { @apply text-gray-400 pl-4 !important; }
.col-user { @apply px-1.5 !important; }
.col-date { @apply pl-0 !important; }

/* delete btn */
.btn-del {
  @apply bg-transparent border border-gray-200 text-gray-400 text-xs px-1.5 py-px rounded cursor-pointer font-sans hover:border-[#ff5252] hover:text-[#ff5252];
}

/* spinner */
.spinner {
  @apply w-8 h-8 border-[3px] border-gray-200 rounded-full animate-spin;
  border-top-color: var(--primary);
}
</style>
