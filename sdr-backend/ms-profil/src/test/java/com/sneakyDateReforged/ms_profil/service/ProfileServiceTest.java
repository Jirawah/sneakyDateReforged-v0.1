package com.sneakyDateReforged.ms_profil.service;

import com.sneakyDateReforged.ms_profil.client.AuthClient;
import com.sneakyDateReforged.ms_profil.client.FriendClient;
import com.sneakyDateReforged.ms_profil.client.RdvClient;
import com.sneakyDateReforged.ms_profil.client.dto.FriendCountResponse;
import com.sneakyDateReforged.ms_profil.client.dto.RdvNextResponse;
import com.sneakyDateReforged.ms_profil.client.dto.RdvStatsResponse;
import com.sneakyDateReforged.ms_profil.dto.ProfileDTO;
import com.sneakyDateReforged.ms_profil.dto.ProfileUpdateDTO;
import com.sneakyDateReforged.ms_profil.model.Profile;
import com.sneakyDateReforged.ms_profil.repository.ProfileRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    ProfileRepository repo;
    @Mock
    FriendClient friendClient;
    @Mock
    RdvClient rdvClient;
    @Mock
    AuthClient authClient;

    @InjectMocks
    ProfileService service;

    /* ---------------- Bio ---------------- */

    @Nested
    @DisplayName("Bio")
    class Bio {

        @Test
        void getOrCreate_creates_with_display_from_email() {
            when(repo.findByUserId(42L)).thenReturn(Optional.empty());
            when(repo.save(any(Profile.class))).thenAnswer(inv -> inv.getArgument(0));

            ProfileDTO dto = service.getOrCreateFor(42L, "coco@example.com");

            assertEquals(42L, dto.getUserId());
            assertEquals("coco", dto.getDisplayName()); // deriveDisplayName
        }

        @Test
        void update_normalizes_fields() {
            Profile existing = Profile.builder().id(1L).userId(42L).displayName("Old").build();
            when(repo.findByUserId(42L)).thenReturn(Optional.of(existing));
            when(repo.save(any(Profile.class))).thenAnswer(inv -> inv.getArgument(0));

            ProfileUpdateDTO body = new ProfileUpdateDTO();
            body.setDisplayName("  New  ");
            body.setCountry("FR");         // adapte si tu as mis une normalisation
            body.setLanguages("fr,en");    // idem
            body.setAge(20);

            ProfileDTO dto = service.updateFor(42L, body);

            assertEquals("New", dto.getDisplayName());
            assertEquals("FR", dto.getCountry());
            assertEquals("fr,en", dto.getLanguages());
            assertEquals(20, dto.getAge());
        }
    }

    /* ------------- Agrégation ------------- */

    @Nested
    @DisplayName("Agrégation")
    class Aggregation {

        private Profile prof(long id) {
            return Profile.builder().id(1L).userId(id).displayName("Coco").build();
        }

        @Test
        void aggregated_public_uses_public_endpoints() {
            when(repo.findByUserId(1L)).thenReturn(Optional.of(prof(1L)));
            when(friendClient.getFriendCountsPublic(1L)).thenReturn(new FriendCountResponse(3));
            when(rdvClient.getStatsPublic(1L)).thenReturn(new RdvStatsResponse(5, 2, 1, 2));
            when(rdvClient.getNextDatePublic(1L)).thenReturn(new RdvNextResponse(null));

            var dto = service.getAggregatedPublicView(1L);

            assertEquals(1L, dto.getUserId());
            assertEquals(3, dto.getNombreAmis());
            assertEquals(5, dto.getStatsRDV().getTotal());

            // 👉 vérifications clés
            verify(friendClient).getFriendCountsPublic(1L);
            verify(rdvClient).getStatsPublic(1L);
            verify(rdvClient).getNextDatePublic(1L);

            // En public, on ne doit PAS taper les endpoints privés ni auth
            verify(friendClient, never()).getFriendCounts(anyLong());
            verify(rdvClient, never()).getStats(anyLong());
            verify(rdvClient, never()).getNextDate(anyLong());
            verifyNoInteractions(authClient);
        }


        //        @Test
//        void aggregated_private_uses_private_endpoints_and_fallback_on_error() {
//            when(repo.findByUserId(1L)).thenReturn(Optional.of(prof(1L)));
//            when(friendClient.getFriendCounts(1L)).thenReturn(new FriendCountResponse(7));
//            when(rdvClient.getStats(1L)).thenThrow(feignError(500));
//            when(rdvClient.getNextDate(1L)).thenThrow(feignError(503));
//
//            SecurityContextHolder.getContext().setAuthentication(
//                    new UsernamePasswordAuthenticationToken(
//                            "me@example.com",
//                            null,
//                            java.util.List.of(new SimpleGrantedAuthority("ROLE_USER"))
//                    )
//            );
//
//            var dto = service.getAggregatedView(1L);
//
//            assertEquals(7, dto.getNombreAmis());
//            assertEquals(0, dto.getStatsRDV().getTotal());
//            assertNull(dto.getProchainRDV());
//
//            // 👉 vérifications clés
//            verify(friendClient).getFriendCounts(1L);
//            verify(rdvClient).getStats(1L);
//            verify(rdvClient).getNextDate(1L);
//
//            // En privé, on ne doit PAS appeler les endpoints publics
//            verify(friendClient, never()).getFriendCountsPublic(anyLong());
//            verify(rdvClient,   never()).getStatsPublic(anyLong());
//            verify(rdvClient,   never()).getNextDatePublic(anyLong());
//
//            SecurityContextHolder.clearContext();
//        }
        @Test
        void aggregated_private_uses_private_endpoints_and_fallback_on_error() {
            when(repo.findByUserId(1L)).thenReturn(Optional.of(prof(1L)));
            when(friendClient.getFriendCounts(1L)).thenReturn(new FriendCountResponse(7));
            when(rdvClient.getStats(1L)).thenThrow(feignError(500));
            when(rdvClient.getNextDate(1L)).thenThrow(feignError(503));

            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(
                            "me@example.com", null,
                            java.util.List.of(new SimpleGrantedAuthority("ROLE_USER"))
                    )
            );

            MockHttpServletRequest req = new MockHttpServletRequest();
            req.addHeader("Authorization", "Bearer test"); // n'importe quelle valeur
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(req));

            try {
                var dto = service.getAggregatedView(1L);

                assertEquals(7, dto.getNombreAmis());
                assertEquals(0, dto.getStatsRDV().getTotal());
                assertNull(dto.getProchainRDV());

                verify(friendClient).getFriendCounts(1L);
                verify(rdvClient).getStats(1L);
                verify(rdvClient).getNextDate(1L);
                verify(friendClient, never()).getFriendCountsPublic(anyLong());
                verify(rdvClient, never()).getStatsPublic(anyLong());
                verify(rdvClient, never()).getNextDatePublic(anyLong());
            } finally {
                RequestContextHolder.resetRequestAttributes();
                SecurityContextHolder.clearContext();
            }
        }

        private feign.FeignException feignError(int status) {
            var req = feign.Request.create(feign.Request.HttpMethod.GET, "/x", Map.of(), null, StandardCharsets.UTF_8, null);
            var rsp = feign.Response.builder().request(req).status(status).build();
            return feign.FeignException.errorStatus("methodKey", rsp);
        }
    }
}
