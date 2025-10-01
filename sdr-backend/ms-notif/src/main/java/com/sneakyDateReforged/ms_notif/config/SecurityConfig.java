package com.sneakyDateReforged.ms_notif.config;

import com.sneakyDateReforged.ms_notif.security.InternalTokenFilter;
import com.sneakyDateReforged.ms_notif.security.JwtAuthFilter;
import com.sneakyDateReforged.ms_notif.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtils jwtUtils;

    @Value("${cors.allowed-origins:*}")
    private String allowedOrigins;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CORS
        http.cors(cors -> cors.configurationSource(req -> {
            CorsConfiguration cfg = new CorsConfiguration();
            List<String> origins = Arrays.asList(allowedOrigins.split("\\s*,\\s*"));
            cfg.setAllowedOrigins(origins);
            cfg.setAllowedMethods(Arrays.asList("GET","POST","PATCH","DELETE","OPTIONS"));
            cfg.setAllowedHeaders(Arrays.asList("*"));
            cfg.setAllowCredentials(true);
            return cfg;
        }));

        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers(HttpMethod.POST, "/events").permitAll()  // filtré par InternalTokenFilter
                .requestMatchers("/my/**", "/notifications/**").authenticated()
                .anyRequest().denyAll()
        );

        // Filtres : d'abord le header interne sur /events, puis JWT pour /my/**
        http.addFilterBefore(new InternalTokenFilter(null), BasicAuthenticationFilter.class);
        http.addFilterBefore(new JwtAuthFilter(jwtUtils), BasicAuthenticationFilter.class);

        // no sessions
        http.httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public InternalTokenFilter internalTokenFilter(
            @Value("${internal.token:dev-internal-token}") String internalToken) {
        return new InternalTokenFilter(internalToken);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, InternalTokenFilter internalFilter) throws Exception {
        // ...
        http.addFilterBefore(internalFilter, BasicAuthenticationFilter.class);
        http.addFilterBefore(new JwtAuthFilter(jwtUtils), BasicAuthenticationFilter.class);
        return http.build();
    }

}
