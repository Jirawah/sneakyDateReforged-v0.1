package com.sneakyDateReforged.ms_rdv.domain;

import com.sneakyDateReforged.ms_rdv.domain.enums.ParticipantRole;
import com.sneakyDateReforged.ms_rdv.domain.enums.ParticipationStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="participant")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Participant {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable=false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="rdv_id")
    private com.sneakyDateReforged.ms_rdv.domain.Rdv rdv;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=50)
    private ParticipantRole role;

    @Enumerated(EnumType.STRING)
    @Column(name="statut_participation", nullable=false, length=50)
    private ParticipationStatus statutParticipation;
}
