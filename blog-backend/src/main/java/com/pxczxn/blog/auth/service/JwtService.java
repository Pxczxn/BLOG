/**
 * JWT 令牌服务
 * <p>
 * 负责JWT令牌的生成、解析、验证等核心功能。
 * 使用 HMAC-SHA 算法签名，支持令牌过期时间配置和黑名单机制。
 */
package com.pxczxn.blog.auth.service;

import com.pxczxn.blog.auth.dto.TokenValidationResult;
import com.pxczxn.blog.auth.repository.DeviceSessionRepository;
import com.pxczxn.blog.auth.repository.TokenRevokedRepository;
import com.pxczxn.blog.common.response.ApiErrorCode;
import com.pxczxn.blog.user.entity.AdminUserStatus;
import com.pxczxn.blog.user.repository.AdminUserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class JwtService {

    /** JWT 签名密钥，使用 HMAC-SHA 算法 */
    private final SecretKey key;
    /** 令牌过期时间（毫秒），默认 24 小时 */
    private final long jwtExpiration;
    private final TokenRevokedRepository tokenRevokedRepository;
    private final DeviceSessionRepository deviceSessionRepository;
    private final AdminUserRepository adminUserRepository;

    /**
     * 构造函数，初始化 JWT 服务
     *
     * @param secret           JWT 签名密钥，至少 32 个字符
     * @param jwtExpiration    令牌过期时间（毫秒）
     * @param tokenRevokedRepository 令牌黑名单仓库
     * @param deviceSessionRepository 设备会话仓库
     * @param adminUserRepository     管理员用户仓库
     * @throws IllegalStateException 如果密钥为空或长度不足
     */
    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expiration:86400000}") long jwtExpiration,
                      TokenRevokedRepository tokenRevokedRepository,
                      DeviceSessionRepository deviceSessionRepository,
                      AdminUserRepository adminUserRepository) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT_SECRET 不能为空");
        }
        if (secret.length() < 32) {
            throw new IllegalStateException("JWT_SECRET 长度不能少于 32 个字符");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.jwtExpiration = jwtExpiration;
        this.tokenRevokedRepository = tokenRevokedRepository;
        this.deviceSessionRepository = deviceSessionRepository;
        this.adminUserRepository = adminUserRepository;
    }

    /**
     * 生成 JWT 令牌
     * <p>
     * 将用户ID、用户名、设备ID和角色信息写入 Claims，设置唯一标识（jti）和过期时间。
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param deviceId 设备ID
     * @param role     用户角色
     * @return 签名后的 JWT 令牌字符串
     */
    public String generateToken(Long userId, String username, String deviceId, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(String.valueOf(userId))
                .claim("username", username)
                .claim("deviceId", deviceId)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /**
     * 解析令牌获取 Claims
     *
     * @param token JWT 令牌
     * @return 解析后的 Claims 对象
     * @throws Exception 如果令牌格式无效或签名验证失败
     */
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /** 从令牌中提取用户ID（subject） */
    public String extractUserId(String token) {
        return extractClaims(token).getSubject();
    }

    /** 从令牌中提取用户名 */
    public String extractUsername(String token) {
        return extractClaims(token).get("username", String.class);
    }

    /** 从令牌中提取设备ID */
    public String extractDeviceId(String token) {
        return extractClaims(token).get("deviceId", String.class);
    }

    /** 从令牌中提取角色 */
    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    /** 从令牌中提取唯一标识（jti），用于令牌黑名单查询 */
    public String extractJti(String token) {
        return extractClaims(token).getId();
    }

    /** 检查令牌是否已过期 */
    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    /** 简化版验证，仅返回是否有效 */
    public boolean isTokenValid(String token) {
        return validateToken(token).valid();
    }

    /**
     * 完整验证 JWT 令牌
     * <p>
     * 验证流程：1)签名和格式校验 → 2)过期检查 → 3)黑名单检查 →
     * 4)用户存在性检查 → 5)账号状态检查 → 6)设备会话有效性检查。
     *
     * @param token 待验证的 JWT 令牌
     * @return 验证结果，包含是否有效、Claims 或错误码
     */
    public TokenValidationResult validateToken(String token) {
        try {
            Claims claims = extractClaims(token);
            // 1. 检查令牌是否过期
            if (claims.getExpiration().before(new Date())) {
                return TokenValidationResult.invalid(ApiErrorCode.AUTH_TOKEN_EXPIRED);
            }

            // 2. 检查令牌是否已被撤销（登出后加入黑名单）
            String jti = claims.getId();
            if (jti != null && tokenRevokedRepository.existsByJti(jti)) {
                log.warn("Token has been revoked: jti={}", jti);
                return TokenValidationResult.invalid(ApiErrorCode.AUTH_SESSION_EXPIRED);
            }

            // 3. 检查设备ID是否存在
            Long userId = Long.valueOf(claims.getSubject());
            String deviceId = claims.get("deviceId", String.class);
            if (deviceId == null || deviceId.isBlank()) {
                return TokenValidationResult.invalid(ApiErrorCode.AUTH_INVALID_TOKEN);
            }

            // 4. 检查用户是否存在
            var user = adminUserRepository.findById(userId).orElse(null);
            if (user == null) {
                return TokenValidationResult.invalid(ApiErrorCode.AUTH_SESSION_EXPIRED);
            }
            // 5. 检查账号是否被禁用
            if (AdminUserStatus.BANNED.equals(user.getStatus())) {
                return TokenValidationResult.invalid(ApiErrorCode.AUTH_ACCOUNT_DISABLED);
            }
            // 6. 检查设备会话是否仍然有效
            if (deviceSessionRepository.findByAdminUser_IdAndDeviceIdAndIsActiveTrue(userId, deviceId).isEmpty()) {
                return TokenValidationResult.invalid(ApiErrorCode.AUTH_SESSION_EXPIRED);
            }

            return TokenValidationResult.valid(claims);
        } catch (ExpiredJwtException ex) {
            log.warn("Token expired: {}", ex.getMessage());
            return TokenValidationResult.invalid(ApiErrorCode.AUTH_TOKEN_EXPIRED);
        } catch (Exception ex) {
            log.warn("Token validation failed: {}", ex.getMessage());
            return TokenValidationResult.invalid(ApiErrorCode.AUTH_INVALID_TOKEN);
        }
    }

    public Date getExpirationDate(String token) {
        return extractClaims(token).getExpiration();
    }
}