package com.sneakyDateReforged.ms_auth.service;

import com.sneakyDateReforged.ms_auth.dto.DiscordSyncRequestDTO;
import com.sneakyDateReforged.ms_auth.dto.SteamProfileDTO;
import com.sneakyDateReforged.ms_auth.model.UserAuthModel;
import com.sneakyDateReforged.ms_auth.repository.UserAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAuthService {

    private final UserAuthRepository userAuthRepository;

    public void syncDiscordProfile(DiscordSyncRequestDTO dto) {
        userAuthRepository.findByDiscordId(dto.getDiscordId()).ifPresent(user -> {
            user.setDiscordUsername(dto.getDiscordUsername());
            user.setDiscordAvatarUrl(dto.getDiscordAvatarUrl());
            user.setDiscordDiscriminator(dto.getDiscordDiscriminator());
            user.setDiscordNickname(dto.getDiscordNickname());
            userAuthRepository.save(user);
        });
    }

    public void updateSteamProfile(UserAuthModel user, SteamProfileDTO steamProfile) {
        user.setSteamPseudo(steamProfile.getPersonaName());
        user.setSteamAvatar(steamProfile.getAvatarFull());
        userAuthRepository.save(user);
    }
}
