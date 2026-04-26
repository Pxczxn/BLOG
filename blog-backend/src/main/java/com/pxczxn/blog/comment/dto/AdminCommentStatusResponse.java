/**
 * 评论审核状态响应 DTO
 * <p>
 * 审核操作（通过/拒绝）后返回评论ID和更新后的状态。
 */
package com.pxczxn.blog.comment.dto;

import com.pxczxn.blog.comment.entity.Comment;
import com.pxczxn.blog.comment.entity.CommentStatus;

public record AdminCommentStatusResponse(
        /** 评论ID */
        Long id,
        /** 审核状态 */
        CommentStatus status
) {
    /**
     * 将评论实体转换为响应DTO
     *
     * @param comment 评论实体
     * @return 响应DTO
     */
    public static AdminCommentStatusResponse from(Comment comment) {
        return new AdminCommentStatusResponse(comment.getId(), comment.getStatus());
    }
}

