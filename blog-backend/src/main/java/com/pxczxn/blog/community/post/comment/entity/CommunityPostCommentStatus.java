/**
 * 社区帖子评论状态枚举
 */
package com.pxczxn.blog.community.post.comment.entity;

public enum CommunityPostCommentStatus {
    /** 待审核 */
    PENDING,
    /** 已通过 */
    APPROVED,
    /** 已驳回 */
    REJECTED
}

