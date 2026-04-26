package com.pxczxn.blog.community.node.repository;

import com.pxczxn.blog.community.node.entity.CommunityNode;
import com.pxczxn.blog.community.node.entity.CommunityNodeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;




public interface CommunityNodeRepository extends JpaRepository<CommunityNode, Long> {

    





    Optional<CommunityNode> findBySlug(String slug);

    






    Optional<CommunityNode> findBySlugAndStatus(String slug, CommunityNodeStatus status);

    





    boolean existsBySlug(String slug);

    





    boolean existsByName(String name);

    





    List<CommunityNode> findAllByStatusOrderBySortOrderAscCreatedAtAsc(CommunityNodeStatus status);
}
