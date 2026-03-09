package com.pxczxn.blog.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCommentRequest {

    @NotNull(message = "articleId must not be null")
    private Long articleId;

    private Long parentId;

    @NotBlank(message = "nickname must not be blank")
    @Size(min = 1, max = 50, message = "nickname length must be between 1 and 50")
    private String nickname;

    @Size(max = 100, message = "email length must be <= 100")
    private String email;

    @NotBlank(message = "content must not be blank")
    @Size(min = 1, max = 1000, message = "content length must be between 1 and 1000")
    private String content;
}
