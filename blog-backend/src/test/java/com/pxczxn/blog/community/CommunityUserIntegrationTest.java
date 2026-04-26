


package com.pxczxn.blog.community;

import com.pxczxn.blog.auth.dto.TokenValidationResult;
import com.pxczxn.blog.community.dto.CommunityLoginRequest;
import com.pxczxn.blog.community.dto.CommunityProfileUpdateRequest;
import com.pxczxn.blog.community.dto.CommunityRegisterRequest;
import com.pxczxn.blog.community.service.CommunityAuthService;
import com.pxczxn.blog.community.service.CommunityJwtService;
import com.pxczxn.blog.community.service.CommunityProfileService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class CommunityUserIntegrationTest {

    @Autowired
    private CommunityAuthService communityAuthService;

    @Autowired
    private CommunityJwtService communityJwtService;

    @Autowired
    private CommunityProfileService communityProfileService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private HttpServletRequest mockRequest;

    @BeforeEach
    void setUp() {
        resetDatabase();
        mockRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        Mockito.when(mockRequest.getHeader("User-Agent")).thenReturn("JUnit");
    }

    @Test
    void registerAndLogoutInvalidateCommunityToken() {
        CommunityRegisterRequest request = new CommunityRegisterRequest();
        request.setUsername("community-user");
        request.setDisplayName("Community User");
        request.setEmail("community@example.com");
        request.setPassword("communityPass123");

        var response = communityAuthService.register(request, mockRequest);

        assertNotNull(response.getToken());
        TokenValidationResult validationResult = communityJwtService.validateToken(response.getToken());
        assertEquals(true, validationResult.valid());

        communityAuthService.logout(response.getToken());

        assertFalse(communityJwtService.validateToken(response.getToken()).valid());
    }

    @Test
    void updateProfileReflectsOnPublicProfile() {
        CommunityRegisterRequest request = new CommunityRegisterRequest();
        request.setUsername("profile-user");
        request.setDisplayName("Profile User");
        request.setEmail("profile@example.com");
        request.setPassword("profilePass123");

        var response = communityAuthService.register(request, mockRequest);

        CommunityProfileUpdateRequest updateRequest = new CommunityProfileUpdateRequest();
        updateRequest.setDisplayName("Updated Profile");
        updateRequest.setBio("I write code and comments.");
        updateRequest.setWebsite("https://example.com");

        var me = communityProfileService.updateMe(response.getUserId(), updateRequest);
        var publicProfile = communityProfileService.getPublicProfile(response.getUsername(), null);

        assertEquals("Updated Profile", me.getDisplayName());
        assertEquals("Updated Profile", publicProfile.getDisplayName());
        assertEquals("I write code and comments.", publicProfile.getBio());
        assertEquals("https://example.com", publicProfile.getWebsite());
    }

    @Test
    void adminCredentialsCanLoginToBlogAsCommunityIdentity() {
        jdbcTemplate.update(
                """
                        INSERT INTO admin_user (username, email, password_hash, status, role, created_at, updated_at)
                        VALUES (?, ?, ?, 'ACTIVE', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                        """,
                "site-admin",
                "site-admin@example.com",
                passwordEncoder.encode("adminPass123")
        );

        CommunityLoginRequest request = new CommunityLoginRequest();
        request.setIdentifier("site-admin");
        request.setPassword("adminPass123");

        var response = communityAuthService.login(request, mockRequest);

        assertNotNull(response.getToken());
        assertEquals("site-admin", response.getUsername());
        assertEquals("MODERATOR", response.getRole());
        assertEquals(1, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM community_user WHERE username = 'site-admin'",
                Integer.class
        ));
    }

    private void resetDatabase() {
        deleteIfExists("DELETE FROM community_token_revoked");
        deleteIfExists("DELETE FROM comment");
        deleteIfExists("DELETE FROM article_tag");
        deleteIfExists("DELETE FROM article");
        deleteIfExists("DELETE FROM token_revoked");
        deleteIfExists("DELETE FROM device_session");
        deleteIfExists("DELETE FROM community_user");
        deleteIfExists("DELETE FROM admin_user");
    }

    private void deleteIfExists(String sql) {
        try {
            jdbcTemplate.update(sql);
        } catch (DataAccessException ignored) {
        }
    }
}

