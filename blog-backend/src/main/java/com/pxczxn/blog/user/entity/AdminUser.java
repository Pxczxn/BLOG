/**
 * 管理员用户实体
 * <p>
 * 后台管理员账户信息，包含用户名、密码哈希、邮箱、角色和状态等。
 * 继承 BaseTimeEntity 自动管理创建和更新时间。
 */
package com.pxczxn.blog.user.entity;

import com.pxczxn.blog.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 管理员用户实体
 * <p>
 * 对应数据库 admin_user 表，存储后台管理员的账户信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "admin_user")
public class AdminUser extends BaseTimeEntity {

    /** 用户ID，主键自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** 用户名，唯一，长度 3-50 个字符 */
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    /** 邮箱地址，唯一，最大 100 个字符 */
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    /** 密码哈希（BCrypt 加密），固定 60 个字符 */
    @Column(name = "password_hash", nullable = false, length = 60)
    private String passwordHash;

    /** 账户状态：ACTIVE-正常，BANNED-禁用，默认 ACTIVE */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private AdminUserStatus status = AdminUserStatus.ACTIVE;

    /** 角色，默认 ADMIN */
    @Column(name = "role", nullable = false, length = 20)
    @Builder.Default
    private String role = "ADMIN";

    /** 最后登录时间 */
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
}
