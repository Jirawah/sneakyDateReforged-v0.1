package com.sneakyDateReforged.ms_invitation.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

public class JwtUtils {

    private final SecretKey key;

    public JwtUtils(String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Jws<Claims> parse(String token) throws JwtException {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

    public Optional<Long> extractUserId(String token) {
        try {
            Claims c = parse(token).getBody();
            Object v = c.get("userId"); // <— standardisé
            if (v instanceof Integer) return Optional.of(((Integer) v).longValue());
            if (v instanceof Long)    return Optional.of((Long) v);
            if (v instanceof String)  return Optional.of(Long.parseLong((String) v));
            return Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public boolean isExpired(String token) {
        try {
            Date exp = parse(token).getBody().getExpiration();
            return exp != null && exp.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}
