




package com.pxczxn.blog.community.moderation.dto;

import com.pxczxn.blog.community.moderation.entity.ModerationContentType;
import com.pxczxn.blog.community.moderation.entity.ReportReason;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommunityReportCreateRequest {

    
    @NotNull(message = "内容类型不能为空")
    private ModerationContentType contentType;

    
    @NotNull(message = "内容ID不能为空")
    private Long contentId;

    
    @NotNull(message = "举报原因不能为空")
    private ReportReason reason;

    
    @Size(max = 500, message = "举报描述长度不能超过500")
    private String description;
}
