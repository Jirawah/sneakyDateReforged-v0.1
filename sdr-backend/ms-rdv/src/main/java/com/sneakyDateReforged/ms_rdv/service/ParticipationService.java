package com.sneakyDateReforged.ms_rdv.service;

import com.sneakyDateReforged.ms_rdv.api.dto.ParticipantDTO;
import com.sneakyDateReforged.ms_rdv.api.dto.ParticipationRequest;
import com.sneakyDateReforged.ms_rdv.api.dto.UpdateParticipationStatusRequest;

import java.util.List;

public interface ParticipationService {
    ParticipantDTO request(Long rdvId, ParticipationRequest req);
    ParticipantDTO updateStatus(Long rdvId, Long participationId, UpdateParticipationStatusRequest req);
    List<ParticipantDTO> list(Long rdvId);
}

