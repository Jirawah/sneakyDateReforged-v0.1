package com.sneakyDateReforged.ms_rdv.service;

import com.sneakyDateReforged.ms_rdv.api.dto.ParticipantDTO;
import com.sneakyDateReforged.ms_rdv.api.dto.ParticipationRequest;
import com.sneakyDateReforged.ms_rdv.api.dto.UpdateParticipationStatusRequest;

import java.util.List;

public interface ParticipationService {
    ParticipantDTO request(Long rdvId, ParticipationRequest req);
    ParticipantDTO request(Long rdvId, ParticipationRequest req, Long currentUserId);
    ParticipantDTO invite(Long rdvId, ParticipationRequest req, Long currentUserId);
    ParticipantDTO updateStatus(Long rdvId, Long participationId, UpdateParticipationStatusRequest req);
    List<ParticipantDTO> list(Long rdvId);
    void withdraw(Long rdvId, Long participationId);
    com.sneakyDateReforged.ms_rdv.api.dto.ParticipantDTO withdrawAndReturn(Long rdvId, Long participationId);
    default ParticipantDTO request(Long rdvId, ParticipationRequest req, Long currentUserId, boolean keepLegacy) {
        return request(rdvId, req, currentUserId);
    }
}

