<template>
  <nav class="flex justify-between items-center px-6 h-[52px] bg-white border-b border-gray-100 sticky top-0 z-[100] font-sans">
    <ul class="flex items-center gap-4 list-none m-0 p-0 separator-gap">
      <li>
        <router-link to="/" class="font-bold text-lg no-underline" :style="{ color: settings.primary }">Sleep Blog</router-link>
      </li>
      <li>
        <router-link to="/articles" class="nav-link" active-class="nav-active">
          <FileText :size="15" /> 看文章
        </router-link>
      </li>
      <li v-if="articleTitle">
        <span class="font-mono inline-flex items-center no-underline text-sm px-3 py-1 rounded-md text-white max-w-[300px] overflow-hidden text-ellipsis whitespace-nowrap -ml-2" :style="{ background: settings.primary }">{{ articleTitle }}</span>
      </li>
      <li>
        <span class="nav-link nav-disabled cursor-not-allowed relative group/tooltip">
          <Newspaper :size="15" /> 读新闻
          <span class="hidden group-hover/tooltip:block absolute top-[calc(100%+8px)] left-1/2 -translate-x-1/2 bg-gray-800 text-white px-3 py-1.5 rounded-md text-xs whitespace-nowrap z-[300] pointer-events-none after:content-[''] after:absolute after:bottom-full after:left-1/2 after:-translate-x-1/2 after:border-[6px] after:border-transparent after:border-b-gray-800">coming soon</span>
        </span>
      </li>
      <li>
        <router-link to="/files" class="nav-link" active-class="nav-active">
          <Download :size="15" /> 下资料
        </router-link>
      </li>
      <li>
        <router-link to="/about" class="nav-link" active-class="nav-active">
          <User :size="15" /> 关于我
        </router-link>
      </li>
    </ul>

    <ul class="flex items-center gap-4 list-none m-0 p-0 separator-gap">
      <li>
        <button @click="openSettings" class="nav-link font-sans bg-transparent border-none cursor-pointer" :style="{ color: settings.primary }" title="样式设置">
          <Palette :size="15" /> 样式
        </button>
      </li>

      <template v-if="auth.isLoggedIn">
        <li class="relative"
            @mouseenter="openMenu"
            @mouseleave="startMenuClose">
          <button class="nav-link font-sans bg-transparent border-none cursor-pointer">
            {{ auth.user?.nickname }}
          </button>
          <div v-if="showMenu"
               class="absolute right-0 top-full mt-1 bg-white border border-gray-100 rounded-lg shadow-lg z-[300] min-w-[140px] py-1.5"
               @mouseenter="cancelMenuClose"
               @mouseleave="startMenuClose">
            <router-link v-if="auth.isAdmin" to="/write"
                         class="flex items-center gap-2 px-4 py-2 text-[0.88rem] text-gray-700 no-underline hover:bg-gray-50 hover:text-primary transition-colors"
                         @click="showMenu = false">
              <Pencil :size="14" /> 写文章
            </router-link>
            <router-link v-if="auth.isAdmin" to="/drafts"
                         class="flex items-center gap-2 px-4 py-2 text-[0.88rem] text-gray-700 no-underline hover:bg-gray-50 hover:text-primary transition-colors"
                         @click="showMenu = false">
              <Archive :size="14" /> 草稿箱
            </router-link>
            <router-link to="/profile"
                         class="flex items-center gap-2 px-4 py-2 text-[0.88rem] text-gray-700 no-underline hover:bg-gray-50 hover:text-primary transition-colors"
                         @click="showMenu = false">
              <User :size="14" /> 个人信息
            </router-link>
            <div class="border-t border-gray-100 my-1"></div>
            <button @click="auth.logout()"
                    class="flex items-center gap-2 w-full px-4 py-2 text-[0.88rem] text-gray-700 bg-transparent border-none cursor-pointer hover:bg-gray-50 hover:text-red-500 transition-colors">
              <LogOut :size="14" /> 退出登录
            </button>
          </div>
        </li>
      </template>

      <template v-else>
        <li>
          <button @click="$emit('openAuth', 'login')" class="nav-link font-sans bg-transparent border-none cursor-pointer">登录</button>
        </li>
        <li>
          <button @click="$emit('openAuth', 'register')" class="nav-link font-sans bg-transparent border-none cursor-pointer">注册</button>
        </li>
      </template>
    </ul>
  </nav>

  <div v-if="showSettings" class="fixed inset-0 z-[199]" @click="cancelSettings"></div>

  <div v-if="showSettings" class="fixed top-14 right-4 bg-white border border-gray-100 rounded-lg px-6 py-5 shadow-lg z-[200] min-w-[260px] flex flex-col gap-3">
    <h3 class="m-0 text-[0.95rem] text-gray-800">样式设置</h3>
    <label class="flex justify-between items-center text-[0.85rem] text-gray-500 gap-2">主题色</label>
    <div class="flex gap-1.5 flex-wrap items-center">
      <span v-for="c in presets" :key="c" class="w-6 h-6 rounded cursor-pointer border-2 border-transparent hover:scale-[1.2] transition-transform duration-150" :class="{ '!border-gray-800 scale-[1.15]': draft.primary === c }" :style="{ background: c }" @click="draft.primary = c" title="主色"></span>
    </div>
    <label class="flex justify-between items-center text-[0.85rem] text-gray-500 gap-2">
      预览主题
      <select v-model="draft.previewTheme" class="px-1.5 py-0.5 border border-gray-200 rounded text-xs">
        <option value="default">默认</option>
        <option value="github">GitHub</option>
        <option value="vuepress">VuePress</option>
        <option value="mk-cute">mk-cute</option>
        <option value="smart-blue">Smart Blue</option>
        <option value="cyanosis">Cyanosis</option>
      </select>
    </label>
    <label class="flex justify-between items-center text-[0.85rem] text-gray-500 gap-2">
      代码配色
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
      <button @click="saveSettings" class="nav-btn hover:opacity-90" :style="{ background: settings.primary, borderColor: settings.primary, color: '#fff' }">保存</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useSettingsStore } from '@/stores/settings'
