/**
 * 已撤销令牌实体
 * <p>
 * 记录已被主动撤销（登出）的 JWT 令牌，实现令牌黑名单机制。
 * 每次验证令牌时都会检查是否在此表中，以防止已登出的令牌被复用。
 */
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

    /** 主键ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** 令牌唯一标识（JWT ID），用于黑名单查询 */
    @Column(name = "jti", nullable = false, unique = true, length = 64)
    private String jti;

    /** 令牌所属用户ID */
    @Column(name = "admin_user_id", nullable = false)
    private Long adminUserId;

    /** 撤销时间 */
    @Column(name = "revoked_at", nullable = false)
    private LocalDateTime revokedAt;

    /** 令牌原始过期时间（用于清理过期记录） */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /** 撤销原因，如 LOGOUT、FORCE_LOGOUT */
    @Column(name = "reason", length = 50)
    private String reason;
}

