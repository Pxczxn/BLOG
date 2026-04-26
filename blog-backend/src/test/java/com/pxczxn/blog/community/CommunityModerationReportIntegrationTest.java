


package com.pxczxn.blog.community;

import com.pxczxn.blog.community.dto.CommunityAuthResponse;
import com.pxczxn.blog.community.dto.CommunityRegisterRequest;
import com.pxczxn.blog.community.moderation.dto.AdminReportHandleRequest;
import com.pxczxn.blog.community.moderation.dto.CommunityReportCreateRequest;
import com.pxczxn.blog.community.moderation.entity.ModerationContentType;
import com.pxczxn.blog.community.moderation.entity.ModerationTaskStatus;
import com.pxczxn.blog.community.moderation.entity.ReportHandleAction;
import com.pxczxn.blog.community.moderation.entity.ReportReason;
import com.pxczxn.blog.community.moderation.entity.ReportStatus;
import com.pxczxn.blog.community.moderation.repository.ModerationTaskRepository;
import com.pxczxn.blog.community.moderation.service.ModerationService;
import com.pxczxn.blog.community.node.dto.CommunityNodeRequest;
import com.pxczxn.blog.community.node.service.CommunityNodeService;
import com.pxczxn.blog.community.service.CommunityAuthService;
import com.pxczxn.blog.community.post.comment.dto.CommunityPostCommentCreateRequest;
import com.pxczxn.blog.community.post.comment.entity.CommunityPostCommentStatus;
import com.pxczxn.blog.community.post.comment.repository.CommunityPostCommentRepository;
import com.pxczxn.blog.community.post.comment.service.CommunityPostCommentService;
import com.pxczxn.blog.community.post.dto.AdminCommunityPostStatusRequest;
import com.pxczxn.blog.community.post.dto.CommunityPostWriteRequest;
import com.pxczxn.blog.community.post.entity.CommunityPostStatus;
import com.pxczxn.blog.community.post.service.CommunityPostService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class CommunityModerationReportIntegrationTest {

    @Autowired
    private CommunityAuthService communityAuthService;

    @Autowired
    private CommunityNodeService communityNodeService;

    @Autowired
    private CommunityPostService communityPostService;

    @Autowired
    private CommunityPostCommentService communityPostCommentService;

    @Autowired
    private ModerationService moderationService;

    @Autowired
    private CommunityPostCommentRepository communityPostCommentRepository;

    @Autowired
    private ModerationTaskRepository moderationTaskRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private HttpServletRequest mockRequest;

    @BeforeEach
    void setUp() {
        resetDatabase();
        mockRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        Mockito.when(mockRequest.getHeader("User-Agent")).thenReturn("JUnit");
    }

    @Test
    void resolvingPostCommentReportRejectsCommentAndClosesPendingTask() {
        var author = registerUser("report-author", "report-author@example.com");
        var commenter = registerUser("report-commenter", "report-commenter@example.com");

        CommunityNodeRequest nodeRequest = new CommunityNodeRequest();
        nodeRequest.setName("Moderation");
        nodeRequest.setSlug("moderation");
        nodeRequest.setDescription("Moderation test node");
        var node = communityNodeService.create(nodeRequest);

        CommunityPostWriteRequest postRequest = new CommunityPostWriteRequest();
        postRequest.setNodeId(node.getId());
        postRequest.setTitle("Moderation report target post");
        postRequest.setSummary("summary");
        postRequest.setContent("content");
        postRequest.setStatus(CommunityPostStatus.PUBLISHED);
        var editor = communityPostService.create(author.getUserId(), postRequest);

        AdminCommunityPostStatusRequest publishRequest = new AdminCommunityPostStatusRequest();
        publishRequest.setStatus(CommunityPostStatus.PUBLISHED);
        communityPostService.updateStatus(editor.getId(), publishRequest);

        CommunityPostCommentCreateRequest commentRequest = new CommunityPostCommentCreateRequest();
        commentRequest.setContent("This is a normal test comment.");
        var createdComment = communityPostCommentService.create(commenter.getUserId(), editor.getId(), commentRequest);
        Long commentId = createdComment.id();

        assertEquals("PENDING", createdComment.status());
        assertTrue(moderationTaskRepository.findFirstByContentTypeAndContentIdAndStatusOrderByCreatedAtDesc(
                ModerationContentType.POST_COMMENT,
                commentId,
                ModerationTaskStatus.PENDING
        ).isPresent());

        CommunityReportCreateRequest createReportRequest = new CommunityReportCreateRequest();
        createReportRequest.setContentType(ModerationContentType.POST_COMMENT);
        createReportRequest.setContentId(commentId);
        createReportRequest.setReason(ReportReason.OTHER);
        createReportRequest.setDescription("Reported in integration test");
        var report = moderationService.createReport(author.getUserId(), createReportRequest);

        AdminReportHandleRequest handleRequest = new AdminReportHandleRequest();
        handleRequest.setStatus(ReportStatus.RESOLVED);
        handleRequest.setHandleAction(ReportHandleAction.REJECT_COMMENT);
        handleRequest.setHandleNote("Reject through report flow");
        var handled = moderationService.handleReport(report.getId(), handleRequest, null);

        assertEquals("RESOLVED", handled.getStatus());
        assertEquals("REJECT_COMMENT", handled.getHandleAction());
        assertEquals(CommunityPostCommentStatus.REJECTED, communityPostCommentRepository.findById(commentId).orElseThrow().getStatus());
        assertTrue(moderationTaskRepository.findFirstByContentTypeAndContentIdAndStatusOrderByCreatedAtDesc(
                ModerationContentType.POST_COMMENT,
                commentId,
                ModerationTaskStatus.PENDING
        ).isEmpty());
        assertTrue(moderationTaskRepository.findFirstByContentTypeAndContentIdAndStatusOrderByCreatedAtDesc(
                ModerationContentType.POST_COMMENT,
                commentId,
                ModerationTaskStatus.REJECTED
        ).isPresent());
    }

    private void resetDatabase() {
        deleteIfExists("DELETE FROM moderation_rule_hit");
        deleteIfExists("DELETE FROM moderation_task");
        deleteIfExists("DELETE FROM content_report");
        deleteIfExists("DELETE FROM community_notification");
        deleteIfExists("DELETE FROM community_post_comment");
        deleteIfExists("DELETE FROM community_post_like");
        deleteIfExists("DELETE FROM community_post_favorite");
        deleteIfExists("DELETE FROM community_user_follow");
        deleteIfExists("DELETE FROM community_post");
        deleteIfExists("DELETE FROM community_token_revoked");
        deleteIfExists("DELETE FROM comment");
        deleteIfExists("DELETE FROM article_tag");
        deleteIfExists("DELETE FROM article");
        deleteIfExists("DELETE FROM token_revoked");
        deleteIfExists("DELETE FROM device_session");
        deleteIfExists("DELETE FROM community_user");
        deleteIfExists("DELETE FROM community_node WHERE slug = 'moderation'");
    }

    private void deleteIfExists(String sql) {
        try {
            jdbcTemplate.update(sql);
        } catch (DataAccessException ignored) {
        }
    }

    private CommunityAuthResponse registerUser(String username, String email) {
        CommunityRegisterRequest request = new CommunityRegisterRequest();
        request.setUsername(username);
        request.setDisplayName(username);
        request.setEmail(email);
        request.setPassword("password123");
        return communityAuthService.register(request, mockRequest);
    }
}

