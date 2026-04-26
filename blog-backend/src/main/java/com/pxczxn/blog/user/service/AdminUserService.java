/**
 * 管理员用户服务
 * <p>
 * 处理管理员账户的创建、查询和信息更新等操作
 */
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

/**
 * 管理员用户服务
 * <p>
 * 提供管理员用户的业务逻辑处理，包括创建、查询和状态更新等
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserService {

    /** 管理员用户数据访问层 */
    private final AdminUserRepository adminUserRepository;

    /** 密码编码器，用于加密用户密码 */
    private final PasswordEncoder passwordEncoder;

    /**
     * 创建管理员用户
     * <p>
     * 校验用户名和邮箱是否已存在，密码加密后保存
     *
     * @param request 创建请求 DTO，包含用户名、邮箱和密码
     * @return 创建成功的用户响应 DTO
     * @throws UserAlreadyExistsException 用户名或邮箱已被占用
     */
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

    /**
     * 根据ID查询管理员用户
     *
     * @param id 用户ID
     * @return 用户响应 DTO
     * @throws UserNotFoundException 用户不存在
     */
    @Transactional(readOnly = true)
    public AdminUserResponse getById(Long id) {
        AdminUser adminUser = adminUserRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return AdminUserResponse.from(adminUser);
    }

    /**
     * 根据用户名查询管理员用户
     *
     * @param username 用户名
     * @return 用户响应 DTO
     * @throws UserNotFoundException 用户不存在
     */
    @Transactional(readOnly = true)
    public AdminUserResponse getByUsername(String username) {
        AdminUser adminUser = adminUserRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        return AdminUserResponse.from(adminUser);
    }

    /**
     * 根据用户名或邮箱查询管理员用户实体
     * <p>
     * 用于登录认证场景，传入的标识符同时匹配用户名和邮箱字段
     *
     * @param usernameOrEmail 用户名或邮箱地址
     * @return 管理员用户实体
     * @throws UserNotFoundException 用户不存在
     */
    @Transactional(readOnly = true)
    public AdminUser getByUsernameOrEmail(String usernameOrEmail) {
        return adminUserRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UserNotFoundException(usernameOrEmail));
    }

    /**
     * 查询所有管理员用户
     *
     * @return 所有用户的响应 DTO 列表
     */
    @Transactional(readOnly = true)
    public List<AdminUserResponse> getAll() {
        return adminUserRepository.findAll().stream()
                .map(AdminUserResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 根据状态查询管理员用户
     *
     * @param status 用户状态
     * @return 符合状态条件的用户响应 DTO 列表
     */
    @Transactional(readOnly = true)
    public List<AdminUserResponse> getByStatus(AdminUserStatus status) {
        return adminUserRepository.findAll().stream()
                .filter(user -> user.getStatus() == status)
                .map(AdminUserResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 存在返回 true，否则返回 false
     */
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return adminUserRepository.existsByUsername(username);
    }

    /**
     * 检查邮箱是否存在
     *
     * @param email 邮箱地址
     * @return 存在返回 true，否则返回 false
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return adminUserRepository.existsByEmail(email);
    }

    /**
     * 更新用户最后登录时间
     *
     * @param userId 用户ID
     * @throws UserNotFoundException 用户不存在
     */
    @Transactional
    public void updateLastLoginAt(Long userId) {
        AdminUser adminUser = adminUserRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        adminUser.setLastLoginAt(java.time.LocalDateTime.now());
        adminUserRepository.save(adminUser);
    }
}
