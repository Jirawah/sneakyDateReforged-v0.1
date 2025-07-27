package com.sneakyDateReforged.ms_auth.controller;

import com.sneakyDateReforged.ms_auth.dto.ResetPasswordRequestDTO;
import com.sneakyDateReforged.ms_auth.dto.ResetRequestDTO;
import com.sneakyDateReforged.ms_auth.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService resetService;

    @PostMapping("/reset-request")
    public ResponseEntity<?> requestReset(@RequestBody ResetRequestDTO dto) {
        resetService.requestReset(dto);
        return ResponseEntity.ok("Email envoyé si l'adresse existe.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequestDTO dto) {
        resetService.resetPassword(dto);
        return ResponseEntity.ok("Mot de passe mis à jour.");
    }
}
