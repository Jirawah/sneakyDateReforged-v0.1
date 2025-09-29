package com.sneakyDateReforged.ms_rdv.domain;

import com.sneakyDateReforged.ms_rdv.domain.enums.ParticipantRole;
import com.sneakyDateReforged.ms_rdv.domain.enums.ParticipationStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "participant",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_participant_rdv_user", columnNames = {"rdv_id", "user_id"})
        },
        indexes = {
                @Index(name = "idx_participant_rdv", columnList = "rdv_id"),
                @Index(name = "idx_participant_user_status", columnList = "user_id, statut_participation")
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Participant {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable=false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rdv_id", nullable = false, foreignKey = @ForeignKey(name = "fk_participant_rdv"))
    private Rdv rdv;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=50)
    private ParticipantRole role;

    @Enumerated(EnumType.STRING)
    @Column(name="statut_participation", nullable=false, length=50)
    private ParticipationStatus statutParticipation;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participant that = (Participant) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
