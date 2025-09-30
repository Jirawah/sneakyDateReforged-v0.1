package com.sneakyDateReforged.ms_invitation.service;

import com.sneakyDateReforged.ms_invitation.api.dto.CreateInvitationRequest;
import com.sneakyDateReforged.ms_invitation.api.dto.InvitationDTO;
import com.sneakyDateReforged.ms_invitation.api.dto.UpdateInvitationStatusRequest;

import java.util.List;

public interface InvitationService {
    InvitationDTO invite(Long currentUserId, CreateInvitationRequest req);
    InvitationDTO updateStatus(Long currentUserId, Long invitationId, UpdateInvitationStatusRequest req);
    List<InvitationDTO> listForRdv(Long rdvId);
    List<InvitationDTO> myInvitations(Long currentUserId); // reçues
}
