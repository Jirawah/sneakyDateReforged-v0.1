package com.sneakyDateReforged.ms_rdv.config;

import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;

/**
 * Dev only: runs flyway.repair() before standard migrate()
 */
@Configuration
@Profile("dev")
public class FlywayRepairConfig {

    @Bean
    public FlywayMigrationStrategy repairThenMigrate() {
        return (Flyway flyway) -> {
            // Ne supprime rien, corrige l'historique et checksums
            flyway.repair();
            flyway.migrate();
        };
    }
}
