/**
 * 标签实体类
 * <p>
 * 用于存储文章标签信息，支持通过标签对文章进行分类和检索。
 */
package com.pxczxn.blog.tag.entity;

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
@Table(name = "tag")
public class Tag extends BaseTimeEntity {

    /** 标签ID，主键自增 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** 标签名称，最长100字符，唯一 */
    @Column(name = "name", nullable = false, length = 100, unique = true)
    private String name;

    /** 标签别名，用于URL友好显示，最长120字符，唯一 */
    @Column(name = "slug", nullable = false, length = 120, unique = true)
    private String slug;
}

