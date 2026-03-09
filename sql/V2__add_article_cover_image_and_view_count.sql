-- 添加 cover_image 列
ALTER TABLE article
    ADD COLUMN cover_image VARCHAR(500) NULL AFTER content;

-- 添加 view_count 列
ALTER TABLE article
    ADD COLUMN view_count BIGINT NOT NULL DEFAULT 0 AFTER cover_image;
