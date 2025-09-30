package com.sneakyDateReforged.ms_invitation.service.impl;

import com.sneakyDateReforged.ms_invitation.api.dto.CreateInvitationRequest;
import com.sneakyDateReforged.ms_invitation.api.dto.InvitationDTO;
import com.sneakyDateReforged.ms_invitation.api.dto.UpdateInvitationStatusRequest;
import com.sneakyDateReforged.ms_invitation.client.RdvClient;
import com.sneakyDateReforged.ms_invitation.client.ParticipationClient;
import com.sneakyDateReforged.ms_invitation.client.NotifClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.sneakyDateReforged.ms_invitation.domain.Invitation;
import com.sneakyDateReforged.ms_invitation.domain.InvitationStatus;
import com.sneakyDateReforged.ms_invitation.mapper.InvitationMapper;
import com.sneakyDateReforged.ms_invitation.repository.InvitationRepository;
import com.sneakyDateReforged.ms_invitation.service.InvitationService;
import com.sneakyDateReforged.ms_invitation.service.NotifGateway;
import lombok.RequiredArgsConstructor;
import feign.FeignException;
import org.springframework.security.access.AccessDeniedException;
import com.sneakyDateReforged.ms_invitation.exception.BadRequestException;
import com.sneakyDateReforged.ms_invitation.exception.ConflictException;
import com.sneakyDateReforged.ms_invitation.exception.NotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InvitationServiceImpl implements InvitationService {

    private final InvitationRepository repo;
    private final RdvClient rdvClient;
    private final ParticipationClient participationClient;
    private final NotifGateway notifGateway;

    private static String correlationId() {
        var attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes sra) {
            return sra.getRequest().getHeader("X-Request-Id");
        }
        return null;
    }

    @Override
    public InvitationDTO invite(Long currentUserId, CreateInvitationRequest req) {
        if (currentUserId.equals(req.inviteeUserId())) {
            throw new BadRequestException("Impossible de s’inviter soi-même");
        }

        try {
            var rdv = rdvClient.get(req.rdvId());
            if (!rdv.organisateurId().equals(currentUserId)) {
                throw new AccessDeniedException("Seul l'organisateur peut inviter");
            }
            if ("ANNULE".equalsIgnoreCase(rdv.statut())) {
                throw new ConflictException("RDV annulé: invitation impossible");
            }
        } catch (FeignException.NotFound e) {
            throw new NotFoundException("RDV introuvable: " + req.rdvId());
        } catch (FeignException e) {
            throw new IllegalStateException("Service RDV indisponible");
        }

        repo.findByRdvIdAndInviteeUserId(req.rdvId(), req.inviteeUserId())
                .ifPresent(inv -> { throw new ConflictException("Invitation déjà existante"); });

        Invitation inv = Invitation.builder()
                .rdvId(req.rdvId())
                .inviterUserId(currentUserId)
                .inviteeUserId(req.inviteeUserId())
                .status(InvitationStatus.PENDING)
                .message(req.message())
                .build();

        try {
            Invitation saved = repo.save(inv);

            notifGateway.trySend(new NotifClient.NotificationPayload(
                    "INVITATION_CREATED",
                    saved.getRdvId(),
                    saved.getInviterUserId(),
                    saved.getInviteeUserId(),
                    saved.getId(),
                    saved.getMessage(),
                    correlationId()
            ));

            return InvitationMapper.toDTO(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("Invitation déjà existante");
        }
    }

    @Override
    public InvitationDTO updateStatus(Long currentUserId, Long invitationId, UpdateInvitationStatusRequest req) {
        Invitation inv = repo.findById(invitationId)
                .orElseThrow(() -> new NotFoundException("Invitation introuvable: " + invitationId));

        if (inv.getStatus() == req.status()) {
            return InvitationMapper.toDTO(inv);
        }
        if (inv.getStatus() == InvitationStatus.CANCELED && req.status() != InvitationStatus.CANCELED) {
            throw new ConflictException("Invitation annulée: modification interdite");
        }

        switch (req.status()) {
            case ACCEPTED, DECLINED -> {
                if (!inv.getInviteeUserId().equals(currentUserId)) {
                    throw new AccessDeniedException("Seul l'invité peut accepter/refuser");
                }
                inv.setStatus(req.status());
            }
            case CANCELED -> {
                if (!inv.getInviterUserId().equals(currentUserId)) {
                    throw new AccessDeniedException("Seul l'inviteur peut annuler");
                }
                inv.setStatus(InvitationStatus.CANCELED);
            }
            case PENDING -> throw new BadRequestException("Transition vers PENDING non autorisée");
        }

        // 1) on persiste
        Invitation saved = repo.save(inv);

        // 2) si ACCEPTED -> tenter la création de participation (tu l'avais déjà)
        if (req.status() == InvitationStatus.ACCEPTED) {
            try {
                participationClient.acceptFromInvitation(
                        saved.getRdvId(),
                        new ParticipationClient.ParticipationRequest(currentUserId, "JOUEUR")
                );
            } catch (FeignException e) {
                log.warn("ms-rdv indisponible: participation non créée maintenant (rdvId={}, userId={})",
                        saved.getRdvId(), currentUserId, e);
            }
        }

        // 3) 🔔 Notif de transition (après save)
        String type = switch (req.status()) {
            case ACCEPTED -> "INVITATION_ACCEPTED";
            case DECLINED -> "INVITATION_DECLINED";
            case CANCELED -> "INVITATION_CANCELED";
            case PENDING  -> null; // jamais ici
        };
        if (type != null) {
            notifGateway.trySend(new NotifClient.NotificationPayload(
                    type,
                    saved.getRdvId(),
                    saved.getInviterUserId(),
                    saved.getInviteeUserId(),
                    saved.getId(),
                    null,               // pas de message pour ces transitions
                    correlationId()
            ));
        }

        return InvitationMapper.toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvitationDTO> listForRdv(Long rdvId) {
        return repo.findByRdvId(rdvId).stream().map(InvitationMapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvitationDTO> myInvitations(Long currentUserId) {
        return repo.findByInviteeUserId(currentUserId).stream().map(InvitationMapper::toDTO).toList();
    }
}
