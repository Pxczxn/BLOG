





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

    





    @EntityGraph(attributePaths = {"author"})
    Optional<Article> findBySlug(String slug);

    






    @EntityGraph(attributePaths = {"author"})
    Optional<Article> findBySlugAndStatus(String slug, ArticleStatus status);

    





    @EntityGraph(attributePaths = {"author"})
    List<Article> findByStatusOrderByPublishedAtDesc(ArticleStatus status);

    






    @EntityGraph(attributePaths = {"author"})
    Page<Article> findByStatusOrderByCreatedAtDesc(ArticleStatus status, Pageable pageable);

    





    boolean existsBySlug(String slug);

    





    boolean existsByCategoryId(Long categoryId);

    





    @EntityGraph(attributePaths = {"author"})
    @Query("SELECT a FROM Article a WHERE a.status = 'PUBLISHED' AND a.createdAt < :createdAt ORDER BY a.createdAt DESC")
    Optional<Article> findPreviousArticle(@Param("createdAt") java.time.LocalDateTime createdAt);

    





    @EntityGraph(attributePaths = {"author"})
    @Query("SELECT a FROM Article a WHERE a.status = 'PUBLISHED' AND a.createdAt > :createdAt ORDER BY a.createdAt ASC")
    Optional<Article> findNextArticle(@Param("createdAt") java.time.LocalDateTime createdAt);
}
