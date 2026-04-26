




package com.pxczxn.blog.comment.dto;

import com.pxczxn.blog.comment.entity.Comment;
import com.pxczxn.blog.comment.entity.CommentStatus;

import java.time.LocalDateTime;

public record CreateCommentResponse(
        
        Long id,
        
        CommentStatus status,
        
        LocalDateTime createdAt
) {
    





    public static CreateCommentResponse from(Comment comment) {
        return new CreateCommentResponse(
                comment.getId(),
                comment.getStatus(),
                comment.getCreatedAt()
        );
    }
}

