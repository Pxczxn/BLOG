package com.pxczxn.blog.community.post.comment.dto;

import com.pxczxn.blog.community.post.comment.entity.CommunityPostComment;
import com.pxczxn.blog.community.post.comment.entity.CommunityPostCommentStatus;

import java.time.LocalDateTime;

public record AdminCommunityPostCommentItemResponse(
        Long id,
        Long postId,
        String postTitle,
        String postSlug,
        Long parentId,
        String nickname,
        String content,
        CommunityPostCommentStatus status,
        LocalDateTime createdAt
) {
    public static AdminCommunityPostCommentItemResponse from(CommunityPostComment comment,
                                                             String postTitle,
                                                             String postSlug) {
        return new AdminCommunityPostCommentItemResponse(
                comment.getId(),
                comment.getPostId(),
                postTitle,
                postSlug,
                comment.getParentId(),
                comment.getNickname(),
                comment.getContent(),
                comment.getStatus(),
                comment.getCreatedAt()
        );
    }
}
