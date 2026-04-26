/**
 * Flyway 数据库迁移配置
 * <p>
 * 配置数据库迁移工具 Flyway
 */
package com.pxczxn.blog.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * 数据库迁移配置。
 */
@Configuration
@ConditionalOnProperty(name = "spring.flyway.enabled", havingValue = "true", matchIfMissing = true)
public class FlywayConfig {

    /**
     * 创建并初始化 Flyway 数据库迁移工具
     * <p>
     * 配置迁移脚本位置、基线版本等参数，并自动执行数据库迁移。
     * 若遇到失败的迁移记录，会尝试修复后重新迁移。
     *
     * @param dataSource 数据源
     * @return 已初始化的 Flyway 实例
     */
    @Bean
    public Flyway flyway(DataSource dataSource) {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .validateOnMigrate(false)
                .table("flyway_schema_history")
                .baselineVersion("0")
                .cleanDisabled(false)
                .load();

        // 优先执行迁移，遇到已知失败迁移状态时自动修复。
        try {
            flyway.migrate();
        } catch (Exception e) {
            // 修复失败的迁移元数据，然后重试一次。
            if (e.getMessage().contains("failed migration")) {
                flyway.repair();
                flyway.migrate();
            } else {
                throw e;
            }
        }

        return flyway;
    }
}
