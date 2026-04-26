


package com.pxczxn.blog.user.service;

import com.pxczxn.blog.user.dto.AdminUserCreateRequest;
import com.pxczxn.blog.user.dto.AdminUserResponse;
import com.pxczxn.blog.user.entity.AdminUser;
import com.pxczxn.blog.user.entity.AdminUserStatus;
import com.pxczxn.blog.user.exception.UserAlreadyExistsException;
import com.pxczxn.blog.user.exception.UserNotFoundException;
import com.pxczxn.blog.user.repository.AdminUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AdminUserServiceTest {

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password123";

    @BeforeEach
    void resetDatabase() {
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
    void testCreateAdminUser_Success() {
        AdminUserCreateRequest request = new AdminUserCreateRequest();
        request.setUsername(TEST_USERNAME);
        request.setEmail(TEST_EMAIL);
        request.setPassword(TEST_PASSWORD);

        AdminUserResponse response = adminUserService.create(request);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals(TEST_USERNAME, response.getUsername());
        assertEquals(TEST_EMAIL, response.getEmail());
        assertEquals(AdminUserStatus.ACTIVE, response.getStatus());
        assertEquals("ADMIN", response.getRole());
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());
    }

    @Test
    void testCreateAdminUser_PasswordEncrypted() {
        AdminUserCreateRequest request = new AdminUserCreateRequest();
        request.setUsername(TEST_USERNAME);
        request.setEmail(TEST_EMAIL);
        request.setPassword(TEST_PASSWORD);

        adminUserService.create(request);
        AdminUser savedUser = adminUserRepository.findByUsername(TEST_USERNAME).orElse(null);

        assertNotNull(savedUser);
        assertNotNull(savedUser.getPasswordHash());
        assertNotEquals(TEST_PASSWORD, savedUser.getPasswordHash());
        assertTrue(passwordEncoder.matches(TEST_PASSWORD, savedUser.getPasswordHash()));
    }

    @Test
    void testCreateAdminUser_DuplicateUsername_ThrowsException() {
        AdminUserCreateRequest request1 = new AdminUserCreateRequest();
        request1.setUsername(TEST_USERNAME);
        request1.setEmail("user1@example.com");
        request1.setPassword(TEST_PASSWORD);
        adminUserService.create(request1);

        AdminUserCreateRequest request2 = new AdminUserCreateRequest();
        request2.setUsername(TEST_USERNAME);
        request2.setEmail("user2@example.com");
        request2.setPassword("anotherpass");

        assertThrows(UserAlreadyExistsException.class, () -> adminUserService.create(request2));
    }

    @Test
    void testCreateAdminUser_DuplicateEmail_ThrowsException() {
        AdminUserCreateRequest request1 = new AdminUserCreateRequest();
        request1.setUsername("user1");
        request1.setEmail(TEST_EMAIL);
        request1.setPassword(TEST_PASSWORD);
        adminUserService.create(request1);

        AdminUserCreateRequest request2 = new AdminUserCreateRequest();
        request2.setUsername("user2");
        request2.setEmail(TEST_EMAIL);
        request2.setPassword("anotherpass");

        assertThrows(UserAlreadyExistsException.class, () -> adminUserService.create(request2));
    }

    @Test
    void testGetById_Success() {
        AdminUserCreateRequest request = new AdminUserCreateRequest();
        request.setUsername(TEST_USERNAME);
        request.setEmail(TEST_EMAIL);
        request.setPassword(TEST_PASSWORD);
        AdminUserResponse created = adminUserService.create(request);

        AdminUserResponse response = adminUserService.getById(created.getId());

        assertNotNull(response);
        assertEquals(created.getId(), response.getId());
        assertEquals(TEST_USERNAME, response.getUsername());
        assertEquals(TEST_EMAIL, response.getEmail());
    }

    @Test
    void testGetById_NotFound_ThrowsException() {
        assertThrows(UserNotFoundException.class, () -> adminUserService.getById(99999L));
    }

    @Test
    void testGetByUsername_Success() {
        AdminUserCreateRequest request = new AdminUserCreateRequest();
        request.setUsername(TEST_USERNAME);
        request.setEmail(TEST_EMAIL);
        request.setPassword(TEST_PASSWORD);
        adminUserService.create(request);

        AdminUserResponse response = adminUserService.getByUsername(TEST_USERNAME);

        assertNotNull(response);
        assertEquals(TEST_USERNAME, response.getUsername());
        assertEquals(TEST_EMAIL, response.getEmail());
    }

    @Test
    void testGetByUsernameOrEmail_WithUsername() {
        AdminUserCreateRequest request = new AdminUserCreateRequest();
        request.setUsername(TEST_USERNAME);
        request.setEmail(TEST_EMAIL);
        request.setPassword(TEST_PASSWORD);
        adminUserService.create(request);

        AdminUser user = adminUserService.getByUsernameOrEmail(TEST_USERNAME);

        assertNotNull(user);
        assertEquals(TEST_USERNAME, user.getUsername());
    }

    @Test
    void testGetByUsernameOrEmail_WithEmail() {
        AdminUserCreateRequest request = new AdminUserCreateRequest();
        request.setUsername(TEST_USERNAME);
        request.setEmail(TEST_EMAIL);
        request.setPassword(TEST_PASSWORD);
        adminUserService.create(request);

        AdminUser user = adminUserService.getByUsernameOrEmail(TEST_EMAIL);

        assertNotNull(user);
        assertEquals(TEST_EMAIL, user.getEmail());
    }

    @Test
    void testGetAll_Success() {
        adminUserService.create(createRequest("user1", "user1@example.com"));
        adminUserService.create(createRequest("user2", "user2@example.com"));
        adminUserService.create(createRequest("user3", "user3@example.com"));

        var users = adminUserService.getAll();

        assertNotNull(users);
        assertEquals(3, users.size());
    }

    @Test
    void testUpdateLastLoginAt_Success() {
        AdminUserCreateRequest request = new AdminUserCreateRequest();
        request.setUsername(TEST_USERNAME);
        request.setEmail(TEST_EMAIL);
        request.setPassword(TEST_PASSWORD);
        AdminUserResponse created = adminUserService.create(request);

        adminUserService.updateLastLoginAt(created.getId());
        AdminUser updated = adminUserRepository.findById(created.getId()).orElse(null);

        assertNotNull(updated);
        assertNotNull(updated.getLastLoginAt());
    }

    @Test
    void testExistsByUsername_True() {
        AdminUserCreateRequest request = new AdminUserCreateRequest();
        request.setUsername(TEST_USERNAME);
        request.setEmail(TEST_EMAIL);
        request.setPassword(TEST_PASSWORD);
        adminUserService.create(request);

        assertTrue(adminUserService.existsByUsername(TEST_USERNAME));
    }

    @Test
    void testExistsByUsername_False() {
        assertFalse(adminUserService.existsByUsername("nonexistent"));
    }

    @Test
    void testExistsByEmail_True() {
        AdminUserCreateRequest request = new AdminUserCreateRequest();
        request.setUsername(TEST_USERNAME);
        request.setEmail(TEST_EMAIL);
        request.setPassword(TEST_PASSWORD);
        adminUserService.create(request);

        assertTrue(adminUserService.existsByEmail(TEST_EMAIL));
    }

    @Test
    void testExistsByEmail_False() {
        assertFalse(adminUserService.existsByEmail("nonexistent@example.com"));
    }

    private AdminUserCreateRequest createRequest(String username, String email) {
        AdminUserCreateRequest request = new AdminUserCreateRequest();
        request.setUsername(username);
        request.setEmail(email);
        request.setPassword("password123");
        return request;
    }
}

