/**
 * 社区帖子实体
 */
package com.pxczxn.blog.community.post.entity;

import com.pxczxn.blog.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "community_post")
public class CommunityPost extends BaseTimeEntity {

    /** 帖子ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 所属节点ID */
    @Column(name = "node_id", nullable = false)
    private Long nodeId;

    /** 作者ID */
    @Column(name = "author_id", nullable = false)
    private Long authorId;

    /** 帖子标题 */
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /** URL Slug，唯一标识 */
    @Column(name = "slug", nullable = false, unique = true, length = 220)
    private String slug;

    /** 帖子摘要 */
    @Column(name = "summary", length = 500)
    private String summary;

    /** 帖子正文内容 */
    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    /** 帖子状态 */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private CommunityPostStatus status = CommunityPostStatus.DRAFT;

    /** 发布时间 */
    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    /** 最后编辑时间 */
    @Column(name = "last_edited_at")
    private LocalDateTime lastEditedAt;

    /** 浏览次数 */
    @Column(name = "view_count", nullable = false)
    @Builder.Default
    private Long viewCount = 0L;

    /** 驳回原因 */
    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;
}

