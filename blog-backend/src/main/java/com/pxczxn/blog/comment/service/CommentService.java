






package com.pxczxn.blog.comment.service;

import com.pxczxn.blog.comment.dto.AdminCommentItemResponse;
import com.pxczxn.blog.comment.dto.AdminCommentStatusResponse;
import com.pxczxn.blog.comment.dto.CommentItemResponse;
import com.pxczxn.blog.comment.dto.CreateCommentRequest;
import com.pxczxn.blog.comment.dto.CreateCommentResponse;
import com.pxczxn.blog.comment.entity.Comment;
import com.pxczxn.blog.comment.entity.CommentStatus;
import com.pxczxn.blog.comment.exception.CommentNotFoundException;
import com.pxczxn.blog.comment.exception.ParentCommentInvalidException;
import com.pxczxn.blog.comment.repository.CommentRepository;
import com.pxczxn.blog.common.response.PageResponse;
import com.pxczxn.blog.community.entity.CommunityUser;
import com.pxczxn.blog.community.exception.CommunityUserNotFoundException;
import com.pxczxn.blog.community.moderation.service.ModerationService;
import com.pxczxn.blog.community.repository.CommunityUserRepository;
import com.pxczxn.blog.content.exception.ArticleNotFoundException;
import com.pxczxn.blog.content.repository.ArticleRepository;
import com.pxczxn.blog.security.AuthenticatedUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final CommunityUserRepository communityUserRepository;
    private final ModerationService moderationService;

    









    @Transactional
    public CreateCommentResponse create(CreateCommentRequest request, AuthenticatedUserPrincipal currentUser) {
        Long articleId = request.getArticleId();
        if (!articleRepository.existsById(articleId)) {
            throw new ArticleNotFoundException(articleId);
        }

        Long parentId = request.getParentId();
        Comment parent = null;
        if (parentId != null) {
            parent = commentRepository.findById(parentId)
                    .orElseThrow(ParentCommentInvalidException::new);
            if (!parent.getArticleId().equals(articleId)) {
                throw new ParentCommentInvalidException();
            }
            if (parent.getStatus() != CommentStatus.APPROVED) {
                throw new ParentCommentInvalidException();
            }
        }

        CommunityUser communityUser = resolveCommunityUser(currentUser);
        String nickname = communityUser != null ? communityUser.getDisplayName() : trimToNull(request.getNickname());
        String email = communityUser != null ? communityUser.getEmail() : trimToNull(request.getEmail());
        if (nickname == null || email == null) {
            throw new IllegalArgumentException("Guest comments require nickname and email");
        }

        Comment comment = Comment.builder()
                .articleId(articleId)
                .parentId(parentId)
                .communityUserId(communityUser != null ? communityUser.getId() : null)
                .nickname(nickname)
                .email(email)
                .content(request.getContent().trim())
                .status(CommentStatus.PENDING)
                .build();

        Comment saved = commentRepository.save(comment);
        moderationService.submitCommentForReview(saved, communityUser != null ? communityUser.getId() : null);
        return CreateCommentResponse.from(saved);
    }

    





    @Transactional(readOnly = true)
    public List<CommentItemResponse> listApprovedByArticle(Long articleId) {
        List<Comment> comments = commentRepository.findByArticleIdAndStatusOrderByCreatedAtAsc(articleId, CommentStatus.APPROVED);
        Map<Long, Comment> commentMap = comments.stream()
                .collect(java.util.stream.Collectors.toMap(Comment::getId, item -> item));
        Map<Long, CommunityUser> users = loadCommunityUsers(comments);
        return comments.stream()
                .map(comment -> {
                    CommunityUser user = comment.getCommunityUserId() == null ? null : users.get(comment.getCommunityUserId());
                    Comment parent = comment.getParentId() == null ? null : commentMap.get(comment.getParentId());
                    return CommentItemResponse.from(
                            comment,
                            parent != null ? parent.getNickname() : null,
                            user != null ? user.getAvatar() : null,
                            user != null ? user.getUsername() : null,
                            user != null
                    );
                })
                .toList();
    }

    







    @Transactional(readOnly = true)
    public PageResponse<AdminCommentItemResponse> listByStatus(CommentStatus status, Pageable pageable, int page) {
        Page<Comment> result = commentRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        List<AdminCommentItemResponse> items = result.getContent().stream()
                .map(AdminCommentItemResponse::from)
                .toList();
        return new PageResponse<>(items, result.getTotalElements(), page, result.getSize());
    }

    







    @Transactional
    public AdminCommentStatusResponse approve(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));
        boolean shouldNotify = comment.getStatus() != CommentStatus.APPROVED;
        comment.setStatus(CommentStatus.APPROVED);
        Comment saved = commentRepository.save(comment);
        moderationService.syncCommentTaskAfterAdminAction(saved.getId(), saved.getStatus(), null, null);
        if (shouldNotify) {
            moderationService.notifyApprovedArticleComment(saved.getId());
        }
        return AdminCommentStatusResponse.from(saved);
    }

    







    @Transactional
    public AdminCommentStatusResponse reject(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));
        comment.setStatus(CommentStatus.REJECTED);
        Comment saved = commentRepository.save(comment);
        moderationService.syncCommentTaskAfterAdminAction(saved.getId(), saved.getStatus(), null, null);
        return AdminCommentStatusResponse.from(saved);
    }

    




    @Transactional
    public void delete(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));
        commentRepository.delete(comment);
    }

    





    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    





    private CommunityUser resolveCommunityUser(AuthenticatedUserPrincipal currentUser) {
        if (currentUser == null || !currentUser.isCommunityUser()) {
            return null;
        }
        return communityUserRepository.findById(currentUser.userId())
                .orElseThrow(() -> new CommunityUserNotFoundException(currentUser.userId()));
    }

    





    private Map<Long, CommunityUser> loadCommunityUsers(List<Comment> comments) {
        Set<Long> userIds = comments.stream()
                .map(Comment::getCommunityUserId)
                .filter(Objects::nonNull)
                .collect(java.util.stream.Collectors.toSet());
        Map<Long, CommunityUser> users = new HashMap<>();
        if (userIds.isEmpty()) {
            return users;
        }
        communityUserRepository.findAllById(userIds)
                .forEach(user -> users.put(user.getId(), user));
        return users;
    }
}

