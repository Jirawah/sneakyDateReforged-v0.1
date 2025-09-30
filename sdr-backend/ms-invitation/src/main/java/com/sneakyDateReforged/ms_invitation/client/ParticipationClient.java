// ParticipationClient.java (ms-invitation)
package com.sneakyDateReforged.ms_invitation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "ms-rdv", contextId = "participationClient")
public interface ParticipationClient {

    @PostMapping("/rdv/{rdvId}/participations/accept")
    ParticipantDTO acceptFromInvitation(@PathVariable("rdvId") Long rdvId,
                                        @RequestBody ParticipationRequest body);

    record ParticipationRequest(Long userId, String role) {} // role = "JOUEUR" par ex.
    record ParticipantDTO(Long id, Long userId, Long rdvId, String role, String statutParticipation) {}
}
