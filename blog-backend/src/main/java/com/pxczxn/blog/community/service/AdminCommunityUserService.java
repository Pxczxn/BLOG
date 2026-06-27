package com.pxczxn.blog.community.service;

import com.pxczxn.blog.common.response.PageResponse;
import com.pxczxn.blog.community.dto.AdminCommunityUserItemResponse;
import com.pxczxn.blog.community.dto.AdminCommunityUserUpdateRequest;
import com.pxczxn.blog.community.entity.CommunityUser;
import com.pxczxn.blog.community.entity.CommunityUserRole;
import com.pxczxn.blog.community.entity.CommunityUserStatus;
import com.pxczxn.blog.community.repository.CommunityUserRepository;
import com.pxczxn.blog.user.exception.UserNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AdminCommunityUserService {

    private final CommunityUserRepository communityUserRepository;

    @Transactional(readOnly = true)
    public PageResponse<AdminCommunityUserItemResponse> list(Pageable pageable,
                                                             int page,
                                                             String status,
                                                             String role,
                                                             String keyword) {
        Page<CommunityUser> result = communityUserRepository.findAll(buildSpecification(status, role, keyword), pageable);
        List<AdminCommunityUserItemResponse> items = result.getContent().stream()
                .map(AdminCommunityUserItemResponse::from)
                .toList();
        return new PageResponse<>(items, result.getTotalElements(), page, pageable.getPageSize());
    }

    @Transactional
    public AdminCommunityUserItemResponse update(Long id, AdminCommunityUserUpdateRequest request) {
        CommunityUser user = communityUserRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setRole(request.getRole());
        user.setStatus(request.getStatus());
        return AdminCommunityUserItemResponse.from(communityUserRepository.save(user));
    }

    private Specification<CommunityUser> buildSpecification(String status, String role, String keyword) {
        CommunityUserStatus statusFilter = parseStatus(status);
        CommunityUserRole roleFilter = parseRole(role);
        String keywordFilter = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (statusFilter != null) {
                predicates.add(cb.equal(root.get("status"), statusFilter));
            }
            if (roleFilter != null) {
                predicates.add(cb.equal(root.get("role"), roleFilter));
            }
            if (!keywordFilter.isBlank()) {
                String like = "%" + keywordFilter + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("username")), like),
                        cb.like(cb.lower(root.get("email")), like),
                        cb.like(cb.lower(root.get("displayName")), like)
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private CommunityUserStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        return CommunityUserStatus.valueOf(status.trim().toUpperCase(Locale.ROOT));
    }

    private CommunityUserRole parseRole(String role) {
        if (role == null || role.isBlank()) {
            return null;
        }
        return CommunityUserRole.valueOf(role.trim().toUpperCase(Locale.ROOT));
    }
}
