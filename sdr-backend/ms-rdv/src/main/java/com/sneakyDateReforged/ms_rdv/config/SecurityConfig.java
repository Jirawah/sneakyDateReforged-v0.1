package com.sneakyDateReforged.ms_rdv.config;

import com.sneakyDateReforged.ms_rdv.security.JwtAuthFilter;
import com.sneakyDateReforged.ms_rdv.security.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    JwtUtils jwtUtils() {
        return new JwtUtils(jwtSecret);
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        JwtAuthFilter jwtFilter = new JwtAuthFilter(jwtUtils());

        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // laissez les healthchecks / monitoring accessibles
                        .requestMatchers("/actuator/**").permitAll()
                        // (optionnel) laissez un ping public pour debug si vous voulez
                        .requestMatchers(HttpMethod.GET, "/api/rdv/ping").permitAll()
                        // tout le reste nécessite un Bearer
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
