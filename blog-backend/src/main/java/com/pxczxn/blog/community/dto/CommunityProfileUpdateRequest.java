


package com.pxczxn.blog.community.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommunityProfileUpdateRequest {

    
    @Size(min = 2, max = 80, message = "显示名称长度必须在 2-80 个字符之间")
    private String displayName;

    
    @Size(max = 500, message = "个人简介长度不能超过 500")
    private String bio;

    
    @Size(max = 255, message = "头像链接长度不能超过255")
    private String avatar;

    
    @Size(max = 255, message = "个人网站链接长度不能超过255")
    @Pattern(regexp = "^(https?://.+)?$", message = "网站链接格式无效")
    private String website;
}
