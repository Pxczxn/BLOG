/**
 * 审核风险级别枚举
 * <p>
 * 定义审核任务的风险等级，用于标识内容的违规严重程度
 */
package com.pxczxn.blog.community.moderation.entity;

public enum ModerationRiskLevel {
    /** 低风险 */
    LOW,
    /** 中风险 */
    MEDIUM,
    /** 高风险 */
    HIGH
}
