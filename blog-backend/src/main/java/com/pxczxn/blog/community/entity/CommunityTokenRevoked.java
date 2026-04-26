/**
 * 社区用户已吊销令牌实体，记录已被注销或失效的 JWT 令牌
 */
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

    /** 主键 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** JWT 令牌的唯一标识符（JTI） */
    @Column(name = "jti", nullable = false, unique = true, length = 64)
    private String jti;

    /** 所属社区用户 ID */
    @Column(name = "community_user_id", nullable = false)
    private Long communityUserId;

    /** 令牌吊销时间 */
    @Column(name = "revoked_at", nullable = false)
    private LocalDateTime revokedAt;

    /** 令牌原始过期时间 */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /** 吊销原因（如 logout、password_change 等） */
    @Column(name = "reason", length = 50)
    private String reason;
}
