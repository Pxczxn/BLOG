/**
 * 内容举报未找到异常
 * <p>
 * 当根据 ID 查找不到举报记录时抛出
 */
package com.pxczxn.blog.community.moderation.exception;

public class ContentReportNotFoundException extends RuntimeException {

    /**
     * 根据举报 ID 构造异常
     *
     * @param id 未找到的举报 ID
     */
    public ContentReportNotFoundException(Long id) {
        super("Content report not found: " + id);
    }
}
