package com.sneakyDateReforged.ms_rdv.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Propage les en-têtes d'authentification et de corrélation aux appels Feign sortants.
 */
@Configuration
public class FeignAuthForwarder {

    @Bean
    public RequestInterceptor authForwarder() {
        return template -> {
            RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
            if (!(attrs instanceof ServletRequestAttributes sra)) {
                return; // pas de contexte HTTP (ex: scheduler)
            }
            var request = sra.getRequest();

            // Authorization -> Feign
            String auth = request.getHeader("Authorization");
            if (auth != null && !auth.isBlank()) {
                template.header("Authorization", auth);
            }

            // (optionnel) X-Request-Id -> Feign (tracing)
            String reqId = request.getHeader("X-Request-Id");
            if (reqId != null && !reqId.isBlank()) {
                template.header("X-Request-Id", reqId);
            }
        };
    }
}
