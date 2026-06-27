-- ============================================================
-- pxczxn-blog 个人博客系统 - 完整数据库初始化脚本
-- 数据库: pxczxn-blog
-- 用户: pxczxn / pxczxn
-- 说明: 合并 Flyway V1~V19 全部迁移，一次执行即可还原完整数据库
-- 生成时间: 2026-05-29
-- ============================================================

CREATE DATABASE IF NOT EXISTS `pxczxn-blog` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `pxczxn-blog`;

-- ============================================================
-- 1. 管理员用户表 (V1 + V15)
-- ============================================================
CREATE TABLE IF NOT EXISTS admin_user (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱地址',
    password_hash VARCHAR(60) NOT NULL COMMENT '密码哈希值(BCrypt)',
    status ENUM('ACTIVE','BANNED') NOT NULL DEFAULT 'ACTIVE' COMMENT '账号状态: ACTIVE-正常, BANNED-封禁',
    role VARCHAR(20) NOT NULL DEFAULT 'ADMIN' COMMENT '角色',
    last_login_at DATETIME NULL COMMENT '最后登录时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员用户表';

-- ============================================================
-- 2. 社区用户表 (V7 + V15)
-- ============================================================
CREATE TABLE IF NOT EXISTS community_user (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱地址',
    password_hash VARCHAR(60) NOT NULL COMMENT '密码哈希值(BCrypt)',
    display_name VARCHAR(80) NOT NULL COMMENT '显示名称',
    avatar VARCHAR(255) NULL COMMENT '头像URL',
    bio VARCHAR(500) NULL COMMENT '个人简介',
    website VARCHAR(255) NULL COMMENT '个人网站',
    status ENUM('ACTIVE', 'PENDING', 'BANNED') NOT NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-正常, PENDING-待激活, BANNED-封禁',
    role VARCHAR(30) NOT NULL DEFAULT 'USER' COMMENT '角色: USER-普通用户, MODERATOR-版主',
    last_login_at DATETIME NULL COMMENT '最后登录时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_community_user_username (username),
    INDEX idx_community_user_email (email),
    INDEX idx_community_user_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社区用户表';

-- ============================================================
-- 3. 文章表 (V1 + V2 + V4 + V15 + V17)
-- ============================================================
CREATE TABLE IF NOT EXISTS article (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    title VARCHAR(200) NOT NULL COMMENT '文章标题',
    slug VARCHAR(200) NOT NULL UNIQUE COMMENT 'URL别名(唯一标识)',
    summary TEXT NULL COMMENT '文章摘要',
    content LONGTEXT NOT NULL COMMENT '文章内容(Markdown)',
    cover_image VARCHAR(500) NULL COMMENT '封面图片URL',
    view_count BIGINT NOT NULL DEFAULT 0 COMMENT '浏览次数',
    status ENUM('DRAFT','PUBLISHED') NOT NULL DEFAULT 'DRAFT' COMMENT '状态: DRAFT-草稿, PUBLISHED-已发布',
    published_at DATETIME NULL COMMENT '发布时间',
    author_id BIGINT UNSIGNED NULL COMMENT '作者ID(admin_user)',
    community_author_id BIGINT UNSIGNED NULL COMMENT '社区作者ID(community_user)',
    category_id BIGINT UNSIGNED NULL COMMENT '分类ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (author_id) REFERENCES admin_user(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX idx_slug (slug),
    INDEX idx_status (status),
    INDEX idx_status_published_at (status, published_at DESC),
    INDEX idx_article_status_created_at (status, created_at DESC),
    INDEX idx_author_id (author_id),
    INDEX idx_article_category_id (category_id),
    INDEX idx_article_community_author_id (community_author_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='博客文章表';

-- ============================================================
-- 4. 设备会话表 (V1 + V15)
-- ============================================================
CREATE TABLE IF NOT EXISTS device_session (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    admin_user_id BIGINT NOT NULL COMMENT '管理员用户ID',
    device_id VARCHAR(200) NOT NULL COMMENT '设备唯一标识',
    device_name VARCHAR(200) NULL COMMENT '设备名称',
    ip VARCHAR(45) NOT NULL COMMENT 'IP地址',
    user_agent TEXT NULL COMMENT '浏览器User-Agent',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    last_seen_at DATETIME NOT NULL COMMENT '最后活跃时间',
    is_active TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否活跃',
    INDEX idx_admin_user_id_device (admin_user_id, device_id),
    INDEX idx_admin_user_id_active (admin_user_id, is_active),
    INDEX idx_last_seen_at (last_seen_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员设备会话表';

-- ============================================================
-- 5. Token撤销表 (V1 + V15)
-- ============================================================
-- ============================================================
-- 4.1. Site settings table
-- ============================================================
CREATE TABLE IF NOT EXISTS site_setting (
    setting_key VARCHAR(80) PRIMARY KEY COMMENT 'Setting key',
    setting_value TEXT NOT NULL COMMENT 'Setting value',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Site settings';

INSERT INTO site_setting (setting_key, setting_value)
VALUES
    ('siteName', '破星辰只寻你'),
    ('siteSub', '在代码的星河中，寻找技术与自由'),
    ('adminNick', '破星辰只寻你'),
    ('keywords', '博客,前端,React,Java,Spring Boot,AI Coding,个人网站'),
    ('description', '一个记录真实项目、踩坑复盘、工程实践与持续构建过程的个人博客。'),
    ('allowGuest', 'false'),
    ('homeTitle', '探索技术边界'),
    ('homeIntro', '分享前端开发、系统设计与工程化实践，也记录一个开发者不断打磨作品的过程。'),
    ('aboutBio', '一个正在学习与探索 AI Coding 的全栈开发者。这里会记录技术、设计和持续构建过程中的思考与实践。'),
    ('aboutBioSub', '这个博客会更偏向真实项目、踩坑记录、审美迭代，以及把想法真正落成作品的过程。'),
    ('frontend', 'React / Vue / TypeScript / Tailwind CSS'),
    ('backend', 'Java / Spring Boot / Node.js'),
    ('engineering', 'Vite / Webpack / Git'),
    ('other', 'MySQL / Redis / Linux')
ON DUPLICATE KEY UPDATE setting_value = setting_value;

CREATE TABLE IF NOT EXISTS token_revoked (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    jti VARCHAR(64) NOT NULL UNIQUE COMMENT 'JWT唯一标识',
    admin_user_id BIGINT NOT NULL COMMENT '管理员用户ID',
    revoked_at DATETIME NOT NULL COMMENT '撤销时间',
    expires_at DATETIME NOT NULL COMMENT 'Token过期时间',
    reason VARCHAR(50) NULL COMMENT '撤销原因',
    INDEX idx_jti (jti),
    INDEX idx_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员Token撤销记录表';

-- ============================================================
-- 6. 文章分类表 (V4 + V15)
-- ============================================================
CREATE TABLE IF NOT EXISTS category (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(100) NOT NULL UNIQUE COMMENT '分类名称',
    slug VARCHAR(120) NOT NULL UNIQUE COMMENT 'URL别名',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_category_created_at (created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章分类表';

-- ============================================================
-- 7. 文章标签表 (V4_1 + V15)
-- ============================================================
CREATE TABLE IF NOT EXISTS tag (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(100) NOT NULL UNIQUE COMMENT '标签名称',
    slug VARCHAR(120) NOT NULL UNIQUE COMMENT 'URL别名',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_tag_created_at (created_at DESC),
    INDEX idx_tag_slug (slug)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章标签表';

-- ============================================================
-- 8. 文章标签关联表 (V4_1 + V15)
-- ============================================================
CREATE TABLE IF NOT EXISTS article_tag (
    article_id BIGINT UNSIGNED NOT NULL COMMENT '文章ID',
    tag_id BIGINT UNSIGNED NOT NULL COMMENT '标签ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (article_id, tag_id),
    CONSTRAINT fk_article_tag_article FOREIGN KEY (article_id) REFERENCES article(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_article_tag_tag FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX idx_article_tag_tag_id (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章-标签关联表';

-- ============================================================
-- 9. 文章评论表 (V4_1 + V7 community_user_id + V15)
-- ============================================================
CREATE TABLE IF NOT EXISTS comment (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    article_id BIGINT UNSIGNED NOT NULL COMMENT '文章ID',
    parent_id BIGINT UNSIGNED NULL COMMENT '父评论ID(用于回复)',
    community_user_id BIGINT UNSIGNED NULL COMMENT '社区用户ID(登录用户)',
    nickname VARCHAR(50) NOT NULL COMMENT '昵称(游客)',
    email VARCHAR(100) NULL COMMENT '邮箱(游客)',
    content VARCHAR(1000) NOT NULL COMMENT '评论内容',
    status ENUM('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING-待审核, APPROVED-已通过, REJECTED-已拒绝',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CONSTRAINT fk_comment_article FOREIGN KEY (article_id) REFERENCES article(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_comment_parent FOREIGN KEY (parent_id) REFERENCES comment(id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_comment_community_user FOREIGN KEY (community_user_id) REFERENCES community_user(id) ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX idx_comment_article_status_created_at (article_id, status, created_at),
    INDEX idx_comment_status_created_at (status, created_at),
    INDEX idx_comment_community_user_id (community_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章评论表';

-- ============================================================
-- 10. 社区Token撤销表 (V7 + V15)
-- ============================================================
CREATE TABLE IF NOT EXISTS community_token_revoked (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    jti VARCHAR(64) NOT NULL UNIQUE COMMENT 'JWT唯一标识',
    community_user_id BIGINT UNSIGNED NOT NULL COMMENT '社区用户ID',
    revoked_at DATETIME NOT NULL COMMENT '撤销时间',
    expires_at DATETIME NOT NULL COMMENT 'Token过期时间',
    reason VARCHAR(50) NULL COMMENT '撤销原因',
    CONSTRAINT fk_community_token_revoked_user
        FOREIGN KEY (community_user_id) REFERENCES community_user(id) ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_community_token_revoked_user_id (community_user_id),
    INDEX idx_community_token_revoked_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社区用户Token撤销记录表';

-- ============================================================
-- 11. 社区节点/板块表 (V8 + V15)
-- ============================================================
CREATE TABLE IF NOT EXISTS community_node (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(80) NOT NULL UNIQUE COMMENT '节点名称',
    slug VARCHAR(80) NOT NULL UNIQUE COMMENT 'URL别名',
    description VARCHAR(255) NULL COMMENT '节点描述',
    icon VARCHAR(50) NULL COMMENT '图标名称',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序权重(越大越靠前)',
    status ENUM('ACTIVE', 'HIDDEN') NOT NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-显示, HIDDEN-隐藏',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_community_node_sort (sort_order, created_at),
    INDEX idx_community_node_status_sort_created (status, sort_order, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社区节点/板块表';

-- ============================================================
-- 12. 社区帖子表 (V8 + V15)
-- ============================================================
CREATE TABLE IF NOT EXISTS community_post (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    node_id BIGINT UNSIGNED NOT NULL COMMENT '所属节点ID',
    author_id BIGINT UNSIGNED NOT NULL COMMENT '作者ID',
    title VARCHAR(200) NOT NULL COMMENT '帖子标题',
    slug VARCHAR(220) NOT NULL UNIQUE COMMENT 'URL别名',
    summary VARCHAR(500) NULL COMMENT '帖子摘要',
    content LONGTEXT NOT NULL COMMENT '帖子内容(Markdown)',
    status ENUM('DRAFT', 'PENDING_REVIEW', 'PUBLISHED', 'REJECTED', 'HIDDEN') NOT NULL DEFAULT 'DRAFT' COMMENT '状态: DRAFT-草稿, PENDING_REVIEW-待审核, PUBLISHED-已发布, REJECTED-已拒绝, HIDDEN-已隐藏',
    published_at DATETIME NULL COMMENT '发布时间',
    last_edited_at DATETIME NULL COMMENT '最后编辑时间',
    view_count BIGINT NOT NULL DEFAULT 0 COMMENT '浏览次数',
    rejection_reason VARCHAR(500) NULL COMMENT '拒绝原因',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT fk_community_post_node
        FOREIGN KEY (node_id) REFERENCES community_node(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_community_post_author
        FOREIGN KEY (author_id) REFERENCES community_user(id) ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_community_post_node_status (node_id, status, published_at),
    INDEX idx_community_post_node_status_published_created (node_id, status, published_at DESC, created_at DESC),
    INDEX idx_community_post_author_created (author_id, created_at),
    INDEX idx_community_post_status_created (status, created_at),
    INDEX idx_community_post_status_published_created (status, published_at DESC, created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社区帖子表';

-- ============================================================
-- 13. 审核关键词规则表 (V9 + V15)
-- ============================================================
CREATE TABLE IF NOT EXISTS moderation_keyword_rule (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(80) NOT NULL COMMENT '规则名称',
    keyword_value VARCHAR(120) NOT NULL UNIQUE COMMENT '关键词',
    severity ENUM('LOW', 'MEDIUM', 'HIGH', 'BLOCK') NOT NULL DEFAULT 'MEDIUM' COMMENT '严重程度: LOW-低, MEDIUM-中, HIGH-高, BLOCK-自动拦截',
    enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_moderation_keyword_enabled (enabled, severity)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容审核关键词规则表';

-- ============================================================
-- 14. 审核任务表 (V9 + V13 POST_COMMENT + V15)
-- ============================================================
CREATE TABLE IF NOT EXISTS moderation_task (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    content_type ENUM('POST', 'COMMENT', 'POST_COMMENT') NOT NULL COMMENT '内容类型: POST-帖子, COMMENT-评论, POST_COMMENT-帖子评论',
    content_id BIGINT UNSIGNED NOT NULL COMMENT '内容ID',
    submitted_by BIGINT UNSIGNED NULL COMMENT '提交者ID',
    title_snapshot VARCHAR(220) NULL COMMENT '标题快照',
    status ENUM('PENDING', 'APPROVED', 'REJECTED', 'CANCELED') NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING-待审核, APPROVED-已通过, REJECTED-已拒绝, CANCELED-已取消',
    risk_level ENUM('LOW', 'MEDIUM', 'HIGH') NOT NULL DEFAULT 'LOW' COMMENT '风险等级',
    hit_count INT NOT NULL DEFAULT 0 COMMENT '命中规则数',
    decision_note VARCHAR(500) NULL COMMENT '审核备注',
    reviewed_by BIGINT UNSIGNED NULL COMMENT '审核人ID',
    submitted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    reviewed_at DATETIME NULL COMMENT '审核时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT fk_moderation_task_submitter
        FOREIGN KEY (submitted_by) REFERENCES community_user(id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_moderation_task_reviewer
        FOREIGN KEY (reviewed_by) REFERENCES admin_user(id) ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX idx_moderation_task_status_created (status, created_at),
    INDEX idx_moderation_task_content_status (content_type, content_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容审核任务表';

-- ============================================================
-- 15. 审核规则命中记录表 (V9 + V15)
-- ============================================================
CREATE TABLE IF NOT EXISTS moderation_rule_hit (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    task_id BIGINT UNSIGNED NOT NULL COMMENT '审核任务ID',
    rule_id BIGINT UNSIGNED NOT NULL COMMENT '规则ID',
    keyword_value VARCHAR(120) NOT NULL COMMENT '关键词',
    snippet VARCHAR(255) NULL COMMENT '命中上下文片段',
    severity ENUM('LOW', 'MEDIUM', 'HIGH', 'BLOCK') NOT NULL COMMENT '严重程度',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CONSTRAINT fk_moderation_rule_hit_task
        FOREIGN KEY (task_id) REFERENCES moderation_task(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_moderation_rule_hit_rule
        FOREIGN KEY (rule_id) REFERENCES moderation_keyword_rule(id) ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_moderation_rule_hit_task (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审核规则命中记录表';

-- ============================================================
-- 16. 内容举报表 (V9 + V13 POST_COMMENT + V15)
-- ============================================================
CREATE TABLE IF NOT EXISTS content_report (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    content_type ENUM('POST', 'COMMENT', 'POST_COMMENT') NOT NULL COMMENT '内容类型: POST-帖子, COMMENT-评论, POST_COMMENT-帖子评论',
    content_id BIGINT UNSIGNED NOT NULL COMMENT '内容ID',
    reporter_user_id BIGINT UNSIGNED NOT NULL COMMENT '举报人ID',
    reason ENUM('SPAM', 'ABUSE', 'COPYRIGHT', 'ILLEGAL', 'OTHER') NOT NULL DEFAULT 'OTHER' COMMENT '举报原因',
    description VARCHAR(500) NULL COMMENT '举报描述',
    status ENUM('OPEN', 'RESOLVED', 'DISMISSED') NOT NULL DEFAULT 'OPEN' COMMENT '状态: OPEN-待处理, RESOLVED-已解决, DISMISSED-已驳回',
    handle_action ENUM('NONE', 'HIDE_POST', 'REJECT_COMMENT') NOT NULL DEFAULT 'NONE' COMMENT '处理动作',
    handle_note VARCHAR(500) NULL COMMENT '处理备注',
    handled_by BIGINT UNSIGNED NULL COMMENT '处理人ID',
    handled_at DATETIME NULL COMMENT '处理时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT fk_content_report_reporter
        FOREIGN KEY (reporter_user_id) REFERENCES community_user(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_content_report_handler
        FOREIGN KEY (handled_by) REFERENCES admin_user(id) ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX idx_content_report_status_created (status, created_at),
    INDEX idx_content_report_target (content_type, content_id),
    INDEX idx_content_report_reporter (reporter_user_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容举报表';

-- ============================================================
-- 17. 帖子点赞表 (V10 + V15)
-- ============================================================
CREATE TABLE IF NOT EXISTS community_post_like (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    post_id BIGINT UNSIGNED NOT NULL COMMENT '帖子ID',
    user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    CONSTRAINT fk_community_post_like_post
        FOREIGN KEY (post_id) REFERENCES community_post(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_community_post_like_user
        FOREIGN KEY (user_id) REFERENCES community_user(id) ON DELETE CASCADE ON UPDATE CASCADE,
    UNIQUE KEY uk_community_post_like (post_id, user_id),
    INDEX idx_community_post_like_user_created (user_id, created_at),
    INDEX idx_community_post_like_post_created (post_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社区帖子点赞表';

-- ============================================================
-- 18. 帖子收藏表 (V10 + V15)
-- ============================================================
CREATE TABLE IF NOT EXISTS community_post_favorite (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    post_id BIGINT UNSIGNED NOT NULL COMMENT '帖子ID',
    user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    CONSTRAINT fk_community_post_favorite_post
        FOREIGN KEY (post_id) REFERENCES community_post(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_community_post_favorite_user
        FOREIGN KEY (user_id) REFERENCES community_user(id) ON DELETE CASCADE ON UPDATE CASCADE,
    UNIQUE KEY uk_community_post_favorite (post_id, user_id),
    INDEX idx_community_post_favorite_user_created (user_id, created_at),
    INDEX idx_community_post_favorite_post_created (post_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社区帖子收藏表';

-- ============================================================
-- 19. 用户关注表 (V10 + V15)
-- ============================================================
CREATE TABLE IF NOT EXISTS community_user_follow (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    follower_id BIGINT UNSIGNED NOT NULL COMMENT '关注者ID',
    following_id BIGINT UNSIGNED NOT NULL COMMENT '被关注者ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
    CONSTRAINT fk_community_user_follow_follower
        FOREIGN KEY (follower_id) REFERENCES community_user(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_community_user_follow_following
        FOREIGN KEY (following_id) REFERENCES community_user(id) ON DELETE CASCADE ON UPDATE CASCADE,
    UNIQUE KEY uk_community_user_follow (follower_id, following_id),
    INDEX idx_community_user_follow_follower_created (follower_id, created_at),
    INDEX idx_community_user_follow_following_created (following_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社区用户关注表';

-- ============================================================
-- 20. 帖子评论表 (V12 + V13 默认PENDING + V15)
-- ============================================================
CREATE TABLE IF NOT EXISTS community_post_comment (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    post_id BIGINT UNSIGNED NOT NULL COMMENT '帖子ID',
    parent_id BIGINT UNSIGNED NULL COMMENT '父评论ID(用于回复)',
    community_user_id BIGINT UNSIGNED NOT NULL COMMENT '社区用户ID',
    nickname VARCHAR(80) NOT NULL COMMENT '昵称',
    content VARCHAR(1000) NOT NULL COMMENT '评论内容',
    status ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING-待审核, APPROVED-已通过, REJECTED-已拒绝',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT fk_community_post_comment_post
        FOREIGN KEY (post_id) REFERENCES community_post(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_community_post_comment_parent
        FOREIGN KEY (parent_id) REFERENCES community_post_comment(id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_community_post_comment_user
        FOREIGN KEY (community_user_id) REFERENCES community_user(id) ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_community_post_comment_post_status_created (post_id, status, created_at),
    INDEX idx_community_post_comment_user_created (community_user_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社区帖子评论表';

-- ============================================================
-- 21. 社区通知表 (V10 + V11 COMMENT_REPLIED + V13 + V15)
-- ============================================================
CREATE TABLE IF NOT EXISTS community_notification (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT UNSIGNED NOT NULL COMMENT '接收用户ID',
    actor_user_id BIGINT UNSIGNED NULL COMMENT '触发用户ID',
    type ENUM('POST_LIKED', 'POST_FAVORITED', 'USER_FOLLOWED', 'COMMENT_REPLIED', 'SYSTEM') NOT NULL COMMENT '通知类型',
    title VARCHAR(120) NOT NULL COMMENT '通知标题',
    content VARCHAR(500) NULL COMMENT '通知内容',
    related_post_id BIGINT UNSIGNED NULL COMMENT '关联帖子ID',
    related_comment_id BIGINT UNSIGNED NULL COMMENT '关联评论ID',
    related_post_comment_id BIGINT UNSIGNED NULL COMMENT '关联帖子评论ID',
    is_read TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已读',
    read_at DATETIME NULL COMMENT '阅读时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CONSTRAINT fk_community_notification_user
        FOREIGN KEY (user_id) REFERENCES community_user(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_community_notification_actor_user
        FOREIGN KEY (actor_user_id) REFERENCES community_user(id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_community_notification_post
        FOREIGN KEY (related_post_id) REFERENCES community_post(id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_community_notification_comment
        FOREIGN KEY (related_comment_id) REFERENCES comment(id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_community_notification_post_comment
        FOREIGN KEY (related_post_comment_id) REFERENCES community_post_comment(id) ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX idx_community_notification_user_unread (user_id, is_read, created_at),
    INDEX idx_community_notification_user_created (user_id, created_at),
    INDEX idx_community_notification_post_comment (related_post_comment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社区通知表';

-- ============================================================
-- 22. 帖子标签关联表 (V16)
-- ============================================================
CREATE TABLE IF NOT EXISTS community_post_tag (
    post_id BIGINT UNSIGNED NOT NULL COMMENT '帖子ID',
    tag_id BIGINT UNSIGNED NOT NULL COMMENT '标签ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (post_id, tag_id),
    CONSTRAINT fk_community_post_tag_post FOREIGN KEY (post_id) REFERENCES community_post(id) ON DELETE CASCADE,
    CONSTRAINT fk_community_post_tag_tag FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE RESTRICT,
    INDEX idx_community_post_tag_tag_id (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社区帖子标签关联表';


-- ============================================================
-- 初始化种子数据
-- ============================================================

-- ----------------------------
-- 管理员账号 (默认密码: Pxczxnpxczxn，BCrypt 加密)
-- ----------------------------
INSERT INTO admin_user (username, email, password_hash, status, role) VALUES
('破星辰只寻你', 'Pxczxn@163.com', '$2a$10$dE2S5XOgzqmpH2EJwn27CuNcnZTEwzyOUf7FOzJ1h1s1vvBHhhMvG', 'ACTIVE', 'ADMIN');

-- ----------------------------
-- 文章分类 (V19 最终版: 9个)
-- ----------------------------
INSERT INTO category (name, slug) VALUES
('前端开发', 'frontend-dev'),
('后端开发', 'backend-dev'),
('移动开发', 'mobile-dev'),
('DevOps', 'devops'),
('算法与数据结构', 'algorithms'),
('系统架构', 'architecture'),
('项目记录', 'project-log'),
('学习笔记', 'learning-notes'),
('随笔思考', 'essays');

-- ----------------------------
-- 标签 (V19 最终版: 28个)
-- ----------------------------
INSERT INTO tag (name, slug) VALUES
('Spring Boot', 'spring-boot'),
('React', 'react'),
('Java', 'java'),
('TypeScript', 'typescript'),
('MySQL', 'mysql'),
('Vue', 'vue'),
('Node.js', 'nodejs'),
('Python', 'python'),
('Docker', 'docker'),
('Linux', 'linux'),
('教程', 'tutorial'),
('实战', 'practice'),
('踩坑记录', 'pitfalls'),
('最佳实践', 'best-practices'),
('源码分析', 'source-code'),
('性能优化', 'performance'),
('入门', 'beginner'),
('进阶', 'intermediate'),
('高级', 'advanced'),
('连载中', 'ongoing'),
('已完结', 'completed'),
('人工智能', 'ai'),
('机器学习', 'machine-learning'),
('大模型', 'llm'),
('前端开发', 'frontend'),
('后端开发', 'backend'),
('系统设计', 'system-design'),
('博客系统', 'blog-system');

-- ----------------------------
-- 社区节点 (V8: 3个板块)
-- ----------------------------
INSERT INTO community_node (name, slug, description, icon, sort_order) VALUES
('开发日常', 'dev-log', '记录代码、工具和实践的日常分享', 'terminal', 10),
('问题求助', 'q-and-a', '提问、排障和互相解答的讨论区', 'life-buoy', 20),
('作品展示', 'showcase', '展示自己的项目、页面和灵感', 'sparkles', 30);

-- ----------------------------
-- 审核关键词规则 (V9 基础 + V18 扩展)
-- ----------------------------
INSERT INTO moderation_keyword_rule (name, keyword_value, severity, enabled) VALUES
('Ad keyword', 'buy followers', 'HIGH', 1),
('Ad keyword', 'contact me on telegram', 'HIGH', 1),
('Fraud keyword', 'investment guarantee', 'BLOCK', 1),
('Abuse keyword', 'hate speech', 'BLOCK', 1),
('Spam keyword', 'click this link', 'MEDIUM', 1),
('External link', 'http://', 'BLOCK', 1),
('External link', 'https://', 'BLOCK', 1),
('External link', 'www.', 'HIGH', 1),
('External link', 'bit.ly', 'HIGH', 1),
('External link', 't.cn', 'HIGH', 1),
('Contact info', 'wechat', 'HIGH', 1),
('Contact info', 'wechat id', 'HIGH', 1),
('Contact info', 'wx:', 'HIGH', 1),
('Contact info', 'vx', 'HIGH', 1),
('Contact info', 'telegram', 'HIGH', 1),
('Contact info', 'whatsapp', 'HIGH', 1),
('Contact info', 'qq', 'MEDIUM', 1),
('Contact info', '私聊', 'HIGH', 1),
('Contact info', '加微信', 'HIGH', 1),
('Spam', '买粉', 'BLOCK', 1),
('Spam', '刷粉', 'BLOCK', 1),
('Spam', '刷单', 'BLOCK', 1),
('Spam', '引流', 'HIGH', 1),
('Spam', '广告', 'MEDIUM', 1),
('Spam', '兼职', 'MEDIUM', 1),
('Spam', '稳赚', 'BLOCK', 1),
('Spam', '投资稳赚', 'BLOCK', 1),
('Spam', '免费领取', 'MEDIUM', 1),
('Abuse', '傻逼', 'BLOCK', 1),
('Abuse', '废物', 'BLOCK', 1),
('Abuse', '垃圾', 'HIGH', 1),
('Abuse', '去死', 'BLOCK', 1),
('Adult', '色情', 'BLOCK', 1),
('Adult', '赌博', 'BLOCK', 1),
('Adult', '博彩', 'BLOCK', 1),
('Adult', '下注', 'BLOCK', 1);
