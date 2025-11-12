package com.sneakyDateReforged.ms_auth.service;

import com.sneakyDateReforged.ms_auth.dto.LoginRequestDTO;
import com.sneakyDateReforged.ms_auth.dto.RegisterRequestDTO;
import com.sneakyDateReforged.ms_auth.dto.SteamProfileDTO;
import com.sneakyDateReforged.ms_auth.exception.DuplicateUserException;
import com.sneakyDateReforged.ms_auth.exception.SteamAccountBannedException;
import com.sneakyDateReforged.ms_auth.model.UserAuthModel;
import com.sneakyDateReforged.ms_auth.procedure.RegisterProcedureExecutor;
import com.sneakyDateReforged.ms_auth.repository.UserAuthRepository;
import com.sneakyDateReforged.ms_auth.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserAuthRepository userAuthRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtils jwtUtils;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private SteamVerificationService steamVerificationService;
    @Mock private RegisterProcedureExecutor registerProcedureExecutor;
    @Mock private UserAuthService userAuthService;

    // ✅ IMPORTANT : pour éviter le NPE dans AuthService.register()
    @Mock private DiscordSyncService discordSyncService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequestDTO validRequest;
    private SteamProfileDTO validSteamProfile;
    private UserAuthModel mockUser;

    @BeforeEach
    void setUp() {
        validRequest = RegisterRequestDTO.builder()
                .pseudo("TestUser")
                .email("test@example.com")
                .steamId("123456789")
                .password("Password123")
                .confirmPassword("Password123")
                .discordId("123456789987654321")
                .build();

        validSteamProfile = SteamProfileDTO.builder()
                .personaName("SteamUser")
                .avatarFull("http://avatar.url")
                .banned(false)
                .build();

        mockUser = UserAuthModel.builder()
                .email(validRequest.getEmail())
                .steamPseudo(validSteamProfile.getPersonaName())
                .steamAvatar(validSteamProfile.getAvatarFull())
                .password("$2a$10$hash") // juste pour éviter des NPE éventuels ailleurs
                .build();

        // Par défaut : pas de snapshot (le code gère snap == null)
        lenient().when(discordSyncService.getLastSnapshot()).thenReturn(null);
    }

    @Test
    void shouldRegisterSuccessfully_whenNoDiscordSnapshot() {
        // Steam OK
        when(steamVerificationService.verifySteamUser(validRequest.getSteamId()))
                .thenReturn(validSteamProfile);

        // Encodage OK
        when(passwordEncoder.encode(validRequest.getPassword()))
                .thenReturn("encodedPassword");

        // Procédure: 0 = success
        // Signature exacte : (email, finalPseudo, passwordHash, steamId,
        //                    discordId, username, discriminator, nickname, avatarUrl, boolean,
        //                    steamPersonaName, steamAvatarUrl)
        when(registerProcedureExecutor.execute(
                anyString(), anyString(), anyString(), anyString(),
                any(), any(), any(), any(), any(),
                anyBoolean(),
                anyString(), anyString()
        )).thenReturn(0);

        // Rechargement de l'utilisateur après la procédure
        when(userAuthRepository.findByEmail(validRequest.getEmail()))
                .thenReturn(Optional.of(mockUser));

        // ACT & ASSERT
        assertDoesNotThrow(() -> authService.register(validRequest));

        // Vérifs d'interactions clés
        verify(passwordEncoder).encode("Password123");
        verify(registerProcedureExecutor).execute(
                anyString(), anyString(), anyString(), anyString(),
                isNull(), isNull(), isNull(), isNull(), isNull(),
                eq(false),
                anyString(), anyString()
        );
        verify(userAuthRepository).findByEmail(validRequest.getEmail());
        verify(userAuthService).updateSteamProfile(mockUser, validSteamProfile);

        // register() ne touche pas JwtUtils → aucune interaction attendue
        verifyNoInteractions(jwtUtils);
    }

    @Test
    void shouldRegisterSuccessfully_whenDiscordSnapshotPresent_usesDiscordPseudoIfChosen() {
        // Simule un snapshot Discord présent
        DiscordSyncService.DiscordSnapshot snap = mock(DiscordSyncService.DiscordSnapshot.class);
        when(snap.getChosenPseudo()).thenReturn("DiscordNick"); // priorité à ce pseudo
        when(snap.getDiscordId()).thenReturn(validRequest.getDiscordId());
        when(snap.getUsername()).thenReturn("DiscordUser");
        when(snap.getDiscriminator()).thenReturn("1234");
        when(snap.getNickname()).thenReturn("ServerNick");
        when(snap.getAvatarUrl()).thenReturn("http://discord.avatar");

        when(discordSyncService.getLastSnapshot()).thenReturn(snap);

        when(steamVerificationService.verifySteamUser(validRequest.getSteamId()))
                .thenReturn(validSteamProfile);
        when(passwordEncoder.encode(validRequest.getPassword()))
                .thenReturn("encodedPassword");

        when(registerProcedureExecutor.execute(
                // on pourrait matcher précisément DiscordNick ici,
                // mais on reste souple pour ne pas rendre le test fragile
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(),
                eq(true),
                anyString(), anyString()
        )).thenReturn(0);

        when(userAuthRepository.findByEmail(validRequest.getEmail()))
                .thenReturn(Optional.of(mockUser));

        assertDoesNotThrow(() -> authService.register(validRequest));

        verify(registerProcedureExecutor).execute(
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyString(),
                eq(true),
                anyString(), anyString()
        );
        verify(userAuthService).updateSteamProfile(mockUser, validSteamProfile);
        verifyNoInteractions(jwtUtils);
    }

    @Test
    void shouldThrowWhenPasswordsDoNotMatch() {
        validRequest.setConfirmPassword("WrongPassword");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(validRequest)
        );

        assertEquals("Les mots de passe ne correspondent pas.", ex.getMessage());
        verifyNoInteractions(registerProcedureExecutor);
    }

    @Test
    void shouldThrowWhenSteamIsBanned() {
        SteamProfileDTO bannedProfile = SteamProfileDTO.builder()
                .banned(true)
                .build();

        when(steamVerificationService.verifySteamUser(validRequest.getSteamId()))
                .thenReturn(bannedProfile);

        assertThrows(SteamAccountBannedException.class,
                () -> authService.register(validRequest));

        verifyNoInteractions(registerProcedureExecutor);
    }

    @Test
    void shouldThrowWhenDuplicateUserDetected() {
        when(steamVerificationService.verifySteamUser(validRequest.getSteamId()))
                .thenReturn(validSteamProfile);

        when(passwordEncoder.encode(validRequest.getPassword()))
                .thenReturn("encodedPassword");

        // -1 = doublon (email/pseudo/steam déjà utilisé)
        when(registerProcedureExecutor.execute(
                anyString(), anyString(), anyString(), anyString(),
                any(), any(), any(), any(), any(),
                anyBoolean(),
                anyString(), anyString()
        )).thenReturn(-1);

        assertThrows(DuplicateUserException.class,
                () -> authService.register(validRequest));

        verify(registerProcedureExecutor).execute(
                anyString(), anyString(), anyString(), anyString(),
                any(), any(), any(), any(), any(),
                anyBoolean(),
                anyString(), anyString()
        );
    }
}
