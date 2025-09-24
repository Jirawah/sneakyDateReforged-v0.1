package com.sneakyDateReforged.ms_profil.config;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
@Order(1) // tôt dans la chaîne
public class CorrelationIdFilter implements Filter {
    private static final String HEADER = "X-Request-Id";
    private static final String MDC_KEY = "requestId";

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest  request  = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String requestId = Optional.ofNullable(request.getHeader(HEADER))
                .orElse(UUID.randomUUID().toString());

        // Ajoute l’ID au MDC pour l’inclure dans chaque ligne de log
        MDC.put(MDC_KEY, requestId);
        // Renvoie aussi l’ID au client dans la réponse
        response.setHeader(HEADER, requestId);

        try {
            chain.doFilter(req, res);
        } finally {
            MDC.remove(MDC_KEY);
        }
    }
}
