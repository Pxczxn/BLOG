package com.pxczxn.blog.community.node.service;

import com.pxczxn.blog.community.node.dto.CommunityNodeRequest;
import com.pxczxn.blog.community.node.dto.CommunityNodeResponse;
import com.pxczxn.blog.community.node.entity.CommunityNode;
import com.pxczxn.blog.community.node.entity.CommunityNodeStatus;
import com.pxczxn.blog.community.node.exception.CommunityNodeNotFoundException;
import com.pxczxn.blog.community.node.repository.CommunityNodeRepository;
import com.pxczxn.blog.community.post.entity.CommunityPostStatus;
import com.pxczxn.blog.community.post.repository.CommunityPostRepository;
import com.pxczxn.blog.community.post.repository.NodePostCountProjection;
import com.pxczxn.blog.user.exception.UserAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 社区节点服务
 * <p>
 * 处理社区节点的增删改查，节点是帖子的分类版块
 */
@Service
@RequiredArgsConstructor
public class CommunityNodeService {

    private final CommunityNodeRepository communityNodeRepository;
    private final CommunityPostRepository communityPostRepository;

    /**
     * 获取公开的节点列表（仅激活状态）
     * <p>
     * 按排序顺序和创建时间升序排列
     *
     * @return 激活状态的节点响应列表
     */
    @Transactional(readOnly = true)
    public List<CommunityNodeResponse> listPublic() {
        List<CommunityNode> nodes = communityNodeRepository.findAllByStatusOrderBySortOrderAscCreatedAtAsc(CommunityNodeStatus.ACTIVE);
        Map<Long, Long> postCounts = loadPublishedPostCounts(nodes.stream().map(CommunityNode::getId).toList());
        return nodes.stream()
                .map(node -> CommunityNodeResponse.from(node, postCounts.getOrDefault(node.getId(), 0L)))
                .toList();
    }

    /**
     * 根据URL别名获取公开的节点详情
     *
     * @param slug URL别名
     * @return 节点响应
     * @throws CommunityNodeNotFoundException 节点不存在或未激活时抛出
     */
    @Transactional(readOnly = true)
    public CommunityNodeResponse getPublicBySlug(String slug) {
        CommunityNode node = communityNodeRepository.findBySlugAndStatus(slug, CommunityNodeStatus.ACTIVE)
                .orElseThrow(() -> new CommunityNodeNotFoundException(slug));
        return CommunityNodeResponse.from(
                node,
                communityPostRepository.countByNodeIdAndStatus(node.getId(), CommunityPostStatus.PUBLISHED)
        );
    }

    /**
     * 获取所有节点列表（管理员视图）
     * <p>
     * 包含所有状态的节点，按排序顺序和创建时间升序排列
     *
     * @return 所有节点的响应列表
     */
    @Transactional(readOnly = true)
    public List<CommunityNodeResponse> listAdmin() {
        List<CommunityNode> nodes = communityNodeRepository.findAll().stream()
                .sorted(java.util.Comparator.comparing(CommunityNode::getSortOrder).thenComparing(CommunityNode::getCreatedAt))
                .toList();
        Map<Long, Long> postCounts = loadPublishedPostCounts(nodes.stream().map(CommunityNode::getId).toList());
        return nodes.stream()
                .map(node -> CommunityNodeResponse.from(node, postCounts.getOrDefault(node.getId(), 0L)))
                .toList();
    }

    /**
     * 创建新的社区节点
     *
     * @param request 节点创建请求
     * @return 创建成功的节点响应
     * @throws UserAlreadyExistsException 当slug或name已存在时抛出
     */
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

    /**
     * 更新社区节点
     *
     * @param id      节点ID
     * @param request 节点更新请求
     * @return 更新后的节点响应
     * @throws CommunityNodeNotFoundException 节点不存在时抛出
     * @throws UserAlreadyExistsException     当新的slug或name已被其他节点使用时抛出
     */
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

    /**
     * 根据ID获取节点实体
     *
     * @param id 节点ID
     * @return 节点实体
     * @throws CommunityNodeNotFoundException 节点不存在时抛出
     */
    @Transactional(readOnly = true)
    public CommunityNode getById(Long id) {
        return communityNodeRepository.findById(id)
                .orElseThrow(() -> new CommunityNodeNotFoundException(id));
    }

    /**
     * 根据ID获取激活状态的节点实体
     *
     * @param id 节点ID
     * @return 激活状态的节点实体
     * @throws CommunityNodeNotFoundException 节点不存在或未激活时抛出
     */
    @Transactional(readOnly = true)
    public CommunityNode getActiveNode(Long id) {
        CommunityNode node = getById(id);
        if (node.getStatus() != CommunityNodeStatus.ACTIVE) {
            throw new CommunityNodeNotFoundException(id);
        }
        return node;
    }

    /**
     * 解析节点标识符，支持ID或URL别名
     *
     * @param nodeKey 节点标识符（可以是数字ID或字符串slug）
     * @return 节点ID，未找到返回null
     */
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

    /**
     * 将字符串去除首尾空白，若结果为空则返回null
     *
     * @param value 原始字符串
     * @return 处理后的字符串或null
     */
    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private Map<Long, Long> loadPublishedPostCounts(Collection<Long> nodeIds) {
        if (nodeIds.isEmpty()) {
            return Map.of();
        }
        return communityPostRepository.countByNodeIdsAndStatus(nodeIds, CommunityPostStatus.PUBLISHED).stream()
                .collect(Collectors.toMap(NodePostCountProjection::getNodeId, NodePostCountProjection::getCount));
    }
}
