/**
 * 文章状态更新响应 DTO
 * <p>
 * 文章发布/下架操作后的响应，返回更新后的状态和更新时间。
 */
package com.pxczxn.blog.content.dto;

import com.pxczxn.blog.content.entity.Article;
import com.pxczxn.blog.content.entity.ArticleStatus;

import java.time.LocalDateTime;

public record ArticleStatusUpdateResponse(
        /** 文章ID */
        Long id,
        /** 更新后的发布状态 */
        ArticleStatus status,
        /** 更新时间 */
        LocalDateTime updatedAt
) {
    /**
     * 从文章实体构建响应
     *
     * @param article 更新后的文章实体
     * @return 状态更新响应
     */
    public static ArticleStatusUpdateResponse from(Article article) {
        return new ArticleStatusUpdateResponse(
                article.getId(),
                article.getStatus(),
                article.getUpdatedAt()
        );
    }
}
