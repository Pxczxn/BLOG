# Quick Start

## 1. 构建

```bash
cd blog-backend && mvn clean package -DskipTests
cd ../blog-public && npm install && npm run build
cd ../blog-frontend && npm install && npm run build
```

## 2. 配置环境变量

```bash
export SERVER_HOST="your-server-ip"
export SERVER_USER="root"
export DOMAIN="your-domain.com"
export SSH_KEY_PATH="~/.ssh/id_rsa"
export DB_PASSWORD="<strong-password>"
export JWT_SECRET="<strong-jwt-secret>"
```

## 3. 执行自动部署

```bash
bash deploy/auto-deploy.sh
```

## 4. 验证

- `https://<your-domain>/api/public/categories` 应返回 200
- `https://<your-domain>/admin-pxczxn/` 可访问管理端
- 上传图片后，`/uploads/...` 可正常访问
