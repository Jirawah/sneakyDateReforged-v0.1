package com.sneakyDateReforged.ms_profil.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProfileDTO {
    private Long userId;
    private String displayName;
    private String bio;
    private String country;
    private String languages;
    private Integer age;
    private String steamPseudo;
    private String steamAvatar;
    private String discordUsername;
    private String discordAvatarUrl;
}
