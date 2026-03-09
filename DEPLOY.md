# Deploy Guide (Entry)

请优先阅读以下文档:

1. 快速部署: [`deploy/QUICKSTART.md`](./deploy/QUICKSTART.md)
2. 详细部署: [`deploy/README.md`](./deploy/README.md)
3. 上线前检查: [`deploy/PRODUCTION_CHECKLIST.md`](./deploy/PRODUCTION_CHECKLIST.md)

## 当前生产目录约定

- `/app/blog/backend`
- `/app/blog/frontend`
- `/app/blog/upload`

## 重要提醒

- 严禁提交明文密码、密钥文件。
- 部署时通过环境变量传入敏感信息。
