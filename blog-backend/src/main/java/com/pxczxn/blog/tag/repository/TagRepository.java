/**
 * 标签数据访问接口
 */
package com.pxczxn.blog.tag.repository;

import com.pxczxn.blog.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    /**
     * 判断指定slug的标签是否存在
     *
     * @param slug 标签别名
     * @return 存在返回true，否则返回false
     */
    boolean existsBySlug(String slug);

    /**
     * 判断指定名称的标签是否存在
     *
     * @param name 标签名称
     * @return 存在返回true，否则返回false
     */
    boolean existsByName(String name);

    /**
     * 根据slug查找标签
     *
     * @param slug 标签别名
     * @return 标签Optional包装
     */
    Optional<Tag> findBySlug(String slug);

    /**
     * 查询所有标签，按创建时间降序排列
     *
     * @return 标签列表
     */
    List<Tag> findAllByOrderByCreatedAtDesc();
}

