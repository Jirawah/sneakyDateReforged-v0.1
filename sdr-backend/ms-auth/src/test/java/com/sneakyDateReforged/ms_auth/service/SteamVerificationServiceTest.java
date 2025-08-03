package com.sneakyDateReforged.ms_auth.service;

import com.sneakyDateReforged.ms_auth.dto.SteamProfileDTO;
import com.sneakyDateReforged.ms_auth.exception.InvalidSteamIdException;
import com.sneakyDateReforged.ms_auth.exception.SteamUnavailableException;
import com.sneakyDateReforged.ms_auth.util.TestEnvLoader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SteamVerificationServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private String steamId;
    private SteamVerificationService steamVerificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Utilisation centralisée du TestEnvLoader
        TestEnvLoader.loadEnv();
        steamId = TestEnvLoader.get("TEST_STEAM_ID", "123456789");
        String apiKey = TestEnvLoader.get("STEAM_API_KEY", "dummy-api-key");

        steamVerificationService = new SteamVerificationService();
        ReflectionTestUtils.setField(steamVerificationService, "steamApiKey", apiKey);
        ReflectionTestUtils.setField(steamVerificationService, "restTemplate", restTemplate);
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
        JSONObject response = new JSONObject().put("response", new JSONObject().put("players", playersArray));

        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(response.toString());

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

        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(emptyResponse.toString());

        assertThrows(InvalidSteamIdException.class, () -> steamVerificationService.verifySteamUser(steamId));
    }

    @Test
    void shouldReturnBannedTrueIfGameBansPresent() {
        JSONObject player = new JSONObject()
                .put("personaname", "SteamUser")
                .put("avatarfull", "http://avatar.url");

        JSONArray playersArray = new JSONArray().put(player);
        JSONObject steamResponse = new JSONObject().put("response", new JSONObject().put("players", playersArray));

        JSONObject banResponse = new JSONObject()
                .put("players", new JSONArray().put(new JSONObject().put("NumberOfGameBans", 1)));

        when(restTemplate.getForObject(contains("GetPlayerSummaries"), eq(String.class)))
                .thenReturn(steamResponse.toString());

        when(restTemplate.getForObject(contains("GetPlayerBans"), eq(String.class)))
                .thenReturn(banResponse.toString());

        SteamProfileDTO result = steamVerificationService.verifySteamUser(steamId);

        assertNotNull(result);
        assertTrue(result.isBanned());
    }
}
