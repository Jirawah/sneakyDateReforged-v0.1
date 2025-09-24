package com.sneakyDateReforged.ms_profil.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test unitaire de shouldNotFilter(...) sans lancer tout Spring.
 * On utilise une sous-classe qui expose la méthode protégée.
 */
class JwtAuthFilterTest {

    // Petite sous-classe pour exposer shouldNotFilter
    static class ExposedJwtAuthFilter extends JwtAuthFilter {
        ExposedJwtAuthFilter(JwtUtils jwtUtils) { super(jwtUtils); }
        boolean callShouldNotFilter(MockHttpServletRequest req) throws Exception {
            return super.shouldNotFilter(req);
        }
    }

    @Test
    void shouldNotFilter_public_and_health() throws Exception {
        JwtUtils utils = mock(JwtUtils.class);
        ExposedJwtAuthFilter f = new ExposedJwtAuthFilter(utils);

        var reqPublic      = new MockHttpServletRequest("GET", "/profiles/1/public");
        var reqPublicFull  = new MockHttpServletRequest("GET", "/profiles/1/public-full");
        var reqHealth      = new MockHttpServletRequest("GET", "/actuator/health");
        var reqMe          = new MockHttpServletRequest("GET", "/profiles/me");

        assertTrue(f.callShouldNotFilter(reqPublic),      "GET /profiles/{id}/public doit être exclu");
        assertTrue(f.callShouldNotFilter(reqPublicFull),  "GET /profiles/{id}/public-full doit être exclu");
        assertTrue(f.callShouldNotFilter(reqHealth),      "GET /actuator/health doit être exclu");
        assertFalse(f.callShouldNotFilter(reqMe),         "GET /profiles/me ne doit PAS être exclu");
    }

    @Test
    void shouldNotFilter_only_for_GET() throws Exception {
        JwtUtils utils = mock(JwtUtils.class);
        ExposedJwtAuthFilter f = new ExposedJwtAuthFilter(utils);

        var postPublic = new MockHttpServletRequest("POST", "/profiles/1/public");
        assertFalse(f.callShouldNotFilter(postPublic), "POST /profiles/{id}/public ne doit pas être exclu");
    }

    /*
import jakarta.servlet.FilterChain;
import org.springframework.mock.web.MockHttpServletResponse;
import static org.mockito.Mockito.*;
*/

    @Test
    void doFilterInternal_without_auth_header_should_continue_chain() throws Exception {
        JwtUtils utils = mock(JwtUtils.class);
        ExposedJwtAuthFilter f = new ExposedJwtAuthFilter(utils);

        var req = new org.springframework.mock.web.MockHttpServletRequest("GET", "/profiles/me");
        var res = new org.springframework.mock.web.MockHttpServletResponse();
        var chain = mock(jakarta.servlet.FilterChain.class);

        // Pas de header Authorization -> le filtre doit simplement passer la main
        f.doFilterInternal(req, res, chain);
        verify(chain, times(1)).doFilter(req, res);
    }

}
