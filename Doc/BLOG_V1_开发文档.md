# BLOG v1 开发文档

## 1. 项目愿景

`BLOG v1` 不再只是一个“我能发文章的博客系统”，而是从 `v0` 升级为一个“博客 + 社区”的内容平台。

核心定位：

- 博客区：承载站长发布的长文、教程、项目记录和精选内容。
- 社区区：承载用户发帖、提问、讨论、分享和互动。
- 管理区：承载内容审核、用户治理、风险控制和日常运营。

产品气质：

- 更接近 `V2EX / Reddit` 这种有分区、有讨论氛围的小社区。
- 保留博客内容作为站点的核心辨识度。
- 通过规则、审核和工具设计降低运营成本。

## 2. v1 产品目标

### 2.1 核心目标

- 支持普通用户注册、登录、完善资料并参与社区互动。
- 保留现有文章系统，继续作为高质量内容区。
- 新增独立的社区帖子系统，而不是直接复用文章模型。
- 建立审核优先的内容治理机制，降低开放社区后的管理压力。

### 2.2 v1 完成标准

- 用户可以注册、登录、编辑个人资料、发帖。
- 用户帖子可以进入审核流程并被管理员通过或驳回。
- 社区首页、节点页、帖子详情页、用户主页可以正常使用。
- 评论支持回复，形成基础讨论链路。
- 后台支持审核队列、用户管理、举报处理。
- 帖子和评论具备“关键词规则 + AI 辅助审核”的基础能力。

## 3. 产品范围

### 3.1 v1 必做内容

- 用户注册与登录
- 用户主页与资料设置
- 社区节点
- 社区帖子
- 帖子审核流转
- 评论回复
- 举报系统
- 基础通知
- 后台审核中心
- 关键词规则与 AI 辅助审核

### 3.2 v1 暂不纳入内容

- 私信系统
- 推荐算法
- 勋章、积分、等级体系
- 复杂全文搜索
- 个性化推荐流
- 大规模实时功能

## 4. 当前项目基础

当前仓库结构：

- `blog-backend`：Spring Boot 后端
- `blog-public`：公开站点前端
- `blog-frontend`：管理后台前端

当前已有能力：

- 文章、分类、标签、评论、上传、JWT 鉴权、后台管理已经具备
- 后端模块划分较清晰，便于继续扩展
- 前台与后台分离，适合继续演进为双端结构

当前主要不足：

- 用户体系目前偏向管理员，不适合开放社区
- `Article` 适合博客发布，不适合作为用户帖子模型
- 审核、举报、通知、社区分区等流程尚未建立
- 部分文件存在中文乱码或编码问题
- 本地配置中仍有需要清理的安全隐患

## 5. v1 总体策略

### 5.1 保留 Article，新增 Post

不要把现有 `Article` 强行改造成统一内容模型。

推荐拆分：

- `Article`：官方博客内容、精选长文
- `Post`：社区用户发布的帖子
- `Comment`：评论体系，后续扩展支持文章和帖子两类内容

这样做的原因：

- 文章和帖子在创建流程、审核策略、排序规则、展示目标上都不同
- 保持分离可以减少后续维护复杂度
- 不会破坏现有博客功能的稳定性

### 5.2 公开站点升级为双内容区

推荐站点信息架构：

- `/` 或 `/blog`：博客首页
- `/community`：社区首页
- `/nodes/:slug`：节点页
- `/posts/:id` 或 `/t/:id`：帖子详情
- `/submit`：发帖页
- `/u/:username`：用户主页
- `/settings/profile`：个人设置
- `/notifications`：通知页

### 5.3 管理后台升级为运营后台

推荐后台栏目：

- 仪表盘
- 文章管理
- 社区帖子管理
- 评论管理
- 举报处理
- 审核中心
- 用户管理
- 节点管理
- 标签 / 分类管理
- 站点设置

## 6. 后端模块设计建议

