


package com.pxczxn.blog.community.interaction.repository;

import com.pxczxn.blog.community.interaction.entity.CommunityUserFollow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommunityUserFollowRepository extends JpaRepository<CommunityUserFollow, Long> {

    






    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    





    long countByFollowerId(Long followerId);

    





    long countByFollowingId(Long followingId);

    






    Optional<CommunityUserFollow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);
}

