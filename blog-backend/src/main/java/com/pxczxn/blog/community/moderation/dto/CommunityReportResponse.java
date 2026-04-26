




package com.pxczxn.blog.community.moderation.dto;

import com.pxczxn.blog.community.moderation.entity.ContentReport;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommunityReportResponse {

    
    private Long id;
    
    private String contentType;
    
    private Long contentId;
    
    private String reason;
    
    private String status;
    
    private LocalDateTime createdAt;

    





    public static CommunityReportResponse from(ContentReport report) {
        return CommunityReportResponse.builder()
                .id(report.getId())
                .contentType(report.getContentType().name())
                .contentId(report.getContentId())
                .reason(report.getReason().name())
                .status(report.getStatus().name())
                .createdAt(report.getCreatedAt())
                .build();
    }
}
