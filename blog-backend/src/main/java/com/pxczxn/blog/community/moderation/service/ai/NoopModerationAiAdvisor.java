/**
 * 空 AI 审核顾问实现
 * <p>
 * 默认实现，不进行任何实际的 AI 审核，直接返回空建议。
 * 可在需要时替换为真实的 AI 审核实现。
 */
package com.pxczxn.blog.community.moderation.service.ai;

import com.pxczxn.blog.community.moderation.entity.ModerationContentType;
import org.springframework.stereotype.Component;

@Component
public class NoopModerationAiAdvisor implements ModerationAiAdvisor {

    /**
     * 返回空建议，不进行实际审核
     *
     * @param contentType 内容类型
     * @param sourceText 待审核文本
     * @return 空审核建议
     */
    @Override
    public ModerationAiAdvice advise(ModerationContentType contentType, String sourceText) {
        return ModerationAiAdvice.noop();
    }
}
