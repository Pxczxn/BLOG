CREATE TABLE IF NOT EXISTS moderation_keyword_rule (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(80) NOT NULL,
    keyword_value VARCHAR(120) NOT NULL UNIQUE,
    severity ENUM('LOW', 'MEDIUM', 'HIGH', 'BLOCK') NOT NULL DEFAULT 'MEDIUM',
    enabled TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_moderation_keyword_enabled (enabled, severity)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS moderation_task (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    content_type ENUM('POST', 'COMMENT') NOT NULL,
    content_id BIGINT UNSIGNED NOT NULL,
    submitted_by BIGINT UNSIGNED NULL,
    title_snapshot VARCHAR(220) NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED', 'CANCELED') NOT NULL DEFAULT 'PENDING',
    risk_level ENUM('LOW', 'MEDIUM', 'HIGH') NOT NULL DEFAULT 'LOW',
    hit_count INT NOT NULL DEFAULT 0,
    decision_note VARCHAR(500) NULL,
    reviewed_by BIGINT UNSIGNED NULL,
    submitted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reviewed_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_moderation_task_submitter
        FOREIGN KEY (submitted_by) REFERENCES community_user(id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_moderation_task_reviewer
        FOREIGN KEY (reviewed_by) REFERENCES admin_user(id) ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX idx_moderation_task_status_created (status, created_at),
    INDEX idx_moderation_task_content_status (content_type, content_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS moderation_rule_hit (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT UNSIGNED NOT NULL,
    rule_id BIGINT UNSIGNED NOT NULL,
    keyword_value VARCHAR(120) NOT NULL,
    snippet VARCHAR(255) NULL,
    severity ENUM('LOW', 'MEDIUM', 'HIGH', 'BLOCK') NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_moderation_rule_hit_task
        FOREIGN KEY (task_id) REFERENCES moderation_task(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_moderation_rule_hit_rule
        FOREIGN KEY (rule_id) REFERENCES moderation_keyword_rule(id) ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_moderation_rule_hit_task (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS content_report (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    content_type ENUM('POST', 'COMMENT') NOT NULL,
    content_id BIGINT UNSIGNED NOT NULL,
    reporter_user_id BIGINT UNSIGNED NOT NULL,
    reason ENUM('SPAM', 'ABUSE', 'COPYRIGHT', 'ILLEGAL', 'OTHER') NOT NULL DEFAULT 'OTHER',
    description VARCHAR(500) NULL,
    status ENUM('OPEN', 'RESOLVED', 'DISMISSED') NOT NULL DEFAULT 'OPEN',
    handle_action ENUM('NONE', 'HIDE_POST', 'REJECT_COMMENT') NOT NULL DEFAULT 'NONE',
    handle_note VARCHAR(500) NULL,
    handled_by BIGINT UNSIGNED NULL,
    handled_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_content_report_reporter
        FOREIGN KEY (reporter_user_id) REFERENCES community_user(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_content_report_handler
        FOREIGN KEY (handled_by) REFERENCES admin_user(id) ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX idx_content_report_status_created (status, created_at),
    INDEX idx_content_report_target (content_type, content_id),
    INDEX idx_content_report_reporter (reporter_user_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO moderation_keyword_rule (name, keyword_value, severity, enabled)
SELECT 'Ad keyword', 'buy followers', 'HIGH', 1
WHERE NOT EXISTS (SELECT 1 FROM moderation_keyword_rule WHERE keyword_value = 'buy followers');

INSERT INTO moderation_keyword_rule (name, keyword_value, severity, enabled)
SELECT 'Ad keyword', 'contact me on telegram', 'HIGH', 1
WHERE NOT EXISTS (SELECT 1 FROM moderation_keyword_rule WHERE keyword_value = 'contact me on telegram');

INSERT INTO moderation_keyword_rule (name, keyword_value, severity, enabled)
SELECT 'Fraud keyword', 'investment guarantee', 'BLOCK', 1
WHERE NOT EXISTS (SELECT 1 FROM moderation_keyword_rule WHERE keyword_value = 'investment guarantee');

INSERT INTO moderation_keyword_rule (name, keyword_value, severity, enabled)
SELECT 'Abuse keyword', 'hate speech', 'BLOCK', 1
WHERE NOT EXISTS (SELECT 1 FROM moderation_keyword_rule WHERE keyword_value = 'hate speech');

INSERT INTO moderation_keyword_rule (name, keyword_value, severity, enabled)
SELECT 'Spam keyword', 'click this link', 'MEDIUM', 1
WHERE NOT EXISTS (SELECT 1 FROM moderation_keyword_rule WHERE keyword_value = 'click this link');


