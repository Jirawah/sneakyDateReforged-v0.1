package com.sneakyDateReforged.ms_auth.security;

import com.sneakyDateReforged.ms_auth.service.UserAuthDetailsService;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires de JwtAuthFilter (sans charger le contexte Spring).
 * On utilise les mocks de Spring Web pour la requête/réponse/chaîne.
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserAuthDetailsService userDetailsService;

    @InjectMocks
    private JwtAuthFilter filter;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ---------- shouldNotFilter ----------

    @Test
    void shouldNotFilter_returnsTrue_forOPTIONS() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setMethod("OPTIONS");
        req.setServletPath("/any/path");

        assertTrue(filter.shouldNotFilter(req));
    }

    @Test
    void shouldNotFilter_returnsTrue_forWhitelist_authLogin() {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/auth/login");
        req.setServletPath("/auth/login");

        assertTrue(filter.shouldNotFilter(req));
    }

    @Test
    void shouldNotFilter_returnsTrue_forWhitelist_apiAuthDiscord() {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/auth/discord/sync");
        req.setServletPath("/api/auth/discord/sync");

        assertTrue(filter.shouldNotFilter(req));
    }

    @Test
    void shouldNotFilter_returnsTrue_forWhitelist_actuator() {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/actuator/health");
        req.setServletPath("/actuator/health");

        assertTrue(filter.shouldNotFilter(req));
    }

    @Test
    void shouldNotFilter_returnsFalse_for_authUsers_prefix() {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/auth/users/me");
        req.setServletPath("/auth/users/me");

        // Exception à la whitelist : NE DOIT PAS être exclu
        assertFalse(filter.shouldNotFilter(req));
    }

    // ---------- doFilterInternal ----------

    @Test
    void doFilter_noAuthorizationHeader_leavesContextNull_andContinuesChain() throws ServletException, IOException {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/secured");
        req.setServletPath("/secured");
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(req, res, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verifyNoInteractions(jwtUtils, userDetailsService);
    }

    @Test
    void doFilter_bearerHeader_usernameNull_doesNotAuthenticate() throws ServletException, IOException {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/secured");
        req.setServletPath("/secured");
        req.addHeader("Authorization", "Bearer tok123");
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        when(jwtUtils.extractUsername("tok123")).thenReturn(null);

        filter.doFilter(req, res, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtUtils).extractUsername("tok123");
        verifyNoMoreInteractions(jwtUtils);
        verifyNoInteractions(userDetailsService);
    }

    @Test
    void doFilter_validToken_setsAuthenticationInContext() throws ServletException, IOException {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/secured");
        req.setServletPath("/secured");
        req.addHeader("Authorization", "Bearer goodToken");
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        String email = "user@example.com";
        var userDetails = new User(email, "pw", Collections.emptyList());

        when(jwtUtils.extractUsername("goodToken")).thenReturn(email);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(jwtUtils.isTokenValid("goodToken", userDetails)).thenReturn(true);

        filter.doFilter(req, res, chain);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals(email, auth.getName());

        verify(jwtUtils).extractUsername("goodToken");
        verify(userDetailsService).loadUserByUsername(email);
        verify(jwtUtils).isTokenValid("goodToken", userDetails);
    }

    @Test
    void doFilter_invalidToken_doesNotAuthenticate() throws ServletException, IOException {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/secured");
        req.setServletPath("/secured");
        req.addHeader("Authorization", "Bearer badToken");
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        String email = "user@example.com";
        var userDetails = new User(email, "pw", Collections.emptyList());

        when(jwtUtils.extractUsername("badToken")).thenReturn(email);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(jwtUtils.isTokenValid("badToken", userDetails)).thenReturn(false);

        filter.doFilter(req, res, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtUtils).extractUsername("badToken");
        verify(userDetailsService).loadUserByUsername(email);
        verify(jwtUtils).isTokenValid("badToken", userDetails);
    }

    @Test
    void doFilter_authAlreadyPresent_skipsUserLoading() throws ServletException, IOException {
        // Contexte déjà authentifié
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("already@auth", null, Collections.emptyList())
        );

        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/secured");
        req.setServletPath("/secured");
        req.addHeader("Authorization", "Bearer anyToken");
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        // Même si extractUsername est appelé, le filter NE DOIT PAS recharger l'utilisateur
        when(jwtUtils.extractUsername("anyToken")).thenReturn("already@auth");

        filter.doFilter(req, res, chain);

        verify(jwtUtils).extractUsername("anyToken");
        verifyNoInteractions(userDetailsService);
        // L'auth du contexte reste celle déjà posée
        assertEquals("already@auth",
                SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    void doFilter_extractUsernameThrows_propagatesToGlobalHandlerLayer() {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/secured");
        req.setServletPath("/secured");
        req.addHeader("Authorization", "Bearer boom");
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        when(jwtUtils.extractUsername("boom")).thenThrow(new RuntimeException("Token parsing failed"));

        // Le filtre ne catch pas → l'exception remonte (GlobalExceptionHandler s'en chargera en intégration)
        assertThrows(RuntimeException.class, () -> filter.doFilter(req, res, chain));
    }
}
