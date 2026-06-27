# 安全与部署整理

## 当前部署路径

- 源码工作区：`/opt/blog/source`
- 后端运行目录：`/app/blog/backend`
- 前端运行目录：`/app/blog/frontend`
- 上传目录：`/app/blog/upload`
- 后端服务：`blog-backend.service`
- Nginx 站点：`/etc/nginx/sites-enabled/pxczxn.top`

## 已完成

- 后端已移除 Flyway，启动时不再执行数据库迁移。
- 后端只监听 `127.0.0.1:8080`，外部访问必须经过 Nginx。
- Nginx 重复启用的备份站点配置已清理。
- 前端发布后会自动收紧文件权限。
- 后端发布脚本会等待健康检查通过。

## 推荐的配置整理

后续建议把生产环境变量集中放到：

```text
/etc/blog/blog.env
```

示例：

```env
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
DB_URL=jdbc:mysql://localhost:3306/pxczxn-blog?useUnicode=true&characterEncoding=utf8&connectionCollation=utf8mb4_unicode_ci&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
DB_USERNAME=blog_user
DB_PASSWORD=change_me
JWT_SECRET=change_me_to_a_long_random_secret
JWT_EXPIRATION=86400000
JWT_DEVICE_LIMIT=3
CORS_ORIGINS=http://pxczxn.top,https://pxczxn.top
COOKIE_SECURE=true
FILE_UPLOAD_DIR=/app/blog/upload
INITIAL_ADMIN_USERNAME=admin
INITIAL_ADMIN_EMAIL=admin@example.com
INITIAL_ADMIN_PASSWORD=change_me_to_a_strong_password
```

权限：

```bash
chown root:root /etc/blog/blog.env
chmod 600 /etc/blog/blog.env
```

systemd drop-in 推荐：

```ini
[Service]
EnvironmentFile=/etc/blog/blog.env
ExecStart=
ExecStart=/usr/bin/java -Xms256m -Xmx1024m -Dserver.address=127.0.0.1 -jar /app/blog/backend/back-blog-0.0.1-SNAPSHOT.jar
```

## 发布流程

后端：

```bash
/opt/blog/scripts/deploy-backend.sh /opt/blog/releases/back-blog-0.0.1-SNAPSHOT.jar
```

前端：

```bash
/opt/blog/scripts/deploy-frontend.sh /opt/blog/releases/blog-public-dist
```

发布后：

```bash
/opt/blog/scripts/health-check.sh
```

## 下一步安全项

1. 轮换 JWT secret。
2. 修改管理员密码。
3. 修改数据库密码。
4. 启用 HTTPS 并将 HTTP 跳转到 HTTPS。
5. 将后端服务改为非 root 用户运行。
6. 定期执行 `/opt/blog/scripts/backup-live.sh` 并测试恢复。
