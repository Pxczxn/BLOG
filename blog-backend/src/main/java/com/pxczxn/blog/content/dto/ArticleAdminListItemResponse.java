/**
 * 文章管理端列表项响应 DTO
 * <p>
 * 管理端文章列表中每条记录的摘要信息，包含分类名称。
 */
package com.pxczxn.blog.content.dto;

import com.pxczxn.blog.content.entity.Article;
import com.pxczxn.blog.content.entity.ArticleStatus;

import java.time.LocalDateTime;

public record ArticleAdminListItemResponse(
        /** 文章ID */
        Long id,
        /** 文章标题 */
        String title,
        /** 所属分类名称 */
        String categoryName,
        /** 发布状态 */
        ArticleStatus status,
        /** 创建时间 */
        LocalDateTime createdAt,
        /** 更新时间 */
        LocalDateTime updatedAt
) {
    /**
     * 从文章实体和分类名称构建响应
     *
     * @param article      文章实体
     * @param categoryName 分类名称（可为null）
     * @return 管理端文章列表项响应
     */
    public static ArticleAdminListItemResponse from(Article article, String categoryName) {
        return new ArticleAdminListItemResponse(
                article.getId(),
                article.getTitle(),
                categoryName,
                article.getStatus(),
                article.getCreatedAt(),
                article.getUpdatedAt()
        );
    }
}
