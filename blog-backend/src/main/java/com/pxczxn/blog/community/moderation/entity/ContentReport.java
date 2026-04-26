/**
 * 内容举报实体
 * <p>
 * 记录用户对社区内容的举报信息，包括举报原因、处理状态和处理结果
 */
package com.pxczxn.blog.community.moderation.entity;

import com.pxczxn.blog.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "content_report")
public class ContentReport extends BaseTimeEntity {

    /** 举报 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 举报内容类型 */
    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false, length = 20)
    private ModerationContentType contentType;

    /** 举报内容 ID */
    @Column(name = "content_id", nullable = false)
    private Long contentId;

    /** 举报者用户 ID */
    @Column(name = "reporter_user_id", nullable = false)
    private Long reporterUserId;

    /** 举报原因 */
    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false, length = 20)
    @Builder.Default
    private ReportReason reason = ReportReason.OTHER;

    /** 举报描述 */
    @Column(name = "description", length = 500)
    private String description;

    /** 举报处理状态 */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ReportStatus status = ReportStatus.OPEN;

    /** 举报处理动作 */
    @Enumerated(EnumType.STRING)
    @Column(name = "handle_action", nullable = false, length = 20)
    @Builder.Default
    private ReportHandleAction handleAction = ReportHandleAction.NONE;

    /** 处理说明 */
    @Column(name = "handle_note", length = 500)
    private String handleNote;

    /** 处理人（管理员）ID */
    @Column(name = "handled_by")
    private Long handledBy;

    /** 处理时间 */
    @Column(name = "handled_at")
    private LocalDateTime handledAt;
}
