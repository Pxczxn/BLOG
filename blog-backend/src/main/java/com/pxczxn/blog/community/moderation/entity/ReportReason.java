/**
 * 举报原因枚举
 * <p>
 * 定义用户举报内容时选择的原因类型
 */
package com.pxczxn.blog.community.moderation.entity;

public enum ReportReason {
    /** 垃圾信息 */
    SPAM,
    /** 辱骂骚扰 */
    ABUSE,
    /** 版权侵权 */
    COPYRIGHT,
    /** 违法内容 */
    ILLEGAL,
    /** 其他原因 */
    OTHER
}
