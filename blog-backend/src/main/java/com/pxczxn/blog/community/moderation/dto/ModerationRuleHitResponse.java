/**
 * 规则命中响应 DTO
 * <p>
 * 返回审核任务中关键词规则的命中详情
 */
package com.pxczxn.blog.community.moderation.dto;

import com.pxczxn.blog.community.moderation.entity.ModerationRuleHit;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ModerationRuleHitResponse {

    /** 命中记录 ID */
    private Long id;
    /** 命中的关键词 */
    private String keywordValue;
    /** 命中上下文片段 */
    private String snippet;
    /** 严重级别 */
    private String severity;

    /**
     * 从实体转换为响应对象
     *
     * @param hit 规则命中记录
     * @return 响应对象
     */
    public static ModerationRuleHitResponse from(ModerationRuleHit hit) {
        return ModerationRuleHitResponse.builder()
                .id(hit.getId())
                .keywordValue(hit.getKeywordValue())
                .snippet(hit.getSnippet())
                .severity(hit.getSeverity().name())
                .build();
    }
}
