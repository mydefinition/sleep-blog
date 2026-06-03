<template>
  <nav class="flex justify-between items-center px-6 h-[52px] bg-white border-b border-gray-100 sticky top-0 z-[100] font-sans">
    <div class="flex items-center gap-4 separator-gap">
      <router-link to="/" class="font-bold text-lg no-underline" :style="{ color: settings.primary }">Sleep Blog</router-link>

      <router-link to="/articles" class="nav-link" active-class="active">
        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>
        看文章
      </router-link>
      <span v-if="articleTitle" class="nav-breadcrumb" :style="{ background: settings.primary }">{{ articleTitle }}</span>

      <router-link v-if="auth.isLoggedIn && auth.isAdmin" to="/write" class="nav-link" active-class="active">
        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 3a2.85 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5Z"/><path d="m15 5 4 4"/></svg>
        写文章
      </router-link>
      <span v-else class="nav-link disabled tooltip-host">
        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 3a2.85 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5Z"/><path d="m15 5 4 4"/></svg>
        写文章
        <span class="tooltip">只有管理员才能写文章<br>如想发表文章请联系我</span>
      </span>
      <span class="nav-link disabled tooltip-host">
        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M4 22h16a2 2 0 0 0 2-2V4a2 2 0 0 0-2-2H8a2 2 0 0 0-2 2v16a2 2 0 0 1-2 2Zm0 0a2 2 0 0 1-2-2V9a2 2 0 0 1 2-2h2"/><path d="M18 14h-8"/><path d="M15 18h-8"/><path d="M10 6h8v4"/></svg>
        读新闻
        <span class="tooltip">coming soon</span>
      </span>
      <router-link to="/files" class="nav-link" active-class="active">
        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>
        下资料
      </router-link>
      <router-link to="/about" class="nav-link" active-class="active">
        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
        关于我
      </router-link>
    </div>
    <div class="flex items-center gap-4 separator-gap">
      <button @click="openSettings" class="nav-link setting-btn" :style="{ color: settings.primary }" title="样式设置">
        <svg width="15" height="15" viewBox="0 0 24 24" fill="currentColor" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20.38 3.46 16 2a4 4 0 0 1-8 0L3.62 3.46a2 2 0 0 0-1.34 2.23l.58 3.47a1 1 0 0 0 .99.84H6v10c0 1.1.9 2 2 2h8a2 2 0 0 0 2-2V10h2.15a1 1 0 0 0 .99-.84l.58-3.47a2 2 0 0 0-1.34-2.23z"/></svg>
        样式
      </button>
      <template v-if="auth.isLoggedIn">
        <router-link to="/profile" class="nav-link" active-class="active">{{ auth.user?.username }}</router-link>
        <button @click="auth.logout" class="nav-btn">退出</button>
      </template>
      <template v-else>
        <router-link to="/login" class="nav-link">登录</router-link>
        <router-link to="/register" class="nav-link">注册</router-link>
      </template>
    </div>
  </nav>

  <div v-if="showSettings" class="fixed inset-0 z-[199]" @click="cancelSettings"></div>
  <div v-if="showSettings" class="fixed top-14 right-4 bg-white border border-gray-100 rounded-lg px-6 py-5 shadow-lg z-[200] min-w-[260px] flex flex-col gap-3">
    <h3 class="m-0 text-[0.95rem] text-gray-800">样式设置</h3>
    <label class="flex justify-between items-center text-[0.85rem] text-gray-500 gap-2">主题色</label>
    <div class="flex gap-1.5 flex-wrap items-center">
      <span v-for="c in presets" :key="c" class="color-swatch" :style="{ background: c }" :class="{ active: draft.primary === c }" @click="draft.primary = c" title="主色"></span>
    </div>
    <label class="flex justify-between items-center text-[0.85rem] text-gray-500 gap-2">预览主题
      <select v-model="draft.previewTheme" class="px-1.5 py-0.5 border border-gray-200 rounded text-xs">
        <option value="default">默认</option>
        <option value="github">GitHub</option>
        <option value="vuepress">VuePress</option>
        <option value="mk-cute">mk-cute</option>
        <option value="smart-blue">Smart Blue</option>
        <option value="cyanosis">Cyanosis</option>
      </select>
    </label>
    <label class="flex justify-between items-center text-[0.85rem] text-gray-500 gap-2">代码配色
      <select v-model="draft.codeTheme" class="px-1.5 py-0.5 border border-gray-200 rounded text-xs">
        <option value="github">GitHub 亮色</option>
        <option value="atom">Atom 暗色</option>
        <option value="a11y">a11y 暗色</option>
        <option value="androidstudio">Android Studio</option>
        <option value="dracula">Dracula</option>
        <option value="monokai">Monokai</option>
        <option value="monokai-sublime">Monokai Sublime</option>
        <option value="vs">VS 亮色</option>
        <option value="vs2015">VS 2015 暗色</option>
        <option value="xcode">Xcode</option>
      </select>
    </label>
    <div class="flex justify-end gap-2 mt-1">
      <button @click="cancelSettings" class="nav-btn">取消</button>
      <button @click="saveSettings" class="nav-btn btn-primary" :style="{ background: settings.primary, borderColor: settings.primary, color: '#fff' }">保存</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useSettingsStore } from '@/stores/settings'
