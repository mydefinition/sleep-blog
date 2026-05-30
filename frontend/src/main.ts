import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import 'highlight.js/styles/github.css'
import './assets/markdown.css'
import { useSettingsStore } from './stores/settings'

const app = createApp(App)
app.use(createPinia())
app.use(router)
// set CSS vars before mount
const settings = useSettingsStore()
settings.apply()

app.mount('#app')