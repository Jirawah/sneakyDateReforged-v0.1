package com.sneakyDateReforged.ms_rdv.mapper;

import com.sneakyDateReforged.ms_rdv.api.dto.*;
import com.sneakyDateReforged.ms_rdv.api.dto.CreateRdvRequest;
import com.sneakyDateReforged.ms_rdv.api.dto.RdvDTO;
import com.sneakyDateReforged.ms_rdv.api.dto.RdvSummaryDTO;
import com.sneakyDateReforged.ms_rdv.domain.Rdv;
import com.sneakyDateReforged.ms_rdv.api.dto.ParticipantDTO;
import com.sneakyDateReforged.ms_rdv.domain.Participant;

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

    public static void copy(UpdateRdvRequest r, Rdv e) {
        e.setNom(r.nom());
        e.setDate(r.date());
        e.setHeure(r.heure());
        e.setJeu(r.jeu());
        e.setStatut(r.statut());
        e.setSlots(r.slots());
    }

    public static ParticipantDTO toDTO(Participant p) {
        return new ParticipantDTO(
                p.getId(), p.getUserId(), p.getRdv().getId(),
                p.getRole(), p.getStatutParticipation()
        );
    }
}
