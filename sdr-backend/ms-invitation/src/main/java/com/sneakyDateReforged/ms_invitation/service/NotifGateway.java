package com.sneakyDateReforged.ms_invitation.service;

import com.sneakyDateReforged.ms_invitation.client.NotifClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotifGateway {

    private final NotifClient notifClient;

    public void trySend(NotifClient.NotificationPayload payload) {
        try {
            notifClient.send(payload);
        } catch (FeignException e) {
            log.warn("ms-notif indisponible (type={}, rdvId={}, inviter={}, invitee={}, invitationId={})",
                    payload.type(), payload.rdvId(), payload.inviterUserId(),
                    payload.inviteeUserId(), payload.invitationId(), e);
        } catch (Exception e) {
            log.warn("Notification non envoyée (type={}) : {}", payload.type(), e.toString());
        }
    }
}
