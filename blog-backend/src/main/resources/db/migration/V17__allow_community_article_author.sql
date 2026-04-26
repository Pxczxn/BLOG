ALTER TABLE article MODIFY COLUMN author_id BIGINT NULL;

ALTER TABLE article ADD COLUMN community_author_id BIGINT NULL;

CREATE INDEX idx_article_community_author_id ON article(community_author_id);
