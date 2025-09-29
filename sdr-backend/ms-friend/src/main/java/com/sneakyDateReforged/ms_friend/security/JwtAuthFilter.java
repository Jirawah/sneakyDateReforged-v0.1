package com.sneakyDateReforged.ms_friend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    public JwtAuthFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);

            Optional<Long> userId = jwtUtils.extractUserId(token);
            if (userId.isPresent() && !jwtUtils.isExpired(token)) {
                // Rôles : on garde simple -> ROLE_USER
                Authentication authentication = new AbstractAuthenticationToken(
                        AuthorityUtils.createAuthorityList("ROLE_USER")) {
                    @Override
                    public Object getCredentials() { return token; }
                    @Override
                    public Object getPrincipal() { return userId.get(); }
                };
                ((AbstractAuthenticationToken) authentication).setAuthenticated(true);
                // pousse dans le SecurityContext
                // via SecurityContextHolder.getContext().setAuthentication(authentication)
                // (Spring 6: se fait via SecurityContextHolder directly)
                org.springframework.security.core.context.SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
            }
        }

        chain.doFilter(request, response);
    }
}
