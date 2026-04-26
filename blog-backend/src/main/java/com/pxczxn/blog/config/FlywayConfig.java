




package com.pxczxn.blog.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;




@Configuration
@ConditionalOnProperty(name = "spring.flyway.enabled", havingValue = "true", matchIfMissing = true)
public class FlywayConfig {

    








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

        
        try {
            flyway.migrate();
        } catch (Exception e) {
            
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
