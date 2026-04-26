


package com.pxczxn.blog.community;

import com.pxczxn.blog.community.dto.CommunityRegisterRequest;
import com.pxczxn.blog.community.node.dto.CommunityNodeRequest;
import com.pxczxn.blog.community.node.service.CommunityNodeService;
import com.pxczxn.blog.community.post.dto.AdminCommunityPostStatusRequest;
import com.pxczxn.blog.community.post.dto.CommunityPostWriteRequest;
import com.pxczxn.blog.community.post.entity.CommunityPostStatus;
import com.pxczxn.blog.community.post.service.CommunityPostService;
import com.pxczxn.blog.community.service.CommunityAuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@ActiveProfiles("test")
class CommunityPostIntegrationTest {

    @Autowired
    private CommunityAuthService communityAuthService;

    @Autowired
    private CommunityNodeService communityNodeService;

    @Autowired
    private CommunityPostService communityPostService;

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
    void publishedCommunityPostAppearsInPublicList() {
        CommunityRegisterRequest registerRequest = new CommunityRegisterRequest();
        registerRequest.setUsername("post-user");
        registerRequest.setDisplayName("Post User");
        registerRequest.setEmail("post-user@example.com");
        registerRequest.setPassword("postPass123");

        var auth = communityAuthService.register(registerRequest, mockRequest);
        CommunityNodeRequest nodeRequest = new CommunityNodeRequest();
        nodeRequest.setName("General");
        nodeRequest.setSlug("general");
        nodeRequest.setDescription("General discussion");
        var node = communityNodeService.create(nodeRequest);

        CommunityPostWriteRequest request = new CommunityPostWriteRequest();
        request.setNodeId(node.getId());
        request.setTitle("My first community post");
        request.setSummary("A short summary.");
        request.setContent("Hello community!");
        request.setStatus(CommunityPostStatus.PUBLISHED);

        var editor = communityPostService.create(auth.getUserId(), request);
        AdminCommunityPostStatusRequest publishRequest = new AdminCommunityPostStatusRequest();
        publishRequest.setStatus(CommunityPostStatus.PUBLISHED);
        communityPostService.updateStatus(editor.getId(), publishRequest);
        var publicList = communityPostService.listPublished(
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "publishedAt", "createdAt")),
                1,
                node.getSlug(),
                auth.getUsername(),
                auth.getUserId()
        );

        assertEquals("PENDING_REVIEW", editor.getStatus());
        assertFalse(publicList.items().isEmpty());
        assertEquals("My first community post", publicList.items().getFirst().getTitle());
    }

    private void resetDatabase() {
        deleteIfExists("DELETE FROM community_post");
        deleteIfExists("DELETE FROM comment");
        deleteIfExists("DELETE FROM community_token_revoked");
        deleteIfExists("DELETE FROM article_tag");
        deleteIfExists("DELETE FROM article");
        deleteIfExists("DELETE FROM token_revoked");
        deleteIfExists("DELETE FROM device_session");
        deleteIfExists("DELETE FROM community_user");
        deleteIfExists("UPDATE community_node SET status = 'ACTIVE'");
    }

    private void deleteIfExists(String sql) {
        try {
            jdbcTemplate.update(sql);
        } catch (DataAccessException ignored) {
        }
    }
}

