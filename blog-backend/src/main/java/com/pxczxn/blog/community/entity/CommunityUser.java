


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

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    
    @Column(name = "password_hash", nullable = false, length = 60)
    private String passwordHash;

    
    @Column(name = "display_name", nullable = false, length = 80)
    private String displayName;

    
    @Column(name = "avatar", length = 255)
    private String avatar;

    
    @Column(name = "bio", length = 500)
    private String bio;

    
    @Column(name = "website", length = 255)
    private String website;

    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private CommunityUserStatus status = CommunityUserStatus.ACTIVE;

    
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    @Builder.Default
    private CommunityUserRole role = CommunityUserRole.USER;

    
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
}
