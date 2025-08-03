package com.sneakyDateReforged.ms_auth.security;

import com.sneakyDateReforged.ms_auth.service.UserAuthDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserAuthDetailsService userDetailsService;

    // Liste des routes exclues du filtre JWT
    private static final List<String> EXCLUDED_PATHS = List.of(
            "/auth/register",
            "/auth/login",
            "/auth/discord-sync",
            "/auth/reset-request",
            "/auth/reset-password"
    );

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // Cas : aucune Authorization ou mauvais format
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("[JWT] Aucune entête Authorization présente ou mal formée → requête ignorée");
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        // Extraction du username (email) — exceptions propagées vers GlobalExceptionHandler
        String email = jwtUtils.extractUsername(jwt);
        System.out.println("[JWT] Email extrait du token : " + email);

        if (email == null || email.isBlank()) {
            System.out.println("[JWT] Email null ou vide → requête ignorée");
            filterChain.doFilter(request, response);
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            if (jwtUtils.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("[JWT] Utilisateur authentifié : " + email);
            } else {
                System.out.println("[JWT] Token invalide pour l'utilisateur : " + email);
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath().toLowerCase();
        boolean excluded = EXCLUDED_PATHS.stream().anyMatch(path::equalsIgnoreCase);
        System.out.println("[FILTER] Requête reçue sur : " + path);
        System.out.println("[FILTER] Est exclue du filtre ? " + excluded);
        return excluded;
    }
}
