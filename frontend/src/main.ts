import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import 'highlight.js/styles/github.css'
import './assets/main.css'
import { useSettingsStore } from './stores/settings'
import { config } from 'md-editor-v3'
import hljs from 'highlight.js'

// 禁止 md-editor-v3 从 unpkg CDN 加载，改用本地 npm 包
config({
  editorExtensions: {
    highlight: { instance: hljs },
  },
})

const app = createApp(App)
app.use(createPinia())
app.use(router)
const settings = useSettingsStore()
settings.apply()

app.mount('#app')
