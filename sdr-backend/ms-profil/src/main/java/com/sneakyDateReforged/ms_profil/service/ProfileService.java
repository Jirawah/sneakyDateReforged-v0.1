package com.sneakyDateReforged.ms_profil.service;

import com.sneakyDateReforged.ms_profil.client.AuthClient;
import com.sneakyDateReforged.ms_profil.client.FriendClient;
import com.sneakyDateReforged.ms_profil.client.RdvClient;
import com.sneakyDateReforged.ms_profil.client.dto.FriendCountResponse;
import com.sneakyDateReforged.ms_profil.client.dto.RdvNextResponse;
import com.sneakyDateReforged.ms_profil.client.dto.RdvStatsResponse;
import com.sneakyDateReforged.ms_profil.dto.AggregatedProfileDTO;
import com.sneakyDateReforged.ms_profil.dto.ProfileDTO;
import com.sneakyDateReforged.ms_profil.dto.ProfileUpdateDTO;
import com.sneakyDateReforged.ms_profil.dto.StatsRdvDTO;
import com.sneakyDateReforged.ms_profil.exception.NotFoundException;
import com.sneakyDateReforged.ms_profil.model.Profile;
import com.sneakyDateReforged.ms_profil.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import feign.FeignException;
import java.util.function.Supplier;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository repo;
    private final FriendClient friendClient;
    private final RdvClient rdvClient;
    private final AuthClient authClient;

    /* ---------- BIO (persistée) ---------- */

    @Transactional
    public ProfileDTO getOrCreateFor(long userId, String email) {
        Profile p = repo.findByUserId(userId).orElseGet(() -> {
            Profile np = Profile.builder()
                    .userId(userId)
                    .email(email)
                    .displayName(deriveDisplayName(email))
                    .build();
            return repo.save(np);
        });
        return toDTO(p);
    }

    @Transactional
    public ProfileDTO updateFor(long userId, ProfileUpdateDTO body) {
        Profile p = repo.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Profil introuvable"));
        if (body.getDisplayName() != null) p.setDisplayName(body.getDisplayName());
        if (body.getBio() != null) p.setBio(body.getBio());
        if (body.getCountry() != null) p.setCountry(body.getCountry());
        if (body.getLanguages() != null) p.setLanguages(body.getLanguages());
        if (body.getAge() != null) p.setAge(body.getAge());
        return toDTO(repo.save(p));
    }

    @Transactional(readOnly = true)
    public ProfileDTO getPublicView(long userId) {
        Profile p = repo.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Profil introuvable"));
        return toDTO(p);
    }

    /* ---------- AGRÉGATION (non persistée) ---------- */

    @Transactional(readOnly = true)
    public AggregatedProfileDTO getAggregatedView(long userId) {
        Profile p = repo.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Profil introuvable"));
        return aggregateFor(p);
    }

    @Transactional(readOnly = true)
    public AggregatedProfileDTO getAggregatedPublicView(long userId) {
        Profile p = repo.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Profil introuvable"));
        return aggregateFor(p);
    }

    private AggregatedProfileDTO aggregateFor(Profile p) {
        // --- Appels inter-MS via Feign (avec dégradation gracieuse si 401/403/5xx) ---
        var friends = safe(() -> friendClient.getFriendCounts(p.getUserId()),
                new FriendCountResponse(0), "friend.count");

        var rdv = safe(() -> rdvClient.getStats(p.getUserId()),
                new RdvStatsResponse(0, 0, 0, 0), "rdv.stats");

        var next = safe(() -> rdvClient.getNextDate(p.getUserId()),
                new RdvNextResponse(null), "rdv.next");

//        var games = safe(() -> authClient.getFavoriteGames(p.getUserId()),
//                java.util.List.of(), "auth.games");
        List<String> games = safe(() -> authClient.getFavoriteGames(p.getUserId()),
                List.of(),
                "auth.games");

        String steamId = safe(() -> authClient.getSteamId(p.getUserId()).id(),
                null, "auth.steamId");

        String discordId = safe(() -> authClient.getDiscordId(p.getUserId()).id(),
                null, "auth.discordId");

        String discordUsername = safe(() -> authClient.getDiscordUsername(p.getUserId()).username(),
                null, "auth.discordUsername");

        // --- Fusion identité / avatar (mêmes helpers que chez toi) ---
        String pseudo = firstNonBlank(
                p.getDisplayName(),
                discordUsername,
                p.getSteamPseudo()
        );

        String avatarUrl = firstNonBlank(
                p.getDiscordAvatarUrl(),
                p.getSteamAvatar()
        );

        return AggregatedProfileDTO.builder()
                .userId(p.getUserId())
                .pseudo(pseudo)
                .avatarUrl(avatarUrl)
                .steamId(steamId)
                .discordId(discordId)
                .nombreAmis(friends.count())
                .nombreRDVs(rdv.total())
                .prochainRDV(next.nextDate())
                .jeuxFavoris(games)
                .statsRDV(StatsRdvDTO.builder()
                        .total(rdv.total())
                        .confirmes(rdv.confirmes())
                        .annules(rdv.annules())
                        .participations(rdv.participations())
                        .build())
                .build();
    }

    private String deriveDisplayName(String email) {
        int at = email == null ? -1 : email.indexOf('@');
        return (at > 0) ? email.substring(0, at) : (email != null ? email : "user");
    }

    private ProfileDTO toDTO(Profile p) {
        return ProfileDTO.builder()
                .userId(p.getUserId())
                .displayName(p.getDisplayName())
                .bio(p.getBio())
                .country(p.getCountry())
                .languages(p.getLanguages())
                .age(p.getAge())
                .steamPseudo(p.getSteamPseudo())
                .steamAvatar(p.getSteamAvatar())
                .discordUsername(p.getDiscordUsername())
                .discordAvatarUrl(p.getDiscordAvatarUrl())
                .build();
    }

    private static <T> T safe(Supplier<? extends T> call, T fallback, String what) {
        try {
            return call.get();
        } catch (FeignException e) {
            log.warn("Feign {} failed: status={} msg={}", what, e.status(), e.getMessage());
            return fallback;
        } catch (Exception e) {
            log.warn("Call {} failed: {}", what, e.toString());
            return fallback;
        }
    }

    private static String firstNonBlank(String... vals) {
        for (String v : vals) {
            if (v != null && !v.isBlank()) return v;
        }
        return null;
    }

    private static String nullSafe(String v) { return Objects.toString(v, null); }
}
