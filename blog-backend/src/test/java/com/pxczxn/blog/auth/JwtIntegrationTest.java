package com.pxczxn.blog.auth;

import com.pxczxn.blog.auth.dto.LoginRequest;
import com.pxczxn.blog.auth.repository.DeviceSessionRepository;
import com.pxczxn.blog.auth.repository.TokenRevokedRepository;
import com.pxczxn.blog.auth.service.DeviceSessionService;
import com.pxczxn.blog.auth.service.JwtService;
import com.pxczxn.blog.auth.service.LoginService;
import com.pxczxn.blog.auth.service.LogoutService;
import com.pxczxn.blog.user.dto.AdminUserCreateRequest;
import com.pxczxn.blog.user.service.AdminUserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class JwtIntegrationTest {

    @Autowired
    private LoginService loginService;

    @Autowired
    private LogoutService logoutService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private DeviceSessionService deviceSessionService;

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private DeviceSessionRepository deviceSessionRepository;

    @Autowired
    private TokenRevokedRepository tokenRevokedRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String TEST_USERNAME = "jwttuser";
    private static final String TEST_EMAIL = "jwtuser@example.com";
    private static final String TEST_PASSWORD = "password123";

    @BeforeEach
    void setup() {
        resetDatabase();

        AdminUserCreateRequest request = new AdminUserCreateRequest();
        request.setUsername(TEST_USERNAME);
        request.setEmail(TEST_EMAIL);
        request.setPassword(TEST_PASSWORD);
        adminUserService.create(request);
    }

    private void resetDatabase() {
        deleteIfExists("DELETE FROM article_tag");
        deleteIfExists("DELETE FROM comment");
        deleteIfExists("DELETE FROM article");
        deleteIfExists("DELETE FROM token_revoked");
        deleteIfExists("DELETE FROM device_session");
        deleteIfExists("DELETE FROM admin_user");
    }

    private void deleteIfExists(String sql) {
        try {
            jdbcTemplate.update(sql);
        } catch (DataAccessException ignored) {
        }
    }

    @Test
    void testLogin_ReturnsToken() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(TEST_USERNAME);
        loginRequest.setPassword(TEST_PASSWORD);
        loginRequest.setDeviceId("device-001");

        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        when(mockRequest.getHeader("User-Agent")).thenReturn("TestAgent");
        when(mockRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        var response = loginService.login(loginRequest, mockRequest);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertFalse(response.getToken().isEmpty());
        assertNotNull(response.getExpiresAt());
        assertEquals(TEST_USERNAME, response.getUsername());
    }

    @Test
    void testJwtToken_ValidatesCorrectly() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(TEST_USERNAME);
        loginRequest.setPassword(TEST_PASSWORD);
        loginRequest.setDeviceId("device-001");

        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        when(mockRequest.getHeader("User-Agent")).thenReturn("TestAgent");
        when(mockRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        var response = loginService.login(loginRequest, mockRequest);
        String token = response.getToken();

        assertTrue(jwtService.isTokenValid(token));
        assertEquals(response.getUserId().toString(), jwtService.extractUserId(token));
        assertEquals(TEST_USERNAME, jwtService.extractUsername(token));
        assertEquals("device-001", jwtService.extractDeviceId(token));
    }

    @Test
    void testLogout_TokenBlacklisted() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(TEST_USERNAME);
        loginRequest.setPassword(TEST_PASSWORD);
        loginRequest.setDeviceId("device-001");

        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        when(mockRequest.getHeader("User-Agent")).thenReturn("TestAgent");
        when(mockRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        var response = loginService.login(loginRequest, mockRequest);
        String token = response.getToken();
        String jti = jwtService.extractJti(token);

        assertTrue(jwtService.isTokenValid(token));

        logoutService.logout(token);

        assertTrue(tokenRevokedRepository.existsByJti(jti));
        assertFalse(jwtService.isTokenValid(token));
    }

    @Test
    void testDeviceLimit_4Devices_KicksOldest() {
        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        when(mockRequest.getHeader("User-Agent")).thenReturn("TestAgent");
        when(mockRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        for (int i = 1; i <= 4; i++) {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setUsername(TEST_USERNAME);
            loginRequest.setPassword(TEST_PASSWORD);
            loginRequest.setDeviceId("device-" + String.format("%03d", i));

            loginService.login(loginRequest, mockRequest);
        }

        Long userId = adminUserService.getByUsername(TEST_USERNAME).getId();
        var sessions = deviceSessionService.getActiveSessions(userId);

        assertEquals(3, sessions.size());

        var device001Session = sessions.stream()
                .filter(s -> "device-001".equals(s.getDeviceId()))
                .findFirst();

        assertFalse(device001Session.isPresent());

        var allSessions = deviceSessionRepository.findAll();
        var device001 = allSessions.stream()
                .filter(s -> "device-001".equals(s.getDeviceId()))
                .findFirst();
        assertTrue(device001.isPresent());
        assertFalse(device001.get().getIsActive());
    }

    @Test
    void testLogout_DeactivatesDeviceSession() {
        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        when(mockRequest.getHeader("User-Agent")).thenReturn("TestAgent");
        when(mockRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(TEST_USERNAME);
        loginRequest.setPassword(TEST_PASSWORD);
        loginRequest.setDeviceId("device-001");

        var response = loginService.login(loginRequest, mockRequest);
        String token = response.getToken();
        Long userId = response.getUserId();

        assertTrue(deviceSessionService.findActiveSession(userId, "device-001").isPresent());

        logoutService.logout(token);

        assertFalse(deviceSessionService.findActiveSession(userId, "device-001").isPresent());
    }
}
