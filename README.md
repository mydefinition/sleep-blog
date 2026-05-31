# 📝 sample-blog

基于 Vue 3 + Spring Boot 3 的个人博客系统。

## 🧱 技术栈

| 层 | 技术 |
|---|---|
| 前端 | Vue 3 + TypeScript + Vite |
| 状态管理 | Pinia |
| 路由 | Vue Router 4 |
| Markdown | md-editor-v3 |
| HTTP | 原生 fetch |
| 后端 | Spring Boot 3 |
| ORM | MyBatis-Plus |
| 数据库 | SQLite |
| 认证 | JWT + BCrypt |

## 👥 用户角色

| 角色 | 权限 |
|---|---|
| 管理员 (ADMIN) | 发/编/删文章，上传文件/图片，删评论 |
| 登录用户 (USER) | 发评论，修改个人信息，下载文件 |
| 未登录 | 浏览文章和文件 |

## 📄 页面

| 页面 | 路由 | 权限 |
|---|---|---|
| 首页 | `/` | 公开 |
| 文章列表 | `/articles` | 公开 |
| 文章详情 | `/articles/:id` | 公开 |
| 文件存储 | `/files` | 公开 |
| 登录 | `/login` | 公开 |
| 注册 | `/register` | 公开 |
| 个人信息 | `/profile` | 登录 |
| 修改信息 | `/profile/edit` | 登录 |
| 发布文章 | `/write` | ADMIN |
| 编辑文章 | `/write/:id` | ADMIN |

## 🔌 API

### 认证

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/api/auth/register` | 注册 |
| POST | `/api/auth/login` | 登录，返回 JWT |

### 用户

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/user/profile` | 个人信息 |
| PUT | `/api/user/profile` | 修改用户名/密码 |

### 文章

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/articles` | 列表 `?tagId=&page=&size=` |
| GET | `/api/articles/:id` | 详情 |
| POST | `/api/articles` | 发布 |
| PUT | `/api/articles/:id` | 编辑 |
| DELETE | `/api/articles/:id` | 删除 |

### 标签

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/tags` | 列表 |
| POST | `/api/tags` | 创建 |

### 评论

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/articles/:id/comments` | 列表 |
| POST | `/api/articles/:id/comments` | 发表 |
| DELETE | `/api/comments/:id` | 删除（软删） |

### 图片

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/api/images/upload` | 上传（MultipartFile） |

### 文件存储

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/files` | 完整文件树 JSON |
| POST | `/api/files/upload` | 上传 `MultipartFile + localId` |
| POST | `/api/files/mkdir` | 新建文件夹 `{name, localId}` |
| DELETE | `/api/files/{id}` | 删除（递归删磁盘） |
| GET | `/api/files/{id}/download` | 下载 |

### 权限管理

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/super/users` | 用户列表 |
| GET | `/api/super/users/find?username=` | 按用户名查找 |
| PUT | `/api/super/users/promote` | 提升管理员 `{id}` |
| PUT | `/api/super/users/revoke` | 撤销管理员 `{id}` |
| PUT | `/api/super/users/reset-password` | 重置密码 `{id, password}` |

> 需 Header `super-token`，密钥配置 `app.super-secret`

## 📊 数据库

### user

| 字段 | 类型 | 说明 |
|---|---|---|
| id | INTEGER PK | 自增 |
| username | VARCHAR(50) UNIQUE | 用户名 |
| password | CHAR(60) | BCrypt |
| role | VARCHAR(10) | ADMIN / USER |
| created_at | DATETIME | |

### article

| 字段 | 类型 | 说明 |
|---|---|---|
| id | INTEGER PK | 自增 |
| title | VARCHAR(200) | |
| content | TEXT | Markdown |
| summary | VARCHAR(500) | |
| author_id | FK → user.id | |
| is_deleted | TINYINT | 软删除 |
| created_at | DATETIME | |
| updated_at | DATETIME | |

### tag

| 字段 | 类型 | 说明 |
|---|---|---|
| id | INTEGER PK | 自增 |
| name | VARCHAR(50) UNIQUE | |

### article_tag

| 字段 | 类型 |
|---|---|
| article_id | FK → article.id |
| tag_id | FK → tag.id |

### comment

| 字段 | 类型 | 说明 |
|---|---|---|
| id | INTEGER PK | 自增 |
| content | TEXT | |
| article_id | FK → article.id | |
| user_id | FK → user.id | |
| is_deleted | TINYINT | 软删除 |
| created_at | DATETIME | |

### file_storage

| 字段 | 类型 | 说明 |
|---|---|---|
| id | INTEGER PK | 自增，0=根目录 |
| name | TEXT | 文件/文件夹名 |
| is_dir | INTEGER | 0=文件 1=目录 |
| size | INTEGER | 文件大小（目录 NULL） |
| local_id | FK → file_storage.id | 父目录 |
| hash | TEXT | SHA-256（目录 NULL） |
| user_id | FK → user.id | 上传者 |
| upload_at | TEXT | |

## 🖼️ 图片流程

1. 编辑器选图 → `POST /api/images/upload`
2. 后端存 `uploads/images/`，返回 URL
3. 插入 `![](/uploads/images/xxx.png)`

## 🚀 启动

```bash
# 后端
cd backend && mvn spring-boot:run

# 前端
cd frontend && npm install && npm run dev
```

后端默认 `http://localhost:8080`，前端 `http://localhost:3000`。