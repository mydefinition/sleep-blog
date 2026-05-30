import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'home', component: () => import('@/views/HomeView.vue') },
    { path: '/articles', name: 'articles', component: () => import('@/views/ArticleList.vue') },
    { path: '/articles/:id', name: 'article-detail', component: () => import('@/views/ArticleDetail.vue') },
    { path: '/login', name: 'login', component: () => import('@/views/LoginView.vue') },
    { path: '/register', name: 'register', component: () => import('@/views/RegisterView.vue') },
    { path: '/profile', name: 'profile', component: () => import('@/views/ProfileView.vue') },
    { path: '/profile/edit', name: 'profile-edit', component: () => import('@/views/ProfileEdit.vue') },
    { path: '/write', name: 'write', component: () => import('@/views/WriteView.vue') },
    { path: '/write/:id', name: 'write-edit', component: () => import('@/views/WriteView.vue') },
  ]
})

export default router