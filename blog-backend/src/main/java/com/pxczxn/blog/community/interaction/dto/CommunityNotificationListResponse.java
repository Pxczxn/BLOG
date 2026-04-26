


package com.pxczxn.blog.community.interaction.dto;

import com.pxczxn.blog.common.response.PageResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommunityNotificationListResponse {

    
    private PageResponse<CommunityNotificationItemResponse> page;
    
    private long unreadCount;
}

