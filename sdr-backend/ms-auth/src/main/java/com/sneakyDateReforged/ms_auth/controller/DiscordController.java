package com.sneakyDateReforged.ms_auth.controller;

import com.sneakyDateReforged.ms_auth.dto.DiscordSyncRequestDTO;
import com.sneakyDateReforged.ms_auth.service.DiscordSyncService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth/discord")
@RequiredArgsConstructor
public class DiscordController {

    private final DiscordSyncService discordSyncService;

    @PostMapping("/sync")
    public ResponseEntity<Void> syncDiscord(@Valid @RequestBody DiscordSyncRequestDTO dto) {
        log.info("🔁 Reçu synchro Discord : {}", dto);
        System.out.println("✅ Données reçues du bot Discord : " + dto);
        discordSyncService.handleSync(dto);
        return ResponseEntity.ok().build();
    }
}
