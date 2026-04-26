/**
 * 社区帖子评论实体
 */
package com.pxczxn.blog.community.post.comment.entity;

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
@Table(name = "community_post_comment")
public class CommunityPostComment extends BaseTimeEntity {

    /** 评论ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 所属帖子ID */
    @Column(name = "post_id", nullable = false)
    private Long postId;

    /** 父评论ID，为空表示顶级评论 */
    @Column(name = "parent_id")
    private Long parentId;

    /** 评论用户ID */
    @Column(name = "community_user_id", nullable = false)
    private Long communityUserId;

    /** 评论者昵称 */
    @Column(name = "nickname", nullable = false, length = 80)
    private String nickname;

    /** 评论内容 */
    @Column(name = "content", nullable = false, length = 1000)
    private String content;

    /** 评论状态 */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private CommunityPostCommentStatus status = CommunityPostCommentStatus.PENDING;
}

