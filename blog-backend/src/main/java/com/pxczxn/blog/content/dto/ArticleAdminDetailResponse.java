/**
 * 文章管理端详情响应 DTO
 * <p>
 * 管理端获取文章详情时返回的完整信息，包含标签ID列表。
 */
package com.pxczxn.blog.content.dto;

import com.pxczxn.blog.content.entity.Article;
import com.pxczxn.blog.content.entity.ArticleStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ArticleAdminDetailResponse(
        /** 文章ID */
        Long id,
        /** 文章标题 */
        String title,
        /** URL友好标识 */
        String slug,
        /** 文章摘要 */
        String summary,
        /** 文章正文 */
        String content,
        /** 封面图片URL */
        String coverImage,
        /** 发布状态 */
        ArticleStatus status,
        /** 所属分类ID */
        Long categoryId,
        /** 关联标签ID列表 */
        List<Long> tagIds,
        /** 发布时间 */
        LocalDateTime publishedAt,
        /** 创建时间 */
        LocalDateTime createdAt,
        /** 更新时间 */
        LocalDateTime updatedAt
) {
    /**
     * 从文章实体和标签ID列表构建响应
     *
     * @param article 文章实体
     * @param tagIds  文章关联的标签ID列表
     * @return 管理端文章详情响应
     */
    public static ArticleAdminDetailResponse from(Article article, List<Long> tagIds) {
        return new ArticleAdminDetailResponse(
                article.getId(),
                article.getTitle(),
                article.getSlug(),
                article.getSummary(),
                article.getContent(),
                article.getCoverImage(),
                article.getStatus(),
                article.getCategoryId(),
                tagIds,
                article.getPublishedAt(),
                article.getCreatedAt(),
                article.getUpdatedAt()
        );
    }
}
