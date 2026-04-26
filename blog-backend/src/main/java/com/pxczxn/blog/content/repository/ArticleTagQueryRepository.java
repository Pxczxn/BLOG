/**
 * 文章标签关联查询仓库
 * <p>
 * 使用JdbcTemplate直接操作article_tag关联表，
 * 提供文章与标签的多对多关联查询和更新功能。
 */
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

    /** JDBC模板，用于执行原生SQL操作 */
    private final JdbcTemplate jdbcTemplate;

    /**
     * 根据文章ID查询关联的标签ID列表
     *
     * @param articleId 文章ID
     * @return 该文章关联的标签ID列表，按标签ID排序
     */
    public List<Long> findTagIdsByArticleId(Long articleId) {
        String sql = "SELECT tag_id FROM article_tag WHERE article_id = ? ORDER BY tag_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("tag_id"), articleId);
    }

    /**
     * 根据多个文章ID批量查询关联的标签ID
     *
     * @param articleIds 文章ID列表
     * @return 以文章ID为键、对应标签ID列表为值的映射
     */
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

    /**
     * 根据标签ID查询关联的文章ID列表
     *
     * @param tagId 标签ID
     * @return 该标签关联的文章ID列表，按文章ID排序
     */
    public List<Long> findArticleIdsByTagId(Long tagId) {
        String sql = "SELECT article_id FROM article_tag WHERE tag_id = ? ORDER BY article_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("article_id"), tagId);
    }

    /**
     * 根据标签ID删除所有文章-标签关联记录
     *
     * @param tagId 标签ID
     */
    public void deleteByTagId(Long tagId) {
        jdbcTemplate.update("DELETE FROM article_tag WHERE tag_id = ?", tagId);
    }

    /**
     * 替换文章的标签关联
     * <p>
     * 先删除该文章的所有标签关联，再批量插入新的标签关联。
     *
     * @param articleId 文章ID
     * @param tagIds    新的标签ID列表，为空则仅删除不插入
     */
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

    /**
     * 绑定文章-标签关联的PreparedStatement参数
     *
     * @param ps        PreparedStatement
     * @param articleId 文章ID
     * @param tagId     标签ID
     * @throws SQLException SQL异常
     */
    private void bindArticleTag(PreparedStatement ps, Long articleId, Long tagId) throws SQLException {
        ps.setLong(1, articleId);
        if (tagId == null) {
            ps.setNull(2, Types.BIGINT);
            return;
        }
        ps.setLong(2, tagId);
    }
}
