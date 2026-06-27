/**
 * 设备会话实体
 * <p>
 * 记录管理员在不同设备上的登录会话信息，包括设备ID、设备名称、IP地址、User-Agent 等。
 * 用于实现多设备登录管理和设备踢出功能。
 */
package com.pxczxn.blog.auth.entity;

import com.pxczxn.blog.user.entity.AdminUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "device_session")
public class DeviceSession {

    /** 会话ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** 关联的管理员用户 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_device_session_admin_user"))
    private AdminUser adminUser;

    /** 设备唯一标识（前端生成或服务端根据 IP+UA 哈希生成） */
    @Column(name = "device_id", nullable = false, length = 200)
    private String deviceId;

    /** 设备名称，如 "Chrome on Windows" */
    @Column(name = "device_name", length = 200)
    private String deviceName;

    /** 客户端 IP 地址 */
    @Column(name = "ip", nullable = false, length = 45)
    private String ip;

    /** User-Agent 字符串 */
    @Lob
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    /** 会话创建时间 */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 最后活跃时间 */
    @Column(name = "last_seen_at", nullable = false)
    private LocalDateTime lastSeenAt;

    /** 会话是否活跃（登出后设为 false） */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "refresh_token_hash", length = 128)
    private String refreshTokenHash;

    @Column(name = "refresh_token_expires_at")
    private LocalDateTime refreshTokenExpiresAt;

    /** 持久化前自动设置时间字段 */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (lastSeenAt == null) {
            lastSeenAt = now;
        }
    }
}

