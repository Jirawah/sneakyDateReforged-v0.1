//package com.sneakyDateReforged.ms_auth.security;
//
//import com.sneakyDateReforged.ms_auth.service.UserAuthDetailsService;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.jetbrains.annotations.NotNull;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//public class JwtAuthFilter extends OncePerRequestFilter {
//
//    private final JwtUtils jwtUtils;
//    private final UserAuthDetailsService userDetailsService;
//
//    // Liste des routes exclues du filtre JWT
//    private static final List<String> EXCLUDED_PATHS = List.of(
//            "/auth/register",
//            "/auth/login",
//            "/auth/discord-sync",
//            "/api/auth/discord/sync",
//            "/auth/reset-request",
//            "/auth/reset-password",
//            "/actuator/health"
//    );
//
//    @Override
//    protected void doFilterInternal(
//            HttpServletRequest request,
//            @NotNull HttpServletResponse response,
//            @NotNull FilterChain filterChain
//    ) throws ServletException, IOException {
//
//        final String authHeader = request.getHeader("Authorization");
//
//        // Cas : aucune Authorization ou mauvais format
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            System.out.println("[JWT] Aucune entête Authorization présente ou mal formée → requête ignorée");
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        final String jwt = authHeader.substring(7);
//
//        // Extraction du username (email) — exceptions propagées vers GlobalExceptionHandler
//        String email = jwtUtils.extractUsername(jwt);
//        System.out.println("[JWT] Email extrait du token : " + email);
//
//        if (email == null || email.isBlank()) {
//            System.out.println("[JWT] Email null ou vide → requête ignorée");
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        if (SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
//
//            if (jwtUtils.isTokenValid(jwt, userDetails)) {
//                UsernamePasswordAuthenticationToken authToken =
//                        new UsernamePasswordAuthenticationToken(
//                                userDetails,
//                                null,
//                                userDetails.getAuthorities()
//                        );
//
//                SecurityContextHolder.getContext().setAuthentication(authToken);
//                System.out.println("[JWT] Utilisateur authentifié : " + email);
//            } else {
//                System.out.println("[JWT] Token invalide pour l'utilisateur : " + email);
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }
//
//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
//        String path = request.getRequestURI().toLowerCase();
//        boolean excluded = EXCLUDED_PATHS.stream().anyMatch(path::equalsIgnoreCase);
//        System.out.println("[FILTER] Requête reçue sur : " + path);
//        System.out.println("[FILTER] Est exclue du filtre ? " + excluded);
//        return excluded;
//    }
//}
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
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserAuthDetailsService userDetailsService;

    // ✅ Whitelist en patterns
    private static final AntPathMatcher MATCHER = new AntPathMatcher();
    private static final String[] WHITELIST = {
            "/auth/**",        // login, register, reset, etc.
            "/api/auth/**",    // ex: /api/auth/discord/sync
            "/actuator/**"
    };

    //    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
//        String path = request.getServletPath(); // 👈 évite les soucis de context-path
//        // OPTIONS (préflight CORS) -> toujours bypass
//        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
//            System.out.println("[FILTER] OPTIONS bypass sur : " + path);
//            return true;
//        }
//        boolean excluded = false;
//        for (String pattern : WHITELIST) {
//            if (MATCHER.match(pattern, path)) {
//                excluded = true;
//                break;
//            }
//        }
//        System.out.println("[FILTER] Requête reçue sur : " + path);
//        System.out.println("[FILTER] Est exclue du filtre ? " + excluded);
//        return excluded;
//    }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        // Préflight CORS
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;

        // ⛔ NE PAS exclure les endpoints /auth/users/** (ils doivent lire le Bearer)
        if (path.startsWith("/auth/users/")) return false;

        // Whitelist habituelle
        for (String pattern : WHITELIST) {
            if (MATCHER.match(pattern, path)) return true;
        }
        return false;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // 👇 Sans Bearer -> on laisse la chaîne décider (permitAll/authenticated)
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
                // (On ne renvoie pas 401 pour ne pas changer ton comportement existant)
            }
        }

        filterChain.doFilter(request, response);
    }
}
