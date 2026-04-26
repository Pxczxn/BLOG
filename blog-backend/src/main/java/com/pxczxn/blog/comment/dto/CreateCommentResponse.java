/**
 * 创建评论响应 DTO
 * <p>
 * 返回新创建评论的ID、审核状态和创建时间。
 */
package com.pxczxn.blog.comment.dto;

import com.pxczxn.blog.comment.entity.Comment;
import com.pxczxn.blog.comment.entity.CommentStatus;

import java.time.LocalDateTime;

public record CreateCommentResponse(
        /** 评论ID */
        Long id,
        /** 审核状态 */
        CommentStatus status,
        /** 创建时间 */
        LocalDateTime createdAt
) {
    /**
     * 将评论实体转换为响应DTO
     *
     * @param comment 评论实体
     * @return 响应DTO
     */
    public static CreateCommentResponse from(Comment comment) {
        return new CreateCommentResponse(
                comment.getId(),
                comment.getStatus(),
                comment.getCreatedAt()
        );
    }
}

