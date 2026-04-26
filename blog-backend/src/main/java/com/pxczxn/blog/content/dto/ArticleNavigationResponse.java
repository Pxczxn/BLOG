/**
 * 文章导航响应 DTO
 * <p>
 * 包含当前文章的上一篇和下一篇文章的导航信息，
 * 用于文章详情页的上下篇切换。
 */
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

    /** 文章ID */
    private Long id;
    /** 文章标题 */
    private String title;
    /** 文章slug标识 */
    private String slug;
    /** 创建时间 */
    private LocalDateTime createdAt;

    /**
     * 从导航项构建导航响应
     *
     * @param item 导航项，为null时返回null
     * @return 导航响应
     */
    public static ArticleNavigationResponse from(ArticleNavigationItem item) {
        if (item == null) return null;
        return ArticleNavigationResponse.builder()
                .id(item.getId())
                .title(item.getTitle())
                .slug(item.getSlug())
                .createdAt(item.getCreatedAt())
                .build();
    }

    /**
     * 文章导航项
     * <p>
     * 表示单篇导航文章的基本信息。
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArticleNavigationItem {
        /** 文章ID */
        private Long id;
        /** 文章标题 */
        private String title;
        /** 文章slug标识 */
        private String slug;
        /** 创建时间 */
        private LocalDateTime createdAt;
    }

    /**
     * 文章导航数据
     * <p>
     * 包含上一篇和下一篇文章的导航项。
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArticleNavigationData {
        /** 上一篇文章导航项 */
        private ArticleNavigationItem previous;
        /** 下一篇文章导航项 */
        private ArticleNavigationItem next;
    }
}
