package com.pxczxn.blog.content.service;

import com.pxczxn.blog.category.entity.Category;
import com.pxczxn.blog.category.repository.CategoryRepository;
import com.pxczxn.blog.common.response.PageResponse;
import com.pxczxn.blog.content.dto.ArticleAdminDetailResponse;
import com.pxczxn.blog.content.dto.ArticleAdminListItemResponse;
import com.pxczxn.blog.content.dto.ArticleCategorySummaryResponse;
import com.pxczxn.blog.content.dto.ArticleCreateRequest;
import com.pxczxn.blog.content.dto.ArticleResponse;
import com.pxczxn.blog.content.dto.ArticleStatusUpdateResponse;
import com.pxczxn.blog.content.dto.ArticleTagSummaryResponse;
import com.pxczxn.blog.content.dto.ArticleUpdateRequest;
import com.pxczxn.blog.content.dto.PublicArticleDetailResponse;
import com.pxczxn.blog.content.dto.PublicArticleListItemResponse;
import com.pxczxn.blog.content.entity.Article;
import com.pxczxn.blog.content.entity.ArticleStatus;
import com.pxczxn.blog.content.exception.ArticleNotFoundException;
import com.pxczxn.blog.content.exception.SlugAlreadyExistsException;
import com.pxczxn.blog.content.repository.ArticleRepository;
import com.pxczxn.blog.content.repository.ArticleTagQueryRepository;
import com.pxczxn.blog.tag.entity.Tag;
import com.pxczxn.blog.tag.repository.TagRepository;
import com.pxczxn.blog.user.entity.AdminUser;
import com.pxczxn.blog.user.repository.AdminUserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.text.Normalizer;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {

    private static final int MAX_SLUG_LENGTH = 200;
    private static final int MAX_SLUG_RETRY = 10;
    private static final int RANDOM_SUFFIX_LENGTH = 4;
    private static final char[] RANDOM_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
    private static final Pattern DIACRITICS = Pattern.compile("\\p{M}+");
    private static final Pattern NON_SLUG_CHARS = Pattern.compile("[^a-z0-9\\s-]");
    private static final Pattern MULTI_WHITESPACE = Pattern.compile("\\s+");
    private static final Pattern MULTI_HYPHEN = Pattern.compile("-+");

    private final ArticleRepository articleRepository;
    private final ArticleTagQueryRepository articleTagQueryRepository;
    private final AdminUserRepository adminUserRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public ArticleResponse create(ArticleCreateRequest request, Long authorId) {
        AdminUser author = adminUserRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("Author does not exist: " + authorId));

        ArticleStatus status = request.getStatus() != null ? request.getStatus() : ArticleStatus.DRAFT;
        String slug = generateUniqueSlug(request.getTitle());

        Article article = Article.builder()
                .title(request.getTitle())
                .slug(slug)
                .summary(request.getSummary())
                .content(request.getContent())
                .coverImage(request.getCoverImage())
                .categoryId(normalizeCategoryId(request.getCategoryId()))
                .status(status)
                .author(author)
                .build();

        if (ArticleStatus.PUBLISHED.equals(status)) {
            article.setPublishedAt(LocalDateTime.now());
        }

        Article saved = articleRepository.save(article);
        log.info("Article created: id={}, slug={}, author={}", saved.getId(), saved.getSlug(), author.getUsername());
        return toArticleResponse(saved);
    }

    @Transactional(readOnly = true)
    public ArticleResponse getById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ArticleNotFoundException(id));
        return toArticleResponse(article);
    }

    @Transactional(readOnly = true)
    public ArticleAdminDetailResponse getAdminDetail(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ArticleNotFoundException(id));
        List<Long> tagIds = articleTagQueryRepository.findTagIdsByArticleId(id);
        return ArticleAdminDetailResponse.from(article, tagIds);
    }

    @Transactional(readOnly = true)
    public ArticleResponse getBySlug(String slug) {
        Article article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new ArticleNotFoundException(slug));
        return toArticleResponse(article);
    }

    @Transactional(readOnly = true)
    public PublicArticleDetailResponse getPublishedBySlug(String slug) {
        Article article = articleRepository.findBySlugAndStatus(slug, ArticleStatus.PUBLISHED)
                .orElseThrow(() -> new ArticleNotFoundException(slug));
        Map<Long, Category> categoriesById = loadCategoriesById(
                article.getCategoryId() == null ? List.of() : List.of(article.getCategoryId())
        );
        Map<Long, List<ArticleTagSummaryResponse>> tagsByArticleId = loadTagSummariesByArticleIds(List.of(article.getId()));
        return PublicArticleDetailResponse.from(
                article,
                toCategorySummary(article, categoriesById),
                tagsByArticleId.getOrDefault(article.getId(), List.of())
        );
    }

    @Transactional(readOnly = true)
    public List<ArticleResponse> listAll() {
        Sort sort = Sort.by(Sort.Order.asc("status"), Sort.Order.desc("publishedAt"));
        return articleRepository.findAll(sort).stream()
                .map(this::toArticleResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ArticleAdminListItemResponse> listAdmin(String status, String keyword) {
        Specification<Article> specification = buildAdminListSpecification(status, keyword);
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        List<Article> articles = articleRepository.findAll(specification, sort);
        Map<Long, Category> categoriesById = loadCategoriesById(extractCategoryIds(articles));
        return articles.stream()
                .map(article -> ArticleAdminListItemResponse.from(article, getCategoryName(article, categoriesById)))
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<ArticleAdminListItemResponse> listAdmin(int page, int size, String status, String keyword) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.max(size, 1);
        int p = Math.max(safePage - 1, 0);
        Pageable pageable = PageRequest.of(p, safeSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Specification<Article> specification = buildAdminListSpecification(status, keyword);
        Page<Article> result = articleRepository.findAll(specification, pageable);
        Map<Long, Category> categoriesById = loadCategoriesById(extractCategoryIds(result.getContent()));
        List<ArticleAdminListItemResponse> items = result.getContent().stream()
                .map(article -> ArticleAdminListItemResponse.from(article, getCategoryName(article, categoriesById)))
                .toList();
        return new PageResponse<>(items, result.getTotalElements(), safePage, result.getSize());
    }

    @Transactional(readOnly = true)
    public List<ArticleResponse> listByStatus(ArticleStatus status) {
        return articleRepository.findByStatusOrderByPublishedAtDesc(status).stream()
                .map(this::toArticleResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ArticleResponse> listPublished() {
        return listByStatus(ArticleStatus.PUBLISHED);
    }

    @Transactional(readOnly = true)
    public PageResponse<PublicArticleListItemResponse> listPublished(
            Pageable pageable,
            int page,
            String categoryKey,
            String tagKey
    ) {
        Long categoryId = resolveCategoryId(categoryKey);
        Long tagId = resolveTagId(tagKey);
        boolean invalidCategoryFilter = hasText(categoryKey) && categoryId == null;
        boolean invalidTagFilter = hasText(tagKey) && tagId == null;
        List<Long> articleIdsByTag = tagId == null
                ? Collections.emptyList()
                : articleTagQueryRepository.findArticleIdsByTagId(tagId);
        Specification<Article> specification = buildPublicListSpecification(
                categoryId,
                tagId,
                invalidCategoryFilter,
                invalidTagFilter,
                articleIdsByTag
        );
        Page<Article> articlePage = articleRepository.findAll(specification, pageable);
        Map<Long, Category> categoriesById = loadCategoriesById(extractCategoryIds(articlePage.getContent()));
        List<PublicArticleListItemResponse> items = articlePage.getContent().stream()
                .map(article -> PublicArticleListItemResponse.from(
                        article,
                        toCategorySummary(article, categoriesById)
                ))
                .toList();

        return new PageResponse<>(items, articlePage.getTotalElements(), page, articlePage.getSize());
    }

    @Transactional(readOnly = true)
    public PageResponse<PublicArticleListItemResponse> listPublished(Pageable pageable, int page) {
        return listPublished(pageable, page, null, null);
    }

    @Transactional
    public ArticleResponse update(Long id, ArticleUpdateRequest request, Long authorId) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ArticleNotFoundException(id));

        if (request.getSlug() != null && !request.getSlug().equals(article.getSlug())) {
            if (articleRepository.existsBySlug(request.getSlug())) {
                throw new SlugAlreadyExistsException(request.getSlug());
            }
            article.setSlug(request.getSlug());
        }

        if (request.getTitle() != null) {
            article.setTitle(request.getTitle());
        }
        if (request.getSummary() != null) {
            article.setSummary(request.getSummary());
        }
        if (request.getContent() != null) {
            article.setContent(request.getContent());
        }
        if (request.getCoverImage() != null) {
            article.setCoverImage(request.getCoverImage());
        }

        article.setCategoryId(normalizeCategoryId(request.getCategoryId()));

        if (request.getStatus() != null) {
            ArticleStatus oldStatus = article.getStatus();
            article.setStatus(request.getStatus());

            if (ArticleStatus.DRAFT.equals(oldStatus) && ArticleStatus.PUBLISHED.equals(request.getStatus())) {
                article.setPublishedAt(LocalDateTime.now());
                log.info("Article published by update: id={}, title={}", article.getId(), article.getTitle());
            } else if (ArticleStatus.PUBLISHED.equals(oldStatus) && ArticleStatus.DRAFT.equals(request.getStatus())) {
                article.setPublishedAt(null);
                log.info("Article reverted to draft: id={}, title={}", article.getId(), article.getTitle());
            }
        }

        Article saved = articleRepository.save(article);
        log.info("Article updated: id={}", saved.getId());
        return toArticleResponse(saved);
    }

    @Transactional
    public void updateTags(Long id, List<Long> tagIds) {
        articleRepository.findById(id)
                .orElseThrow(() -> new ArticleNotFoundException(id));

        List<Long> normalizedTagIds = normalizeTagIds(tagIds);
        articleTagQueryRepository.replaceTagsForArticle(id, normalizedTagIds);
        log.info("Article tags updated: id={}, tagCount={}", id, normalizedTagIds.size());
    }

    @Transactional
    public void delete(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ArticleNotFoundException(id));
        articleRepository.delete(article);
        log.info("Article deleted: id={}, title={}", article.getId(), article.getTitle());
    }

    @Transactional
    public ArticleResponse publish(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ArticleNotFoundException(id));

        if (ArticleStatus.PUBLISHED.equals(article.getStatus())) {
            return toArticleResponse(article);
        }

        article.setStatus(ArticleStatus.PUBLISHED);
        article.setPublishedAt(LocalDateTime.now());
        Article saved = articleRepository.save(article);

        log.info("Article published: id={}, title={}", saved.getId(), saved.getTitle());
        return toArticleResponse(saved);
    }

    @Transactional
    public ArticleStatusUpdateResponse publishToPublished(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ArticleNotFoundException(id));

        LocalDateTime now = LocalDateTime.now();
        article.setStatus(ArticleStatus.PUBLISHED);
        if (article.getPublishedAt() == null) {
            article.setPublishedAt(now);
        }
        article.setUpdatedAt(now);
        article = articleRepository.save(article);

        return ArticleStatusUpdateResponse.from(article);
    }

    @Transactional
    public ArticleResponse unpublish(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ArticleNotFoundException(id));

        if (ArticleStatus.DRAFT.equals(article.getStatus())) {
            return toArticleResponse(article);
        }

        article.setStatus(ArticleStatus.DRAFT);
        article.setPublishedAt(null);
        Article saved = articleRepository.save(article);

        log.info("Article unpublished: id={}, title={}", saved.getId(), saved.getTitle());
        return toArticleResponse(saved);
    }

    @Transactional
    public ArticleStatusUpdateResponse publishToDraft(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ArticleNotFoundException(id));

        LocalDateTime now = LocalDateTime.now();
        article.setStatus(ArticleStatus.DRAFT);
        article.setPublishedAt(null);
        article.setUpdatedAt(now);
        article = articleRepository.save(article);

        return ArticleStatusUpdateResponse.from(article);
    }

    private ArticleResponse toArticleResponse(Article article) {
        String authorName = article.getAuthor() != null ? article.getAuthor().getUsername() : null;
        return ArticleResponse.from(article, authorName);
    }

    private Specification<Article> buildAdminListSpecification(String status, String keyword) {
        ArticleStatus statusFilter = parseStatus(status);
        String keywordFilter = keyword == null ? null : keyword.trim();

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (statusFilter != null) {
                predicates.add(cb.equal(root.get("status"), statusFilter));
            }

            if (keywordFilter != null && !keywordFilter.isBlank()) {
                String like = "%" + keywordFilter.toLowerCase(Locale.ROOT) + "%";
                predicates.add(cb.like(cb.lower(root.get("title")), like));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Specification<Article> buildPublicListSpecification(
            Long categoryId,
            Long tagId,
            boolean invalidCategoryFilter,
            boolean invalidTagFilter,
            List<Long> articleIdsByTag
    ) {
        return (root, query, cb) -> {
            if (invalidCategoryFilter || invalidTagFilter) {
                return cb.disjunction();
            }

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("status"), ArticleStatus.PUBLISHED));

            if (categoryId != null) {
                predicates.add(cb.equal(root.get("categoryId"), categoryId));
            }

            if (tagId != null) {
                if (articleIdsByTag == null || articleIdsByTag.isEmpty()) {
                    return cb.disjunction();
                }
                predicates.add(root.get("id").in(articleIdsByTag));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private ArticleStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return ArticleStatus.valueOf(status.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid status");
        }
    }

    private String generateUniqueSlug(String title) {
        String slugBase = slugify(title);
        String epochSeconds = String.valueOf(Instant.now().getEpochSecond());
        String prefix = trimSlug(epochSeconds + "-" + slugBase, MAX_SLUG_LENGTH);
        String candidate = prefix;

        int retry = 0;
        while (articleRepository.existsBySlug(candidate)) {
            retry++;
            if (retry > MAX_SLUG_RETRY) {
                throw new SlugAlreadyExistsException(prefix);
            }

            String suffix = randomSuffix(RANDOM_SUFFIX_LENGTH);
            String truncatedPrefix = trimSlug(prefix, MAX_SLUG_LENGTH - RANDOM_SUFFIX_LENGTH - 1);
            candidate = truncatedPrefix + "-" + suffix;
        }

        return candidate;
    }

    private String slugify(String title) {
        String source = title == null ? "" : title;
        String normalized = Normalizer.normalize(source, Normalizer.Form.NFD);
        String noAccents = DIACRITICS.matcher(normalized).replaceAll("");
        String lower = noAccents.toLowerCase(Locale.ROOT).trim();
        String cleaned = NON_SLUG_CHARS.matcher(lower).replaceAll("");
        String hyphenated = MULTI_WHITESPACE.matcher(cleaned).replaceAll("-");
        String compact = MULTI_HYPHEN.matcher(hyphenated).replaceAll("-")
                .replaceAll("^-", "")
                .replaceAll("-$", "");

        return compact.isBlank() ? "article" : compact;
    }

    private String trimSlug(String slug, int maxLength) {
        if (slug.length() <= maxLength) {
            return slug;
        }
        String trimmed = slug.substring(0, maxLength);
        return trimmed.replaceAll("-$", "");
    }

    private String randomSuffix(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(RANDOM_CHARS[secureRandom.nextInt(RANDOM_CHARS.length)]);
        }
        return sb.toString();
    }

    private Long normalizeCategoryId(Long categoryId) {
        if (categoryId == null || categoryId <= 0) {
            return null;
        }
        if (!categoryRepository.existsById(categoryId)) {
            throw new IllegalArgumentException("Category not found");
        }
        return categoryId;
    }

    private List<Long> normalizeTagIds(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return List.of();
        }

        List<Long> normalized = tagIds.stream()
                .filter(id -> id != null && id > 0)
                .collect(java.util.stream.Collectors.collectingAndThen(
                        java.util.stream.Collectors.toCollection(LinkedHashSet::new),
                        List::copyOf
                ));
        if (normalized.isEmpty()) {
            return List.of();
        }

        Set<Long> existing = tagRepository.findAllById(normalized).stream()
                .map(Tag::getId)
                .collect(java.util.stream.Collectors.toSet());
        if (existing.size() != normalized.size()) {
            throw new IllegalArgumentException("Tag not found");
        }
        return normalized;
    }

    private Long resolveCategoryId(String categoryKey) {
        if (categoryKey == null || categoryKey.isBlank()) {
            return null;
        }

        String value = categoryKey.trim();
        try {
            Long categoryId = Long.valueOf(value);
            if (categoryRepository.existsById(categoryId)) {
                return categoryId;
            }
        } catch (NumberFormatException ignored) {
        }

        return categoryRepository.findBySlug(value)
                .map(Category::getId)
                .orElse(null);
    }

    private Long resolveTagId(String tagKey) {
        if (tagKey == null || tagKey.isBlank()) {
            return null;
        }

        String value = tagKey.trim();
        try {
            Long tagId = Long.valueOf(value);
            if (tagRepository.existsById(tagId)) {
                return tagId;
            }
        } catch (NumberFormatException ignored) {
        }

        return tagRepository.findBySlug(value)
                .map(Tag::getId)
                .orElse(null);
    }

    private List<Long> extractCategoryIds(Collection<Article> articles) {
        if (articles == null || articles.isEmpty()) {
            return List.of();
        }

        return articles.stream()
                .map(Article::getCategoryId)
                .filter(id -> id != null && id > 0)
                .distinct()
                .toList();
    }

    private Map<Long, Category> loadCategoriesById(Collection<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return new HashMap<>();
        }

        return categoryRepository.findAllById(categoryIds).stream()
                .collect(java.util.stream.Collectors.toMap(Category::getId, category -> category));
    }

    private Map<Long, List<ArticleTagSummaryResponse>> loadTagSummariesByArticleIds(List<Long> articleIds) {
        if (articleIds == null || articleIds.isEmpty()) {
            return new HashMap<>();
        }

        Map<Long, List<Long>> tagIdsByArticleId = articleTagQueryRepository.findTagIdsByArticleIds(articleIds);
        Set<Long> tagIds = tagIdsByArticleId.values().stream()
                .flatMap(List::stream)
                .collect(java.util.stream.Collectors.toSet());
        if (tagIds.isEmpty()) {
            return new HashMap<>();
        }

        Map<Long, Tag> tagsById = tagRepository.findAllById(tagIds).stream()
                .collect(java.util.stream.Collectors.toMap(Tag::getId, tag -> tag));
        Map<Long, List<ArticleTagSummaryResponse>> result = new HashMap<>();
        for (Map.Entry<Long, List<Long>> entry : tagIdsByArticleId.entrySet()) {
            List<ArticleTagSummaryResponse> tags = entry.getValue().stream()
                    .map(tagsById::get)
                    .filter(tag -> tag != null)
                    .map(ArticleTagSummaryResponse::from)
                    .toList();
            result.put(entry.getKey(), tags);
        }
        return result;
    }

    private String getCategoryName(Article article, Map<Long, Category> categoriesById) {
        if (article.getCategoryId() == null) {
            return null;
        }
        Category category = categoriesById.get(article.getCategoryId());
        return category != null ? category.getName() : null;
    }

    private ArticleCategorySummaryResponse toCategorySummary(Article article, Map<Long, Category> categoriesById) {
        if (article.getCategoryId() == null) {
            return null;
        }
        Category category = categoriesById.get(article.getCategoryId());
        return ArticleCategorySummaryResponse.from(category);
    }

    /**
     * 根据 slug 获取文章导航信息
     */
    @Transactional(readOnly = true)
    public com.pxczxn.blog.content.dto.ArticleNavigationResponse.ArticleNavigationData getNavigation(String slug) {
        Article article = articleRepository.findBySlugAndStatus(slug, ArticleStatus.PUBLISHED)
                .orElseThrow(() -> new ArticleNotFoundException(slug));

        var previous = articleRepository.findPreviousArticle(article.getCreatedAt())
                .map(art -> com.pxczxn.blog.content.dto.ArticleNavigationResponse.ArticleNavigationItem.builder()
                        .id(art.getId())
                        .title(art.getTitle())
                        .slug(art.getSlug())
                        .createdAt(art.getCreatedAt())
                        .build())
                .orElse(null);

        var next = articleRepository.findNextArticle(article.getCreatedAt())
                .map(art -> com.pxczxn.blog.content.dto.ArticleNavigationResponse.ArticleNavigationItem.builder()
                        .id(art.getId())
                        .title(art.getTitle())
                        .slug(art.getSlug())
                        .createdAt(art.getCreatedAt())
                        .build())
                .orElse(null);

        return com.pxczxn.blog.content.dto.ArticleNavigationResponse.ArticleNavigationData.builder()
                .previous(previous)
                .next(next)
                .build();
    }

    /**
     * 增加文章浏览次数
     */
    @Transactional
    public void incrementViewCount(String slug) {
        articleRepository.findBySlug(slug).ifPresent(article -> {
            article.setViewCount((article.getViewCount() == null ? 0L : article.getViewCount()) + 1);
            articleRepository.save(article);
        });
    }
}