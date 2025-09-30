package com.sneakyDateReforged.ms_invitation.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignAuthForwarder {
    @Bean
    public RequestInterceptor authForwarder() {
        return template -> {
            RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
            if (!(attrs instanceof ServletRequestAttributes sra)) return;
            var req = sra.getRequest();

            String auth = req.getHeader("Authorization");
            if (auth != null && !auth.isBlank()) template.header("Authorization", auth);

            String reqId = req.getHeader("X-Request-Id");
            if (reqId != null && !reqId.isBlank()) template.header("X-Request-Id", reqId);
        };
    }
}
