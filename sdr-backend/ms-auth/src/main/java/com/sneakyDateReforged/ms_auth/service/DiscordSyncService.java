//package com.sneakyDateReforged.ms_auth.service;
//
//import com.sneakyDateReforged.ms_auth.dto.DiscordSyncRequestDTO;
//import lombok.Getter;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.time.Duration;
//import java.time.Instant;
//import java.util.UUID;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ConcurrentMap;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class DiscordSyncService {
//
//    private final UserAuthService userAuthService;
//    @Getter
//    private volatile String lastDiscordPseudo = null;
//
//    /**
//     * recentConnections :
//     *  - historique par username Discord (legacy, utilisé si on vérifie par pseudo)
//     */
//    private final ConcurrentMap<String, Instant> recentConnections = new ConcurrentHashMap<>();
//
//    /**
//     * pendingStates :
//     *  - états (UUID) que le front vient de demander via /discord/pending
//     *  - donc "cet onglet attend une validation Discord"
//     */
//    private final ConcurrentMap<String, Instant> pendingStates = new ConcurrentHashMap<>();
//
//    /**
//     * connectedStates :
//     *  - états (UUID) considérés comme validés côté backend,
//     *    soit via /link (flux commande),
//     *    soit via détection vocale (ce qu'on va ajouter)
//     */
//    private final ConcurrentMap<String, Instant> connectedStates = new ConcurrentHashMap<>();
//
//    // Durée de validité d'un état (3 minutes)
//    private static final Duration TTL = Duration.ofMinutes(3);
//
//    /**
//     * Appelée quand le bot nous envoie les infos complètes Discord
//     * (ex: via /api/auth/discord/sync).
//     * On synchronise le profil utilisateur en BDD.
//     */
//    public void handleSync(DiscordSyncRequestDTO dto) {
//        log.info("📦 Traitement de la synchro Discord : {}", dto.getDiscordUsername());
//        System.out.println("🎯 DiscordSyncService.handleSync exécuté pour : " + dto.getDiscordUsername());
//
//        // Mise à jour de l'utilisateur en base avec les infos Discord
//        userAuthService.syncDiscordProfile(dto);
//    }
//
//    /**
//     * Marque un utilisateur comme "connecté Discord".
//     * Avant : utilisé par la commande /link du bot (le bot nous envoie username + state).
//     * On le garde (retro-compatible), même si on va maintenant valider aussi via vocal.
//     */
//    public void markConnectedFrom(DiscordSyncRequestDTO dto) {
//        // Clé "username" (legacy)
//        String key = safe(dto.getDiscordUsername());
//        if (!key.isBlank()) {
//            recentConnections.put(key.toLowerCase(), Instant.now());
//        }
//
//        // Clé "state" (nouveau)
//        String st = safe(dto.getState());
//        if (!st.isBlank()) {
//            connectedStates.put(st, Instant.now());
//            // si ce state était encore "pending", on peut le retirer :
//            pendingStates.remove(st);
//        }
//    }
//
//    /**
//     * ✨ NOUVEAU FLUX "AUTO-VALIDATION PAR VOCAL"
//     *
//     * Appelé quand le bot détecte qu'un humain rejoint le salon vocal d'auth.
//     * Idée : tout onglet en attente (pendingStates) est maintenant considéré
//     * comme validé (connected).
//     *
//     * => Ça évite d'imposer la commande /link.
//     *
//     * Param discordUserId : purement pour log/debug.
//     */
//    public void markAllPendingAsConnectedFromVoiceJoin(String discordUserId, String pseudoFromDiscord) {
//        Instant now = Instant.now();
//
//        log.info(
//                "[DiscordSyncService] Voice join from {} -> validating ALL pending states as connected with pseudo '{}'",
//                discordUserId,
//                pseudoFromDiscord
//        );
//        System.out.println(
//                "[DiscordSyncService] Voice join from " + discordUserId +
//                        " -> validating ALL pending states with pseudo '" + pseudoFromDiscord + "'"
//        );
//
//        // On retient le pseudo Discord validé pour l'exposer ensuite au front
//        this.lastDiscordPseudo = pseudoFromDiscord;
//
//        // Tous les states en attente deviennent "connectés"
//        for (String st : pendingStates.keySet()) {
//            connectedStates.put(st, now);
//        }
//
//        // On vide la liste d'attente
//        pendingStates.clear();
//    }
//
//    /**
//     * Legacy :
//     * Vérifie si un pseudo Discord est "récent" (connecté il y a < TTL).
//     * /status?pseudo=...
//     */
//    public boolean isConnected(String pseudo) {
//        String key = safe(pseudo);
//        if (key.isBlank()) return false;
//        key = key.toLowerCase();
//
//        Instant ts = recentConnections.get(key);
//        if (ts == null) return false;
//
//        if (Instant.now().isAfter(ts.plus(TTL))) {
//            // expiré -> on supprime
//            recentConnections.remove(key);
//            return false;
//        }
//        return true;
//    }
//
//    /**
//     * Création d’un "state" côté back, retourné au front via /discord/pending.
//     *
//     * AVANT : on renvoyait juste un UUID sans le stocker.
//     * MAINTENANT : on le range dans pendingStates pour dire
//     * "cet onglet attend la validation Discord".
//     */
//    public String createPendingState() {
//        String state = UUID.randomUUID().toString();
//        pendingStates.put(state, Instant.now());
//        return state;
//    }
//
//    /**
//     * GET /status?state=... → utilisé par le polling du front
//     *
//     * Le front répète cette requête toutes les X secondes.
//     * On répond true si :
//     *   - ce state est dans connectedStates
//     *   - ET pas expiré
//     *
//     * (Note : après markAllPendingAsConnectedFromVoiceJoin(), on a déplacé
//     * tous les pendingStates vers connectedStates, donc ça va retourner true)
//     */
//    public boolean isConnectedByState(String state) {
//        String st = safe(state);
//        if (st.isBlank()) return false;
//
//        Instant ts = connectedStates.get(st);
//        if (ts == null) {
//            return false;
//        }
//
//        // TTL expiré ?
//        if (Instant.now().isAfter(ts.plus(TTL))) {
//            connectedStates.remove(st);
//            return false;
//        }
//        return true;
//    }
//
//    private String safe(String s) {
//        return (s == null) ? "" : s.trim();
//    }
//}
//package com.sneakyDateReforged.ms_auth.service;
//
//import com.sneakyDateReforged.ms_auth.dto.DiscordSyncRequestDTO;
//import lombok.Data;
//import lombok.Getter;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.time.Duration;
//import java.time.Instant;
//import java.util.Map;
//import java.util.UUID;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ConcurrentMap;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class DiscordSyncService {
//
//    private final UserAuthService userAuthService;
//
//    /**
//     * lastDiscordPseudo :
//     *  - utilisé par le front pour pré-remplir "pseudo"
//     *  - alimenté quand on détecte quelqu'un en vocal
//     */
//    @Getter
//    private volatile String lastDiscordPseudo = null;
//
//    /**
//     * ⬇⬇⬇ NOUVEAU : snapshot complet du dernier utilisateur Discord vu.
//     * On va s'en servir plus tard dans AuthService.register() pour remplir
//     * les colonnes discord_* lors de la création en base.
//     */
//    @Data
//    public static class DiscordSnapshot {
//        private String discordId;
//        private String username;
//        private String discriminator;
//        private String nickname;
//        private String avatarUrl;
//        private String chosenPseudo; // celui qu'on veut utiliser comme pseudo app
//    }
//
//    @Getter
//    private volatile DiscordSnapshot lastSnapshot = null;
//
//    /**
//     * recentConnections :
//     *  - historique par username Discord (legacy, utilisé si on vérifie par pseudo)
//     */
//    private final ConcurrentMap<String, Instant> recentConnections = new ConcurrentHashMap<>();
//
//    /**
//     * pendingStates :
//     *  - états (UUID) que le front vient de demander via /discord/pending
//     *  - donc "cet onglet attend une validation Discord"
//     */
//    private final ConcurrentMap<String, Instant> pendingStates = new ConcurrentHashMap<>();
//
//    /**
//     * connectedStates :
//     *  - états (UUID) considérés comme validés côté backend,
//     *    soit via /link (ancien flux),
//     *    soit via détection vocale (nouveau flux)
//     */
//    private final ConcurrentMap<String, Instant> connectedStates = new ConcurrentHashMap<>();
//
//    // Durée de validité d'un state (3 minutes)
//    private static final Duration TTL = Duration.ofMinutes(3);
//
//    /**
//     * Reçoit les infos Discord envoyées par le bot (via /api/auth/discord/sync).
//     * Sert à mettre à jour un user EXISTANT en base (si on le retrouve).
//     */
//    public void handleSync(DiscordSyncRequestDTO dto) {
//        log.info("📦 Traitement de la synchro Discord : {}", dto.getDiscordUsername());
//        System.out.println("🎯 DiscordSyncService.handleSync exécuté pour : " + dto.getDiscordUsername());
//
//        // Mise à jour éventuelle de l'utilisateur en base avec les infos Discord
//        userAuthService.syncDiscordProfile(dto);
//    }
//
//    /**
//     * Marque un utilisateur comme "connecté Discord".
//     * Avant : utilisé par la commande /link du bot (le bot nous envoie username + state).
//     * On le garde pour la compat ascendante.
//     */
//    public void markConnectedFrom(DiscordSyncRequestDTO dto) {
//        // Clé "username" (legacy)
//        String key = safe(dto.getDiscordUsername());
//        if (!key.isBlank()) {
//            recentConnections.put(key.toLowerCase(), Instant.now());
//        }
//
//        // Clé "state" (nouveau)
//        String st = safe(dto.getState());
//        if (!st.isBlank()) {
//            connectedStates.put(st, Instant.now());
//            // si ce state était encore "pending", on peut le retirer
//            pendingStates.remove(st);
//        }
//    }
//
//    /**
//     * VERSION HISTORIQUE (2 arguments) -- COMPATIBILITÉ
//     *
//     * Appelée actuellement par VoiceChannelListener dans ta version du code.
//     * On la garde pour ne pas casser la compilation maintenant.
//     *
//     * Elle délègue vers la vraie nouvelle méthode en remplissant ce qu'on sait.
//     */
//    public void markAllPendingAsConnectedFromVoiceJoin(String discordUserId, String pseudoFromDiscord) {
//        // On ne connait pas encore discriminator / avatar si l'ancien listener ne les passe pas.
//        // On passe ce qu'on a.
//        this.markAllPendingAsConnectedFromVoiceJoin(
//                discordUserId,
//                pseudoFromDiscord,   // username
//                null,                // discriminator (pas dispo ici)
//                pseudoFromDiscord,   // nickname fallback = pseudoFromDiscord
//                null                 // avatarUrl inconnu ici
//        );
//    }
//
//    /**
//     * ✨ NOUVELLE VERSION COMPLÈTE (5 arguments)
//     *
//     * Appelée quand le bot détecte qu'un humain rejoint le salon vocal dédié à l'auth.
//     * Idée :
//     *  - On considère que toutes les sessions "pending" sont maintenant validées.
//     *  - On enregistre un snapshot complet des infos Discord de cet humain.
//     *
//     * C'est CE snapshot qu'on utilisera ensuite dans register()
//     * pour remplir discord_id, discord_username, avatar, etc.
//     */
//    public void markAllPendingAsConnectedFromVoiceJoin(
//            String discordUserId,
//            String username,
//            String discriminator,
//            String nickname,
//            String avatarUrl
//    ) {
//        Instant now = Instant.now();
//
//        log.info(
//                "[DiscordSyncService] Voice join from {} -> validating ALL pending states",
//                discordUserId
//        );
//        System.out.println(
//                "[DiscordSyncService] Voice join from " + discordUserId +
//                        " -> validating ALL pending states (username=" + username + ", nickname=" + nickname + ")"
//        );
//
//        // 1) Construire / mémoriser le snapshot Discord complet
//        DiscordSnapshot snap = new DiscordSnapshot();
//        snap.setDiscordId(discordUserId);
//        snap.setUsername(username);
//        snap.setDiscriminator(discriminator);
//        snap.setNickname(nickname);
//        snap.setAvatarUrl(avatarUrl);
//
//        // Le pseudo qu'on utilisera dans l'app = nickname serveur si présent, sinon username
//        String chosenPseudo = (nickname != null && !nickname.isBlank())
//                ? nickname
//                : username;
//        snap.setChosenPseudo(chosenPseudo);
//
//        this.lastSnapshot = snap;
//        this.lastDiscordPseudo = chosenPseudo; // <- important : sert au front pour pré-remplir "pseudo"
//
//        // 2) Tous les states en attente deviennent "connectés"
//        for (Map.Entry<String, Instant> entry : pendingStates.entrySet()) {
//            connectedStates.put(entry.getKey(), now);
//        }
//
//        // 3) On vide les states en attente
//        pendingStates.clear();
//    }
//
//    /**
//     * Legacy :
//     * Vérifie si un pseudo Discord est "récent" (connecté il y a < TTL).
//     * /status?pseudo=...
//     */
//    public boolean isConnected(String pseudo) {
//        String key = safe(pseudo);
//        if (key.isBlank()) return false;
//        key = key.toLowerCase();
//
//        Instant ts = recentConnections.get(key);
//        if (ts == null) return false;
//
//        if (Instant.now().isAfter(ts.plus(TTL))) {
//            // expiré -> on supprime
//            recentConnections.remove(key);
//            return false;
//        }
//        return true;
//    }
//
//    /**
//     * Création d’un "state" côté back, retourné au front via /discord/pending.
//     * On l'enregistre dans pendingStates pour dire :
//     *   "cet onglet attend la validation Discord"
//     */
//    public String createPendingState() {
//        String state = UUID.randomUUID().toString();
//        pendingStates.put(state, Instant.now());
//        return state;
//    }
//
//    /**
//     * GET /status?state=... → utilisé par le polling du front
//     *
//     * On répond true si :
//     *   - ce state est dans connectedStates
//     *   - ET pas expiré (TTL)
//     */
//    public boolean isConnectedByState(String state) {
//        String st = safe(state);
//        if (st.isBlank()) return false;
//
//        Instant ts = connectedStates.get(st);
//        if (ts == null) {
//            return false;
//        }
//
//        // TTL expiré ?
//        if (Instant.now().isAfter(ts.plus(TTL))) {
//            connectedStates.remove(st);
//            return false;
//        }
//        return true;
//    }
//
//    private String safe(String s) {
//        return (s == null) ? "" : s.trim();
//    }
//}
package com.sneakyDateReforged.ms_auth.service;