import { api } from '@/api'
import { FileText, Pencil, Newspaper, Download, User, Palette, Archive, LogOut } from 'lucide-vue-next'

defineEmits<{ openAuth: [tab: 'login' | 'register'] }>()

const route = useRoute()
const auth = useAuthStore()
const settings = useSettingsStore()
const showSettings = ref(false)
const showMenu = ref(false)
const articleTitle = ref('')
let menuCloseTimer: ReturnType<typeof setTimeout> | null = null

function openMenu() { cancelMenuClose(); showMenu.value = true }
function startMenuClose() { menuCloseTimer = setTimeout(() => { showMenu.value = false }, 300) }
function cancelMenuClose() { if (menuCloseTimer) { clearTimeout(menuCloseTimer); menuCloseTimer = null } }

const presets = ['#42b883','#2196f3','#9c27b0','#e03131','#ff9800','#009688','#e91e63','#3f51b5']

const draft = ref({ primary: '', previewTheme: '', codeTheme: '' })
const snapshot = ref({ primary: '', previewTheme: '', codeTheme: '' })

function openSettings() {
  snapshot.value = { primary: settings.primary, previewTheme: settings.previewTheme, codeTheme: settings.codeTheme }
  draft.value = { ...snapshot.value }
  showSettings.value = true
}
function applyDraft() { settings.primary = draft.value.primary; settings.previewTheme = draft.value.previewTheme; settings.codeTheme = draft.value.codeTheme }
function saveSettings() { applyDraft(); showSettings.value = false }
function cancelSettings() {
  settings.primary = snapshot.value.primary; settings.previewTheme = snapshot.value.previewTheme; settings.codeTheme = snapshot.value.codeTheme
  showSettings.value = false
}

watch(() => draft.value, () => { if (showSettings.value) applyDraft() }, { deep: true })

watch(() => route.params.id, async (id) => {
  if (id) { try { const res = await api.get(`/api/articles/${id}`); articleTitle.value = res.data.title } catch { articleTitle.value = '' } }
  else { articleTitle.value = '' }
}, { immediate: true })

onMounted(() => settings.apply())
</script>