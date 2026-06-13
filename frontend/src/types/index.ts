// ============================================================
// 与后端 VO/DTO 对齐
// ============================================================

// ---- 分类 ----
export interface CategoryItem {
    id: number
    name: string
}

export interface CategoryDTO {
    level1: CategoryItem | null
    level2: CategoryItem | null
    level3: CategoryItem | null
}

// ---- 标签 ----
export interface Tag {
    id: number
    name: string
}

// ---- 文章 ----
/** 文章列表项 (ArticleQueryVO) */
export interface ArticleSummary {
    id: number
    title: string
    summary: string
    authorId: number
    authorName: string
    isPublished: number
    createdAt: string
    category: CategoryDTO
    tags: Tag[]
}

/** 文章详情 (ArticleDetailVO) */
export interface ArticleDetail {
    id: number
    title: string
    content: string
    summary: string
    authorId: number
    authorName: string
    isPublished: number
    createdAt: string
    updatedAt: string | null
    categoryLevel: CategoryDTO    // 注意：详情接口字段名是 categoryLevel
    tags: Tag[]
}

// ---- 分页 ----
export interface PageResult<T> {
    items: T[]     // 后端字段名是 items，不是 records
    total: number
    page: number
    size: number
}

// ---- 用户 ----
export interface User {
    id: number
    nickname: string   // 不是 username
    email: string
    role: string       // "USER" | "ADMIN" | "SUPER"
    createdAt: string
}

// ---- 首页 ----
export interface HomeVO {
    dailyTask: string
}

// ---- 云文件 ----
export interface CloudFile {
    id: number
    parentId: number
    ownerId: number
    userName: string
    fileName: string
    isDir: number
    createdAt: string
}

// ---- 评论（后端 CommentVO 目前为空实现，预留） ----
export interface Comment {
    id: number
    content: string
    articleId: number
    userId: number
    username: string
    createdAt: string
}

export interface CategoryVO {
    id: number
    name: string
    children: CategoryVO[]
}