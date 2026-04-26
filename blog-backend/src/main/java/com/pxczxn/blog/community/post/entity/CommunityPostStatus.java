/**
 * 社区帖子状态枚举
 */
package com.pxczxn.blog.community.post.entity;

public enum CommunityPostStatus {
    /** 草稿 */
    DRAFT,
    /** 待审核 */
    PENDING_REVIEW,
    /** 已发布 */
    PUBLISHED,
    /** 已驳回 */
    REJECTED,
    /** 已隐藏 */
    HIDDEN
}

