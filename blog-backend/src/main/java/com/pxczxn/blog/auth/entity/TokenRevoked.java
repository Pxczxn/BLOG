





package com.pxczxn.blog.auth.entity;

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
@Table(name = "token_revoked")
public class TokenRevoked {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    
    @Column(name = "jti", nullable = false, unique = true, length = 64)
    private String jti;

    
    @Column(name = "admin_user_id", nullable = false)
    private Long adminUserId;

    
    @Column(name = "revoked_at", nullable = false)
    private LocalDateTime revokedAt;

    
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    
    @Column(name = "reason", length = 50)
    private String reason;
}

