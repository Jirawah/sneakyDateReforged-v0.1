package com.sneakyDateReforged.ms_profil.security;

import com.sneakyDateReforged.ms_profil.security.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    // Routes exclues du filtre
    private static final List<String> EXCLUDED = List.of(
            "/actuator/health"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            if (path.matches("^/actuator/health$")) return true;
            if (path.matches("^/profiles/[^/]+/public$")) return true;
            if (path.matches("^/profiles/[^/]+/public-full$")) return true; // << ajouté
        }
        return false;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {

        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = auth.substring(7);
        if (!jwtUtils.isSignatureValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        String email = jwtUtils.extractUsername(token);
        Long userId = jwtUtils.extractUserId(token); // peut être null si absent
        String role = jwtUtils.extractRole(token);

        var authToken = new UsernamePasswordAuthenticationToken(
                email, // principal
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + (role == null ? "USER" : role)))
        );

        // on garde userId dispo côté request
        request.setAttribute("userId", userId);

        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }
}
