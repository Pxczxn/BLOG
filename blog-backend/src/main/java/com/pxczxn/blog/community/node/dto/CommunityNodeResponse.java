package com.pxczxn.blog.community.node.dto;

import com.pxczxn.blog.community.node.entity.CommunityNode;
import lombok.Builder;
import lombok.Data;






@Data
@Builder
public class CommunityNodeResponse {

    


    private Long id;

    


    private String name;

    


    private String slug;

    


    private String description;

    


    private String icon;

    


    private Integer sortOrder;

    


    private String status;

    


    private long postCount;

    






    public static CommunityNodeResponse from(CommunityNode node, long postCount) {
        return CommunityNodeResponse.builder()
                .id(node.getId())
                .name(node.getName())
                .slug(node.getSlug())
                .description(node.getDescription())
                .icon(node.getIcon())
                .sortOrder(node.getSortOrder())
                .status(node.getStatus().name())
                .postCount(postCount)
                .build();
    }
}
