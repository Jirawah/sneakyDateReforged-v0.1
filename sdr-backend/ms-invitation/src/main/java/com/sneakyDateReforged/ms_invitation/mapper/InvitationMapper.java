package com.sneakyDateReforged.ms_invitation.mapper;

import com.sneakyDateReforged.ms_invitation.api.dto.InvitationDTO;
import com.sneakyDateReforged.ms_invitation.domain.Invitation;

public final class InvitationMapper {
    private InvitationMapper(){}

    public static InvitationDTO toDTO(Invitation i) {
        return new InvitationDTO(
                i.getId(), i.getRdvId(), i.getInviterUserId(), i.getInviteeUserId(),
                i.getStatus(), i.getMessage(), i.getCreatedAt(), i.getUpdatedAt()
        );
    }
}
