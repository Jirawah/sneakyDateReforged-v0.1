package com.sneakyDateReforged.ms_profil.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatsRdvDTO {
    /**
     * Nombre total de RDV liés à l'utilisateur (créés + participations).
     */
    private Integer total;

    /**
     * RDV confirmés (où l'utilisateur est confirmé).
     */
    private Integer confirmes;

    /**
     * RDV annulés.
     */
    private Integer annules;

    /**
     * Nombre de participations (invitations acceptées/effectuées).
     */
    private Integer participations;
}
