# Deployment README

本目录提供可公开仓库使用的部署模板，默认不包含任何明文凭据。

## 约定目录

- 后端: `/app/blog/backend`
- 前端: `/app/blog/frontend`
- 上传目录: `/app/blog/upload`

## 文件说明

- `auto-deploy.sh`: Linux/macOS 自动部署模板（基于 SSH）
- `nginx.conf`: Nginx 站点模板
- `QUICKSTART.md`: 快速上手
- `PRODUCTION_CHECKLIST.md`: 上线检查项

## 后端关键环境变量

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`
- `CORS_ORIGINS`
- `FILE_UPLOAD_DIR` (建议: `/app/blog/upload`)

## Nginx 路由建议

- `/` -> 博客前台静态页
- `/admin-pxczxn/` -> 管理后台静态页
- `/api/` -> 反向代理到 `127.0.0.1:8080`
- `/uploads/` -> `alias /app/blog/upload/`

## 常见问题

1. 图片上传 500
- 检查后端 `file.upload-dir` 是否是 Linux 绝对路径
- 检查上传目录权限

2. 上传成功但图片不显示
- 检查 Nginx 是否配置 `/uploads/` 的 `alias`

3. API 502
- 检查 `blog-backend` 是否监听 `127.0.0.1:8080`
