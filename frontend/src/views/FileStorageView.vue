<template>
  <div class="files-page">
    <div class="files-main">
      <div class="files-header">
        <h2>小仓库</h2>
        <div v-if="auth.isAdmin" class="toolbar">
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

      <div class="breadcrumb">
        <span v-for="(part, i) in breadcrumbParts" :key="i">
          <a v-if="i < breadcrumbParts.length - 1" href="#" @click.prevent="navigateTo(i)">{{ part.name }}</a>
          <span v-else>{{ part.name }}</span>
        </span>
      </div>

      <div v-if="loading" class="loading-wrap"><span class="spinner"></span></div>
      <div v-else-if="errorMsg" class="error">{{ errorMsg }}</div>

      <table v-else class="file-table">
        <tbody>
          <tr v-if="parentId !== null" class="row-folder" @click="currentDirId = parentId">
            <td>
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="9 14 4 9 9 4"/><path d="M20 20v-7a4 4 0 0 0-4-4H4"/></svg>
            </td>
            <td class="name">上级目录</td>
            <td class="meta size"></td>
            <td class="user-icon"></td>
            <td class="meta col-user"></td>
            <td class="user-icon"></td>
            <td class="meta col-date"></td>
            <td v-if="auth.isAdmin" class="col-del"></td>
          </tr>
          <tr v-for="n in currentChildren" :key="n.id"
              :class="n.isDir ? 'row-folder' : 'row-file'"
              @click="n.isDir ? currentDirId = n.id : downloadFile(n.id)">
            <td>{{ n.isDir ? '📁' : '📄' }}</td>
            <td class="name"><OverflowScroll :text="n.name" /></td>
            <td class="meta size">{{ n.isDir ? '' : formatSize(n.size!) }}</td>
            <td class="user-icon">
              <svg v-if="!n.isDir" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
            </td>
            <td class="meta col-user">
              <OverflowScroll v-if="!n.isDir" :text="n.uploaderName || ''" />
            </td>
            <td class="user-icon">
              <svg v-if="!n.isDir" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M8 2v4"/><path d="M16 2v4"/><rect width="18" height="18" x="3" y="4" rx="2"/><path d="M3 10h18"/><path d="M8 14h.01"/><path d="M12 14h.01"/><path d="M16 14h.01"/><path d="M8 18h.01"/><path d="M12 18h.01"/><path d="M16 18h.01"/></svg>
            </td>
            <td class="meta col-date">
              <span v-if="!n.isDir">{{ n.uploadAt }}</span>
            </td>
            <td v-if="auth.isAdmin" class="col-del">
              <button class="btn-del" @click.stop="deleteNode(n.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-if="!loading && currentChildren.length === 0 && parentId === null" class="empty">—— 此目录为空 ——</div>
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

// 当前所在目录 id（0 = 根目录）
const currentDirId = ref(0)

// 全量缓存
const nodeMap = ref(new Map<number, FileTreeNode>())
const pathIdMap = ref(new Map<string, number>())

// 上传
const fileInput = ref<HTMLInputElement | null>(null)
const uploading = ref(false)

// 新建文件夹
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

// 当前目录的子节点
const currentChildren = computed(() => {
  const node = nodeMap.value.get(currentDirId.value)
  if (!node || !node.children) return []
  return node.children
})

// 父目录 id
const parentId = computed(() => {
  if (currentDirId.value === 0) return null
  const node = nodeMap.value.get(currentDirId.value)
  if (!node || !node.path) return null
  const parentPath = node.path.substring(0, node.path.lastIndexOf('/'))
  if (!parentPath) return 0
  return pathIdMap.value.get(parentPath) ?? 0
})

// 面包屑
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

