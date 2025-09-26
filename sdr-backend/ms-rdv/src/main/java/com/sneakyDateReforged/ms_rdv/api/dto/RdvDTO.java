package com.sneakyDateReforged.ms_rdv.api.dto;

import com.sneakyDateReforged.ms_rdv.domain.enums.RdvStatus;
import java.time.LocalDate;
import java.time.LocalTime;

public record RdvDTO(
        Long id, String nom, LocalDate date, LocalTime heure,
        String jeu, RdvStatus statut, int slots, Long organisateurId,
        Integer participants // nombre actuel (optionnel pour l’instant)
) {}
