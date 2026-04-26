/**
 * 分类实体
 * <p>
 * 博客文章的分类，包含分类名称和 URL 友好的 slug 标识。
 * name 和 slug 均为唯一约束。继承 BaseTimeEntity 自动管理创建和更新时间。
 */
package com.pxczxn.blog.category.entity;

import com.pxczxn.blog.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "category")
public class Category extends BaseTimeEntity {

    /**
     * 分类唯一标识ID，自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 分类名称，最大长度100，唯一约束
     */
    @Column(name = "name", nullable = false, length = 100, unique = true)
    private String name;

    /**
     * URL友好的分类标识，最大长度120，唯一约束，自动根据name生成
     */
    @Column(name = "slug", nullable = false, length = 120, unique = true)
    private String slug;
}

