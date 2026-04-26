/**
 * 管理端评论列表项响应 DTO
 * <p>
 * 返回评论的ID、文章ID、昵称、内容、审核状态和创建时间。
 */
package com.pxczxn.blog.comment.dto;

import com.pxczxn.blog.comment.entity.Comment;
import com.pxczxn.blog.comment.entity.CommentStatus;

import java.time.LocalDateTime;

public record AdminCommentItemResponse(
        /** 评论ID */
        Long id,
        /** 所属文章ID */
        Long articleId,
        /** 评论者昵称 */
        String nickname,
        /** 评论内容 */
        String content,
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
    public static AdminCommentItemResponse from(Comment comment) {
        return new AdminCommentItemResponse(
                comment.getId(),
                comment.getArticleId(),
                comment.getNickname(),
                comment.getContent(),
                comment.getStatus(),
                comment.getCreatedAt()
        );
    }
}

