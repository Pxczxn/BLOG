/**
 * 时间戳基类
 * <p>
 * 抽象实体基类，自动管理 createdAt 和 updatedAt 字段。
 * 继承此类的实体的创建和更新时间会在持久化时自动设置。
 */
package com.pxczxn.blog.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseTimeEntity {

    /** 创建时间，持久化时自动设置，不可更新 */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 更新时间，持久化和更新时自动设置 */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 实体持久化前的回调
     * <p>
     * 在实体首次保存到数据库时，自动设置创建时间和更新时间。
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    /**
     * 实体更新前的回调
     * <p>
     * 在实体更新到数据库时，自动更新更新时间。
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
