package com.sneakyDateReforged.ms_rdv.api.dto;

import com.sneakyDateReforged.ms_rdv.domain.enums.RdvStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;

public record CreateRdvRequest(
        @NotBlank String nom,
        @NotNull LocalDate date,
        @NotNull LocalTime heure,
        @NotBlank String jeu,
        @NotNull RdvStatus statut,     // OUVERT / FERME / ANNULE
        @Min(1) int slots,
        @NotNull Long organisateurId   // TODO: plus tard, le déduire du JWT
) {}
