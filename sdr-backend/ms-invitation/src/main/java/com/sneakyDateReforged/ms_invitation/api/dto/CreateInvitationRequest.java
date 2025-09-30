package com.sneakyDateReforged.ms_invitation.api.dto;

import jakarta.validation.constraints.*;

public record CreateInvitationRequest(
        @NotNull Long rdvId,
        @NotNull Long inviteeUserId,
        @Size(max = 500) String message
) {}
