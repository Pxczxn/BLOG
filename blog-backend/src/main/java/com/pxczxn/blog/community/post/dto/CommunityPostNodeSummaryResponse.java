


package com.pxczxn.blog.community.post.dto;

import com.pxczxn.blog.community.node.entity.CommunityNode;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommunityPostNodeSummaryResponse {

    
    private Long id;
    
    private String name;
    
    private String slug;

    





    public static CommunityPostNodeSummaryResponse from(CommunityNode node) {
        return CommunityPostNodeSummaryResponse.builder()
                .id(node.getId())
                .name(node.getName())
                .slug(node.getSlug())
                .build();
    }
}

