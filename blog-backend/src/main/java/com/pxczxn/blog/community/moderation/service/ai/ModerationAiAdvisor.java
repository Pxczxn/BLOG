/**
 * 审核 AI 顾问接口
 * <p>
 * 定义 AI 审核能力的标准接口，用于对内容进行智能审核分析
 */
package com.pxczxn.blog.community.moderation.service.ai;

import com.pxczxn.blog.community.moderation.entity.ModerationContentType;

public interface ModerationAiAdvisor {

    /**
     * 对内容进行审核分析
     *
     * @param contentType 内容类型
     * @param sourceText 待审核文本
     * @return 审核建议
     */
    ModerationAiAdvice advise(ModerationContentType contentType, String sourceText);
}
