/**
 * 社区用户关注关系数据访问接口
 */
package com.pxczxn.blog.community.interaction.repository;

import com.pxczxn.blog.community.interaction.entity.CommunityUserFollow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommunityUserFollowRepository extends JpaRepository<CommunityUserFollow, Long> {

    /**
     * 检查是否存在关注关系
     *
     * @param followerId  关注者用户ID
     * @param followingId 被关注者用户ID
     * @return 是否存在关注关系
     */
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    /**
     * 统计用户的关注数
     *
     * @param followerId 关注者用户ID
     * @return 关注数
     */
    long countByFollowerId(Long followerId);

    /**
     * 统计用户的粉丝数
     *
     * @param followingId 被关注者用户ID
     * @return 粉丝数
     */
    long countByFollowingId(Long followingId);

    /**
     * 根据关注者和被关注者ID查找关注记录
     *
     * @param followerId  关注者用户ID
     * @param followingId 被关注者用户ID
     * @return 关注记录
     */
    Optional<CommunityUserFollow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);
}

