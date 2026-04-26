




package com.pxczxn.blog.config;

import com.pxczxn.blog.user.entity.AdminUser;
import com.pxczxn.blog.user.entity.AdminUserStatus;
import com.pxczxn.blog.user.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@Profile("prod")
@RequiredArgsConstructor
public class ProductionAdminBootstrap implements ApplicationRunner {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.initial.username:}")
    private String username;

    @Value("${app.admin.initial.email:}")
    private String email;

    @Value("${app.admin.initial.password:}")
    private String password;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        validateInitialAdminConfig();

        AdminUser adminUser = adminUserRepository.findByUsernameOrEmail(username, email)
                .orElseGet(AdminUser::new);
        boolean created = adminUser.getId() == null;

        adminUser.setUsername(username);
        adminUser.setEmail(email);
        adminUser.setPasswordHash(passwordEncoder.encode(password));
        adminUser.setStatus(AdminUserStatus.ACTIVE);
        adminUser.setRole("ADMIN");

        adminUserRepository.save(adminUser);
        log.info("生产管理员账号已{}: username={}", created ? "初始化" : "更新", username);
    }

    private void validateInitialAdminConfig() {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(email) || !StringUtils.hasText(password)) {
            throw new IllegalStateException("生产环境必须配置 INITIAL_ADMIN_USERNAME、INITIAL_ADMIN_EMAIL、INITIAL_ADMIN_PASSWORD");
        }

        if (password.length() < 12) {
            throw new IllegalStateException("INITIAL_ADMIN_PASSWORD 长度不能少于 12 个字符");
        }
    }
}
