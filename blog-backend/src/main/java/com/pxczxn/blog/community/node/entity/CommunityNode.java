package com.pxczxn.blog.community.node.entity;

import com.pxczxn.blog.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 社区节点实体
 * <p>
 * 节点是社区帖子的分类版块，用于组织和归类帖子
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "community_node")
public class CommunityNode extends BaseTimeEntity {

    /**
     * 节点ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 节点名称，唯一
     */
    @Column(name = "name", nullable = false, unique = true, length = 80)
    private String name;

    /**
     * URL别名，用于构建友好的URL路径，唯一
     */
    @Column(name = "slug", nullable = false, unique = true, length = 80)
    private String slug;

    /**
     * 节点描述
     */
    @Column(name = "description", length = 255)
    private String description;

    /**
     * 图标名称
     */
    @Column(name = "icon", length = 50)
    private String icon;

    /**
     * 排序顺序，数值越小越靠前
     */
    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    /**
     * 节点状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private CommunityNodeStatus status = CommunityNodeStatus.ACTIVE;
}
