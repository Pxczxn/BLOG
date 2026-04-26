/**
 * 设备会话数据访问层
 * <p>
 * 提供设备会话实体的 CRUD 操作及按用户ID、设备ID查询等方法。
 */
package com.pxczxn.blog.auth.repository;

import com.pxczxn.blog.auth.entity.DeviceSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceSessionRepository extends JpaRepository<DeviceSession, Long> {

    /** 按用户ID查询所有活跃会话，按最后活跃时间升序（最早活跃的排前面） */
    List<DeviceSession> findByAdminUser_IdAndIsActiveTrueOrderByLastSeenAtAsc(Long adminUserId);

    /** 按用户ID查询所有活跃会话 */
    List<DeviceSession> findByAdminUser_IdAndIsActiveTrue(Long adminUserId);

    /** 按用户ID和设备ID查询活跃会话 */
    Optional<DeviceSession> findByAdminUser_IdAndDeviceIdAndIsActiveTrue(Long adminUserId, String deviceId);
}

