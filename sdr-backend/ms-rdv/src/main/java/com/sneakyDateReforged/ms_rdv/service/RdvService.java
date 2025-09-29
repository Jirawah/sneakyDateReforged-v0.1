package com.sneakyDateReforged.ms_rdv.service;

import com.sneakyDateReforged.ms_rdv.api.dto.*;
import com.sneakyDateReforged.ms_rdv.domain.enums.ParticipationStatus;

import java.time.LocalDate;
import java.util.List;

public interface RdvService {
    RdvDTO create(CreateRdvRequest request);
    RdvDTO getById(Long id);
    List<RdvSummaryDTO> listByDate(LocalDate date, String jeu);
    List<RdvSummaryDTO> listByOrganisateur(Long organisateurId);
    List<RdvParticipationDTO> listParticipations(Long userId, ParticipationStatus statusOrNull);

    RdvDTO update(Long id, UpdateRdvRequest req);
    void delete(Long id);
    RdvDTO cancel(Long id);
}
