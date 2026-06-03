<template>
  <div class="min-h-[calc(100vh-52px)] p-8 box-border flex items-center justify-center">
    <div class="grid grid-cols-[1fr_auto_1fr] items-center gap-8 max-w-[960px] w-full">
      <div></div>
      <div class="flex justify-center">
        <div class="flex flex-col items-center gap-4 max-w-[380px] text-center">
          <img src="/avatar.png" alt="avatar" class="w-[120px] h-[120px] rounded-full object-cover border-[3px] border-gray-100" />
          <h2 class="text-2xl font-semibold text-gray-800 m-0 font-sans">I love JT</h2>
          <p class="text-[0.95rem] text-gray-600 leading-relaxed m-0 font-sans">
            存活状态：尚存，最近在学CSAPP ing……
          </p>
          <p class="text-[0.95rem] text-gray-600 leading-relaxed m-0 font-sans">
            亚洲区域赛银，全国邀请赛金，电子科技大学算法与逻辑实验室在读。AI时代我相信人定胜天。
          </p>
          <div class="flex gap-4 items-center mt-2">
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

      <div class="w-[260px] flex flex-col gap-6">
        <div class="card-gradient-border p-5">
          <h3 class="text-[0.9rem] font-semibold text-gray-800 m-0 mb-4 font-sans">特别鸣谢</h3>
          <div class="flex gap-2 flex-wrap">
            <span class="inline-block bg-gray-100 text-gray-600 px-2.5 py-1 rounded text-xs font-sans">codex</span>
            <span class="inline-block bg-gray-100 text-gray-600 px-2.5 py-1 rounded text-xs font-sans">deepseek</span>
          </div>
        </div>

        <div class="card-gradient-border p-5">
          <h3 class="text-[0.9rem] font-semibold text-gray-800 m-0 mb-4 font-sans">待办事项</h3>
          <ul class="list-none p-0 m-0 flex flex-col gap-2.5">
            <li class="flex items-center gap-2 text-[0.85rem] text-gray-400 font-sans">
              <span class="w-3.5 h-3.5 border-[1.5px] border-gray-300 rounded-[3px] shrink-0"></span>
              新闻agent
            </li>
            <li class="flex items-center gap-2 text-[0.85rem] text-gray-400 font-sans">
              <span class="w-3.5 h-3.5 border-[1.5px] border-gray-300 rounded-[3px] shrink-0"></span>
              读新闻功能前后端开发
            </li>
            <li class="flex items-center gap-2 text-[0.85rem] text-gray-400 font-sans">
              <span class="w-3.5 h-3.5 border-[1.5px] border-gray-300 rounded-[3px] shrink-0"></span>
              移动端适配（响应式布局）
            </li>
            <li class="flex items-center gap-2 text-[0.85rem] text-gray-400 font-sans">
              <span class="w-3.5 h-3.5 border-[1.5px] border-gray-300 rounded-[3px] shrink-0"></span>
              优化登录UI
            </li>
            <li class="flex items-center gap-2 text-[0.85rem] text-gray-400 font-sans">
              <span class="w-3.5 h-3.5 border-[1.5px] border-gray-300 rounded-[3px] shrink-0"></span>
              修复写文章中添加标签的bug
            </li>
            <li class="flex items-center gap-2 text-[0.85rem] text-gray-400 font-sans">
              <span class="w-3.5 h-3.5 border-[1.5px] border-gray-300 rounded-[3px] shrink-0"></span>
              搞证书配HTTPS
            </li>
            <li class="flex items-center gap-2 text-[0.85rem] text-gray-400 font-sans">
              <span class="w-3.5 h-3.5 border-[1.5px] border-gray-300 rounded-[3px] shrink-0"></span>
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
.link {
  @apply inline-flex items-center gap-1 no-underline text-[0.9rem] text-gray-600 border-b border-dashed border-gray-300 transition-[color,border-color] duration-200;
}
.link:hover {
  color: v-bind('settings.primary');
  border-color: v-bind('settings.primary');
}
.copy-link {
  @apply cursor-pointer relative;
}
.copy-toast {
  @apply absolute -top-7 left-1/2 -translate-x-1/2 bg-gray-800 text-white px-2 py-px rounded text-[0.7rem] whitespace-nowrap pointer-events-none;
}
</style>
