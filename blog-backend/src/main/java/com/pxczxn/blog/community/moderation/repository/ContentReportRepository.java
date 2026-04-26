/**
 * 内容举报数据访问接口
 * <p>
 * 提供举报记录的 CRUD 操作和条件查询
 */
package com.pxczxn.blog.community.moderation.repository;

import com.pxczxn.blog.community.moderation.entity.ContentReport;
import com.pxczxn.blog.community.moderation.entity.ModerationContentType;
import com.pxczxn.blog.community.moderation.entity.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ContentReportRepository extends JpaRepository<ContentReport, Long>, JpaSpecificationExecutor<ContentReport> {

    /**
     * 检查指定用户是否已对指定内容的举报处于待处理状态
     *
     * @param reporterUserId 举报者用户 ID
     * @param contentType    内容类型
     * @param contentId      内容 ID
     * @param status         举报状态
     * @return 是否存在符合条件的举报记录
     */
    boolean existsByReporterUserIdAndContentTypeAndContentIdAndStatus(
            Long reporterUserId,
            ModerationContentType contentType,
            Long contentId,
            ReportStatus status
    );
}
