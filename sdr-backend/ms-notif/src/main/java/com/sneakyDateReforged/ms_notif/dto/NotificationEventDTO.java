package com.sneakyDateReforged.ms_notif.dto;

import com.sneakyDateReforged.ms_notif.domain.enums.NotificationType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;
import java.util.Map;

public record NotificationEventDTO(
        @NotNull NotificationType eventType,
        String eventId,                    // conseillé pour idempotence (UUID); sinon on en génère un
        Long rdvId,
        Long organisateurId,
        Long participantId,                // pour PARTICIPATION_STATUS_CHANGED
        Long userId,                       // destinataire direct possible
        Long invitedUserId,                // pour INVITATION_CREATED
        List<Long> recipients,             // fan-out explicite (optionnel)
        @Pattern(regexp="\\d{4}-\\d{2}-\\d{2}") String date,
        @Pattern(regexp="\\d{2}:\\d{2}") String heure,
        String jeu,
        String oldStatus,
        String newStatus,
        Map<String, Object> metadata       // payload libre complémentaire
) {}
