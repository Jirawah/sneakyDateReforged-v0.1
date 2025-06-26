package com.sneakyDateReforged.ms_auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DiscordSyncRequest {

    @NotBlank
    private String discordId;

    @NotBlank
    private String discordUsername;

    private String discordDiscriminator;

    private String discordNickname;

    private String discordAvatarUrl;
}
