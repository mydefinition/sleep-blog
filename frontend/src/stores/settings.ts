import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

export const useSettingsStore = defineStore('settings', () => {
  const primary = ref(localStorage.getItem('theme-primary') || '#42b883')
  const previewTheme = ref(localStorage.getItem('theme-preview') || 'github')
  const codeTheme = ref(localStorage.getItem('theme-code') || 'github')

  watch(primary, v => { localStorage.setItem('theme-primary', v); apply() })
  watch(previewTheme, v => localStorage.setItem('theme-preview', v))
  watch(codeTheme, v => localStorage.setItem('theme-code', v))

  function apply() {
    document.documentElement.style.setProperty('--primary', primary.value)
  }

  return { primary, previewTheme, codeTheme, apply }
})