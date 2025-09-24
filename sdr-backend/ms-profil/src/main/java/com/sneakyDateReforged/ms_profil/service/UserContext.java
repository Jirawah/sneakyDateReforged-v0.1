package com.sneakyDateReforged.ms_profil.service;

import com.sneakyDateReforged.ms_profil.security.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserContext {
    private final HttpServletRequest request;
    private final JwtUtils jwtUtils;

    public long getUserId() {
        Object v = request.getAttribute("userId");
        if (v instanceof Long l) return l;
        // fallback si besoin : relire le token
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            Long id = jwtUtils.extractUserId(auth.substring(7));
            if (id != null) return id;
        }
        throw new IllegalStateException("userId introuvable dans le JWT");
    }

    public String getEmail() {
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            return jwtUtils.extractUsername(auth.substring(7));
        }
        throw new IllegalStateException("Authorization manquante");
    }
}
