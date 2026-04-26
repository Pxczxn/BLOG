/**
 * 健康检查控制器
 * <p>
 * 提供应用健康状态检查接口，包括数据库连通性、磁盘空间等。
 * 用于 K8s 存活探针和就绪探针检测。
 */
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

    /** JDBC 模板，用于数据库连通性检查 */
    private final JdbcTemplate jdbcTemplate;

    /** 管理员用户仓库 */
    private final AdminUserRepository adminUserRepository;

    /** 文章仓库 */
    private final ArticleRepository articleRepository;

    /** 设备会话仓库 */
    private final DeviceSessionRepository deviceSessionRepository;

    /**
     * 完整健康检查
     * <p>
     * 检查数据库连通性、核心数据数量、磁盘空间等。
     *
     * @return 包含详细健康状态信息的响应
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");

        Map<String, Object> details = new HashMap<>();

        // 检查数据库连通性和核心数据数量。
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

        // 检查磁盘剩余空间。
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

    /**
     * 存活探针
     * <p>
     * 用于 K8s 检测应用是否存活，只要应用在运行就返回 UP。
     *
     * @return 存活状态响应
     */
    @GetMapping("/liveness")
    public ResponseEntity<Map<String, String>> liveness() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }

    /**
     * 就绪探针
     * <p>
     * 用于 K8s 检测应用是否就绪，需要数据库可用才返回 UP。
     *
     * @return 就绪状态响应，数据库不可用时返回 503
     */
    @GetMapping("/readiness")
    public ResponseEntity<Map<String, Object>> readiness() {
        // 就绪检查需要数据库可用。
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return ResponseEntity.ok(Map.of("status", "UP"));
        } catch (Exception e) {
            return ResponseEntity.status(503).body(Map.of("status", "DOWN", "error", "Database not available"));
        }
    }
}
