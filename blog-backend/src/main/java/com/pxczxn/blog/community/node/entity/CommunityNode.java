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






@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "community_node")
public class CommunityNode extends BaseTimeEntity {

    


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    


    @Column(name = "name", nullable = false, unique = true, length = 80)
    private String name;

    


    @Column(name = "slug", nullable = false, unique = true, length = 80)
    private String slug;

    


    @Column(name = "description", length = 255)
    private String description;

    


    @Column(name = "icon", length = 50)
    private String icon;

    


    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    


    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private CommunityNodeStatus status = CommunityNodeStatus.ACTIVE;
}
