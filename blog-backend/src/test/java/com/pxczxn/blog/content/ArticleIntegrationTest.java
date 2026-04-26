


package com.pxczxn.blog.content;

import com.pxczxn.blog.common.response.PageResponse;
import com.pxczxn.blog.content.dto.ArticleCreateRequest;
import com.pxczxn.blog.content.dto.PublicArticleDetailResponse;
import com.pxczxn.blog.content.dto.PublicArticleListItemResponse;
import com.pxczxn.blog.content.entity.ArticleStatus;
import com.pxczxn.blog.content.exception.ArticleNotFoundException;
import com.pxczxn.blog.content.service.ArticleService;
import com.pxczxn.blog.user.dto.AdminUserCreateRequest;
import com.pxczxn.blog.user.service.AdminUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ArticleIntegrationTest {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long authorId;

    @BeforeEach
    void setup() {
        resetDatabase();

        AdminUserCreateRequest request = new AdminUserCreateRequest();
        request.setUsername("article-user");
        request.setEmail("article-user@example.com");
        request.setPassword("password123");
        authorId = adminUserService.create(request).getId();
    }

    private void resetDatabase() {
        executeIfPossible("CREATE TABLE IF NOT EXISTS article_tag (article_id BIGINT, tag_id BIGINT)");
        deleteIfExists("DELETE FROM article_tag");
        deleteIfExists("DELETE FROM comment");
        deleteIfExists("DELETE FROM article");
        deleteIfExists("DELETE FROM token_revoked");
        deleteIfExists("DELETE FROM device_session");
        deleteIfExists("DELETE FROM admin_user");
    }

    private void deleteIfExists(String sql) {
        try {
            jdbcTemplate.update(sql);
        } catch (DataAccessException ignored) {
        }
    }

    private void executeIfPossible(String sql) {
        try {
            jdbcTemplate.execute(sql);
        } catch (DataAccessException ignored) {
        }
    }

    @Test
    void create_generatesSlugAndDefaultDraft() {
        ArticleCreateRequest request = new ArticleCreateRequest();
        request.setTitle("Hello Spring Boot");
        request.setSummary("summary");
        request.setContent("content");

        var created = articleService.create(request, authorId);

        assertNotNull(created.getId());
        assertNotNull(created.getSlug());
        assertTrue(created.getSlug().matches("\\d{10,}-[a-z0-9-]+"));
        assertEquals(ArticleStatus.DRAFT, created.getStatus());
    }

    @Test
    void create_sameTitle_generatesDifferentSlug() {
        ArticleCreateRequest request = new ArticleCreateRequest();
        request.setTitle("Same Title");
        request.setContent("content");

        var first = articleService.create(request, authorId);
        var second = articleService.create(request, authorId);

        assertNotEquals(first.getSlug(), second.getSlug());
    }

    @Test
    void publicDetail_onlyAllowsPublished() {
        ArticleCreateRequest request = new ArticleCreateRequest();
        request.setTitle("Draft post");
        request.setContent("content");
        request.setStatus(ArticleStatus.DRAFT);

        var created = articleService.create(request, authorId);

        assertThrows(ArticleNotFoundException.class, () -> articleService.getPublishedBySlug(created.getSlug()));
    }

    @Test
    void publicList_returnsPublishedWithPaging() {
        ArticleCreateRequest draft = new ArticleCreateRequest();
        draft.setTitle("Draft");
        draft.setContent("content");
        draft.setStatus(ArticleStatus.DRAFT);
        articleService.create(draft, authorId);

        ArticleCreateRequest published = new ArticleCreateRequest();
        published.setTitle("Published");
        published.setContent("content");
        published.setStatus(ArticleStatus.PUBLISHED);
        var created = articleService.create(published, authorId);

        PageResponse<PublicArticleListItemResponse> page = articleService.listPublished(
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt")),
                1
        );

        assertEquals(1, page.total());
        assertEquals(1, page.items().size());
        assertEquals(1, page.page());
        assertEquals(created.getSlug(), page.items().get(0).slug());

        PublicArticleDetailResponse detail = articleService.getPublishedBySlug(created.getSlug());
        assertEquals(created.getSlug(), detail.slug());
    }
}

