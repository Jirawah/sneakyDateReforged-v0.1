//package com.sneakyDateReforged.ms_auth.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sneakyDateReforged.ms_auth.dto.AuthResponseDTO;
//import com.sneakyDateReforged.ms_auth.service.AuthService;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
//import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.Map;
//
//import static org.hamcrest.Matchers.equalTo;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.verifyNoMoreInteractions;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(
//        controllers = AuthController.class,
//        excludeAutoConfiguration = {
//                SecurityAutoConfiguration.class,
//                SecurityFilterAutoConfiguration.class
//        }
//)
//@AutoConfigureMockMvc(addFilters = false) // pas de filtres Spring Security
//@TestPropertySource(properties = {
//        "spring.main.allow-bean-definition-overriding=true",
//        "spring.cloud.config.enabled=false",   // au cas où
//        "eureka.client.enabled=false"          // au cas où
//})
//class AuthControllerTest {
//
//    @Autowired MockMvc mockMvc;
//    @Autowired ObjectMapper om;
//
//    @MockBean AuthService authService; // on mocke le service appelé par le contrôleur
//
//    @Test
//    @DisplayName("POST /auth/register -> 201 + message")
//    void register_shouldReturn201_andCallService() throws Exception {
//        String reqJson = """
//                {
//                  "pseudo":"TestUser",
//                  "email":"test@example.com",
//                  "steamId":"123456789",
//                  "password":"Password123",
//                  "confirmPassword":"Password123",
//                  "discordId":"123456789987654321"
//                }
//                """;
//
//        mockMvc.perform(post("/auth/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(reqJson))
//                .andExpect(status().isCreated())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.message", equalTo("Inscription réussie. Tu peux te connecter.")));
//
//        verify(authService).register(any());
//        verifyNoMoreInteractions(authService);
//    }
//
//    @Test
//    @DisplayName("POST /auth/login -> 200 + AuthResponseDTO")
//    void login_shouldReturn200_withAuthResponse() throws Exception {
//        String reqJson = """
//                {"email":"test@example.com","password":"Password123"}
//                """;
//
//        var response = AuthResponseDTO.builder()
//                .token("jwt-token-abc")
//                .steamPseudo("SteamUser")
//                .steamAvatar("http://avatar.url")
//                .gamesHours(Map.of("PUBG", 123, "Rust", 45))
//                .build();
//
//        Mockito.when(authService.login(any())).thenReturn(response);
//
//        mockMvc.perform(post("/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(reqJson))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.token", equalTo("jwt-token-abc")))
//                .andExpect(jsonPath("$.steamPseudo", equalTo("SteamUser")))
//                .andExpect(jsonPath("$.gamesHours.PUBG", equalTo(123)));
//
//        verify(authService).login(any());
//        verifyNoMoreInteractions(authService);
//    }
//
//    @Test
//    @DisplayName("POST /auth/discord-sync -> 200 + message")
//    void discordSync_shouldReturn200_andCallService() throws Exception {
//        String reqJson = """
//                {
//                  "discordId":"123456789987654321",
//                  "discordUsername":"DiscordUser",
//                  "discordAvatarUrl":"http://avatar.url",
//                  "chosenPseudo":"MyNick"
//                }
//                """;
//
//        mockMvc.perform(post("/auth/discord-sync")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(reqJson))
//                .andExpect(status().isOk())
//                .andExpect(content().string("✅ Connexion Discord synchronisée avec succès."));
//
//        verify(authService).syncDiscord(any());
//        verifyNoMoreInteractions(authService);
//    }
//}
