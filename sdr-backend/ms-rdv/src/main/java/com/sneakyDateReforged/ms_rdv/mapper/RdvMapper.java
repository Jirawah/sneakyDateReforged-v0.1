package com.sneakyDateReforged.ms_rdv.mapper;

import com.sneakyDateReforged.ms_rdv.api.dto.*;
import com.sneakyDateReforged.ms_rdv.domain.Rdv;

public final class RdvMapper {
    private RdvMapper() {}

    public static Rdv from(CreateRdvRequest r) {
        return Rdv.builder()
                .nom(r.nom()).date(r.date()).heure(r.heure())
                .jeu(r.jeu()).statut(r.statut())
                .slots(r.slots()).organisateurId(r.organisateurId())
                .build();
    }

    public static RdvDTO toDTO(Rdv e, Integer participants) {
        return new RdvDTO(e.getId(), e.getNom(), e.getDate(), e.getHeure(),
                e.getJeu(), e.getStatut(), e.getSlots(), e.getOrganisateurId(), participants);
    }

    public static RdvSummaryDTO toSummary(Rdv e) {
        return new RdvSummaryDTO(e.getId(), e.getNom(), e.getJeu(), e.getDate(), e.getHeure(), e.getStatut());
    }
}
