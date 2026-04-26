


package com.pxczxn.blog.community.post.dto;

import com.pxczxn.blog.community.entity.CommunityUser;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommunityPostAuthorSummaryResponse {

    
    private Long userId;
    
    private String username;
    
    private String displayName;
    
    private String avatar;
    
    private String role;

    





    public static CommunityPostAuthorSummaryResponse from(CommunityUser user) {
        return CommunityPostAuthorSummaryResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .avatar(user.getAvatar())
                .role(user.getRole().name())
                .build();
    }
}

