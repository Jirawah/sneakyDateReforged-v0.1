package com.sneakyDateReforged.ms_auth.dto;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class SteamProfileDTO {

    private String steamId;
    private String personaName;
    private String avatar;
    private String avatarMedium;
    private String avatarFull;
    private String profileUrl;
    private String realName;
    private String countryCode;
    private boolean banned;

    @Builder.Default
    private Map<String, Integer> gamesHours = new HashMap<>();
}
