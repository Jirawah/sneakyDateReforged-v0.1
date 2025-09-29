package com.sneakyDateReforged.ms_rdv.mapper;

import com.sneakyDateReforged.ms_rdv.api.dto.*;
import com.sneakyDateReforged.ms_rdv.api.dto.CreateRdvRequest;
import com.sneakyDateReforged.ms_rdv.api.dto.RdvDTO;
import com.sneakyDateReforged.ms_rdv.api.dto.RdvSummaryDTO;
import com.sneakyDateReforged.ms_rdv.domain.Rdv;
import com.sneakyDateReforged.ms_rdv.api.dto.ParticipantDTO;
import com.sneakyDateReforged.ms_rdv.domain.Participant;
import com.sneakyDateReforged.ms_rdv.domain.enums.RdvStatus;

public final class RdvMapper {
    private RdvMapper() {}

    public static Rdv from(CreateRdvRequest r) {
        var statut = r.statut() != null ? r.statut() : RdvStatus.OUVERT;
        return Rdv.builder()
                .nom(r.nom())
                .date(r.date())
                .heure(r.heure())
                .jeu(r.jeu())
                .statut(statut)
                .slots(r.slots())
                .organisateurId(r.organisateurId())
                .build();
    }

    public static RdvDTO toDTO(Rdv e, Integer participants) {
        return new RdvDTO(e.getId(), e.getNom(), e.getDate(), e.getHeure(),
                e.getJeu(), e.getStatut(), e.getSlots(), e.getOrganisateurId(), participants);
    }

    public static RdvSummaryDTO toSummary(Rdv e) {
        return new RdvSummaryDTO(e.getId(), e.getNom(), e.getJeu(), e.getDate(), e.getHeure(), e.getStatut(), e.getOrganisateurId());
    }

    public static void copy(UpdateRdvRequest r, Rdv e) {
        if (r.nom() != null) e.setNom(r.nom());
        if (r.date() != null) e.setDate(r.date());
        if (r.heure() != null) e.setHeure(r.heure());
        if (r.jeu() != null) e.setJeu(r.jeu());
        if (r.statut() != null) e.setStatut(r.statut());
        if (r.slots() != null) e.setSlots(r.slots());
    }

    public static ParticipantDTO toDTO(Participant p) {
        return new ParticipantDTO(
                p.getId(), p.getUserId(), p.getRdv().getId(),
                p.getRole(), p.getStatutParticipation()
        );
    }

    public static RdvParticipationDTO toParticipationDTO(Participant p) {
        return new RdvParticipationDTO(
                p.getId(),
                p.getRole(),
                p.getStatutParticipation(),
                toSummary(p.getRdv())
        );
    }
}
