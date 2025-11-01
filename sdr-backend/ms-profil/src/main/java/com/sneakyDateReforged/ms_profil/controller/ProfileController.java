package com.sneakyDateReforged.ms_profil.controller;

import com.sneakyDateReforged.ms_profil.dto.AggregatedProfileDTO;
import com.sneakyDateReforged.ms_profil.dto.ProfileDTO;
import com.sneakyDateReforged.ms_profil.dto.ProfileUpdateDTO;
import com.sneakyDateReforged.ms_profil.service.ProfileService;
import com.sneakyDateReforged.ms_profil.service.UserContext;
import com.sneakyDateReforged.ms_profil.dto.PublicAggregatedProfileDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@Tag(name = "Profiles", description = "Endpoints de gestion du profil (bio) et vues agrégées")
@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService service;
    private final UserContext userCtx;

    /** Vue "bio" (persistée) */
    @Operation(
            summary = "Mon profil (bio)",
            description = "Retourne la bio du profil et le crée si absent (ID issu du JWT). **JWT requis.**"
    )
    @GetMapping("/me")
    public ProfileDTO me() {
        long userId = userCtx.getUserId();
        String email = userCtx.getEmail();
        return service.getOrCreateFor(userId, email);
    }

    /** Update "bio" (persistée) */
    @Operation(
            summary = "Mettre à jour ma bio",
            description = "Met à jour les champs du profil (validation côté DTO). **JWT requis.**"
    )
    @PutMapping("/me")
    public ProfileDTO update(@Valid @RequestBody ProfileUpdateDTO body) {
        return service.updateFor(userCtx.getUserId(), body);
    }

    /** Vue publique minimale (bio) */
    @Operation(
            summary = "Vue publique (bio) d’un profil",
            description = "Consultation publique de la bio d’un utilisateur par son userId. **Pas de JWT requis.**"
    )
    @GetMapping("/{userId}/public")
    public ProfileDTO publicView(
            @Parameter(description = "Identifiant utilisateur (source ms-auth)", example = "1")
            @PathVariable long userId) {
        return service.getPublicView(userId);
    }

    /** Vue complète agrégée (auth requise pour ME) */
    @Operation(
            summary = "Ma vue complète agrégée",
            description = "Vue complète (bio + agrégats amis/RDV/jeux) calculée à la volée. **JWT requis.**"
    )
//    @GetMapping("/me/full")
//    public AggregatedProfileDTO meFull() {
//        long userId = userCtx.getUserId();
//        return service.getAggregatedView(userId);
//    }
    @GetMapping("/me/full")
    public AggregatedProfileDTO meFull() {
        long userId = userCtx.getUserId();
        String email = userCtx.getEmail();

        // ✅ On s'assure que la bio existe (création + snapshot depuis ms-auth si absent)
        service.getOrCreateFor(userId, email);

        // Puis on calcule la vue agrégée (amis/RDV/jeux + pseudo/avatar fusionnés)
        return service.getAggregatedView(userId);
    }

    /** Vue publique complète agrégée (permitAll) */
    @Operation(
            summary = "Vue publique complète agrégée",
            description = "Vue complète (bio + agrégats publics) pour un utilisateur. **Pas de JWT requis.**"
    )
    @GetMapping("/{userId}/public-full")
    public PublicAggregatedProfileDTO publicFull(@PathVariable long userId) {
        return service.getAggregatedPublicView(userId);
    }
}
