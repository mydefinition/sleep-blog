<template>
  <div class="about">
    <div class="about-layout">
      <div class="spacer"></div>
      <div class="about-main">
        <div class="about-card">
          <img src="/avatar.png" alt="avatar" class="avatar" />
          <h2 class="name">I love JT</h2>
          <p class="bio">
            存活状态：尚存，最近在学CSAPP ing……
          </p>
          <p class="bio">
            亚洲区域赛金，全国邀请赛银，电子科技大学算法与逻辑实验室在读。AI时代我相信人定胜天。
          </p>
          <div class="links">
            <a href="https://github.com/mydefinition" target="_blank" class="link">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor"><path d="M12 0C5.37 0 0 5.37 0 12c0 5.31 3.435 9.795 8.205 11.385.6.105.825-.255.825-.57 0-.285-.015-1.23-.015-2.235-3.015.555-3.795-.735-4.035-1.41-.135-.345-.72-1.41-1.23-1.695-.42-.225-1.02-.78-.015-.795.945-.015 1.62.87 1.845 1.23 1.08 1.815 2.805 1.305 3.495.99.105-.78.42-1.305.765-1.605-2.67-.3-5.46-1.335-5.46-5.925 0-1.305.465-2.385 1.23-3.225-.12-.3-.54-1.53.12-3.18 0 0 1.005-.315 3.3 1.23.96-.27 1.98-.405 3-.405s2.04.135 3 .405c2.295-1.56 3.3-1.23 3.3-1.23.66 1.65.24 2.88.12 3.18.765.84 1.23 1.905 1.23 3.225 0 4.605-2.805 5.625-5.475 5.925.435.375.81 1.095.81 2.22 0 1.605-.015 2.895-.015 3.3 0 .315.225.69.825.57A12.02 12.02 0 0 0 24 12c0-6.63-5.37-12-12-12z"/></svg>
              GitHub
            </a>
            <span class="link copy-link" @click="copyQQ">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor"><path d="M21.395 15.035a39.548 39.548 0 0 0-.803-2.264l-1.079-2.695c.001-.032.014-.562.014-.836C19.526 4.632 17.351 0 12 0S4.474 4.632 4.474 9.241c0 .274.013.804.014.836l-1.08 2.695a38.97 38.97 0 0 0-.802 2.264c-1.021 3.283-.69 4.643-.438 4.673.54.065 2.103-2.472 2.103-2.472 0 1.469.756 3.387 2.394 4.773-.612.529-1.334.921-1.616 1.362-.407.638-.187 1.087.588 1.087.44 0 1.05-.371 1.594-.748 1.029.309 2.218.489 3.463.489 1.245 0 2.434-.18 3.463-.489.544.377 1.154.748 1.594.748.775 0 .995-.449.588-1.087-.282-.441-1.004-.833-1.616-1.362 1.638-1.386 2.394-3.304 2.394-4.773 0 0 1.563 2.537 2.103 2.472.252-.03.583-1.39-.438-4.673z"/></svg>
              QQ
              <span v-if="qqCopied" class="copy-toast">已复制</span>
            </span>
            <span class="link copy-link" @click="copyWechat">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor"><path d="M8.691 2.188C3.891 2.188 0 5.476 0 9.53c0 2.212 1.17 4.203 3.002 5.55a.59.59 0 0 1 .213.665l-.39 1.48c-.019.07-.048.141-.048.213 0 .163.13.295.29.295a.326.326 0 0 0 .167-.054l1.903-1.114a.864.864 0 0 1 .717-.098 10.16 10.16 0 0 0 2.837.403c.276 0 .543-.027.811-.05-.857-2.578.157-4.972 1.932-6.446 1.703-1.415 3.882-1.98 5.853-1.838-.576-3.583-4.196-6.348-8.596-6.348zM5.785 5.991c.642 0 1.162.529 1.162 1.18a1.17 1.17 0 0 1-1.162 1.178A1.17 1.17 0 0 1 4.623 7.17c0-.651.52-1.18 1.162-1.18zm5.813 0c.642 0 1.162.529 1.162 1.18a1.17 1.17 0 0 1-1.162 1.178 1.17 1.17 0 0 1-1.162-1.178c0-.651.52-1.18 1.162-1.18zm5.34 2.867c-1.797-.052-3.746.512-5.28 1.786-1.72 1.428-2.687 3.72-1.78 6.22.942 2.453 3.666 4.229 6.884 4.229.826 0 1.622-.12 2.361-.336a.722.722 0 0 1 .598.082l1.584.926a.272.272 0 0 0 .14.045c.136 0 .245-.111.245-.247 0-.06-.023-.12-.04-.178l-.325-1.233a.49.49 0 0 1 .178-.554C23.028 18.48 24 16.82 24 14.98c0-3.21-2.931-5.952-7.062-6.122zm-2.18 2.769c.535 0 .969.44.969.982a.976.976 0 0 1-.969.983.976.976 0 0 1-.969-.983c0-.542.434-.982.97-.982zm4.844 0c.535 0 .969.44.969.982a.976.976 0 0 1-.969.983.976.976 0 0 1-.969-.983c0-.542.434-.982.97-.982z"/></svg>
              WeChat
              <span v-if="wechatCopied" class="copy-toast">已复制</span>
            </span>
            <a href="mailto:huang.yicheng@163.com" class="link">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="4" width="20" height="16" rx="2"/><path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7"/></svg>
              Email
            </a>
          </div>
        </div>
      </div>

      <div class="about-sidebar">
        <div class="thanks-card">
          <h3 class="section-title">特别鸣谢</h3>
          <div class="thanks-items">
            <span class="thanks-tag">codex</span>
            <span class="thanks-tag">deepseek</span>
          </div>
        </div>

        <div class="todo-card">
          <h3 class="section-title">待办事项</h3>
          <ul class="todo-list">
            <li class="todo-item undone">
              <span class="todo-check"></span>
              新闻agent
            </li>
            <li class="todo-item undone">
              <span class="todo-check"></span>
              读新闻功能前后端开发
            </li>
            <li class="todo-item undone">
              <span class="todo-check"></span>
              移动端适配（响应式布局）
            </li>
            <li class="todo-item undone">
              <span class="todo-check"></span>
              本页内容配置化
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue"
import { useSettingsStore } from "@/stores/settings"

