/**
 * 帖子计数投影接口
 */
package com.pxczxn.blog.community.interaction.repository;

public interface PostCountProjection {
    /**
     * 获取帖子ID
     *
     * @return 帖子ID
     */
    Long getPostId();

    /**
     * 获取计数
     *
     * @return 计数
     */
    long getCount();
}

