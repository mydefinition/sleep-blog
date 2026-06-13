import {createRouter, createWebHistory} from 'vue-router'
import {useAuthStore} from '@/stores/auth'

const router = createRouter({
    history: createWebHistory(),
    routes: [
        {path: '/', name: 'home', component: () => import('@/views/HomeView.vue')},
        {path: '/articles', name: 'articles', component: () => import('@/views/ArticleList.vue')},
        {path: '/articles/:id', name: 'article-detail', component: () => import('@/views/ArticleDetail.vue')},
        {path: '/profile', name: 'profile', component: () => import('@/views/ProfileView.vue')},
        {path: '/write', name: 'write', component: () => import('@/views/WriteView.vue')},
        {path: '/write/:id', name: 'write-edit', component: () => import('@/views/WriteView.vue')},
        {path: '/about', name: 'about', component: () => import('@/views/AboutView.vue')},
        {path: '/files', name: 'files', component: () => import('@/views/FileStorageView.vue')},
        {path: '/drafts', name: 'drafts', component: () => import('@/views/DraftsView.vue')},
    ]
})

let initialized = false

router.beforeEach(async (to, _from, next) => {
    const auth = useAuthStore()

    // 首次导航时尝试获取用户信息（session cookie 自动携带）
    if (!initialized) {
        initialized = true
        await auth.fetchProfile()
    }

    if (to.path === '/drafts' && !auth.isAdmin) {
        next('/')
        return
    }

    if (to.path.startsWith('/write') && !auth.isAdmin) {
        next('/')
        return
    }

    if (['/profile', '/profile/edit'].includes(to.path) && !auth.isLoggedIn) {
        next('/login')
        return
    }

    if (['/login', '/register'].includes(to.path) && auth.isLoggedIn) {
        next('/')
        return
    }

    next()
})

export default router