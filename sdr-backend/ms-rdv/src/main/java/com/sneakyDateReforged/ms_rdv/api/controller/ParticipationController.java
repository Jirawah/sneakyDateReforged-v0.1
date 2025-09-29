package com.sneakyDateReforged.ms_rdv.api.controller;

import com.sneakyDateReforged.ms_rdv.api.dto.ParticipantDTO;
import com.sneakyDateReforged.ms_rdv.api.dto.ParticipationRequest;
import com.sneakyDateReforged.ms_rdv.api.dto.UpdateParticipationStatusRequest;
import com.sneakyDateReforged.ms_rdv.service.ParticipationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rdv/{rdvId}/participations")
@RequiredArgsConstructor
public class ParticipationController {

    private final ParticipationService participationService;

    /**
     * Demande "publique" (utilisateur courant) avec option invited=true pour signaler une invitation.
     * invited=false (défaut) => ParticipationService.request(...)
     * invited=true              => ParticipationService.invite(...) (réservé organisateur)
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipantDTO request(@PathVariable Long rdvId,
                                  @Valid @RequestBody ParticipationRequest req,
                                  @RequestHeader(value = "X-User-Id", required = false) Long currentUserId,
                                  @RequestParam(value = "invited", defaultValue = "false") boolean invited) {
        if (invited) {
            return participationService.invite(rdvId, req, currentUserId);
        }
        return participationService.request(rdvId, req, currentUserId);
    }

    /**
     * Endpoint explicite d'invitation par l'organisateur.
     * Équivalent à POST .../participations?invited=true
     */
    @PostMapping("/invite")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipantDTO invite(@PathVariable Long rdvId,
                                 @Valid @RequestBody ParticipationRequest req,
                                 @RequestHeader(value = "X-User-Id", required = false) Long currentUserId) {
        return participationService.invite(rdvId, req, currentUserId);
    }

    @PatchMapping("/{participationId}/status")
    public ParticipantDTO updateStatus(@PathVariable Long rdvId,
                                       @PathVariable Long participationId,
                                       @Valid @RequestBody UpdateParticipationStatusRequest req) {
        return participationService.updateStatus(rdvId, participationId, req);
    }

    @GetMapping
    public List<ParticipantDTO> list(@PathVariable Long rdvId) {
        return participationService.list(rdvId);
    }

    @DeleteMapping("/{participationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void withdraw(@PathVariable Long rdvId, @PathVariable Long participationId) {
        participationService.withdraw(rdvId, participationId);
    }

    @PatchMapping("/{participationId}/withdraw")
    public ParticipantDTO withdrawAndReturn(@PathVariable Long rdvId, @PathVariable Long participationId) {
        return participationService.withdrawAndReturn(rdvId, participationId);
    }
}
