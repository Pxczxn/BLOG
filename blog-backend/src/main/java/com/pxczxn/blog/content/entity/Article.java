/**
 * 文章实体
 * <p>
 * 博客核心内容实体，包含标题、slug、摘要、正文、封面图、浏览量、
 * 分类、发布状态和作者等信息。继承 BaseTimeEntity 自动管理时间戳。
 * <p>
 * 文章状态流转：DRAFT（草稿）→ PUBLISHED（已发布）/ ARCHIVED（已归档）
 */
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

    /** 文章ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** 文章标题 */
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /** URL友好标识，自动生成，唯一约束 */
    @Column(name = "slug", nullable = false, unique = true, length = 200)
    private String slug;

    /** 文章摘要/简介 */
    @Column(name = "summary", length = 500)
    private String summary;

    /** 文章正文（富文本，存储为 LONGTEXT） */
    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    /** 封面图片URL */
    @Column(name = "cover_image", length = 500)
    private String coverImage;

    /** 浏览次数 */
    @Column(name = "view_count", nullable = false)
    @Builder.Default
    private Long viewCount = 0L;

    /** 所属分类ID */
    @Column(name = "category_id")
    private Long categoryId;

    /** 发布状态：DRAFT-草稿，PUBLISHED-已发布，ARCHIVED-已归档 */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ArticleStatus status = ArticleStatus.DRAFT;

    /** 发布时间（首次发布时设置） */
    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    /** 作者（多对一关联到 AdminUser） */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_article_author"))
    private AdminUser author;
}

