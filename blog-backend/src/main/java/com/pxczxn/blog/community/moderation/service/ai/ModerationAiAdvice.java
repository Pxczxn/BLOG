




package com.pxczxn.blog.community.moderation.service.ai;

import com.pxczxn.blog.community.moderation.entity.ModerationRiskLevel;

public record ModerationAiAdvice(
        
        boolean forceReject,
        
        String reason,
        
        ModerationRiskLevel suggestedRisk
) {
    




    public static ModerationAiAdvice noop() {
        return new ModerationAiAdvice(false, null, null);
    }
}
