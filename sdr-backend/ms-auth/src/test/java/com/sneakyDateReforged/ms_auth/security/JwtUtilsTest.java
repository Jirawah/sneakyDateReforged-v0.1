package com.sneakyDateReforged.ms_auth.security;

import com.sneakyDateReforged.ms_auth.model.UserAuthModel;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;

import java.lang.reflect.Field;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    private final String secret = "ThisIsASecretKeyForJWTsAndMustBe256BitsLong!"; // même que config
    private final long expiration = 3600000; // 1 heure

    @BeforeEach
    void setUp() throws Exception {
        jwtUtils = new JwtUtils();

        // Injecter manuellement les valeurs @Value
        Field secretField = JwtUtils.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(jwtUtils, secret);

        Field expField = JwtUtils.class.getDeclaredField("expirationMs");
        expField.setAccessible(true);
        expField.set(jwtUtils, expiration);

        // Appel à @PostConstruct
        jwtUtils.init();
    }

    @Test
    void generateToken_shouldContainCorrectSubjectAndClaims() {
        UserAuthModel user = UserAuthModel.builder()
                .id(42L)
                .email("test@email.com")
                .role("USER")
                .build();

        String token = jwtUtils.generateToken(user);

        assertNotNull(token);
        assertEquals("test@email.com", jwtUtils.extractUsername(token));
    }

    @Test
    void isTokenValid_shouldReturnTrue_forValidToken() {
        UserAuthModel user = UserAuthModel.builder()
                .id(1L)
                .email("valid@email.com")
                .role("ADMIN")
                .build();

        String token = jwtUtils.generateToken(user);

        User userDetails = new User("valid@email.com", "pwd", Collections.emptyList());

        assertTrue(jwtUtils.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenValid_shouldReturnFalse_ifUsernameMismatch() {
        UserAuthModel user = UserAuthModel.builder()
                .id(1L)
                .email("wrong@email.com")
                .role("ADMIN")
                .build();

        String token = jwtUtils.generateToken(user);

        User userDetails = new User("expected@email.com", "pwd", Collections.emptyList());

        assertFalse(jwtUtils.isTokenValid(token, userDetails));
    }

//    @Test
//    void isTokenValid_shouldReturnFalse_ifTokenExpired() throws Exception {
//        JwtUtils shortLivedJwt = new JwtUtils();
//
//        // Réglage : expiration 1 ms
//        Field secretField = JwtUtils.class.getDeclaredField("secret");
//        secretField.setAccessible(true);
//        secretField.set(shortLivedJwt, secret);
//
//        Field expField = JwtUtils.class.getDeclaredField("expirationMs");
//        expField.setAccessible(true);
//        expField.set(shortLivedJwt, 1L);
//
//        shortLivedJwt.init();
//
//        UserAuthModel user = UserAuthModel.builder()
//                .id(7L)
//                .email("expired@email.com")
//                .role("USER")
//                .build();
//
//        String token = shortLivedJwt.generateToken(user);
//
//        Thread.sleep(5); // Laisse le temps d’expirer
//
//        User userDetails = new User("expired@email.com", "pwd", Collections.emptyList());
//
//        assertFalse(shortLivedJwt.isTokenValid(token, userDetails));
//    }
@Test
void isTokenValid_shouldReturnFalse_ifTokenExpired_immediately() throws Exception {
    JwtUtils expJwt = new JwtUtils();

    // secret identique
    var secretField = JwtUtils.class.getDeclaredField("secret");
    secretField.setAccessible(true);
    secretField.set(expJwt, "ThisIsASecretKeyForJWTsAndMustBe256BitsLong!");

    // expiration négative => déjà expiré
    var expField = JwtUtils.class.getDeclaredField("expirationMs");
    expField.setAccessible(true);
    expField.set(expJwt, -1000L);

    expJwt.init();

    var user = com.sneakyDateReforged.ms_auth.model.UserAuthModel.builder()
            .email("expired@email.com").role("USER").id(7L).build();

    var token = expJwt.generateToken(user);
    var userDetails = new org.springframework.security.core.userdetails.User(
            "expired@email.com", "pwd", java.util.Collections.emptyList());

    assertFalse(expJwt.isTokenValid(token, userDetails));
}

    @Test
    void extractUsername_shouldThrowException_forMalformedToken() {
        assertThrows(MalformedJwtException.class, () -> jwtUtils.extractUsername("not.a.valid.token"));
    }
}
