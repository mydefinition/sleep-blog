<template>
  <div class="max-w-[1100px] mx-auto px-4 py-8">
    <div class="flex-1 min-w-0">
      <div class="flex justify-between items-center mb-2 flex-wrap gap-4">
        <h2 class="m-0">小仓库</h2>
        <div v-if="auth.isAdmin" class="flex items-center gap-2">
          <input type="file" ref="fileInput" @change="onFileSelected" hidden/>
          <button @click="triggerUpload" :disabled="uploading"
                  class="px-4 py-1.5 bg-primary text-white border-none rounded-md cursor-pointer text-[0.85rem] font-sans disabled:opacity-40 disabled:cursor-not-allowed">
            {{ uploading ? '上传中...' : '上传 📄' }}
          </button>
          <button @click="toggleMkdir"
                  class="px-4 py-1.5 text-[0.85rem] font-sans cursor-pointer transition-all duration-200 border-[1.5px] border-primary"
                  :class="showMkdir ? 'bg-primary text-white rounded-[20px]' : 'bg-transparent text-primary rounded-md hover:bg-primary hover:text-white'">
            新建 📂
          </button>
          <span
              class="inline-flex items-center gap-1.5 max-w-0 overflow-hidden opacity-0 transition-[max-width,opacity] duration-300"
              :class="{ 'max-w-[280px] opacity-100': showMkdir }">
            <input v-model="mkdirName" placeholder="文件夹名称"
                   class="w-[160px] px-2.5 py-1.5 border-0 border-b-2 border-gray-100 text-[0.9rem] outline-none text-gray-800 font-sans bg-transparent transition-[border-color] duration-150 placeholder:text-gray-300"
                   :class="{ 'border-b-primary': mkdirName || mkdirFocused }" @keyup.enter="doMkdir"
                   @focus="mkdirFocused = true" @blur="mkdirFocused = false" ref="mkdirInput"/>
            <button @click="doMkdir" :disabled="!mkdirName.trim()"
                    class="px-4 py-1.5 bg-transparent text-primary border-[1.5px] border-primary rounded-md text-[0.85rem] font-sans cursor-pointer whitespace-nowrap transition-all duration-200 hover:bg-primary hover:text-white disabled:opacity-35 disabled:cursor-not-allowed">确定</button>
          </span>
        </div>
      </div>

      <!-- 面包屑 -->
      <div class="py-2 text-[0.9rem] text-gray-400">
        <span v-for="(crumb, i) in breadcrumbs" :key="crumb.id" class="breadcrumb-sep">
          <a v-if="i < breadcrumbs.length - 1" href="#" @click.prevent="goToCrumb(i)"
             class="text-gray-400 no-underline hover:text-primary transition-colors duration-150">{{ crumb.name }}</a>
          <span v-else class="text-primary font-medium">{{ crumb.name }}</span>
        </span>
      </div>

      <div v-if="loading" class="flex justify-center min-h-[30vh] items-center">
        <span class="inline-block w-8 h-8 border-[3px] border-gray-200 rounded-full animate-spin border-t-primary"></span>
      </div>
      <div v-else-if="errorMsg" class="text-[#ff5252] text-center py-8">{{ errorMsg }}</div>

      <table v-else class="w-full table-fixed border-collapse text-[0.9rem]">
        <tbody>
        <tr v-if="parentId !== null" class="cursor-pointer group" @click="goUp">
          <td class="!w-8 px-2.5 py-2 border-y border-gray-100 overflow-hidden border-l-[3px] border-transparent group-hover:border-l-primary group-hover:bg-gray-50 transition-[border-color,background] duration-200">
            <CornerUpLeft :size="14"/>
          </td>
          <td class="overflow-hidden whitespace-nowrap text-gray-800 font-medium px-2.5 py-2 border-y border-gray-100 group-hover:bg-gray-50 group-hover:text-primary transition-colors duration-200">
            上级目录
          </td>
          <td class="text-gray-400 text-[0.85rem] whitespace-nowrap text-right !w-[70px] px-2.5 py-2 border-y border-gray-100 group-hover:bg-gray-50"></td>
          <td class="!w-[18px] text-gray-400 px-0 py-2 border-y border-gray-100 group-hover:bg-gray-50"></td>
          <td class="text-gray-400 text-[0.85rem] whitespace-nowrap !w-[100px] px-0 py-2 border-y border-gray-100 group-hover:bg-gray-50"></td>
          <td class="!w-[18px] text-gray-400 px-0 py-2 border-y border-gray-100 group-hover:bg-gray-50"></td>
          <td class="text-gray-400 text-[0.85rem] whitespace-nowrap !w-[90px] px-0 py-2 border-y border-gray-100 group-hover:bg-gray-50"></td>
          <td v-if="auth.isAdmin"
              class="!w-[70px] text-center px-2.5 py-2 border-y border-gray-100 group-hover:bg-gray-50"></td>
        </tr>
        <tr v-for="item in currentItems" :key="item.id"
            @click="item.isDir ? enterDir(item) : downloadFile(item.id)"
            class="cursor-pointer group">
          <td class="!w-8 px-2.5 py-2 border-y border-gray-100 overflow-hidden border-l-[3px] border-transparent group-hover:border-l-primary group-hover:bg-gray-50 transition-[border-color,background] duration-200">
            {{ item.isDir ? '📁' : '📄' }}
          </td>
          <td class="overflow-hidden whitespace-nowrap text-gray-800 font-medium px-2.5 py-2 border-y border-gray-100 group-hover:bg-gray-50 group-hover:text-primary transition-colors duration-200">
            {{ item.fileName }}
          </td>
          <td class="text-gray-400 text-[0.85rem] whitespace-nowrap text-right !w-[70px] px-2.5 py-2 border-y border-gray-100 group-hover:bg-gray-50">
            -
          </td>
          <td class="!w-[18px] text-gray-400 px-0 py-2 border-y border-gray-100 group-hover:bg-gray-50"></td>
          <td class="text-gray-400 text-[0.85rem] whitespace-nowrap !w-[100px] px-0 py-2 border-y border-gray-100 group-hover:bg-gray-50">
            {{ item.userName || '-' }}
          </td>
          <td class="!w-[18px] text-gray-400 px-0 py-2 border-y border-gray-100 group-hover:bg-gray-50"></td>
          <td class="text-gray-400 text-[0.85rem] whitespace-nowrap !w-[90px] px-0 py-2 border-y border-gray-100 group-hover:bg-gray-50">
            {{ item.createdAt ? new Date(item.createdAt).toLocaleDateString('zh-CN') : '-' }}
          </td>
          <td v-if="auth.isAdmin"
              class="!w-[70px] text-center px-2.5 py-2 border-y border-gray-100 group-hover:bg-gray-50">
            <button @click.stop="deleteNode(item.id)"
                    class="px-3 py-0.5 bg-transparent text-[#ff5252] border-[1.5px] border-[#ff5252] rounded-md text-xs font-sans cursor-pointer whitespace-nowrap transition-all duration-200 hover:bg-[#ff5252] hover:text-white">
              删除
            </button>
          </td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue'
