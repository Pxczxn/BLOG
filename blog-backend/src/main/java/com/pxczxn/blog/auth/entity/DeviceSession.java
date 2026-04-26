





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

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_device_session_admin_user"))
    private AdminUser adminUser;

    
    @Column(name = "device_id", nullable = false, length = 200)
    private String deviceId;

    
    @Column(name = "device_name", length = 200)
    private String deviceName;

    
    @Column(name = "ip", nullable = false, length = 45)
    private String ip;

    
    @Lob
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    
    @Column(name = "last_seen_at", nullable = false)
    private LocalDateTime lastSeenAt;

    
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    
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

