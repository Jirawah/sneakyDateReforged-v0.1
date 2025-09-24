package com.sneakyDateReforged.ms_profil.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO d'agrégation fidèle à la conception : identité + agrégats RDV/Amis + jeux favoris.
 * Les champs agrégés ne sont PAS stockés dans la BDD ms-profil : calcul à la volée via Feign.
 */
@Schema(name = "AggregatedProfile", description = "Vue agrégée (bio + agrégats inter-MS)")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AggregatedProfileDTO {

    @Schema(description = "Identifiant utilisateur", example = "42")
    private Long userId;

    @Schema(description = "Nom affiché priorisé (displayName/discordUsername/steamPseudo)", example = "Coco")
    private String pseudo;

    @Schema(description = "URL de l’avatar (discord > steam)", example = "https://cdn.example.com/avatars/42.png", format = "uri")
    private String avatarUrl;

    @Schema(description = "Identifiant Steam (si disponible)", example = "76561198000000000")
    private String steamId;

    @Schema(description = "Identifiant Discord (si disponible)", example = "123456789012345678")
    private String discordId;

    @Schema(description = "Nombre d'amis", example = "5")
    private Integer nombreAmis;

    @Schema(description = "Nombre total de RDV liés", example = "12")
    private Integer nombreRDVs;

    @Schema(description = "Prochain RDV (si disponible)", type = "string", format = "date-time", example = "2025-10-15T20:30:00")
    private LocalDateTime prochainRDV;

    @Schema(description = "Liste des jeux favoris", example = "[\"Valorant\",\"Overwatch 2\"]")
    private List<String> jeuxFavoris;

    @Schema(description = "Statistiques RDV (confirmés/annulés/participations)")
    private StatsRdvDTO statsRDV;
}
