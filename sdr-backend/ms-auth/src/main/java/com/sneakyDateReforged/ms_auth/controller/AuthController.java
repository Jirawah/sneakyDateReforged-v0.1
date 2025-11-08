package com.sneakyDateReforged.ms_auth.controller;

import com.sneakyDateReforged.ms_auth.dto.AuthResponseDTO;
import com.sneakyDateReforged.ms_auth.dto.DiscordSyncRequestDTO;
import com.sneakyDateReforged.ms_auth.dto.LoginRequestDTO;
import com.sneakyDateReforged.ms_auth.dto.RegisterRequestDTO;
import com.sneakyDateReforged.ms_auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequestDTO request) {
        authService.register(request); // plus de token généré
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Inscription réussie. Tu peux te connecter."));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        AuthResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/discord-sync")
    public ResponseEntity<String> syncDiscord(@Valid @RequestBody DiscordSyncRequestDTO request) {
        authService.syncDiscord(request);
        return ResponseEntity.ok("✅ Connexion Discord synchronisée avec succès.");
    }
}

