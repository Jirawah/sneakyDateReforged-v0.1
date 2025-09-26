package com.sneakyDateReforged.ms_rdv;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Base de tests d'intégration avec Testcontainers (MySQL).
 * Étends cette classe dans tes @SpringBootTest si besoin.
 */
@Testcontainers
@TestInstance(Lifecycle.PER_CLASS)
public abstract class TestcontainersConfiguration {

	@Container
	static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0")
			.withDatabaseName("msrdvdb")
			.withUsername("msrdvuser")
			.withPassword("msrdvpwd");

	@DynamicPropertySource
	static void registerProps(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
		registry.add("spring.datasource.username", MYSQL::getUsername);
		registry.add("spring.datasource.password", MYSQL::getPassword);
		registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");

		// Laisse Flyway piloter le schéma si tes migrations sont dispo en test classpath
		registry.add("spring.flyway.enabled", () -> "true");
		registry.add("spring.jpa.hibernate.ddl-auto", () -> "none"); // pas de génération Hibernate, Flyway fait le job
	}
}
