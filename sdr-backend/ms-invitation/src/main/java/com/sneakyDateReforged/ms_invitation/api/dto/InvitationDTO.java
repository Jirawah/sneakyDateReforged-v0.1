package com.sneakyDateReforged.ms_invitation.api.dto;

import com.sneakyDateReforged.ms_invitation.domain.InvitationStatus;
import java.time.Instant;

public record InvitationDTO(
        Long id,
        Long rdvId,
        Long inviterUserId,
        Long inviteeUserId,
        InvitationStatus status,
        String message,
        Instant createdAt,
        Instant updatedAt
) {}
