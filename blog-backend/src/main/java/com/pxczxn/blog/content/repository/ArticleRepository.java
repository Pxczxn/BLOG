package com.pxczxn.blog.content.repository;

import com.pxczxn.blog.content.entity.Article;
import com.pxczxn.blog.content.entity.ArticleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long>, JpaSpecificationExecutor<Article> {

    Optional<Article> findBySlug(String slug);

    Optional<Article> findBySlugAndStatus(String slug, ArticleStatus status);

    List<Article> findByStatusOrderByPublishedAtDesc(ArticleStatus status);

    Page<Article> findByStatusOrderByCreatedAtDesc(ArticleStatus status, Pageable pageable);

    boolean existsBySlug(String slug);

    boolean existsByCategoryId(Long categoryId);

    /**
     * 查找上一篇文章（创建时间早于当前文章的最新一篇）
     */
    @Query("SELECT a FROM Article a WHERE a.status = 'PUBLISHED' AND a.createdAt < :createdAt ORDER BY a.createdAt DESC")
    Optional<Article> findPreviousArticle(@Param("createdAt") java.time.LocalDateTime createdAt);

    /**
     * 查找下一篇文章（创建时间晚于当前文章的最早一篇）
     */
    @Query("SELECT a FROM Article a WHERE a.status = 'PUBLISHED' AND a.createdAt > :createdAt ORDER BY a.createdAt ASC")
    Optional<Article> findNextArticle(@Param("createdAt") java.time.LocalDateTime createdAt);
}
