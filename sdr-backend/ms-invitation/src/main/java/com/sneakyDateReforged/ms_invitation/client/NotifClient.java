package com.sneakyDateReforged.ms_invitation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "ms-notif", contextId = "notifClient")
public interface NotifClient {

    @PostMapping("/notifications")
    void send(@RequestBody NotificationPayload payload);

    record NotificationPayload(
            String type,         // INVITATION_CREATED, INVITATION_ACCEPTED, ...
            Long   rdvId,
            Long   inviterUserId,
            Long   inviteeUserId,
            Long   invitationId,
            String message,      // optionnel (ex: message d’invite)
            String correlationId // optionnel (X-Request-Id si dispo)
    ) {}
}
