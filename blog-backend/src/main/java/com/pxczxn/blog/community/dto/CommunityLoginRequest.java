/**
 * 社区用户登录请求 DTO
 */
package com.pxczxn.blog.community.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommunityLoginRequest {

    /** 用户名或邮箱（登录标识符） */
    @NotBlank(message = "用户名或邮箱不能为空")
    @Size(max = 100, message = "用户名或邮箱长度不能超过 100 个字符")
    private String identifier;

    /** 用户密码 */
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 72, message = "密码长度必须在 8-72 个字符之间")
    private String password;
}
