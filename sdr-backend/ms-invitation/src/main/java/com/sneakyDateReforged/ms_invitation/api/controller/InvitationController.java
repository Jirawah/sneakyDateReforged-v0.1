package com.sneakyDateReforged.ms_invitation.api.controller;

import com.sneakyDateReforged.ms_invitation.api.dto.CreateInvitationRequest;
import com.sneakyDateReforged.ms_invitation.api.dto.InvitationDTO;
import com.sneakyDateReforged.ms_invitation.api.dto.UpdateInvitationStatusRequest;
import com.sneakyDateReforged.ms_invitation.service.InvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/invitations")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService service;

    private Long currentUserId(Authentication auth) {
        if (auth == null || auth.getPrincipal() == null)
            throw new IllegalStateException("Utilisateur non authentifié");
        Object p = auth.getPrincipal();
        if (p instanceof Long l) return l;
        if (p instanceof String s) return Long.parseLong(s);
        throw new IllegalStateException("Principal non supporté: " + p);
    }

    @PostMapping
    public InvitationDTO create(@Valid @RequestBody CreateInvitationRequest req, Authentication auth) {
        return service.invite(currentUserId(auth), req);
    }

    @PatchMapping("/{invitationId}/status")
    public InvitationDTO updateStatus(@PathVariable Long invitationId,
                                      @Valid @RequestBody UpdateInvitationStatusRequest req,
                                      Authentication auth) {
        return service.updateStatus(currentUserId(auth), invitationId, req);
    }

    @GetMapping("/my")
    public List<InvitationDTO> myInvitations(Authentication auth) {
        return service.myInvitations(currentUserId(auth));
    }

    @GetMapping("/rdv/{rdvId}")
    public List<InvitationDTO> listForRdv(@PathVariable Long rdvId) {
        // Lecture liée au RDV — privée chez toi (on garde protégé)
        return service.listForRdv(rdvId);
    }
}
