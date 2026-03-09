package com.pxczxn.blog.auth.repository;

import com.pxczxn.blog.auth.entity.DeviceSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceSessionRepository extends JpaRepository<DeviceSession, Long> {

    List<DeviceSession> findByAdminUser_IdAndIsActiveTrueOrderByLastSeenAtAsc(Long adminUserId);

    List<DeviceSession> findByAdminUser_IdAndIsActiveTrue(Long adminUserId);

    Optional<DeviceSession> findByAdminUser_IdAndDeviceIdAndIsActiveTrue(Long adminUserId, String deviceId);
}
