





package com.pxczxn.blog.common.controller;

import com.pxczxn.blog.auth.repository.DeviceSessionRepository;
import com.pxczxn.blog.content.repository.ArticleRepository;
import com.pxczxn.blog.user.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthCheckController {

    
    private final JdbcTemplate jdbcTemplate;

    
    private final AdminUserRepository adminUserRepository;

    
    private final ArticleRepository articleRepository;

    
    private final DeviceSessionRepository deviceSessionRepository;

    






    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");

        Map<String, Object> details = new HashMap<>();

        
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            long userCount = adminUserRepository.count();
            long articleCount = articleRepository.count();
            long sessionCount = deviceSessionRepository.count();

            details.put("database", Map.of(
                    "status", "up",
                    "userCount", userCount,
                    "articleCount", articleCount,
                    "sessionCount", sessionCount
            ));
        } catch (Exception e) {
            details.put("database", Map.of("status", "down", "error", e.getMessage()));
        }

        
        File disk = new File("/");
        long freeSpace = disk.getFreeSpace() / (1024 * 1024 * 1024);
        long totalSpace = disk.getTotalSpace() / (1024 * 1024 * 1024);
        details.put("disk", Map.of(
                "status", freeSpace > 5 ? "up" : "low",
                "freeSpaceGB", freeSpace,
                "totalSpaceGB", totalSpace
        ));

        health.put("details", details);

        return ResponseEntity.ok(health);
    }

    






    @GetMapping("/liveness")
    public ResponseEntity<Map<String, String>> liveness() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }

    






    @GetMapping("/readiness")
    public ResponseEntity<Map<String, Object>> readiness() {
        
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return ResponseEntity.ok(Map.of("status", "UP"));
        } catch (Exception e) {
            return ResponseEntity.status(503).body(Map.of("status", "DOWN", "error", "Database not available"));
        }
    }
}
