


package com.pxczxn.blog.community.dto;

import com.pxczxn.blog.community.entity.CommunityUser;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class CommunityAuthResponse {

    
    private Long userId;

    
    private String username;

    
    private String email;

    
    private String displayName;

    
    private String avatar;

    
    private String bio;

    
    private String website;

    
    private String role;

    
    private String status;

    
    private String token;

    
    private Instant expiresAt;

    







    public static CommunityAuthResponse from(CommunityUser user, String token, Instant expiresAt) {
        return CommunityAuthResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .avatar(user.getAvatar())
                .bio(user.getBio())
                .website(user.getWebsite())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .token(token)
                .expiresAt(expiresAt)
                .build();
    }
}
