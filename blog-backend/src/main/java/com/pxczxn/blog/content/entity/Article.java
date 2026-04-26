







package com.pxczxn.blog.content.entity;

import com.pxczxn.blog.common.entity.BaseTimeEntity;
import com.pxczxn.blog.user.entity.AdminUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "article")
public class Article extends BaseTimeEntity {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    
    @Column(name = "slug", nullable = false, unique = true, length = 200)
    private String slug;

    
    @Column(name = "summary", length = 500)
    private String summary;

    
    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    
    @Column(name = "cover_image", length = 500)
    private String coverImage;

    
    @Column(name = "view_count", nullable = false)
    @Builder.Default
    private Long viewCount = 0L;

    
    @Column(name = "category_id")
    private Long categoryId;

    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ArticleStatus status = ArticleStatus.DRAFT;

    
    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_article_author"))
    private AdminUser author;
}

