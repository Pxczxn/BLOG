


package com.pxczxn.blog.tag.service;

import com.pxczxn.blog.content.repository.ArticleTagQueryRepository;
import com.pxczxn.blog.community.post.repository.CommunityPostTagQueryRepository;
import com.pxczxn.blog.tag.dto.TagCreateRequest;
import com.pxczxn.blog.tag.entity.Tag;
import com.pxczxn.blog.tag.exception.TagNotFoundException;
import com.pxczxn.blog.tag.repository.TagRepository;
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
public class TagService {

    
    private static final int MAX_SLUG_LENGTH = 120;
    
    private static final int MAX_SLUG_RETRY = 10;
    
    private static final int RANDOM_SUFFIX_LENGTH = 4;
    
    private static final char[] RANDOM_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
    
    private static final Pattern DIACRITICS = Pattern.compile("\\p{M}+");
    
    private static final Pattern NON_SLUG_CHARS = Pattern.compile("[^a-z0-9\\s-]");
    
    private static final Pattern MULTI_WHITESPACE = Pattern.compile("\\s+");
    
    private static final Pattern MULTI_HYPHEN = Pattern.compile("-+");

    
    private final TagRepository tagRepository;
    
    private final ArticleTagQueryRepository articleTagQueryRepository;
    private final CommunityPostTagQueryRepository communityPostTagQueryRepository;
    
    private final SecureRandom secureRandom = new SecureRandom();

    


    @Transactional(readOnly = true)
    public List<Tag> list() {
        return tagRepository.findAllByOrderByCreatedAtDesc();
    }

    


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

    


    @Transactional
    public void delete(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new TagNotFoundException(id));
        articleTagQueryRepository.deleteByTagId(id);
        communityPostTagQueryRepository.deleteByTagId(id);
        tagRepository.delete(tag);
    }

    


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

