package com.sneakyDateReforged.ms_profil.dto;

import lombok.*;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "StatsRdv", description = "Statistiques RDV d’un utilisateur")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatsRdvDTO {

    @Schema(description = "Nombre total de RDV (créés + participations)", example = "12")
    private Integer total;

    @Schema(description = "RDV confirmés", example = "8")
    private Integer confirmes;

    @Schema(description = "RDV annulés", example = "2")
    private Integer annules;

    @Schema(description = "Participations", example = "4")
    private Integer participations;
}
