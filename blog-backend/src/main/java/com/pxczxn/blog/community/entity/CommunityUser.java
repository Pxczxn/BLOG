/**
 * 社区用户实体，存储社区用户的基本信息
 */
package com.pxczxn.blog.community.entity;

import com.pxczxn.blog.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "community_user")
public class CommunityUser extends BaseTimeEntity {

    /** 用户 ID（主键） */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 用户名，唯一标识，最大 50 个字符 */
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    /** 邮箱地址，唯一，最大 100 个字符 */
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    /** 密码哈希值，最大 60 个字符 */
    @Column(name = "password_hash", nullable = false, length = 60)
    private String passwordHash;

    /** 显示名称（昵称），最大 80 个字符 */
    @Column(name = "display_name", nullable = false, length = 80)
    private String displayName;

    /** 头像 URL，最大 255 个字符 */
    @Column(name = "avatar", length = 255)
    private String avatar;

    /** 个人简介，最大 500 个字符 */
    @Column(name = "bio", length = 500)
    private String bio;

    /** 个人网站 URL，最大 255 个字符 */
    @Column(name = "website", length = 255)
    private String website;

    /** 用户状态，默认为 ACTIVE */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private CommunityUserStatus status = CommunityUserStatus.ACTIVE;

    /** 用户角色，默认为 USER */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    @Builder.Default
    private CommunityUserRole role = CommunityUserRole.USER;

    /** 最后登录时间 */
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
}
