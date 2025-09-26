package com.sneakyDateReforged.ms_rdv.service;

import com.sneakyDateReforged.ms_rdv.api.dto.CreateRdvRequest;
import com.sneakyDateReforged.ms_rdv.api.dto.RdvDTO;
import com.sneakyDateReforged.ms_rdv.api.dto.RdvSummaryDTO;
import com.sneakyDateReforged.ms_rdv.api.dto.UpdateRdvRequest;

import java.time.LocalDate;
import java.util.List;

public interface RdvService {
    RdvDTO create(CreateRdvRequest request);
    RdvDTO getById(Long id);
    List<RdvSummaryDTO> listByDate(LocalDate date, String jeu);

    RdvDTO update(Long id, UpdateRdvRequest req);
    void delete(Long id);
    RdvDTO cancel(Long id);
}
