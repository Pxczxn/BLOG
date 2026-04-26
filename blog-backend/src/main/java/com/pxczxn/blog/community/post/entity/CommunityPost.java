


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

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Column(name = "node_id", nullable = false)
    private Long nodeId;

    
    @Column(name = "author_id", nullable = false)
    private Long authorId;

    
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    
    @Column(name = "slug", nullable = false, unique = true, length = 220)
    private String slug;

    
    @Column(name = "summary", length = 500)
    private String summary;

    
    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private CommunityPostStatus status = CommunityPostStatus.DRAFT;

    
    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    
    @Column(name = "last_edited_at")
    private LocalDateTime lastEditedAt;

    
    @Column(name = "view_count", nullable = false)
    @Builder.Default
    private Long viewCount = 0L;

    
    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;
}