建议在 `blog-backend/src/main/java/com/pxczxn/blog` 下新增模块：

- `member`：社区用户注册、登录、会话
- `profile`：用户资料与公开主页信息
- `post`：社区帖子
- `node`：社区节点 / 分区
- `moderation`：审核队列、AI 审核、关键词规则
- `report`：举报处理
- `notification`：站内通知
- `follow`：关注关系

现有模块继续保留并扩展：

- `content`：博客文章
- `comment`：评论系统，后续扩展支持帖子评论
- `user`：可暂时保留为管理员体系，后续再视情况整合

## 7. 数据模型设计建议

### 7.1 用户与资料

建议新增表：

- `community_user`
- `community_user_profile`
- `community_user_role`
- `community_user_status_log`

建议 `community_user` 字段：

| 字段 | 说明 |
| --- | --- |
| `id` | 主键 |
| `username` | 唯一用户名 |
| `email` | 唯一邮箱，可选 |
| `password_hash` | 密码哈希 |
| `status` | 正常 / 禁言 / 封禁 / 待激活 |
| `role` | 用户 / 版主 / 管理员 |
| `created_at` | 创建时间 |
| `updated_at` | 更新时间 |
| `last_login_at` | 最近登录时间 |

建议 `community_user_profile` 字段：

| 字段 | 说明 |
| --- | --- |
| `user_id` | 用户外键 |
| `nickname` | 显示昵称 |
| `avatar` | 头像地址 |
| `bio` | 个人简介 |
| `website` | 个人网站，可选 |
| `github` | GitHub，可选 |
| `location` | 所在地，可选 |
| `post_count` | 发帖数，冗余统计 |
| `comment_count` | 评论数，冗余统计 |
| `follower_count` | 粉丝数，冗余统计 |
| `following_count` | 关注数，冗余统计 |

### 7.2 社区结构

建议新增表：

- `post_node`
- `post`
- `post_tag`
- `post_tag_relation`

建议 `post_node` 字段：

| 字段 | 说明 |
| --- | --- |
| `id` | 主键 |
| `name` | 节点名称 |
| `slug` | 路由标识 |
| `description` | 节点描述 |
| `sort_order` | 排序值 |
| `status` | 启用 / 停用 |

建议 `post` 字段：

| 字段 | 说明 |
| --- | --- |
| `id` | 主键 |
| `author_id` | 发帖用户 |
| `node_id` | 所属节点 |
| `title` | 标题 |
| `content` | Markdown 正文 |
| `summary` | 可选摘要 |
| `status` | 草稿 / 待审核 / 已发布 / 已拒绝 / 已隐藏 |
| `moderation_status` | 无 / 待审核 / 已通过 / 已拒绝 |
| `view_count` | 浏览数 |
| `comment_count` | 评论数 |
| `like_count` | 点赞数 |
| `favorite_count` | 收藏数 |
| `last_commented_at` | 最近被回复时间 |
| `published_at` | 发布时间 |
| `created_at` | 创建时间 |
| `updated_at` | 更新时间 |

### 7.3 评论升级

现有 `comment` 模块建议扩展为同时支持文章和帖子。

建议补充字段：

- `target_type`：`ARTICLE` 或 `POST`
- `target_id`
- `user_id`
- `parent_id`
- `reply_to_user_id`
- `status`
- `like_count`
- `moderation_status`

如果直接改现有评论表风险较高，也可以先为社区单独建一张 `community_comment` 表，后续再统一。

### 7.4 审核与举报

建议新增表：

- `moderation_task`
- `moderation_rule_hit`
- `user_report`
- `moderation_action_log`
- `keyword_rule`

建议 `moderation_task` 字段：

