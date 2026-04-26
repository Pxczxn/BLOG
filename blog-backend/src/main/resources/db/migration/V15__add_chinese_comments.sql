-- 功能：数据库迁移脚本。
-- 为所有表和字段添加中文注释

-- ============================================
-- 管理员用户表
-- ============================================
ALTER TABLE admin_user COMMENT '管理员用户表';

ALTER TABLE admin_user
    MODIFY COLUMN id BIGINT UNSIGNED AUTO_INCREMENT COMMENT '主键ID',
    MODIFY COLUMN username VARCHAR(50) NOT NULL COMMENT '用户名',
    MODIFY COLUMN email VARCHAR(100) NOT NULL COMMENT '邮箱地址',
    MODIFY COLUMN password_hash VARCHAR(60) NOT NULL COMMENT '密码哈希值(BCrypt)',
    MODIFY COLUMN status ENUM('ACTIVE','BANNED') NOT NULL DEFAULT 'ACTIVE' COMMENT '账号状态: ACTIVE-正常, BANNED-封禁',
    MODIFY COLUMN role VARCHAR(20) NOT NULL DEFAULT 'ADMIN' COMMENT '角色',
    MODIFY COLUMN last_login_at DATETIME NULL COMMENT '最后登录时间',
    MODIFY COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    MODIFY COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

-- ============================================
-- 文章表
-- ============================================
ALTER TABLE article COMMENT '博客文章表';

ALTER TABLE article
    MODIFY COLUMN id BIGINT UNSIGNED AUTO_INCREMENT COMMENT '主键ID',
    MODIFY COLUMN title VARCHAR(200) NOT NULL COMMENT '文章标题',
    MODIFY COLUMN slug VARCHAR(200) NOT NULL COMMENT 'URL别名(唯一标识)',
    MODIFY COLUMN summary TEXT NULL COMMENT '文章摘要',
    MODIFY COLUMN content LONGTEXT NOT NULL COMMENT '文章内容(Markdown)',
    MODIFY COLUMN cover_image VARCHAR(500) NULL COMMENT '封面图片URL',
    MODIFY COLUMN view_count BIGINT NOT NULL DEFAULT 0 COMMENT '浏览次数',
    MODIFY COLUMN status ENUM('DRAFT','PUBLISHED') NOT NULL DEFAULT 'DRAFT' COMMENT '状态: DRAFT-草稿, PUBLISHED-已发布',
    MODIFY COLUMN published_at DATETIME NULL COMMENT '发布时间',
    MODIFY COLUMN author_id BIGINT UNSIGNED NOT NULL COMMENT '作者ID',
    MODIFY COLUMN category_id BIGINT UNSIGNED NULL COMMENT '分类ID',
    MODIFY COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    MODIFY COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

-- ============================================
-- 分类表
-- ============================================
ALTER TABLE category COMMENT '文章分类表';

ALTER TABLE category
    MODIFY COLUMN id BIGINT UNSIGNED AUTO_INCREMENT COMMENT '主键ID',
    MODIFY COLUMN name VARCHAR(100) NOT NULL COMMENT '分类名称',
    MODIFY COLUMN slug VARCHAR(120) NOT NULL COMMENT 'URL别名',
    MODIFY COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    MODIFY COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

-- ============================================
-- 标签表
-- ============================================
ALTER TABLE tag COMMENT '文章标签表';

ALTER TABLE tag
    MODIFY COLUMN id BIGINT UNSIGNED AUTO_INCREMENT COMMENT '主键ID',
    MODIFY COLUMN name VARCHAR(100) NOT NULL COMMENT '标签名称',
    MODIFY COLUMN slug VARCHAR(120) NOT NULL COMMENT 'URL别名',
    MODIFY COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    MODIFY COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

-- ============================================
-- 文章标签关联表
-- ============================================
ALTER TABLE article_tag COMMENT '文章-标签关联表';

ALTER TABLE article_tag
    MODIFY COLUMN article_id BIGINT UNSIGNED NOT NULL COMMENT '文章ID',
    MODIFY COLUMN tag_id BIGINT UNSIGNED NOT NULL COMMENT '标签ID',
    MODIFY COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';

-- ============================================
-- 文章评论表
-- ============================================
ALTER TABLE comment COMMENT '文章评论表';

