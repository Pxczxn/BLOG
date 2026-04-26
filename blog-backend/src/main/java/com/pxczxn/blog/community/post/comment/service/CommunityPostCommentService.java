




package com.pxczxn.blog.community.post.comment.service;

import com.pxczxn.blog.community.entity.CommunityUser;
import com.pxczxn.blog.community.entity.CommunityUserStatus;
import com.pxczxn.blog.community.exception.CommunityAuthException;
import com.pxczxn.blog.community.exception.CommunityUserNotFoundException;
import com.pxczxn.blog.community.interaction.service.CommunityInteractionService;
import com.pxczxn.blog.community.moderation.service.ModerationService;
import com.pxczxn.blog.community.post.comment.dto.CommunityPostCommentCreateRequest;
import com.pxczxn.blog.community.post.comment.dto.CommunityPostCommentItemResponse;
import com.pxczxn.blog.community.post.comment.entity.CommunityPostComment;
import com.pxczxn.blog.community.post.comment.entity.CommunityPostCommentStatus;
import com.pxczxn.blog.community.post.comment.repository.CommunityPostCommentRepository;
import com.pxczxn.blog.community.post.entity.CommunityPost;
import com.pxczxn.blog.community.post.entity.CommunityPostStatus;
import com.pxczxn.blog.community.post.exception.CommunityPostNotFoundException;
import com.pxczxn.blog.community.post.repository.CommunityPostRepository;
import com.pxczxn.blog.community.repository.CommunityUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CommunityPostCommentService {

    private final CommunityPostRepository communityPostRepository;
    private final CommunityPostCommentRepository communityPostCommentRepository;
    private final CommunityUserRepository communityUserRepository;
    private final CommunityInteractionService communityInteractionService;
    private final ModerationService moderationService;

    @Transactional(readOnly = true)
    public List<CommunityPostCommentItemResponse> listByPostSlug(String slug) {
        CommunityPost post = communityPostRepository.findBySlugAndStatus(slug, CommunityPostStatus.PUBLISHED)
                .orElseThrow(() -> new CommunityPostNotFoundException(slug));

        List<CommunityPostComment> comments = communityPostCommentRepository.findByPostIdAndStatusOrderByCreatedAtAsc(
                post.getId(),
                CommunityPostCommentStatus.APPROVED
        );
        Map<Long, CommunityPostComment> commentMap = new HashMap<>();
        comments.forEach(item -> commentMap.put(item.getId(), item));
        Map<Long, CommunityUser> users = loadUsers(comments.stream().map(CommunityPostComment::getCommunityUserId).toList());

        return comments.stream()
                .map(comment -> {
                    CommunityPostComment parent = comment.getParentId() == null ? null : commentMap.get(comment.getParentId());
                    CommunityUser user = users.get(comment.getCommunityUserId());
                    return CommunityPostCommentItemResponse.from(
                            comment,
                            parent != null ? parent.getNickname() : null,
                            user != null ? user.getAvatar() : null,
                            user != null ? user.getUsername() : null
                    );
                })
                .toList();
    }

    @Transactional
    public CommunityPostCommentItemResponse create(Long userId, Long postId, CommunityPostCommentCreateRequest request) {
        CommunityUser author = getActiveUser(userId);
        CommunityPost post = getPublishedPost(postId);

        CommunityPostComment parent = null;
        if (request.getParentId() != null) {
            parent = communityPostCommentRepository.findByIdAndStatus(request.getParentId(), CommunityPostCommentStatus.APPROVED)
                    .orElseThrow(() -> new IllegalArgumentException("Parent comment is invalid"));
            if (!Objects.equals(parent.getPostId(), post.getId())) {
                throw new IllegalArgumentException("Parent comment is invalid");
            }
        }

        CommunityPostComment comment = communityPostCommentRepository.save(
                CommunityPostComment.builder()
                        .postId(post.getId())
                        .parentId(parent != null ? parent.getId() : null)
                        .communityUserId(author.getId())
                        .nickname(resolveDisplayName(author))
                        .content(request.getContent().trim())
                        .status(CommunityPostCommentStatus.PENDING)
                        .build()
        );

        moderationService.submitPostCommentForReview(comment, author.getId());

        if (comment.getStatus() == CommunityPostCommentStatus.APPROVED) {
            if (parent != null && !Objects.equals(parent.getCommunityUserId(), author.getId())) {
                communityInteractionService.notifyPostCommentReplied(
                        parent.getCommunityUserId(),
                        author.getId(),
                        resolveDisplayName(author),
                        post.getId(),
                        comment.getId(),
                        comment.getContent()
                );
            } else if (!Objects.equals(post.getAuthorId(), author.getId())) {
                communityInteractionService.notifyPostCommented(
                        post.getAuthorId(),
                        author.getId(),
                        resolveDisplayName(author),
                        post.getId(),
                        comment.getId(),
                        comment.getContent()
                );
            }
        }

        return CommunityPostCommentItemResponse.from(
                comment,
                parent != null ? parent.getNickname() : null,
                author.getAvatar(),
                author.getUsername()
        );
    }

    private CommunityPost getPublishedPost(Long postId) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new CommunityPostNotFoundException(postId));
        if (post.getStatus() != CommunityPostStatus.PUBLISHED) {
            throw new CommunityPostNotFoundException(postId);
        }
        return post;
    }

    private CommunityUser getActiveUser(Long userId) {
        CommunityUser user = communityUserRepository.findById(userId)
                .orElseThrow(() -> new CommunityUserNotFoundException(userId));
        if (user.getStatus() != CommunityUserStatus.ACTIVE) {
            throw CommunityAuthException.accountDisabled();
        }
        return user;
    }

    private String resolveDisplayName(CommunityUser user) {
        String displayName = user.getDisplayName();
        if (displayName == null || displayName.isBlank()) {
            return user.getUsername();
        }
        return displayName.trim();
    }

    private Map<Long, CommunityUser> loadUsers(List<Long> userIds) {
        Map<Long, CommunityUser> users = new HashMap<>();
        Set<Long> ids = userIds.stream().filter(Objects::nonNull).collect(java.util.stream.Collectors.toSet());
        if (ids.isEmpty()) {
            return users;
        }
        communityUserRepository.findAllById(ids).forEach(user -> users.put(user.getId(), user));
        return users;
    }
}

