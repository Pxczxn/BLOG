package com.pxczxn.blog.category.service;

import com.pxczxn.blog.category.dto.CategoryCreateRequest;
import com.pxczxn.blog.category.dto.CategoryCreateResponse;
import com.pxczxn.blog.category.dto.CategoryPublicResponse;
import com.pxczxn.blog.category.dto.CategoryUpdateRequest;
import com.pxczxn.blog.category.dto.CategoryUpdateResponse;
import com.pxczxn.blog.category.entity.Category;
import com.pxczxn.blog.category.exception.CategoryInUseException;
import com.pxczxn.blog.category.exception.CategoryNotFoundException;
import com.pxczxn.blog.category.repository.CategoryRepository;
import com.pxczxn.blog.content.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private static final int MAX_SLUG_LENGTH = 120;
    private static final int MAX_SLUG_RETRY = 10;
    private static final int RANDOM_SUFFIX_LENGTH = 4;
    private static final char[] RANDOM_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
    private static final Pattern DIACRITICS = Pattern.compile("\\p{M}+");
    private static final Pattern NON_SLUG_CHARS = Pattern.compile("[^a-z0-9\\s-]");
    private static final Pattern MULTI_WHITESPACE = Pattern.compile("\\s+");
    private static final Pattern MULTI_HYPHEN = Pattern.compile("-+");

    private final CategoryRepository categoryRepository;
    private final ArticleRepository articleRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional(readOnly = true)
    public List<Category> list() {
        return categoryRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<CategoryPublicResponse> listPublic() {
        return categoryRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(CategoryPublicResponse::from)
                .toList();
    }

    @Transactional
    public CategoryCreateResponse create(CategoryCreateRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Category name already exists");
        }

        Category category = Category.builder()
                .name(request.getName().trim())
                .slug(generateUniqueSlug(request.getName()))
                .build();

        return CategoryCreateResponse.from(categoryRepository.save(category));
    }

    @Transactional
    public CategoryUpdateResponse update(Long id, CategoryUpdateRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        String newName = request.getName().trim();
        if (!newName.equals(category.getName()) && categoryRepository.existsByName(newName)) {
            throw new IllegalArgumentException("Category name already exists");
        }

        category.setName(newName);
        return CategoryUpdateResponse.from(categoryRepository.save(category));
    }

    @Transactional
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        if (articleRepository.existsByCategoryId(id)) {
            throw new CategoryInUseException(id);
        }

        categoryRepository.delete(category);
    }

    private String generateUniqueSlug(String name) {
        String base = trimSlug(slugify(name), MAX_SLUG_LENGTH);
        String candidate = base;

        int retry = 0;
        while (categoryRepository.existsBySlug(candidate)) {
            retry++;
            if (retry > MAX_SLUG_RETRY) {
                throw new IllegalArgumentException("Cannot generate unique category slug");
            }

            String suffix = randomSuffix(RANDOM_SUFFIX_LENGTH);
            String prefix = trimSlug(base, MAX_SLUG_LENGTH - RANDOM_SUFFIX_LENGTH - 1);
            candidate = prefix + "-" + suffix;
        }

        return candidate;
    }

    private String slugify(String source) {
        String normalized = Normalizer.normalize(source == null ? "" : source, Normalizer.Form.NFD);
        String noAccents = DIACRITICS.matcher(normalized).replaceAll("");
        String lower = noAccents.toLowerCase(Locale.ROOT).trim();
        String cleaned = NON_SLUG_CHARS.matcher(lower).replaceAll("");
        String hyphenated = MULTI_WHITESPACE.matcher(cleaned).replaceAll("-");
        String compact = MULTI_HYPHEN.matcher(hyphenated).replaceAll("-")
                .replaceAll("^-", "")
                .replaceAll("-$", "");

        return compact.isBlank() ? "category" : compact;
    }

    private String trimSlug(String slug, int maxLength) {
        if (slug.length() <= maxLength) {
            return slug;
        }
        return slug.substring(0, maxLength).replaceAll("-$", "");
    }

    private String randomSuffix(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(RANDOM_CHARS[secureRandom.nextInt(RANDOM_CHARS.length)]);
        }
        return sb.toString();
    }
}
