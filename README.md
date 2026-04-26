# Pxczxn Blog

个人博客系统，包含公开博客端、管理后台和 Spring Boot API。

## 技术栈

- 后端: Java 21, Spring Boot, Spring Security, JPA, Flyway, MySQL 8
- 博客前台: React + Vite (`blog-public`)
- 管理后台: React + Vite (`blog-frontend`)
- 网关/静态服务: Nginx

## 项目结构

- `blog-backend`: 后端服务
- `blog-public`: 公开博客前端
- `blog-frontend`: 管理后台前端

## 本地开发

### 1) 后端

```bash
cd blog-backend
mvn clean spring-boot:run
```

默认端口: `4002`

### 2) 博客前台

```bash
cd blog-public
npm install
npm run dev
```

默认端口: `4000`

### 3) 管理后台

```bash
cd blog-frontend
npm install
npm run dev
```

默认端口: `4001`

## 生产部署约定

当前生产目录规范:

- 后端目录: `/app/blog/backend`
- 前端目录: `/app/blog/frontend`
- 上传目录: `/app/blog/upload`
- Nginx 站点配置: `/etc/nginx/sites-available/<your-domain>`

关键路由:

- 博客前台: `/`
- 管理后台: `/admin-pxczxn/`
- API: `/api/` -> `127.0.0.1:8080`
- 上传静态文件: `/uploads/` -> `/app/blog/upload/`

## SQL 迁移

- 仅保留并使用：`blog-backend/src/main/resources/db/migration/`
- 不再维护根目录 `sql/` 副本，避免重复与漂移

## 安全说明

- 不要在仓库中提交任何明文密码、私钥、服务器敏感地址。
- 推荐通过环境变量注入 `DB_PASSWORD`、`JWT_SECRET`、`FILE_UPLOAD_DIR`。
