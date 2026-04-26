CREATE TABLE IF NOT EXISTS community_post_tag (
    post_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (post_id, tag_id),
    CONSTRAINT fk_community_post_tag_post FOREIGN KEY (post_id) REFERENCES community_post(id) ON DELETE CASCADE,
    CONSTRAINT fk_community_post_tag_tag FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE RESTRICT
);

CREATE INDEX idx_community_post_tag_tag_id ON community_post_tag(tag_id);
