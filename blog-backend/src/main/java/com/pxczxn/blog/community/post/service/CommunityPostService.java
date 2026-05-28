/**
 * 社区帖子服务
 * <p>
 * 处理社区帖子的增删改查、发布/下架和审核状态管理
 */
package com.pxczxn.blog.community.post.service;

import com.pxczxn.blog.common.response.ApiErrorCode;
import com.pxczxn.blog.common.response.PageResponse;
import com.pxczxn.blog.community.entity.CommunityUser;
import com.pxczxn.blog.community.entity.CommunityUserStatus;
import com.pxczxn.blog.community.exception.CommunityAuthException;
import com.pxczxn.blog.community.interaction.dto.PostInteractionResponse;
import com.pxczxn.blog.community.interaction.service.CommunityInteractionService;
import com.pxczxn.blog.community.moderation.service.ModerationService;
import com.pxczxn.blog.community.node.entity.CommunityNode;
import com.pxczxn.blog.community.node.repository.CommunityNodeRepository;
import com.pxczxn.blog.community.node.service.CommunityNodeService;
import com.pxczxn.blog.community.post.dto.AdminCommunityPostListItemResponse;
import com.pxczxn.blog.community.post.dto.AdminCommunityPostStatusRequest;
import com.pxczxn.blog.community.post.dto.CommunityPostAuthorSummaryResponse;
import com.pxczxn.blog.community.post.dto.CommunityPostEditorResponse;
import com.pxczxn.blog.community.post.dto.CommunityPostMineItemResponse;
import com.pxczxn.blog.community.post.dto.CommunityPostNodeSummaryResponse;
import com.pxczxn.blog.community.post.dto.CommunityPostTagSummaryResponse;
import com.pxczxn.blog.community.post.dto.CommunityPostWriteRequest;
import com.pxczxn.blog.community.post.dto.PublicCommunityPostDetailResponse;
import com.pxczxn.blog.community.post.dto.PublicCommunityPostListItemResponse;
import com.pxczxn.blog.community.post.entity.CommunityPost;
import com.pxczxn.blog.community.post.entity.CommunityPostStatus;
import com.pxczxn.blog.community.post.comment.entity.CommunityPostCommentStatus;
import com.pxczxn.blog.community.post.comment.repository.CommunityPostCommentRepository;
import com.pxczxn.blog.community.post.exception.CommunityPostNotFoundException;
import com.pxczxn.blog.community.post.repository.CommunityPostRepository;
import com.pxczxn.blog.community.post.repository.CommunityPostTagQueryRepository;
import com.pxczxn.blog.community.repository.CommunityUserRepository;
import com.pxczxn.blog.tag.entity.Tag;
import com.pxczxn.blog.tag.repository.TagRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CommunityPostService {

    private static final int MAX_SLUG_LENGTH = 220;
    private static final int MAX_SLUG_RETRY = 10;
    private static final int RANDOM_SUFFIX_LENGTH = 4;
    private static final char[] RANDOM_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
    private static final Pattern DIACRITICS = Pattern.compile("\\p{M}+");
    private static final Pattern NON_SLUG_CHARS = Pattern.compile("[^a-z0-9\\s-]");
    private static final Pattern MULTI_WHITESPACE = Pattern.compile("\\s+");
    private static final Pattern MULTI_HYPHEN = Pattern.compile("-+");

    private final CommunityPostRepository communityPostRepository;
    private final CommunityNodeService communityNodeService;
    private final CommunityNodeRepository communityNodeRepository;
    private final CommunityUserRepository communityUserRepository;
    private final CommunityInteractionService communityInteractionService;
    private final ModerationService moderationService;
    private final CommunityPostTagQueryRepository communityPostTagQueryRepository;
    private final CommunityPostCommentRepository communityPostCommentRepository;
    private final TagRepository tagRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public CommunityPostEditorResponse create(Long authorId, CommunityPostWriteRequest request) {
        CommunityUser author = getActiveAuthor(authorId);
        CommunityNode node = communityNodeService.getActiveNode(request.getNodeId());
        CommunityPostStatus targetStatus = resolveWritableStatus(request.getStatus());

        CommunityPost post = CommunityPost.builder()
                .authorId(author.getId())
                .nodeId(node.getId())
                .title(request.getTitle().trim())
                .slug(generateUniqueSlug(request.getTitle()))
                .summary(trimToNull(request.getSummary()))
                .content(request.getContent().trim())
                .status(targetStatus)
                .lastEditedAt(LocalDateTime.now())
                .build();
        post.setPublishedAt(null);

        CommunityPost saved = communityPostRepository.save(post);
        List<Long> tagIds = normalizeTagIds(request.getTagIds());
        communityPostTagQueryRepository.replaceTagsForPost(saved.getId(), tagIds);
        if (targetStatus == CommunityPostStatus.PENDING_REVIEW) {
            moderationService.submitPostForReview(saved, authorId);
            saved = communityPostRepository.findById(saved.getId()).orElse(saved);
        }
        return CommunityPostEditorResponse.from(saved, buildTagSummaries(tagIds));
    }

    @Transactional
    public CommunityPostEditorResponse update(Long authorId, Long postId, CommunityPostWriteRequest request) {
        CommunityPost post = getOwnedPost(authorId, postId);
        CommunityNode node = communityNodeService.getActiveNode(request.getNodeId());
        CommunityPostStatus targetStatus = resolveWritableStatus(request.getStatus());

        String nextTitle = request.getTitle().trim();
        if (!post.getTitle().equals(nextTitle)) {
            post.setSlug(generateUniqueSlug(nextTitle));
        }
        post.setNodeId(node.getId());
        post.setTitle(nextTitle);
        post.setSummary(trimToNull(request.getSummary()));
        post.setContent(request.getContent().trim());
        post.setStatus(targetStatus);
        post.setLastEditedAt(LocalDateTime.now());
        post.setRejectionReason(null);
        post.setPublishedAt(null);

        CommunityPost saved = communityPostRepository.save(post);
        List<Long> tagIds = normalizeTagIds(request.getTagIds());
        communityPostTagQueryRepository.replaceTagsForPost(saved.getId(), tagIds);
        if (targetStatus == CommunityPostStatus.PENDING_REVIEW) {
            moderationService.submitPostForReview(saved, authorId);
            saved = communityPostRepository.findById(saved.getId()).orElse(saved);
        }
        return CommunityPostEditorResponse.from(saved, buildTagSummaries(tagIds));
    }

    @Transactional(readOnly = true)
    public CommunityPostEditorResponse getEditor(Long authorId, Long postId) {
        CommunityPost post = getOwnedPost(authorId, postId);
        return CommunityPostEditorResponse.from(post, buildTagSummaries(communityPostTagQueryRepository.findTagIdsByPostId(post.getId())));
    }

    @Transactional(readOnly = true)
    public List<CommunityPostMineItemResponse> listMine(Long authorId) {
        List<CommunityPost> posts = communityPostRepository.findAll(
                (root, query, cb) -> cb.equal(root.get("authorId"), authorId),
                Sort.by(Sort.Direction.DESC, "updatedAt")
        );
        Map<Long, CommunityNode> nodes = loadNodes(posts.stream().map(CommunityPost::getNodeId).toList());
        return posts.stream()
                .map(post -> CommunityPostMineItemResponse.from(
                        post,
                        CommunityPostNodeSummaryResponse.from(nodes.get(post.getNodeId()))
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<PublicCommunityPostListItemResponse> listPublished(Pageable pageable,
                                                                           int page,
                                                                           String nodeKey,
                                                                           String username,
                                                                           Long viewerUserId) {
        Long nodeId = communityNodeService.resolveNodeId(nodeKey);
        Long authorId = resolveAuthorId(username);
        boolean invalidNodeFilter = nodeKey != null && !nodeKey.isBlank() && nodeId == null;
        boolean invalidAuthorFilter = username != null && !username.isBlank() && authorId == null;

        Page<CommunityPost> result = communityPostRepository.findAll(buildPublicSpecification(nodeId, authorId, invalidNodeFilter, invalidAuthorFilter), pageable);
        Map<Long, CommunityNode> nodes = loadNodes(result.getContent().stream().map(CommunityPost::getNodeId).toList());
        Map<Long, CommunityUser> authors = loadAuthors(result.getContent().stream().map(CommunityPost::getAuthorId).toList());
        Map<Long, PostInteractionResponse> interactions = communityInteractionService.buildPostInteractionMap(
                result.getContent().stream().map(CommunityPost::getId).toList(),
                viewerUserId
        );
        Map<Long, List<CommunityPostTagSummaryResponse>> tagsByPostId = loadTagSummariesByPostIds(
                result.getContent().stream().map(CommunityPost::getId).toList()
        );
        List<PublicCommunityPostListItemResponse> items = result.getContent().stream()
                .map(post -> {
                    PostInteractionResponse interaction = interactions.getOrDefault(post.getId(), emptyInteraction(post.getId()));
                    return PublicCommunityPostListItemResponse.from(
                            post,
                            CommunityPostNodeSummaryResponse.from(nodes.get(post.getNodeId())),
                            CommunityPostAuthorSummaryResponse.from(authors.get(post.getAuthorId())),
                            interaction.getLikeCount(),
                            interaction.getFavoriteCount(),
                            tagsByPostId.getOrDefault(post.getId(), List.of())
                    );
                })
                .toList();
        return new PageResponse<>(items, result.getTotalElements(), page, result.getSize());
    }

    @Transactional(readOnly = true)
    public PublicCommunityPostDetailResponse getPublishedBySlug(String slug, Long viewerUserId) {
        CommunityPost post = resolvePublishedPost(slug);
        CommunityNode node = loadNode(post.getNodeId());
        CommunityUser author = loadAuthor(post.getAuthorId());
        PostInteractionResponse interaction = communityInteractionService.getPostInteraction(post.getId(), viewerUserId);
        return PublicCommunityPostDetailResponse.from(
                post,
                CommunityPostNodeSummaryResponse.from(node),
                CommunityPostAuthorSummaryResponse.from(author),
                interaction.getLikeCount(),
                interaction.getFavoriteCount(),
                interaction.isLikedByMe(),
                interaction.isFavoritedByMe(),
                buildTagSummaries(communityPostTagQueryRepository.findTagIdsByPostId(post.getId()))
        );
    }

    @Transactional
    public void incrementViewCount(String slug) {
        CommunityPost post = resolvePublishedPost(slug);
        post.setViewCount((post.getViewCount() == null ? 0L : post.getViewCount()) + 1);
        communityPostRepository.save(post);
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminCommunityPostListItemResponse> listAdmin(Pageable pageable, int page, String status, String nodeKey, String keyword) {
        CommunityPostStatus statusFilter = parseStatus(status);
        Long nodeId = communityNodeService.resolveNodeId(nodeKey);
        Page<CommunityPost> result = communityPostRepository.findAll(buildAdminSpecification(statusFilter, nodeId, keyword), pageable);
        Map<Long, CommunityNode> nodes = loadNodes(result.getContent().stream().map(CommunityPost::getNodeId).toList());
        Map<Long, CommunityUser> authors = loadAuthors(result.getContent().stream().map(CommunityPost::getAuthorId).toList());
        Map<Long, Long> likeCounts = communityInteractionService.buildPostInteractionMap(
                result.getContent().stream().map(CommunityPost::getId).toList(),
                null
        ).values().stream().collect(java.util.stream.Collectors.toMap(
                PostInteractionResponse::getPostId,
                PostInteractionResponse::getLikeCount
        ));
        Map<Long, Long> commentCounts = loadApprovedCommentCounts(result.getContent().stream().map(CommunityPost::getId).toList());
        List<AdminCommunityPostListItemResponse> items = result.getContent().stream()
                .map(post -> AdminCommunityPostListItemResponse.from(
                        post,
                        nodes.get(post.getNodeId()) != null ? nodes.get(post.getNodeId()).getName() : null,
                        authors.get(post.getAuthorId()) != null ? authors.get(post.getAuthorId()).getDisplayName() : null,
                        likeCounts.getOrDefault(post.getId(), 0L),
                        commentCounts.getOrDefault(post.getId(), 0L)
                ))
                .toList();
        return new PageResponse<>(items, result.getTotalElements(), page, result.getSize());
    }

    @Transactional
    public CommunityPostEditorResponse updateStatus(Long id, AdminCommunityPostStatusRequest request) {
        CommunityPost post = communityPostRepository.findById(id)
                .orElseThrow(() -> new CommunityPostNotFoundException(id));
        post.setStatus(request.getStatus());
        post.setRejectionReason(trimToNull(request.getRejectionReason()));
        if (request.getStatus() == CommunityPostStatus.PUBLISHED) {
            post.setPublishedAt(post.getPublishedAt() == null ? LocalDateTime.now() : post.getPublishedAt());
        } else {
            post.setPublishedAt(null);
        }
        CommunityPost saved = communityPostRepository.save(post);
        moderationService.syncPostTaskAfterAdminAction(saved.getId(), saved.getStatus(), null, saved.getRejectionReason());
        return CommunityPostEditorResponse.from(saved);
    }

    @Transactional
    public void delete(Long id) {
        CommunityPost post = communityPostRepository.findById(id)
                .orElseThrow(() -> new CommunityPostNotFoundException(id));
        communityPostRepository.delete(post);
    }

    @Transactional(readOnly = true)
    public long countPublishedByAuthorId(Long authorId) {
        return communityPostRepository.countByAuthorIdAndStatus(authorId, CommunityPostStatus.PUBLISHED);
    }

    private Specification<CommunityPost> buildPublicSpecification(Long nodeId,
                                                                  Long authorId,
                                                                  boolean invalidNodeFilter,
                                                                  boolean invalidAuthorFilter) {
        return (root, query, cb) -> {
            if (invalidNodeFilter || invalidAuthorFilter) {
                return cb.disjunction();
            }
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("status"), CommunityPostStatus.PUBLISHED));
            if (nodeId != null) {
                predicates.add(cb.equal(root.get("nodeId"), nodeId));
            }
            if (authorId != null) {
                predicates.add(cb.equal(root.get("authorId"), authorId));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Specification<CommunityPost> buildAdminSpecification(CommunityPostStatus status, Long nodeId, String keyword) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (nodeId != null) {
                predicates.add(cb.equal(root.get("nodeId"), nodeId));
            }
            if (keyword != null && !keyword.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private CommunityPost getOwnedPost(Long authorId, Long postId) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new CommunityPostNotFoundException(postId));
        if (!post.getAuthorId().equals(authorId)) {
            throw new CommunityAuthException(ApiErrorCode.AUTH_FORBIDDEN, "You cannot edit this post");
        }
        return post;
    }

    private CommunityUser getActiveAuthor(Long authorId) {
        CommunityUser user = loadAuthor(authorId);
        if (user.getStatus() != CommunityUserStatus.ACTIVE) {
            throw CommunityAuthException.accountDisabled();
        }
        return user;
    }

    private CommunityUser loadAuthor(Long authorId) {
        return communityUserRepository.findById(authorId)
                .orElseThrow(() -> new CommunityPostNotFoundException(authorId));
    }

    private Long resolveAuthorId(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }
        return communityUserRepository.findByUsername(username.trim().toLowerCase(Locale.ROOT))
                .map(CommunityUser::getId)
                .orElse(null);
    }

    private Map<Long, CommunityNode> loadNodes(Collection<Long> nodeIds) {
        Map<Long, CommunityNode> nodes = new HashMap<>();
        Set<Long> uniqueIds = nodeIds.stream().filter(Objects::nonNull).collect(java.util.stream.Collectors.toSet());
        if (uniqueIds.isEmpty()) {
            return nodes;
        }
        communityNodeRepository.findAllById(uniqueIds).forEach(node -> nodes.put(node.getId(), node));
        return nodes;
    }

    private Map<Long, CommunityUser> loadAuthors(Collection<Long> authorIds) {
        Map<Long, CommunityUser> authors = new HashMap<>();
        Set<Long> uniqueIds = authorIds.stream().filter(Objects::nonNull).collect(java.util.stream.Collectors.toSet());
        if (uniqueIds.isEmpty()) {
            return authors;
        }
        communityUserRepository.findAllById(uniqueIds).forEach(user -> authors.put(user.getId(), user));
        return authors;
    }

    private CommunityNode loadNode(Long nodeId) {
        return communityNodeRepository.findById(nodeId)
                .orElseThrow(() -> new CommunityPostNotFoundException(nodeId));
    }

    private CommunityPost resolvePublishedPost(String slugOrId) {
        return communityPostRepository.findBySlugAndStatus(slugOrId, CommunityPostStatus.PUBLISHED)
                .or(() -> parsePostId(slugOrId)
                        .flatMap(communityPostRepository::findById)
                        .filter(post -> post.getStatus() == CommunityPostStatus.PUBLISHED))
                .orElseThrow(() -> new CommunityPostNotFoundException(slugOrId));
    }

    private java.util.Optional<Long> parsePostId(String slugOrId) {
        if (slugOrId == null || slugOrId.isBlank()) {
            return java.util.Optional.empty();
        }
        try {
            return java.util.Optional.of(Long.parseLong(slugOrId));
        } catch (NumberFormatException ex) {
            return java.util.Optional.empty();
        }
    }

    private List<Long> normalizeTagIds(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return List.of();
        }

        List<Long> normalized = tagIds.stream()
                .filter(id -> id != null && id > 0)
                .collect(java.util.stream.Collectors.collectingAndThen(
                        java.util.stream.Collectors.toCollection(java.util.LinkedHashSet::new),
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

    private List<CommunityPostTagSummaryResponse> buildTagSummaries(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return List.of();
        }
        Map<Long, Tag> tagsById = tagRepository.findAllById(tagIds).stream()
                .collect(java.util.stream.Collectors.toMap(Tag::getId, tag -> tag));
        return tagIds.stream()
                .map(tagsById::get)
                .filter(tag -> tag != null)
                .map(CommunityPostTagSummaryResponse::from)
                .toList();
    }

    private Map<Long, List<CommunityPostTagSummaryResponse>> loadTagSummariesByPostIds(List<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return new HashMap<>();
        }

        Map<Long, List<Long>> tagIdsByPostId = communityPostTagQueryRepository.findTagIdsByPostIds(postIds);
        Set<Long> tagIds = tagIdsByPostId.values().stream()
                .flatMap(List::stream)
                .collect(java.util.stream.Collectors.toSet());
        if (tagIds.isEmpty()) {
            return new HashMap<>();
        }

        Map<Long, Tag> tagsById = tagRepository.findAllById(tagIds).stream()
                .collect(java.util.stream.Collectors.toMap(Tag::getId, tag -> tag));
        Map<Long, List<CommunityPostTagSummaryResponse>> result = new HashMap<>();
        for (Map.Entry<Long, List<Long>> entry : tagIdsByPostId.entrySet()) {
            List<CommunityPostTagSummaryResponse> tags = entry.getValue().stream()
                    .map(tagsById::get)
                    .filter(tag -> tag != null)
                    .map(CommunityPostTagSummaryResponse::from)
                    .toList();
            result.put(entry.getKey(), tags);
        }
        return result;
    }

    private Map<Long, Long> loadApprovedCommentCounts(List<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return Map.of();
        }

        return communityPostCommentRepository.countByPostIdsAndStatus(postIds, CommunityPostCommentStatus.APPROVED)
                .stream()
                .collect(java.util.stream.Collectors.toMap(
                        com.pxczxn.blog.community.interaction.repository.PostCountProjection::getPostId,
                        com.pxczxn.blog.community.interaction.repository.PostCountProjection::getCount
                ));
    }

    private CommunityPostStatus resolveWritableStatus(CommunityPostStatus requestedStatus) {
        if (requestedStatus == null || requestedStatus == CommunityPostStatus.DRAFT) {
            return CommunityPostStatus.DRAFT;
        }
        if (requestedStatus == CommunityPostStatus.PUBLISHED || requestedStatus == CommunityPostStatus.PENDING_REVIEW) {
            return CommunityPostStatus.PENDING_REVIEW;
        }
        throw new IllegalArgumentException("Unsupported post status");
    }

    private CommunityPostStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return CommunityPostStatus.valueOf(status.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid post status");
        }
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String generateUniqueSlug(String title) {
        String slugBase = slugify(title);
        String epochSeconds = String.valueOf(Instant.now().getEpochSecond());
        String prefix = trimSlug(epochSeconds + "-" + slugBase, MAX_SLUG_LENGTH);
        String candidate = prefix;
        int retry = 0;
        while (communityPostRepository.existsBySlug(candidate)) {
            retry++;
            if (retry > MAX_SLUG_RETRY) {
                throw new IllegalArgumentException("Failed to generate unique slug");
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
        return compact.isBlank() ? "post" : compact;
    }

    private String trimSlug(String slug, int maxLength) {
        if (slug.length() <= maxLength) {
            return slug;
        }
        return slug.substring(0, maxLength).replaceAll("-$", "");
    }

    private String randomSuffix(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(RANDOM_CHARS[secureRandom.nextInt(RANDOM_CHARS.length)]);
        }
        return sb.toString();
    }

    private PostInteractionResponse emptyInteraction(Long postId) {
        return PostInteractionResponse.builder()
                .postId(postId)
                .likeCount(0)
                .favoriteCount(0)
                .likedByMe(false)
                .favoritedByMe(false)
                .build();
    }
}

