package com.sneakyDateReforged.ms_rdv.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sneakyDateReforged.ms_rdv.domain.enums.RdvStatus;
import java.time.LocalDate;
import java.time.LocalTime;

public record RdvDTO(
        Long id,
        String nom,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        java.time.LocalDate date,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
        java.time.LocalTime heure,
        String jeu,
        RdvStatus statut,
        int slots,
        Long organisateurId,
        Integer participants
) {}
