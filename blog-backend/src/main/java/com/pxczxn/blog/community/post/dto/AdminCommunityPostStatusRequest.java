


package com.pxczxn.blog.community.post.dto;

import com.pxczxn.blog.community.post.entity.CommunityPostStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminCommunityPostStatusRequest {

    
    @NotNull(message = "帖子状态不能为空")
    private CommunityPostStatus status;

    
    @Size(max = 500, message = "驳回原因长度不能超过 500 个字符")
    private String rejectionReason;
}