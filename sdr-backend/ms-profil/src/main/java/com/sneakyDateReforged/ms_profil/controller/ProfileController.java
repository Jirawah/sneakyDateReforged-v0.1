package com.sneakyDateReforged.ms_profil.controller;

import com.sneakyDateReforged.ms_profil.dto.AggregatedProfileDTO;
import com.sneakyDateReforged.ms_profil.dto.ProfileDTO;
import com.sneakyDateReforged.ms_profil.dto.ProfileUpdateDTO;
import com.sneakyDateReforged.ms_profil.service.ProfileService;
import com.sneakyDateReforged.ms_profil.service.UserContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService service;
    private final UserContext userCtx;

    /** Vue "bio" (persistée) */
    @GetMapping("/me")
    public ProfileDTO me() {
        long userId = userCtx.getUserId();
        String email = userCtx.getEmail();
        return service.getOrCreateFor(userId, email);
    }

    /** Update "bio" (persistée) */
    @PutMapping("/me")
    public ProfileDTO update(@Valid @RequestBody ProfileUpdateDTO body) {
        return service.updateFor(userCtx.getUserId(), body);
    }

    /** Vue publique minimale (bio) */
    @GetMapping("/{userId}/public")
    public ProfileDTO publicView(@PathVariable long userId) {
        return service.getPublicView(userId);
    }

    /** Vue complète agrégée (auth requise pour ME) */
    @GetMapping("/me/full")
    public AggregatedProfileDTO meFull() {
        long userId = userCtx.getUserId();
        return service.getAggregatedView(userId);
    }

    /** Vue publique complète agrégée (permitAll) */
    @GetMapping("/{userId}/public-full")
    public AggregatedProfileDTO publicFull(@PathVariable long userId) {
        return service.getAggregatedPublicView(userId);
    }
}
