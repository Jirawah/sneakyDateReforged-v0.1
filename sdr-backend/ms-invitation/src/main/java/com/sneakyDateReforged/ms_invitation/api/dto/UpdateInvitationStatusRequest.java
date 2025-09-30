package com.sneakyDateReforged.ms_invitation.api.dto;

import com.sneakyDateReforged.ms_invitation.domain.InvitationStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateInvitationStatusRequest(@NotNull InvitationStatus status) {}
