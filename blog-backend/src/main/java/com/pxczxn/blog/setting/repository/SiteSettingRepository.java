package com.pxczxn.blog.setting.repository;

import com.pxczxn.blog.setting.entity.SiteSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SiteSettingRepository extends JpaRepository<SiteSetting, String> {
}
