<template>
  <nav class="navbar">
    <div class="nav-left">
      <router-link to="/" class="logo">Sample Blog</router-link>

      <router-link to="/articles" class="nav-link" active-class="active">
        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>
        看文章
      </router-link>
      <span v-if="articleTitle" class="nav-breadcrumb">{{ articleTitle }}</span>

      <router-link v-if="auth.isLoggedIn && auth.isAdmin" to="/write" class="nav-link" active-class="active">
        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 3a2.85 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5Z"/><path d="m15 5 4 4"/></svg>
        写文章
      </router-link>
      <span v-else class="nav-link disabled tooltip-host">
        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 3a2.85 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5Z"/><path d="m15 5 4 4"/></svg>
        写文章
        <span class="tooltip">只有管理员才能写文章<br>如想发表文章请联系我</span>
      </span>
      <a href="#" class="nav-link">
        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M4 22h16a2 2 0 0 0 2-2V4a2 2 0 0 0-2-2H8a2 2 0 0 0-2 2v16a2 2 0 0 1-2 2Zm0 0a2 2 0 0 1-2-2V9a2 2 0 0 1 2-2h2"/><path d="M18 14h-8"/><path d="M15 18h-8"/><path d="M10 6h8v4"/></svg>
        读新闻
      </a>
      <a href="#" class="nav-link">
        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>
        下资料
      </a>
      <a href="#" class="nav-link">
        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
        关于我
      </a>
    </div>
    <div class="nav-right">
      <button @click="openSettings" class="nav-link setting-btn" title="样式设置">
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

  <div v-if="showSettings" class="settings-backdrop" @click="cancelSettings"></div>
  <div v-if="showSettings" class="settings-panel">
    <h3>样式设置</h3>
    <label>主题色</label>
    <div class="color-presets">
      <span v-for="c in presets" :key="c" class="color-swatch" :style="{ background: c }" :class="{ active: draft.primary === c }" @click="draft.primary = c" title="主色"></span>
    </div>
    <label>预览主题
      <select v-model="draft.previewTheme">
        <option value="default">默认</option>
        <option value="github">GitHub</option>
        <option value="vuepress">VuePress</option>
        <option value="mk-cute">mk-cute</option>
        <option value="smart-blue">Smart Blue</option>
        <option value="cyanosis">Cyanosis</option>
      </select>
    </label>
    <label>代码配色
      <select v-model="draft.codeTheme">
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
    <div class="settings-actions">
      <button @click="cancelSettings" class="nav-btn">取消</button>
      <button @click="saveSettings" class="nav-btn btn-primary">保存</button>
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
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 1.5rem;
  height: 52px;
  background: #fff;
  border-bottom: 1px solid #eee;
  position: sticky;
  top: 0;
  z-index: 100;
  font-family: 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Noto Sans SC', sans-serif;
}

.nav-left,
.nav-right {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.logo {
  font-weight: 700;
  font-size: 1.1rem;
  color: v-bind('settings.primary');
  text-decoration: none;
}

.nav-link {
  font-family: 'JetBrains Mono', 'Consolas', 'Microsoft YaHei', monospace;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: #555;
  text-decoration: none;
  font-size: 0.875rem;
  padding: 0.25rem 0.75rem;
  border-radius: 6px;
  border-left: none;
}

.nav-link:hover {
  color: v-bind('settings.primary');
}

.nav-link.active {
  background: v-bind('settings.primary');
  color: #fff;
}

.nav-link.disabled {
  color: #ccc;
  cursor: not-allowed;
}

/* gap分隔线 */
.nav-left > *,
.nav-right > * {
  position: relative;
}

.nav-left > :not(:first-child)::before,
.nav-right > :not(:first-child)::before {
  content: '';
  position: absolute;
  left: -0.5rem;
  top: 0;
  bottom: 0;
  border-left: 1px solid #eee;
}

.nav-breadcrumb {
  font-family: 'JetBrains Mono', 'Consolas', 'Microsoft YaHei', monospace;
  display: inline-flex;
  align-items: center;
  text-decoration: none;
  font-size: 0.875rem;
  padding: 0.25rem 0.75rem;
  border-radius: 6px;
  border-left: none;
  color: #fff;
  background: v-bind('settings.primary');
  max-width: 300px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-left: -0.5rem;
}

.nav-btn {
  background: none;
  border: 1px solid #ddd;
  padding: 0.3rem 0.8rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
  color: #666;
}

.nav-btn:hover {
  border-color: v-bind('settings.primary');
  color: v-bind('settings.primary');
}

.btn-primary {
  background: v-bind("settings.primary");
  color: #fff;
  border-color: v-bind("settings.primary");
}

.btn-primary:hover {
  opacity: 0.9;
  color: #fff;
}

.setting-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: #555;
  text-decoration: none;
  background: none;
  border: none;
  cursor: pointer;
  font-family: inherit;
}

/* 提示框 */
.tooltip-host {
  position: relative;
}

.tooltip {
  display: none;
  position: absolute;
  bottom: calc(100% + 8px);
  left: 50%;
  transform: translateX(-50%);
  background: #333;
  color: #fff;
  padding: 0.4rem 0.8rem;
  border-radius: 6px;
  font-size: 0.8rem;
  white-space: nowrap;
  z-index: 300;
  pointer-events: none;
}

.tooltip::after {
  content: "";
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%);
  border: 6px solid transparent;
  border-top-color: #333;
}

.tooltip-host:hover .tooltip {
  display: block;
}

/* 样式设置图标颜色 */
.setting-btn {
  color: v-bind('settings.primary');
}

/* 设置面板遮罩 */
.settings-backdrop {
  position: fixed;
  inset: 0;
  z-index: 199;
}

/* 设置面板 */
.settings-panel {
  position: fixed;
  top: 56px;
  right: 1rem;
  background: #fff;
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 1.2rem 1.5rem;
  box-shadow: 0 4px 16px rgba(0,0,0,0.1);
  z-index: 200;
  min-width: 260px;
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
}

.settings-panel h3 {
  margin: 0;
  font-size: 0.95rem;
  color: #333;
}

.settings-panel label {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.85rem;
  color: #666;
  gap: 0.5rem;
}

.settings-panel input[type="color"] {
  width: 36px;
  height: 28px;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  padding: 1px;
}

.settings-panel select {
  padding: 0.2rem 0.4rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 0.8rem;
}

.settings-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
  margin-top: 0.4rem;
}

.color-presets {
  display: flex;
  gap: 0.35rem;
  flex-wrap: wrap;
  align-items: center;
}

.color-swatch {
  width: 24px;
  height: 24px;
  border-radius: 4px;
  cursor: pointer;
  border: 2px solid transparent;
  transition: transform 0.15s;
}

.color-swatch:hover {
  transform: scale(1.2);
}

.color-swatch.active {
  border-color: #333;
  transform: scale(1.15);
}
</style>