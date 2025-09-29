package com.sneakyDateReforged.ms_friend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "friend_list_member",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_friend_list_member", columnNames = {"friend_list_id","member_user_id"})
        })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FriendListMember {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="friend_list_id", nullable=false)
    private Long friendListId;

    @Column(name="member_user_id", nullable=false)
    private Long memberUserId;

    @Column(name="added_at", nullable=false, updatable=false, insertable=false,
            columnDefinition = "timestamp default current_timestamp")
    private Instant addedAt;
}
