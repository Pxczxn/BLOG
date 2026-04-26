/**
 * 社区用户资料服务
 * <p>
 * 处理社区用户个人资料的查看和更新
 */
package com.pxczxn.blog.community.service;

import com.pxczxn.blog.community.dto.CommunityProfileResponse;
import com.pxczxn.blog.community.dto.CommunityProfileUpdateRequest;
import com.pxczxn.blog.community.dto.PublicCommunityProfileResponse;
import com.pxczxn.blog.community.entity.CommunityUser;
import com.pxczxn.blog.community.entity.CommunityUserStatus;
import com.pxczxn.blog.community.exception.CommunityUserNotFoundException;
import com.pxczxn.blog.community.interaction.repository.CommunityUserFollowRepository;
import com.pxczxn.blog.community.repository.CommunityUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CommunityProfileService {

    private final CommunityUserRepository communityUserRepository;
    private final CommunityUserFollowRepository communityUserFollowRepository;

    @Transactional(readOnly = true)
    public CommunityProfileResponse getMe(Long userId) {
        CommunityUser user = communityUserRepository.findById(userId)
                .orElseThrow(() -> new CommunityUserNotFoundException(userId));
        return CommunityProfileResponse.from(user);
    }

    @Transactional
    public CommunityProfileResponse updateMe(Long userId, CommunityProfileUpdateRequest request) {
        CommunityUser user = communityUserRepository.findById(userId)
                .orElseThrow(() -> new CommunityUserNotFoundException(userId));

        if (request.getDisplayName() != null) {
            user.setDisplayName(request.getDisplayName().trim());
        }
        if (request.getBio() != null) {
            user.setBio(trimToNull(request.getBio()));
        }
        if (request.getAvatar() != null) {
            user.setAvatar(trimToNull(request.getAvatar()));
        }
        if (request.getWebsite() != null) {
            user.setWebsite(trimToNull(request.getWebsite()));
        }
        return CommunityProfileResponse.from(communityUserRepository.save(user));
    }

    @Transactional(readOnly = true)
    public PublicCommunityProfileResponse getPublicProfile(String username, Long viewerUserId) {
        CommunityUser user = communityUserRepository.findByUsername(username == null ? "" : username.trim().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new CommunityUserNotFoundException(username));
        if (user.getStatus() != CommunityUserStatus.ACTIVE) {
            throw new CommunityUserNotFoundException(username);
        }
        long followerCount = communityUserFollowRepository.countByFollowingId(user.getId());
        long followingCount = communityUserFollowRepository.countByFollowerId(user.getId());
        boolean followedByMe = viewerUserId != null
                && !viewerUserId.equals(user.getId())
                && communityUserFollowRepository.existsByFollowerIdAndFollowingId(viewerUserId, user.getId());
        return PublicCommunityProfileResponse.from(user, followerCount, followingCount, followedByMe);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

