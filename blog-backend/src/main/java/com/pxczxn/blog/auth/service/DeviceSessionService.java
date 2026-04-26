





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

    
    @Value("${jwt.device.limit:3}")
    private int maxDevices;

    












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

    
    @Transactional
    public void deactivateSession(Long sessionId) {
        deviceSessionRepository.findById(sessionId).ifPresent(session -> {
            session.setIsActive(false);
            deviceSessionRepository.save(session);
        });
    }

    
    @Transactional
    public void deactivateAllSessions(Long userId) {
        List<DeviceSession> sessions = deviceSessionRepository
                .findByAdminUser_IdAndIsActiveTrue(userId);
        sessions.forEach(s -> s.setIsActive(false));
        deviceSessionRepository.saveAll(sessions);
        log.info("用户所有设备会话已失效: userId={}, count={}", userId, sessions.size());
    }

    
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
