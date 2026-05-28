package com.pxczxn.blog.comment.dto;

import com.pxczxn.blog.comment.entity.Comment;
import com.pxczxn.blog.comment.entity.CommentStatus;

import java.time.LocalDateTime;

public record AdminCommentItemResponse(
        Long id,
        Long articleId,
        String articleTitle,
        String nickname,
        String content,
        CommentStatus status,
        LocalDateTime createdAt
) {
    public static AdminCommentItemResponse from(Comment comment, String articleTitle) {
        return new AdminCommentItemResponse(
                comment.getId(),
                comment.getArticleId(),
                articleTitle,
                comment.getNickname(),
                comment.getContent(),
                comment.getStatus(),
                comment.getCreatedAt()
        );
    }
}
