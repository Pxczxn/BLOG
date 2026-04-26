




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

    
    private Long id;
    
    private String title;
    
    private String slug;
    
    private String summary;
    
    private String content;
    
    private String coverImage;
    
    private Long viewCount;
    
    private ArticleStatus status;
    
    private LocalDateTime publishedAt;
    
    private Long authorId;
    
    private String authorName;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;

    






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

    





    public static ArticleResponse from(Article article) {
        return from(article, null);
    }
}
