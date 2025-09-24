package com.sneakyDateReforged.ms_profil.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO d'agrégation fidèle à la conception : identité + agrégats RDV/Amis + jeux favoris.
 * Les champs agrégés ne sont PAS stockés dans la BDD ms-profil : calcul à la volée via Feign.
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AggregatedProfileDTO {
    // Identité de base
    private Long userId;
    private String pseudo;         // affichage unique (displayName/discordUsername/steamPseudo)
    private String avatarUrl;      // avatar priorisé (discord > steam)

    // Identifiants plateformes (affichage / liens)
    private String steamId;
    private String discordId;

    // Agrégats inter-MS
    private Integer nombreAmis;
    private Integer nombreRDVs;
    private LocalDateTime prochainRDV;
    private List<String> jeuxFavoris;

    private StatsRdvDTO statsRDV;
}
