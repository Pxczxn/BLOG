


package com.pxczxn.blog.community.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "community_token_revoked")
public class CommunityTokenRevoked {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Column(name = "jti", nullable = false, unique = true, length = 64)
    private String jti;

    
    @Column(name = "community_user_id", nullable = false)
    private Long communityUserId;

    
    @Column(name = "revoked_at", nullable = false)
    private LocalDateTime revokedAt;

    
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    
    @Column(name = "reason", length = 50)
    private String reason;
}
