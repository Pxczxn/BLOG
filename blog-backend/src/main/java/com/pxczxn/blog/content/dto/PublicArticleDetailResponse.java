/**
 * 公开文章详情响应 DTO
 * <p>
 * 公开端获取已发布文章详情时返回的信息，包含分类摘要和标签列表。
 */
package com.pxczxn.blog.content.dto;

import com.pxczxn.blog.content.entity.Article;

import java.time.LocalDateTime;
import java.util.List;

public record PublicArticleDetailResponse(
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
        /** 浏览次数 */
        Long viewCount,
        /** 所属分类摘要 */
        ArticleCategorySummaryResponse category,
        /** 关联标签摘要列表 */
        List<ArticleTagSummaryResponse> tags,
        /** 创建时间 */
        LocalDateTime createdAt
) {
    /**
     * 从文章实体、分类摘要和标签列表构建响应
     *
     * @param article  文章实体
     * @param category 分类摘要（可为null）
     * @param tags     标签摘要列表
     * @return 公开文章详情响应
     */
    public static PublicArticleDetailResponse from(
            Article article,
            ArticleCategorySummaryResponse category,
            List<ArticleTagSummaryResponse> tags
    ) {
        return new PublicArticleDetailResponse(
                article.getId(),
                article.getTitle(),
                article.getSlug(),
                article.getSummary(),
                article.getContent(),
                article.getCoverImage(),
                article.getViewCount(),
                category,
                tags,
                article.getCreatedAt()
        );
    }
}
