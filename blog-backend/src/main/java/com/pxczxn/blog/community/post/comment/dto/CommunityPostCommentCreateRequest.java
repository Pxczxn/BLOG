


package com.pxczxn.blog.community.post.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommunityPostCommentCreateRequest {

    
    private Long parentId;

    
    @NotBlank(message = "评论内容不能为空")
    @Size(min = 1, max = 1000, message = "评论内容长度必须在 1-1000 个字符之间")
    private String content;
}