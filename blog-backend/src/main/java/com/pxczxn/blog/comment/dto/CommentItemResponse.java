package com.pxczxn.blog.comment.dto;

import com.pxczxn.blog.comment.entity.Comment;

import java.time.LocalDateTime;

public record CommentItemResponse(
        Long id,
        Long parentId,
        String nickname,
        String content,
        LocalDateTime createdAt
) {
    public static CommentItemResponse from(Comment comment) {
        return new CommentItemResponse(
                comment.getId(),
                comment.getParentId(),
                comment.getNickname(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }
}
