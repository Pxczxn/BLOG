-- 功能：数据库迁移脚本。
CREATE TABLE IF NOT EXISTS community_node (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(80) NOT NULL UNIQUE,
    slug VARCHAR(80) NOT NULL UNIQUE,
    description VARCHAR(255) NULL,
    icon VARCHAR(50) NULL,
    sort_order INT NOT NULL DEFAULT 0,
    status ENUM('ACTIVE', 'HIDDEN') NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_community_node_sort (sort_order, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS community_post (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    node_id BIGINT UNSIGNED NOT NULL,
    author_id BIGINT UNSIGNED NOT NULL,
    title VARCHAR(200) NOT NULL,
    slug VARCHAR(220) NOT NULL UNIQUE,
    summary VARCHAR(500) NULL,
    content LONGTEXT NOT NULL,
    status ENUM('DRAFT', 'PENDING_REVIEW', 'PUBLISHED', 'REJECTED', 'HIDDEN') NOT NULL DEFAULT 'DRAFT',
    published_at DATETIME NULL,
    last_edited_at DATETIME NULL,
    view_count BIGINT NOT NULL DEFAULT 0,
    rejection_reason VARCHAR(500) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_community_post_node
        FOREIGN KEY (node_id) REFERENCES community_node(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_community_post_author
        FOREIGN KEY (author_id) REFERENCES community_user(id) ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_community_post_node_status (node_id, status, published_at),
    INDEX idx_community_post_author_created (author_id, created_at),
    INDEX idx_community_post_status_created (status, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO community_node (name, slug, description, icon, sort_order, status)
SELECT '寮€鍙戞棩甯?, 'dev-log', '璁板綍浠ｇ爜銆佸伐鍏峰拰瀹炶返鐨勬棩甯稿垎浜€?, 'terminal', 10, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM community_node WHERE slug = 'dev-log');

INSERT INTO community_node (name, slug, description, icon, sort_order, status)
SELECT '闂姹傚姪', 'q-and-a', '鎻愰棶銆佹帓闅滃拰浜掔浉瑙ｇ瓟鐨勮璁哄尯銆?, 'life-buoy', 20, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM community_node WHERE slug = 'q-and-a');

INSERT INTO community_node (name, slug, description, icon, sort_order, status)
SELECT '浣滃搧灞曠ず', 'showcase', '灞曠ず鑷繁鐨勯」鐩€侀〉闈㈠拰鐏垫劅銆?, 'sparkles', 30, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM community_node WHERE slug = 'showcase');

