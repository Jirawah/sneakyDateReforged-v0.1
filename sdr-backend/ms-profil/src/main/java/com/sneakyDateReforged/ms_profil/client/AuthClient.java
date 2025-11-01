package com.sneakyDateReforged.ms_profil.client;

import com.sneakyDateReforged.ms_profil.client.dto.DiscordUsernameResponse;
import com.sneakyDateReforged.ms_profil.client.dto.IdResponse;
import com.sneakyDateReforged.ms_profil.client.dto.ProfileImageResponse;
import com.sneakyDateReforged.ms_profil.config.FeignAuthForwarder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.sneakyDateReforged.ms_profil.client.dto.ProfileBootstrapResponse;

import java.util.List;

@FeignClient(name = "ms-auth", configuration = FeignAuthForwarder.class)
public interface AuthClient {

    @GetMapping("/auth/users/{userId}/steam-id")
    IdResponse getSteamId(@PathVariable("userId") Long userId);

    @GetMapping("/auth/users/{userId}/discord-id")
    IdResponse getDiscordId(@PathVariable("userId") Long userId);

    @GetMapping("/auth/users/{userId}/discord-username")
    DiscordUsernameResponse getDiscordUsername(@PathVariable("userId") Long userId);

    @GetMapping("/auth/users/{userId}/favorite-games")
    List<String> getFavoriteGames(@PathVariable("userId") Long userId);

    @GetMapping("/auth/users/{userId}/profile-bootstrap")
    ProfileBootstrapResponse getProfileBootstrap(@PathVariable("userId") Long userId);

    @GetMapping("/auth/users/{userId}/discord-avatar")
    ProfileImageResponse getDiscordAvatar(@PathVariable("userId") Long userId);
}
