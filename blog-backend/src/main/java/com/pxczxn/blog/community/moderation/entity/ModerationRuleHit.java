/**
 * 审核规则命中记录实体
 * <p>
 * 记录审核任务中关键词规则的命中详情，包括匹配的关键词、上下文片段和严重级别
 */
package com.pxczxn.blog.community.moderation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "moderation_rule_hit")
public class ModerationRuleHit {

    /** 命中记录 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 关联的审核任务 ID */
    @Column(name = "task_id", nullable = false)
    private Long taskId;

    /** 关联的关键词规则 ID */
    @Column(name = "rule_id", nullable = false)
    private Long ruleId;

    /** 命中的关键词值 */
    @Column(name = "keyword_value", nullable = false, length = 120)
    private String keywordValue;

    /** 命中上下文片段 */
    @Column(name = "snippet", length = 255)
    private String snippet;

    /** 严重级别 */
    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 10)
    private ModerationRuleSeverity severity;

    /** 创建时间 */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 持久化前自动设置创建时间 */
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
