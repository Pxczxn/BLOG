package com.pxczxn.blog.setting.controller;

import com.pxczxn.blog.common.response.Result;
import com.pxczxn.blog.setting.dto.SiteSettingsRequest;
import com.pxczxn.blog.setting.dto.SiteSettingsResponse;
import com.pxczxn.blog.setting.service.SiteSettingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SiteSettingController {

    private final SiteSettingService siteSettingService;

    @GetMapping("/api/public/site-settings")
    public Result<SiteSettingsResponse> publicSettings() {
        return Result.success(siteSettingService.getSettings());
    }

    @GetMapping("/api/admin/site-settings")
    public Result<SiteSettingsResponse> adminSettings() {
        return Result.success(siteSettingService.getSettings());
    }

    @PutMapping("/api/admin/site-settings")
    public Result<SiteSettingsResponse> updateSettings(@Valid @RequestBody SiteSettingsRequest request) {
        return Result.success(siteSettingService.updateSettings(request));
    }
}
