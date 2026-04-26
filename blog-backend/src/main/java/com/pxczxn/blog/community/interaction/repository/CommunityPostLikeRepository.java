/**
 * 社区帖子点赞数据访问接口
 */
package com.pxczxn.blog.community.interaction.repository;

import com.pxczxn.blog.community.interaction.entity.CommunityPostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CommunityPostLikeRepository extends JpaRepository<CommunityPostLike, Long> {

    /**
     * 检查帖子是否已被用户点赞
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 是否已点赞
     */
    boolean existsByPostIdAndUserId(Long postId, Long userId);

    /**
     * 统计帖子的点赞数
     *
     * @param postId 帖子ID
     * @return 点赞数
     */
    long countByPostId(Long postId);

    /**
     * 根据帖子ID和用户ID查找点赞记录
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 点赞记录
     */
    Optional<CommunityPostLike> findByPostIdAndUserId(Long postId, Long userId);

    /**
     * 根据用户ID和帖子ID列表批量查询点赞记录
     *
     * @param userId  用户ID
     * @param postIds 帖子ID列表
     * @return 点赞记录列表
     */
    List<CommunityPostLike> findAllByUserIdAndPostIdIn(Long userId, Collection<Long> postIds);

    /**
     * 根据帖子ID列表统计点赞数
     *
     * @param postIds 帖子ID列表
     * @return 帖子点赞数统计列表
     */
    @Query("select p.postId as postId, count(p) as count from CommunityPostLike p where p.postId in :postIds group by p.postId")
    List<PostCountProjection> countByPostIds(Collection<Long> postIds);

    /**
     * 查询点赞数最高的帖子统计
     *
     * @param pageable 分页参数
     * @return 帖子点赞数统计列表
     */
    @Query("select p.postId as postId, count(p) as count from CommunityPostLike p group by p.postId order by count(p) desc")
    List<PostCountProjection> findTopPostCounts(Pageable pageable);
}