import com.sneakyDateReforged.ms_auth.dto.DiscordSyncRequestDTO;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscordSyncService {

    private final UserAuthService userAuthService;

    // historique par username (legacy)
    private final ConcurrentMap<String, Instant> recentConnections = new ConcurrentHashMap<>();

    // états "en attente" -> onglets front qui attendent validation Discord
    private final ConcurrentMap<String, Instant> pendingStates = new ConcurrentHashMap<>();

    // états validés (le front peut débloquer la case Discord)
    private final ConcurrentMap<String, Instant> connectedStates = new ConcurrentHashMap<>();

    private static final Duration TTL = Duration.ofMinutes(3);

    /**
     * Snapshot Discord le plus récent capturé par le bot vocal.
     * On va l'utiliser pour remplir la BDD au moment du register().
     */
    @Data
    public static class DiscordSnapshot {
        private String discordId;
        private String username;
        private String discriminator;
        private String nickname;
        private String avatarUrl;
        private String chosenPseudo; // le pseudo qu'on garde pour l'app
    }

    // utilisé par AuthService.register() pour remplir la BDD
    @Getter
    private volatile DiscordSnapshot lastSnapshot;

    // ============================================================
    // appelé par le listener vocal dès qu'un humain rejoint le chan vocal d'auth
    // ============================================================
    public void markAllPendingAsConnectedFromVoiceJoin(
            String discordUserId,
            String username,
            String discriminator,
            String nickname,
            String avatarUrl
    ) {
        Instant now = Instant.now();
        log.info("[DiscordSyncService] Voice join from {} -> validate all pending states", discordUserId);

        // on construit le snapshot complet
        DiscordSnapshot snap = new DiscordSnapshot();
        snap.setDiscordId(discordUserId);
        snap.setUsername(username);
        snap.setDiscriminator(discriminator);
        snap.setNickname(nickname);
        snap.setAvatarUrl(avatarUrl);
        snap.setChosenPseudo(
                (nickname != null && !nickname.isBlank())
                        ? nickname
                        : username
        );

        this.lastSnapshot = snap; // 🟢 dispo pour AuthService.register()

        // tous les "pending" deviennent "connected"
        for (Map.Entry<String, Instant> e : pendingStates.entrySet()) {
            connectedStates.put(e.getKey(), now);
        }
        pendingStates.clear();
    }

    /**
     * Appelé par le bot (via /api/auth/discord/sync) pour push des infos Discord et maj BDD si besoin.
     * On garde pour compatibilité.
     */
    public void handleSync(DiscordSyncRequestDTO dto) {
        log.info("📦 Sync Discord : {}", dto.getDiscordUsername());
        userAuthService.syncDiscordProfile(dto);
    }

    public void markConnectedFrom(DiscordSyncRequestDTO dto) {
        // legacy : on rattache l'username
        String key = safe(dto.getDiscordUsername());
        if (!key.isBlank()) {
            recentConnections.put(key.toLowerCase(), Instant.now());
        }

        // si le bot envoie un state spécifique, on le marque aussi comme connecté
        String st = safe(dto.getState());
        if (!st.isBlank()) {
            connectedStates.put(st, Instant.now());
            pendingStates.remove(st);
        }
    }

    // ============================================================
    // API utilisée par le front
    // ============================================================

    public String createPendingState() {
        String state = UUID.randomUUID().toString();
        pendingStates.put(state, Instant.now());
        return state;
    }

    // utilisé par le polling Angular
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

    // compat fallback "?pseudo="
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

    // renvoyé au front pour afficher "Pseudo détecté : xxx"
    public String getLastDiscordPseudo() {
        return lastSnapshot != null ? lastSnapshot.getChosenPseudo() : null;
    }

    public String getLastDiscordId() {
        return (lastSnapshot != null) ? lastSnapshot.getDiscordId() : null;
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }
}

