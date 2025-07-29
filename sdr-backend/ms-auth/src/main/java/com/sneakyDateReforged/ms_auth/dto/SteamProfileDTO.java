package com.sneakyDateReforged.ms_auth.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
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

    // Clé = nom du jeu, valeur = nombre d’heures
    private Map<String, Integer> gamesHours = new HashMap<>();
}
