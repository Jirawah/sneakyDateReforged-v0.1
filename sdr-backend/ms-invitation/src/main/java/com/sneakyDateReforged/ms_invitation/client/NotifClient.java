package com.sneakyDateReforged.ms_invitation.client;

import com.sneakyDateReforged.ms_invitation.client.dto.NotificationEventDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "ms-notif",
        contextId = "notifClient",                  // tu peux le garder
        configuration = NotifFeignConfig.class      // ajoute X-Internal-Token + X-Request-Id
)
public interface NotifClient {

    @PostMapping(value = "/events", consumes = "application/json")
    void send(@RequestBody NotificationEventDTO dto);

    // ✅ Compatibilité avec ton service existant
    record NotificationPayload(
            String type,         // INVITATION_CREATED, INVITATION_ACCEPTED, ...
            Long rdvId,
            Long inviterUserId,
            Long inviteeUserId,
            Long invitationId,
            String message,      // optionnel (ex: message d’invite)
            String correlationId // optionnel (X-Request-Id si dispo)
    ) {}
}
