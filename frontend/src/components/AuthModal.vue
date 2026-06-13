<template>
  <Teleport to="body">
    <div v-if="visible" class="fixed inset-0 z-[500] flex items-center justify-center">
      <div class="absolute inset-0 bg-black/30" @click="close"></div>

      <div class="relative bg-white rounded-lg shadow-2xl w-[400px] max-h-[90vh] overflow-y-auto p-8">
        <button @click="close"
                class="absolute top-3 right-4 text-gray-400 hover:text-gray-600 text-xl leading-none bg-transparent border-none cursor-pointer">&times;</button>

        <div class="flex mb-6 border-b border-gray-100">
          <button @click="tab = 'login'"
                  class="flex-1 pb-2.5 text-center text-[0.95rem] font-semibold border-b-2 transition-colors duration-150 bg-transparent cursor-pointer"
                  :class="tab === 'login' ? 'text-primary border-primary' : 'text-gray-400 border-transparent hover:text-gray-600'">
            登录
          </button>
          <button @click="tab = 'register'"
                  class="flex-1 pb-2.5 text-center text-[0.95rem] font-semibold border-b-2 transition-colors duration-150 bg-transparent cursor-pointer"
                  :class="tab === 'register' ? 'text-primary border-primary' : 'text-gray-400 border-transparent hover:text-gray-600'">
            注册
          </button>
        </div>

        <!-- 登录表单 -->
        <form v-if="tab === 'login'" @submit.prevent="handleLogin" class="flex flex-col gap-3">
          <input v-model="loginForm.idOrEmail" type="text" placeholder="用户ID或邮箱" required
                 class="px-2.5 py-2 border-0 border-b border-gray-200 text-base outline-none focus:border-primary transition-colors" />
          <input v-model="loginForm.password" type="password" placeholder="密码" required
                 class="px-2.5 py-2 border-0 border-b border-gray-200 text-base outline-none focus:border-primary transition-colors" />
          <p v-if="loginError" class="text-[#ff5252] text-[0.85rem] m-0">{{ loginError }}</p>
          <button type="submit"
                  class="mt-1 py-2.5 bg-primary text-white border-none rounded-md text-base cursor-pointer hover:opacity-85 transition-opacity duration-150">
            登录
          </button>
        </form>

        <!-- 注册表单 -->
        <form v-if="tab === 'register'" @submit.prevent="handleRegister" class="flex flex-col gap-2.5">
          <input v-model="regForm.nickname" type="text" placeholder="昵称" required
                 class="px-2.5 py-2 border-0 border-b text-base outline-none transition-colors"
                 :class="regForm.nickname && !nicknameValid ? 'border-[#ff5252]' : 'border-gray-200 focus:border-primary'" />
          <p v-if="regForm.nickname && nicknameError" class="text-[#ff5252] text-[0.8rem] m-0 -mt-1">{{ nicknameError }}</p>

          <input v-model="regForm.email" type="email" placeholder="邮箱" required
                 class="px-2.5 py-2 border-0 border-b text-base outline-none transition-colors"
                 :class="regForm.email && !emailValid ? 'border-[#ff5252]' : 'border-gray-200 focus:border-primary'" />
          <p v-if="regForm.email && !emailValid" class="text-[#ff5252] text-[0.8rem] m-0 -mt-1">邮箱格式不正确</p>

          <div class="flex gap-2">
            <input v-model="regForm.code" type="text" placeholder="验证码" required maxlength="6"
                   class="flex-1 px-2.5 py-2 border-0 border-b border-gray-200 text-base outline-none focus:border-primary transition-colors" />
            <button type="button" @click="sendCode"
                    :disabled="!emailValid || !regForm.email.trim() || sending || countdown > 0"
                    class="shrink-0 px-4 py-2 border-none rounded-md text-sm cursor-pointer transition-all duration-150 whitespace-nowrap min-w-[90px] flex items-center justify-center"
                    :class="countdown > 0
                      ? 'bg-white text-primary border border-gray-200'
                      : sending
                        ? 'bg-primary text-white hover:opacity-85'
                        : 'bg-primary text-white hover:opacity-85 disabled:opacity-40 disabled:cursor-not-allowed'">
              <span v-if="sending" class="inline-block w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></span>
              <span v-else>{{ countdown > 0 ? `${countdown}s 后重发` : '获取验证码' }}</span>
            </button>
          </div>

          <input v-model="regForm.password" type="password" placeholder="密码（至少6位）" required minlength="6"
                 class="px-2.5 py-2 border-0 border-b border-gray-200 text-base outline-none focus:border-primary transition-colors" />

          <input v-model="repeatPassword" type="password" placeholder="重复密码" required
                 class="px-2.5 py-2 border-0 border-b text-base outline-none transition-colors"
                 :class="repeatPassword && !passwordMatch ? 'border-[#ff5252]' : 'border-gray-200 focus:border-primary'" />
          <p v-if="repeatPassword && !passwordMatch" class="text-[#ff5252] text-[0.8rem] m-0 -mt-1">两次密码不一致</p>

          <p v-if="regError" class="text-[#ff5252] text-[0.85rem] m-0">{{ regError }}</p>
          <p v-if="regSuccess" class="text-[#2e7d32] text-[0.85rem] m-0">注册成功</p>

          <button type="submit"
                  class="mt-1 py-2.5 bg-primary text-white border-none rounded-md text-base cursor-pointer hover:opacity-85 transition-opacity duration-150">
            注册
          </button>
        </form>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { api } from '@/api'

