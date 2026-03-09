package com.pxczxn.blog.comment.dto;

import com.pxczxn.blog.comment.entity.Comment;

import java.time.LocalDateTime;

public record PublicCommentResponse(
        Long id,
        Long parentId,
        String nickname,
        String content,
        LocalDateTime createdAt
) {
    public static PublicCommentResponse from(Comment comment) {
        return new PublicCommentResponse(
                comment.getId(),
                comment.getParentId(),
                comment.getNickname(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }
}
