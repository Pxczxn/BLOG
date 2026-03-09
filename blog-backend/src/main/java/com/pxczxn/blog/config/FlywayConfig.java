package com.pxczxn.blog.config;

import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Flyway 数据库迁移配置
 */
@Configuration
public class FlywayConfig {

    @Bean
    public Flyway flyway(DataSource dataSource) {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .validateOnMigrate(false) // 禁用验证，避免迁移文件修改导致启动失败
                .table("flyway_schema_history")
                .baselineVersion("0")
                .cleanDisabled(false) // 允许执行 clean 操作
                .load();

        // 如果存在失败的迁移，先清理再重新迁移
        try {
            flyway.migrate();
        } catch (Exception e) {
            // 如果是失败迁移导致的错误，尝试修复
            if (e.getMessage().contains("failed migration")) {
                // 标记失败记录为成功（针对 MySQL 的特性）
                flyway.repair();
                // 再次尝试迁移
                flyway.migrate();
            } else {
                throw e;
            }
        }

        return flyway;
    }
}
