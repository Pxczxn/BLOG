


package com.pxczxn.blog.community.interaction.repository;

import com.pxczxn.blog.community.interaction.entity.CommunityPostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CommunityPostLikeRepository extends JpaRepository<CommunityPostLike, Long> {

    






    boolean existsByPostIdAndUserId(Long postId, Long userId);

    





    long countByPostId(Long postId);

    






    Optional<CommunityPostLike> findByPostIdAndUserId(Long postId, Long userId);

    






    List<CommunityPostLike> findAllByUserIdAndPostIdIn(Long userId, Collection<Long> postIds);

    





    @Query("select p.postId as postId, count(p) as count from CommunityPostLike p where p.postId in :postIds group by p.postId")
    List<PostCountProjection> countByPostIds(Collection<Long> postIds);

    





    @Query("select p.postId as postId, count(p) as count from CommunityPostLike p group by p.postId order by count(p) desc")
    List<PostCountProjection> findTopPostCounts(Pageable pageable);
}

