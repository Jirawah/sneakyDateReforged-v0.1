package com.sneakyDateReforged.ms_auth.service;

import com.sneakyDateReforged.ms_auth.dto.DiscordSyncRequestDTO;
import lombok.Getter;
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
    @Getter
    private volatile String lastDiscordPseudo = null;

    /**
     * recentConnections :
     *  - historique par username Discord (legacy, utilisé si on vérifie par pseudo)
     */
    private final ConcurrentMap<String, Instant> recentConnections = new ConcurrentHashMap<>();

    /**
     * pendingStates :
     *  - états (UUID) que le front vient de demander via /discord/pending
     *  - donc "cet onglet attend une validation Discord"
     */
    private final ConcurrentMap<String, Instant> pendingStates = new ConcurrentHashMap<>();

    /**
     * connectedStates :
     *  - états (UUID) considérés comme validés côté backend,
     *    soit via /link (flux commande),
     *    soit via détection vocale (ce qu'on va ajouter)
     */
    private final ConcurrentMap<String, Instant> connectedStates = new ConcurrentHashMap<>();

    // Durée de validité d'un état (3 minutes)
    private static final Duration TTL = Duration.ofMinutes(3);

    /**
     * Appelée quand le bot nous envoie les infos complètes Discord
     * (ex: via /api/auth/discord/sync).
     * On synchronise le profil utilisateur en BDD.
     */
    public void handleSync(DiscordSyncRequestDTO dto) {
        log.info("📦 Traitement de la synchro Discord : {}", dto.getDiscordUsername());
        System.out.println("🎯 DiscordSyncService.handleSync exécuté pour : " + dto.getDiscordUsername());

        // Mise à jour de l'utilisateur en base avec les infos Discord
        userAuthService.syncDiscordProfile(dto);
    }

    /**
     * Marque un utilisateur comme "connecté Discord".
     * Avant : utilisé par la commande /link du bot (le bot nous envoie username + state).
     * On le garde (retro-compatible), même si on va maintenant valider aussi via vocal.
     */
    public void markConnectedFrom(DiscordSyncRequestDTO dto) {
        // Clé "username" (legacy)
        String key = safe(dto.getDiscordUsername());
        if (!key.isBlank()) {
            recentConnections.put(key.toLowerCase(), Instant.now());
        }

        // Clé "state" (nouveau)
        String st = safe(dto.getState());
        if (!st.isBlank()) {
            connectedStates.put(st, Instant.now());
            // si ce state était encore "pending", on peut le retirer :
            pendingStates.remove(st);
        }
    }

    /**
     * ✨ NOUVEAU FLUX "AUTO-VALIDATION PAR VOCAL"
     *
     * Appelé quand le bot détecte qu'un humain rejoint le salon vocal d'auth.
     * Idée : tout onglet en attente (pendingStates) est maintenant considéré
     * comme validé (connected).
     *
     * => Ça évite d'imposer la commande /link.
     *
     * Param discordUserId : purement pour log/debug.
     */
    public void markAllPendingAsConnectedFromVoiceJoin(String discordUserId, String pseudoFromDiscord) {
        Instant now = Instant.now();

        log.info(
                "[DiscordSyncService] Voice join from {} -> validating ALL pending states as connected with pseudo '{}'",
                discordUserId,
                pseudoFromDiscord
        );
        System.out.println(
                "[DiscordSyncService] Voice join from " + discordUserId +
                        " -> validating ALL pending states with pseudo '" + pseudoFromDiscord + "'"
        );

        // On retient le pseudo Discord validé pour l'exposer ensuite au front
        this.lastDiscordPseudo = pseudoFromDiscord;

        // Tous les states en attente deviennent "connectés"
        for (String st : pendingStates.keySet()) {
            connectedStates.put(st, now);
        }

        // On vide la liste d'attente
        pendingStates.clear();
    }

    /**
     * Legacy :
     * Vérifie si un pseudo Discord est "récent" (connecté il y a < TTL).
     * /status?pseudo=...
     */
    public boolean isConnected(String pseudo) {
        String key = safe(pseudo);
        if (key.isBlank()) return false;
        key = key.toLowerCase();

        Instant ts = recentConnections.get(key);
        if (ts == null) return false;

        if (Instant.now().isAfter(ts.plus(TTL))) {
            // expiré -> on supprime
            recentConnections.remove(key);
            return false;
        }
        return true;
    }

    /**
     * Création d’un "state" côté back, retourné au front via /discord/pending.
     *
     * AVANT : on renvoyait juste un UUID sans le stocker.
     * MAINTENANT : on le range dans pendingStates pour dire
     * "cet onglet attend la validation Discord".
     */
    public String createPendingState() {
        String state = UUID.randomUUID().toString();
        pendingStates.put(state, Instant.now());
        return state;
    }

    /**
     * GET /status?state=... → utilisé par le polling du front
     *
     * Le front répète cette requête toutes les X secondes.
     * On répond true si :
     *   - ce state est dans connectedStates
     *   - ET pas expiré
     *
     * (Note : après markAllPendingAsConnectedFromVoiceJoin(), on a déplacé
     * tous les pendingStates vers connectedStates, donc ça va retourner true)
     */
    public boolean isConnectedByState(String state) {
        String st = safe(state);
        if (st.isBlank()) return false;

        Instant ts = connectedStates.get(st);
        if (ts == null) {
            return false;
        }

        // TTL expiré ?
        if (Instant.now().isAfter(ts.plus(TTL))) {
            connectedStates.remove(st);
            return false;
        }
        return true;
    }

    private String safe(String s) {
        return (s == null) ? "" : s.trim();
    }
}
