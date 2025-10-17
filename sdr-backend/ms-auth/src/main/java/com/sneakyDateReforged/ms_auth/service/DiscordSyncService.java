package com.sneakyDateReforged.ms_auth.service;

import com.sneakyDateReforged.ms_auth.dto.DiscordSyncRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscordSyncService {

    private final UserAuthService userAuthService;
    private final ConcurrentMap<String, Instant> recentConnections = new ConcurrentHashMap<>();


    private static final Duration TTL = Duration.ofMinutes(3);

    public void handleSync(DiscordSyncRequestDTO dto) {
        log.info("📦 Traitement de la synchro Discord : {}", dto.getDiscordUsername());
        System.out.println("🎯 DiscordSyncService.handleSync exécuté pour : " + dto.getDiscordUsername());

        // Mise à jour de l'utilisateur en base avec les infos Discord
        userAuthService.syncDiscordProfile(dto);
    }

    public void markConnectedFrom(DiscordSyncRequestDTO dto) {
        // ⇩⇩⇩ IMPORTANT : choisis le bon champ qui représente le pseudo saisi côté front
        // Exemples possibles selon ton DTO :
        //   String key = safe(dto.getUsername());
        //   String key = safe(dto.getPseudo());
        //   String key = safe(dto.getDisplayName());
        String key = safe(dto.getDiscordUsername()); // ← remplace par le bon getter si besoin

        if (!key.isBlank()) {
            recentConnections.put(key.toLowerCase(), Instant.now());
        }
    }

    // GET /status?pseudo=...  → utilisé par le front (poll)
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

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