ALTER TABLE comment
    MODIFY COLUMN id BIGINT UNSIGNED AUTO_INCREMENT COMMENT '主键ID',
    MODIFY COLUMN article_id BIGINT UNSIGNED NOT NULL COMMENT '文章ID',
    MODIFY COLUMN parent_id BIGINT UNSIGNED NULL COMMENT '父评论ID(用于回复)',
    MODIFY COLUMN community_user_id BIGINT UNSIGNED NULL COMMENT '社区用户ID(登录用户)',
    MODIFY COLUMN nickname VARCHAR(50) NOT NULL COMMENT '昵称(游客)',
    MODIFY COLUMN email VARCHAR(100) NULL COMMENT '邮箱(游客)',
    MODIFY COLUMN content VARCHAR(1000) NOT NULL COMMENT '评论内容',
    MODIFY COLUMN status ENUM('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING-待审核, APPROVED-已通过, REJECTED-已拒绝',
    MODIFY COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';

-- ============================================
-- 设备会话表
-- ============================================
ALTER TABLE device_session COMMENT '管理员设备会话表';

ALTER TABLE device_session
    MODIFY COLUMN id BIGINT AUTO_INCREMENT COMMENT '主键ID',
    MODIFY COLUMN admin_user_id BIGINT NOT NULL COMMENT '管理员用户ID',
    MODIFY COLUMN device_id VARCHAR(200) NOT NULL COMMENT '设备唯一标识',
    MODIFY COLUMN device_name VARCHAR(200) NULL COMMENT '设备名称',
    MODIFY COLUMN ip VARCHAR(45) NOT NULL COMMENT 'IP地址',
    MODIFY COLUMN user_agent TEXT NULL COMMENT '浏览器User-Agent',
    MODIFY COLUMN created_at DATETIME NOT NULL COMMENT '创建时间',
    MODIFY COLUMN last_seen_at DATETIME NOT NULL COMMENT '最后活跃时间',
    MODIFY COLUMN is_active TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否活跃';

-- ============================================
-- Token撤销表
-- ============================================
ALTER TABLE token_revoked COMMENT '管理员Token撤销记录表';

ALTER TABLE token_revoked
    MODIFY COLUMN id BIGINT AUTO_INCREMENT COMMENT '主键ID',
    MODIFY COLUMN jti VARCHAR(64) NOT NULL COMMENT 'JWT唯一标识',
    MODIFY COLUMN admin_user_id BIGINT NOT NULL COMMENT '管理员用户ID',
    MODIFY COLUMN revoked_at DATETIME NOT NULL COMMENT '撤销时间',
    MODIFY COLUMN expires_at DATETIME NOT NULL COMMENT 'Token过期时间',
    MODIFY COLUMN reason VARCHAR(50) NULL COMMENT '撤销原因';

-- ============================================
-- 社区用户表
-- ============================================
ALTER TABLE community_user COMMENT '社区用户表';

ALTER TABLE community_user
    MODIFY COLUMN id BIGINT UNSIGNED AUTO_INCREMENT COMMENT '主键ID',
    MODIFY COLUMN username VARCHAR(50) NOT NULL COMMENT '用户名',
    MODIFY COLUMN email VARCHAR(100) NOT NULL COMMENT '邮箱地址',
    MODIFY COLUMN password_hash VARCHAR(60) NOT NULL COMMENT '密码哈希值(BCrypt)',
    MODIFY COLUMN display_name VARCHAR(80) NOT NULL COMMENT '显示名称',
    MODIFY COLUMN avatar VARCHAR(255) NULL COMMENT '头像URL',
    MODIFY COLUMN bio VARCHAR(500) NULL COMMENT '个人简介',
    MODIFY COLUMN website VARCHAR(255) NULL COMMENT '个人网站',
    MODIFY COLUMN status ENUM('ACTIVE', 'PENDING', 'BANNED') NOT NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-正常, PENDING-待激活, BANNED-封禁',
    MODIFY COLUMN role VARCHAR(30) NOT NULL DEFAULT 'USER' COMMENT '角色: USER-普通用户, MODERATOR-版主',
    MODIFY COLUMN last_login_at DATETIME NULL COMMENT '最后登录时间',
    MODIFY COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    MODIFY COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

-- ============================================
-- 社区Token撤销表
-- ============================================
ALTER TABLE community_token_revoked COMMENT '社区用户Token撤销记录表';

ALTER TABLE community_token_revoked
    MODIFY COLUMN id BIGINT UNSIGNED AUTO_INCREMENT COMMENT '主键ID',
    MODIFY COLUMN jti VARCHAR(64) NOT NULL COMMENT 'JWT唯一标识',
    MODIFY COLUMN community_user_id BIGINT UNSIGNED NOT NULL COMMENT '社区用户ID',
    MODIFY COLUMN revoked_at DATETIME NOT NULL COMMENT '撤销时间',
    MODIFY COLUMN expires_at DATETIME NOT NULL COMMENT 'Token过期时间',
    MODIFY COLUMN reason VARCHAR(50) NULL COMMENT '撤销原因';

-- ============================================
-- 社区节点表
-- ============================================
ALTER TABLE community_node COMMENT '社区节点/板块表';

ALTER TABLE community_node
    MODIFY COLUMN id BIGINT UNSIGNED AUTO_INCREMENT COMMENT '主键ID',
    MODIFY COLUMN name VARCHAR(80) NOT NULL COMMENT '节点名称',
    MODIFY COLUMN slug VARCHAR(80) NOT NULL COMMENT 'URL别名',
    MODIFY COLUMN description VARCHAR(255) NULL COMMENT '节点描述',
    MODIFY COLUMN icon VARCHAR(50) NULL COMMENT '图标名称',
    MODIFY COLUMN sort_order INT NOT NULL DEFAULT 0 COMMENT '排序权重(越大越靠前)',
    MODIFY COLUMN status ENUM('ACTIVE', 'HIDDEN') NOT NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-显示, HIDDEN-隐藏',
    MODIFY COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    MODIFY COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

-- ============================================
-- 社区帖子表
-- ============================================
ALTER TABLE community_post COMMENT '社区帖子表';

ALTER TABLE community_post
    MODIFY COLUMN id BIGINT UNSIGNED AUTO_INCREMENT COMMENT '主键ID',
    MODIFY COLUMN node_id BIGINT UNSIGNED NOT NULL COMMENT '所属节点ID',
    MODIFY COLUMN author_id BIGINT UNSIGNED NOT NULL COMMENT '作者ID',
    MODIFY COLUMN title VARCHAR(200) NOT NULL COMMENT '帖子标题',
    MODIFY COLUMN slug VARCHAR(220) NOT NULL COMMENT 'URL别名',
    MODIFY COLUMN summary VARCHAR(500) NULL COMMENT '帖子摘要',
    MODIFY COLUMN content LONGTEXT NOT NULL COMMENT '帖子内容(Markdown)',
    MODIFY COLUMN status ENUM('DRAFT', 'PENDING_REVIEW', 'PUBLISHED', 'REJECTED', 'HIDDEN') NOT NULL DEFAULT 'DRAFT' COMMENT '状态: DRAFT-草稿, PENDING_REVIEW-待审核, PUBLISHED-已发布, REJECTED-已拒绝, HIDDEN-已隐藏',
    MODIFY COLUMN published_at DATETIME NULL COMMENT '发布时间',
    MODIFY COLUMN last_edited_at DATETIME NULL COMMENT '最后编辑时间',
    MODIFY COLUMN view_count BIGINT NOT NULL DEFAULT 0 COMMENT '浏览次数',
    MODIFY COLUMN rejection_reason VARCHAR(500) NULL COMMENT '拒绝原因',
    MODIFY COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    MODIFY COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

-- ============================================
-- 社区帖子评论表
-- ============================================
ALTER TABLE community_post_comment COMMENT '社区帖子评论表';

ALTER TABLE community_post_comment
    MODIFY COLUMN id BIGINT UNSIGNED AUTO_INCREMENT COMMENT '主键ID',
    MODIFY COLUMN post_id BIGINT UNSIGNED NOT NULL COMMENT '帖子ID',
    MODIFY COLUMN parent_id BIGINT UNSIGNED NULL COMMENT '父评论ID(用于回复)',
    MODIFY COLUMN community_user_id BIGINT UNSIGNED NOT NULL COMMENT '社区用户ID',
    MODIFY COLUMN nickname VARCHAR(80) NOT NULL COMMENT '昵称',
    MODIFY COLUMN content VARCHAR(1000) NOT NULL COMMENT '评论内容',
    MODIFY COLUMN status ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'APPROVED' COMMENT '状态: PENDING-待审核, APPROVED-已通过, REJECTED-已拒绝',
    MODIFY COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    MODIFY COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

-- ============================================
-- 审核关键词规则表
-- ============================================
ALTER TABLE moderation_keyword_rule COMMENT '内容审核关键词规则表';

ALTER TABLE moderation_keyword_rule
    MODIFY COLUMN id BIGINT UNSIGNED AUTO_INCREMENT COMMENT '主键ID',
    MODIFY COLUMN name VARCHAR(80) NOT NULL COMMENT '规则名称',
    MODIFY COLUMN keyword_value VARCHAR(120) NOT NULL COMMENT '关键词',
    MODIFY COLUMN severity ENUM('LOW', 'MEDIUM', 'HIGH', 'BLOCK') NOT NULL DEFAULT 'MEDIUM' COMMENT '严重程度: LOW-低, MEDIUM-中, HIGH-高, BLOCK-自动拦截',
    MODIFY COLUMN enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
    MODIFY COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    MODIFY COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

-- ============================================
-- 审核任务表
-- ============================================
ALTER TABLE moderation_task COMMENT '内容审核任务表';

ALTER TABLE moderation_task
    MODIFY COLUMN id BIGINT UNSIGNED AUTO_INCREMENT COMMENT '主键ID',
    MODIFY COLUMN content_type ENUM('POST', 'COMMENT') NOT NULL COMMENT '内容类型: POST-帖子, COMMENT-评论',
    MODIFY COLUMN content_id BIGINT UNSIGNED NOT NULL COMMENT '内容ID',
    MODIFY COLUMN submitted_by BIGINT UNSIGNED NULL COMMENT '提交者ID',
    MODIFY COLUMN title_snapshot VARCHAR(220) NULL COMMENT '标题快照',
    MODIFY COLUMN status ENUM('PENDING', 'APPROVED', 'REJECTED', 'CANCELED') NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING-待审核, APPROVED-已通过, REJECTED-已拒绝, CANCELED-已取消',
    MODIFY COLUMN risk_level ENUM('LOW', 'MEDIUM', 'HIGH') NOT NULL DEFAULT 'LOW' COMMENT '风险等级: LOW-低, MEDIUM-中, HIGH-高',
    MODIFY COLUMN hit_count INT NOT NULL DEFAULT 0 COMMENT '命中规则数',
    MODIFY COLUMN decision_note VARCHAR(500) NULL COMMENT '审核备注',
    MODIFY COLUMN reviewed_by BIGINT UNSIGNED NULL COMMENT '审核人ID',
    MODIFY COLUMN submitted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    MODIFY COLUMN reviewed_at DATETIME NULL COMMENT '审核时间',
    MODIFY COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    MODIFY COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

-- ============================================
-- 审核规则命中记录表
-- ============================================
ALTER TABLE moderation_rule_hit COMMENT '审核规则命中记录表';

ALTER TABLE moderation_rule_hit
    MODIFY COLUMN id BIGINT UNSIGNED AUTO_INCREMENT COMMENT '主键ID',
    MODIFY COLUMN task_id BIGINT UNSIGNED NOT NULL COMMENT '审核任务ID',
    MODIFY COLUMN rule_id BIGINT UNSIGNED NOT NULL COMMENT '规则ID',
    MODIFY COLUMN keyword_value VARCHAR(120) NOT NULL COMMENT '关键词',
    MODIFY COLUMN snippet VARCHAR(255) NULL COMMENT '命中上下文片段',
    MODIFY COLUMN severity ENUM('LOW', 'MEDIUM', 'HIGH', 'BLOCK') NOT NULL COMMENT '严重程度',
    MODIFY COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';

-- ============================================
-- 内容举报表
-- ============================================
ALTER TABLE content_report COMMENT '内容举报表';

ALTER TABLE content_report
    MODIFY COLUMN id BIGINT UNSIGNED AUTO_INCREMENT COMMENT '主键ID',
    MODIFY COLUMN content_type ENUM('POST', 'COMMENT') NOT NULL COMMENT '内容类型: POST-帖子, COMMENT-评论',
    MODIFY COLUMN content_id BIGINT UNSIGNED NOT NULL COMMENT '内容ID',
    MODIFY COLUMN reporter_user_id BIGINT UNSIGNED NOT NULL COMMENT '举报人ID',
    MODIFY COLUMN reason ENUM('SPAM', 'ABUSE', 'COPYRIGHT', 'ILLEGAL', 'OTHER') NOT NULL DEFAULT 'OTHER' COMMENT '举报原因: SPAM-垃圾信息, ABUSE-辱骂攻击, COPYRIGHT-侵权, ILLEGAL-违法违规, OTHER-其他',
    MODIFY COLUMN description VARCHAR(500) NULL COMMENT '举报描述',
    MODIFY COLUMN status ENUM('OPEN', 'RESOLVED', 'DISMISSED') NOT NULL DEFAULT 'OPEN' COMMENT '状态: OPEN-待处理, RESOLVED-已解决, DISMISSED-已驳回',
    MODIFY COLUMN handle_action ENUM('NONE', 'HIDE_POST', 'REJECT_COMMENT') NOT NULL DEFAULT 'NONE' COMMENT '处理动作: NONE-无, HIDE_POST-隐藏帖子, REJECT_COMMENT-拒绝评论',
    MODIFY COLUMN handle_note VARCHAR(500) NULL COMMENT '处理备注',
    MODIFY COLUMN handled_by BIGINT UNSIGNED NULL COMMENT '处理人ID',
    MODIFY COLUMN handled_at DATETIME NULL COMMENT '处理时间',
    MODIFY COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    MODIFY COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

-- ============================================
-- 帖子点赞表
-- ============================================
ALTER TABLE community_post_like COMMENT '社区帖子点赞表';

ALTER TABLE community_post_like
    MODIFY COLUMN id BIGINT UNSIGNED AUTO_INCREMENT COMMENT '主键ID',
    MODIFY COLUMN post_id BIGINT UNSIGNED NOT NULL COMMENT '帖子ID',
    MODIFY COLUMN user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    MODIFY COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间';

-- ============================================
-- 帖子收藏表
-- ============================================
ALTER TABLE community_post_favorite COMMENT '社区帖子收藏表';

ALTER TABLE community_post_favorite
    MODIFY COLUMN id BIGINT UNSIGNED AUTO_INCREMENT COMMENT '主键ID',
    MODIFY COLUMN post_id BIGINT UNSIGNED NOT NULL COMMENT '帖子ID',
    MODIFY COLUMN user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    MODIFY COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间';

-- ============================================
-- 用户关注表
-- ============================================
ALTER TABLE community_user_follow COMMENT '社区用户关注表';

ALTER TABLE community_user_follow
    MODIFY COLUMN id BIGINT UNSIGNED AUTO_INCREMENT COMMENT '主键ID',
    MODIFY COLUMN follower_id BIGINT UNSIGNED NOT NULL COMMENT '关注者ID',
    MODIFY COLUMN following_id BIGINT UNSIGNED NOT NULL COMMENT '被关注者ID',
    MODIFY COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间';

-- ============================================
-- 社区通知表
-- ============================================
ALTER TABLE community_notification COMMENT '社区通知表';

ALTER TABLE community_notification
    MODIFY COLUMN id BIGINT UNSIGNED AUTO_INCREMENT COMMENT '主键ID',
    MODIFY COLUMN user_id BIGINT UNSIGNED NOT NULL COMMENT '接收用户ID',
    MODIFY COLUMN actor_user_id BIGINT UNSIGNED NULL COMMENT '触发用户ID',
    MODIFY COLUMN type ENUM('POST_LIKED', 'POST_FAVORITED', 'USER_FOLLOWED', 'SYSTEM') NOT NULL COMMENT '通知类型: POST_LIKED-点赞, POST_FAVORITED-收藏, USER_FOLLOWED-关注, SYSTEM-系统',
    MODIFY COLUMN title VARCHAR(120) NOT NULL COMMENT '通知标题',
    MODIFY COLUMN content VARCHAR(500) NULL COMMENT '通知内容',
    MODIFY COLUMN related_post_id BIGINT UNSIGNED NULL COMMENT '关联帖子ID',
    MODIFY COLUMN related_comment_id BIGINT UNSIGNED NULL COMMENT '关联评论ID',
    MODIFY COLUMN is_read TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已读',
    MODIFY COLUMN read_at DATETIME NULL COMMENT '阅读时间',
    MODIFY COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';
