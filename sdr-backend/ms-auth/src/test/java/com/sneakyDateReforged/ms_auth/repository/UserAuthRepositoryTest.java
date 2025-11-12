package com.sneakyDateReforged.ms_auth.repository;

import com.sneakyDateReforged.ms_auth.model.UserAuthModel;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.TestPropertySource;

import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@EntityScan(basePackageClasses = UserAuthModel.class)
@EnableJpaRepositories(basePackageClasses = UserAuthRepository.class)
@TestPropertySource(properties = {
        // 👉 Désactive toute migration auto
        "spring.flyway.enabled=false",
        "spring.liquibase.enabled=false",
        "spring.sql.init.mode=never",

        // 👉 H2 en mémoire, comportement proche MySQL
        "spring.datasource.url=jdbc:h2:mem:msauth;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",

        // 👉 Laisse Hibernate générer le schéma pour le test
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.show-sql=false"
})
class UserAuthRepositoryTest {

    @Configuration
    static class MinimalCfg {
        // Intentionnellement vide : on réduit au minimum (entités + repo)
    }

    @Autowired
    private UserAuthRepository repo;

    @PersistenceContext
    private EntityManager em;

    private UserAuthModel persist(String email, String pseudo, String steamId, @Nullable String discordId) {
        UserAuthModel u = UserAuthModel.builder()
                .email(email)
                .password("hash")       // champ requis par l’entité
                .pseudo(pseudo)
                .steamId(steamId)
                .discordId(discordId)
                .build();
        em.persist(u);
        em.flush();
        em.clear();
        return u;
    }

    @Test
    void save_shouldGenerateId() {
        UserAuthModel u = persist("a@b.c", "UserA", "STEAM_A", "DISCORD_A");
        assertThat(u.getId()).isNotNull();
    }

    @Nested
    class ExistsBy {
        @Test
        void existsByEmail() {
            persist("x@y.z", "UserX", "STEAM_X", "DISCORD_X");
            assertThat(repo.existsByEmail("x@y.z")).isTrue();
            assertThat(repo.existsByEmail("no@no.no")).isFalse();
        }

        @Test
        void existsByPseudo() {
            persist("p@q.r", "PseudoPQ", "STEAM_PQ", null);
            assertThat(repo.existsByPseudo("PseudoPQ")).isTrue();
            assertThat(repo.existsByPseudo("Other")).isFalse();
        }

        @Test
        void existsBySteamId() {
            persist("s@t.u", "SteamUser", "STEAM_123", null);
            assertThat(repo.existsBySteamId("STEAM_123")).isTrue();
            assertThat(repo.existsBySteamId("STEAM_999")).isFalse();
        }
    }

    @Nested
    class FindBy {
        @Test
        void findByEmail_present() {
            persist("present@mail.com", "U1", "S1", null);
            Optional<UserAuthModel> found = repo.findByEmail("present@mail.com");
            assertThat(found).isPresent();
            assertThat(found.get().getPseudo()).isEqualTo("U1");
        }

        @Test
        void findByEmail_absent() {
            assertThat(repo.findByEmail("absent@mail.com")).isNotPresent();
        }

        @Test
        void findByPseudo() {
            persist("p@mail.com", "PseudoA", "S2", null);
            assertThat(repo.findByPseudo("PseudoA")).isPresent();
            assertThat(repo.findByPseudo("Nope")).isNotPresent();
        }

        @Test
        void findBySteamId() {
            persist("s@mail.com", "U2", "STEAM_U2", "D2");
            assertThat(repo.findBySteamId("STEAM_U2")).isPresent();
            assertThat(repo.findBySteamId("NONE")).isNotPresent();
        }

        @Test
        void findByDiscordId() {
            persist("d@mail.com", "U3", "S3", "DISCORD_3");
            assertThat(repo.findByDiscordId("DISCORD_3")).isPresent();
            assertThat(repo.findByDiscordId("UNKNOWN")).isNotPresent();
        }
    }
}
