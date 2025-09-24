package com.sneakyDateReforged.ms_profil.dto;

import lombok.*;
import java.time.LocalDateTime;

// (optionnel) Swagger
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PublicAggregatedProfile", description = "Vue publique agrégée d’un profil (sans données sensibles)")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PublicAggregatedProfileDTO {
    @Schema(description = "Identifiant utilisateur", example = "42")
    private Long userId;

    @Schema(description = "Nom affiché priorisé", example = "Coco")
    private String pseudo;

    @Schema(description = "URL de l’avatar", example = "https://cdn.example.com/avatars/42.png")
    private String avatarUrl;

    @Schema(description = "Nombre d'amis", example = "5")
    private Integer nombreAmis;

    @Schema(description = "Nombre total de RDV", example = "12")
    private Integer nombreRDVs;

    @Schema(description = "Prochain RDV (si dispo)", type = "string", format = "date-time", example = "2025-10-15T20:30:00")
    private LocalDateTime prochainRDV;

    @Schema(description = "Statistiques RDV agrégées")
    private StatsRdvDTO statsRDV;
}
