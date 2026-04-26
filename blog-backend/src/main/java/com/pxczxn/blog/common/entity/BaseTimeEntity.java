





package com.pxczxn.blog.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseTimeEntity {

    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    




    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    




    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
