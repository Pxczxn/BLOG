





package com.pxczxn.blog.comment.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCommentRequest {

    
    @NotNull(message = "文章ID不能为空")
    private Long articleId;

    
    private Long parentId;

    
    @Size(min = 1, max = 50, message = "昵称长度必须在 1-50 个字符之间")
    private String nickname;

    
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过 100 个字符")
    private String email;

    
    @NotBlank(message = "评论内容不能为空")
    @Size(min = 1, max = 1000, message = "评论内容长度必须在 1-1000 个字符之间")
    private String content;
}