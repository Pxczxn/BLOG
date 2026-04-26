/**
 * 社区互动服务
 * <p>
 * 处理社区用户之间的互动功能，包括点赞、收藏、关注和通知等
 */
package com.pxczxn.blog.community.interaction.service;

import com.pxczxn.blog.common.response.PageResponse;
import com.pxczxn.blog.community.entity.CommunityUser;
import com.pxczxn.blog.community.entity.CommunityUserStatus;
import com.pxczxn.blog.community.exception.CommunityAuthException;
import com.pxczxn.blog.community.exception.CommunityUserNotFoundException;
import com.pxczxn.blog.community.interaction.dto.CommunityFavoritePostItemResponse;
import com.pxczxn.blog.community.interaction.dto.CommunityFollowStatusResponse;
import com.pxczxn.blog.community.interaction.dto.CommunityNotificationItemResponse;
import com.pxczxn.blog.community.interaction.dto.CommunityNotificationListResponse;
import com.pxczxn.blog.community.interaction.dto.AdminInteractionOverviewResponse;
import com.pxczxn.blog.community.interaction.dto.AdminPostHeatItemResponse;
import com.pxczxn.blog.community.interaction.dto.PostInteractionResponse;
import com.pxczxn.blog.community.interaction.entity.CommunityNotification;
import com.pxczxn.blog.community.interaction.entity.CommunityNotificationType;
import com.pxczxn.blog.community.interaction.entity.CommunityPostFavorite;
import com.pxczxn.blog.community.interaction.entity.CommunityPostLike;
import com.pxczxn.blog.community.interaction.entity.CommunityUserFollow;
import com.pxczxn.blog.community.interaction.repository.CommunityNotificationRepository;
import com.pxczxn.blog.community.interaction.repository.CommunityPostFavoriteRepository;
import com.pxczxn.blog.community.interaction.repository.CommunityPostLikeRepository;
import com.pxczxn.blog.community.interaction.repository.CommunityUserFollowRepository;
import com.pxczxn.blog.community.interaction.repository.PostCountProjection;
import com.pxczxn.blog.community.node.entity.CommunityNode;
import com.pxczxn.blog.community.node.repository.CommunityNodeRepository;
import com.pxczxn.blog.community.post.dto.CommunityPostAuthorSummaryResponse;
import com.pxczxn.blog.community.post.dto.CommunityPostNodeSummaryResponse;
import com.pxczxn.blog.community.post.entity.CommunityPost;
import com.pxczxn.blog.community.post.entity.CommunityPostStatus;
import com.pxczxn.blog.community.post.exception.CommunityPostNotFoundException;
import com.pxczxn.blog.community.post.repository.CommunityPostRepository;
import com.pxczxn.blog.community.repository.CommunityUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityInteractionService {

    private final CommunityPostRepository communityPostRepository;
    private final CommunityNodeRepository communityNodeRepository;
    private final CommunityUserRepository communityUserRepository;
    private final CommunityPostLikeRepository communityPostLikeRepository;
    private final CommunityPostFavoriteRepository communityPostFavoriteRepository;
    private final CommunityUserFollowRepository communityUserFollowRepository;
    private final CommunityNotificationRepository communityNotificationRepository;

    /**
     * 点赞帖子
     *
     * @param userId 操作用户ID
     * @param postId 帖子ID
     * @return 帖子互动状态
     */
    @Transactional
    public PostInteractionResponse likePost(Long userId, Long postId) {
        CommunityUser actor = getActiveUser(userId);
        CommunityPost post = getPublishedPost(postId);
        if (!communityPostLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            communityPostLikeRepository.save(CommunityPostLike.builder()
                    .postId(postId)
                    .userId(userId)
                    .build());
            notifyPostOwner(post, actor, CommunityNotificationType.POST_LIKED, actor.getDisplayName() + " liked your post");
        }
        return getPostInteraction(postId, userId);
    }

    /**
     * 取消点赞帖子
     *
     * @param userId 操作用户ID
     * @param postId 帖子ID
     * @return 帖子互动状态
     */
    @Transactional
    public PostInteractionResponse unlikePost(Long userId, Long postId) {
        getActiveUser(userId);
        CommunityPost post = getPublishedPost(postId);
        communityPostLikeRepository.findByPostIdAndUserId(postId, userId)
                .ifPresent(communityPostLikeRepository::delete);
        return getPostInteraction(post.getId(), userId);
    }

    /**
     * 收藏帖子
     *
     * @param userId 操作用户ID
     * @param postId 帖子ID
     * @return 帖子互动状态
     */
    @Transactional
    public PostInteractionResponse favoritePost(Long userId, Long postId) {
        CommunityUser actor = getActiveUser(userId);
        CommunityPost post = getPublishedPost(postId);
        if (!communityPostFavoriteRepository.existsByPostIdAndUserId(postId, userId)) {
            communityPostFavoriteRepository.save(CommunityPostFavorite.builder()
                    .postId(postId)
                    .userId(userId)
                    .build());
            notifyPostOwner(post, actor, CommunityNotificationType.POST_FAVORITED, actor.getDisplayName() + " favorited your post");
        }
        return getPostInteraction(postId, userId);
    }

    /**
     * 取消收藏帖子
     *
     * @param userId 操作用户ID
     * @param postId 帖子ID
     * @return 帖子互动状态
     */
    @Transactional
    public PostInteractionResponse unfavoritePost(Long userId, Long postId) {
        getActiveUser(userId);
        CommunityPost post = getPublishedPost(postId);
        communityPostFavoriteRepository.findByPostIdAndUserId(postId, userId)
                .ifPresent(communityPostFavoriteRepository::delete);
        return getPostInteraction(post.getId(), userId);
    }

    /**
     * 获取帖子互动状态（点赞数、收藏数、当前用户是否已点赞/收藏）
     *
     * @param postId 帖子ID
     * @param userId 当前用户ID，为null时不判断是否已点赞/收藏
     * @return 帖子互动状态
     */
    @Transactional(readOnly = true)
    public PostInteractionResponse getPostInteraction(Long postId, Long userId) {
        CommunityPost post = getPublishedPost(postId);
        long likeCount = communityPostLikeRepository.countByPostId(post.getId());
        long favoriteCount = communityPostFavoriteRepository.countByPostId(post.getId());
        boolean likedByMe = userId != null && communityPostLikeRepository.existsByPostIdAndUserId(post.getId(), userId);
        boolean favoritedByMe = userId != null && communityPostFavoriteRepository.existsByPostIdAndUserId(post.getId(), userId);
        return PostInteractionResponse.builder()
                .postId(post.getId())
                .likeCount(likeCount)
                .favoriteCount(favoriteCount)
                .likedByMe(likedByMe)
                .favoritedByMe(favoritedByMe)
                .build();
    }

    /**
     * 批量构建帖子互动状态映射
     *
     * @param postIds 帖子ID集合
     * @param userId  当前用户ID，为null时不判断是否已点赞/收藏
     * @return 帖子ID到互动状态的映射
     */
    @Transactional(readOnly = true)
    public Map<Long, PostInteractionResponse> buildPostInteractionMap(Collection<Long> postIds, Long userId) {
        Set<Long> ids = postIds.stream().filter(Objects::nonNull).collect(Collectors.toSet());
        if (ids.isEmpty()) {
            return Map.of();
        }

        Map<Long, Long> likeCounts = toCountMap(communityPostLikeRepository.countByPostIds(ids));
        Map<Long, Long> favoriteCounts = toCountMap(communityPostFavoriteRepository.countByPostIds(ids));
        Set<Long> likedIds = new HashSet<>();
        Set<Long> favoritedIds = new HashSet<>();
        if (userId != null) {
            likedIds = communityPostLikeRepository.findAllByUserIdAndPostIdIn(userId, ids)
                    .stream()
                    .map(CommunityPostLike::getPostId)
                    .collect(Collectors.toSet());
            favoritedIds = communityPostFavoriteRepository.findAllByUserIdAndPostIdIn(userId, ids)
                    .stream()
                    .map(CommunityPostFavorite::getPostId)
                    .collect(Collectors.toSet());
        }

        Map<Long, PostInteractionResponse> result = new HashMap<>();
        for (Long postId : ids) {
            result.put(postId, PostInteractionResponse.builder()
                    .postId(postId)
                    .likeCount(likeCounts.getOrDefault(postId, 0L))
                    .favoriteCount(favoriteCounts.getOrDefault(postId, 0L))
                    .likedByMe(likedIds.contains(postId))
                    .favoritedByMe(favoritedIds.contains(postId))
                    .build());
        }
        return result;
    }

    /**
     * 关注用户
     *
     * @param followerId     关注者用户ID
     * @param targetUsername  目标用户名
     * @return 关注状态
     */
    @Transactional
    public CommunityFollowStatusResponse followUser(Long followerId, String targetUsername) {
        CommunityUser follower = getActiveUser(followerId);
        CommunityUser target = getActiveUserByUsername(targetUsername);
        if (follower.getId().equals(target.getId())) {
            throw new CommunityAuthException(com.pxczxn.blog.common.response.ApiErrorCode.INVALID_ARGUMENT, "Cannot follow yourself");
        }
        if (!communityUserFollowRepository.existsByFollowerIdAndFollowingId(follower.getId(), target.getId())) {
            communityUserFollowRepository.save(CommunityUserFollow.builder()
                    .followerId(follower.getId())
                    .followingId(target.getId())
                    .build());
            createNotification(
                    target.getId(),
                    follower.getId(),
                    CommunityNotificationType.USER_FOLLOWED,
                    "You have a new follower",
                    follower.getDisplayName() + " started following you",
                    null,
                    null,
                    null
            );
        }
        return buildFollowStatus(follower.getId(), target);
    }

    /**
     * 取消关注用户
     *
     * @param followerId     关注者用户ID
     * @param targetUsername  目标用户名
     * @return 关注状态
     */
    @Transactional
    public CommunityFollowStatusResponse unfollowUser(Long followerId, String targetUsername) {
        CommunityUser follower = getActiveUser(followerId);
        CommunityUser target = getActiveUserByUsername(targetUsername);
        communityUserFollowRepository.findByFollowerIdAndFollowingId(follower.getId(), target.getId())
                .ifPresent(communityUserFollowRepository::delete);
        return buildFollowStatus(follower.getId(), target);
    }

    /**
     * 获取关注状态
     *
     * @param viewerId       查看者用户ID
     * @param targetUsername  目标用户名
     * @return 关注状态
     */
    @Transactional(readOnly = true)
    public CommunityFollowStatusResponse getFollowStatus(Long viewerId, String targetUsername) {
        CommunityUser target = getActiveUserByUsername(targetUsername);
        return buildFollowStatus(viewerId, target);
    }

    /**
     * 获取当前用户的收藏帖子列表
     *
     * @param userId   用户ID
     * @param pageable 分页参数
     * @param page     页码
     * @return 收藏帖子分页响应
     */
    @Transactional(readOnly = true)
    public PageResponse<CommunityFavoritePostItemResponse> listMyFavorites(Long userId, Pageable pageable, int page) {
        getActiveUser(userId);
        Page<CommunityPostFavorite> favoritePage = communityPostFavoriteRepository.findPublishedByUserIdOrderByCreatedAtDesc(userId, pageable);

        List<Long> postIds = favoritePage.getContent().stream()
                .map(CommunityPostFavorite::getPostId)
                .toList();
        Map<Long, CommunityPost> posts = loadPosts(postIds);
        Map<Long, CommunityNode> nodes = loadNodes(posts.values().stream().map(CommunityPost::getNodeId).toList());
        Map<Long, CommunityUser> authors = loadUsers(posts.values().stream().map(CommunityPost::getAuthorId).toList());
        Map<Long, PostInteractionResponse> interactions = buildPostInteractionMap(postIds, userId);

        List<CommunityFavoritePostItemResponse> items = favoritePage.getContent().stream()
                .map(favorite -> {
                    CommunityPost post = posts.get(favorite.getPostId());
                    if (post == null || post.getStatus() != CommunityPostStatus.PUBLISHED) {
                        return null;
                    }
                    CommunityNode node = nodes.get(post.getNodeId());
                    CommunityUser author = authors.get(post.getAuthorId());
                    PostInteractionResponse interaction = interactions.getOrDefault(post.getId(), PostInteractionResponse.builder()
                            .postId(post.getId())
                            .likeCount(0)
                            .favoriteCount(0)
                            .likedByMe(false)
                            .favoritedByMe(true)
                            .build());
                    return CommunityFavoritePostItemResponse.from(
                            post,
                            node != null ? CommunityPostNodeSummaryResponse.from(node) : null,
                            author != null ? CommunityPostAuthorSummaryResponse.from(author) : null,
                            favorite.getCreatedAt(),
                            interaction.getLikeCount(),
                            interaction.getFavoriteCount()
                    );
                })
                .filter(Objects::nonNull)
                .toList();

        return new PageResponse<>(items, favoritePage.getTotalElements(), page, favoritePage.getSize());
    }

    /**
     * 获取当前用户的通知列表
     *
     * @param userId     用户ID
     * @param pageable   分页参数
     * @param page       页码
     * @param unreadOnly 是否只显示未读通知
     * @return 通知列表响应
     */
    @Transactional(readOnly = true)
    public CommunityNotificationListResponse listNotifications(Long userId, Pageable pageable, int page, boolean unreadOnly) {
        getActiveUser(userId);
        Page<CommunityNotification> notificationPage = unreadOnly
                ? communityNotificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId, pageable)
                : communityNotificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        List<CommunityNotificationItemResponse> items = notificationPage.getContent().stream()
                .map(CommunityNotificationItemResponse::from)
                .toList();
        return CommunityNotificationListResponse.builder()
                .page(new PageResponse<>(items, notificationPage.getTotalElements(), page, notificationPage.getSize()))
                .unreadCount(communityNotificationRepository.countByUserIdAndIsReadFalse(userId))
                .build();
    }

    /**
     * 标记单条通知为已读
     *
     * @param userId         用户ID
     * @param notificationId 通知ID
     */
    @Transactional
    public void markNotificationRead(Long userId, Long notificationId) {
        getActiveUser(userId);
        CommunityNotification notification = communityNotificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        if (!notification.getUserId().equals(userId)) {
            throw new CommunityAuthException(com.pxczxn.blog.common.response.ApiErrorCode.AUTH_FORBIDDEN, "Cannot read other user's notification");
        }
        if (!Boolean.TRUE.equals(notification.getIsRead())) {
            notification.setIsRead(true);
            notification.setReadAt(LocalDateTime.now());
            communityNotificationRepository.save(notification);
        }
    }

    /**
     * 标记当前用户所有通知为已读
     *
     * @param userId 用户ID
     * @return 已标记为已读的通知数量
     */
    @Transactional
    public int markAllNotificationsRead(Long userId) {
        getActiveUser(userId);
        return communityNotificationRepository.markAllReadByUserId(userId, LocalDateTime.now());
    }

    /**
     * 获取管理端社区互动概览数据
     *
     * @param topSize 热门帖子数量限制
     * @return 互动概览响应
     */
    @Transactional(readOnly = true)
    public AdminInteractionOverviewResponse getAdminOverview(int topSize) {
        int safeTopSize = Math.max(1, Math.min(topSize, 50));
        Map<Long, Long> likeCounts = toCountMap(communityPostLikeRepository.findTopPostCounts(PageRequest.of(0, safeTopSize)));
        Map<Long, Long> favoriteCounts = toCountMap(communityPostFavoriteRepository.findTopPostCounts(PageRequest.of(0, safeTopSize)));

        Set<Long> postIds = new HashSet<>();
        postIds.addAll(likeCounts.keySet());
        postIds.addAll(favoriteCounts.keySet());

        Map<Long, CommunityPost> posts = loadPosts(postIds);
        List<AdminPostHeatItemResponse> hotPosts = postIds.stream()
                .map(posts::get)
                .filter(Objects::nonNull)
                .map(post -> AdminPostHeatItemResponse.from(
                        post,
                        likeCounts.getOrDefault(post.getId(), 0L),
                        favoriteCounts.getOrDefault(post.getId(), 0L)
                ))
                .sorted((a, b) -> Long.compare(b.getHeatScore(), a.getHeatScore()))
                .limit(safeTopSize)
                .toList();

        return AdminInteractionOverviewResponse.builder()
                .totalLikes(communityPostLikeRepository.count())
                .totalFavorites(communityPostFavoriteRepository.count())
                .totalFollows(communityUserFollowRepository.count())
                .totalNotifications(communityNotificationRepository.count())
                .unreadNotifications(communityNotificationRepository.countByIsReadFalse())
                .topHotPosts(hotPosts)
                .build();
    }

    /**
     * 通知用户其评论被回复
     *
     * @param targetUserId      被回复的用户ID
     * @param actorUserId       回复者用户ID
     * @param actorDisplayName  回复者显示名
     * @param relatedCommentId  关联评论ID
     * @param replyContent      回复内容
     */
    @Transactional
    public void notifyCommentReplied(Long targetUserId,
                                     Long actorUserId,
                                     String actorDisplayName,
                                     Long relatedCommentId,
                                     String replyContent) {
        if (targetUserId == null) {
            return;
        }
        if (actorUserId != null && actorUserId.equals(targetUserId)) {
            return;
        }
        if (!communityUserRepository.existsById(targetUserId)) {
            return;
        }

        String actorName = trimToLength(actorDisplayName, 80);
        if (actorName == null) {
            actorName = "Someone";
        }
        createNotification(
                targetUserId,
                actorUserId,
                CommunityNotificationType.COMMENT_REPLIED,
                actorName + " replied to your comment",
                trimToLength(replyContent, 500),
                null,
                relatedCommentId,
                null
        );
    }

    /**
     * 通知用户其帖子评论被回复
     *
     * @param targetUserId         被回复的用户ID
     * @param actorUserId          回复者用户ID
     * @param actorDisplayName     回复者显示名
     * @param relatedPostId        关联帖子ID
     * @param relatedPostCommentId 关联帖子评论ID
     * @param replyContent         回复内容
     */
    @Transactional
    public void notifyPostCommentReplied(Long targetUserId,
                                         Long actorUserId,
                                         String actorDisplayName,
                                         Long relatedPostId,
                                         Long relatedPostCommentId,
                                         String replyContent) {
        if (targetUserId == null) {
            return;
        }
        if (actorUserId != null && actorUserId.equals(targetUserId)) {
            return;
        }
        if (!communityUserRepository.existsById(targetUserId)) {
            return;
        }

        String actorName = trimToLength(actorDisplayName, 80);
        if (actorName == null) {
            actorName = "Someone";
        }
        createNotification(
                targetUserId,
                actorUserId,
                CommunityNotificationType.COMMENT_REPLIED,
                actorName + " replied to your comment",
                trimToLength(replyContent, 500),
                relatedPostId,
                null,
                relatedPostCommentId
        );
    }

    /**
     * 通知用户其帖子被评论
     *
     * @param targetUserId         帖子作者用户ID
     * @param actorUserId          评论者用户ID
     * @param actorDisplayName     评论者显示名
     * @param relatedPostId        关联帖子ID
     * @param relatedPostCommentId 关联帖子评论ID
     * @param commentContent       评论内容
     */
    @Transactional
    public void notifyPostCommented(Long targetUserId,
                                    Long actorUserId,
                                    String actorDisplayName,
                                    Long relatedPostId,
                                    Long relatedPostCommentId,
                                    String commentContent) {
        if (targetUserId == null) {
            return;
        }
        if (actorUserId != null && actorUserId.equals(targetUserId)) {
            return;
        }
        if (!communityUserRepository.existsById(targetUserId)) {
            return;
        }

        String actorName = trimToLength(actorDisplayName, 80);
        if (actorName == null) {
            actorName = "Someone";
        }
        createNotification(
                targetUserId,
                actorUserId,
                CommunityNotificationType.COMMENT_REPLIED,
                actorName + " commented on your post",
                trimToLength(commentContent, 500),
                relatedPostId,
                null,
                relatedPostCommentId
        );
    }

    /**
     * 通知帖子作者（点赞/收藏时调用，自己操作自己的帖子不通知）
     *
     * @param post 帖子实体
     * @param actor 操作者实体
     * @param type  通知类型
     * @param title 通知标题
     */
    private void notifyPostOwner(CommunityPost post, CommunityUser actor, CommunityNotificationType type, String title) {
        if (post.getAuthorId().equals(actor.getId())) {
            return;
        }
        createNotification(
                post.getAuthorId(),
                actor.getId(),
                type,
                title,
                post.getTitle(),
                post.getId(),
                null,
                null
        );
    }

    /**
     * 创建通知记录
     *
     * @param userId              通知接收者用户ID
     * @param actorUserId         操作者用户ID
     * @param type                通知类型
     * @param title               通知标题
     * @param content             通知内容
     * @param relatedPostId       关联帖子ID
     * @param relatedCommentId    关联评论ID
     * @param relatedPostCommentId 关联帖子评论ID
     */
    private void createNotification(Long userId,
                                    Long actorUserId,
                                    CommunityNotificationType type,
                                    String title,
                                    String content,
                                    Long relatedPostId,
                                    Long relatedCommentId,
                                    Long relatedPostCommentId) {
        communityNotificationRepository.save(CommunityNotification.builder()
                .userId(userId)
                .actorUserId(actorUserId)
                .type(type)
                .title(trimToLength(title, 120))
                .content(trimToLength(content, 500))
                .relatedPostId(relatedPostId)
                .relatedCommentId(relatedCommentId)
                .relatedPostCommentId(relatedPostCommentId)
                .isRead(false)
                .build());
    }

    /**
     * 构建用户关注状态响应
     *
     * @param viewerId 查看者用户ID
     * @param target   目标用户实体
     * @return 关注状态响应
     */
    private CommunityFollowStatusResponse buildFollowStatus(Long viewerId, CommunityUser target) {
        boolean following = viewerId != null
                && !viewerId.equals(target.getId())
                && communityUserFollowRepository.existsByFollowerIdAndFollowingId(viewerId, target.getId());
        return CommunityFollowStatusResponse.builder()
                .targetUserId(target.getId())
                .targetUsername(target.getUsername())
                .following(following)
                .followerCount(communityUserFollowRepository.countByFollowingId(target.getId()))
                .followingCount(communityUserFollowRepository.countByFollowerId(target.getId()))
                .build();
    }

    /**
     * 将帖子计数投影列表转换为帖子ID到计数的映射
     *
     * @param projections 计数投影列表
     * @return 帖子ID到计数的映射
     */
    private Map<Long, Long> toCountMap(List<PostCountProjection> projections) {
        Map<Long, Long> map = new HashMap<>();
        for (PostCountProjection projection : projections) {
            map.put(projection.getPostId(), projection.getCount());
        }
        return map;
    }

    /**
     * 获取已发布的帖子，不存在或未发布时抛出异常
     *
     * @param postId 帖子ID
     * @return 帖子实体
     */
    private CommunityPost getPublishedPost(Long postId) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new CommunityPostNotFoundException(postId));
        if (post.getStatus() != CommunityPostStatus.PUBLISHED) {
            throw new CommunityPostNotFoundException(postId);
        }
        return post;
    }

    /**
     * 获取活跃状态的用户，不存在或未激活时抛出异常
     *
     * @param userId 用户ID
     * @return 用户实体
     */
    private CommunityUser getActiveUser(Long userId) {
        CommunityUser user = communityUserRepository.findById(userId)
                .orElseThrow(() -> new CommunityUserNotFoundException(userId));
        if (user.getStatus() != CommunityUserStatus.ACTIVE) {
            throw CommunityAuthException.accountDisabled();
        }
        return user;
    }

    /**
     * 根据用户名获取活跃状态的用户，不存在或未激活时抛出异常
     *
     * @param username 用户名
     * @return 用户实体
     */
    private CommunityUser getActiveUserByUsername(String username) {
        String normalized = username == null ? "" : username.trim().toLowerCase(Locale.ROOT);
        CommunityUser user = communityUserRepository.findByUsername(normalized)
                .orElseThrow(() -> new CommunityUserNotFoundException(username));
        if (user.getStatus() != CommunityUserStatus.ACTIVE) {
            throw new CommunityUserNotFoundException(username);
        }
        return user;
    }

    /**
     * 根据帖子ID集合批量加载帖子
     *
     * @param postIds 帖子ID集合
     * @return 帖子ID到实体的映射
     */
    private Map<Long, CommunityPost> loadPosts(Collection<Long> postIds) {
        Map<Long, CommunityPost> posts = new HashMap<>();
        Set<Long> ids = postIds.stream().filter(Objects::nonNull).collect(Collectors.toSet());
        if (ids.isEmpty()) {
            return posts;
        }
        communityPostRepository.findAllById(ids).forEach(post -> posts.put(post.getId(), post));
        return posts;
    }

    /**
     * 根据节点ID集合批量加载节点
     *
     * @param nodeIds 节点ID集合
     * @return 节点ID到实体的映射
     */
    private Map<Long, CommunityNode> loadNodes(Collection<Long> nodeIds) {
        Map<Long, CommunityNode> nodes = new HashMap<>();
        Set<Long> ids = nodeIds.stream().filter(Objects::nonNull).collect(Collectors.toSet());
        if (ids.isEmpty()) {
            return nodes;
        }
        communityNodeRepository.findAllById(ids).forEach(node -> nodes.put(node.getId(), node));
        return nodes;
    }

    /**
     * 根据用户ID集合批量加载用户
     *
     * @param userIds 用户ID集合
     * @return 用户ID到实体的映射
     */
    private Map<Long, CommunityUser> loadUsers(Collection<Long> userIds) {
        Map<Long, CommunityUser> users = new HashMap<>();
        Set<Long> ids = userIds.stream().filter(Objects::nonNull).collect(Collectors.toSet());
        if (ids.isEmpty()) {
            return users;
        }
        communityUserRepository.findAllById(ids).forEach(user -> users.put(user.getId(), user));
        return users;
    }

    /**
     * 截断字符串到指定长度，空字符串返回null
     *
     * @param value     原始字符串
     * @param maxLength 最大长度
     * @return 截断后的字符串，可能为null
     */
    private String trimToLength(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        if (trimmed.length() <= maxLength) {
            return trimmed;
        }
        return trimmed.substring(0, maxLength);
    }
}
