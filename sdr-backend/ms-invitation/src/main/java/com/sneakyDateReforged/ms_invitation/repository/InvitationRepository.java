package com.sneakyDateReforged.ms_invitation.repository;

import com.sneakyDateReforged.ms_invitation.domain.Invitation;
import com.sneakyDateReforged.ms_invitation.domain.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    Optional<Invitation> findByRdvIdAndInviteeUserId(Long rdvId, Long inviteeUserId);
    List<Invitation> findByRdvId(Long rdvId);
    List<Invitation> findByInviteeUserId(Long inviteeUserId);
    List<Invitation> findByInviteeUserIdAndStatus(Long inviteeUserId, InvitationStatus status);
}
