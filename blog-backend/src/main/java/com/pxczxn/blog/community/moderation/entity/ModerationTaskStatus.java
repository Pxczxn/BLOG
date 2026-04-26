/**
 * 审核任务状态枚举
 * <p>
 * 定义审核任务的生命周期状态
 */
package com.pxczxn.blog.community.moderation.entity;

public enum ModerationTaskStatus {
    /** 待审核 */
    PENDING,
    /** 已通过 */
    APPROVED,
    /** 已拒绝 */
    REJECTED,
    /** 已取消 */
    CANCELED
}