// 加载
async function loadTree() {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await api.get('/api/files')
    const tree: FileTreeNode[] = res.data || []
    const map = new Map<number, FileTreeNode>()
    const pathMap = new Map<string, number>()
    // 根节点 id=0
    map.set(0, { id: 0, name: ', isDir: true, path: ', children: tree })
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

// 上传
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
    const token = localStorage.getItem('token')
    await fetch('/api/files/upload', {
      method: 'POST',
      headers: { 'Authorization': 'Bearer ' + token },
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

// 新建文件夹
async function doMkdir() {
  if (!mkdirName.value.trim()) return
  try {
    const token = localStorage.getItem('token')
    await fetch('/api/files/mkdir', {
      method: 'POST',
      headers: {
        'Authorization': 'Bearer ' + token,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ name: mkdirName.value.trim(), localId: currentDirId.value })
    })
    mkdirName.value = ''
    showMkdir.value = false
    await loadTree()
  } catch (e: any) {
    alert(e.message || '创建失败')
  }
}

// 删除
async function deleteNode(id: number) {
  const node = nodeMap.value.get(id)
  const msg = node?.isDir ? '确定删除文件夹 "' + node.name + '" 及其所有内容？' : '确定删除文件 "' + (node?.name ?? '') + '"？'
  if (!confirm(msg)) return
  try {
    const token = localStorage.getItem('token')
    await fetch('/api/files/' + id, {
      method: 'DELETE',
      headers: { 'Authorization': 'Bearer ' + token }
    })
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
/* === 页面布局 === */
.files-page { max-width: 1100px; margin: 0 auto; padding: 2rem 1rem; }
.files-main { flex: 1; min-width: 0; }

/* === 头部工具栏 === */
.files-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.5rem; flex-wrap: wrap; gap: 1rem; }
.files-header h2 { margin: 0; }
.toolbar { display: flex; align-items: center; gap: 0.5rem; }

.btn-upload {
  padding: 0.35rem 1rem; background: var(--primary); color: #fff;
  border: none; border-radius: 6px; cursor: pointer; font-size: 0.85rem; font-family: inherit;
}
.btn-upload:disabled { opacity: 0.4; cursor: not-allowed; }

.btn-mkdir {
  padding: 0.3rem 0.9rem;
  background: transparent; color: var(--primary);
  border: 1.5px solid var(--primary); border-radius: 6px;
  cursor: pointer; font-size: 0.85rem; font-family: inherit;
  transition: background 0.2s, color 0.2s, border-radius 0.3s;
}
.btn-mkdir:hover { background: var(--primary); color: #fff; }
.btn-mkdir.on {
  background: var(--primary); color: #fff;
  border-radius: 20px; border-color: var(--primary);
}

/* === 新建文件夹滑入输入框 === */
.mkdir-wrap {
  display: inline-flex; align-items: center; gap: 0.4rem;
  max-width: 0; overflow: hidden; opacity: 0;
  transition: max-width 0.3s ease, opacity 0.25s ease;
}
.mkdir-wrap.open { max-width: 280px; opacity: 1; }
.mkdir-input {
  width: 160px; padding: 0.4rem 0.6rem;
  border: none; border-bottom: 2px solid #eee;
  font-size: 0.9rem; outline: none; color: #333;
  font-family: inherit; background: transparent;
  transition: border-color 0.15s;
}
.mkdir-input::placeholder { color: #ccc; }
.mkdir-input:focus { border-bottom-color: var(--primary); }
.mkdir-wrap.active .mkdir-input { border-bottom-color: var(--primary); }
.btn-mkdir-confirm {
  padding: 0.25rem 0.8rem;
  background: transparent; color: var(--primary);
  border: 1.5px solid var(--primary); border-radius: 6px;
  font-size: 0.8rem; font-family: inherit; cursor: pointer;
  white-space: nowrap;
  transition: background 0.2s, color 0.2s, border-radius 0.3s, opacity 0.2s;
}
.btn-mkdir-confirm:hover:not(:disabled) {
  background: var(--primary); color: #fff;
  border-radius: 20px;
}
.btn-mkdir-confirm:disabled { opacity: 0.35; cursor: not-allowed; }

/* === 面包屑 === */
.breadcrumb {
  margin-bottom: 0; padding: 0.5rem 0; font-size: 0.9rem; color: #999;
}
.breadcrumb span::after { content: ' / '; color: #ccc; }
.breadcrumb span:last-child::after { content: ''; }
.breadcrumb a { color: #999; text-decoration: none; }
.breadcrumb a:hover { color: var(--primary); }
.breadcrumb span:last-child { color: var(--primary); font-weight: 500; }

/* === 文件表格 === */
.file-table { width: 100%; table-layout: fixed; border-collapse: collapse; font-size: 0.9rem; }
.file-table td {
  padding: 0.55rem 0.6rem; border-bottom: 1px solid #f0f0f0; border-top: 1px solid #f0f0f0; overflow: hidden;
}
.file-table td:first-child { width: 32px; }
.name { overflow: hidden; white-space: nowrap; }

/* === 行 hover 效果 === */
.row-folder { cursor: pointer; }
.row-file { cursor: pointer; }

.row-folder .name, .row-file .name { color: #333; transition: color .2s; }
.row-folder .name { font-weight: 500; }

.row-folder td:first-child, .row-file td:first-child { border-left: 3px solid transparent; transition: border-color .2s; }
.row-folder:hover td, .row-file:hover td { background: #fafafa; }
.row-folder:hover .name, .row-file:hover .name { color: var(--primary); }
.row-folder:hover td:first-child, .row-file:hover td:first-child { border-left-color: var(--primary); }

/* === 元数据列 === */
.meta { color: #999; font-size: 0.85rem; white-space: nowrap; }
.size { text-align: right; width: 70px; }
.user-icon { width: 18px; color: #999; padding: 0.55rem 0 0.55rem 1rem !important; }
.col-user { width: 100px; padding: 0.55rem 0.3rem 0.55rem 0 !important; }
.col-date { width: 90px; padding-left: 0 !important; }

/* === 删除按钮 === */
.col-del { width: 50px; text-align: center; }
.btn-del {
  background: none; border: 1px solid #e0e0e0; color: #999;
  font-size: 0.75rem; padding: 0.15rem 0.4rem; border-radius: 4px;
  cursor: pointer; font-family: inherit;
}
.btn-del:hover { border-color: #ff5252; color: #ff5252; }

/* === 通用状态 === */
.loading-wrap { display: flex; justify-content: center; min-height: 30vh; align-items: center; }
.spinner { width: 32px; height: 32px; border: 3px solid #e0e0e0; border-top-color: var(--primary); border-radius: 50%; animation: spin 0.7s linear infinite; }
.error { color: #ff5252; text-align: center; padding: 2rem; }
.empty { text-align: center; padding: 3rem 0; color: #bbb; font-size: 0.9rem; }
@keyframes spin { to { transform: rotate(360deg); } }
</style>