/**
 * 分页响应 DTO
 * <p>
 * 统一的分页数据返回格式，包含数据列表、总数、当前页和每页大小。
 */
package com.pxczxn.blog.common.response;

import java.util.List;

/**
 * 分页响应记录类
 *
 * @param <T> 数据项类型
 * @param items 数据列表
 * @param total 总记录数
 * @param page 当前页码（从 1 开始）
 * @param size 每页大小
 */
public record PageResponse<T>(List<T> items, long total, int page, int size) {
}
