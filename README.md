# sleep blog

基于 Vue 3 + Spring Boot 3 的个人博客系统。

## 技术栈

| 层 | 技术 |
|---|---|
| 前端 | Vue 3 + TypeScript + Vite |
| 状态管理 | Pinia |
| 路由 | Vue Router 4 |
| Markdown | md-editor-v3 |
| 后端 | Spring Boot 3 |
| ORM | MyBatis-Plus |
| 数据库 | SQLite |
| 认证 | JWT + BCrypt |

## 页面

| 页面 | 路由 | 权限 |
|---|---|---|
| 首页 | `/` | 公开 |
| 文章列表 | `/articles` | 公开 |
| 文章详情 | `/articles/:id` | 公开 |
| 文件存储 | `/files` | 公开 |
| 关于我 | `/about` | 公开 |
| 登录 | `/login` | 公开 |
| 注册 | `/register` | 公开 |
| 个人信息 | `/profile` | 登录 |
| 修改信息 | `/profile/edit` | 登录 |
| 发布文章 | `/write` | ADMIN |
| 编辑文章 | `/write/:id` | ADMIN |

## 用户角色

| 角色 | 权限 |
|---|---|
| 管理员 (ADMIN) | 发/编/删文章，上传文件/图片，删评论 |
| 登录用户 (USER) | 发评论，修改个人信息，下载文件 |
| 未登录 | 浏览文章和文件 |

## API

### 首页

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/home/daily` | 获取今日任务 |

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
| POST | `/api/images/upload` | 上传，返回 `/storage/xxx.png` |

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

> Header: `super-token`，密钥 `app.super-secret`

## 配置

开发环境配置文件位于 `backend/src/main/resources/application.yml`。

```yaml
app:
  storage:
    image-upload: ./storage/images     # 图片存储路径
    filestorage-root: ./storage/files  # 文件存储路径
  jwt:
    secret: <至少32字符的随机密钥>
    expiration: 86400000               # 24h
  super-secret: <管理员密钥>
```

## 每日任务

任务词条存储在 `tasks.json`（JSON 数组），外置优先：

- JAR 同级目录存在 `tasks.json` → 直接读取
- 不存在 → 首次启动从 JAR 内复制一份出来
- 修改后下次请求生效，无需重启

日界点为凌晨 4 点，同一天内返回相同结果。

## 构建与部署

### 本地开发

```bash
# 后端 (http://localhost:8080)
cd backend && mvn spring-boot:run

# 前端 (http://localhost:5173)
cd frontend && npm install && npm run dev
```

### 生产打包

```bash
# 1. 构建前端
cd frontend && npm run build

# 2. 复制前端产物到后端静态资源
rm -rf ../backend/src/main/resources/static/*
cp -r dist/* ../backend/src/main/resources/static/

# 3. 打包后端（fat JAR，内含前端）
cd ../backend && mvn clean package -DskipTests

# 4. 收集部署文件
mkdir -p ../release
cp ../backend/target/sample-blog-1.0.0.jar ../release/
cp ../application-prod.yml ../release/
cp ../run.sh ../release/
```

产物目录 `release/`：

```
release/
├── sample-blog-1.0.0.jar
├── application-prod.yml
└── run.sh
```

### 部署到服务器

将 `release/` 目录上传到服务器（如 `/opt/sample-blog/`）：

```
/opt/sample-blog/
├── sample-blog-1.0.0.jar
├── application-prod.yml
└── run.sh
```

编辑 `application-prod.yml`，修改密钥和路径：

```yaml
server:
  port: 80

app:
  storage:
    image-upload: /data/storage/images
    filestorage-root: /data/storage/files
  jwt:
    secret: <真随机密钥，openssl rand -base64 32>
  super-secret: <真随机密钥>
```

启动：

```bash
chmod +x run.sh
./run.sh start     # 启动
./run.sh stop      # 停止
./run.sh restart   # 重启
./run.sh status    # 查看状态
```

### 首次登录

| 用户名 | 密码 |
|---|---|
| admin | admin123 |

登录后请立即修改密码。

## 数据库

SQLite 单文件 `blog.db`，自动建表，数据存储在 JAR 同级目录。

| 表 | 说明 |
|---|---|
| user | 用户 |
| article | 文章 |
| tag | 标签 |
| article_tag | 文章-标签关联 |
| comment | 评论 |
| file_storage | 文件/目录树 |

## Knife4j 接口文档

生产环境默认关闭。如需启用，编辑 `application-prod.yml`：

```yaml
springdoc:
  swagger-ui:
    enabled: true
  api-docs:
    enabled: true
knife4j:
  enable: true
```

重启后访问 `http://服务器/doc.html`。
