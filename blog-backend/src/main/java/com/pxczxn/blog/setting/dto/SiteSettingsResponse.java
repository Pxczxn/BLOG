package com.pxczxn.blog.setting.dto;

public record SiteSettingsResponse(
        String siteName,
        String siteSub,
        String adminNick,
        String keywords,
        String description,
        boolean allowGuest,
        String homeTitle,
        String homeIntro,
        String aboutBio,
        String aboutBioSub,
        String frontend,
        String backend,
        String engineering,
        String other
) {
}
