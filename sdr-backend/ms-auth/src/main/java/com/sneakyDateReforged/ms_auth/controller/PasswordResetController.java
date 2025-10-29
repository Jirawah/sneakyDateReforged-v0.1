package com.sneakyDateReforged.ms_auth.controller;

import com.sneakyDateReforged.ms_auth.dto.ResetPasswordRequestDTO;
import com.sneakyDateReforged.ms_auth.dto.ResetRequestDTO;
import com.sneakyDateReforged.ms_auth.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService resetService;

    @PostMapping("/reset-request")
    public ResponseEntity<Map<String, String>> requestReset(@RequestBody ResetRequestDTO dto) {
        resetService.requestReset(dto);

        // Toujours répondre 200 + message générique,
        // pour ne pas révéler si l'email existe ou pas.
        return ResponseEntity.ok(
                Map.of("message", "Email envoyé si l'adresse existe.")
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody ResetPasswordRequestDTO dto) {
        resetService.resetPassword(dto);

        return ResponseEntity.ok(
                Map.of("message", "Mot de passe mis à jour.")
        );
    }
}
