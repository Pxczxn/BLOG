/**
 * 登录请求 DTO
 * <p>
 * 用于接收管理员登录时提交的用户名/邮箱、密码及设备信息。
 */
package com.pxczxn.blog.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    /** 用户名或邮箱 */
    @NotBlank(message = "用户名或邮箱不能为空")
    private String username;

    /** 密码 */
    @NotBlank(message = "密码不能为空")
    private String password;

    /** 设备ID（可选，不传则服务端自动生成） */
    private String deviceId;

    /** 设备名称（可选，如 "Chrome on Windows"） */
    private String deviceName;
}