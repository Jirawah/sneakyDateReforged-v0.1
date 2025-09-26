//package com.sneakyDateReforged.ms_profil.it;
//
//import com.sneakyDateReforged.ms_profil.model.Profile;
//import com.sneakyDateReforged.ms_profil.repository.ProfileRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
///**
// * Vérifie que le préflight OPTIONS fonctionne pour une route publique
// * et que le GET depuis Origin autorisée renvoie le header CORS.
// */
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ActiveProfiles("test")
//@AutoConfigureMockMvc
//@TestPropertySource(properties = {
//        "spring.config.import=optional:",
//        "spring.cloud.config.enabled=false",
//        "spring.cloud.config.fail-fast=false",
//        "eureka.client.enabled=false",
//        "spring.cloud.discovery.enabled=false"
//})
//class CorsIT {
//
//    @Autowired MockMvc mvc;
//    @Autowired ProfileRepository repo;
//
//    @Test
//    void preflight_ok_for_public_endpoint() throws Exception {
//        mvc.perform(options("/profiles/1/public")
//                        .header("Origin", "http://localhost:4200")
//                        .header("Access-Control-Request-Method", "GET"))
//                .andExpect(status().isOk())
//                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"));
//    }
//
//    @Test
//    void get_public_includes_cors_header_when_origin_allowed() throws Exception {
//        // s'assure que l'utilisateur existe
//        repo.save(Profile.builder().userId(1L).displayName("Coco").build());
//
//        mvc.perform(get("/profiles/1/public")
//                        .header("Origin", "http://localhost:4200")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"));
//    }
//}
