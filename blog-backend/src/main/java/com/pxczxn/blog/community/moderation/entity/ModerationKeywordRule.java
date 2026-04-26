




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

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Column(name = "name", nullable = false, length = 80)
    private String name;

    
    @Column(name = "keyword_value", nullable = false, unique = true, length = 120)
    private String keywordValue;

    
    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 10)
    @Builder.Default
    private ModerationRuleSeverity severity = ModerationRuleSeverity.MEDIUM;

    
    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private Boolean enabled = Boolean.TRUE;
}
