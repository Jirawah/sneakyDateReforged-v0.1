package com.sneakyDateReforged.ms_rdv.infra.notif.dto;

import java.util.List;
import java.util.Map;

/** DTO contract pour POST /events de ms-notif. */
public record NotificationEventDTO(
        String eventType,        // "RDV_CREATED" | "RDV_CANCELED" | "PARTICIPATION_STATUS_CHANGED"
        String eventId,          // UUID pour idempotence
        Long rdvId,
        Long organisateurId,
        Long participantId,      // id utilisateur du participant si concerné
        Long userId,             // destinataire direct (ex: participant)
        Long invitedUserId,      // non utilisé côté ms-rdv (réservé invitations)
        List<Long> recipients,   // fan-out explicite (ex: tous les participants lors d'une annulation)
        String date,             // yyyy-MM-dd
        String heure,            // HH:mm
        String jeu,
        String oldStatus,        // pour PARTICIPATION_STATUS_CHANGED
        String newStatus,        // pour PARTICIPATION_STATUS_CHANGED
        Map<String, Object> metadata
) {}
