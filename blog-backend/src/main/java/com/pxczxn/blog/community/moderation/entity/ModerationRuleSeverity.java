/**
 * 审核规则严重级别枚举
 * <p>
 * 定义关键词规则的严重程度，BLOCK 级别会直接拦截内容
 */
package com.pxczxn.blog.community.moderation.entity;

public enum ModerationRuleSeverity {
    /** 低 */
    LOW,
    /** 中 */
    MEDIUM,
    /** 高 */
    HIGH,
    /** 阻断，命中即自动拒绝 */
    BLOCK
}
