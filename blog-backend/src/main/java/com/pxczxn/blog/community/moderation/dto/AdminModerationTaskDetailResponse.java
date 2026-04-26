




package com.pxczxn.blog.community.moderation.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class AdminModerationTaskDetailResponse {

    
    private Long id;
    
    private String contentType;
    
    private Long contentId;
    
    private String titleSnapshot;
    
    private String status;
    
    private String riskLevel;
    
    private Integer hitCount;
    
    private String submittedBy;
    
    private String reviewedBy;
    
    private String decisionNote;
    
    private LocalDateTime submittedAt;
    
    private LocalDateTime reviewedAt;
    
    private List<ModerationRuleHitResponse> hits;
}
