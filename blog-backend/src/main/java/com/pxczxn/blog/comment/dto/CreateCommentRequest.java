/**
 * 创建评论请求 DTO
 * <p>
 * 包含文章ID、父评论ID（回复时使用）、昵称、邮箱和评论内容。
 * 社区用户登录后可省略昵称和邮箱，游客则必须填写。
 */
package com.pxczxn.blog.comment.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCommentRequest {

    /** 文章ID */
    @NotNull(message = "文章ID不能为空")
    private Long articleId;

    /** 父评论ID，回复时填写 */
    private Long parentId;

    /** 昵称，游客必填 */
    @Size(min = 1, max = 50, message = "昵称长度必须在 1-50 个字符之间")
    private String nickname;

    /** 邮箱，游客必填 */
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过 100 个字符")
    private String email;

    /** 评论内容 */
    @NotBlank(message = "评论内容不能为空")
    @Size(min = 1, max = 1000, message = "评论内容长度必须在 1-1000 个字符之间")
    private String content;
}