| 字段 | 说明 |
| --- | --- |
| `id` | 主键 |
| `target_type` | 帖子 / 评论 / 资料 |
| `target_id` | 目标内容 ID |
| `submitted_by` | 触发人 |
| `source` | 系统 / AI / 举报 |
| `decision` | 放行 / 复审 / 拒绝 |
| `risk_score` | 风险分数 |
| `labels` | 风险标签，建议 JSON |
| `reason` | 原因说明 |
| `review_status` | 待处理 / 已处理 |
| `reviewed_by` | 审核人 |
| `reviewed_at` | 审核时间 |

### 7.5 社交基础能力

建议新增表：

- `post_like`
- `post_favorite`
- `follow_relation`
- `notification`

## 8. 审核与风控方案

### 8.1 核心原则

采用“规则优先，AI 辅助，人工兜底”。

### 8.2 审核流程

1. 用户提交帖子或评论
2. 规则引擎先检查关键词、外链、重复内容、新用户风险
3. 命中高风险规则则直接进入审核队列
4. AI 输出结构化判断：`allow` / `review` / `reject`
5. 高风险或不确定内容进入后台审核
6. 管理员给出最终结论并记录操作日志

### 8.3 v1 规则集

- 敏感词匹配
- 联系方式识别
- 广告词 / 引流词拦截
- 外链数量限制
- 重复内容检测
- 发帖 / 评论频率限制
- 新用户前几帖强制审核

### 8.4 AI 审核输出规范

AI 审核层必须返回结构化 JSON，例如：

```json
{
  "decision": "allow",
  "risk_score": 12,
  "labels": ["safe"],
  "reason": "正常技术讨论"
}
```

建议约束：

- AI 不直接决定封号、禁言这类高风险操作
- AI 可以帮助自动放行低风险内容，但必须建立在规则和用户等级基础上
- AI 的拒绝结论必须能在后台复核

## 9. 前台页面规划

### 9.1 公开站点页面

- 博客首页
- 文章详情页
- 社区首页
- 节点页
- 帖子详情页
- 发帖页
- 用户主页
- 个人设置页
- 通知页

### 9.2 社区首页模块建议

- 顶部导航：博客 / 社区 / 节点 / 关于
- 推荐节点
- 最新帖子
- 热门帖子
- 未回复帖子
- 新用户使用说明

### 9.3 帖子详情页建议

- 标题与元信息
- 作者信息卡片
- Markdown 正文
- 点赞 / 收藏 / 举报操作区
- 评论讨论区
- 同节点相关推荐

### 9.4 用户主页建议

- 用户信息
- 最近发帖
- 最近评论
- 收藏内容
- 关注 / 粉丝

## 10. 后台页面规划

建议在 `blog-frontend` 中新增：

- `pages/Dashboard`
- `pages/Post`
- `pages/Moderation`
- `pages/Report`
- `pages/User`
- `pages/Node`

后台关键流程：

- 审核待发布帖子
- 审核待展示评论
- 查看风险标签和规则命中记录
- 通过 / 驳回 / 隐藏 / 恢复内容
- 禁言 / 封禁用户
- 管理节点
- 处理举报

## 11. 接口规划

### 11.1 用户体系

- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/logout`
- `GET /api/me`
- `PUT /api/me/profile`

### 11.2 社区帖子

- `GET /api/community/posts`
- `POST /api/community/posts`
- `GET /api/community/posts/{id}`
- `PUT /api/community/posts/{id}`
- `DELETE /api/community/posts/{id}`
- `POST /api/community/posts/{id}/publish`
- `POST /api/community/posts/{id}/like`
- `POST /api/community/posts/{id}/favorite`

### 11.3 节点

- `GET /api/community/nodes`
- `GET /api/community/nodes/{slug}`

### 11.4 评论

- `GET /api/community/posts/{id}/comments`
- `POST /api/community/posts/{id}/comments`
- `POST /api/community/comments/{id}/reply`
- `POST /api/community/comments/{id}/like`
- `POST /api/community/comments/{id}/report`

### 11.5 通知

- `GET /api/notifications`
- `POST /api/notifications/{id}/read`

### 11.6 举报与审核

- `POST /api/reports`
- `GET /api/admin/moderation/tasks`
- `POST /api/admin/moderation/tasks/{id}/approve`
- `POST /api/admin/moderation/tasks/{id}/reject`
- `GET /api/admin/reports`
- `POST /api/admin/reports/{id}/resolve`

## 12. 仓库级实施计划

### P0：基础修复与加固

优先级：最高

- 修复中文乱码与编码问题
- 清理仓库中的明文敏感配置
- 统一接口异常与错误响应
- 检查 JWT、权限边界和登录态处理
- 复核上传安全与路径校验

### P1：用户体系地基

优先级：最高

- 增加社区用户注册与登录
- 增加用户资料接口
- 增加公开用户主页
- 按需要区分管理员身份与社区用户身份

### P2：社区内容地基

优先级：高

- 新增节点模型与迁移
- 新增帖子模型与迁移
- 完成帖子发布、详情、列表接口
- 完成社区首页
- 完成发帖页
- 后台支持基础帖子管理

### P3：审核闭环

优先级：高

- 新增审核任务表与审核队列
- 接入关键词规则
- 预留 AI 审核服务层
- 接入举报流程
- 后台新增审核中心

### P4：互动补全

优先级：中

- 评论回复
- 通知
- 点赞 / 收藏
- 关注系统

### P5：打磨与上线

优先级：中

- 统一博客区与社区区的视觉风格
- 增加新用户引导与社区规则说明
- 增加日志、指标和基础监控
- 增加冒烟测试与回归验证
- 完成 Beta 部署并收集反馈

## 13. 里程碑建议

### 里程碑一：v1-alpha

- 完成 P0
- 完成 P1
- 完成 P2 的核心能力

预期结果：

- 用户可以注册、登录并发帖
- 社区首页和帖子详情页可用

### 里程碑二：v1-beta

- 完成 P3
- 完成基础评论回复

预期结果：

- 审核闭环可运行
- 平台具备有限开放能力

### 里程碑三：v1-release

- 完成 P4 和 P5 核心项

预期结果：

- 社区可用、可治理、可持续迭代

## 14. 针对当前仓库的落地建议

建议在 `blog-public/src` 下新增：

- `pages/Community`
- `pages/PostDetail`
- `pages/SubmitPost`
- `pages/Profile`
- `pages/Notifications`
- `api/community.js`
- `api/user.js`
- `api/notification.js`

建议在 `blog-frontend/src` 下新增：

- `pages/Post`
- `pages/Moderation`
- `pages/Report`
- `pages/User`
- `pages/Node`

建议后端迁移文件命名方向：

- `V7__create_community_user_tables.sql`
- `V8__create_post_and_node_tables.sql`
- `V9__extend_comment_for_community.sql`
- `V10__create_moderation_and_report_tables.sql`
- `V11__create_notification_and_follow_tables.sql`

## 15. 风险评估

### 15.1 产品风险

- 如果在审核工具不足时过早开放发帖，运营压力会迅速上升
- 如果把文章和帖子混成一个模型，后续复杂度会明显增加

### 15.2 技术风险

- 编码问题如果不先处理，后续扩展时会持续污染代码
- 管理员鉴权和社区用户鉴权如果混用不清晰，容易产生安全漏洞
- 完全依赖 AI 审核会导致稳定性和可解释性不足

## 16. 最终建议

`BLOG v1` 的目标不应该是“一次做成超级大社区”，而应该是：

“一个以博客为内容核心，同时允许用户在结构化社区中发言、讨论和成长的平台。”

推荐开发顺序：

1. 先修基础
2. 再补用户体系
3. 再做社区帖子
4. 再做审核治理
5. 最后补互动和体验

这条路线既能保留 `v0` 已有价值，也能让 `v1` 真正具备社区形态。
