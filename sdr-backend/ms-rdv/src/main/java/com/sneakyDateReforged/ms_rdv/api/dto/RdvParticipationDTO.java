package com.sneakyDateReforged.ms_rdv.api.dto;

import com.sneakyDateReforged.ms_rdv.domain.enums.ParticipationStatus;
import com.sneakyDateReforged.ms_rdv.domain.enums.ParticipantRole;

public record RdvParticipationDTO(
        Long participationId,
        ParticipantRole role,
        ParticipationStatus statut,
        RdvSummaryDTO rdv
) {}
