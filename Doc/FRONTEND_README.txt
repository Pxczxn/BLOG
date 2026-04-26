前端项目说明
================

一、前端整体结构
----------------

本项目包含两个前端应用：

1. blog-frontend
   管理后台前端，面向管理员和运营人员使用。
   主要负责文章管理、分类管理、标签管理、评论审核、社区帖子管理、审核任务、举报处理和社区互动概览。

2. blog-public
   公开站和社区前台，面向普通访问用户和社区用户使用。
   主要负责博客文章展示、文章详情、分类页、标签页、社区首页、节点页、帖子详情、发帖、登录注册、个人主页等功能。

两个前端都是 Vite + React 项目，彼此独立安装依赖、独立启动、独立构建。


二、技术栈
----------

两个前端共同使用：

- React 19
- React DOM 19
- React Router DOM 7
- Vite 7
- Axios
- Tailwind CSS
- ESLint

管理后台 blog-frontend 额外使用：

- Ant Design
- @ant-design/icons
- @uiw/react-md-editor
- lucide-react

公开站 blog-public 额外使用：

- react-markdown
- react-helmet-async
- lucide-react
- @tailwindcss/typography


三、启动方式
------------

启动管理后台：

cd blog-frontend
npm install
npm run dev

启动公开站：

cd blog-public
npm install
npm run dev

构建管理后台：

cd blog-frontend
npm run build

构建公开站：

cd blog-public
npm run build

预览构建产物：

npm run preview

代码检查：

npm run lint


四、环境变量
------------

两个前端都通过 Vite 环境变量读取后端 API 地址：

VITE_API_BASE_URL

如果不配置，默认使用空字符串，表示请求当前域名下的后端接口。

常见本地配置示例：

VITE_API_BASE_URL=http://localhost:8080

建议分别在 blog-frontend 和 blog-public 下创建自己的 .env.local 文件，避免把本地环境配置提交到仓库。


五、管理后台 blog-frontend
-------------------------

入口文件：

blog-frontend/src/main.jsx

应用根组件：

blog-frontend/src/App.jsx

路由入口：

blog-frontend/src/router/index.jsx

请求封装：

blog-frontend/src/utils/request.js

后台布局：

blog-frontend/src/layouts/BasicLayout.jsx

管理员登录保护：

blog-frontend/src/auth/RequireAdminAuth.jsx

管理员 token 存取：

blog-frontend/src/auth/storage.js

主要页面目录：

blog-frontend/src/pages/Login
登录页面。

blog-frontend/src/pages/Article
文章列表、新建文章、编辑文章。

blog-frontend/src/pages/Category
分类管理。

blog-frontend/src/pages/Tag
标签管理。

blog-frontend/src/pages/Comment
文章评论管理和审核。

blog-frontend/src/pages/CommunityPost
社区帖子管理。

blog-frontend/src/pages/CommunityInteraction
社区互动概览。

blog-frontend/src/pages/ModerationTask
审核任务管理。

blog-frontend/src/pages/ContentReport
举报处理。


六、管理后台路由
----------------

管理后台路由集中在 blog-frontend/src/router/index.jsx。

主要路由：

/login
管理员登录页。

/
进入后台布局后默认跳转到 /articles。

/articles
文章列表。

/articles/new
新建文章。

/articles/edit/:id
编辑文章。

/categories
分类管理。

/tags
标签管理。

/comments
评论管理。

/community-posts
社区帖子管理。

/community-interactions
社区互动概览。

/moderation-tasks
审核任务管理。

/content-reports
举报处理。

后台路由外层使用 RequireAdminAuth 做登录保护。
未登录时会跳转到登录页。


七、管理后台接口请求
--------------------

管理后台使用 blog-frontend/src/utils/request.js 统一封装 Axios。

请求时会从本地存储读取管理员 token。
如果存在 token，会自动添加请求头：

Authorization: Bearer <token>

接口返回 401 或 403 时，会清理管理员 token，并跳转到登录页。

管理后台主要面向 /api/admin 开头的后端接口。


八、公开站 blog-public
---------------------

入口文件：

blog-public/src/main.jsx

应用根组件：

blog-public/src/App.jsx

路由入口：

blog-public/src/router/index.jsx

请求封装：

blog-public/src/api/request.js

博客接口：

blog-public/src/api/article.js

社区接口：

blog-public/src/api/community.js

全局布局：

blog-public/src/components/Layout.jsx

社区登录状态：

blog-public/src/auth/AuthContext.jsx

主要页面目录：

blog-public/src/pages/List
博客文章列表首页。

blog-public/src/pages/Detail
博客文章详情页。

blog-public/src/pages/Category
分类文章列表页。

blog-public/src/pages/Tag
标签文章列表页。

blog-public/src/pages/About
关于页面。

blog-public/src/pages/Login
社区用户登录页。

blog-public/src/pages/Register
社区用户注册页。

blog-public/src/pages/Me
当前用户个人中心。

blog-public/src/pages/UserProfile
公开用户主页。

blog-public/src/pages/CommunityHome
社区首页。

blog-public/src/pages/CommunityNode
社区节点页。

blog-public/src/pages/CommunityPostDetail
社区帖子详情页。

blog-public/src/pages/CommunityPostEditor
发帖和编辑帖子页面。

blog-public/src/pages/NotFound.jsx
404 页面。


九、公开站路由
--------------

公开站路由集中在 blog-public/src/router/index.jsx。

主要路由：

/
博客文章列表首页。

/category/:slug
分类文章列表页。

/tag/:slug
标签文章列表页。

/post/:slug
博客文章详情页。

/about
关于页面。

/login
社区用户登录。

/register
社区用户注册。

/me
当前用户个人中心。

/u/:username
公开用户主页。

