/**
 * 审核任务未找到异常
 * <p>
 * 当根据 ID 查找不到审核任务时抛出
 */
package com.pxczxn.blog.community.moderation.exception;

public class ModerationTaskNotFoundException extends RuntimeException {

    /**
     * 根据任务 ID 构造异常
     *
     * @param id 未找到的审核任务 ID
     */
    public ModerationTaskNotFoundException(Long id) {
        super("Moderation task not found: " + id);
    }
}
