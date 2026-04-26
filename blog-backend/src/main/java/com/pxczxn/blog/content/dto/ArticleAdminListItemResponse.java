




package com.pxczxn.blog.content.dto;

import com.pxczxn.blog.content.entity.Article;
import com.pxczxn.blog.content.entity.ArticleStatus;

import java.time.LocalDateTime;

public record ArticleAdminListItemResponse(
        
        Long id,
        
        String title,
        
        String categoryName,
        
        ArticleStatus status,
        
        LocalDateTime createdAt,
        
        LocalDateTime updatedAt
) {
    






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
