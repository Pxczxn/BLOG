


package com.pxczxn.blog.community.interaction.repository;

import com.pxczxn.blog.community.interaction.entity.CommunityPostFavorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CommunityPostFavoriteRepository extends JpaRepository<CommunityPostFavorite, Long> {

    






    boolean existsByPostIdAndUserId(Long postId, Long userId);

    





    long countByPostId(Long postId);

    






    Optional<CommunityPostFavorite> findByPostIdAndUserId(Long postId, Long userId);

    






    Page<CommunityPostFavorite> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    






    @Query(
            value = """
                    select f.*
                    from community_post_favorite f
                    join community_post p on p.id = f.post_id
                    where f.user_id = :userId
                      and p.status = 'PUBLISHED'
                    order by f.created_at desc
                    """,
            countQuery = """
                    select count(*)
                    from community_post_favorite f
                    join community_post p on p.id = f.post_id
                    where f.user_id = :userId
                      and p.status = 'PUBLISHED'
                    """,
            nativeQuery = true
    )
    Page<CommunityPostFavorite> findPublishedByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);

    






    List<CommunityPostFavorite> findAllByUserIdAndPostIdIn(Long userId, Collection<Long> postIds);

    





    @Query("select p.postId as postId, count(p) as count from CommunityPostFavorite p where p.postId in :postIds group by p.postId")
    List<PostCountProjection> countByPostIds(Collection<Long> postIds);

    





    @Query("select p.postId as postId, count(p) as count from CommunityPostFavorite p group by p.postId order by count(p) desc")
    List<PostCountProjection> findTopPostCounts(Pageable pageable);
}

