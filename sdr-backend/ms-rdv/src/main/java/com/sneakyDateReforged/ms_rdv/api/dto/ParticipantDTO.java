package com.sneakyDateReforged.ms_rdv.api.dto;

import com.sneakyDateReforged.ms_rdv.domain.enums.ParticipationStatus;
import com.sneakyDateReforged.ms_rdv.domain.enums.ParticipantRole;

public record ParticipantDTO(
        Long id, Long userId, Long rdvId,
        ParticipantRole role, ParticipationStatus statutParticipation
) {}
