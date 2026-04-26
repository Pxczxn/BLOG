/**
 * 设备会话服务
 * <p>
 * 管理用户的多设备登录会话，包括创建、更新、失效会话等操作。
 * 支持配置最大设备数量，超出限制时自动踢出最早活跃的设备。
 */
package com.pxczxn.blog.auth.service;

import com.pxczxn.blog.auth.entity.DeviceSession;
import com.pxczxn.blog.auth.repository.DeviceSessionRepository;
import com.pxczxn.blog.user.entity.AdminUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceSessionService {

    private final DeviceSessionRepository deviceSessionRepository;

    /** 最大同时在线设备数，默认为 3 */
    @Value("${jwt.device.limit:3}")
    private int maxDevices;

    /**
     * 创建或更新设备会话
     * <p>
     * 若设备已有活跃会话则更新 lastSeenAt 和 IP 等信息，
     * 否则创建新会话。创建前会检查设备数量限制，超限时踢出最早活跃的设备。
     *
     * @param user       管理员用户
     * @param deviceId   设备唯一标识
     * @param deviceName 设备名称（如 "Chrome on Windows"）
     * @param ip         客户端 IP 地址
     * @param userAgent  User-Agent 字符串
     * @return 创建或更新后的设备会话
     */
    @Transactional
    public DeviceSession createOrUpdateSession(AdminUser user, String deviceId, String deviceName,
                                               String ip, String userAgent) {
        Optional<DeviceSession> existingSession = deviceSessionRepository
                .findByAdminUser_IdAndDeviceIdAndIsActiveTrue(user.getId(), deviceId);

        DeviceSession session;
        if (existingSession.isPresent()) {
            session = existingSession.get();
            session.setLastSeenAt(LocalDateTime.now());
            session.setIp(ip);
            session.setUserAgent(userAgent);
            if (deviceName != null && !deviceName.isEmpty()) {
                session.setDeviceName(deviceName);
            }
            session = deviceSessionRepository.save(session);
            log.debug("复用已有设备会话: userId={}, deviceId={}", user.getId(), deviceId);
        } else {
            // 创建新会话前检查设备数量限制。
            enforceDeviceLimit(user.getId());

            session = DeviceSession.builder()
                    .adminUser(user)
                    .deviceId(deviceId)
                    .deviceName(deviceName)
                    .ip(ip)
                    .userAgent(userAgent)
                    .createdAt(LocalDateTime.now())
                    .lastSeenAt(LocalDateTime.now())
                    .isActive(true)
                    .build();
            session = deviceSessionRepository.save(session);
            log.debug("创建设备会话成功: userId={}, deviceId={}", user.getId(), deviceId);
        }
        return session;
    }

    /**
     * 强制执行设备数量限制
     * <p>
     * 若活跃设备数已达上限，将最早活跃的设备会话标记为失效（踢出）。
     */
    private void enforceDeviceLimit(Long userId) {
        List<DeviceSession> activeSessions = deviceSessionRepository
                .findByAdminUser_IdAndIsActiveTrueOrderByLastSeenAtAsc(userId);

        if (activeSessions.size() >= maxDevices) {
            DeviceSession oldestSession = activeSessions.get(0);
            oldestSession.setIsActive(false);
            deviceSessionRepository.save(oldestSession);
            log.info("超过设备数量限制，已踢出最早会话: userId={}, deviceId={}, kickedAt={}",
                    userId, oldestSession.getDeviceId(), LocalDateTime.now());
        }
    }

    /** 使指定会话失效（按会话ID） */
    @Transactional
    public void deactivateSession(Long sessionId) {
        deviceSessionRepository.findById(sessionId).ifPresent(session -> {
            session.setIsActive(false);
            deviceSessionRepository.save(session);
        });
    }

    /** 使某用户的所有设备会话失效（强制全部下线） */
    @Transactional
    public void deactivateAllSessions(Long userId) {
        List<DeviceSession> sessions = deviceSessionRepository
                .findByAdminUser_IdAndIsActiveTrue(userId);
        sessions.forEach(s -> s.setIsActive(false));
        deviceSessionRepository.saveAll(sessions);
        log.info("用户所有设备会话已失效: userId={}, count={}", userId, sessions.size());
    }

    /** 使指定设备的会话失效（按用户ID+设备ID，用于登出时踢出设备） */
    @Transactional
    public void deactivateSessionByDeviceId(Long userId, String deviceId) {
        deviceSessionRepository.findByAdminUser_IdAndDeviceIdAndIsActiveTrue(userId, deviceId)
                .ifPresent(session -> {
                    session.setIsActive(false);
                    deviceSessionRepository.save(session);
                    log.info("设备会话已失效: userId={}, deviceId={}", userId, deviceId);
                });
    }

    public Optional<DeviceSession> findActiveSession(Long userId, String deviceId) {
        return deviceSessionRepository.findByAdminUser_IdAndDeviceIdAndIsActiveTrue(userId, deviceId);
    }

    public List<DeviceSession> getActiveSessions(Long userId) {
        return deviceSessionRepository.findByAdminUser_IdAndIsActiveTrueOrderByLastSeenAtAsc(userId);
    }
}
