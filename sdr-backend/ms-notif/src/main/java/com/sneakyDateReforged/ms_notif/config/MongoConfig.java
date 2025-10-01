package com.sneakyDateReforged.ms_notif.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;

@Configuration
@RequiredArgsConstructor
public class MongoConfig {

    private final MongoTemplate mongoTemplate;

    /**
     * Crée les index nécessaires au démarrage :
     * - Compound: (userId, status, createdAt desc) -> listes utilisateur performantes
     * - RDV: (rdvId, createdAt desc) -> recherche par rdv
     * - Idempotence: eventId unique
     * - TTL: expiresAt (expireAfterSeconds=0 -> TTL basé sur la valeur du champ)
     */
    @Bean
    public ApplicationRunner createIndexesAtStartup() {
        return args -> {
            IndexOperations ops = mongoTemplate.indexOps("notifications");

            // userId + status + createdAt desc
            ops.ensureIndex(
                    new Index()
                            .on("userId", Sort.Direction.ASC)
                            .on("status", Sort.Direction.ASC)
                            .on("createdAt", Sort.Direction.DESC)
                            .named("idx_user_status_created")
            );

            // rdvId + createdAt desc
            ops.ensureIndex(
                    new Index()
                            .on("rdvId", Sort.Direction.ASC)
                            .on("createdAt", Sort.Direction.DESC)
                            .named("idx_rdv_created")
            );

            // eventId unique (idempotence)
            ops.ensureIndex(
                    new Index()
                            .on("eventId", Sort.Direction.ASC)
                            .unique()
                            .named("uk_event_id")
            );

            // TTL basé sur expiresAt (expire à la date indiquée)
            ops.ensureIndex(
                    new Index()
                            .on("expiresAt", Sort.Direction.ASC)
                            .expire(0) // 0 -> expire quand expiresAt est atteint
                            .named("ttl_expires_at")
            );
        };
    }
}
