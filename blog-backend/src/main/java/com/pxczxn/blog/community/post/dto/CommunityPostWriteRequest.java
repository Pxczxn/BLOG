


package com.pxczxn.blog.community.post.dto;

import com.pxczxn.blog.community.post.entity.CommunityPostStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CommunityPostWriteRequest {

    
    @NotNull(message = "节点ID不能为空")
    private Long nodeId;

    
    @NotBlank(message = "帖子标题不能为空")
    @Size(max = 200, message = "帖子标题长度不能超过200")
    private String title;

    
    @Size(max = 500, message = "帖子摘要长度不能超过500")
    private String summary;

    
    @NotBlank(message = "帖子内容不能为空")
    private String content;

    
    private CommunityPostStatus status;

    @Size(max = 5, message = "帖子标签最多选择5个")
    private List<Long> tagIds;
}

