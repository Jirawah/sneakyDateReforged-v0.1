package com.sneakyDateReforged.ms_profil.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ProfileUpdate", description = "Payload de mise à jour de la bio")
@Getter @Setter
public class ProfileUpdateDTO {

    @Schema(description = "Nom affiché", example = "Coco", minLength = 2, maxLength = 50)
    @Size(min = 2, max = 50)
    private String displayName;

    @Schema(description = "Bio / description", example = "Joue surtout le soir.", maxLength = 1000)
    @Size(max = 1000)
    private String bio;

    @Schema(description = "Pays ISO-3166 alpha-2", example = "FR", pattern = "^[A-Z]{2}$")
    @Pattern(regexp = "^[A-Z]{2}$", message = "country must be ISO-3166 alpha-2 (e.g. FR)")
    private String country;

    @Schema(description = "Langues (codes ISO-639-1, séparés par virgules)", example = "fr,en", pattern = "^[a-z]{2}(,[a-z]{2})*$")
    @Pattern(regexp = "^[a-z]{2}(,[a-z]{2})*$", message = "languages must be like fr,en")
    private String languages;

    @Schema(description = "Âge", example = "24", minimum = "13", maximum = "120")
    @Min(13) @Max(120)
    private Integer age;
}
