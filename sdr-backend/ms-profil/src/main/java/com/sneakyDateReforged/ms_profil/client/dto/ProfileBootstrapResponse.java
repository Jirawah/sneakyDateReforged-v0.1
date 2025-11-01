package com.sneakyDateReforged.ms_profil.client.dto;

import lombok.Builder;

/** Payload renvoyé par ms-auth: /auth/users/{id}/profile-bootstrap */
@Builder
public record ProfileBootstrapResponse(
        Long userId,
        String email,
        String pseudo,

        String discordId,
        String discordUsername,
        String discordAvatarUrl,

        String steamId,
        String steamPseudo,
        String steamAvatar,

        Integer hoursPubg,
        Integer hoursRust,
        Integer hoursAmongUs
) {}
