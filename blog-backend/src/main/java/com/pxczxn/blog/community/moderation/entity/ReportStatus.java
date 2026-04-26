/**
 * 举报状态枚举
 * <p>
 * 定义举报记录的处理状态
 */
package com.pxczxn.blog.community.moderation.entity;

public enum ReportStatus {
    /** 待处理 */
    OPEN,
    /** 已解决 */
    RESOLVED,
    /** 已驳回 */
    DISMISSED
}
