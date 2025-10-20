package com.sneakyDateReforged.ms_auth.service;

import com.sneakyDateReforged.ms_auth.dto.DiscordSyncRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscordSyncService {

    private final UserAuthService userAuthService;

    // ✅ Historique "username" (déjà en place)
    private final ConcurrentMap<String, Instant> recentConnections = new ConcurrentHashMap<>();

    // ✅ Nouveau : corrélation par "state" (link code)
    private final ConcurrentMap<String, Instant> connectedStates = new ConcurrentHashMap<>();

    // TTL commun (3 min)
    private static final Duration TTL = Duration.ofMinutes(3);

    public void handleSync(DiscordSyncRequestDTO dto) {
        log.info("📦 Traitement de la synchro Discord : {}", dto.getDiscordUsername());
        System.out.println("🎯 DiscordSyncService.handleSync exécuté pour : " + dto.getDiscordUsername());

        // Mise à jour de l'utilisateur en base avec les infos Discord
        userAuthService.syncDiscordProfile(dto);
    }

    /**
     * Marque l'utilisateur comme "connecté" via username ET/OU via state si présent.
     * Appelée après réception du POST /api/auth/discord/sync (par le bot).
     */
    public void markConnectedFrom(DiscordSyncRequestDTO dto) {
        // Clé "username" (legacy)
        String key = safe(dto.getDiscordUsername());
        if (!key.isBlank()) {
            recentConnections.put(key.toLowerCase(), Instant.now());
        }

        // ✅ Clé "state" (nouveau, pour corréler navigateur <-> bot)
        String st = safe(dto.getState());
        if (!st.isBlank()) {
            connectedStates.put(st, Instant.now());
        }
    }

    /**
     * GET /status?pseudo=... → legacy (par username)
     */
    public boolean isConnected(String pseudo) {
        String key = safe(pseudo);
        if (key.isBlank()) return false;
        key = key.toLowerCase();

        Instant ts = recentConnections.get(key);
        if (ts == null) return false;

        if (Instant.now().isAfter(ts.plus(TTL))) {
            recentConnections.remove(key);
            return false;
        }
        return true;
    }

    /**
     * ✅ Création d’un "state" côté back (retourné au front via /pending)
     * On ne stocke pas de "pending" côté back ; on marquera "connected" à l’arrivée du /sync.
     */
    public String createPendingState() {
        return UUID.randomUUID().toString();
    }

    /**
     * ✅ GET /status?state=... → vérifie si ce state a été marqué connecté par le /sync
     */
    public boolean isConnectedByState(String state) {
        String st = safe(state);
        if (st.isBlank()) return false;

        Instant ts = connectedStates.get(st);
        if (ts == null) return false;

        if (Instant.now().isAfter(ts.plus(TTL))) {
            connectedStates.remove(st);
            return false;
        }
        return true;
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
