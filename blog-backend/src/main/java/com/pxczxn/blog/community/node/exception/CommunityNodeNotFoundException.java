package com.pxczxn.blog.community.node.exception;

/**
 * 社区节点未找到异常
 * <p>
 * 当根据ID或slug查询节点不存在时抛出
 */
public class CommunityNodeNotFoundException extends RuntimeException {

    /**
     * 根据节点ID构造异常
     *
     * @param id 节点ID
     */
    public CommunityNodeNotFoundException(Long id) {
        super("Community node not found: " + id);
    }

    /**
     * 根据节点URL别名构造异常
     *
     * @param slug 节点URL别名
     */
    public CommunityNodeNotFoundException(String slug) {
        super("Community node not found: " + slug);
    }
}
