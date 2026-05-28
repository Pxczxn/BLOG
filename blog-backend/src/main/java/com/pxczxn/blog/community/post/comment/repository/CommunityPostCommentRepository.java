/**
 * 接口
 */
package com.pxczxn.blog.community.post.comment.repository;

import com.pxczxn.blog.community.post.comment.entity.CommunityPostComment;
import com.pxczxn.blog.community.post.comment.entity.CommunityPostCommentStatus;
import com.pxczxn.blog.community.interaction.repository.PostCountProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CommunityPostCommentRepository extends JpaRepository<CommunityPostComment, Long> {

    Page<CommunityPostComment> findByStatusOrderByCreatedAtDesc(CommunityPostCommentStatus status, Pageable pageable);

    List<CommunityPostComment> findByPostIdAndStatusOrderByCreatedAtAsc(Long postId, CommunityPostCommentStatus status);

    Optional<CommunityPostComment> findByIdAndStatus(Long id, CommunityPostCommentStatus status);

    long countByPostIdAndStatus(Long postId, CommunityPostCommentStatus status);

    @Query("select c.postId as postId, count(c) as count from CommunityPostComment c where c.postId in :postIds and c.status = :status group by c.postId")
    List<PostCountProjection> countByPostIdsAndStatus(@Param("postIds") Collection<Long> postIds,
                                                      @Param("status") CommunityPostCommentStatus status);
}

