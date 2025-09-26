package com.sneakyDateReforged.ms_rdv.service.impl;

import com.sneakyDateReforged.ms_rdv.api.dto.ParticipantDTO;
import com.sneakyDateReforged.ms_rdv.api.dto.ParticipationRequest;
import com.sneakyDateReforged.ms_rdv.api.dto.UpdateParticipationStatusRequest;
import com.sneakyDateReforged.ms_rdv.domain.Participant;
import com.sneakyDateReforged.ms_rdv.domain.Rdv;
import com.sneakyDateReforged.ms_rdv.domain.enums.ParticipationStatus;
import com.sneakyDateReforged.ms_rdv.domain.enums.RdvStatus;
import com.sneakyDateReforged.ms_rdv.mapper.RdvMapper;
import com.sneakyDateReforged.ms_rdv.repository.ParticipantRepository;
import com.sneakyDateReforged.ms_rdv.repository.RdvRepository;
import com.sneakyDateReforged.ms_rdv.service.ParticipationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @RequiredArgsConstructor
public class ParticipationServiceImpl implements ParticipationService {

    private final RdvRepository rdvRepository;
    private final ParticipantRepository participantRepository;

    @Override
    public ParticipantDTO request(Long rdvId, ParticipationRequest req) {
        Rdv rdv = rdvRepository.findById(rdvId)
                .orElseThrow(() -> new EntityNotFoundException("RDV not found: " + rdvId));
        if (rdv.getStatut() == RdvStatus.ANNULE) {
            throw new IllegalStateException("RDV annulé: demande impossible");
        }
        if (participantRepository.existsByRdvIdAndUserId(rdvId, req.userId())) {
            throw new IllegalArgumentException("Déjà participant pour ce RDV");
        }
        Participant p = Participant.builder()
                .rdv(rdv)
                .userId(req.userId())
                .role(req.role())
                .statutParticipation(ParticipationStatus.EN_ATTENTE)
                .build();
        return RdvMapper.toDTO(participantRepository.save(p));
    }

    @Override
    public ParticipantDTO updateStatus(Long rdvId, Long participationId, UpdateParticipationStatusRequest req) {
        Rdv rdv = rdvRepository.findById(rdvId)
                .orElseThrow(() -> new EntityNotFoundException("RDV not found: " + rdvId));

        Participant p = participantRepository.findById(participationId)
                .orElseThrow(() -> new EntityNotFoundException("Participation not found: " + participationId));
        if (!p.getRdv().getId().equals(rdv.getId()))
            throw new IllegalArgumentException("Participation n'appartient pas à ce RDV");

        // Règle de capacité : on ne peut CONFIRMER que s'il reste de la place
        if (req.status() == ParticipationStatus.CONFIRME) {
            long confirmed = participantRepository.countByRdvAndStatutParticipation(rdv, ParticipationStatus.CONFIRME);
            if (confirmed >= rdv.getSlots()) {
                throw new IllegalStateException("Capacité atteinte");
            }
        }

        p.setStatutParticipation(req.status());
        return RdvMapper.toDTO(participantRepository.save(p));
    }

    @Override
    public List<ParticipantDTO> list(Long rdvId) {
        Rdv rdv = rdvRepository.findById(rdvId)
                .orElseThrow(() -> new EntityNotFoundException("RDV not found: " + rdvId));
        return participantRepository.findByRdv(rdv).stream().map(RdvMapper::toDTO).toList();
    }
}
