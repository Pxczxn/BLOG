/**
 * 标签服务
 * <p>
 * 处理标签的核心业务逻辑，包括标签的增删改查和 slug 自动生成。
 */
package com.pxczxn.blog.tag.service;

import com.pxczxn.blog.content.repository.ArticleTagQueryRepository;
import com.pxczxn.blog.community.post.repository.CommunityPostTagQueryRepository;
import com.pxczxn.blog.tag.dto.TagCreateRequest;
import com.pxczxn.blog.tag.dto.TagPublicResponse;
import com.pxczxn.blog.tag.entity.Tag;
import com.pxczxn.blog.tag.exception.TagNotFoundException;
import com.pxczxn.blog.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class TagService {

    /** slug最大长度 */
    private static final int MAX_SLUG_LENGTH = 120;
    /** slug生成重试最大次数 */
    private static final int MAX_SLUG_RETRY = 10;
    /** 随机后缀长度 */
    private static final int RANDOM_SUFFIX_LENGTH = 4;
    /** 随机字符集（小写字母+数字） */
    private static final char[] RANDOM_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
    /** Unicode变音符号正则 */
    private static final Pattern DIACRITICS = Pattern.compile("\\p{M}+");
    /** 非slug合法字符正则 */
    private static final Pattern NON_SLUG_CHARS = Pattern.compile("[^a-z0-9\\s-]");
    /** 连续空白字符正则 */
    private static final Pattern MULTI_WHITESPACE = Pattern.compile("\\s+");
    /** 连续连字符正则 */
    private static final Pattern MULTI_HYPHEN = Pattern.compile("-+");

    /** 标签数据访问 */
    private final TagRepository tagRepository;
    private final JdbcTemplate jdbcTemplate;
    /** 文章-标签关联数据访问 */
    private final ArticleTagQueryRepository articleTagQueryRepository;
    private final CommunityPostTagQueryRepository communityPostTagQueryRepository;
    /** 安全随机数生成器 */
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * 获取所有标签列表，按创建时间降序排列
     *
     * @return 标签列表
     */
    @Transactional(readOnly = true)
    public List<TagPublicResponse> list() {
        List<Tag> tags = tagRepository.findAllByOrderByCreatedAtDesc();
        Map<Long, Long> articleCounts = loadArticleCountsByTagId();
        return tags.stream()
                .map(tag -> TagPublicResponse.from(tag, articleCounts.get(tag.getId())))
                .toList();
    }

    /**
     * 创建新标签
     * <p>
     * 校验标签名称唯一性后，自动生成唯一slug并保存。
     *
     * @param request 标签创建请求
     * @return 创建成功的标签
     * @throws IllegalArgumentException 标签名称已存在时抛出
     */
    @Transactional
    public Tag create(TagCreateRequest request) {
        String name = request.getName().trim();
        if (tagRepository.existsByName(name)) {
            throw new IllegalArgumentException("Tag name already exists");
        }

        Tag tag = Tag.builder()
                .name(name)
                .slug(generateUniqueSlug(name))
                .build();

        return tagRepository.save(tag);
    }

    /**
     * 根据ID删除标签
     * <p>
     * 同时删除该标签与文章的关联关系。
     *
     * @param id 标签ID
     * @throws TagNotFoundException 标签不存在时抛出
     */
    @Transactional
    public void delete(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new TagNotFoundException(id));
        articleTagQueryRepository.deleteByTagId(id);
        communityPostTagQueryRepository.deleteByTagId(id);
        tagRepository.delete(tag);
    }

    /**
     * 生成唯一的slug
     * <p>
     * 基于标签名称生成slug，若已存在则追加随机后缀重试。
     *
     * @param name 标签名称
     * @return 唯一的slug字符串
     * @throws IllegalArgumentException 超过最大重试次数仍无法生成唯一slug时抛出
     */
    private String generateUniqueSlug(String name) {
        String base = trimSlug(slugify(name), MAX_SLUG_LENGTH);
        String candidate = base;

        int retry = 0;
        while (tagRepository.existsBySlug(candidate)) {
            retry++;
            if (retry > MAX_SLUG_RETRY) {
                throw new IllegalArgumentException("Cannot generate unique tag slug");
            }

            String suffix = randomSuffix(RANDOM_SUFFIX_LENGTH);
            String prefix = trimSlug(base, MAX_SLUG_LENGTH - RANDOM_SUFFIX_LENGTH - 1);
            candidate = prefix + "-" + suffix;
        }

        return candidate;
    }

    /**
     * 将字符串转换为slug格式
     * <p>
     * 处理流程：去除变音符号 -> 转小写 -> 移除非法字符 -> 空格转连字符 -> 合并连续连字符 -> 去除首尾连字符
     *
     * @param source 原始字符串
     * @return slug格式的字符串，若结果为空则返回"tag"
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

        return compact.isBlank() ? "tag" : compact;
    }

    /**
     * 截断slug到指定最大长度，并移除末尾连字符
     *
     * @param slug      原始slug
     * @param maxLength 最大长度
     * @return 截断后的slug
     */
    private String trimSlug(String slug, int maxLength) {
        if (slug.length() <= maxLength) {
            return slug;
        }
        return slug.substring(0, maxLength).replaceAll("-$", "");
    }

    /**
     * 生成指定长度的随机后缀
     *
     * @param len 后缀长度
     * @return 随机后缀字符串
     */
    private String randomSuffix(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(RANDOM_CHARS[secureRandom.nextInt(RANDOM_CHARS.length)]);
        }
        return sb.toString();
    }

    private Map<Long, Long> loadArticleCountsByTagId() {
        String sql = "SELECT tag_id, COUNT(*) AS article_count FROM article_tag GROUP BY tag_id";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        Map<Long, Long> result = new HashMap<>();
        for (Map<String, Object> row : rows) {
            Object tagIdValue = row.get("tag_id");
            Object articleCountValue = row.get("article_count");
            if (tagIdValue instanceof Number tagId && articleCountValue instanceof Number articleCount) {
                result.put(tagId.longValue(), articleCount.longValue());
            }
        }
        return result;
    }
}

