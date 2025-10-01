package com.sneakyDateReforged.ms_notif.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Protège POST /events par un header X-Internal-Token (simple et efficace pour S2S).
 */
public class InternalTokenFilter extends OncePerRequestFilter {

    private final String internalToken;
    private static final AntPathMatcher PATH = new AntPathMatcher();

    public InternalTokenFilter(@Value("${internal.token:dev-internal-token}") String internalToken) {
        this.internalToken = internalToken;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // On ne filtre que /events
        return !("POST".equalsIgnoreCase(request.getMethod()) && PATH.match("/events", request.getRequestURI()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String header = req.getHeader("X-Internal-Token");
        if (internalToken == null || internalToken.isBlank() || !internalToken.equals(header)) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            res.getWriter().write("Forbidden: missing or invalid X-Internal-Token");
            return;
        }
        chain.doFilter(req, res);
    }
}