import { api } from '@/api'
import { useAuthStore } from '@/stores/auth'
import { CornerUpLeft } from 'lucide-vue-next'
import type { CloudFile } from '@/types'

const auth = useAuthStore()

// ---- 导航状态 ----
const breadcrumbs = ref<{ id: number; name: string }[]>([{ id: 0, name: '根目录' }])

const dirCache = ref(new Map<number, CloudFile[]>())

const loading = ref(false)
const errorMsg = ref('')

const currentDirId = computed(() => breadcrumbs.value[breadcrumbs.value.length - 1]?.id ?? 0)

const currentItems = computed(() => {
  const items = dirCache.value.get(currentDirId.value) ?? []
  // 文件夹在前，文件在后
  return [...items].sort((a, b) => {
    if (a.isDir !== b.isDir) return b.isDir - a.isDir  // 1(文件夹) > 0(文件)
    return a.fileName.localeCompare(b.fileName)
  })
})

const parentId = computed(() => {
  if (breadcrumbs.value.length <= 1) return null
  return breadcrumbs.value[breadcrumbs.value.length - 2]?.id ?? null
})

function enterDir(item: CloudFile) {
  breadcrumbs.value.push({ id: item.id, name: item.fileName })
  loadCurrentDir()
}

function goUp() {
  if (breadcrumbs.value.length <= 1) return
  breadcrumbs.value.pop()
  loadCurrentDir()
}

function goToCrumb(index: number) {
  breadcrumbs.value = breadcrumbs.value.slice(0, index + 1)
  loadCurrentDir()
}

// ---- 数据加载 ----
async function loadCurrentDir() {
  const dirId = currentDirId.value
  if (dirCache.value.has(dirId)) {
    loading.value = false
    errorMsg.value = ''
    return
  }
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await api.get(`/api/cloud/${dirId}`)
    const list: CloudFile[] = res.data || []
    dirCache.value.set(dirId, list)
  } catch (e: any) {
    errorMsg.value = e.message || '加载失败'
  } finally {
    loading.value = false
  }
}

// ---- 上传 ----
const fileInput = ref<HTMLInputElement | null>(null)
const uploading = ref(false)

function triggerUpload() {
  fileInput.value?.click()
}

let pendingFile: File | null = null

function onFileSelected(e: Event) {
  const input = e.target as HTMLInputElement
  const f = input.files?.[0]
  if (f) {
    pendingFile = f
    doUpload()
  }
}

async function doUpload() {
  if (!pendingFile) return
  uploading.value = true
  try {
    const fd = new FormData()
    fd.append('file', pendingFile)
    fd.append('localId', String(currentDirId.value))
    await fetch('/api/cloud/upload', { method: 'POST', body: fd })
    if (fileInput.value) fileInput.value.value = ''
    pendingFile = null
    dirCache.value.delete(currentDirId.value)
    await loadCurrentDir()
  } catch (e: any) {
    alert(e.message || '上传失败')
  } finally {
    uploading.value = false
  }
}

// ---- 新建文件夹 ----
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

async function doMkdir() {
  if (!mkdirName.value.trim()) return
  try {
    await api.post('/api/cloud/mkdir', {
      localId: currentDirId.value,
      dirName: mkdirName.value.trim(),
    })
    mkdirName.value = ''
    showMkdir.value = false
    dirCache.value.delete(currentDirId.value)
    await loadCurrentDir()
  } catch (e: any) {
    alert(e.message || '创建失败')
  }
}

// ---- 删除 ----
async function deleteNode(id: number) {
  const items = currentItems.value
  const node = items.find(n => n.id === id)
  const msg = node?.isDir
      ? `确定删除文件夹"${node.fileName}"及其所有内容？`
      : `确定删除文件 "${node?.fileName ?? ''}"？`
  if (!confirm(msg)) return
  try {
    await api.delete(`/api/cloud/${id}`)
    dirCache.value.delete(currentDirId.value)
    await loadCurrentDir()
  } catch (e: any) {
    alert(e.message || '删除失败')
  }
}

// ---- 下载 ----
function downloadFile(id: number) {
  window.open(`/api/cloud/${id}/download`, '_blank')
}

onMounted(loadCurrentDir)
</script>