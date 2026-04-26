ALTER TABLE community_notification
    ADD COLUMN related_post_comment_id BIGINT UNSIGNED NULL AFTER related_comment_id,
    ADD CONSTRAINT fk_community_notification_post_comment
        FOREIGN KEY (related_post_comment_id) REFERENCES community_post_comment(id) ON DELETE SET NULL ON UPDATE CASCADE,
    ADD INDEX idx_community_notification_post_comment (related_post_comment_id);

ALTER TABLE moderation_task
    MODIFY COLUMN content_type ENUM('POST', 'COMMENT', 'POST_COMMENT') NOT NULL;

ALTER TABLE content_report
    MODIFY COLUMN content_type ENUM('POST', 'COMMENT', 'POST_COMMENT') NOT NULL;

ALTER TABLE community_post_comment
    MODIFY COLUMN status ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING';