import { api } from '@/api'

const route = useRoute()
const auth = useAuthStore()
const settings = useSettingsStore()
const showSettings = ref(false)
const articleTitle = ref('')

const presets = ['#42b883','#2196f3','#9c27b0','#e03131','#ff9800','#009688','#e91e63','#3f51b5']

const draft = ref({ primary: '', previewTheme: '', codeTheme: '' })
const snapshot = ref({ primary: '', previewTheme: '', codeTheme: '' })

function openSettings() {
  snapshot.value = {
    primary: settings.primary,
    previewTheme: settings.previewTheme,
    codeTheme: settings.codeTheme
  }
  draft.value = { ...snapshot.value }
  showSettings.value = true
}

function applyDraft() {
  settings.primary = draft.value.primary
  settings.previewTheme = draft.value.previewTheme
  settings.codeTheme = draft.value.codeTheme
}

function saveSettings() { applyDraft(); showSettings.value = false }
function cancelSettings() {
  settings.primary = snapshot.value.primary
  settings.previewTheme = snapshot.value.previewTheme
  settings.codeTheme = snapshot.value.codeTheme
  showSettings.value = false
}

watch(() => draft.value, () => { if (showSettings.value) applyDraft() }, { deep: true })

watch(() => route.params.id, async (id) => {
  if (id) {
    try {
      const res = await api.get(`/api/articles/${id}`)
      articleTitle.value = res.data.title
    } catch { articleTitle.value = '' }
  } else {
    articleTitle.value = ''
  }
}, { immediate: true })

onMounted(() => settings.apply())
</script>

<style scoped>
/* === Shared nav-link base === */
.nav-link {
  @apply font-mono inline-flex items-center gap-1 text-gray-600 no-underline text-sm px-3 py-1 rounded-md border-l-0;
}
.nav-link:hover {
  color: v-bind('settings.primary');
}
.nav-link.active {
  background: v-bind('settings.primary');
  @apply text-white;
}
.nav-link.disabled {
  @apply text-gray-300 cursor-not-allowed;
}

/* breadcrumb */
.nav-breadcrumb {
  @apply font-mono inline-flex items-center no-underline text-sm px-3 py-1 rounded-md border-l-0 text-white max-w-[300px] overflow-hidden text-ellipsis whitespace-nowrap -ml-2;
}

/* nav-btn */
.nav-btn {
  @apply bg-transparent border border-gray-200 px-3 py-1 rounded text-[0.85rem] text-gray-500 cursor-pointer;
}
.nav-btn:hover {
  border-color: v-bind('settings.primary');
  color: v-bind('settings.primary');
}
.btn-primary {
  @apply text-white;
}
.btn-primary:hover {
  @apply opacity-90 text-white;
}

/* setting-btn */
.setting-btn {
  @apply inline-flex items-center gap-1 no-underline bg-transparent border-none cursor-pointer font-sans;
}

/* tooltip */
.tooltip-host {
  @apply relative;
}
.tooltip {
  @apply hidden absolute top-[calc(100%+8px)] left-1/2 -translate-x-1/2 bg-gray-800 text-white px-3 py-1.5 rounded-md text-xs whitespace-nowrap z-[300] pointer-events-none;
}
.tooltip::after {
  content: "";
  @apply absolute bottom-full left-1/2 -translate-x-1/2 border-[6px] border-transparent;
  border-bottom-color: #333;
}
.tooltip-host:hover .tooltip {
  @apply block;
}

/* color swatch */
.color-swatch {
  @apply w-6 h-6 rounded cursor-pointer border-2 border-transparent transition-transform duration-150;
}
.color-swatch:hover {
  transform: scale(1.2);
}
.color-swatch.active {
  @apply border-gray-800;
  transform: scale(1.15);
}
</style>
