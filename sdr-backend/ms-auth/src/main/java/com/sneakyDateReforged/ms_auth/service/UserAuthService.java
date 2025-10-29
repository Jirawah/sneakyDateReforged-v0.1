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
        // 1. Infos "visuelles" Steam
        user.setSteamPseudo(steamProfile.getPersonaName());
        user.setSteamAvatar(steamProfile.getAvatarFull());

        // 2. Heures de jeu ciblées
        if (steamProfile.getGamesHours() != null) {
            // On récupère la map { "PUBG": xxx, "Rust": yyy, "Among Us": zzz }
            var hoursMap = steamProfile.getGamesHours();

            // On prend les valeurs si elles existent, sinon 0 par défaut
            user.setPubgHours(hoursMap.getOrDefault("PUBG", 0));
            user.setRustHours(hoursMap.getOrDefault("Rust", 0));
            user.setAmongUsHours(hoursMap.getOrDefault("Among Us", 0));
        } else {
            // Si pour une raison X Steam ne répond pas, on évite de laisser du vieux contenu
            user.setPubgHours(0);
            user.setRustHours(0);
            user.setAmongUsHours(0);
        }

        // 3. On pourrait marquer le compte Steam comme non validé (0), car il vient d'être lu mais pas "confirmé".
        //    Tu le fais déjà par défaut dans l'entity, donc pas obligatoire ici.
        //    user.setSteamValidated(false);

        // 4. Sauvegarde finale
        userAuthRepository.save(user);
    }
}
