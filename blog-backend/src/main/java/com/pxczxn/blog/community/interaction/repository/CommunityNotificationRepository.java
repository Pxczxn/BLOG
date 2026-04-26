/**
 * 社区通知数据访问接口
 */
package com.pxczxn.blog.community.interaction.repository;

import com.pxczxn.blog.community.interaction.entity.CommunityNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface CommunityNotificationRepository extends JpaRepository<CommunityNotification, Long> {

    /**
     * 根据用户ID分页查询通知（按创建时间降序）
     *
     * @param userId   用户ID
     * @param pageable 分页参数
     * @return 通知分页列表
     */
    Page<CommunityNotification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * 根据用户ID分页查询未读通知（按创建时间降序）
     *
     * @param userId   用户ID
     * @param pageable 分页参数
     * @return 未读通知分页列表
     */
    Page<CommunityNotification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * 统计用户未读通知数量
     *
     * @param userId 用户ID
     * @return 未读通知数量
     */
    long countByUserIdAndIsReadFalse(Long userId);

    /**
     * 统计所有未读通知数量
     *
     * @return 未读通知数量
     */
    long countByIsReadFalse();

    /**
     * 将用户所有通知标记为已读
     *
     * @param userId 用户ID
     * @param readAt 已读时间
     * @return 更新的记录数
     */
    @Modifying
    @Query("update CommunityNotification n set n.isRead = true, n.readAt = :readAt where n.userId = :userId and n.isRead = false")
    int markAllReadByUserId(Long userId, LocalDateTime readAt);
}

