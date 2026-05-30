# Sample Blog 部署文档

## 服务器要求

| 资源 | 最低 | 推荐 |
|------|------|------|
| 操作系统 | Ubuntu 20.04+ | Ubuntu 22.04 |
| CPU | 1 核 | 1 核 |
| 内存 | 512 MB | 1 GB |
| 磁盘 | 5 GB | 20 GB |
| Java | JDK 17+ | JDK 21 |

---

## 一、部署文件

将以下 2 个文件上传到服务器（如 `/opt/sample-blog/`）：

```
/opt/sample-blog/
├── sample-blog-1.0.0.jar    # 后端+前端一体 JAR
└── application-prod.yml     # 生产环境配置文件
```

> JAR 位于项目 `backend/target/sample-blog-1.0.0.jar`
> 配置文件位于项目根目录 `application-prod.yml`

---

## 二、安装 Java

```bash
sudo apt update
sudo apt install openjdk-17-jre -y
java -version
```

---

## 三、编辑生产配置

```bash
vim /opt/sample-blog/application-prod.yml
```

需要修改的内容：

```yaml
server:
  port: 80                    # 监听端口

app:
  jwt:
    secret: 请替换为至少32字符的随机密钥   # 务必修改！
  upload:
    dir: /opt/sample-blog/uploads         # 上传文件存放目录
```

> 生成随机密钥：`openssl rand -base64 32`

---

## 四、启动服务

### 前台运行（测试用）

```bash
cd /opt/sample-blog
java -jar sample-blog-1.0.0.jar --spring.profiles.active=prod
```

访问 `http://你的服务器IP` 确认能打开。

### 后台运行（systemd 生产环境）

创建服务文件：

```bash
sudo vim /etc/systemd/system/sample-blog.service
```

```ini
[Unit]
Description=Sample Blog
After=network.target

[Service]
Type=simple
User=www-data
WorkingDirectory=/opt/sample-blog
ExecStart=/usr/bin/java -jar /opt/sample-blog/sample-blog-1.0.0.jar --spring.profiles.active=prod
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

启动服务：

```bash
sudo systemctl daemon-reload
sudo systemctl enable sample-blog
sudo systemctl start sample-blog
sudo systemctl status sample-blog
```

查看日志：

```bash
sudo journalctl -u sample-blog -f
```

---

## 五、开放端口

```bash
# 如果端口是 80
sudo ufw allow 80/tcp

# 如果端口是 8080
sudo ufw allow 8080/tcp

# 启用防火墙
sudo ufw enable
```

---

## 六、（可选）Nginx 反向代理

如果端口不是 80 或需要 HTTPS：

```bash
sudo apt install nginx -y
```

```bash
sudo vim /etc/nginx/sites-available/sample-blog
```

```nginx
server {
    listen 80;
    server_name your-domain.com;

    client_max_body_size 10M;

    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

```bash
sudo ln -s /etc/nginx/sites-available/sample-blog /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

---

## 七、首次登录

| 字段 | 值 |
|------|-----|
| 用户名 | `admin` |
| 密码 | `admin123` |

**登录后请立即修改密码！** 路径：右上角用户名 → 进入个人主页 → 编辑资料。

可以在启动日志中看到提醒：
```
WARN  ============================================
WARN    Default admin account created:
WARN    Username: admin   Password: admin123
WARN    Please change the password immediately!
WARN  ============================================
```

---

## 八、数据备份

所有数据存储在 JAR 同级目录的 `blog.db`（SQLite 数据库）：

```bash
# 备份
cp /opt/sample-blog/blog.db /backup/blog-$(date +%Y%m%d).db

# 定时备份 (crontab)
0 3 * * * cp /opt/sample-blog/blog.db /backup/blog-$(date +\%Y\%m\%d).db
```

上传的图片在 `uploads/` 目录下，也一并备份。

---

## 九、Swagger 文档

开发环境默认开启，访问 `http://服务器IP:端口/doc.html`。

生产环境默认关闭。如需临时开启：

```bash
vim /opt/sample-blog/application-prod.yml
```

```yaml
springdoc:
  swagger-ui:
    enabled: true
  api-docs:
    enabled: true

knife4j:
  enable: true
```

重启服务即可。

---

## 十、更新部署

```bash
# 1. 停止服务
sudo systemctl stop sample-blog

# 2. 替换 JAR
cp sample-blog-1.0.0.jar /opt/sample-blog/

# 3. 启动服务
sudo systemctl start sample-blog

# 4. 查看状态
sudo systemctl status sample-blog
```