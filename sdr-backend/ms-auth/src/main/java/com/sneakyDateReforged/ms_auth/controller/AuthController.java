package com.sneakyDateReforged.ms_auth.controller;

import com.sneakyDateReforged.ms_auth.dto.AuthResponse;
import com.sneakyDateReforged.ms_auth.dto.DiscordSyncRequest;
import com.sneakyDateReforged.ms_auth.dto.LoginRequest;
import com.sneakyDateReforged.ms_auth.dto.RegisterRequest;
import com.sneakyDateReforged.ms_auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/discord-sync")
    public ResponseEntity<String> syncDiscord(@Valid @RequestBody DiscordSyncRequest request) {
        authService.syncDiscord(request);
        return ResponseEntity.ok("Connexion Discord synchronisée avec succès.");
    }
}
