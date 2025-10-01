package com.sneakyDateReforged.ms_rdv.infra.notif;

import feign.RequestInterceptor;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * Interceptor dédié au client ms-notif :
 * - ajoute X-Internal-Token (clé S2S)
 * - propage X-Request-Id si présent (tracing)
 *
 * ⚠️ Coexiste sans conflit avec ton FeignAuthForwarder global
 *     (qui propage l'Authorization). Les deux intercepteurs s'exécutent.
 */
public class NotifFeignConfig {

    @Bean
    public RequestInterceptor notifAuthInterceptor(
            @Value("${internal.token:dev-internal-token}") String internalToken) {
        return template -> {
            template.header("X-Internal-Token", internalToken);
            String rid = MDC.get("requestId");
            if (rid != null) template.header("X-Request-Id", rid);
        };
    }
}
