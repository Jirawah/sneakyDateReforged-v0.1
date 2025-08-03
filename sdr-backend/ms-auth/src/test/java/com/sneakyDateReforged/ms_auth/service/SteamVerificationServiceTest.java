package com.sneakyDateReforged.ms_auth.service;

import com.sneakyDateReforged.ms_auth.dto.SteamProfileDTO;
import com.sneakyDateReforged.ms_auth.exception.InvalidSteamIdException;
import com.sneakyDateReforged.ms_auth.exception.SteamUnavailableException;
import com.sneakyDateReforged.ms_auth.util.TestEnvLoader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SteamVerificationServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private String steamId;
    private SteamVerificationService steamVerificationService;

    @BeforeEach
    void setUp() {
        TestEnvLoader.loadEnv();
        steamId = TestEnvLoader.get("TEST_STEAM_ID", "123456789");
        String apiKey = TestEnvLoader.get("STEAM_API_KEY", "dummy-api-key");

        steamVerificationService = new SteamVerificationService(restTemplate);
        ReflectionTestUtils.setField(steamVerificationService, "steamApiKey", apiKey);
    }

    @Test
    void shouldReturnValidProfileWhenSteamIdIsValid() {
        JSONObject fakePlayer = new JSONObject()
                .put("personaname", "SteamUser")
                .put("avatarfull", "http://avatar.url")
                .put("communityvisibilitystate", 3)
                .put("profileurl", "http://steamcommunity.com/id/SteamUser")
                .put("personastate", 1)
                .put("realname", "Test Real Name")
                .put("timecreated", "1234567890")
                .put("loccountrycode", "FR");

        JSONArray playersArray = new JSONArray().put(fakePlayer);
        JSONObject summaryResponse = new JSONObject().put("response", new JSONObject().put("players", playersArray));

        JSONObject bansResponse = new JSONObject()
                .put("players", new JSONArray().put(new JSONObject()
                        .put("VACBanned", false)
                        .put("NumberOfGameBans", 0)
                ));

        // 🔧 Ajout du mock manquant
        JSONObject ownedGamesResponse = new JSONObject()
                .put("response", new JSONObject()
                        .put("games", new JSONArray()));

        when(restTemplate.getForObject(contains("GetPlayerSummaries"), eq(String.class)))
                .thenReturn(summaryResponse.toString());

        when(restTemplate.getForObject(contains("GetPlayerBans"), eq(String.class)))
                .thenReturn(bansResponse.toString());

        when(restTemplate.getForObject(contains("GetOwnedGames"), eq(String.class)))
                .thenReturn(ownedGamesResponse.toString());

        SteamProfileDTO result = steamVerificationService.verifySteamUser(steamId);

        assertNotNull(result);
        assertEquals("SteamUser", result.getPersonaName());
        assertEquals("http://avatar.url", result.getAvatarFull());
        assertFalse(result.isBanned());
    }

    @Test
    void shouldThrowSteamUnavailableExceptionOnRestError() {
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenThrow(new RestClientException("Steam API down"));

        assertThrows(SteamUnavailableException.class, () -> steamVerificationService.verifySteamUser(steamId));
    }

    @Test
    void shouldThrowInvalidSteamIdExceptionIfNoPlayersFound() {
        JSONObject emptyResponse = new JSONObject().put("response", new JSONObject().put("players", new JSONArray()));

        when(restTemplate.getForObject(contains("GetPlayerSummaries"), eq(String.class)))
                .thenReturn(emptyResponse.toString());

        assertThrows(InvalidSteamIdException.class, () -> steamVerificationService.verifySteamUser(steamId));
    }

    @Test
    void shouldReturnBannedTrueIfGameBansPresent() {
        JSONObject fakePlayer = new JSONObject()
                .put("personaname", "SteamUser")
                .put("avatarfull", "http://avatar.url");

        JSONArray playersArray = new JSONArray().put(fakePlayer);
        JSONObject summaryResponse = new JSONObject().put("response", new JSONObject().put("players", playersArray));

        JSONObject banResponse = new JSONObject()
                .put("players", new JSONArray().put(new JSONObject()
                        .put("NumberOfGameBans", 1)
                        .put("VACBanned", true)
                ));

        JSONObject ownedGamesResponse = new JSONObject()
                .put("response", new JSONObject()
                        .put("games", new JSONArray()));

        when(restTemplate.getForObject(contains("GetPlayerSummaries"), eq(String.class)))
                .thenReturn(summaryResponse.toString());

        when(restTemplate.getForObject(contains("GetPlayerBans"), eq(String.class)))
                .thenReturn(banResponse.toString());

        when(restTemplate.getForObject(contains("GetOwnedGames"), eq(String.class)))
                .thenReturn(ownedGamesResponse.toString());

        SteamProfileDTO result = steamVerificationService.verifySteamUser(steamId);

        assertNotNull(result);
        assertTrue(result.isBanned());
    }
}
