package com.sneakyDateReforged.ms_rdv.service.impl;

import com.sneakyDateReforged.ms_rdv.api.dto.CreateRdvRequest;
import com.sneakyDateReforged.ms_rdv.api.dto.RdvDTO;
import com.sneakyDateReforged.ms_rdv.api.dto.RdvSummaryDTO;
import com.sneakyDateReforged.ms_rdv.domain.Rdv;
import com.sneakyDateReforged.ms_rdv.mapper.RdvMapper;
import com.sneakyDateReforged.ms_rdv.repository.ParticipantRepository;
import com.sneakyDateReforged.ms_rdv.repository.RdvRepository;
import com.sneakyDateReforged.ms_rdv.service.RdvService;
import com.sneakyDateReforged.ms_rdv.api.dto.UpdateRdvRequest;
import com.sneakyDateReforged.ms_rdv.domain.enums.RdvStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service @RequiredArgsConstructor
public class RdvServiceImpl implements RdvService {

    private final RdvRepository rdvRepository;
    private final ParticipantRepository participantRepository;

    @Override
    public RdvDTO create(CreateRdvRequest req) {
        Rdv saved = rdvRepository.save(RdvMapper.from(req));
        int participants = (int) participantRepository.countByRdv(saved);
        return RdvMapper.toDTO(saved, participants);
    }

    @Override
    public RdvDTO getById(Long id) {
        Rdv rdv = rdvRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("RDV not found: " + id));
        int participants = (int) participantRepository.countByRdv(rdv);
        return RdvMapper.toDTO(rdv, participants);
    }

    @Override
    public List<RdvSummaryDTO> listByDate(LocalDate date, String jeu) {
        List<Rdv> list = (jeu == null || jeu.isBlank())
                ? rdvRepository.findAllByDate(date)
                : rdvRepository.findAllByDateAndJeu(date, jeu);
        return list.stream().map(RdvMapper::toSummary).toList();
    }

    @Override
    public RdvDTO update(Long id, UpdateRdvRequest req) {
        Rdv rdv = rdvRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("RDV not found: " + id));
        // Règle simple: on ne modifie pas un RDV annulé
        if (rdv.getStatut() == RdvStatus.ANNULE) {
            throw new IllegalStateException("RDV annulé: modification interdite");
        }
        RdvMapper.copy(req, rdv);
        Rdv saved = rdvRepository.save(rdv);
        int participants = (int) participantRepository.countByRdv(saved);
        return RdvMapper.toDTO(saved, participants);
    }

    @Override
    public void delete(Long id) {
        if (!rdvRepository.existsById(id)) throw new EntityNotFoundException("RDV not found: " + id);
        rdvRepository.deleteById(id);
    }

    @Override
    public RdvDTO cancel(Long id) {
        Rdv rdv = rdvRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("RDV not found: " + id));
        rdv.setStatut(RdvStatus.ANNULE);
        Rdv saved = rdvRepository.save(rdv);
        int participants = (int) participantRepository.countByRdv(saved);
        return RdvMapper.toDTO(saved, participants);
    }
}
