package com.pxczxn.blog.community.post.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class CommunityPostTagQueryRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Long> findTagIdsByPostId(Long postId) {
        String sql = "SELECT tag_id FROM community_post_tag WHERE post_id = ? ORDER BY tag_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("tag_id"), postId);
    }

    public Map<Long, List<Long>> findTagIdsByPostIds(List<Long> postIds) {
        Map<Long, List<Long>> result = new HashMap<>();
        if (postIds == null || postIds.isEmpty()) {
            return result;
        }

        String placeholders = String.join(",", java.util.Collections.nCopies(postIds.size(), "?"));
        String sql = "SELECT post_id, tag_id FROM community_post_tag WHERE post_id IN (" + placeholders + ") ORDER BY post_id, tag_id";
        jdbcTemplate.query(sql, rs -> {
            Long postId = rs.getLong("post_id");
            Long tagId = rs.getLong("tag_id");
            result.computeIfAbsent(postId, key -> new ArrayList<>()).add(tagId);
        }, postIds.toArray());
        return result;
    }

    public void deleteByTagId(Long tagId) {
        jdbcTemplate.update("DELETE FROM community_post_tag WHERE tag_id = ?", tagId);
    }

    public void replaceTagsForPost(Long postId, List<Long> tagIds) {
        jdbcTemplate.update("DELETE FROM community_post_tag WHERE post_id = ?", postId);
        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }

        jdbcTemplate.batchUpdate(
                "INSERT INTO community_post_tag (post_id, tag_id) VALUES (?, ?)",
                tagIds,
                tagIds.size(),
                (PreparedStatement ps, Long tagId) -> bindPostTag(ps, postId, tagId)
        );
    }

    private void bindPostTag(PreparedStatement ps, Long postId, Long tagId) throws SQLException {
        ps.setLong(1, postId);
        if (tagId == null) {
            ps.setNull(2, Types.BIGINT);
            return;
        }
        ps.setLong(2, tagId);
    }
}
