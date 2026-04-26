package com.pxczxn.blog.community.node.service;

import com.pxczxn.blog.community.node.dto.CommunityNodeRequest;
import com.pxczxn.blog.community.node.dto.CommunityNodeResponse;
import com.pxczxn.blog.community.node.entity.CommunityNode;
import com.pxczxn.blog.community.node.entity.CommunityNodeStatus;
import com.pxczxn.blog.community.node.exception.CommunityNodeNotFoundException;
import com.pxczxn.blog.community.node.repository.CommunityNodeRepository;
import com.pxczxn.blog.community.post.entity.CommunityPostStatus;
import com.pxczxn.blog.community.post.repository.CommunityPostRepository;
import com.pxczxn.blog.user.exception.UserAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;






@Service
@RequiredArgsConstructor
public class CommunityNodeService {

    private final CommunityNodeRepository communityNodeRepository;
    private final CommunityPostRepository communityPostRepository;

    






    @Transactional(readOnly = true)
    public List<CommunityNodeResponse> listPublic() {
        return communityNodeRepository.findAllByStatusOrderBySortOrderAscCreatedAtAsc(CommunityNodeStatus.ACTIVE).stream()
                .map(node -> CommunityNodeResponse.from(
                        node,
                        communityPostRepository.countByNodeIdAndStatus(node.getId(), CommunityPostStatus.PUBLISHED)
                ))
                .toList();
    }

    






    @Transactional(readOnly = true)
    public CommunityNodeResponse getPublicBySlug(String slug) {
        CommunityNode node = communityNodeRepository.findBySlugAndStatus(slug, CommunityNodeStatus.ACTIVE)
                .orElseThrow(() -> new CommunityNodeNotFoundException(slug));
        return CommunityNodeResponse.from(
                node,
                communityPostRepository.countByNodeIdAndStatus(node.getId(), CommunityPostStatus.PUBLISHED)
        );
    }

    






    @Transactional(readOnly = true)
    public List<CommunityNodeResponse> listAdmin() {
        return communityNodeRepository.findAll().stream()
                .sorted(java.util.Comparator.comparing(CommunityNode::getSortOrder).thenComparing(CommunityNode::getCreatedAt))
                .map(node -> CommunityNodeResponse.from(
                        node,
                        communityPostRepository.countByNodeIdAndStatus(node.getId(), CommunityPostStatus.PUBLISHED)
                ))
                .toList();
    }

    






    @Transactional
    public CommunityNodeResponse create(CommunityNodeRequest request) {
        String slug = request.getSlug().trim().toLowerCase();
        String name = request.getName().trim();
        if (communityNodeRepository.existsBySlug(slug)) {
            throw new UserAlreadyExistsException("node slug", slug);
        }
        if (communityNodeRepository.existsByName(name)) {
            throw new UserAlreadyExistsException("node name", name);
        }

        CommunityNode node = CommunityNode.builder()
                .name(name)
                .slug(slug)
                .description(trimToNull(request.getDescription()))
                .icon(trimToNull(request.getIcon()))
                .sortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder())
                .status(request.getStatus() == null ? CommunityNodeStatus.ACTIVE : request.getStatus())
                .build();
        return CommunityNodeResponse.from(communityNodeRepository.save(node), 0);
    }

    








    @Transactional
    public CommunityNodeResponse update(Long id, CommunityNodeRequest request) {
        CommunityNode node = communityNodeRepository.findById(id)
                .orElseThrow(() -> new CommunityNodeNotFoundException(id));
        String slug = request.getSlug().trim().toLowerCase();
        String name = request.getName().trim();
        if (!node.getSlug().equals(slug) && communityNodeRepository.existsBySlug(slug)) {
            throw new UserAlreadyExistsException("node slug", slug);
        }
        if (!node.getName().equals(name) && communityNodeRepository.existsByName(name)) {
            throw new UserAlreadyExistsException("node name", name);
        }

        node.setName(name);
        node.setSlug(slug);
        node.setDescription(trimToNull(request.getDescription()));
        node.setIcon(trimToNull(request.getIcon()));
        node.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
        node.setStatus(request.getStatus() == null ? CommunityNodeStatus.ACTIVE : request.getStatus());

        CommunityNode saved = communityNodeRepository.save(node);
        return CommunityNodeResponse.from(saved, communityPostRepository.countByNodeIdAndStatus(saved.getId(), CommunityPostStatus.PUBLISHED));
    }

    






    @Transactional(readOnly = true)
    public CommunityNode getById(Long id) {
        return communityNodeRepository.findById(id)
                .orElseThrow(() -> new CommunityNodeNotFoundException(id));
    }

    






    @Transactional(readOnly = true)
    public CommunityNode getActiveNode(Long id) {
        CommunityNode node = getById(id);
        if (node.getStatus() != CommunityNodeStatus.ACTIVE) {
            throw new CommunityNodeNotFoundException(id);
        }
        return node;
    }

    





    @Transactional(readOnly = true)
    public Long resolveNodeId(String nodeKey) {
        if (nodeKey == null || nodeKey.isBlank()) {
            return null;
        }
        String value = nodeKey.trim();
        try {
            return communityNodeRepository.findById(Long.valueOf(value)).map(CommunityNode::getId).orElse(null);
        } catch (NumberFormatException ignored) {
            return communityNodeRepository.findBySlug(value).map(CommunityNode::getId).orElse(null);
        }
    }

    





    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
