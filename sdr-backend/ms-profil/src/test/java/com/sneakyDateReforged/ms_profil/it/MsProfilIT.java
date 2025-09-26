//package com.sneakyDateReforged.ms_profil.it;
//
//import com.github.tomakehurst.wiremock.WireMockServer;
//import com.sneakyDateReforged.ms_profil.model.Profile;
//import com.sneakyDateReforged.ms_profil.repository.ProfileRepository;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.RequestBuilder;
//import org.testcontainers.containers.MySQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
////import static com.github.tomakehurst.wiremock.client.WireMock.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
//import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
//import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
//
//import java.nio.charset.StandardCharsets;
//
//
//// Démarre MySQL dans un conteneur + l'app Spring complète (port aléatoire)
//// et utilise MockMvc pour faire des requêtes HTTP sur le contexte.
//@Testcontainers
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureMockMvc
//@ActiveProfiles("test")
//@TestPropertySource(properties = {
//        "spring.config.import=optional:",
//        "spring.cloud.config.enabled=false",
//        "spring.cloud.config.fail-fast=false",
//        "eureka.client.enabled=false",
//        "spring.cloud.discovery.enabled=false"
//})
//class MsProfilIT {
//
//    // MySQL jetable pour le test
//    @Container
//    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");
//
//    // WireMock pour simuler ms-friend / ms-rdv / ms-auth
//    static WireMockServer friendMock = new WireMockServer(0);
//    static WireMockServer rdvMock    = new WireMockServer(0);
//    static WireMockServer authMock   = new WireMockServer(0);
//
//    // Propriétés dynamiques injectées dans le contexte Spring de test
//    @DynamicPropertySource
//    static void props(DynamicPropertyRegistry r) {
//        r.add("spring.config.import", () -> "");
//        r.add("spring.cloud.config.enabled", () -> "false");
//        r.add("spring.cloud.config.fail-fast", () -> "false");
//        r.add("eureka.client.enabled", () -> "false");
//        r.add("spring.cloud.discovery.enabled", () -> "false");
//
//        // Datasource -> Testcontainers
//        r.add("spring.datasource.url", mysql::getJdbcUrl);
//        r.add("spring.datasource.username", mysql::getUsername);
//        r.add("spring.datasource.password", mysql::getPassword);
//
//        // secret JWT utilisé par JwtUtils dans l'app
//        r.add("jwt.secret", () -> TEST_JWT_SECRET);
//
//        // Démarre WireMock et route Feign vers WireMock (grâce aux url=${...} sur @FeignClient)
//        friendMock.start(); rdvMock.start(); authMock.start();
//        r.add("ms-friend.url", () -> "http://localhost:" + friendMock.port());
//        r.add("ms-rdv.url",    () -> "http://localhost:" + rdvMock.port());
//        r.add("ms-auth.url",   () -> "http://localhost:" + authMock.port());
//    }
//
//    private static final String TEST_JWT_SECRET =
//            "exam_secret_key_which_is_long_enough_1234567890_abcdefgh"; // >= 32 bytes
//
//    @Autowired MockMvc mvc;
//    @Autowired ProfileRepository repo;
//
//    @BeforeEach
//    void stubs() {
//        friendMock.resetAll();
//        rdvMock.resetAll();
//        authMock.resetAll();
//
//        friendMock.stubFor(
//                com.github.tomakehurst.wiremock.client.WireMock.get(urlPathEqualTo("/public/friends/1/count"))
//                        .willReturn(okJson("{\"count\":3}"))
//        );
//
//        rdvMock.stubFor(
//                com.github.tomakehurst.wiremock.client.WireMock.get(urlPathEqualTo("/public/rdv/stats/1"))
//                        .willReturn(okJson("{\"total\":5,\"confirmes\":2,\"annules\":1,\"participations\":2}"))
//        );
//
//        rdvMock.stubFor(
//                com.github.tomakehurst.wiremock.client.WireMock.get(urlPathEqualTo("/public/rdv/next/1"))
//                        .willReturn(okJson("{\"nextDate\":null}"))
//        );
//
//        friendMock.stubFor(
//                com.github.tomakehurst.wiremock.client.WireMock.get(urlPathEqualTo("/friends/42/count"))
//                        .willReturn(okJson("{\"count\":7}"))
//        );
//
//        rdvMock.stubFor(
//                com.github.tomakehurst.wiremock.client.WireMock.get(urlPathEqualTo("/rdv/stats/42"))
//                        .willReturn(okJson("{\"total\":10,\"confirmes\":8,\"annules\":1,\"participations\":1}"))
//        );
//
//        rdvMock.stubFor(
//                com.github.tomakehurst.wiremock.client.WireMock.get(urlPathEqualTo("/rdv/next/42"))
//                        .willReturn(okJson("{\"nextDate\":null}"))
//        );
//
//        authMock.stubFor(
//                com.github.tomakehurst.wiremock.client.WireMock.get(urlPathMatching("/auth/.*"))
//                        .willReturn(okJson("[]"))
//        );
//
//    }
//
//    @Test
//    void public_full_end_to_end() throws Exception {
//        // Prépare un profil en base
//        repo.save(Profile.builder().userId(1L).displayName("Coco").build());
//
//        mvc.perform((RequestBuilder) get("/profiles/1/public-full"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.userId").value(1))
//                .andExpect(jsonPath("$.nombreAmis").value(3))
//                .andExpect(jsonPath("$.statsRDV.total").value(5));
//    }
//
//    @Test
//    void me_full_with_jwt_end_to_end() throws Exception {
//        // Profil pour userId=42
//        repo.save(Profile.builder().userId(42L).displayName("Me").build());
//
//        String jwt = jwtFor("me@example.com", 42L, "USER");
//        mvc.perform(get("/profiles/me/full")
//                        .header("Authorization","Bearer " + jwt))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.userId").value(42))
//                .andExpect(jsonPath("$.nombreAmis").value(7))
//                .andExpect(jsonPath("$.statsRDV.total").value(10));
//    }
//
//    private String jwtFor(String email, long id, String role) {
//        var key = Keys.hmacShaKeyFor(TEST_JWT_SECRET.getBytes(StandardCharsets.UTF_8));
//        return Jwts.builder()
//                .setSubject(email)
//                .claim("id", id)
//                .claim("role", role)
//                .signWith(key)
//                .compact();
//    }
//}
