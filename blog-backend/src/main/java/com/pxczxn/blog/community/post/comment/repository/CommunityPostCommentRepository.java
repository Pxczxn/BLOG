


package com.pxczxn.blog.community.post.comment.repository;

import com.pxczxn.blog.community.post.comment.entity.CommunityPostComment;
import com.pxczxn.blog.community.post.comment.entity.CommunityPostCommentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommunityPostCommentRepository extends JpaRepository<CommunityPostComment, Long> {

    List<CommunityPostComment> findByPostIdAndStatusOrderByCreatedAtAsc(Long postId, CommunityPostCommentStatus status);

    Optional<CommunityPostComment> findByIdAndStatus(Long id, CommunityPostCommentStatus status);
}

