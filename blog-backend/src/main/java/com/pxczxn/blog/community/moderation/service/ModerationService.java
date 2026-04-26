/**
 * 内容审核服务
 * <p>
 * 处理用户提交内容的自动审核和人工审核流程，包括评论审核、帖子审核、
 * 违规检测、举报处理等。支持关键词过滤和 AI 辅助审核。
 */
package com.pxczxn.blog.community.moderation.service;

import com.pxczxn.blog.comment.entity.Comment;
import com.pxczxn.blog.comment.entity.CommentStatus;
import com.pxczxn.blog.comment.exception.CommentNotFoundException;
import com.pxczxn.blog.comment.repository.CommentRepository;
import com.pxczxn.blog.common.response.PageResponse;
import com.pxczxn.blog.community.entity.CommunityUser;
import com.pxczxn.blog.community.exception.CommunityUserNotFoundException;
import com.pxczxn.blog.community.interaction.service.CommunityInteractionService;
import com.pxczxn.blog.community.moderation.dto.AdminModerationDecisionRequest;
import com.pxczxn.blog.community.moderation.dto.AdminModerationTaskDetailResponse;
import com.pxczxn.blog.community.moderation.dto.AdminModerationTaskItemResponse;
import com.pxczxn.blog.community.moderation.dto.AdminReportHandleRequest;
import com.pxczxn.blog.community.moderation.dto.AdminReportItemResponse;
import com.pxczxn.blog.community.moderation.dto.CommunityReportCreateRequest;
import com.pxczxn.blog.community.moderation.dto.CommunityReportResponse;
import com.pxczxn.blog.community.moderation.dto.ModerationRuleHitResponse;
import com.pxczxn.blog.community.moderation.entity.ContentReport;
import com.pxczxn.blog.community.moderation.entity.ModerationContentType;
import com.pxczxn.blog.community.moderation.entity.ModerationKeywordRule;
import com.pxczxn.blog.community.moderation.entity.ModerationRiskLevel;
import com.pxczxn.blog.community.moderation.entity.ModerationRuleHit;
import com.pxczxn.blog.community.moderation.entity.ModerationRuleSeverity;
import com.pxczxn.blog.community.moderation.entity.ModerationTask;
import com.pxczxn.blog.community.moderation.entity.ModerationTaskStatus;
import com.pxczxn.blog.community.moderation.entity.ReportHandleAction;
import com.pxczxn.blog.community.moderation.entity.ReportStatus;
import com.pxczxn.blog.community.moderation.exception.ContentReportNotFoundException;
import com.pxczxn.blog.community.moderation.exception.ModerationTaskNotFoundException;
import com.pxczxn.blog.community.moderation.repository.ContentReportRepository;
import com.pxczxn.blog.community.moderation.repository.ModerationKeywordRuleRepository;
import com.pxczxn.blog.community.moderation.repository.ModerationRuleHitRepository;
import com.pxczxn.blog.community.moderation.repository.ModerationTaskRepository;
import com.pxczxn.blog.community.moderation.service.ai.ModerationAiAdvice;
import com.pxczxn.blog.community.moderation.service.ai.ModerationAiAdvisor;
import com.pxczxn.blog.community.post.entity.CommunityPost;
import com.pxczxn.blog.community.post.entity.CommunityPostStatus;
import com.pxczxn.blog.community.post.exception.CommunityPostNotFoundException;
import com.pxczxn.blog.community.post.comment.entity.CommunityPostComment;
import com.pxczxn.blog.community.post.comment.entity.CommunityPostCommentStatus;
import com.pxczxn.blog.community.post.comment.repository.CommunityPostCommentRepository;
import com.pxczxn.blog.community.post.repository.CommunityPostRepository;
import com.pxczxn.blog.community.repository.CommunityUserRepository;
import com.pxczxn.blog.user.entity.AdminUser;
import com.pxczxn.blog.user.repository.AdminUserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ModerationService {

    private static final String AUTO_REJECT_REASON = "Rejected by keyword rules";

    private final ModerationTaskRepository moderationTaskRepository;
    private final ModerationRuleHitRepository moderationRuleHitRepository;
    private final ModerationKeywordRuleRepository moderationKeywordRuleRepository;
    private final ContentReportRepository contentReportRepository;
    private final CommunityPostRepository communityPostRepository;
    private final CommunityPostCommentRepository communityPostCommentRepository;
    private final CommentRepository commentRepository;
    private final CommunityUserRepository communityUserRepository;
    private final AdminUserRepository adminUserRepository;
    private final ModerationAiAdvisor moderationAiAdvisor;
    private final CommunityInteractionService communityInteractionService;

    /**
     * 提交社区帖子进行审核
     *
     * @param post       待审核的帖子
     * @param submittedBy 提交者用户 ID
     */
    @Transactional
    public void submitPostForReview(CommunityPost post, Long submittedBy) {
        if (post == null || post.getId() == null) {
            throw new IllegalArgumentException("Post id is required for moderation submission");
        }

        ModerationTask task = upsertPendingTask(
                ModerationContentType.POST,
                post.getId(),
                submittedBy,
                trimToLength(trimToNull(post.getTitle()), 220)
        );
        boolean blockedByRule = scanAndStoreRuleHits(task, buildPostScanText(post));

        if (blockedByRule) {
            task.setStatus(ModerationTaskStatus.REJECTED);
            task.setDecisionNote(AUTO_REJECT_REASON);
            task.setReviewedAt(LocalDateTime.now());
            task.setReviewedBy(null);

            post.setStatus(CommunityPostStatus.REJECTED);
            post.setPublishedAt(null);
            post.setRejectionReason(AUTO_REJECT_REASON);
        } else {
            task.setStatus(ModerationTaskStatus.PENDING);
            task.setDecisionNote(null);
            task.setReviewedAt(null);
            task.setReviewedBy(null);

            post.setStatus(CommunityPostStatus.PENDING_REVIEW);
            post.setPublishedAt(null);
            post.setRejectionReason(null);
        }

        moderationTaskRepository.save(task);
        communityPostRepository.save(post);
    }

    /**
     * 提交文章评论进行审核
     *
     * @param comment    待审核的评论
     * @param submittedBy 提交者用户 ID
     */
    @Transactional
    public void submitCommentForReview(Comment comment, Long submittedBy) {
        if (comment == null || comment.getId() == null) {
            throw new IllegalArgumentException("Comment id is required for moderation submission");
        }

        ModerationTask task = upsertPendingTask(
                ModerationContentType.COMMENT,
                comment.getId(),
                submittedBy,
                trimToLength(trimToNull(comment.getContent()), 220)
        );
        boolean blockedByRule = scanAndStoreRuleHits(task, trimToNull(comment.getContent()));

        if (blockedByRule) {
            task.setStatus(ModerationTaskStatus.REJECTED);
            task.setDecisionNote(AUTO_REJECT_REASON);
            task.setReviewedAt(LocalDateTime.now());
            task.setReviewedBy(null);
            comment.setStatus(CommentStatus.REJECTED);
        } else {
            task.setStatus(ModerationTaskStatus.PENDING);
            task.setDecisionNote(null);
            task.setReviewedAt(null);
            task.setReviewedBy(null);
            comment.setStatus(CommentStatus.PENDING);
        }

        moderationTaskRepository.save(task);
        commentRepository.save(comment);
    }

    /**
     * 提交社区帖子评论进行审核
     *
     * @param comment    待审核的评论
     * @param submittedBy 提交者用户 ID
     */
    @Transactional
    public void submitPostCommentForReview(CommunityPostComment comment, Long submittedBy) {
        if (comment == null || comment.getId() == null) {
            throw new IllegalArgumentException("Post comment id is required for moderation submission");
        }

        ModerationTask task = upsertPendingTask(
                ModerationContentType.POST_COMMENT,
                comment.getId(),
                submittedBy,
                trimToLength(trimToNull(comment.getContent()), 220)
        );
        boolean blockedByRule = scanAndStoreRuleHits(task, trimToNull(comment.getContent()));

        if (blockedByRule) {
            task.setStatus(ModerationTaskStatus.REJECTED);
            task.setDecisionNote(AUTO_REJECT_REASON);
            task.setReviewedAt(LocalDateTime.now());
            task.setReviewedBy(null);
            comment.setStatus(CommunityPostCommentStatus.REJECTED);
        } else {
            task.setStatus(ModerationTaskStatus.PENDING);
            task.setDecisionNote(null);
            task.setReviewedAt(null);
            task.setReviewedBy(null);
            comment.setStatus(CommunityPostCommentStatus.PENDING);
        }

        moderationTaskRepository.save(task);
        communityPostCommentRepository.save(comment);
    }

    /**
     * 帖子管理操作后同步审核任务状态
     *
     * @param postId    帖子 ID
     * @param postStatus 帖子状态
     * @param reviewerId 审核人 ID
     * @param note      审核备注
     */
    @Transactional
    public void syncPostTaskAfterAdminAction(Long postId, CommunityPostStatus postStatus, Long reviewerId, String note) {
        if (postId == null || postStatus == null) {
            return;
        }
        moderationTaskRepository.findFirstByContentTypeAndContentIdAndStatusOrderByCreatedAtDesc(
                ModerationContentType.POST,
                postId,
                ModerationTaskStatus.PENDING
        ).ifPresent(task -> {
            if (postStatus == CommunityPostStatus.PUBLISHED) {
                task.setStatus(ModerationTaskStatus.APPROVED);
            } else if (postStatus == CommunityPostStatus.REJECTED || postStatus == CommunityPostStatus.HIDDEN) {
                task.setStatus(ModerationTaskStatus.REJECTED);
            } else {
                task.setStatus(ModerationTaskStatus.CANCELED);
            }
            task.setDecisionNote(trimToLength(trimToNull(note), 500));
            task.setReviewedBy(reviewerId);
            task.setReviewedAt(LocalDateTime.now());
            moderationTaskRepository.save(task);
        });
    }

    /**
     * 文章评论管理操作后同步审核任务状态
     *
     * @param commentId    评论 ID
     * @param commentStatus 评论状态
     * @param reviewerId   审核人 ID
     * @param note         审核备注
     */
    @Transactional
    public void syncCommentTaskAfterAdminAction(Long commentId, CommentStatus commentStatus, Long reviewerId, String note) {
        if (commentId == null || commentStatus == null) {
            return;
        }
        moderationTaskRepository.findFirstByContentTypeAndContentIdAndStatusOrderByCreatedAtDesc(
                ModerationContentType.COMMENT,
                commentId,
                ModerationTaskStatus.PENDING
        ).ifPresent(task -> {
            if (commentStatus == CommentStatus.APPROVED) {
                task.setStatus(ModerationTaskStatus.APPROVED);
            } else if (commentStatus == CommentStatus.REJECTED) {
                task.setStatus(ModerationTaskStatus.REJECTED);
            } else {
                task.setStatus(ModerationTaskStatus.CANCELED);
            }
            task.setDecisionNote(trimToLength(trimToNull(note), 500));
            task.setReviewedBy(reviewerId);
            task.setReviewedAt(LocalDateTime.now());
            moderationTaskRepository.save(task);
        });
    }

    /**
     * 社区帖子评论管理操作后同步审核任务状态
     *
     * @param commentId    评论 ID
     * @param commentStatus 评论状态
     * @param reviewerId   审核人 ID
     * @param note         审核备注
     */
    @Transactional
    public void syncPostCommentTaskAfterAdminAction(Long commentId,
                                                    CommunityPostCommentStatus commentStatus,
                                                    Long reviewerId,
                                                    String note) {
        if (commentId == null || commentStatus == null) {
            return;
        }
        moderationTaskRepository.findFirstByContentTypeAndContentIdAndStatusOrderByCreatedAtDesc(
                ModerationContentType.POST_COMMENT,
                commentId,
                ModerationTaskStatus.PENDING
        ).ifPresent(task -> {
            if (commentStatus == CommunityPostCommentStatus.APPROVED) {
                task.setStatus(ModerationTaskStatus.APPROVED);
            } else if (commentStatus == CommunityPostCommentStatus.REJECTED) {
                task.setStatus(ModerationTaskStatus.REJECTED);
            } else {
                task.setStatus(ModerationTaskStatus.CANCELED);
            }
            task.setDecisionNote(trimToLength(trimToNull(note), 500));
            task.setReviewedBy(reviewerId);
            task.setReviewedAt(LocalDateTime.now());
            moderationTaskRepository.save(task);
        });
    }

    /**
     * 分页查询审核任务列表
     *
     * @param pageable    分页参数
     * @param page        页码
     * @param status      任务状态筛选
     * @param contentType 内容类型筛选
     * @return 审核任务分页结果
     */
    @Transactional(readOnly = true)
    public PageResponse<AdminModerationTaskItemResponse> listTasks(Pageable pageable, int page, String status, String contentType) {
        ModerationTaskStatus statusFilter = parseTaskStatus(status);
        ModerationContentType contentTypeFilter = parseContentType(contentType);
        Specification<ModerationTask> spec = buildTaskSpecification(statusFilter, contentTypeFilter);

        Page<ModerationTask> result = moderationTaskRepository.findAll(spec, pageable);
        Map<Long, String> submitterNames = loadCommunityUserNames(result.getContent().stream().map(ModerationTask::getSubmittedBy).toList());
        Map<Long, String> reviewerNames = loadAdminNames(result.getContent().stream().map(ModerationTask::getReviewedBy).toList());
        List<AdminModerationTaskItemResponse> items = result.getContent().stream()
                .map(task -> AdminModerationTaskItemResponse.from(
                        task,
                        submitterNames.get(task.getSubmittedBy()),
                        reviewerNames.get(task.getReviewedBy())
                ))
                .toList();
        return new PageResponse<>(items, result.getTotalElements(), page, result.getSize());
    }

    /**
     * 获取审核任务详情
     *
     * @param id 任务 ID
     * @return 任务详情
     */
    @Transactional(readOnly = true)
    public AdminModerationTaskDetailResponse getTask(Long id) {
        ModerationTask task = moderationTaskRepository.findById(id)
                .orElseThrow(() -> new ModerationTaskNotFoundException(id));
        return toTaskDetail(task);
    }

    /**
     * 对审核任务做出决定
     *
     * @param id      任务 ID
     * @param request 审核决定请求
     * @param adminId 管理员 ID
     * @return 更新后的任务详情
     */
    @Transactional
    public AdminModerationTaskDetailResponse decideTask(Long id, AdminModerationDecisionRequest request, Long adminId) {
        ModerationTask task = moderationTaskRepository.findById(id)
                .orElseThrow(() -> new ModerationTaskNotFoundException(id));
        if (task.getStatus() != ModerationTaskStatus.PENDING) {
            throw new IllegalArgumentException("Only pending moderation tasks can be decided");
        }
        if (request.getDecision() != ModerationTaskStatus.APPROVED
                && request.getDecision() != ModerationTaskStatus.REJECTED) {
            throw new IllegalArgumentException("Decision must be APPROVED or REJECTED");
        }

        String decisionNote = trimToLength(trimToNull(request.getDecisionNote()), 500);
        task.setStatus(request.getDecision());
        task.setDecisionNote(decisionNote);
        task.setReviewedBy(adminId);
        task.setReviewedAt(LocalDateTime.now());
        applyTaskDecisionToContent(task, request.getDecision(), decisionNote);
        moderationTaskRepository.save(task);

        return toTaskDetail(task);
    }

    /**
     * 创建举报
     *
     * @param reporterUserId 举报者用户 ID
     * @param request        举报请求
     * @return 创建的举报信息
     */
    @Transactional
    public CommunityReportResponse createReport(Long reporterUserId, CommunityReportCreateRequest request) {
        validateReporterExists(reporterUserId);
        validateReportTargetExists(request.getContentType(), request.getContentId());
        if (contentReportRepository.existsByReporterUserIdAndContentTypeAndContentIdAndStatus(
                reporterUserId,
                request.getContentType(),
                request.getContentId(),
                ReportStatus.OPEN
        )) {
            throw new IllegalArgumentException("You already reported this content");
        }

        ContentReport report = ContentReport.builder()
                .contentType(request.getContentType())
                .contentId(request.getContentId())
                .reporterUserId(reporterUserId)
                .reason(request.getReason())
                .description(trimToLength(trimToNull(request.getDescription()), 500))
                .status(ReportStatus.OPEN)
                .handleAction(ReportHandleAction.NONE)
                .build();
        return CommunityReportResponse.from(contentReportRepository.save(report));
    }

    /**
     * 分页查询举报列表
     *
     * @param pageable    分页参数
     * @param page        页码
     * @param status      举报状态筛选
     * @param contentType 内容类型筛选
     * @return 举报分页结果
     */
    @Transactional(readOnly = true)
    public PageResponse<AdminReportItemResponse> listReports(Pageable pageable, int page, String status, String contentType) {
        ReportStatus statusFilter = parseReportStatus(status);
        ModerationContentType contentTypeFilter = parseContentType(contentType);
        Specification<ContentReport> spec = buildReportSpecification(statusFilter, contentTypeFilter);

        Page<ContentReport> result = contentReportRepository.findAll(spec, pageable);
        Map<Long, String> reporterNames = loadCommunityUserNames(result.getContent().stream().map(ContentReport::getReporterUserId).toList());
        Map<Long, String> handlerNames = loadAdminNames(result.getContent().stream().map(ContentReport::getHandledBy).toList());
        List<AdminReportItemResponse> items = result.getContent().stream()
                .map(report -> AdminReportItemResponse.from(
                        report,
                        reporterNames.get(report.getReporterUserId()),
                        handlerNames.get(report.getHandledBy())
                ))
                .toList();
        return new PageResponse<>(items, result.getTotalElements(), page, result.getSize());
    }

    /**
     * 处理举报
     *
     * @param id       举报 ID
     * @param request  处理请求
     * @param adminId  管理员 ID
     * @return 更新后的举报信息
     */
    @Transactional
    public AdminReportItemResponse handleReport(Long id, AdminReportHandleRequest request, Long adminId) {
        ContentReport report = contentReportRepository.findById(id)
                .orElseThrow(() -> new ContentReportNotFoundException(id));

        if (request.getStatus() == ReportStatus.OPEN) {
            throw new IllegalArgumentException("Handled report status cannot stay OPEN");
        }

        ReportHandleAction action = request.getHandleAction() == null ? ReportHandleAction.NONE : request.getHandleAction();
        if (request.getStatus() == ReportStatus.DISMISSED) {
            action = ReportHandleAction.NONE;
        } else {
            applyReportAction(
                    report.getContentType(),
                    report.getContentId(),
                    action,
                    adminId,
                    request.getHandleNote()
            );
        }

        report.setStatus(request.getStatus());
        report.setHandleAction(action);
        report.setHandleNote(trimToLength(trimToNull(request.getHandleNote()), 500));
        report.setHandledBy(adminId);
        report.setHandledAt(LocalDateTime.now());
        ContentReport saved = contentReportRepository.save(report);

        String reporter = loadCommunityUserNames(Collections.singletonList(saved.getReporterUserId())).get(saved.getReporterUserId());
        String handler = loadAdminNames(Collections.singletonList(saved.getHandledBy())).get(saved.getHandledBy());
        return AdminReportItemResponse.from(saved, reporter, handler);
    }

    /**
     * 构建审核任务查询条件
     */
    private Specification<ModerationTask> buildTaskSpecification(ModerationTaskStatus status, ModerationContentType contentType) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (contentType != null) {
                predicates.add(cb.equal(root.get("contentType"), contentType));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * 构建举报查询条件
     */
    private Specification<ContentReport> buildReportSpecification(ReportStatus status, ModerationContentType contentType) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (contentType != null) {
                predicates.add(cb.equal(root.get("contentType"), contentType));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * 转换审核任务为详情响应
     */
    private AdminModerationTaskDetailResponse toTaskDetail(ModerationTask task) {
        String submittedBy = loadCommunityUserNames(Collections.singletonList(task.getSubmittedBy())).get(task.getSubmittedBy());
        String reviewedBy = loadAdminNames(Collections.singletonList(task.getReviewedBy())).get(task.getReviewedBy());
        List<ModerationRuleHitResponse> hits = moderationRuleHitRepository.findAllByTaskIdOrderByIdAsc(task.getId()).stream()
                .map(ModerationRuleHitResponse::from)
                .toList();

        return AdminModerationTaskDetailResponse.builder()
                .id(task.getId())
                .contentType(task.getContentType().name())
                .contentId(task.getContentId())
                .titleSnapshot(task.getTitleSnapshot())
                .status(task.getStatus().name())
                .riskLevel(task.getRiskLevel().name())
                .hitCount(task.getHitCount())
                .submittedBy(submittedBy)
                .reviewedBy(reviewedBy)
                .decisionNote(task.getDecisionNote())
                .submittedAt(task.getSubmittedAt())
                .reviewedAt(task.getReviewedAt())
                .hits(hits)
                .build();
    }

    /**
     * 将审核决定应用到实际内容
     */
    private void applyTaskDecisionToContent(ModerationTask task, ModerationTaskStatus decision, String decisionNote) {
        if (task.getContentType() == ModerationContentType.POST) {
            CommunityPost post = communityPostRepository.findById(task.getContentId())
                    .orElseThrow(() -> new CommunityPostNotFoundException(task.getContentId()));
            if (decision == ModerationTaskStatus.APPROVED) {
                post.setStatus(CommunityPostStatus.PUBLISHED);
                post.setRejectionReason(null);
                if (post.getPublishedAt() == null) {
                    post.setPublishedAt(LocalDateTime.now());
                }
            } else {
                post.setStatus(CommunityPostStatus.REJECTED);
                post.setPublishedAt(null);
                post.setRejectionReason(decisionNote == null ? "Rejected by moderator" : decisionNote);
            }
            communityPostRepository.save(post);
            return;
        }

        if (task.getContentType() == ModerationContentType.COMMENT) {
            Comment comment = commentRepository.findById(task.getContentId())
                    .orElseThrow(() -> new CommentNotFoundException(task.getContentId()));
            CommentStatus previousStatus = comment.getStatus();
            comment.setStatus(decision == ModerationTaskStatus.APPROVED ? CommentStatus.APPROVED : CommentStatus.REJECTED);
            commentRepository.save(comment);
            if (previousStatus != CommentStatus.APPROVED && decision == ModerationTaskStatus.APPROVED) {
                notifyApprovedArticleComment(comment.getId());
            }
            return;
        }

        if (task.getContentType() == ModerationContentType.POST_COMMENT) {
            CommunityPostComment comment = communityPostCommentRepository.findById(task.getContentId())
                    .orElseThrow(() -> new IllegalArgumentException("Post comment not found"));
            CommunityPostCommentStatus previousStatus = comment.getStatus();
            comment.setStatus(decision == ModerationTaskStatus.APPROVED
                    ? CommunityPostCommentStatus.APPROVED
                    : CommunityPostCommentStatus.REJECTED);
            communityPostCommentRepository.save(comment);
            if (previousStatus != CommunityPostCommentStatus.APPROVED && decision == ModerationTaskStatus.APPROVED) {
                notifyApprovedPostComment(comment.getId());
            }
            return;
        }

        throw new IllegalArgumentException("Unsupported moderation content type");
    }

    /**
     * 文章评论审核通过后发送通知
     */
    @Transactional
    public void notifyApprovedArticleComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
        if (comment.getStatus() != CommentStatus.APPROVED || comment.getCommunityUserId() == null || comment.getParentId() == null) {
            return;
        }

        Comment parent = commentRepository.findById(comment.getParentId()).orElse(null);
        if (parent == null || parent.getCommunityUserId() == null) {
            return;
        }
        if (Objects.equals(parent.getCommunityUserId(), comment.getCommunityUserId())) {
            return;
        }

        CommunityUser actor = communityUserRepository.findById(comment.getCommunityUserId()).orElse(null);
        if (actor == null) {
            return;
        }

        communityInteractionService.notifyCommentReplied(
                parent.getCommunityUserId(),
                actor.getId(),
                defaultCommunityDisplayName(actor),
                comment.getId(),
                comment.getContent()
        );
    }

    /**
     * 社区帖子评论审核通过后发送通知
     */
    @Transactional
    public void notifyApprovedPostComment(Long commentId) {
        CommunityPostComment comment = communityPostCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Post comment not found"));
        if (comment.getStatus() != CommunityPostCommentStatus.APPROVED) {
            return;
        }

        CommunityUser actor = communityUserRepository.findById(comment.getCommunityUserId()).orElse(null);
        if (actor == null) {
            return;
        }

        CommunityPost post = communityPostRepository.findById(comment.getPostId()).orElse(null);
        if (post == null) {
            return;
        }

        if (comment.getParentId() != null) {
            CommunityPostComment parent = communityPostCommentRepository.findById(comment.getParentId()).orElse(null);
            if (parent != null
                    && parent.getCommunityUserId() != null
                    && !Objects.equals(parent.getCommunityUserId(), actor.getId())) {
                communityInteractionService.notifyPostCommentReplied(
                        parent.getCommunityUserId(),
                        actor.getId(),
                        defaultCommunityDisplayName(actor),
                        post.getId(),
                        comment.getId(),
                        comment.getContent()
                );
                return;
            }
        }

        if (!Objects.equals(post.getAuthorId(), actor.getId())) {
            communityInteractionService.notifyPostCommented(
                    post.getAuthorId(),
                    actor.getId(),
                    defaultCommunityDisplayName(actor),
                    post.getId(),
                    comment.getId(),
                    comment.getContent()
            );
        }
    }

    /**
     * 应用举报处理动作
     */
    private void applyReportAction(ModerationContentType contentType,
                                   Long contentId,
                                   ReportHandleAction action,
                                   Long reviewerId,
                                   String decisionNote) {
        if (action == ReportHandleAction.NONE) {
            return;
        }
        if (action == ReportHandleAction.HIDE_POST) {
            if (contentType != ModerationContentType.POST) {
                throw new IllegalArgumentException("HIDE_POST action only supports post reports");
            }
            CommunityPost post = communityPostRepository.findById(contentId)
                    .orElseThrow(() -> new CommunityPostNotFoundException(contentId));
            post.setStatus(CommunityPostStatus.HIDDEN);
            post.setPublishedAt(null);
            communityPostRepository.save(post);
            syncPostTaskAfterAdminAction(post.getId(), post.getStatus(), reviewerId, decisionNote);
            return;
        }
        if (action == ReportHandleAction.REJECT_COMMENT) {
            if (contentType == ModerationContentType.COMMENT) {
                Comment comment = commentRepository.findById(contentId)
                        .orElseThrow(() -> new CommentNotFoundException(contentId));
                comment.setStatus(CommentStatus.REJECTED);
                commentRepository.save(comment);
                syncCommentTaskAfterAdminAction(comment.getId(), comment.getStatus(), reviewerId, decisionNote);
                return;
            }
            if (contentType == ModerationContentType.POST_COMMENT) {
                CommunityPostComment comment = communityPostCommentRepository.findById(contentId)
                        .orElseThrow(() -> new IllegalArgumentException("Post comment not found"));
                comment.setStatus(CommunityPostCommentStatus.REJECTED);
                communityPostCommentRepository.save(comment);
                syncPostCommentTaskAfterAdminAction(comment.getId(), comment.getStatus(), reviewerId, decisionNote);
                return;
            }
            throw new IllegalArgumentException("REJECT_COMMENT action only supports comment reports");
        }
        throw new IllegalArgumentException("Unsupported report handle action");
    }

    /**
     * 验证举报目标是否存在
     */
    private void validateReportTargetExists(ModerationContentType contentType, Long contentId) {
        if (contentType == ModerationContentType.POST) {
            if (!communityPostRepository.existsById(contentId)) {
                throw new CommunityPostNotFoundException(contentId);
            }
            return;
        }
        if (contentType == ModerationContentType.COMMENT) {
            if (!commentRepository.existsById(contentId)) {
                throw new CommentNotFoundException(contentId);
            }
            return;
        }
        if (contentType == ModerationContentType.POST_COMMENT) {
            if (!communityPostCommentRepository.existsById(contentId)) {
                throw new IllegalArgumentException("Post comment not found");
            }
            return;
        }
        throw new IllegalArgumentException("Unsupported report content type");
    }

    /**
     * 验证举报者是否存在
     */
    private void validateReporterExists(Long reporterUserId) {
        if (reporterUserId == null) {
            throw new IllegalArgumentException("reporter user id must not be null");
        }
        if (!communityUserRepository.existsById(reporterUserId)) {
            throw new CommunityUserNotFoundException(reporterUserId);
        }
    }

    /**
     * 创建或更新待审核任务
     */
    private ModerationTask upsertPendingTask(ModerationContentType contentType, Long contentId, Long submittedBy, String titleSnapshot) {
        ModerationTask task = moderationTaskRepository.findFirstByContentTypeAndContentIdAndStatusOrderByCreatedAtDesc(
                        contentType,
                        contentId,
                        ModerationTaskStatus.PENDING
                )
                .orElseGet(ModerationTask::new);

        task.setContentType(contentType);
        task.setContentId(contentId);
        task.setSubmittedBy(submittedBy);
        task.setTitleSnapshot(titleSnapshot);
        task.setStatus(ModerationTaskStatus.PENDING);
        task.setRiskLevel(ModerationRiskLevel.LOW);
        task.setHitCount(0);
        task.setDecisionNote(null);
        task.setReviewedBy(null);
        task.setReviewedAt(null);
        task.setSubmittedAt(LocalDateTime.now());

        ModerationTask savedTask = moderationTaskRepository.save(task);
        moderationRuleHitRepository.deleteByTaskId(savedTask.getId());
        return savedTask;
    }

    /**
     * 扫描内容并存储命中的规则
     *
     * @return 是否被规则阻断
     */
    private boolean scanAndStoreRuleHits(ModerationTask task, String sourceText) {
        String scanText = sourceText == null ? "" : sourceText;
        String normalizedText = scanText.toLowerCase(Locale.ROOT);
        List<ModerationKeywordRule> rules = moderationKeywordRuleRepository.findAllByEnabledTrue();

        List<ModerationRuleHit> hits = new ArrayList<>();
        ModerationRiskLevel riskLevel = ModerationRiskLevel.LOW;
        boolean blockedByRule = false;

        for (ModerationKeywordRule rule : rules) {
            String keyword = trimToNull(rule.getKeywordValue());
            if (keyword == null) {
                continue;
            }
            String keywordLower = keyword.toLowerCase(Locale.ROOT);
            int index = normalizedText.indexOf(keywordLower);
            if (index < 0) {
                continue;
            }

            hits.add(ModerationRuleHit.builder()
                    .taskId(task.getId())
                    .ruleId(rule.getId())
                    .keywordValue(keyword)
                    .snippet(extractSnippet(scanText, index, keyword.length()))
                    .severity(rule.getSeverity())
                    .build());

            riskLevel = maxRiskLevel(riskLevel, rule.getSeverity());
            if (rule.getSeverity() == ModerationRuleSeverity.BLOCK) {
                blockedByRule = true;
            }
        }

        if (!hits.isEmpty()) {
            moderationRuleHitRepository.saveAll(hits);
        }

            // AI 审核入口，后续可接入模型审核
        ModerationAiAdvice aiAdvice = moderationAiAdvisor.advise(task.getContentType(), scanText);
        if (aiAdvice.suggestedRisk() != null) {
            riskLevel = maxRiskLevel(riskLevel, aiAdvice.suggestedRisk());
        }
        if (aiAdvice.forceReject()) {
            blockedByRule = true;
        }

        task.setHitCount(hits.size());
        task.setRiskLevel(riskLevel);

        return blockedByRule;
    }

    /**
     * 构建帖子扫描文本
     */
    private String buildPostScanText(CommunityPost post) {
        StringBuilder builder = new StringBuilder();
        appendLine(builder, post.getTitle());
        appendLine(builder, post.getSummary());
        appendLine(builder, post.getContent());
        return builder.toString();
    }

    /**
     * 追加非空行到构建器
     */
    private void appendLine(StringBuilder builder, String value) {
        String trimmed = trimToNull(value);
        if (trimmed == null) {
            return;
        }
        if (builder.length() > 0) {
            builder.append('\n');
        }
        builder.append(trimmed);
    }

    /**
     * 提取命中关键词的上下文片段
     */
    private String extractSnippet(String text, int index, int keywordLength) {
        if (text == null || text.isBlank()) {
            return null;
        }
        int start = Math.max(index - 30, 0);
        int end = Math.min(index + keywordLength + 30, text.length());
        String snippet = text.substring(start, end).replaceAll("\\s+", " ").trim();
        return trimToLength(snippet, 255);
    }

    /**
     * 取最高风险级别（基于规则严重度）
     */
    private ModerationRiskLevel maxRiskLevel(ModerationRiskLevel current, ModerationRuleSeverity severity) {
        if (severity == ModerationRuleSeverity.BLOCK || severity == ModerationRuleSeverity.HIGH) {
            return ModerationRiskLevel.HIGH;
        }
        if (severity == ModerationRuleSeverity.MEDIUM && current == ModerationRiskLevel.LOW) {
            return ModerationRiskLevel.MEDIUM;
        }
        return current;
    }

    /**
     * 取最高风险级别（基于风险级别）
     */
    private ModerationRiskLevel maxRiskLevel(ModerationRiskLevel current, ModerationRiskLevel next) {
        if (current == ModerationRiskLevel.HIGH || next == ModerationRiskLevel.HIGH) {
            return ModerationRiskLevel.HIGH;
        }
        if (current == ModerationRiskLevel.MEDIUM || next == ModerationRiskLevel.MEDIUM) {
            return ModerationRiskLevel.MEDIUM;
        }
        return ModerationRiskLevel.LOW;
    }

    /**
     * 解析审核任务状态字符串
     */
    private ModerationTaskStatus parseTaskStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return ModerationTaskStatus.valueOf(status.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid moderation task status");
        }
    }

    /**
     * 解析举报状态字符串
     */
    private ReportStatus parseReportStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return ReportStatus.valueOf(status.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid report status");
        }
    }

    /**
     * 解析内容类型字符串
     */
    private ModerationContentType parseContentType(String type) {
        if (type == null || type.isBlank()) {
            return null;
        }
        try {
            return ModerationContentType.valueOf(type.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid moderation content type");
        }
    }

    /**
     * 批量加载社区用户显示名称
     */
    private Map<Long, String> loadCommunityUserNames(Collection<Long> userIds) {
        Map<Long, String> names = new HashMap<>();
        Set<Long> uniqueIds = userIds.stream().filter(Objects::nonNull).collect(java.util.stream.Collectors.toSet());
        if (uniqueIds.isEmpty()) {
            return names;
        }
        List<CommunityUser> users = communityUserRepository.findAllById(uniqueIds);
        users.forEach(user -> names.put(user.getId(), defaultCommunityDisplayName(user)));
        return names;
    }

    /**
     * 获取社区用户默认显示名称
     */
    private String defaultCommunityDisplayName(CommunityUser user) {
        String displayName = trimToNull(user.getDisplayName());
        if (displayName != null) {
            return displayName;
        }
        return user.getUsername();
    }

    /**
     * 批量加载管理员用户名
     */
    private Map<Long, String> loadAdminNames(Collection<Long> adminIds) {
        Map<Long, String> names = new HashMap<>();
        Set<Long> uniqueIds = adminIds.stream().filter(Objects::nonNull).collect(java.util.stream.Collectors.toSet());
        if (uniqueIds.isEmpty()) {
            return names;
        }
        List<AdminUser> admins = adminUserRepository.findAllById(uniqueIds);
        admins.forEach(admin -> names.put(admin.getId(), admin.getUsername()));
        return names;
    }

    /**
     * 去除空白字符并转换为 null
     */
    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    /**
     * 截断字符串到指定长度
     */
    private String trimToLength(String value, int maxLength) {
        String trimmed = trimToNull(value);
        if (trimmed == null) {
            return null;
        }
        if (trimmed.length() <= maxLength) {
            return trimmed;
        }
        return trimmed.substring(0, maxLength);
    }
}
