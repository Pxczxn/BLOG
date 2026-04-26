/**
 * 社区通知列表响应DTO
 */
package com.pxczxn.blog.community.interaction.dto;

import com.pxczxn.blog.common.response.PageResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommunityNotificationListResponse {

    /** 通知分页数据 */
    private PageResponse<CommunityNotificationItemResponse> page;
    /** 未读通知数 */
    private long unreadCount;
}

