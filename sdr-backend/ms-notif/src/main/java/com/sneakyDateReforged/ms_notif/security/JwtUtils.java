package com.sneakyDateReforged.ms_notif.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtUtils {

    private final SecretKey key;

    public JwtUtils(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Jws<Claims> parse(String token) throws JwtException {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

    public Long extractUserId(String token) {
        Claims c = parse(token).getBody();
        String sub = c.getSubject();
        try {
            return sub != null ? Long.parseLong(sub) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Claims c = parse(token).getBody();
        Object roles = c.get("roles");
        if (roles instanceof List<?> l) {
            return l.stream().map(String::valueOf).toList();
        }
        return List.of("USER");
    }
}
