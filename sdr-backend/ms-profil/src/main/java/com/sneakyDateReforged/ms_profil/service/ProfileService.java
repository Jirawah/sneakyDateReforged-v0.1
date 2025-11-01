//package com.sneakyDateReforged.ms_profil.service;
//
//import com.sneakyDateReforged.ms_profil.client.AuthClient;
//import com.sneakyDateReforged.ms_profil.client.FriendClient;
//import com.sneakyDateReforged.ms_profil.client.RdvClient;
//import com.sneakyDateReforged.ms_profil.client.dto.FriendCountResponse;
//import com.sneakyDateReforged.ms_profil.client.dto.RdvNextResponse;
//import com.sneakyDateReforged.ms_profil.client.dto.RdvStatsResponse;
//import com.sneakyDateReforged.ms_profil.dto.*;
//import com.sneakyDateReforged.ms_profil.exception.NotFoundException;
//import com.sneakyDateReforged.ms_profil.model.Profile;
//import com.sneakyDateReforged.ms_profil.repository.ProfileRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import feign.FeignException;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import java.util.function.Supplier;
//import java.util.List;
//import java.util.Objects;
//import java.util.LinkedHashSet;
//import java.util.Locale;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class ProfileService {
//
//    private final ProfileRepository repo;
//    private final FriendClient friendClient;
//    private final RdvClient rdvClient;
//    private final AuthClient authClient;
//
//    /* ---------- BIO (persistée) ---------- */
//
////    @Transactional
////    public ProfileDTO getOrCreateFor(long userId, String email) {
////        Profile p = repo.findByUserId(userId).orElseGet(() -> {
////            String dn = deriveDisplayName(email);          // ex: avant l’@
////            dn = trimToNull(dn);                           // nettoie (la valeur par défaut "user" sera mise en @PrePersist si null)
////            Profile np = Profile.builder()
////                    .userId(userId)
////                    .email(trimToNull(email))
////                    .displayName(dn)
////                    .build();
////            return repo.save(np);
////        });
////        return toDTO(p);
////    }
//    @Transactional
//    public ProfileDTO getOrCreateFor(long userId, String email) {
//        // Si le profil existe déjà, on renvoie tel quel
//        var existing = repo.findByUserId(userId);
//        if (existing.isPresent()) {
//            return toDTO(existing.get());
//        }
//
//        // Sinon, on tente de "booter" depuis ms-auth (JWT propagé via FeignAuthForwarder)
//        var boot = safe(() -> authClient.getProfileBootstrap(userId), null, "auth.profileBootstrap");
//
//        // Pseudo prioritaire = pseudo d'app (chez toi = pseudo Discord stocké dans ms-auth)
//        String displayName = firstNonBlank(
//                boot != null ? boot.pseudo() : null,
//                deriveDisplayName(email) // fallback: avant l'@
//        );
//        displayName = trimToNull(displayName);
//
//        Profile np = Profile.builder()
//                .userId(userId)
//                .email(trimToNull(boot != null ? boot.email() : email))
//                .displayName(displayName)
//                // Snapshots identité/avatars utiles à l’UI, pour ne pas dépendre d’un call live
//                .discordUsername(trimToNull(boot != null ? boot.discordUsername() : null))
//                .discordAvatarUrl(trimToNull(boot != null ? boot.discordAvatarUrl() : null))
//                .steamPseudo(trimToNull(boot != null ? boot.steamPseudo() : null))
//                .steamAvatar(trimToNull(boot != null ? boot.steamAvatar() : null))
//                .build();
//
//        Profile saved = repo.save(np);
//        return toDTO(saved);
//    }
//
//
//    @Transactional
//    public ProfileDTO updateFor(long userId, ProfileUpdateDTO body) {
//        Profile p = repo.findByUserId(userId)
//                .orElseThrow(() -> new NotFoundException("Profil introuvable"));
//
//        if (body.getDisplayName() != null) p.setDisplayName(trimToNull(body.getDisplayName()));
//        if (body.getBio() != null) p.setBio(trimToNull(body.getBio()));
//        if (body.getCountry() != null) p.setCountry(normalizeCountry(body.getCountry()));
//        if (body.getLanguages() != null) p.setLanguages(normalizeLanguages(body.getLanguages()));
//        if (body.getAge() != null) p.setAge(body.getAge()); // bornes déjà via @Min/@Max
//
//        return toDTO(repo.save(p));
//    }
//
//    @Transactional(readOnly = true)
//    public ProfileDTO getPublicView(long userId) {
//        Profile p = repo.findByUserId(userId)
//                .orElseThrow(() -> new NotFoundException("Profil introuvable"));
//        return toDTO(p);
//    }
//
//    /* ---------- AGRÉGATION (non persistée) ---------- */
//    @Transactional(readOnly = true)
//    public AggregatedProfileDTO getAggregatedView(long userId) {
//        Profile p = repo.findByUserId(userId)
//                .orElseThrow(() -> new NotFoundException("Profil introuvable"));
//        return aggregateFor(p);
//    }
//
//    @Transactional(readOnly = true)
//    public PublicAggregatedProfileDTO getAggregatedPublicView(long userId) {
//        Profile p = repo.findByUserId(userId)
//                .orElseThrow(() -> new NotFoundException("Profil introuvable"));
//        AggregatedProfileDTO agg = aggregateFor(p); // ta méthode actuelle
//        return toPublic(agg);
//    }
//
//    private AggregatedProfileDTO aggregateFor(Profile p) {
//        // Détecte si on a un JWT utilisateur
//        boolean userCtx = hasUserToken();
//
//        // ---- FRIENDS ----
//        var friends = userCtx
//                ? safe(() -> friendClient.getFriendCounts(p.getUserId()),
//                new FriendCountResponse(0), "friend.count")
//                : safe(() -> friendClient.getFriendCountsPublic(p.getUserId()),
//                new FriendCountResponse(0), "friend.count.public");
//
//        // ---- RDV ----
//        var rdv = userCtx
//                ? safe(() -> rdvClient.getStats(p.getUserId()),
//                new RdvStatsResponse(0, 0, 0, 0), "rdv.stats")
//                : safe(() -> rdvClient.getStatsPublic(p.getUserId()),
//                new RdvStatsResponse(0, 0, 0, 0), "rdv.stats.public");
//
//        var next = userCtx
//                ? safe(() -> rdvClient.getNextDate(p.getUserId()),
//                new RdvNextResponse(null), "rdv.next")
//                : safe(() -> rdvClient.getNextDatePublic(p.getUserId()),
//                new RdvNextResponse(null), "rdv.next.public");
//
//        // ---- AUTH (données privées) ----
//        // Si public: évite d’appeler ms-auth -> valeurs neutres
////        List<String> games = userCtx
////                ? safe(() -> authClient.getFavoriteGames(p.getUserId()),
////                List.of(), "auth.games")
////                : List.of();
////
////        String steamId = userCtx
////                ? safe(() -> authClient.getSteamId(p.getUserId()).id(),
////                null, "auth.steamId")
////                : null;
////
////        String discordId = userCtx
////                ? safe(() -> authClient.getDiscordId(p.getUserId()).id(),
////                null, "auth.discordId")
////                : null;
////
////        String discordUsername = userCtx
////                ? safe(() -> authClient.getDiscordUsername(p.getUserId()).username(),
////                null, "auth.discordUsername")
////                : null;
////
////        // ---- Fusion identité / avatar (persistés côté profil) ----
////        String pseudo = firstNonBlank(
////                p.getDisplayName(),
////                discordUsername,
////                p.getSteamPseudo()
////        );
////
////        String avatarUrl = firstNonBlank(
////                p.getDiscordAvatarUrl(),
////                p.getSteamAvatar()
////        );
//        List<String> games = userCtx
//                ? safe(() -> authClient.getFavoriteGames(p.getUserId()), List.of(), "auth.games")
//                : List.of();
//
//        String steamId = userCtx
//                ? safe(() -> authClient.getSteamId(p.getUserId()).id(), null, "auth.steamId")
//                : null;
//
//        String discordId = userCtx
//                ? safe(() -> authClient.getDiscordId(p.getUserId()).id(), null, "auth.discordId")
//                : null;
//
//        String discordUsername = userCtx
//                ? safe(() -> authClient.getDiscordUsername(p.getUserId()).username(), null, "auth.discordUsername")
//                : null;
//
//// ✅ NOUVEAU : avatar Discord via ms-auth, si exposé
//        String discordAvatarFromAuth = userCtx
//                ? safe(() -> authClient.getDiscordAvatar(p.getUserId()).url(), null, "auth.discordAvatar")
//                : null;
//
//// ---- Fusion identité / avatar (PRIORITÉ ms-auth) ----
//        String pseudo = firstNonBlank(
//                discordUsername,          // 1. pseudo Discord depuis ms-auth
//                p.getDisplayName(),       // 2. displayName (souvent préfixe d’email)
//                p.getSteamPseudo()        // 3. fallback Steam
//        );
//
//        String avatarUrl = firstNonBlank(
//                discordAvatarFromAuth,    // 1. avatar Discord depuis ms-auth
//                p.getDiscordAvatarUrl(),  // 2. snapshot stocké côté ms-profil
//                p.getSteamAvatar()        // 3. avatar Steam
//        );
//
//        return AggregatedProfileDTO.builder()
//                .userId(p.getUserId())
//                .pseudo(pseudo)
//                .avatarUrl(avatarUrl)
//                .steamId(steamId)
//                .discordId(discordId)
//                .nombreAmis(friends.count())
//                .nombreRDVs(rdv.total())
//                .prochainRDV(next.nextDate())
//                .jeuxFavoris(games)
//                .statsRDV(StatsRdvDTO.builder()
//                        .total(rdv.total())
//                        .confirmes(rdv.confirmes())
//                        .annules(rdv.annules())
//                        .participations(rdv.participations())
//                        .build())
//                .build();
//    }
//
//    private PublicAggregatedProfileDTO toPublic(AggregatedProfileDTO a) {
//        return PublicAggregatedProfileDTO.builder()
//                .userId(a.getUserId())
//                .pseudo(a.getPseudo())
//                .avatarUrl(a.getAvatarUrl())
//                .nombreAmis(a.getNombreAmis())
//                .nombreRDVs(a.getNombreRDVs())
//                .prochainRDV(a.getProchainRDV())
//                .statsRDV(a.getStatsRDV())
//                .build();
//    }
//
//    private String deriveDisplayName(String email) {
//        int at = email == null ? -1 : email.indexOf('@');
//        return (at > 0) ? email.substring(0, at) : (email != null ? email : "user");
//    }
//
//    private ProfileDTO toDTO(Profile p) {
//        return ProfileDTO.builder()
//                .userId(p.getUserId())
//                .displayName(p.getDisplayName())
//                .bio(p.getBio())
//                .country(p.getCountry())
//                .languages(p.getLanguages())
//                .age(p.getAge())
//                .steamPseudo(p.getSteamPseudo())
//                .steamAvatar(p.getSteamAvatar())
//                .discordUsername(p.getDiscordUsername())
//                .discordAvatarUrl(p.getDiscordAvatarUrl())
//                .build();
//    }
//
//    private static <T> T safe(java.util.function.Supplier<? extends T> call, T fallback, String what) {
//        try {
//            T res = call.get();
//            if (res == null) {
//                log.warn("Call {} returned null; using fallback", what);
//                return fallback;
//            }
//            return res;
//        } catch (feign.FeignException e) {
//            log.warn("Feign {} failed: status={} msg={}", what, e.status(), e.getMessage());
//            return fallback;
//        } catch (Exception e) {
//            log.warn("Call {} failed: {}", what, e.toString());
//            return fallback;
//        }
//    }
//
//    private boolean hasUserToken() {
//        var attrs = RequestContextHolder.getRequestAttributes();
//        if (attrs instanceof ServletRequestAttributes sra) {
//            String auth = sra.getRequest().getHeader("Authorization");
//            return auth != null && auth.startsWith("Bearer ");
//        }
//        return false;
//    }
//
//    private static String trimToNull(String s) {
//        if (s == null) return null;
//        String t = s.trim();
//        return t.isEmpty() ? null : t;
//    }
//
//    private static String normalizeCountry(String c) {
//        c = trimToNull(c);
//        if (c == null) return null;
//        c = c.toUpperCase(Locale.ROOT);
//        // Si tu as déjà la validation @Pattern, cette vérif est redondante mais sûre :
//        if (!c.matches("^[A-Z]{2}$")) {
//            throw new IllegalArgumentException("country must be ISO-3166 alpha-2 (e.g. FR)");
//        }
//        return c;
//    }
//
//    private static String normalizeLanguages(String langs) {
//        langs = trimToNull(langs);
//        if (langs == null) return null;
//
//        String[] parts = langs.split(",");
//        LinkedHashSet<String> set = new LinkedHashSet<>(); // garde l'ordre, supprime doublons
//        for (String part : parts) {
//            String t = part.trim().toLowerCase(Locale.ROOT);
//            if (!t.isEmpty()) {
//                if (!t.matches("^[a-z]{2}$")) {
//                    throw new IllegalArgumentException("languages must be like fr,en");
//                }
//                set.add(t);
//            }
//        }
//        return set.isEmpty() ? null : String.join(",", set);
//    }
//
//    private static String firstNonBlank(String... vals) {
//        for (String v : vals) {
//            if (v != null && !v.isBlank()) return v;
//        }
//        return null;
//    }
//
//    private static String nullSafe(String v) {
//        return Objects.toString(v, null);
//    }
//}
package com.sneakyDateReforged.ms_profil.service;

