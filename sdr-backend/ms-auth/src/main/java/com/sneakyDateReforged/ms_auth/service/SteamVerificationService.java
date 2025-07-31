package com.sneakyDateReforged.ms_auth.service;

import com.sneakyDateReforged.ms_auth.dto.SteamProfileDTO;
import com.sneakyDateReforged.ms_auth.exception.InvalidSteamIdException;
import com.sneakyDateReforged.ms_auth.exception.SteamUnavailableException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
public class SteamVerificationService {

    @Value("${steam.api.key}")
    private String steamApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    private final Map<Integer, String> targetGames = Map.of(
            578080, "PUBG",
            252490, "Rust",
            945360, "Among Us"
    );

    public SteamProfileDTO verifySteamUser(String steamId) {
        try {
            SteamProfileDTO dto = new SteamProfileDTO();
            dto.setSteamId(steamId);

            // 1. Profil joueur
            String profileUrl = UriComponentsBuilder
                    .fromHttpUrl("https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v2/")
                    .queryParam("key", steamApiKey)
                    .queryParam("steamids", steamId)
                    .toUriString();

            String profileResponse = restTemplate.getForObject(profileUrl, String.class);
            JSONArray players = new JSONObject(profileResponse).getJSONObject("response").getJSONArray("players");

            if (players.isEmpty()) {
                throw new InvalidSteamIdException("Le Steam ID fourni est invalide ou ne correspond à aucun utilisateur Steam.");
            }

            JSONObject player = players.getJSONObject(0);
            dto.setPersonaName(player.optString("personaname"));
            dto.setAvatar(player.optString("avatar"));
            dto.setAvatarMedium(player.optString("avatarmedium"));
            dto.setAvatarFull(player.optString("avatarfull"));
            dto.setProfileUrl(player.optString("profileurl"));
            dto.setRealName(player.optString("realname"));
            dto.setCountryCode(player.optString("loccountrycode"));

            // 2. Infos de bannissement
            String bansUrl = UriComponentsBuilder
                    .fromHttpUrl("https://api.steampowered.com/ISteamUser/GetPlayerBans/v1/")
                    .queryParam("key", steamApiKey)
                    .queryParam("steamids", steamId)
                    .toUriString();

            String bansResponse = restTemplate.getForObject(bansUrl, String.class);
            JSONArray bans = new JSONObject(bansResponse).getJSONArray("players");

            if (!bans.isEmpty()) {
                JSONObject ban = bans.getJSONObject(0);
                boolean isBanned = ban.getBoolean("VACBanned") || ban.getInt("NumberOfGameBans") > 0;
                dto.setBanned(isBanned);
            }

            // 3. Heures de jeu
            String gamesUrl = UriComponentsBuilder
                    .fromHttpUrl("https://api.steampowered.com/IPlayerService/GetOwnedGames/v1/")
                    .queryParam("key", steamApiKey)
                    .queryParam("steamid", steamId)
                    .queryParam("include_appinfo", "true")
                    .queryParam("include_played_free_games", "true")
                    .toUriString();

            String gamesResponse = restTemplate.getForObject(gamesUrl, String.class);
            JSONArray games = new JSONObject(gamesResponse)
                    .getJSONObject("response")
                    .optJSONArray("games");

            if (games != null) {
                for (int i = 0; i < games.length(); i++) {
                    JSONObject game = games.getJSONObject(i);
                    int appId = game.getInt("appid");

                    if (targetGames.containsKey(appId)) {
                        int minutes = game.optInt("playtime_forever", 0);
                        int hours = minutes / 60;
                        dto.getGamesHours().put(targetGames.get(appId), hours);
                    }
                }
            }

            return dto;

        } catch (RestClientException | NullPointerException | IllegalArgumentException ex) {
            throw new SteamUnavailableException("L'API Steam est actuellement indisponible. Merci de réessayer plus tard.", ex);
        }
    }
}
