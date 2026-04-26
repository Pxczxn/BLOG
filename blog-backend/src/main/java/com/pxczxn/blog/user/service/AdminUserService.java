




package com.pxczxn.blog.user.service;

import com.pxczxn.blog.user.dto.AdminUserCreateRequest;
import com.pxczxn.blog.user.dto.AdminUserResponse;
import com.pxczxn.blog.user.entity.AdminUser;
import com.pxczxn.blog.user.entity.AdminUserStatus;
import com.pxczxn.blog.user.exception.UserAlreadyExistsException;
import com.pxczxn.blog.user.exception.UserNotFoundException;
import com.pxczxn.blog.user.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;






@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserService {

    
    private final AdminUserRepository adminUserRepository;

    
    private final PasswordEncoder passwordEncoder;

    








    @Transactional
    public AdminUserResponse create(AdminUserCreateRequest request) {
        if (adminUserRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("用户名", request.getUsername());
        }

        if (adminUserRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("邮箱", request.getEmail());
        }

        AdminUser adminUser = AdminUser.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .status(AdminUserStatus.ACTIVE)
                .role("ADMIN")
                .build();

        adminUser = adminUserRepository.save(adminUser);
        log.info("管理员创建成功: username={}", adminUser.getUsername());

        return AdminUserResponse.from(adminUser);
    }

    






    @Transactional(readOnly = true)
    public AdminUserResponse getById(Long id) {
        AdminUser adminUser = adminUserRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return AdminUserResponse.from(adminUser);
    }

    






    @Transactional(readOnly = true)
    public AdminUserResponse getByUsername(String username) {
        AdminUser adminUser = adminUserRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        return AdminUserResponse.from(adminUser);
    }

    








    @Transactional(readOnly = true)
    public AdminUser getByUsernameOrEmail(String usernameOrEmail) {
        return adminUserRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UserNotFoundException(usernameOrEmail));
    }

    




    @Transactional(readOnly = true)
    public List<AdminUserResponse> getAll() {
        return adminUserRepository.findAll().stream()
                .map(AdminUserResponse::from)
                .collect(Collectors.toList());
    }

    





    @Transactional(readOnly = true)
    public List<AdminUserResponse> getByStatus(AdminUserStatus status) {
        return adminUserRepository.findAll().stream()
                .filter(user -> user.getStatus() == status)
                .map(AdminUserResponse::from)
                .collect(Collectors.toList());
    }

    





    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return adminUserRepository.existsByUsername(username);
    }

    





    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return adminUserRepository.existsByEmail(email);
    }

    





    @Transactional
    public void updateLastLoginAt(Long userId) {
        AdminUser adminUser = adminUserRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        adminUser.setLastLoginAt(java.time.LocalDateTime.now());
        adminUserRepository.save(adminUser);
    }
}
