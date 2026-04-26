ALTER TABLE article
    ADD COLUMN cover_image VARCHAR(500) NULL AFTER content;

ALTER TABLE article
    ADD COLUMN view_count BIGINT NOT NULL DEFAULT 0 AFTER cover_image;