/community
社区首页。

/community/node/:slug
社区节点页。

/community/post/:slug
社区帖子详情页。

/community/new
发布新帖子。

/community/edit/:id
编辑帖子。

*
未匹配路由进入 404 页面。


十、公开站接口请求
------------------

公开站使用 blog-public/src/api/request.js 统一封装 Axios。

请求配置：

- baseURL 来自 VITE_API_BASE_URL
- timeout 为 10000 毫秒
- withCredentials 为 true

社区用户登录后，会从 localStorage 读取 community_token。
如果存在 token，会自动添加请求头：

X-Community-Authorization: Bearer <token>

公开站同时处理博客公开接口和社区接口。
博客公开接口一般面向 /api/public 或 /api/articles。
社区接口一般面向 /api/community。


十一、静态资源和上传文件
------------------------

前端本地静态资源通常放在各自的 src/assets 目录。

后端上传文件一般通过 /uploads/xxx 访问。
公开站 request.js 中提供 getStaticUrl 方法，用于把 /uploads/ 开头的路径拼接成完整静态资源地址。


十二、开发时常见修改位置
------------------------

新增管理后台页面：

1. 在 blog-frontend/src/pages 下新增页面目录。
2. 在 blog-frontend/src/router/index.jsx 中注册路由。
3. 如果需要菜单入口，在 blog-frontend/src/layouts/BasicLayout.jsx 中添加菜单项。
4. 如果需要接口请求，优先使用 blog-frontend/src/utils/request.js。

新增公开站页面：

1. 在 blog-public/src/pages 下新增页面目录。
2. 在 blog-public/src/router/index.jsx 中注册路由。
3. 如果需要页面导航入口，在 blog-public/src/components/Layout.jsx 中调整导航。
4. 如果是博客接口，优先放到 blog-public/src/api/article.js。
5. 如果是社区接口，优先放到 blog-public/src/api/community.js。

新增接口调用：

1. 不建议在页面里直接 new axios。
2. 管理后台统一使用 blog-frontend/src/utils/request.js。
3. 公开站统一使用 blog-public/src/api/request.js。
4. 页面只负责调用接口和展示数据，接口细节尽量放在 api 或 utils 层。

新增样式：

1. 优先沿用当前页面已有样式组织方式。
2. 公共样式可以放在 App.css 或 index.css。
3. 组件局部样式尽量和组件放在同一业务目录附近。


十三、前后端鉴权区别
--------------------

管理后台鉴权：

- 使用管理员 token。
- 请求头是 Authorization。
- 格式是 Bearer token。
- 面向管理员接口。

社区前台鉴权：

- 使用社区用户 token。
- 请求头是 X-Community-Authorization。
- 格式是 Bearer token。
- 面向社区用户接口。

这两个 token 体系不要混用。


十四、构建产物
--------------

执行 npm run build 后，Vite 会生成 dist 目录。

blog-frontend/dist 是管理后台构建产物。
blog-public/dist 是公开站构建产物。

dist 目录属于构建输出，不建议手动修改。
如果页面有问题，应修改 src 源码后重新构建。


十五、排查问题顺序
------------------

页面打不开：

1. 确认 npm run dev 是否正常启动。
2. 确认浏览器访问的是正确端口。
3. 确认路由是否在 router/index.jsx 中注册。
4. 确认控制台是否有 JavaScript 报错。

接口请求失败：

1. 确认后端是否启动。
2. 确认 VITE_API_BASE_URL 是否正确。
3. 确认浏览器 Network 中的请求地址是否正确。
4. 确认 token 是否存在且请求头是否正确。
5. 确认后端接口路径是否和前端调用一致。

登录后又跳回登录页：

1. 管理后台检查管理员 token 是否保存成功。
2. 管理后台检查 Authorization 请求头是否存在。
3. 公开站检查 community_token 是否保存成功。
4. 公开站检查 X-Community-Authorization 请求头是否存在。
5. 检查后端是否返回 401 或 403。

样式不生效：

1. 确认对应 CSS 文件是否被入口文件引入。
2. 确认 className 是否写对。
3. 如果使用 Tailwind，确认类名没有被动态拼接到无法扫描。
4. 清理浏览器缓存后重试。


十六、当前需要注意的点
----------------------

1. 项目中曾出现过中文乱码，后续新增中文内容时要确保文件使用 UTF-8 编码。
2. 不要使用带 BOM 的 UTF-8 保存 Java 源码，避免编译出现非法字符。
3. 两个前端请求封装不完全相同，修改接口层时要分别确认管理后台和公开站。
4. 管理后台和社区前台使用不同 token 请求头，不要混用。
5. 如果构建提示包体较大，可以后续考虑路由级懒加载和代码拆分。


十七、推荐阅读顺序
------------------

第一次看管理后台：

1. blog-frontend/src/main.jsx
2. blog-frontend/src/App.jsx
3. blog-frontend/src/router/index.jsx
4. blog-frontend/src/layouts/BasicLayout.jsx
5. blog-frontend/src/utils/request.js
6. blog-frontend/src/pages/Article/List.jsx

第一次看公开站：

1. blog-public/src/main.jsx
2. blog-public/src/App.jsx
3. blog-public/src/router/index.jsx
4. blog-public/src/components/Layout.jsx
5. blog-public/src/api/request.js
6. blog-public/src/pages/List/index.jsx
7. blog-public/src/pages/CommunityHome/index.jsx


十八、简单理解
--------------

blog-frontend 是后台运营系统。
blog-public 是用户访问的网站和社区。

后台负责管理内容、审核内容、处理举报。
公开站负责展示内容、用户登录注册、社区发帖和互动。

如果要改后台功能，优先看 blog-frontend。
如果要改用户看到的页面，优先看 blog-public。
