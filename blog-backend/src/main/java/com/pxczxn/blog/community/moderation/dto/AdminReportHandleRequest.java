




package com.pxczxn.blog.community.moderation.dto;

import com.pxczxn.blog.community.moderation.entity.ReportHandleAction;
import com.pxczxn.blog.community.moderation.entity.ReportStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminReportHandleRequest {

    
    @NotNull(message = "处理状态不能为空")
    private ReportStatus status;

    
    private ReportHandleAction handleAction;

    
    @Size(max = 500, message = "处理说明长度不能超过 500 个字符")
    private String handleNote;
}
