# Sleep Blog

## 简介

基于 ***Vue 3*** + ***Spring Boot*** 的个人博客系统。拥有写读文章、***Agent*** 自动新闻发布、文件存储等功能。

技术栈如下：

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

## 功能介绍

// todo



## 构建

> 省流：
>
> 至少需配备 **JDK17**，**Maven3.x**，**Node.js18** 的环境。
>
> **Windows** 在根目录下执行
>
> ```powershell
> Set-ExecutionPolicy RemoteSigned -Scope Process
> ./build.ps1
> ```
>
> **Linux** 在根目录下执行
>
> ```shell
> ./build.sh
> ```

### 环境要求

构建时请确保以下环境配置

| 工具        | 版本要求         | 说明                                |
| ----------- | ---------------- | ----------------------------------- |
| **JDK**     | $\geq17$         | **Java** 开发环境+运行环境          |
| **Maven**   | $3.\times$       | **Java** 依赖管理，打包 **fat JAR** |
| **Node.js** | $\geq18$         | 运行 **Vite**，编译打包前端资源     |
| **npm**     | 随 **Node** 自带 | 前端依赖管理                        |

也即至少需配备 **JDK17**，**Maven3.x**，**Node.js18** 的环境。

### 自动构建

#### Windows 自动构建

在 ***Windows*** 下，在项目根目录处运行

```powershell
./build.ps1 [frontend|backend|all]
```

即可，其中可选项 `fronend`，`backend`，`all` 分别表示 仅构建前端，仅构建后端并打包，构建前后端并打包，默认情况下为 `all`。

若出现类似

```powershell
无法加载文件 xxx\build.ps1，因为在此系统上禁止运行脚本。有关详细信息，请参阅 https:/go.microsoft.com/fwlink/?LinkID=135170 中的 about_Execution_Policies。
```

报错，是由于 ***PowerShell*** 默认禁止运行脚本文件，请先输入

```powershell
Set-ExecutionPolicy RemoteSigned -Scope Process
```

再输入 ` ./build.ps1` 即可。

#### Linux 自动构建

在 ***Linux*** 下，在项目根目录处运行

```powershell
./build.sh [frontend|backend|all]
```

即可，各可选项释义见上。

#### 自动构建流程与结果

自动构建过程包括并会输出显示以下四个步骤：

```powershell
=== Building frontend ===
```

> 构建前端，产物 `frontend/dist/`.

```powershell
=== Copying frontend dist to backend static ===
```

> 将前端构建产物 `frontend/dist/` 复制到 `backend/src/main/resources/static/`.

```powershell
=== Building backend ===
```

> 构建后端并打包，产物 `backend/target/sample-blog-x.x.x.jar`.

```powershell
=== Packaging release ===
```

> 将产物 `backend/target/sample-blog-x.x.x.jar`、`application-prod.yml`、`run.sh` 复制进入 `release/` 下。

其中 `./build.ps1 frontend` 或 `./build.sh frontend` 包括前二步骤；

`./build.ps1 backend` 或 `./build.sh backend` 包括后二步骤；

`./build.ps1` 或 `./build.sh`  或 `./build.ps1 all` 或 `./build.sh all` 包括全四步骤。

可见最终会在根目录下 `release/` 内：

```
release/
├── sample-blog-1.0.0.jar
├── application-prod.yml
└── run.sh
```

### 手动构建

手动构建的流程与自动构建流程一致。

#### Windows 手动构建

在根目录下执行以下命令即可：

```powershell
# === Building frontend ===
cd frontend
npm install
npx vite build
cd ..

# === Copying frontend dist to backend static ===
Remove-Item backend\src\main\resources\static\* -Recurse -Force
Copy-Item frontend\dist\* -Destination backend\src\main\resources\static\ -Recurse -Force

# === Building backend ===
cd backend
mvn clean package -DskipTests
cd ..

# === Packaging release ===
New-Item -ItemType Directory -Path release -Force
Copy-Item backend\target\sample-blog-1.0.0.jar -Destination release\
Copy-Item application-prod.yml -Destination release\
Copy-Item run.sh -Destination release\
```

#### Linux 手动构建

在根目录下执行以下命令即可：

