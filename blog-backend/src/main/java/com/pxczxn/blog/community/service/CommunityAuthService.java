/**
 * 社区认证服务
 * <p>
 * 处理社区用户的注册、登录和令牌管理
 */
package com.pxczxn.blog.community.service;

import com.pxczxn.blog.community.dto.CommunityAuthResponse;
import com.pxczxn.blog.community.dto.CommunityLoginRequest;
import com.pxczxn.blog.community.dto.CommunityRegisterRequest;
import com.pxczxn.blog.community.entity.CommunityTokenRevoked;
import com.pxczxn.blog.community.entity.CommunityUser;
import com.pxczxn.blog.community.entity.CommunityUserRole;
import com.pxczxn.blog.community.entity.CommunityUserStatus;
import com.pxczxn.blog.community.exception.CommunityAuthException;
import com.pxczxn.blog.community.repository.CommunityTokenRevokedRepository;
import com.pxczxn.blog.community.repository.CommunityUserRepository;
import com.pxczxn.blog.user.entity.AdminUser;
import com.pxczxn.blog.user.entity.AdminUserStatus;
import com.pxczxn.blog.user.exception.UserAlreadyExistsException;
import com.pxczxn.blog.user.repository.AdminUserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommunityAuthService {

    private final CommunityUserRepository communityUserRepository;
    private final CommunityTokenRevokedRepository communityTokenRevokedRepository;
    private final AdminUserRepository adminUserRepository;
    private final CommunityJwtService communityJwtService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public CommunityAuthResponse register(CommunityRegisterRequest request, HttpServletRequest httpRequest) {
        String username = normalize(request.getUsername());
        String email = normalize(request.getEmail());

        if (communityUserRepository.existsByUsername(username)) {
            throw new UserAlreadyExistsException("username", username);
        }
        if (communityUserRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("email", email);
        }

        CommunityUser user = CommunityUser.builder()
                .username(username)
                .email(email)
                .displayName(request.getDisplayName().trim())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(CommunityUserRole.USER)
                .status(CommunityUserStatus.ACTIVE)
                .lastLoginAt(LocalDateTime.now())
                .build();
        user = communityUserRepository.save(user);
        return issueResponse(user);
    }

    @Transactional
    public CommunityAuthResponse login(CommunityLoginRequest request, HttpServletRequest httpRequest) {
        String identifier = normalize(request.getIdentifier());
        Optional<CommunityUser> communityUser = communityUserRepository.findByUsernameOrEmail(identifier, identifier);
        Optional<AdminUser> adminUser = adminUserRepository.findByUsernameOrEmail(identifier, identifier);

        if (communityUser.isPresent() && passwordEncoder.matches(request.getPassword(), communityUser.get().getPasswordHash())) {
            CommunityUser user = communityUser.get();
            if (user.getStatus() != CommunityUserStatus.ACTIVE) {
                throw CommunityAuthException.accountDisabled();
            }
            user.setLastLoginAt(LocalDateTime.now());
            return issueResponse(communityUserRepository.save(user));
        }

        if (adminUser.isEmpty() || !passwordEncoder.matches(request.getPassword(), adminUser.get().getPasswordHash())) {
            throw CommunityAuthException.invalidCredentials();
        }

        return issueResponse(syncAdminAsCommunityUser(adminUser.get(), communityUser));
    }

    @Transactional
    public void logout(String token) {
        Claims claims;
        try {
            claims = communityJwtService.extractClaims(token);
        } catch (Exception ex) {
            return;
        }

        String jti = claims.getId();
        if (jti == null || communityTokenRevokedRepository.existsByJti(jti)) {
            return;
        }

        CommunityTokenRevoked revoked = CommunityTokenRevoked.builder()
                .jti(jti)
                .communityUserId(Long.valueOf(claims.getSubject()))
                .revokedAt(LocalDateTime.now())
                .expiresAt(claims.getExpiration().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .reason("LOGOUT")
                .build();
        communityTokenRevokedRepository.save(revoked);
    }

    private CommunityAuthResponse issueResponse(CommunityUser user) {
        String token = communityJwtService.generateToken(user.getId(), user.getUsername(), user.getRole().name());
        return CommunityAuthResponse.from(user, token, communityJwtService.getExpirationDate(token).toInstant());
    }

    private CommunityUser syncAdminAsCommunityUser(AdminUser admin, Optional<CommunityUser> existingCommunityUser) {
        if (AdminUserStatus.ACTIVE != admin.getStatus()) {
            throw CommunityAuthException.accountDisabled();
        }

        CommunityUser user = existingCommunityUser.orElseGet(() -> communityUserRepository.findByUsernameOrEmail(
                normalize(admin.getUsername()),
                normalize(admin.getEmail())
        ).orElseGet(CommunityUser::new));

        user.setUsername(admin.getUsername());
        user.setEmail(admin.getEmail());
        user.setDisplayName(admin.getUsername());
        user.setPasswordHash(admin.getPasswordHash());
        user.setRole(CommunityUserRole.MODERATOR);
        user.setStatus(CommunityUserStatus.ACTIVE);
        user.setLastLoginAt(LocalDateTime.now());
        return communityUserRepository.save(user);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}

