//package com.sneakyDateReforged.ms_auth.controller;
//
//import com.sneakyDateReforged.ms_auth.dto.DiscordSyncRequestDTO;
//import com.sneakyDateReforged.ms_auth.service.DiscordSyncService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
//@Slf4j
//@RestController
//@RequestMapping("/api/auth/discord")
//@RequiredArgsConstructor
//public class DiscordController {
//
//    private final DiscordSyncService discordSyncService;
//
//    /**
//     * Créé un "state" (code de liaison) que le front utilisera pour corréler la session
//     * avec l'évènement envoyé par le bot Discord.
//     */
//    @PostMapping("/pending")
//    public ResponseEntity<Map<String, String>> createPending() {
//        String state = discordSyncService.createPendingState();
//        return ResponseEntity.ok(Map.of("state", state));
//    }
//
//    /**
//     * Récupère l'état de connexion Discord.
//     * - Préférence au "state" (flux sans saisie de pseudo)
//     * - Rétro-compat: accepte aussi "pseudo"
//     */
//    @GetMapping("/status")
//    public ResponseEntity<Map<String, Object>> status(
//            @RequestParam(required = false) String state,
//            @RequestParam(required = false) String pseudo
//    ) {
//        boolean connected = false;
//
//        if (state != null && !state.isBlank()) {
//            connected = discordSyncService.isConnectedByState(state);
//        } else if (pseudo != null && !pseudo.isBlank()) {
//            // rétro-compat (au cas où l'ancien flux par pseudo existe encore)
//            connected = discordSyncService.isConnected(pseudo);
//        }
//
//        // On récupère le pseudo Discord mémorisé lors de la connexion vocale.
//        // (c'est rempli dans markAllPendingAsConnectedFromVoiceJoin(...))
//        String discordPseudo = discordSyncService.getLastDiscordPseudo();
//
//        return ResponseEntity.ok(
//                Map.of(
//                        "connected", connected,
//                        "discordPseudo", discordPseudo
//                )
//        );
//    }
//
//    /**
//     * Appelé par le bot quand il récupère les infos Discord de l'utilisateur.
//     * On met à jour le profil (handleSync) puis on marque la connexion
//     * (par username et/ou par state si présent).
//     */
//    @PostMapping("/sync")
//    public ResponseEntity<Void> syncDiscord(@Valid @RequestBody DiscordSyncRequestDTO dto) {
//        log.info("🔁 Reçu synchro Discord : {}", dto);
//        System.out.println("✅ Données reçues du bot Discord : " + dto);
//
//        discordSyncService.handleSync(dto);
//        discordSyncService.markConnectedFrom(dto);
//
//        return ResponseEntity.ok().build();
//    }
//}
package com.sneakyDateReforged.ms_auth.controller;

import com.sneakyDateReforged.ms_auth.dto.DiscordSyncRequestDTO;
import com.sneakyDateReforged.ms_auth.service.DiscordSyncService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth/discord")
@RequiredArgsConstructor
public class DiscordController {

    private final DiscordSyncService discordSyncService;

    @PostMapping("/pending")
    public ResponseEntity<Map<String, String>> createPending() {
        String state = discordSyncService.createPendingState();
        return ResponseEntity.ok(Map.of("state", state));
    }

    /**
     * Pollé par le front toutes les 2s:
     * - state=... (cas moderne, sans pseudo)
     * - pseudo=... (legacy)
     *
     * Retourne:
     * {
     *   connected: boolean,
     *   discordPseudo: "AlwaysFailed" | null
     * }
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status(
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String pseudo
    ) {
        boolean connected = false;

        if (state != null && !state.isBlank()) {
            connected = discordSyncService.isConnectedByState(state);
        } else if (pseudo != null && !pseudo.isBlank()) {
            connected = discordSyncService.isConnected(pseudo);
        }

        String discordPseudo = discordSyncService.getLastDiscordPseudo();
        String discordId     = discordSyncService.getLastDiscordId();

        Map<String, Object> body = new HashMap<>();
        body.put("connected", connected);
        body.put("discordPseudo", discordPseudo);
        body.put("discordId", discordId);

        return ResponseEntity.ok(body);
    }

    /**
     * Appelé par le bot en POST pour pousser les infos Discord.
     * (On garde pour compat, même si maintenant on fait beaucoup via le vocal)
     */
    @PostMapping("/sync")
    public ResponseEntity<Void> syncDiscord(@Valid @RequestBody DiscordSyncRequestDTO dto) {
        log.info("🔁 Synchro Discord reçue : {}", dto);
        discordSyncService.handleSync(dto);
        discordSyncService.markConnectedFrom(dto);
        return ResponseEntity.ok().build();
    }
}

