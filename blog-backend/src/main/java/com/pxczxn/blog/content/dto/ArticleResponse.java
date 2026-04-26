/**
 * 文章通用响应 DTO
 * <p>
 * 文章的完整信息响应，包含作者信息，用于管理端和通用场景。
 */
package com.pxczxn.blog.content.dto;

import com.pxczxn.blog.content.entity.Article;
import com.pxczxn.blog.content.entity.ArticleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleResponse {

    /** 文章ID */
    private Long id;
    /** 文章标题 */
    private String title;
    /** URL友好标识 */
    private String slug;
    /** 文章摘要 */
    private String summary;
    /** 文章正文 */
    private String content;
    /** 封面图片URL */
    private String coverImage;
    /** 浏览次数 */
    private Long viewCount;
    /** 发布状态 */
    private ArticleStatus status;
    /** 发布时间 */
    private LocalDateTime publishedAt;
    /** 作者ID */
    private Long authorId;
    /** 作者名称 */
    private String authorName;
    /** 创建时间 */
    private LocalDateTime createdAt;
    /** 更新时间 */
    private LocalDateTime updatedAt;

    /**
     * 从文章实体和作者名称构建响应
     *
     * @param article    文章实体
     * @param authorName 作者名称（可为null）
     * @return 文章响应
     */
    public static ArticleResponse from(Article article, String authorName) {
        return ArticleResponse.builder()
                .id(article.getId())
                .title(article.getTitle())
                .slug(article.getSlug())
                .summary(article.getSummary())
                .content(article.getContent())
                .coverImage(article.getCoverImage())
                .viewCount(article.getViewCount())
                .status(article.getStatus())
                .publishedAt(article.getPublishedAt())
                .authorId(article.getAuthor() != null ? article.getAuthor().getId() : null)
                .authorName(authorName)
                .createdAt(article.getCreatedAt())
                .updatedAt(article.getUpdatedAt())
                .build();
    }

    /**
     * 从文章实体构建响应（不含作者名称）
     *
     * @param article 文章实体
     * @return 文章响应
     */
    public static ArticleResponse from(Article article) {
        return from(article, null);
    }
}
