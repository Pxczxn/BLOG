/**
 * 分类数据访问层
 * <p>
 * 提供分类实体的 CRUD 操作及按 slug、名称查询等方法。
 */
package com.pxczxn.blog.category.repository;

import com.pxczxn.blog.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * 判断指定slug是否已存在
     *
     * @param slug URL友好的分类标识
     * @return 存在返回true，否则返回false
     */
    boolean existsBySlug(String slug);

    /**
     * 判断指定名称是否已存在
     *
     * @param name 分类名称
     * @return 存在返回true，否则返回false
     */
    boolean existsByName(String name);

    /**
     * 根据slug查询分类
     *
     * @param slug URL友好的分类标识
     * @return 匹配的分类，可能为空
     */
    Optional<Category> findBySlug(String slug);

    /**
     * 查询所有分类并按创建时间降序排列
     *
     * @return 按创建时间降序排列的分类列表
     */
    List<Category> findAllByOrderByCreatedAtDesc();
}

