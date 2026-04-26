


package com.pxczxn.blog.community.interaction.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AdminInteractionOverviewResponse {

    
    private long totalLikes;
    
    private long totalFavorites;
    
    private long totalFollows;
    
    private long totalNotifications;
    
    private long unreadNotifications;
    
    private List<AdminPostHeatItemResponse> topHotPosts;
}

