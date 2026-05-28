package com.pxczxn.blog.community.post.dto;

import com.pxczxn.blog.community.post.entity.CommunityPost;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminCommunityPostListItemResponse {
    private Long id;
    private String title;
    private String slug;
    private String status;
    private String nodeName;
    private String authorName;
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;

    public static AdminCommunityPostListItemResponse from(CommunityPost post,
                                                          String nodeName,
                                                          String authorName,
                                                          long likeCount,
                                                          long commentCount) {
        return AdminCommunityPostListItemResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .slug(post.getSlug())
                .status(post.getStatus().name())
                .nodeName(nodeName)
                .authorName(authorName)
                .createdAt(post.getCreatedAt())
                .publishedAt(post.getPublishedAt())
                .viewCount(post.getViewCount())
                .likeCount(likeCount)
                .commentCount(commentCount)
                .build();
    }
}
