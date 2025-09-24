package com.sneakyDateReforged.ms_profil.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "profile", uniqueConstraints = {
        @UniqueConstraint(name = "uk_profile_user", columnNames = "userId")
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column
    private String email;

    @Column(nullable = false)
    private String displayName;

    @Column(length = 1000)
    private String bio;

    private String country;
    private String languages; // simple string pour commencer
    private Integer age;

    private String steamPseudo;
    private String steamAvatar;

    private String discordUsername;
    private String discordAvatarUrl;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        if (this.displayName == null) this.displayName = "user";
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
