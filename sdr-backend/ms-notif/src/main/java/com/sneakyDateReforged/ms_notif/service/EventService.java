package com.sneakyDateReforged.ms_notif.service;

import com.sneakyDateReforged.ms_notif.domain.NotificationDocument;
import com.sneakyDateReforged.ms_notif.domain.enums.NotificationStatus;
import com.sneakyDateReforged.ms_notif.domain.enums.NotificationType;
import com.sneakyDateReforged.ms_notif.dto.NotificationEventDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final NotificationService notificationService;

    public List<NotificationDocument> handleEvent(NotificationEventDTO dto) {
        String eventId = dto.eventId() != null && !dto.eventId().isBlank()
                ? dto.eventId()
                : UUID.randomUUID().toString();

        // Détermine les destinataires
        List<Long> recipients = deriveRecipients(dto);
        if (recipients.isEmpty()) return List.of();

        Map<String,Object> payload = buildPayload(dto);

        List<NotificationDocument> created = new ArrayList<>();
        Instant now = Instant.now();

        for (Long userId : recipients.stream().filter(Objects::nonNull).collect(Collectors.toSet())) {
            NotificationDocument doc = NotificationDocument.builder()
                    .type(dto.eventType())
                    .status(NotificationStatus.PENDING)
                    .userId(userId)
                    .rdvId(dto.rdvId())
                    .organisateurId(dto.organisateurId())
                    .participantId(dto.participantId())
                    .payload(payload)
                    .eventId(eventId)
                    .createdAt(now)
                    .build();

            created.add(notificationService.createInAppNotification(doc));
        }
        return created;
    }

    private List<Long> deriveRecipients(NotificationEventDTO dto) {
        if (dto.recipients() != null && !dto.recipients().isEmpty()) return dto.recipients();

        NotificationType t = dto.eventType();
        switch (t) {
            case INVITATION_CREATED -> {
                return dto.invitedUserId() != null ? List.of(dto.invitedUserId()) : List.of();
            }
            case PARTICIPATION_STATUS_CHANGED -> {
                List<Long> list = new ArrayList<>();
                if (dto.organisateurId() != null) list.add(dto.organisateurId());
                if (dto.userId() != null) list.add(dto.userId());
                return list;
            }
            case RDV_CREATED -> {
                return dto.organisateurId() != null ? List.of(dto.organisateurId()) : List.of();
            }
            case RDV_CANCELED -> {
                // sans liste explicite de participants, on ne peut notifier qu'un destinataire si fourni
                if (dto.recipients() != null && !dto.recipients().isEmpty()) {
                    return dto.recipients();
                } else if (dto.userId() != null) {
                    return List.of(dto.userId());
                } else {
                    return List.of();
                }
            }
            default -> { return List.of(); }
        }
    }

    private Map<String, Object> buildPayload(NotificationEventDTO dto) {
        Map<String, Object> payload = new LinkedHashMap<>();
        if (dto.jeu() != null) payload.put("jeu", dto.jeu());
        if (dto.date() != null) payload.put("date", dto.date());
        if (dto.heure() != null) payload.put("heure", dto.heure());
        if (dto.oldStatus() != null) payload.put("oldStatus", dto.oldStatus());
        if (dto.newStatus() != null) payload.put("newStatus", dto.newStatus());
        if (dto.metadata() != null && !dto.metadata().isEmpty()) payload.putAll(dto.metadata());
        return payload;
    }
}
