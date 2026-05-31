# 📝 sample-blog


$$
\cos{\theta}=\frac{qx}{|q||x|}=\frac{\sum_{i=1}^nq_ix_i}{\sqrt{\sum_{i=1}^nq_i^2}\sqrt{\sum_{i=1}^nx_i^2}}
$$




基于 Vue 3 + Spring Boot 的个人博客系统。

## 🧱 技术栈

| 层 | 技术 |
|---|---|
| 前端框架 | Vue 3 + Composition API + TypeScript |
| 构建工具 | Vite |
| 状态管理 | Pinia |
| 路由 | Vue Router 4 |
| Markdown 编辑器 | @kangc/v-md-editor |
| Markdown 渲染 | markdown-it + highlight.js |
| HTTP | 原生 fetch |
| 后端框架 | Spring Boot 3 |
| ORM | MyBatis-Plus |
| 数据库 | SQLite |
| 构建 | Maven |
| 认证 | JWT + BCrypt |

## 👥 用户角色

| 角色 | 权限 |
|---|---|
| 管理员 (ADMIN) | 发/编/删文章，发/删评论，上传图片 |
| 登录用户 (USER) | 发评论，查看/修改个人信息 |
| 未登录用户 | 浏览所有内容 |

## 📄 页面

| 页面 | 路由 | 权限 |
|---|---|---|
| 首页 | `/` | 公开 |
| 文章列表 | `/articles` | 公开 |
| 文章详情 | `/articles/:id` | 公开 |
| 登录 | `/login` | 公开 |
| 注册 | `/register` | 公开 |
| 个人信息 | `/profile` | 登录 |
| 修改信息 | `/profile/edit` | 登录 |
| 发布文章 | `/write` | ADMIN |
| 编辑文章 | `/write/:id` | ADMIN |

## 🔌 API

### 文章

| 方法 | 路径 | 说明 | 权限 |
|---|---|---|---|
| GET | `/api/articles` | 文章列表（`?tagId=&page=&size=`） | 公开 |
| GET | `/api/articles/:id` | 文章详情 | 公开 |
| POST | `/api/articles` | 发布文章 | ADMIN |
| PUT | `/api/articles/:id` | 编辑文章 | ADMIN |
| DELETE | `/api/articles/:id` | 删除文章 | ADMIN |

### 标签

| 方法 | 路径 | 说明 | 权限 |
|---|---|---|---|
| GET | `/api/tags` | 获取所有标签 | 公开 |
| POST | `/api/tags` | 创建标签 | ADMIN |

### 评论

| 方法 | 路径 | 说明 | 权限 |
|---|---|---|---|
| GET | `/api/articles/:id/comments` | 评论列表（排除已删除） | 公开 |
| POST | `/api/articles/:id/comments` | 发评论 | 登录 |
| DELETE | `/api/comments/:id` | 删评论（软删除） | ADMIN |

### 认证

| 方法 | 路径 | 说明 | 权限 |
|---|---|---|---|
| POST | `/api/auth/register` | 注册 | 公开 |
| POST | `/api/auth/login` | 登录，返回 JWT | 公开 |

### 用户

| 方法 | 路径 | 说明 | 权限 |
|---|---|---|---|
| GET | `/api/user/profile` | 获取个人信息 | 登录 |
| PUT | `/api/user/profile` | 修改用户名/密码 | 登录 |

### 图片

| 方法 | 路径 | 说明 | 权限 |
|---|---|---|---|
| POST | `/api/images/upload` | 上传图片到本地 | ADMIN |


### 管理员管理（需Header `super-token`）

| 方法 | 路径 | 说明 | 权限 |
|---|---|---|---|
| GET | `/api/super/users` | 列出所有用户 | Header `super-token` |
| GET | `/api/super/users/find?username=xxx` | 按用户名查用户 | Header `super-token` |
| PUT | `/api/super/users/promote` | 提升为 ADMIN | Header `super-token` |
| PUT | `/api/super/users/revoke` | 撤销为 USER | Header `super-token` |
| PUT | `/api/super/users/reset-password` | 重置密码 | Header `super-token` |

> 请求需带 Header `super-token`，密钥在 `application.yml` 中配置 `app.super-secret`，生产环境请务必修改。


### 文件存储

| 方法 | 路径 | 说明 | 权限 |
|---|---|---|---|
| GET | `/api/files` | 文件列表（JSON） | 公开 |
| POST | `/api/files` | 上传文件（form: file + path） | ADMIN |
| DELETE | `/api/files/{id}` | 删除文件（软删 DB + 真删磁盘） | ADMIN |
| GET | `/api/files/{id}/download` | 下载文件 | 公开 |

## 📊 数据库设计

### user
### file_storage

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| id | BIGINT | PK 自增 | |
| file_name | VARCHAR(255) | NOT NULL | 原始文件名 |
| file_path | VARCHAR(500) | NOT NULL DEFAULT '/' | 目录路径 |
| size | BIGINT | NOT NULL | 字节数 |
| uploaded_by | BIGINT | FK → user.id | 上传者 |
| is_deleted | TINYINT | NOT NULL DEFAULT 0 | 软删除标记 |
| created_at | DATETIME | NOT NULL | |

### user

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| id | BIGINT | PK 自增 | |
| username | VARCHAR(50) | UNIQUE NOT NULL | 昵称 |
| password | CHAR(60) | NOT NULL | BCrypt 加密 |
| role | VARCHAR(10) | NOT NULL DEFAULT USER | ADMIN / USER |
| created_at | DATETIME | NOT NULL | |

### article

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| id | BIGINT | PK 自增 | |
| title | VARCHAR(200) | NOT NULL | |
| content | TEXT | NOT NULL | Markdown 原文 |
| summary | VARCHAR(500) | | |
| author_id | BIGINT | FK → user.id | |
| is_deleted | TINYINT | NOT NULL DEFAULT 0 | 软删除标识 |
| created_at | DATETIME | NOT NULL | 发表日期 |
| updated_at | DATETIME | | 修改日期 |

### tag

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| id | BIGINT | PK 自增 | |
| name | VARCHAR(50) | NOT NULL | |


### article_tag

| 字段 | 类型 | 约束 |
|---|---|---|
| article_id | BIGINT | FK → article.id |
| tag_id | BIGINT | FK → tag.id |

### comment

| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| id | BIGINT | PK 自增 | |
| content | TEXT | NOT NULL | |
| article_id | BIGINT | FK → article.id | |
| user_id | BIGINT | FK → user.id | |
| is_deleted | TINYINT | NOT NULL DEFAULT 0 | 软删除标识 |
| created_at | DATETIME | NOT NULL | |

## 🖼️ Markdown 图片流程

1. 编辑器点击插入图片 → 选本地文件
2. 前端 POST `/api/images/upload` 上传
3. 后端存到 `backend/uploads/`，返回 URL
4. 编辑器插入 `![](/uploads/xxx.png)`
5. 前端 markdown-it 渲染展示

