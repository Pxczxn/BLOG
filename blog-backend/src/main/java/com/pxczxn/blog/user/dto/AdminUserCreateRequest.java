/**
 * 创建管理员用户请求 DTO
 * <p>
 * 用于接收创建管理员账户的请求数据
 */
package com.pxczxn.blog.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建管理员用户请求 DTO
 */
@Data
public class AdminUserCreateRequest {

    /** 用户名，长度 3-50 个字符 */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在 3-50 个字符之间")
    private String username;

    /** 邮箱地址，最大 100 个字符 */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过 100 个字符")
    private String email;

    /** 密码，长度 6-50 个字符 */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 50, message = "密码长度必须在 6-50 个字符之间")
    private String password;
}
