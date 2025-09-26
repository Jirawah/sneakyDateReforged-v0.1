package com.sneakyDateReforged.ms_rdv.api.dto;

import com.sneakyDateReforged.ms_rdv.domain.enums.RdvStatus;
import java.time.LocalDate;
import java.time.LocalTime;

public record RdvSummaryDTO(
        Long id, String nom, String jeu, LocalDate date, LocalTime heure, RdvStatus statut
) {}
