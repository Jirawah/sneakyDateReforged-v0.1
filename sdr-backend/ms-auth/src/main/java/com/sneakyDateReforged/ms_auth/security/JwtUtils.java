//package com.sneakyDateReforged.ms_auth.security;
//
//import com.sneakyDateReforged.ms_auth.model.UserAuthModel;
//import io.jsonwebtoken.*;
//import io.jsonwebtoken.security.Keys;
//import jakarta.annotation.PostConstruct;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//
//import java.security.Key;
//import java.util.Date;
//
//@Component
//public class JwtUtils {
//
//    @Value("${jwt.secret}")
//    private String secret;
//
//    @Value("${jwt.expirationMs}")
//    private long expirationMs;
//
//    private Key key;
//
//    @PostConstruct
//    public void init() {
//        this.key = Keys.hmacShaKeyFor(secret.getBytes());
//    }
//
//    public String generateToken(UserAuthModel user) {
//        return Jwts.builder()
//                .setSubject(user.getEmail())
//                .claim("id", user.getId())
//                .claim("role", user.getRole())
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
//                .signWith(key, SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//    public String extractUsername(String token) {
//        return parseClaims(token).getBody().getSubject();
//    }
//
//    public boolean isTokenValid(String token, UserDetails userDetails) {
//        String email = extractUsername(token);
//        return email.equals(userDetails.getUsername()) && !isExpired(token);
//    }
//
//    private boolean isExpired(String token) {
//        Date expiration = parseClaims(token).getBody().getExpiration();
//        return expiration.before(new Date());
//    }
//
//    private Jws<Claims> parseClaims(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(key)
//                .setAllowedClockSkewSeconds(1)
//                .build()
//                .parseClaimsJws(token);
//    }
//}
package com.sneakyDateReforged.ms_auth.security;

import com.sneakyDateReforged.ms_auth.model.UserAuthModel;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expirationMs}")
    private long expirationMs;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(UserAuthModel user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("id", user.getId())
                .claim("role", user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getBody().getSubject();
    }

    // ✅ rendu tolérant : retourne false au lieu de propager une exception
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token); // peut lever ExpiredJwtException, etc.
            return username.equals(userDetails.getUsername()) && !isExpired(token);
        } catch (ExpiredJwtException
                 | MalformedJwtException
                 | SignatureException
                 | IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isExpired(String token) {
        try {
            Date expiration = parseClaims(token).getBody().getExpiration();
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            // Si le parse lève déjà l'expiration, on considère le token expiré.
            return true;
        }
    }

    private Jws<Claims> parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .setAllowedClockSkewSeconds(1) // petite marge pour la dérive d’horloge
                .build()
                .parseClaimsJws(token);
    }
}
