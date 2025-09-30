package com.sneakyDateReforged.ms_invitation.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "invitation",
        uniqueConstraints = @UniqueConstraint(name = "uq_invitation_unique", columnNames = {"rdv_id","invitee_user_id"}),
        indexes = {
                @Index(name="idx_invitation_rdv_status", columnList="rdv_id,status"),
                @Index(name="idx_invitation_invitee_status", columnList="invitee_user_id,status"),
                @Index(name="idx_invitation_inviter", columnList="inviter_user_id")
        })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Invitation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="rdv_id", nullable=false)
    private Long rdvId;

    @Column(name="inviter_user_id", nullable=false)
    private Long inviterUserId;

    @Column(name="invitee_user_id", nullable=false)
    private Long inviteeUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=16)
    private InvitationStatus status; // PENDING/ACCEPTED/DECLINED/CANCELED

    @Column(length=500)
    private String message;

    @Column(name="created_at", nullable=false, updatable=false, insertable=false,
            columnDefinition = "timestamp default current_timestamp")
    private Instant createdAt;

    @Column(name="updated_at", insertable=false, updatable=false,
            columnDefinition = "timestamp null default null on update current_timestamp")
    private Instant updatedAt;
}
