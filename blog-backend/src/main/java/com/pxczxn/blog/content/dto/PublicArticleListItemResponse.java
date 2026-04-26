




package com.pxczxn.blog.content.dto;

import com.pxczxn.blog.content.entity.Article;

import java.time.LocalDateTime;

public record PublicArticleListItemResponse(
        
        Long id,
        
        String title,
        
        String slug,
        
        String summary,
        
        String coverImage,
        
        Long viewCount,
        
        ArticleCategorySummaryResponse category,
        
        LocalDateTime createdAt
) {
    






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
