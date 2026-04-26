/**
 * 评论审核状态枚举
 * <p>
 * PENDING - 待审核，APPROVED - 已通过，REJECTED - 已拒绝
 */
package com.pxczxn.blog.comment.entity;

public enum CommentStatus {
    /** 待审核 */
    PENDING,
    /** 已通过 */
    APPROVED,
    /** 已拒绝 */
    REJECTED
}

