




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

    
    private final PasswordEncoder passwordEncoder;

    
    private final AdminUserService adminUserService;

    





    public UtilController(PasswordEncoder passwordEncoder, AdminUserService adminUserService) {
        this.passwordEncoder = passwordEncoder;
        this.adminUserService = adminUserService;
    }

    







    @GetMapping("/hash")
    public Map<String, String> generateHash(@RequestParam String password) {
        Map<String, String> result = new HashMap<>();
        result.put("password", password);
        result.put("hash", passwordEncoder.encode(password));
        return result;
    }

    






    @PostMapping("/admin-users/create-default")
    public Result<AdminUserResponse> createDefaultAdmin() {
        final String username = "";
        final String password = "";
        final String email = "";

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
