package com.sneakyDateReforged.ms_invitation.service;

import com.sneakyDateReforged.ms_invitation.client.NotifClient;
import com.sneakyDateReforged.ms_invitation.client.NotifClient.NotificationPayload;
import com.sneakyDateReforged.ms_invitation.client.dto.NotificationEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotifGateway {

    private final NotifClient notifClient;

    /** Conversion compat: NotificationPayload -> NotificationEventDTO, puis POST /events. */
    public void trySend(NotificationPayload p) {
        // eventId stable: invitationId + type (évite les doublons si retry)
        String eventId = "INV-" + p.invitationId() + "-" + p.type();

        var dto = new NotificationEventDTO(
                p.type(),              // "INVITATION_CREATED" | "INVITATION_ACCEPTED" | ...
                eventId,
                p.rdvId(),
                p.inviterUserId(),     // organisateurId
                null,                  // participantId (NA)
                null,                  // userId direct (NA ici)
                p.inviteeUserId(),     // destinataire = invité
                null,                  // recipients (pas nécessaire ici)
                null, null, null,      // date, heure, jeu (optionnel: on peut enrichir plus tard)
                null, null,
                Map.of(
                        "message", p.message(),
                        "correlationId", p.correlationId()
                )
        );

        try {
            notifClient.send(dto);
        } catch (Exception e) {
            log.warn("notif push failed [event={}, invId={}, rdvId={}, inviter={}, invitee={}]: {}",
                    p.type(), p.invitationId(), p.rdvId(), p.inviterUserId(), p.inviteeUserId(), e.getMessage());
            // log.debug("stack:", e);
        }
    }
}
