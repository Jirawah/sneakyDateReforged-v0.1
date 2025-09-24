package com.sneakyDateReforged.ms_profil.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public boolean isSignatureValid(String token) {
        try {
            parser().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return parser().parseClaimsJws(token).getBody().getSubject();
    }

    public Long extractUserId(String token) {
        Object id = parser().parseClaimsJws(token).getBody().get("id");
        if (id == null) return null;
        if (id instanceof Integer i) return i.longValue();
        if (id instanceof Long l) return l;
        if (id instanceof String s) return Long.parseLong(s);
        return null;
    }

    public String extractRole(String token) {
        Object role = parser().parseClaimsJws(token).getBody().get("role");
        return role == null ? null : role.toString();
    }

    private JwtParser parser() {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .setAllowedClockSkewSeconds(1)
                .build();
    }
}
