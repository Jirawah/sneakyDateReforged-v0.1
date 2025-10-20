package com.sneakyDateReforged.ms_auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscordSyncRequestDTO {

    @NotBlank
    private String discordId;

    @NotBlank
    private String discordUsername;

    private String discordDiscriminator;

    private String discordNickname;

    private String discordAvatarUrl;

    /** Token de corrélation (link code) entre front et bot – optionnel. */
    private String state;
}
