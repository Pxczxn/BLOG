/**
 * 审核 AI 建议记录
 * <p>
 * 封装 AI 审核模型的返回结果，包括是否强制拒绝、原因和建议的风险级别
 */
package com.pxczxn.blog.community.moderation.service.ai;

import com.pxczxn.blog.community.moderation.entity.ModerationRiskLevel;

public record ModerationAiAdvice(
        /** 是否强制拒绝 */
        boolean forceReject,
        /** 拒绝原因 */
        String reason,
        /** 建议的风险级别 */
        ModerationRiskLevel suggestedRisk
) {
    /**
     * 创建空建议（不执行任何动作）
     *
     * @return 无操作的审核建议
     */
    public static ModerationAiAdvice noop() {
        return new ModerationAiAdvice(false, null, null);
    }
}
