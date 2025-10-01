package com.sneakyDateReforged.ms_notif.dto;

import com.sneakyDateReforged.ms_notif.domain.enums.NotificationChannel;
import com.sneakyDateReforged.ms_notif.domain.enums.NotificationStatus;
import com.sneakyDateReforged.ms_notif.domain.enums.NotificationType;

import java.time.Instant;
import java.util.Map;

public record NotificationDTO(
        String id,
        NotificationType type,
        NotificationChannel channel,
        NotificationStatus status,
        Long userId,
        Long rdvId,
        Long organisateurId,
        Long participantId,
        Map<String,Object> payload,
        String eventId,
        Instant createdAt,
        Instant sentAt,
        Instant readAt
) {}
