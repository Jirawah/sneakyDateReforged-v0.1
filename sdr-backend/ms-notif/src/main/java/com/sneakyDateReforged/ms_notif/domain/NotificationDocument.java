package com.sneakyDateReforged.ms_notif.domain;

import com.sneakyDateReforged.ms_notif.domain.enums.NotificationChannel;
import com.sneakyDateReforged.ms_notif.domain.enums.NotificationStatus;
import com.sneakyDateReforged.ms_notif.domain.enums.NotificationType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Document(collection = "notifications")
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class NotificationDocument {

    @Id
    private String id;

    private NotificationType type;
    private NotificationChannel channel;
    private NotificationStatus status;

    // Destinataire & contextes (ids "métier" provenant des autres ms)
    private Long userId;          // destinataire
    private Long rdvId;           // context RDV (facultatif)
    private Long organisateurId;  // facultatif
    private Long participantId;   // facultatif

    // Détails libres spécifiques à l’événement
    private Map<String, Object> payload;

    // Idempotence / traçabilité
    private String eventId;       // UUID externe de l’événement reçu

    // Horodatages
    private Instant createdAt;
    private Instant sentAt;
    private Instant readAt;

    // Rétention (TTL Mongo basé sur ce champ)
    private Instant expiresAt;
}
