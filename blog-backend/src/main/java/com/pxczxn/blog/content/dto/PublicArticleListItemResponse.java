/**
 * 公开文章列表项响应 DTO
 * <p>
 * 公开端文章列表中每条记录的摘要信息，包含分类摘要。
 */
package com.pxczxn.blog.content.dto;

import com.pxczxn.blog.content.entity.Article;

import java.time.LocalDateTime;

public record PublicArticleListItemResponse(
        /** 文章ID */
        Long id,
        /** 文章标题 */
        String title,
        /** URL友好标识 */
        String slug,
        /** 文章摘要 */
        String summary,
        /** 封面图片URL */
        String coverImage,
        /** 浏览次数 */
        Long viewCount,
        /** 所属分类摘要 */
        ArticleCategorySummaryResponse category,
        /** 创建时间 */
        LocalDateTime createdAt
) {
    /**
     * 从文章实体和分类摘要构建响应
     *
     * @param article  文章实体
     * @param category 分类摘要（可为null）
     * @return 公开文章列表项响应
     */
    public static PublicArticleListItemResponse from(Article article, ArticleCategorySummaryResponse category) {
        return new PublicArticleListItemResponse(
                article.getId(),
                article.getTitle(),
                article.getSlug(),
                article.getSummary(),
                article.getCoverImage(),
                article.getViewCount(),
                category,
                article.getCreatedAt()
        );
    }
}
