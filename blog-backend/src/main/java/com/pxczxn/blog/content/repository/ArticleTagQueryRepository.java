package com.pxczxn.blog.content.repository;

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
public class ArticleTagQueryRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Long> findTagIdsByArticleId(Long articleId) {
        String sql = "SELECT tag_id FROM article_tag WHERE article_id = ? ORDER BY tag_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("tag_id"), articleId);
    }

    public Map<Long, List<Long>> findTagIdsByArticleIds(List<Long> articleIds) {
        Map<Long, List<Long>> result = new HashMap<>();
        if (articleIds == null || articleIds.isEmpty()) {
            return result;
        }

        String placeholders = String.join(",", java.util.Collections.nCopies(articleIds.size(), "?"));
        String sql = "SELECT article_id, tag_id FROM article_tag WHERE article_id IN (" + placeholders + ") ORDER BY article_id, tag_id";
        jdbcTemplate.query(sql, rs -> {
            Long articleId = rs.getLong("article_id");
            Long tagId = rs.getLong("tag_id");
            result.computeIfAbsent(articleId, key -> new ArrayList<>()).add(tagId);
        }, articleIds.toArray());
        return result;
    }

    public List<Long> findArticleIdsByTagId(Long tagId) {
        String sql = "SELECT article_id FROM article_tag WHERE tag_id = ? ORDER BY article_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("article_id"), tagId);
    }

    public void deleteByTagId(Long tagId) {
        jdbcTemplate.update("DELETE FROM article_tag WHERE tag_id = ?", tagId);
    }

    public void replaceTagsForArticle(Long articleId, List<Long> tagIds) {
        jdbcTemplate.update("DELETE FROM article_tag WHERE article_id = ?", articleId);
        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }

        jdbcTemplate.batchUpdate(
                "INSERT INTO article_tag (article_id, tag_id) VALUES (?, ?)",
                tagIds,
                tagIds.size(),
                (PreparedStatement ps, Long tagId) -> bindArticleTag(ps, articleId, tagId)
        );
    }

    private void bindArticleTag(PreparedStatement ps, Long articleId, Long tagId) throws SQLException {
        ps.setLong(1, articleId);
        if (tagId == null) {
            ps.setNull(2, Types.BIGINT);
            return;
        }
        ps.setLong(2, tagId);
    }
}
