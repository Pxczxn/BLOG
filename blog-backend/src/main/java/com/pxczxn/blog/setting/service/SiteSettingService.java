package com.pxczxn.blog.setting.service;

import com.pxczxn.blog.setting.dto.SiteSettingsRequest;
import com.pxczxn.blog.setting.dto.SiteSettingsResponse;
import com.pxczxn.blog.setting.entity.SiteSetting;
import com.pxczxn.blog.setting.repository.SiteSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SiteSettingService {

    private static final Map<String, String> DEFAULTS = new LinkedHashMap<>();

    static {
        DEFAULTS.put("siteName", "破星辰只寻你");
        DEFAULTS.put("siteSub", "在代码的星河中，寻找技术与自由");
        DEFAULTS.put("adminNick", "破星辰只寻你");
        DEFAULTS.put("keywords", "博客,前端,React,Java,Spring Boot,AI Coding,个人网站");
        DEFAULTS.put("description", "一个记录真实项目、踩坑复盘、工程实践与持续构建过程的个人博客。");
        DEFAULTS.put("allowGuest", "false");
        DEFAULTS.put("homeTitle", "探索技术边界");
        DEFAULTS.put("homeIntro", "分享前端开发、系统设计与工程化实践，也记录一个开发者不断打磨作品的过程。");
        DEFAULTS.put("aboutBio", "一个正在学习与探索 AI Coding 的全栈开发者。这里会记录技术、设计和持续构建过程中的思考与实践。");
        DEFAULTS.put("aboutBioSub", "这个博客会更偏向真实项目、踩坑记录、审美迭代，以及把想法真正落成作品的过程。");
        DEFAULTS.put("frontend", "React / Vue / TypeScript / Tailwind CSS");
        DEFAULTS.put("backend", "Java / Spring Boot / Node.js");
        DEFAULTS.put("engineering", "Vite / Webpack / Git");
        DEFAULTS.put("other", "MySQL / Redis / Linux");
    }

    private final SiteSettingRepository siteSettingRepository;

    @Transactional(readOnly = true)
    public SiteSettingsResponse getSettings() {
        Map<String, String> values = new LinkedHashMap<>(DEFAULTS);
        values.putAll(siteSettingRepository.findAll().stream()
                .collect(Collectors.toMap(SiteSetting::getKey, SiteSetting::getValue, (a, b) -> b)));
        return toResponse(values::get);
    }

    @Transactional
    public SiteSettingsResponse updateSettings(SiteSettingsRequest request) {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("siteName", clean(request.siteName(), DEFAULTS.get("siteName")));
        values.put("siteSub", clean(request.siteSub(), DEFAULTS.get("siteSub")));
        values.put("adminNick", clean(request.adminNick(), DEFAULTS.get("adminNick")));
        values.put("keywords", clean(request.keywords(), ""));
        values.put("description", clean(request.description(), ""));
        values.put("allowGuest", String.valueOf(request.allowGuest()));
        values.put("homeTitle", clean(request.homeTitle(), DEFAULTS.get("homeTitle")));
        values.put("homeIntro", clean(request.homeIntro(), DEFAULTS.get("homeIntro")));
        values.put("aboutBio", clean(request.aboutBio(), DEFAULTS.get("aboutBio")));
        values.put("aboutBioSub", clean(request.aboutBioSub(), ""));
        values.put("frontend", clean(request.frontend(), ""));
        values.put("backend", clean(request.backend(), ""));
        values.put("engineering", clean(request.engineering(), ""));
        values.put("other", clean(request.other(), ""));

        values.forEach((key, value) -> {
            SiteSetting setting = siteSettingRepository.findById(key)
                    .orElseGet(() -> SiteSetting.builder().key(key).build());
            setting.setValue(value);
            siteSettingRepository.save(setting);
        });
        return toResponse(values::get);
    }

    private SiteSettingsResponse toResponse(Function<String, String> value) {
        return new SiteSettingsResponse(
                value.apply("siteName"),
                value.apply("siteSub"),
                value.apply("adminNick"),
                value.apply("keywords"),
                value.apply("description"),
                Boolean.parseBoolean(value.apply("allowGuest")),
                value.apply("homeTitle"),
                value.apply("homeIntro"),
                value.apply("aboutBio"),
                value.apply("aboutBioSub"),
                value.apply("frontend"),
                value.apply("backend"),
                value.apply("engineering"),
                value.apply("other")
        );
    }

    private String clean(String value, String fallback) {
        if (value == null) {
            return fallback;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? fallback : trimmed;
    }
}
