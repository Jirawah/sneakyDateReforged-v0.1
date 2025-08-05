package com.sneakyDateReforged.ms_auth.service;

import com.sneakyDateReforged.ms_auth.dto.DiscordSyncRequestDTO;
import com.sneakyDateReforged.ms_auth.repository.UserAuthRepository;
import com.sneakyDateReforged.ms_auth.model.UserAuthModel;
import org.junit.jupiter.api.*;
import org.mockito.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class DiscordSyncServiceTest {

    @Mock
    private UserAuthService userAuthService;

    @InjectMocks
    private DiscordSyncService discordSyncService;

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
    void handleSync_shouldCallSyncDiscordProfile_onUserAuthService() {
        // GIVEN
        DiscordSyncRequestDTO dto = DiscordSyncRequestDTO.builder()
                .discordId("123456789")
                .discordUsername("TestUser")
                .discordDiscriminator("1234")
                .discordNickname("Nick")
                .discordAvatarUrl("http://avatar.com")
                .build();

        // WHEN
        discordSyncService.handleSync(dto);

        // THEN
        verify(userAuthService, times(1)).syncDiscordProfile(dto);
    }
}
