package com.sneakyDateReforged.ms_auth.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_auth_model")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAuthModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // -------- Champs saisis manuellement --------
    @Column(nullable = false, unique = true)
    private String pseudo;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String steamId;

    @Column(nullable = false)
    private String password;

    // -------- Champs récupérés via Discord --------
    @Column(unique = true)
    private String discordId;

    private String discordUsername;
    private String discordDiscriminator;
    private String discordNickname;
    private String discordAvatarUrl;

    // -------- État de validation --------
    @Column(nullable = false)
    private boolean discordValidated = false;

    @Column(nullable = false)
    private boolean steamValidated = false;

    // -------- Pour rôle ou statut futur --------
    private String role = "USER";

    // -------- Timestamps (facultatif mais recommandé) --------
    @Column(updatable = false)
    private java.time.LocalDateTime createdAt;

    private java.time.LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = java.time.LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = java.time.LocalDateTime.now();
    }
}
