


package com.pxczxn.blog.community.interaction.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommunityFollowStatusResponse {

    
    private Long targetUserId;
    
    private String targetUsername;
    
    private boolean following;
    
    private long followerCount;
    
    private long followingCount;
}

