/**
 * 文章数据访问接口
 * <p>
 * 提供文章实体的基础CRUD操作，并扩展了按slug查询、
 * 按状态排序查询、分类关联检查及上下篇导航查询等功能。
 */
package com.pxczxn.blog.content.repository;

import com.pxczxn.blog.content.entity.Article;
import com.pxczxn.blog.content.entity.ArticleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long>, JpaSpecificationExecutor<Article> {

    /**
     * 根据slug查询文章
     *
     * @param slug 文章URL标识
     * @return 匹配的文章，不存在则返回空Optional
     */
    @EntityGraph(attributePaths = {"author"})
    Optional<Article> findBySlug(String slug);

    /**
     * 根据slug和状态查询文章
     *
     * @param slug   文章URL标识
     * @param status 文章状态
     * @return 匹配的文章，不存在则返回空Optional
     */
    @EntityGraph(attributePaths = {"author"})
    Optional<Article> findBySlugAndStatus(String slug, ArticleStatus status);

    /**
     * 根据状态查询文章列表，按发布时间倒序排列
     *
     * @param status 文章状态
     * @return 指定状态的文章列表
     */
    @EntityGraph(attributePaths = {"author"})
    List<Article> findByStatusOrderByPublishedAtDesc(ArticleStatus status);

    /**
     * 根据状态分页查询文章，按创建时间倒序排列
     *
     * @param status   文章状态
     * @param pageable 分页参数
     * @return 分页文章结果
     */
    @EntityGraph(attributePaths = {"author"})
    Page<Article> findByStatusOrderByCreatedAtDesc(ArticleStatus status, Pageable pageable);

    /**
     * 检查指定slug的文章是否已存在
     *
     * @param slug 文章URL标识
     * @return 存在返回true，否则返回false
     */
    boolean existsBySlug(String slug);

    /**
     * 检查指定分类下是否存在文章
     *
     * @param categoryId 分类ID
     * @return 存在返回true，否则返回false
     */
    boolean existsByCategoryId(Long categoryId);

    /**
     * 查找上一篇文章（创建时间早于当前文章的最新一篇）
     *
     * @param createdAt 当前文章的创建时间
     * @return 上一篇文章，不存在则返回空Optional
     */
    @EntityGraph(attributePaths = {"author"})
    @Query("SELECT a FROM Article a WHERE a.status = 'PUBLISHED' AND a.createdAt < :createdAt ORDER BY a.createdAt DESC")
    Optional<Article> findPreviousArticle(@Param("createdAt") java.time.LocalDateTime createdAt);

    /**
     * 查找下一篇文章（创建时间晚于当前文章的最早一篇）
     *
     * @param createdAt 当前文章的创建时间
     * @return 下一篇文章，不存在则返回空Optional
     */
    @EntityGraph(attributePaths = {"author"})
    @Query("SELECT a FROM Article a WHERE a.status = 'PUBLISHED' AND a.createdAt > :createdAt ORDER BY a.createdAt ASC")
    Optional<Article> findNextArticle(@Param("createdAt") java.time.LocalDateTime createdAt);
}
