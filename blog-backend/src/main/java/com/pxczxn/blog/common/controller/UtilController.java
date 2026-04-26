/**
 * 开发工具控制器
 * <p>
 * 仅在 dev 环境生效，提供密码哈希生成、默认管理员创建等开发辅助接口。
 */
package com.pxczxn.blog.common.controller;

import com.pxczxn.blog.common.response.Result;
import com.pxczxn.blog.user.dto.AdminUserCreateRequest;
import com.pxczxn.blog.user.dto.AdminUserResponse;
import com.pxczxn.blog.user.service.AdminUserService;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Profile("dev")
@RequestMapping("/api/dev")
public class UtilController {

    /** 密码编码器，用于生成密码哈希 */
    private final PasswordEncoder passwordEncoder;

    /** 管理员用户服务 */
    private final AdminUserService adminUserService;

    /**
     * 构造函数
     *
     * @param passwordEncoder 密码编码器
     * @param adminUserService 管理员用户服务
     */
    public UtilController(PasswordEncoder passwordEncoder, AdminUserService adminUserService) {
        this.passwordEncoder = passwordEncoder;
        this.adminUserService = adminUserService;
    }

    /**
     * 生成密码哈希值
     * <p>
     * 用于开发时生成测试密码的加密哈希值。
     *
     * @param password 原始密码
     * @return 包含原始密码和哈希值的映射
     */
    @GetMapping("/hash")
    public Map<String, String> generateHash(@RequestParam String password) {
        Map<String, String> result = new HashMap<>();
        result.put("password", password);
        result.put("hash", passwordEncoder.encode(password));
        return result;
    }

    /**
     * 创建默认管理员账号
     * <p>
     * 创建用户名为 pxczxn 的默认管理员账号，如果已存在则返回已有账号信息。
     *
     * @return 创建结果，包含管理员用户信息
     */
    @PostMapping("/admin-users/create-default")
    public Result<AdminUserResponse> createDefaultAdmin() {
        final String username = "pxczxn";
        final String password = "pxczxn";
        final String email = "pxczxn@example.com";

        if (adminUserService.existsByUsername(username)) {
            return Result.success("admin already exists", adminUserService.getByUsername(username));
        }

        AdminUserCreateRequest request = new AdminUserCreateRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setEmail(email);

        return Result.success("admin created", adminUserService.create(request));
    }
}
