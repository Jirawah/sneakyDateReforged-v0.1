package com.sneakyDateReforged.ms_rdv.api.dto;

import com.sneakyDateReforged.ms_rdv.domain.enums.ParticipationStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateParticipationStatusRequest(@NotNull ParticipationStatus status) {}
