




package com.pxczxn.blog.community.moderation.repository;

import com.pxczxn.blog.community.moderation.entity.ContentReport;
import com.pxczxn.blog.community.moderation.entity.ModerationContentType;
import com.pxczxn.blog.community.moderation.entity.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ContentReportRepository extends JpaRepository<ContentReport, Long>, JpaSpecificationExecutor<ContentReport> {

    








    boolean existsByReporterUserIdAndContentTypeAndContentIdAndStatus(
            Long reporterUserId,
            ModerationContentType contentType,
            Long contentId,
            ReportStatus status
    );
}
