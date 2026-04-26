/**
 * 分类服务
 * <p>
 * 处理分类的核心业务逻辑，包括分类的增删改查和 slug 自动生成。
 * slug 根据分类名称自动生成 URL 友好的标识，支持去重重试和随机后缀。
 */
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

    /** slug最大长度 */
    private static final int MAX_SLUG_LENGTH = 120;
    /** slug生成重试最大次数 */
    private static final int MAX_SLUG_RETRY = 10;
    /** 随机后缀长度 */
    private static final int RANDOM_SUFFIX_LENGTH = 4;
    /** 用于生成随机后缀的字符集 */
    private static final char[] RANDOM_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
    /** 用于移除变音符号的正则表达式 */
    private static final Pattern DIACRITICS = Pattern.compile("\\p{M}+");
    /** 用于移除非slug字符的正则表达式 */
    private static final Pattern NON_SLUG_CHARS = Pattern.compile("[^a-z0-9\\s-]");
    /** 用于合并多个空格的正则表达式 */
    private static final Pattern MULTI_WHITESPACE = Pattern.compile("\\s+");
    /** 用于合并多个连字符的正则表达式 */
    private static final Pattern MULTI_HYPHEN = Pattern.compile("-+");

    private final CategoryRepository categoryRepository;
    private final ArticleRepository articleRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * 获取所有分类列表
     *
     * @return 按创建时间降序排列的分类列表
     */
    @Transactional(readOnly = true)
    public List<Category> list() {
        return categoryRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * 获取所有分类的公开响应列表
     *
     * @return 分类公开响应列表
     */
    @Transactional(readOnly = true)
    public List<CategoryPublicResponse> listPublic() {
        return categoryRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(CategoryPublicResponse::from)
                .toList();
    }

    /**
     * 创建新分类
     *
     * @param request 分类创建请求，包含分类名称
     * @return 创建成功后的分类响应
     * @throws IllegalArgumentException 如果分类名称已存在
     */
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

    /**
     * 更新分类信息
     *
     * @param id 分类ID
     * @param request 分类更新请求，包含新的分类名称
     * @return 更新后的分类响应
     * @throws CategoryNotFoundException 如果分类不存在
     * @throws IllegalArgumentException 如果新名称已被其他分类使用
     */
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

    /**
     * 删除分类
     *
     * @param id 分类ID
     * @throws CategoryNotFoundException 如果分类不存在
     * @throws CategoryInUseException 如果分类下存在文章，无法删除
     */
    @Transactional
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        if (articleRepository.existsByCategoryId(id)) {
            throw new CategoryInUseException(id);
        }

        categoryRepository.delete(category);
    }

    /**
     * 生成唯一的slug
     * <p>
     * 根据名称生成slug，如果已存在则添加随机后缀重试
     *
     * @param name 分类名称
     * @return 唯一的slug
     * @throws IllegalArgumentException 如果重试次数超过限制仍无法生成唯一slug
     */
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

    /**
     * 将字符串转换为URL友好的slug格式
     * <p>
     * 处理流程：规范化 -> 移除变音符号 -> 小写 -> 移除特殊字符 -> 替换空格为连字符
     *
     * @param source 源字符串
     * @return 转换后的slug
     */
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

    /**
     * 截断slug到指定长度
     *
     * @param slug 原始slug
     * @param maxLength 最大长度
     * @return 截断后的slug，不会以连字符结尾
     */
    private String trimSlug(String slug, int maxLength) {
        if (slug.length() <= maxLength) {
            return slug;
        }
        return slug.substring(0, maxLength).replaceAll("-$", "");
    }

    /**
     * 生成随机后缀
     *
     * @param len 后缀长度
     * @return 由小写字母和数字组成的随机字符串
     */
    private String randomSuffix(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(RANDOM_CHARS[secureRandom.nextInt(RANDOM_CHARS.length)]);
        }
        return sb.toString();
    }
}