const auth = useAuthStore()
const visible = ref(false)
const tab = ref<'login' | 'register'>('login')

// ---- 登录 ----
const loginForm = reactive({ idOrEmail: '', password: '' })
const loginError = ref('')

async function handleLogin() {
  loginError.value = ''
  try {
    await api.post('/api/user/login', { ...loginForm })
    await auth.fetchProfile()
    close()
  } catch (e: any) {
    loginError.value = e.message
  }
}

// ---- 注册 ----
const regForm = reactive({ nickname: '', email: '', code: '', password: '' })
const repeatPassword = ref('')
const regError = ref('')
const regSuccess = ref(false)
const sending = ref(false)
const countdown = ref(0)
let countdownTimer: ReturnType<typeof setInterval> | null = null

const nicknameValid = computed(() => {
  if (!regForm.nickname) return true
  if (regForm.nickname.length < 2 || regForm.nickname.length > 20) return false
  return /^[\u4e00-\u9fa5a-zA-Z0-9_]+$/.test(regForm.nickname)
})

const nicknameError = computed(() => {
  if (!regForm.nickname) return ''
  if (regForm.nickname.length < 2) return '昵称至少2个字符'
  if (regForm.nickname.length > 20) return '昵称最多20个字符'
  if (!/^[\u4e00-\u9fa5a-zA-Z0-9_]+$/.test(regForm.nickname)) return '只能包含中文、字母、数字和下划线'
  return ''
})

const emailValid = computed(() => {
  if (!regForm.email) return true
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(regForm.email)
})

const passwordMatch = computed(() => {
  if (!repeatPassword.value) return true
  return regForm.password === repeatPassword.value
})

async function sendCode() {
  regError.value = ''
  if (!regForm.email.trim()) {
    regError.value = '请先输入邮箱'
    return
  }
  if (!emailValid.value) {
    regError.value = '邮箱格式不正确'
    return
  }
  sending.value = true
  try {
    await api.post('/api/user/verify', { email: regForm.email })
    countdown.value = 30
    countdownTimer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0 && countdownTimer) {
        clearInterval(countdownTimer)
        countdownTimer = null
      }
    }, 1000)
  } catch (e: any) {
    regError.value = e.message
  } finally {
    sending.value = false
  }
}

async function handleRegister() {
  regError.value = ''
  if (!nicknameValid.value) {
    regError.value = nicknameError.value
    return
  }
  if (!passwordMatch.value) {
    regError.value = '两次密码不一致'
    return
  }
  try {
    await api.post('/api/user/register', {
      nickname: regForm.nickname,
      email: regForm.email,
      password: regForm.password,
      verifyCode: regForm.code,
    })
    regSuccess.value = true
    await auth.fetchProfile()
    setTimeout(() => close(), 800)
  } catch (e: any) {
    regError.value = e.message
  }
}

// ---- 开关 ----
function open(initialTab?: 'login' | 'register') {
  if (initialTab) tab.value = initialTab
  loginForm.idOrEmail = ''; loginForm.password = ''; loginError.value = ''
  regForm.nickname = ''; regForm.email = ''; regForm.code = ''; regForm.password = ''
  repeatPassword.value = ''
  regError.value = ''; regSuccess.value = false
  visible.value = true
}

function close() {
  visible.value = false
}

defineExpose({ open, close })
</script>