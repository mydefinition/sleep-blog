export interface Article {
  id: number
  title: string
  content?: string
  summary: string
  authorId: number
  authorName: string
  tags: Tag[]
  createdAt: string
  updatedAt?: string
}

export interface PageResult<T> {
  records: T[]
  total: number
  page: number
  size: number
}

export interface Tag {
  id: number
  name: string
}

export interface Comment {
  id: number
  content: string
  articleId: number
  userId: number
  username: string
  createdAt: string
}

export interface User {
  id: number
  username: string
  role: string
  createdAt: string
}
export interface FileStorage {
  id: number
  fileName: string
  filePath: string
  size: number
  uploaderName: string
  createdAt: string
}