package com.sneakyDateReforged.ms_friend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "friend",
        indexes = {
                @Index(name="idx_friend_user", columnList = "user_id,status"),
                @Index(name="idx_friend_friend", columnList = "friend_id,status")
        },
        uniqueConstraints = {
                // l’unicité réelle est portée par l’index sur u_min/u_max créé par Flyway (non modélisable en JPA)
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Friend {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable=false)
    private Long userId;

    @Column(name="friend_id", nullable=false)
    private Long friendId;

    @Column(nullable=false, length=16)
    private String status; // PENDING | ACCEPTED | BLOCKED

    @Column(name="created_at", nullable=false, updatable=false, insertable=false,
            columnDefinition = "timestamp default current_timestamp")
    private Instant createdAt;

    @Column(name="updated_at", insertable=false, updatable=false,
            columnDefinition = "timestamp null default null on update current_timestamp")
    private Instant updatedAt;

    // colonnes générées via Flyway (non gérées par JPA)
    @Column(name="u_min", insertable=false, updatable=false)
    private Long uMin;

    @Column(name="u_max", insertable=false, updatable=false)
    private Long uMax;
}
