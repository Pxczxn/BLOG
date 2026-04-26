-- 功能：数据库迁移脚本。
CREATE TABLE IF NOT EXISTS community_post_comment (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT UNSIGNED NOT NULL,
    parent_id BIGINT UNSIGNED NULL,
    community_user_id BIGINT UNSIGNED NOT NULL,
    nickname VARCHAR(80) NOT NULL,
    content VARCHAR(1000) NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'APPROVED',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_community_post_comment_post
        FOREIGN KEY (post_id) REFERENCES community_post(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_community_post_comment_parent
        FOREIGN KEY (parent_id) REFERENCES community_post_comment(id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_community_post_comment_user
        FOREIGN KEY (community_user_id) REFERENCES community_user(id) ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_community_post_comment_post_status_created (post_id, status, created_at),
    INDEX idx_community_post_comment_user_created (community_user_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

