


package com.pxczxn.blog.community.dto;

import com.pxczxn.blog.community.entity.CommunityUser;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommunityProfileResponse {

    
    private Long userId;

    
    private String username;

    
    private String email;

    
    private String displayName;

    
    private String avatar;

    
    private String bio;

    
    private String website;

    
    private String role;

    
    private String status;

    





    public static CommunityProfileResponse from(CommunityUser user) {
        return CommunityProfileResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .avatar(user.getAvatar())
                .bio(user.getBio())
                .website(user.getWebsite())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .build();
    }
}
