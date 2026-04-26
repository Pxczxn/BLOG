package com.pxczxn.blog.community.node.dto;

import com.pxczxn.blog.community.node.entity.CommunityNode;
import lombok.Builder;
import lombok.Data;

/**
 * 社区节点响应DTO
 * <p>
 * 用于返回节点信息给前端
 */
@Data
@Builder
public class CommunityNodeResponse {

    /**
     * 节点ID
     */
    private Long id;

    /**
     * 节点名称
     */
    private String name;

    /**
     * URL别名
     */
    private String slug;

    /**
     * 节点描述
     */
    private String description;

    /**
     * 图标名称
     */
    private String icon;

    /**
     * 排序顺序
     */
    private Integer sortOrder;

    /**
     * 节点状态
     */
    private String status;

    /**
     * 节点下的帖子数量
     */
    private long postCount;

    /**
     * 从实体转换为响应DTO
     *
     * @param node      社区节点实体
     * @param postCount 该节点下的帖子数量
     * @return 响应DTO
     */
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
