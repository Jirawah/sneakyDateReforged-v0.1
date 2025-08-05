package com.sneakyDateReforged.ms_auth.service;

import com.sneakyDateReforged.ms_auth.dto.DiscordSyncRequestDTO;
import com.sneakyDateReforged.ms_auth.dto.SteamProfileDTO;
import com.sneakyDateReforged.ms_auth.model.UserAuthModel;
import com.sneakyDateReforged.ms_auth.repository.UserAuthRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.mockito.Mockito.*;

class UserAuthServiceTest {

    @Mock
    private UserAuthRepository userAuthRepository;

    @InjectMocks
    private UserAuthService userAuthService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void updateSteamProfile_shouldUpdateUserSteamInfoAndSave() {
        // GIVEN
        UserAuthModel user = UserAuthModel.builder().build();

        SteamProfileDTO profile = SteamProfileDTO.builder()
                .personaName("SteamPseudo")
                .avatarFull("http://example.com/avatar.png")
                .build();

        // WHEN
        userAuthService.updateSteamProfile(user, profile);

        // THEN
        verify(userAuthRepository, times(1)).save(user);
        assert user.getSteamPseudo().equals("SteamPseudo");
        assert user.getSteamAvatar().equals("http://example.com/avatar.png");
    }

    @Test
    void syncDiscordProfile_shouldUpdateUserDiscordInfoAndSave() {
        // GIVEN
        DiscordSyncRequestDTO dto = DiscordSyncRequestDTO.builder()
                .discordId("123456789")
                .discordUsername("TestUser")
                .discordDiscriminator("1234")
                .discordNickname("Nick")
                .discordAvatarUrl("http://avatar.url")
                .build();

        UserAuthModel user = UserAuthModel.builder().build();

        when(userAuthRepository.findByDiscordId("123456789")).thenReturn(Optional.of(user));

        // WHEN
        userAuthService.syncDiscordProfile(dto);

        // THEN
        verify(userAuthRepository, times(1)).save(user);
        assert user.getDiscordUsername().equals("TestUser");
        assert user.getDiscordDiscriminator().equals("1234");
        assert user.getDiscordNickname().equals("Nick");
        assert user.getDiscordAvatarUrl().equals("http://avatar.url");
    }

    @Test
    void syncDiscordProfile_shouldDoNothingIfUserNotFound() {
        // GIVEN
        DiscordSyncRequestDTO dto = DiscordSyncRequestDTO.builder()
                .discordId("999999")
                .discordUsername("Nobody")
                .build();

        when(userAuthRepository.findByDiscordId("999999")).thenReturn(Optional.empty());

        // WHEN
        userAuthService.syncDiscordProfile(dto);

        // THEN
        verify(userAuthRepository, never()).save(any());
    }
}
