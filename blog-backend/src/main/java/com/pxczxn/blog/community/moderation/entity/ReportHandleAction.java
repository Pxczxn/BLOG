/**
 * 举报处理动作枚举
 * <p>
 * 定义管理员处理举报时可执行的操作
 */
package com.pxczxn.blog.community.moderation.entity;

public enum ReportHandleAction {
    /** 不采取动作 */
    NONE,
    /** 隐藏帖子 */
    HIDE_POST,
    /** 拒绝评论 */
    REJECT_COMMENT
}