```shell
# === Building frontend ===
cd frontend
npm install
npx vite build
cd ..

# === Copying frontend dist to backend static ===
rm -rf backend/src/main/resources/static/*
cp -r frontend/dist/* backend/src/main/resources/static/

# === Building backend ===
cd backend
mvn clean package -DskipTests
cd ..

# === Packaging release ===
mkdir -p release
cp backend/target/sample-blog-1.0.0.jar release/
cp application-prod.yml release/
cp run.sh release/
```

## 配置



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

## 配置

> 后端完整配置文件位于 `backend/src/main/resources/application.yml`。

构建完毕后，在 `release/` 下会产生 `application-prod.yml`，内容如下：

```yaml
server:
  port: 80

springdoc:
  swagger-ui:
    enabled: false
  api-docs:
    enabled: false

knife4j:
  enable: false

mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.nologging.NoLoggingImpl

app:
  storage:
    image-upload: /data/storage/images
    filestorage-root: /data/storage/files
  super-secret: 请替换为强随机密钥
```

### 监听端口

**Spring Boot** 监听端口（覆盖默认值 $8080$）：

```yaml
server:
  port: 80
```

### 在线文档

项目基于 `swagger` 与 `knife4j` 框架构建在线接口文档与调试页面，默认情况下关闭：

```yaml
springdoc:
  swagger-ui:
    enabled: false
  api-docs:
    enabled: false

knife4j:
  enable: false
```

若需打开可设置：

```yaml
springdoc:
  swagger-ui:
    enabled: true
  api-docs:
    enabled: true

knife4j:
  enable: true
```

### MyBatis SQL 日志

默认关闭 **MyBatis SQL** 日志，避免生产环境刷屏：

```yaml
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.nologging.NoLoggingImpl
```

若需输出日志，推荐走 `SLF4J` 配置：

```yaml
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.slf4j.Slf4jImpl
```

### 存储路径

博客需存储文章 **Markdown** 中的图片，以及云文件系统的文件，从根目录起配置路径：

```yaml
app:
  storage:
    image-upload: /data/storage/images
    filestorage-root: /data/storage/files
```

`image-upload` 为云文件系统中的文件存储路径，`filestorage-root` 为 上传至 **Markdown** 中图片的存储路径。

### 高级（超级）权限

为方便调试，博客携带一组 **super API**，请求时需在 `Header` 中携带 `super-token`，其内容为配置文件中的 `super-secret`：

```yaml
app:
  super-secret: 请替换为强随机密钥
```

> 注意区分`super-secret` 与 `super-token`，该历史遗留问题尚未解决。

## 部署

### 部署在 Linux 服务器上

将完整构建的产物（`release/` 下）上传至 **Linux** 服务器内（如 `/opt/sleep-blog/`），为脚本文件 `run.sh` 添加执行权限：

```shell
chmod +x run.sh
```

`run.sh` 支持四类指令：

```bash
./run.sh start     # 启动
./run.sh stop      # 停止
./run.sh restart   # 重启
./run.sh status    # 查看状态
```

### 首次启动后产生的文件

#### blog.db

**SQLite** 的 `blog.db`，详细见后文 **数据库** 一章，注意建库会自动产生具有 ***ADMIN*** 权限的初始用户。

#### task.json

首页每日任务轮换词条，格式为一 ***JSON*** 数组。

## 数据库

本项目数据库为 **SQLite**，运行时将使用 **JAR** 同级目录下单文件 `blog.db`，自动建表。数据库表如下，具体见 `\backend\src\main\resources\schema.sql`。

| 表 | 说明 |
|---|---|
| user | 用户 |
| article | 文章 |
| tag | 标签 |
| article_tag | 文章-标签关联 |
| comment | 评论 |
| file_storage | 文件/目录树 |

若无 `blog.db` 将自动建库并产生具有 ***ADMIN*** 权限的初始用户，其用户名为 `admin`，密码为 `admin123`，届时我们忠诚的 **Spring Boot** 也会通过 ***Warning*** 的形式提示您。

## 接口文档

见 ***配置与部署-配置-在线文档*** 一节，博客采用 **Knife4j** 接口文档，启用情况下访问 `博客根链接/doc.html` 即可。
