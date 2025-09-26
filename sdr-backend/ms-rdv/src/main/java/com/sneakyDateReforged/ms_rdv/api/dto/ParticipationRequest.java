package com.sneakyDateReforged.ms_rdv.api.dto;

import com.sneakyDateReforged.ms_rdv.domain.enums.ParticipantRole;
import jakarta.validation.constraints.*;

public record ParticipationRequest(
        @NotNull Long userId,
        @NotNull ParticipantRole role
) {}
