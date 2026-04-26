CREATE TABLE IF NOT EXISTS community_user (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(60) NOT NULL,
    display_name VARCHAR(80) NOT NULL,
    avatar VARCHAR(255) NULL,
    bio VARCHAR(500) NULL,
    website VARCHAR(255) NULL,
    status ENUM('ACTIVE', 'PENDING', 'BANNED') NOT NULL DEFAULT 'ACTIVE',
    role VARCHAR(30) NOT NULL DEFAULT 'USER',
    last_login_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_community_user_username (username),
    INDEX idx_community_user_email (email),
    INDEX idx_community_user_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS community_token_revoked (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    jti VARCHAR(64) NOT NULL UNIQUE,
    community_user_id BIGINT UNSIGNED NOT NULL,
    revoked_at DATETIME NOT NULL,
    expires_at DATETIME NOT NULL,
    reason VARCHAR(50) NULL,
    CONSTRAINT fk_community_token_revoked_user
        FOREIGN KEY (community_user_id) REFERENCES community_user(id) ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_community_token_revoked_user_id (community_user_id),
    INDEX idx_community_token_revoked_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

ALTER TABLE comment
    ADD COLUMN community_user_id BIGINT UNSIGNED NULL AFTER parent_id,
    ADD INDEX idx_comment_community_user_id (community_user_id),
    ADD CONSTRAINT fk_comment_community_user
        FOREIGN KEY (community_user_id) REFERENCES community_user(id) ON DELETE SET NULL ON UPDATE CASCADE;


