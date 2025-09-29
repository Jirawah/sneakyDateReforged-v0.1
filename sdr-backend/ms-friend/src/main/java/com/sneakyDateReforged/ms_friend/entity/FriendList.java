package com.sneakyDateReforged.ms_friend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "friend_list",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_friend_list_owner_name", columnNames = {"user_id","name"})
        })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FriendList {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable=false)
    private Long userId;

    @Column(name="name", nullable=false, length=100)
    private String name;

    @Column(name="created_at", nullable=false, updatable=false, insertable=false,
            columnDefinition = "timestamp default current_timestamp")
    private Instant createdAt;
}
