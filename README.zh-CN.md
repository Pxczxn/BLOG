# BLOG

这是一个个人博客与社区 MVP 项目，用来发布文章、整理资源入口，并提供基础后台管理链路。

## 功能概览

- 前台站点：首页、博客列表与详情、资源页、社区页、登录与注册。
- 后台管理：文章、分类、标签、评论/内容审核、仪表盘、上传和账号相关流程。
- 后端服务：JWT 鉴权、Flyway 数据库迁移、MySQL 持久化、上传处理和自动化测试。
- 部署友好：迁移 SQL 不再写入公开默认管理员账号，生产环境通过环境变量初始化管理员。

## 技术栈

- 前台：React 19、TypeScript、Vite、Tailwind CSS、React Router、Axios。
- 管理端：React 19、Vite、Ant Design、Tailwind CSS、React Router、Axios。
- 后端：Java 21、Spring Boot 4、Spring Security、Spring Data JPA、Flyway、MySQL、JJWT。

## 目录结构

```text
blog-backend/    Spring Boot 后端服务
blog-public/     前台站点与内置管理页面
blog-frontend/   可选的独立后台前端
```

## 本地运行

### 后端

```bash
cd blog-backend
copy .env.example .env
```

补全 `.env` 中的空值后运行：

```bash
mvnw spring-boot:run
```

### 前台

```bash
cd blog-public
npm install
npm run dev
```

默认端口是 `4000`。

### 独立后台

```bash
cd blog-frontend
npm install
npm run dev
```

## 生产部署

生产环境建议启用 `prod` profile，并配置：

```env
DB_URL=
DB_USERNAME=
DB_PASSWORD=
JWT_SECRET=
INITIAL_ADMIN_USERNAME=
INITIAL_ADMIN_EMAIL=
INITIAL_ADMIN_PASSWORD=
```

`INITIAL_ADMIN_PASSWORD` 至少 12 位。只有在 `prod` profile 启用，并且三个 `INITIAL_ADMIN_*` 变量都存在时，应用才会创建或更新初始管理员。

## 检查记录

发布副本已执行：

```bash
cd blog-backend
mvnw test
mvnw -DskipTests package
```

前端源码也做过语法解析检查。

## License

暂未声明开源协议。如后续接受外部贡献，建议先补充 License。
