/**
 * 举报创建响应 DTO
 * <p>
 * 返回用户创建的举报信息
 */
package com.pxczxn.blog.community.moderation.dto;

import com.pxczxn.blog.community.moderation.entity.ContentReport;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommunityReportResponse {

    /** 举报 ID */
    private Long id;
    /** 内容类型 */
    private String contentType;
    /** 内容 ID */
    private Long contentId;
    /** 举报原因 */
    private String reason;
    /** 处理状态 */
    private String status;
    /** 创建时间 */
    private LocalDateTime createdAt;

    /**
     * 从实体转换为响应对象
     *
     * @param report 举报实体
     * @return 响应对象
     */
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