import com.sneakyDateReforged.ms_profil.client.AuthClient;
import com.sneakyDateReforged.ms_profil.client.FriendClient;
import com.sneakyDateReforged.ms_profil.client.RdvClient;
import com.sneakyDateReforged.ms_profil.client.dto.FriendCountResponse;
import com.sneakyDateReforged.ms_profil.client.dto.RdvNextResponse;
import com.sneakyDateReforged.ms_profil.client.dto.RdvStatsResponse;
import com.sneakyDateReforged.ms_profil.dto.*;
import com.sneakyDateReforged.ms_profil.exception.NotFoundException;
import com.sneakyDateReforged.ms_profil.model.Profile;
import com.sneakyDateReforged.ms_profil.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
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
        var existing = repo.findByUserId(userId);
        if (existing.isPresent()) {
            return toDTO(existing.get());
        }

        // Bootstrap depuis ms-auth (JWT propagé via FeignAuthForwarder)
        var boot = safe(() -> authClient.getProfileBootstrap(userId), null, "auth.profileBootstrap");

        // Pseudo prioritaire = pseudo d'app (chez toi = pseudo Discord stocké dans ms-auth)
        String displayName = firstNonBlank(
                boot != null ? boot.pseudo() : null,
                deriveDisplayName(email) // fallback: avant l'@
        );
        displayName = trimToNull(displayName);

        Profile np = Profile.builder()
                .userId(userId)
                .email(trimToNull(boot != null ? boot.email() : email))
                .displayName(displayName)
                // Snapshots utiles (pour éviter un appel live pour l’UI)
                .discordUsername(trimToNull(boot != null ? boot.discordUsername() : null))
                .discordAvatarUrl(trimToNull(boot != null ? boot.discordAvatarUrl() : null))
                .steamPseudo(trimToNull(boot != null ? boot.steamPseudo() : null))
                .steamAvatar(trimToNull(boot != null ? boot.steamAvatar() : null))
                .build();

        Profile saved = repo.save(np);
        return toDTO(saved);
    }

    @Transactional
    public ProfileDTO updateFor(long userId, ProfileUpdateDTO body) {
        Profile p = repo.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Profil introuvable"));

        if (body.getDisplayName() != null) p.setDisplayName(trimToNull(body.getDisplayName()));
        if (body.getBio() != null) p.setBio(trimToNull(body.getBio()));
        if (body.getCountry() != null) p.setCountry(normalizeCountry(body.getCountry()));
        if (body.getLanguages() != null) p.setLanguages(normalizeLanguages(body.getLanguages()));
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
    public PublicAggregatedProfileDTO getAggregatedPublicView(long userId) {
        Profile p = repo.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Profil introuvable"));
        AggregatedProfileDTO agg = aggregateFor(p);
        return toPublic(agg);
    }

    private AggregatedProfileDTO aggregateFor(Profile p) {
        // Détecte si on a un JWT utilisateur
        boolean userCtx = hasUserToken();

        // ---- FRIENDS ----
        var friends = userCtx
                ? safe(() -> friendClient.getFriendCounts(p.getUserId()),
                new FriendCountResponse(0), "friend.count")
                : safe(() -> friendClient.getFriendCountsPublic(p.getUserId()),
                new FriendCountResponse(0), "friend.count.public");

        // ---- RDV ----
        var rdv = userCtx
                ? safe(() -> rdvClient.getStats(p.getUserId()),
                new RdvStatsResponse(0, 0, 0, 0), "rdv.stats")
                : safe(() -> rdvClient.getStatsPublic(p.getUserId()),
                new RdvStatsResponse(0, 0, 0, 0), "rdv.stats.public");

        var next = userCtx
                ? safe(() -> rdvClient.getNextDate(p.getUserId()),
                new RdvNextResponse(null), "rdv.next")
                : safe(() -> rdvClient.getNextDatePublic(p.getUserId()),
                new RdvNextResponse(null), "rdv.next.public");

        // ---- AUTH (données privées) ----
        List<String> games = userCtx
                ? safe(() -> authClient.getFavoriteGames(p.getUserId()), List.of(), "auth.games")
                : List.of();

        String steamId = userCtx
                ? safe(() -> authClient.getSteamId(p.getUserId()).id(), null, "auth.steamId")
                : null;

        String discordId = userCtx
                ? safe(() -> authClient.getDiscordId(p.getUserId()).id(), null, "auth.discordId")
                : null;

        String discordUsername = userCtx
                ? safe(() -> authClient.getDiscordUsername(p.getUserId()).username(), null, "auth.discordUsername")
                : null;

        String discordAvatarFromAuth = userCtx
                ? safe(() -> authClient.getDiscordAvatar(p.getUserId()).url(), null, "auth.discordAvatar")
                : null;

        // ---- Fusion identité / avatar (PRIORITÉ ms-auth) ----
        String pseudo = firstNonBlank(
                discordUsername,          // 1) ms-auth (Discord)
                p.getDisplayName(),       // 2) snapshot persistant (souvent dérivé email si 1er bootstrap KO)
                p.getSteamPseudo()        // 3) fallback Steam
        );

        String avatarUrl = firstNonBlank(
                discordAvatarFromAuth,    // 1) ms-auth (Discord)
                p.getDiscordAvatarUrl(),  // 2) snapshot côté ms-profil
                p.getSteamAvatar()        // 3) fallback Steam
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

    private PublicAggregatedProfileDTO toPublic(AggregatedProfileDTO a) {
        return PublicAggregatedProfileDTO.builder()
                .userId(a.getUserId())
                .pseudo(a.getPseudo())
                .avatarUrl(a.getAvatarUrl())
                .nombreAmis(a.getNombreAmis())
                .nombreRDVs(a.getNombreRDVs())
                .prochainRDV(a.getProchainRDV())
                .statsRDV(a.getStatsRDV())
                .build();
    }

    /* ---------- Utils mapping ---------- */

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

    private static <T> T safe(java.util.function.Supplier<? extends T> call, T fallback, String what) {
        try {
            T res = call.get();
            if (res == null) {
                log.warn("Call {} returned null; using fallback", what);
                return fallback;
            }
            return res;
        } catch (Exception e) {
            // FeignException inclus — on log en WARN et on fallback
            log.warn("Feign/call {} failed: {}", what, e.toString());
            return fallback;
        }
    }

    private boolean hasUserToken() {
        var attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes sra) {
            String auth = sra.getRequest().getHeader("Authorization");
            return auth != null && auth.startsWith("Bearer ");
        }
        return false;
    }

    private static String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static String normalizeCountry(String c) {
        c = trimToNull(c);
        if (c == null) return null;
        c = c.toUpperCase(Locale.ROOT);
        if (!c.matches("^[A-Z]{2}$")) {
            throw new IllegalArgumentException("country must be ISO-3166 alpha-2 (e.g. FR)");
        }
        return c;
    }

    private static String normalizeLanguages(String langs) {
        langs = trimToNull(langs);
        if (langs == null) return null;

        String[] parts = langs.split(",");
        LinkedHashSet<String> set = new LinkedHashSet<>();
        for (String part : parts) {
            String t = part.trim().toLowerCase(Locale.ROOT);
            if (!t.isEmpty()) {
                if (!t.matches("^[a-z]{2}$")) {
                    throw new IllegalArgumentException("languages must be like fr,en");
                }
                set.add(t);
            }
        }
        return set.isEmpty() ? null : String.join(",", set);
    }

    private static String firstNonBlank(String... vals) {
        for (String v : vals) {
            if (v != null && !v.isBlank()) return v;
        }
        return null;
    }

    private static String nullSafe(String v) {
        return Objects.toString(v, null);
    }
}
