package com.sneakyDateReforged.ms_auth.service;

import com.sneakyDateReforged.ms_auth.dto.DiscordSyncRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DiscordSyncService {

    public void handleSync(DiscordSyncRequestDTO dto) {
        log.info("📦 Traitement de la synchro Discord : {}", dto.getDiscordUsername());
        System.out.println("🎯 DiscordSyncService.handleSync exécuté pour : " + dto.getDiscordUsername());

        // Ici, à implémenter plus tard : lien avec UserAuthModel, vérif base, etc.
    }
}