const settings = useSettingsStore()

const qqCopied = ref(false)
const wechatCopied = ref(false)

async function copyQQ() {
  await navigator.clipboard.writeText("2785268186")
  qqCopied.value = true
  setTimeout(() => qqCopied.value = false, 1500)
}

async function copyWechat() {
  await navigator.clipboard.writeText("wxid_nfdb12hnlb4712")
  wechatCopied.value = true
  setTimeout(() => wechatCopied.value = false, 1500)
}
</script>

<style scoped>
.about {
  min-height: calc(100vh - 52px);
  padding: 2rem;
  box-sizing: border-box;
  display: flex;
  align-items: center;
  justify-content: center;
}

.about-layout {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: center;
  gap: 2rem;
  max-width: 960px;
  width: 100%;
}

.spacer {
}

.about-main {
  display: flex;
  justify-content: center;
}

.about-sidebar {
  width: 260px;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.about-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  max-width: 380px;
  text-align: center;
}

.avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #eee;
}

.name {
  font-size: 1.5rem;
  font-weight: 600;
  color: #333;
  margin: 0;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.bio {
  font-size: 0.95rem;
  color: #555;
  line-height: 1.8;
  margin: 0;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.links {
  display: flex;
  gap: 1rem;
  align-items: center;
  margin-top: 0.5rem;
}

.link {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: #555;
  text-decoration: none;
  font-size: 0.9rem;
  border-bottom: 1px dashed #ccc;
  transition: color 0.2s, border-color 0.2s;
}

.link:hover {
  color: v-bind('settings.primary');
  border-color: v-bind('settings.primary');
}

.copy-link {
  cursor: pointer;
  position: relative;
}

.copy-toast {
  position: absolute;
  top: -1.8rem;
  left: 50%;
  transform: translateX(-50%);
  background: #333;
  color: #fff;
  padding: 0.15rem 0.5rem;
  border-radius: 4px;
  font-size: 0.7rem;
  white-space: nowrap;
  pointer-events: none;
}

.todo-card, .thanks-card {
  position: relative;
  background: #fff;
  border-radius: 8px;
  padding: 1.2rem;
}

.todo-card::before, .thanks-card::before {
  content: "";
  position: absolute;
  inset: 0;
  border-radius: 8px;
  padding: 1px;
  background: linear-gradient(135deg, #e8e8e8 0%, transparent 30%);
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  pointer-events: none;
}

.section-title {
  font-size: 0.9rem;
  font-weight: 600;
  color: #333;
  margin: 0 0 1rem 0;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.todo-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 0.6rem;
}

.todo-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.85rem;
  color: #888;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.todo-check {
  width: 14px;
  height: 14px;
  border: 1.5px solid #ccc;
  border-radius: 3px;
  flex-shrink: 0;
}

.thanks-items {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.thanks-tag {
  display: inline-block;
  background: #eee;
  color: #555;
  padding: 0.25rem 0.65rem;
  border-radius: 4px;
  font-size: 0.8rem;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
}
</style>
