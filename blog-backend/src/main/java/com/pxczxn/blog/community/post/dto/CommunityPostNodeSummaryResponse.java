/**
 * 帖子节点摘要响应
 */
package com.pxczxn.blog.community.post.dto;

import com.pxczxn.blog.community.node.entity.CommunityNode;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommunityPostNodeSummaryResponse {

    /** 节点ID */
    private Long id;
    /** 节点名称 */
    private String name;
    /** URL Slug */
    private String slug;

    /**
     * 从节点实体转换为响应对象
     *
     * @param node 节点实体
     * @return 响应对象
     */
    public static CommunityPostNodeSummaryResponse from(CommunityNode node) {
        return CommunityPostNodeSummaryResponse.builder()
                .id(node.getId())
                .name(node.getName())
                .slug(node.getSlug())
                .build();
    }
}

