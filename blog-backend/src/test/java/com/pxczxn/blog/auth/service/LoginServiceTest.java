/*
 * 功能：后台认证相关实现。
 */
package com.pxczxn.blog.auth.service;

import com.pxczxn.blog.auth.dto.LoginRequest;
import com.pxczxn.blog.auth.exception.LoginException;
import com.pxczxn.blog.auth.repository.DeviceSessionRepository;
import com.pxczxn.blog.common.response.ApiErrorCode;
import com.pxczxn.blog.user.dto.AdminUserCreateRequest;
import com.pxczxn.blog.user.repository.AdminUserRepository;
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
class LoginServiceTest {

    @Autowired
    private LoginService loginService;

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private DeviceSessionRepository deviceSessionRepository;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String TEST_USERNAME = "testadmin";
    private static final String TEST_EMAIL = "testadmin@example.com";
    private static final String TEST_PASSWORD = "admin123456";
    private static final String TEST_DEVICE_ID = "test-device-001";

    @BeforeEach
    void setup() {
        resetDatabase();

        loginAttemptService.loginSucceeded(TEST_USERNAME);
        loginAttemptService.loginSucceeded(TEST_EMAIL);

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
    void testLoginWithUsername_Success() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(TEST_USERNAME);
        loginRequest.setPassword(TEST_PASSWORD);
        loginRequest.setDeviceId(TEST_DEVICE_ID);
        loginRequest.setDeviceName("Test Device");

        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        when(mockRequest.getHeader("User-Agent")).thenReturn("TestAgent");
        when(mockRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        var response = loginService.login(loginRequest, mockRequest);

        assertNotNull(response);
        assertEquals(TEST_USERNAME, response.getUsername());
        assertEquals(TEST_EMAIL, response.getEmail());
        assertEquals("ADMIN", response.getRole());

        Long userId = response.getUserId();
        var sessions = deviceSessionRepository.findByAdminUser_IdAndDeviceIdAndIsActiveTrue(userId, TEST_DEVICE_ID);
        assertTrue(sessions.isPresent());
        assertTrue(sessions.get().getIsActive());
        assertEquals(TEST_DEVICE_ID, sessions.get().getDeviceId());
        assertEquals("Test Device", sessions.get().getDeviceName());
    }

    @Test
    void testLoginWithEmail_Success() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(TEST_EMAIL);
        loginRequest.setPassword(TEST_PASSWORD);
        loginRequest.setDeviceId(TEST_DEVICE_ID);

        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        when(mockRequest.getHeader("User-Agent")).thenReturn("TestAgent");
        when(mockRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        var response = loginService.login(loginRequest, mockRequest);

        assertNotNull(response);
        assertEquals(TEST_EMAIL, response.getEmail());

        Long userId = response.getUserId();
        var sessions = deviceSessionRepository.findByAdminUser_IdAndDeviceIdAndIsActiveTrue(userId, TEST_DEVICE_ID);
        assertTrue(sessions.isPresent());
    }

    @Test
    void testLoginWithWrongPassword_ThrowsException() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(TEST_USERNAME);
        loginRequest.setPassword(TEST_PASSWORD);
        loginRequest.setDeviceId(TEST_DEVICE_ID);

        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        when(mockRequest.getHeader("User-Agent")).thenReturn("TestAgent");
        when(mockRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        var successResponse = loginService.login(loginRequest, mockRequest);
        Long userId = successResponse.getUserId();

        loginRequest.setPassword("wrongpassword");
        LoginException exception = assertThrows(LoginException.class, () -> loginService.login(loginRequest, mockRequest));

        assertEquals(ApiErrorCode.AUTH_INVALID_CREDENTIALS, exception.getErrorCode());

        deviceSessionRepository.deleteAll();
        assertThrows(LoginException.class, () -> loginService.login(loginRequest, mockRequest));
        var sessions = deviceSessionRepository.findByAdminUser_IdAndDeviceIdAndIsActiveTrue(userId, TEST_DEVICE_ID);
        assertFalse(sessions.isPresent());
    }

    @Test
    void testLoginWithNonExistentUser_ThrowsException() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("nonexistent");
        loginRequest.setPassword(TEST_PASSWORD);
        loginRequest.setDeviceId(TEST_DEVICE_ID);

        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        when(mockRequest.getHeader("User-Agent")).thenReturn("TestAgent");
        when(mockRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        LoginException exception = assertThrows(LoginException.class, () -> loginService.login(loginRequest, mockRequest));

        assertEquals(ApiErrorCode.AUTH_INVALID_CREDENTIALS, exception.getErrorCode());
    }

    @Test
    void testLoginFailureCount() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(TEST_USERNAME);
        loginRequest.setPassword("wrongpassword");
        loginRequest.setDeviceId(TEST_DEVICE_ID);

        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        when(mockRequest.getHeader("User-Agent")).thenReturn("TestAgent");
        when(mockRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        for (int i = 0; i < 5; i++) {
            LoginException ex = assertThrows(LoginException.class, () -> loginService.login(loginRequest, mockRequest));
            assertEquals(ApiErrorCode.AUTH_INVALID_CREDENTIALS, ex.getErrorCode());
        }

        LoginException ex = assertThrows(LoginException.class, () -> loginService.login(loginRequest, mockRequest));
        assertEquals(ApiErrorCode.AUTH_ACCOUNT_LOCKED, ex.getErrorCode());
    }

    @Test
    void testLoginAfterSuccessClearsFailureCount() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(TEST_USERNAME);
        loginRequest.setPassword("wrongpassword");
        loginRequest.setDeviceId(TEST_DEVICE_ID);

        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        when(mockRequest.getHeader("User-Agent")).thenReturn("TestAgent");
        when(mockRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        for (int i = 0; i < 2; i++) {
            assertThrows(LoginException.class, () -> loginService.login(loginRequest, mockRequest));
        }

        loginRequest.setPassword(TEST_PASSWORD);
        var response = loginService.login(loginRequest, mockRequest);
        assertNotNull(response);

        loginRequest.setPassword("wrongpassword");
        LoginException ex = assertThrows(LoginException.class, () -> loginService.login(loginRequest, mockRequest));
        assertEquals(ApiErrorCode.AUTH_INVALID_CREDENTIALS, ex.getErrorCode());
    }

    @Test
    void testDeviceSessionUpdateOnSubsequentLogin() {
        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
        when(mockRequest.getHeader("User-Agent")).thenReturn("TestAgent");
        when(mockRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(TEST_USERNAME);
        loginRequest.setPassword(TEST_PASSWORD);
        loginRequest.setDeviceId(TEST_DEVICE_ID);
        loginRequest.setDeviceName("Device 1");

        var response1 = loginService.login(loginRequest, mockRequest);
        Long userId = response1.getUserId();

        var session1 = deviceSessionRepository.findByAdminUser_IdAndDeviceIdAndIsActiveTrue(userId, TEST_DEVICE_ID);
        assertTrue(session1.isPresent());

        loginRequest.setDeviceName("Device 2 Updated");
        loginService.login(loginRequest, mockRequest);

        var session2 = deviceSessionRepository.findByAdminUser_IdAndDeviceIdAndIsActiveTrue(userId, TEST_DEVICE_ID);
        assertTrue(session2.isPresent());
        assertEquals("Device 2 Updated", session2.get().getDeviceName());
        assertEquals(session1.get().getId(), session2.get().getId());
    }
}

