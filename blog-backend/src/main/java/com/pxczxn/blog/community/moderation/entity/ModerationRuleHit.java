




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

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Column(name = "task_id", nullable = false)
    private Long taskId;

    
    @Column(name = "rule_id", nullable = false)
    private Long ruleId;

    
    @Column(name = "keyword_value", nullable = false, length = 120)
    private String keywordValue;

    
    @Column(name = "snippet", length = 255)
    private String snippet;

    
    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 10)
    private ModerationRuleSeverity severity;

    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
