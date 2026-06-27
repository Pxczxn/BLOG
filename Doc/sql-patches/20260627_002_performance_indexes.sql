-- Performance indexes for common public list queries.
-- Safe to run repeatedly on MySQL 8+.

SET @index_exists := (
    SELECT COUNT(1)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'article'
      AND index_name = 'idx_article_status_created_at'
);
SET @ddl := IF(
    @index_exists = 0,
    'CREATE INDEX idx_article_status_created_at ON article (status, created_at DESC)',
    'SELECT 1'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @index_exists := (
    SELECT COUNT(1)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'community_node'
      AND index_name = 'idx_community_node_status_sort_created'
);
SET @ddl := IF(
    @index_exists = 0,
    'CREATE INDEX idx_community_node_status_sort_created ON community_node (status, sort_order, created_at)',
    'SELECT 1'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @index_exists := (
    SELECT COUNT(1)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'community_post'
      AND index_name = 'idx_community_post_node_status_published_created'
);
SET @ddl := IF(
    @index_exists = 0,
    'CREATE INDEX idx_community_post_node_status_published_created ON community_post (node_id, status, published_at DESC, created_at DESC)',
    'SELECT 1'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @index_exists := (
    SELECT COUNT(1)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'community_post'
      AND index_name = 'idx_community_post_status_published_created'
);
SET @ddl := IF(
    @index_exists = 0,
    'CREATE INDEX idx_community_post_status_published_created ON community_post (status, published_at DESC, created_at DESC)',
    'SELECT 1'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
