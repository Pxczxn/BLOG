


package com.pxczxn.blog.community.dto;

import com.pxczxn.blog.community.entity.CommunityUser;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PublicCommunityProfileResponse {

    
    private Long userId;

    
    private String username;

    
    private String displayName;

    
    private String avatar;

    
    private String bio;

    
    private String website;

    
    private String role;

    
    private long followerCount;

    
    private long followingCount;

    
    private boolean followedByMe;

    








    public static PublicCommunityProfileResponse from(CommunityUser user,
                                                      long followerCount,
                                                      long followingCount,
                                                      boolean followedByMe) {
        return PublicCommunityProfileResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .avatar(user.getAvatar())
                .bio(user.getBio())
                .website(user.getWebsite())
                .role(user.getRole().name())
                .followerCount(followerCount)
                .followingCount(followingCount)
                .followedByMe(followedByMe)
                .build();
    }
}
