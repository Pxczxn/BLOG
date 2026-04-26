




package com.pxczxn.blog.community.moderation.service.ai;

import com.pxczxn.blog.community.moderation.entity.ModerationContentType;

public interface ModerationAiAdvisor {

    






    ModerationAiAdvice advise(ModerationContentType contentType, String sourceText);
}
