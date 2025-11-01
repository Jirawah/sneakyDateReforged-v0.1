package com.sneakyDateReforged.ms_auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileBootstrapResponse {
    Long userId;

    String email;

    // Pseudo d'application (chez toi = pseudo Discord)
    String pseudo;

    // Discord
    String discordId;
    String discordUsername;
    String discordAvatarUrl;

    // Steam
    String steamId;
    String steamPseudo;
    String steamAvatar;

    // Heures de jeu (si tu les as en BDD; sinon restent null)
    Integer hoursPubg;
    Integer hoursRust;
    Integer hoursAmongUs;
}
