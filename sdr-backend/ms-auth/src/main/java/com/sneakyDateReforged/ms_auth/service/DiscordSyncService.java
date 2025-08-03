package com.sneakyDateReforged.ms_auth.service;

import com.sneakyDateReforged.ms_auth.dto.DiscordSyncRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscordSyncService {

    private final UserAuthService userAuthService;

    public void handleSync(DiscordSyncRequestDTO dto) {
        log.info("📦 Traitement de la synchro Discord : {}", dto.getDiscordUsername());
        System.out.println("🎯 DiscordSyncService.handleSync exécuté pour : " + dto.getDiscordUsername());

        // Mise à jour de l'utilisateur en base avec les infos Discord
        userAuthService.syncDiscordProfile(dto);
    }
}
