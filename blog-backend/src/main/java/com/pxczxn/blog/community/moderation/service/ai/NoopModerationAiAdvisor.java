





package com.pxczxn.blog.community.moderation.service.ai;

import com.pxczxn.blog.community.moderation.entity.ModerationContentType;
import org.springframework.stereotype.Component;

@Component
public class NoopModerationAiAdvisor implements ModerationAiAdvisor {

    






    @Override
    public ModerationAiAdvice advise(ModerationContentType contentType, String sourceText) {
        return ModerationAiAdvice.noop();
    }
}
