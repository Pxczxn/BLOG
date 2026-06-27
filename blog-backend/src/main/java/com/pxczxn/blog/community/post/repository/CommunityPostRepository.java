/**
 * 接口
 */
package com.pxczxn.blog.community.post.repository;

import com.pxczxn.blog.community.post.entity.CommunityPost;
import com.pxczxn.blog.community.post.entity.CommunityPostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long>, JpaSpecificationExecutor<CommunityPost> {

    Optional<CommunityPost> findBySlug(String slug);

    Optional<CommunityPost> findBySlugAndStatus(String slug, CommunityPostStatus status);

    boolean existsBySlug(String slug);

    long countByNodeIdAndStatus(Long nodeId, CommunityPostStatus status);

    @Query("""
            select p.nodeId as nodeId, count(p) as count
            from CommunityPost p
            where p.nodeId in :nodeIds and p.status = :status
            group by p.nodeId
            """)
    List<NodePostCountProjection> countByNodeIdsAndStatus(
            @Param("nodeIds") Collection<Long> nodeIds,
            @Param("status") CommunityPostStatus status
    );

    long countByAuthorIdAndStatus(Long authorId, CommunityPostStatus status);
}

