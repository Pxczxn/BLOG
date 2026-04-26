CREATE TABLE IF NOT EXISTS community_post_like (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT UNSIGNED NOT NULL,
    user_id BIGINT UNSIGNED NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_community_post_like_post
        FOREIGN KEY (post_id) REFERENCES community_post(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_community_post_like_user
        FOREIGN KEY (user_id) REFERENCES community_user(id) ON DELETE CASCADE ON UPDATE CASCADE,
    UNIQUE KEY uk_community_post_like (post_id, user_id),
    INDEX idx_community_post_like_user_created (user_id, created_at),
    INDEX idx_community_post_like_post_created (post_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS community_post_favorite (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT UNSIGNED NOT NULL,
    user_id BIGINT UNSIGNED NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_community_post_favorite_post
        FOREIGN KEY (post_id) REFERENCES community_post(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_community_post_favorite_user
        FOREIGN KEY (user_id) REFERENCES community_user(id) ON DELETE CASCADE ON UPDATE CASCADE,
    UNIQUE KEY uk_community_post_favorite (post_id, user_id),
    INDEX idx_community_post_favorite_user_created (user_id, created_at),
    INDEX idx_community_post_favorite_post_created (post_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS community_user_follow (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    follower_id BIGINT UNSIGNED NOT NULL,
    following_id BIGINT UNSIGNED NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_community_user_follow_follower
        FOREIGN KEY (follower_id) REFERENCES community_user(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_community_user_follow_following
        FOREIGN KEY (following_id) REFERENCES community_user(id) ON DELETE CASCADE ON UPDATE CASCADE,
    UNIQUE KEY uk_community_user_follow (follower_id, following_id),
    INDEX idx_community_user_follow_follower_created (follower_id, created_at),
    INDEX idx_community_user_follow_following_created (following_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS community_notification (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    actor_user_id BIGINT UNSIGNED NULL,
    type ENUM('POST_LIKED', 'POST_FAVORITED', 'USER_FOLLOWED', 'SYSTEM') NOT NULL,
    title VARCHAR(120) NOT NULL,
    content VARCHAR(500) NULL,
    related_post_id BIGINT UNSIGNED NULL,
    related_comment_id BIGINT UNSIGNED NULL,
    is_read TINYINT(1) NOT NULL DEFAULT 0,
    read_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_community_notification_user
        FOREIGN KEY (user_id) REFERENCES community_user(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_community_notification_actor_user
        FOREIGN KEY (actor_user_id) REFERENCES community_user(id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_community_notification_post
        FOREIGN KEY (related_post_id) REFERENCES community_post(id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_community_notification_comment
        FOREIGN KEY (related_comment_id) REFERENCES comment(id) ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX idx_community_notification_user_unread (user_id, is_read, created_at),
    INDEX idx_community_notification_user_created (user_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


