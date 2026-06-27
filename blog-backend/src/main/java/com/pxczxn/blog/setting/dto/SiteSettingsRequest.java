package com.pxczxn.blog.setting.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SiteSettingsRequest(
        @NotBlank @Size(max = 80) String siteName,
        @NotBlank @Size(max = 160) String siteSub,
        @NotBlank @Size(max = 80) String adminNick,
        @Size(max = 300) String keywords,
        @Size(max = 500) String description,
        boolean allowGuest,
        @NotBlank @Size(max = 80) String homeTitle,
        @NotBlank @Size(max = 300) String homeIntro,
        @NotBlank @Size(max = 800) String aboutBio,
        @Size(max = 800) String aboutBioSub,
        @Size(max = 300) String frontend,
        @Size(max = 300) String backend,
        @Size(max = 300) String engineering,
        @Size(max = 300) String other
) {
}
