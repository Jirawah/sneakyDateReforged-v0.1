//package com.sneakyDateReforged.ms_profil.it;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
///**
// * Vérifie que le filtre corrélation ajoute/échoue X-Request-Id.
// * On tape /actuator/health car il est permitAll.
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
//class CorrelationIdFilterIT {
//
//    @Autowired MockMvc mvc;
//
//    @Test
//    void adds_request_id_if_missing() throws Exception {
//        mvc.perform(get("/actuator/health"))
//                .andExpect(status().isOk())
//                .andExpect(header().exists("X-Request-Id")); // généré par le filtre
//    }
//
//    @Test
//    void echoes_request_id_if_provided() throws Exception {
//        mvc.perform(get("/actuator/health").header("X-Request-Id","abc-123"))
//                .andExpect(status().isOk())
//                .andExpect(header().string("X-Request-Id","abc-123")); // écho exact
//    }
//}
