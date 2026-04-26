


package com.pxczxn.blog.community.interaction.repository;

import com.pxczxn.blog.community.interaction.entity.CommunityNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface CommunityNotificationRepository extends JpaRepository<CommunityNotification, Long> {

    






    Page<CommunityNotification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    






    Page<CommunityNotification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId, Pageable pageable);

    





    long countByUserIdAndIsReadFalse(Long userId);

    




    long countByIsReadFalse();

    






    @Modifying
    @Query("update CommunityNotification n set n.isRead = true, n.readAt = :readAt where n.userId = :userId and n.isRead = false")
    int markAllReadByUserId(Long userId, LocalDateTime readAt);
}

