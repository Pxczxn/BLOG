/**
 * 社区帖子收藏数据访问接口
 */
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

    /**
     * 检查帖子是否已被用户收藏
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 是否已收藏
     */
    boolean existsByPostIdAndUserId(Long postId, Long userId);

    /**
     * 统计帖子的收藏数
     *
     * @param postId 帖子ID
     * @return 收藏数
     */
    long countByPostId(Long postId);

    /**
     * 根据帖子ID和用户ID查找收藏记录
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 收藏记录
     */
    Optional<CommunityPostFavorite> findByPostIdAndUserId(Long postId, Long userId);

    /**
     * 根据用户ID分页查询收藏记录（按创建时间降序）
     *
     * @param userId   用户ID
     * @param pageable 分页参数
     * @return 收藏记录分页列表
     */
    Page<CommunityPostFavorite> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * 根据用户ID分页查询已发布帖子的收藏记录（按创建时间降序）
     *
     * @param userId   用户ID
     * @param pageable 分页参数
     * @return 收藏记录分页列表
     */
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

    /**
     * 根据用户ID和帖子ID列表批量查询收藏记录
     *
     * @param userId  用户ID
     * @param postIds 帖子ID列表
     * @return 收藏记录列表
     */
    List<CommunityPostFavorite> findAllByUserIdAndPostIdIn(Long userId, Collection<Long> postIds);

    /**
     * 根据帖子ID列表统计收藏数
     *
     * @param postIds 帖子ID列表
     * @return 帖子收藏数统计列表
     */
    @Query("select p.postId as postId, count(p) as count from CommunityPostFavorite p where p.postId in :postIds group by p.postId")
    List<PostCountProjection> countByPostIds(Collection<Long> postIds);

    /**
     * 查询收藏数最高的帖子统计
     *
     * @param pageable 分页参数
     * @return 帖子收藏数统计列表
     */
    @Query("select p.postId as postId, count(p) as count from CommunityPostFavorite p group by p.postId order by count(p) desc")
    List<PostCountProjection> findTopPostCounts(Pageable pageable);
}

