package com.pxczxn.blog.community.node.repository;

import com.pxczxn.blog.community.node.entity.CommunityNode;
import com.pxczxn.blog.community.node.entity.CommunityNodeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 社区节点数据访问接口
 */
public interface CommunityNodeRepository extends JpaRepository<CommunityNode, Long> {

    /**
     * 根据URL别名查询节点
     *
     * @param slug URL别名
     * @return 节点可选对象
     */
    Optional<CommunityNode> findBySlug(String slug);

    /**
     * 根据URL别名和状态查询节点
     *
     * @param slug   URL别名
     * @param status 节点状态
     * @return 节点可选对象
     */
    Optional<CommunityNode> findBySlugAndStatus(String slug, CommunityNodeStatus status);

    /**
     * 检查指定URL别名的节点是否存在
     *
     * @param slug URL别名
     * @return 存在返回true，否则返回false
     */
    boolean existsBySlug(String slug);

    /**
     * 检查指定名称的节点是否存在
     *
     * @param name 节点名称
     * @return 存在返回true，否则返回false
     */
    boolean existsByName(String name);

    /**
     * 查询指定状态的所有节点，按排序顺序和创建时间升序排列
     *
     * @param status 节点状态
     * @return 节点列表
     */
    List<CommunityNode> findAllByStatusOrderBySortOrderAscCreatedAtAsc(CommunityNodeStatus status);
}
