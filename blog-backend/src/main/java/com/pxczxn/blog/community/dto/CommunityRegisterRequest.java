/**
 * 社区用户注册请求 DTO
 */
package com.pxczxn.blog.community.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommunityRegisterRequest {

    /** 用户名，3-50 个字符，只能包含字母、数字、下划线或短横线，必须以字母或数字开头 */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在 3-50 个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9][a-zA-Z0-9_-]{2,49}$", message = "用户名必须以字母或数字开头，并且只能包含字母、数字、下划线或短横线")
    private String username;

    /** 邮箱地址 */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过 100 个字符")
    private String email;

    /** 用户密码，8-72 个字符 */
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 72, message = "密码长度必须在 8-72 个字符之间")
    private String password;

    /** 显示名称（昵称），2-80 个字符 */
    @NotBlank(message = "显示名称不能为空")
    @Size(min = 2, max = 80, message = "显示名称长度必须在 2-80 个字符之间")
    private String displayName;
}
