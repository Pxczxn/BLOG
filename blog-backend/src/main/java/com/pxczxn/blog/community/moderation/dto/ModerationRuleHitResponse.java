




package com.pxczxn.blog.community.moderation.dto;

import com.pxczxn.blog.community.moderation.entity.ModerationRuleHit;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ModerationRuleHitResponse {

    
    private Long id;
    
    private String keywordValue;
    
    private String snippet;
    
    private String severity;

    





    public static ModerationRuleHitResponse from(ModerationRuleHit hit) {
        return ModerationRuleHitResponse.builder()
                .id(hit.getId())
                .keywordValue(hit.getKeywordValue())
                .snippet(hit.getSnippet())
                .severity(hit.getSeverity().name())
                .build();
    }
}
