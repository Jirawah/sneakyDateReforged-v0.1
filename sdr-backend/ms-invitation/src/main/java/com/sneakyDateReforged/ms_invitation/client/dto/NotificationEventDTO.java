package com.sneakyDateReforged.ms_invitation.client.dto;

import java.util.List;
import java.util.Map;

/** Contrat JSON attendu par ms-notif (POST /events). */
public record NotificationEventDTO(
        String eventType,        // "INVITATION_CREATED"
        String eventId,          // ex: "INV-" + invitationId (idempotence)
        Long rdvId,
        Long organisateurId,
        Long participantId,      // NA ici
        Long userId,             // NA ici (on cible invitedUserId)
        Long invitedUserId,      // 👈 destinataire
        List<Long> recipients,   // généralement null ici
        String date,             // yyyy-MM-dd (si dispo)
        String heure,            // HH:mm (si dispo)
        String jeu,              // si dispo
        String oldStatus,        // NA
        String newStatus,        // NA
        Map<String,Object> metadata
) {}
