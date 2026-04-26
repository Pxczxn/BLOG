/**
 * 社区用户个人资料更新请求 DTO
 */
package com.pxczxn.blog.community.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommunityProfileUpdateRequest {

    /** 显示名称（昵称），长度限制 2-80 个字符 */
    @Size(min = 2, max = 80, message = "显示名称长度必须在 2-80 个字符之间")
    private String displayName;

    /** 个人简介，最大 500 个字符 */
    @Size(max = 500, message = "个人简介长度不能超过 500")
    private String bio;

    /** 头像 URL，最大 255 个字符 */
    @Size(max = 255, message = "头像链接长度不能超过255")
    private String avatar;

    /** 个人网站 URL，最大 255 个字符，需符合 URL 格式 */
    @Size(max = 255, message = "个人网站链接长度不能超过255")
    @Pattern(regexp = "^(https?://.+)?$", message = "网站链接格式无效")
    private String website;
}
