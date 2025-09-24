package com.sneakyDateReforged.ms_profil.dto;

import lombok.*;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Profile", description = "Profil (bio) persistant d’un utilisateur")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProfileDTO {

    @Schema(description = "Identifiant utilisateur", example = "42")
    private Long userId;

    @Schema(description = "Nom affiché", example = "Coco")
    private String displayName;

    @Schema(description = "Bio / description", example = "Full-stack player.")
    private String bio;

    @Schema(description = "Pays ISO-3166 alpha-2", example = "FR")
    private String country;

    @Schema(description = "Langues (codes ISO-639-1, séparés par des virgules)", example = "fr,en")
    private String languages;

    @Schema(description = "Âge", example = "24")
    private Integer age;

    @Schema(description = "Pseudo Steam (affichage)", example = "CocoSteam")
    private String steamPseudo;

    @Schema(description = "URL avatar Steam", example = "https://steamcdn.example.com/avatars/42.jpg", format = "uri")
    private String steamAvatar;

    @Schema(description = "Username Discord (affichage)", example = "coco#1234")
    private String discordUsername;

    @Schema(description = "URL avatar Discord", example = "https://cdn.discordapp.com/avatars/...", format = "uri")
    private String discordAvatarUrl;
}
