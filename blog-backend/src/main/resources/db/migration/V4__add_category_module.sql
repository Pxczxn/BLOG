-- 功能：数据库迁移脚本。
CREATE TABLE IF NOT EXISTS category (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(120) NOT NULL UNIQUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category_created_at (created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 濡傛灉 category_id 鍒椾笉瀛樺湪锛屽垯娣诲姞
SET @column_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'article' 
    AND COLUMN_NAME = 'category_id'
);

SET @sql = IF(@column_exists = 0, 
    'ALTER TABLE article ADD COLUMN category_id BIGINT UNSIGNED NULL AFTER author_id', 
    'SELECT "Column category_id already exists" AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 濡傛灉 idx_article_category_id 绱㈠紩涓嶅瓨鍦紝鍒欏垱寤?SET @index_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.STATISTICS 
    WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'article' 
    AND INDEX_NAME = 'idx_article_category_id'
);

SET @sql = IF(@index_exists = 0, 
    'CREATE INDEX idx_article_category_id ON article(category_id)', 
    'SELECT "Index idx_article_category_id already exists" AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

