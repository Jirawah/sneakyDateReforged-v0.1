package com.sneakyDateReforged.ms_rdv.api.dto;

import com.sneakyDateReforged.ms_rdv.domain.enums.RdvStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;

public record UpdateRdvRequest(
        @NotBlank String nom,
        @NotNull LocalDate date,
        @NotNull LocalTime heure,
        @NotBlank String jeu,
        @NotNull RdvStatus statut,
        @Min(1) Integer slots
) {}
