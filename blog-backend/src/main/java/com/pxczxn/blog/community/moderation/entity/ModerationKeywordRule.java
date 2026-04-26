/**
 * 审核关键词规则实体
 * <p>
 * 定义用于自动审核的关键词匹配规则，每个规则包含关键词、严重级别和启用状态
 */
package com.pxczxn.blog.community.moderation.entity;

import com.pxczxn.blog.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "moderation_keyword_rule")
public class ModerationKeywordRule extends BaseTimeEntity {

    /** 规则 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 规则名称 */
    @Column(name = "name", nullable = false, length = 80)
    private String name;

    /** 关键词值，唯一 */
    @Column(name = "keyword_value", nullable = false, unique = true, length = 120)
    private String keywordValue;

    /** 严重级别 */
    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 10)
    @Builder.Default
    private ModerationRuleSeverity severity = ModerationRuleSeverity.MEDIUM;

    /** 是否启用 */
    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private Boolean enabled = Boolean.TRUE;
}
