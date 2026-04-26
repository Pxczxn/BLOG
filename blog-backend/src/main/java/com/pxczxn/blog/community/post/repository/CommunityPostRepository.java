


package com.pxczxn.blog.community.post.repository;

import com.pxczxn.blog.community.post.entity.CommunityPost;
import com.pxczxn.blog.community.post.entity.CommunityPostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long>, JpaSpecificationExecutor<CommunityPost> {

    Optional<CommunityPost> findBySlug(String slug);

    Optional<CommunityPost> findBySlugAndStatus(String slug, CommunityPostStatus status);

    boolean existsBySlug(String slug);

    long countByNodeIdAndStatus(Long nodeId, CommunityPostStatus status);

    long countByAuthorIdAndStatus(Long authorId, CommunityPostStatus status);
}

