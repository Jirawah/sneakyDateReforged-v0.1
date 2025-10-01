package com.sneakyDateReforged.ms_rdv.service.impl;

import com.sneakyDateReforged.ms_rdv.api.dto.ParticipantDTO;
import com.sneakyDateReforged.ms_rdv.api.dto.ParticipationRequest;
import com.sneakyDateReforged.ms_rdv.api.dto.UpdateParticipationStatusRequest;
import com.sneakyDateReforged.ms_rdv.domain.Participant;
import com.sneakyDateReforged.ms_rdv.domain.Rdv;
import com.sneakyDateReforged.ms_rdv.domain.enums.ParticipationStatus;
import com.sneakyDateReforged.ms_rdv.domain.enums.RdvStatus;
import com.sneakyDateReforged.ms_rdv.mapper.RdvMapper;
import com.sneakyDateReforged.ms_rdv.repository.ParticipantRepository;
import com.sneakyDateReforged.ms_rdv.repository.RdvRepository;
import com.sneakyDateReforged.ms_rdv.service.ParticipationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import com.sneakyDateReforged.ms_rdv.infra.notif.NotifClient;
import com.sneakyDateReforged.ms_rdv.infra.notif.dto.NotificationEventDTO;
import java.util.Map;
import java.util.UUID;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ParticipationServiceImpl implements ParticipationService {

    private final RdvRepository rdvRepository;
    private final ParticipantRepository participantRepository;
    private final NotifClient notifClient;

    // --- EXISTANT modifié : délègue vers la version avec currentUserId
    @Override
    public ParticipantDTO request(Long rdvId, ParticipationRequest req) {
        return request(rdvId, req, null);
    }

    // --- NOUVEAU : applique la règle Public/Privé avec l’utilisateur appelant
    @Override
    public ParticipantDTO request(Long rdvId, ParticipationRequest req, Long currentUserId) {
        Rdv rdv = rdvRepository.findById(rdvId)
                .orElseThrow(() -> new EntityNotFoundException("RDV not found: " + rdvId));

        if (rdv.getStatut() == RdvStatus.ANNULE) {
            throw new IllegalStateException("RDV annulé: demande impossible");
        }

        // RDV privé (FERME) => Seul l'organisateur peut créer l'entrée (invitation)
        if (rdv.getStatut() == RdvStatus.FERME) {
            if (currentUserId == null || !currentUserId.equals(rdv.getOrganisateurId())) {
                throw new AccessDeniedException("RDV privé: invitation requise");
            }
        }

        if (participantRepository.existsByRdvIdAndUserId(rdvId, req.userId())) {
            throw new IllegalArgumentException("Déjà participant pour ce RDV");
        }

        Participant p = Participant.builder()
                .rdv(rdv)
                .userId(req.userId())
                .role(req.role())
                .statutParticipation(ParticipationStatus.EN_ATTENTE)
                .build();

        return RdvMapper.toDTO(participantRepository.save(p));
    }

    @Override
    public ParticipantDTO invite(Long rdvId, ParticipationRequest req, Long currentUserId) {
        Rdv rdv = rdvRepository.findById(rdvId)
                .orElseThrow(() -> new EntityNotFoundException("RDV not found: " + rdvId));

        if (rdv.getStatut() == RdvStatus.ANNULE) {
            throw new IllegalStateException("RDV annulé: invitation impossible");
        }

        // 🔐 Seul l'organisateur peut inviter
        if (currentUserId == null || !currentUserId.equals(rdv.getOrganisateurId())) {
            throw new AccessDeniedException("Seul l'organisateur peut inviter");
        }

        // Anti-doublon
        if (participantRepository.existsByRdvIdAndUserId(rdvId, req.userId())) {
            throw new IllegalArgumentException("Utilisateur déjà présent (ou invité) sur ce RDV");
        }

        // Invitation => EN_ATTENTE (la confirmation est gérée plus tard)
        Participant p = Participant.builder()
                .rdv(rdv)
                .userId(req.userId())
                .role(req.role())
                .statutParticipation(ParticipationStatus.EN_ATTENTE)
                .build();

        return RdvMapper.toDTO(participantRepository.save(p));
    }

    @Override
    @Transactional
    public ParticipantDTO updateStatus(Long rdvId, Long participationId, UpdateParticipationStatusRequest req) {
        // 🔒 verrouille la ligne RDV pour sérialiser la vérif de capacité
        Rdv rdv = rdvRepository.findByIdForUpdate(rdvId)
                .orElseThrow(() -> new EntityNotFoundException("RDV not found: " + rdvId));

        Participant p = participantRepository.findById(participationId)
                .orElseThrow(() -> new EntityNotFoundException("Participation not found: " + participationId));

        if (!p.getRdv().getId().equals(rdv.getId())) {
            throw new IllegalArgumentException("Participation n'appartient pas à ce RDV");
        }

        // ⏮️ On garde l'ancien statut pour l'event
        ParticipationStatus oldStatus = p.getStatutParticipation();

        // Règle de capacité uniquement quand on CONFIRME
        if (req.status() == ParticipationStatus.CONFIRME) {
            long confirmed = participantRepository.countByRdvAndStatutParticipation(
                    rdv, ParticipationStatus.CONFIRME
            );
            if (confirmed >= rdv.getSlots()) {
                throw new IllegalStateException("Capacité atteinte");
            }
        }

        // Mise à jour
        p.setStatutParticipation(req.status());
        Participant saved = participantRepository.save(p);

        // 🔔 Push évènements vers ms-notif (non bloquant)
        try {
            // Ne publie que s'il y a VRAIMENT un changement
            if (oldStatus != req.status()) {
                String date = rdv.getDate() != null ? rdv.getDate().toString() : null;
                String heure = rdv.getHeure() != null ? rdv.getHeure().toString() : null;
                String jeu = rdv.getJeu();

                // 1) notif pour le participant lui-même
                var evt = new NotificationEventDTO(
                        "PARTICIPATION_STATUS_CHANGED",
                        UUID.randomUUID().toString(),
                        rdv.getId(),
                        rdv.getOrganisateurId(),
                        saved.getUserId(),          // participant concerné
                        saved.getUserId(),          // destinataire = participant
                        null,                       // invitedUserId
                        null,                       // recipients (pas de fan-out ici)
                        date, heure, jeu,
                        oldStatus != null ? oldStatus.name() : null,
                        saved.getStatutParticipation() != null ? saved.getStatutParticipation().name() : null,
                        Map.of("source", "ms-rdv")
                );
                notifClient.send(evt);

                // 2) notif pour l'organisateur (fan-out d'un seul)
                var evtOrg = new NotificationEventDTO(
                        "PARTICIPATION_STATUS_CHANGED",
                        UUID.randomUUID().toString(),
                        rdv.getId(),
                        rdv.getOrganisateurId(),
                        saved.getUserId(),
                        null,                       // destinataire direct non imposé
                        null,
                        java.util.List.of(rdv.getOrganisateurId()),
                        date, heure, jeu,
                        oldStatus != null ? oldStatus.name() : null,
                        saved.getStatutParticipation() != null ? saved.getStatutParticipation().name() : null,
                        Map.of("source", "ms-rdv")
                );
                notifClient.send(evtOrg);
            }
        } catch (Exception e) {
            log.warn("notif push failed [event=PARTICIPATION_STATUS_CHANGED, rdvId={}, participantUserId={}, oldStatus={}, newStatus={}]: {}",
                    rdv.getId(), saved.getUserId(),
                    oldStatus != null ? oldStatus.name() : null,
                    saved.getStatutParticipation() != null ? saved.getStatutParticipation().name() : null,
                    e.getMessage());
        }

        return RdvMapper.toDTO(saved);
    }


    @Override
    @Transactional(readOnly = true)
    public List<ParticipantDTO> list(Long rdvId) {
        Rdv rdv = rdvRepository.findById(rdvId)
                .orElseThrow(() -> new EntityNotFoundException("RDV not found: " + rdvId));
        return participantRepository.findByRdv(rdv).stream().map(RdvMapper::toDTO).toList();
    }

    @Override
    public void withdraw(Long rdvId, Long participationId) {
        Rdv rdv = rdvRepository.findById(rdvId)
                .orElseThrow(() -> new EntityNotFoundException("RDV not found: " + rdvId));

        if (rdv.getStatut() == RdvStatus.ANNULE) {
            throw new IllegalStateException("RDV annulé: retrait interdit");
        }

        Participant p = participantRepository.findById(participationId)
                .orElseThrow(() -> new EntityNotFoundException("Participation not found: " + participationId));

        if (!p.getRdv().getId().equals(rdvId)) {
            throw new IllegalArgumentException("Participation n'appartient pas au RDV demandé");
        }

        // idempotent
        if (p.getStatutParticipation() != ParticipationStatus.REFUSE) {
            p.setStatutParticipation(ParticipationStatus.REFUSE);
            participantRepository.save(p);
        }
    }

    @Override
    public ParticipantDTO withdrawAndReturn(Long rdvId, Long participationId) {
        withdraw(rdvId, participationId);
        Participant p = participantRepository.findById(participationId)
                .orElseThrow(() -> new EntityNotFoundException("Participation not found: " + participationId));
        return RdvMapper.toDTO(p);
    }

    @Override
    @Transactional
    public ParticipantDTO acceptFromInvitation(Long rdvId, ParticipationRequest req, Long currentUserId) {
        Rdv rdv = rdvRepository.findById(rdvId)
                .orElseThrow(() -> new EntityNotFoundException("RDV not found: " + rdvId));

        if (rdv.getStatut() == RdvStatus.ANNULE) {
            throw new IllegalStateException("RDV annulé: participation impossible");
        }

        // 💡 ICI la différence : on AUTORISE la création par l’invité même si RDV.FERME
        // (car il vient d’une invitation). Pas besoin d’être organisateur.

        if (!currentUserId.equals(req.userId())) {
            throw new AccessDeniedException("L'utilisateur courant doit être l'invité lui-même");
        }

        if (participantRepository.existsByRdvIdAndUserId(rdvId, req.userId())) {
            // Idempotent : retour direct
            return RdvMapper.toDTO(
                    participantRepository.findByRdvIdAndUserId(rdvId, req.userId()).get()
            );
        }

        // Par défaut on met EN_ATTENTE ; l’organisateur pourra CONFIRMER ensuite.
        Participant p = Participant.builder()
                .rdv(rdv)
                .userId(req.userId())
                .role(req.role())
                .statutParticipation(ParticipationStatus.EN_ATTENTE)
                .build();

        return RdvMapper.toDTO(participantRepository.save(p));
    }
}
