<template>
  <div class="max-w-[520px] mx-auto my-12 p-8">
    <h2 class="mb-6 font-semibold text-[1.2rem]">
      个人信息 <span class="text-[1rem] font-normal" :style="{ color: settings.primary }">PROFILE</span>
    </h2>

    <div class="border border-gray-100 rounded-lg overflow-hidden">
      <div v-for="row in rows" :key="row.label"
           class="flex items-center border-b border-gray-50 last:border-b-0">
        <div class="w-[100px] shrink-0 py-3 px-4 text-right text-[0.9rem] text-gray-400">
          {{ row.label }}
        </div>
        <div class="flex-1 py-3 px-4 text-[0.95rem] text-gray-800 flex items-center gap-2">
          <template v-if="row.key === 'nickname'">
            <input
                v-if="editing"
                ref="nicknameInput"
                v-model="nickname"
                @keyup.enter="saveNickname"
                @keyup.escape="cancelEdit"
                class="flex-1 px-2 py-1 border-0 border-b border-primary text-[0.95rem] outline-none"
            />
            <span v-else>{{ auth.user?.nickname }}</span>
            <button v-if="!editing" @click="startEdit"
                    class="bg-transparent border-none text-gray-300 hover:text-primary cursor-pointer p-0.5 transition-colors">
              <Pencil :size="14" />
            </button>
            <span v-else class="flex gap-1.5">
              <button @mousedown.prevent="saveNickname"
                      class="px-2.5 py-0.5 bg-primary text-white border-none rounded text-xs cursor-pointer hover:opacity-85 transition-opacity">
                保存
              </button>
              <button @mousedown.prevent="cancelEdit"
                      class="px-2.5 py-0.5 bg-gray-100 text-gray-500 border-none rounded text-xs cursor-pointer hover:bg-gray-200 transition-colors">
                取消
              </button>
            </span>
          </template>
          <template v-else>{{ row.value }}</template>
        </div>
      </div>
    </div>

    <p v-if="error" class="text-[#ff5252] text-[0.85rem] mt-3 m-0">{{ error }}</p>
    <p v-if="success" class="text-[#2e7d32] text-[0.85rem] mt-3 m-0">修改成功</p>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, nextTick } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useSettingsStore } from '@/stores/settings'
import { api } from '@/api'
import { Pencil } from 'lucide-vue-next'

const auth = useAuthStore()
const settings = useSettingsStore()

const editing = ref(false)
const nickname = ref('')
const error = ref('')
const success = ref(false)
const nicknameInput = ref<HTMLInputElement | null>(null)

function translateRole(role: string) {
  if (role === 'SUPER') return '超级管理员'
  if (role === 'ADMIN') return '管理员'
  return '普通用户'
}

function formatDate(d: string) {
  return new Date(d).toLocaleDateString('zh-CN')
}

const rows = computed(() => {
  const u = auth.user
  if (!u) return []
  return [
    { key: 'nickname', label: '昵称', value: u.nickname },
    { key: 'email', label: '邮箱', value: u.email },
    { key: 'role', label: '角色', value: translateRole(u.role) },
    { key: 'createdAt', label: '注册时间', value: formatDate(u.createdAt) },
  ]
})

async function startEdit() {
  nickname.value = auth.user?.nickname || ''
  editing.value = true
  error.value = ''
  success.value = false
  await nextTick()
  nicknameInput.value?.focus()
}

function cancelEdit() {
  editing.value = false
  nickname.value = ''
  error.value = ''
}

async function saveNickname() {
  if (!nickname.value.trim() || nickname.value.trim() === auth.user?.nickname) {
    editing.value = false
    return
  }
  error.value = ''
  success.value = false
  try {
    await api.put('/api/user/profile', {
      id: auth.user?.id,
      nickname: nickname.value.trim(),
    })
    success.value = true
    await auth.fetchProfile()
    editing.value = false
    setTimeout(() => { success.value = false }, 2000)
  } catch (e: any) {
    error.value = e.message
  }
}
</script>