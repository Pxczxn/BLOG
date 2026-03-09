# Production Checklist

## 安全

- [ ] 仓库中无明文密码、私钥、服务器 root 口令
- [ ] `JWT_SECRET` 使用强随机值（>=32 chars）
- [ ] `DB_PASSWORD` 使用强随机值
- [ ] 关闭不必要端口，仅开放 22/80/443

## 后端

- [ ] `spring.profiles.active=prod`
- [ ] `file.upload-dir` 为 Linux 绝对路径（推荐 `/app/blog/upload`）
- [ ] `spring.servlet.multipart.*` 限制已设置
- [ ] Flyway migration 正常执行

## Nginx

- [ ] `/api/` 代理到 `127.0.0.1:8080`
- [ ] `/uploads/` 使用 `alias /app/blog/upload/`
- [ ] `nginx -t` 通过

## 验证

- [ ] 博客首页加载正常
- [ ] 管理后台可登录
- [ ] 分类/标签接口 200
- [ ] 封面上传成功并可访问图片 URL

## 运维

- [ ] 配置日志轮转
- [ ] 配置数据库和上传目录备份
- [ ] 配置 HTTPS 证书自动续期
