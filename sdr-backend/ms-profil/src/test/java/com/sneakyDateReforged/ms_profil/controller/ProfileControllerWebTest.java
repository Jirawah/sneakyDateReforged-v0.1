package com.sneakyDateReforged.ms_profil.controller;

import com.sneakyDateReforged.ms_profil.config.CorsConfig;
import com.sneakyDateReforged.ms_profil.config.SecurityConfig;
import com.sneakyDateReforged.ms_profil.dto.AggregatedProfileDTO;
import com.sneakyDateReforged.ms_profil.dto.ProfileDTO;
import com.sneakyDateReforged.ms_profil.dto.ProfileUpdateDTO;
import com.sneakyDateReforged.ms_profil.exception.GlobalExceptionHandler;
import com.sneakyDateReforged.ms_profil.security.JwtAuthFilter;
import com.sneakyDateReforged.ms_profil.security.JwtUtils;
import com.sneakyDateReforged.ms_profil.service.ProfileService;
import com.sneakyDateReforged.ms_profil.service.UserContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Slice Web : on charge seulement le contrôleur + sécu + CORS + handler d'erreurs.
 * Tout le métier (service, jwt) est mocké.
 */
@WebMvcTest(controllers = ProfileController.class)
@Import({SecurityConfig.class, JwtAuthFilter.class, CorsConfig.class, GlobalExceptionHandler.class})
class ProfileControllerWebTest {

    @Autowired
    MockMvc mvc;

    // Beans métier/sécu mockés pour le slice
    @MockBean ProfileService service;
    @MockBean JwtUtils jwtUtils;
    @MockBean UserContext userCtx;

    /* ------------------ PUBLIC ------------------ */

    @Test
    void public_endpoint_is_permitAll() throws Exception {
        when(service.getPublicView(1L))
                .thenReturn(ProfileDTO.builder().userId(1L).displayName("Coco").build());

        mvc.perform(get("/profiles/1/public"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.displayName").value("Coco"));

        // pas d’appel userCtx sur ce chemin
        verifyNoInteractions(userCtx);
    }

    /* ------------------ SECURITY ------------------ */

    @Test
    void me_requires_auth_when_no_jwt() throws Exception {
        mvc.perform(get("/profiles/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void me_succeeds_with_valid_jwt() throws Exception {
        // le filtre accepte le token
        when(jwtUtils.isSignatureValid("good")).thenReturn(true);
        when(jwtUtils.extractUsername("good")).thenReturn("me@example.com");
        when(jwtUtils.extractUserId("good")).thenReturn(42L);
        when(jwtUtils.extractRole("good")).thenReturn("USER");

        // le contrôleur lit UserContext -> on le stub
        when(userCtx.getUserId()).thenReturn(42L);
        when(userCtx.getEmail()).thenReturn("me@example.com");

        when(service.getOrCreateFor(42L, "me@example.com"))
                .thenReturn(ProfileDTO.builder().userId(42L).displayName("Me").build());

        mvc.perform(get("/profiles/me")
                        .header("Authorization", "Bearer good"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(42))
                .andExpect(jsonPath("$.displayName").value("Me"));
    }

    @Test
    void me_full_requires_auth_when_no_jwt() throws Exception {
        mvc.perform(get("/profiles/me/full"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void me_full_succeeds_with_valid_jwt() throws Exception {
        when(jwtUtils.isSignatureValid("good")).thenReturn(true);
        when(jwtUtils.extractUsername("good")).thenReturn("me@example.com");
        when(jwtUtils.extractUserId("good")).thenReturn(42L);
        when(jwtUtils.extractRole("good")).thenReturn("USER");

        when(userCtx.getUserId()).thenReturn(42L);

        when(service.getAggregatedView(42L))
                .thenReturn(AggregatedProfileDTO.builder()
                        .userId(42L).pseudo("Me").build());

        mvc.perform(get("/profiles/me/full")
                        .header("Authorization","Bearer good"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(42))
                .andExpect(jsonPath("$.pseudo").value("Me"));
    }

    /* ------------------ VALIDATION ------------------ */

    @Test
    void update_validation_error_returns_400() throws Exception {
        // Pour atteindre la validation, on doit passer la sécurité
        when(jwtUtils.isSignatureValid("good")).thenReturn(true);
        when(jwtUtils.extractUsername("good")).thenReturn("me@example.com");
        when(jwtUtils.extractUserId("good")).thenReturn(42L);
        when(jwtUtils.extractRole("good")).thenReturn("USER");
        when(userCtx.getUserId()).thenReturn(42L);

        // displayName trop court -> @Size(min=2)
        String badJson = """
          {"displayName":"A","bio":"ok","country":"FR","languages":"fr,en","age":20}
        """;

        mvc.perform(put("/profiles/me")
                        .header("Authorization","Bearer good")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", anyOf(is("Validation"), notNullValue())))
                .andExpect(jsonPath("$.status", is(400)));
    }

    /* ------------------ CORS (optionnel) ------------------ */

    @Test
    void cors_preflight_is_allowed_for_public() throws Exception {
        mvc.perform(options("/profiles/1/public")
                        .header("Origin", "http://localhost:4200")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"));
    }
}
