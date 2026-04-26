




package com.pxczxn.blog.content.dto;

import com.pxczxn.blog.content.entity.Article;
import com.pxczxn.blog.content.entity.ArticleStatus;

import java.time.LocalDateTime;

public record ArticleStatusUpdateResponse(
        
        Long id,
        
        ArticleStatus status,
        
        LocalDateTime updatedAt
) {
    





    public static ArticleStatusUpdateResponse from(Article article) {
        return new ArticleStatusUpdateResponse(
                article.getId(),
                article.getStatus(),
                article.getUpdatedAt()
        );
    }
}
