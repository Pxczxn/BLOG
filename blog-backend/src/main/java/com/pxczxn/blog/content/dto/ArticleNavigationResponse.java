package com.pxczxn.blog.content.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleNavigationResponse {
    private Long id;
    private String title;
    private String slug;
    private LocalDateTime createdAt;

    public static ArticleNavigationResponse from(ArticleNavigationItem item) {
        if (item == null) return null;
        return ArticleNavigationResponse.builder()
                .id(item.getId())
                .title(item.getTitle())
                .slug(item.getSlug())
                .createdAt(item.getCreatedAt())
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArticleNavigationItem {
        private Long id;
        private String title;
        private String slug;
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArticleNavigationData {
        private ArticleNavigationItem previous;
        private ArticleNavigationItem next;
    }
}
