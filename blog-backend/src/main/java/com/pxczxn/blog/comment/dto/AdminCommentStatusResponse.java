




package com.pxczxn.blog.comment.dto;

import com.pxczxn.blog.comment.entity.Comment;
import com.pxczxn.blog.comment.entity.CommentStatus;

public record AdminCommentStatusResponse(
        
        Long id,
        
        CommentStatus status
) {
    





    public static AdminCommentStatusResponse from(Comment comment) {
        return new AdminCommentStatusResponse(comment.getId(), comment.getStatus());
    }
}

