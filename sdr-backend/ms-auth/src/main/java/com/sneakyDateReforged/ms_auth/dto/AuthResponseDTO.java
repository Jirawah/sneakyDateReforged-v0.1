package com.sneakyDateReforged.ms_auth.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDTO {

    private String token;
    private String steamPseudo;
    private String steamAvatar;
    private Map<String, Integer> gamesHours;
}
