package com.pxczxn.blog.comment.repository;

import com.pxczxn.blog.comment.entity.Comment;
import com.pxczxn.blog.comment.entity.CommentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByStatusOrderByCreatedAtDesc(CommentStatus status, Pageable pageable);

    List<Comment> findByArticleIdAndStatusOrderByCreatedAtAsc(Long articleId, CommentStatus status);

    long countByArticleIdAndStatus(Long articleId, CommentStatus status);

    Optional<Comment> findById(Long id);
}
