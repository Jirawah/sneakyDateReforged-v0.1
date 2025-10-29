//package com.sneakyDateReforged.ms_auth.service;
//
//import com.sneakyDateReforged.ms_auth.dto.AuthResponseDTO;
//import com.sneakyDateReforged.ms_auth.dto.RegisterRequestDTO;
//import com.sneakyDateReforged.ms_auth.dto.SteamProfileDTO;
//import com.sneakyDateReforged.ms_auth.exception.DuplicateUserException;
//import com.sneakyDateReforged.ms_auth.exception.SteamAccountBannedException;
//import com.sneakyDateReforged.ms_auth.model.UserAuthModel;
//import com.sneakyDateReforged.ms_auth.procedure.RegisterProcedureExecutor;
//import com.sneakyDateReforged.ms_auth.repository.UserAuthRepository;
//import com.sneakyDateReforged.ms_auth.security.JwtUtils;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.*;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(SpringExtension.class)
//class AuthServiceTest {
//
//    @Mock
//    private UserAuthRepository userAuthRepository;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @Mock
//    private JwtUtils jwtUtils;
//
//    @Mock
//    private AuthenticationManager authenticationManager;
//
//    @Mock
//    private SteamVerificationService steamVerificationService;
//
//    @Mock
//    private RegisterProcedureExecutor registerProcedureExecutor;
//
//    @Mock
//    private UserAuthService userAuthService;
//
//    @InjectMocks
//    private AuthService authService;
//
//    private RegisterRequestDTO validRequest;
//    private SteamProfileDTO validSteamProfile;
//    private UserAuthModel mockUser;
//
//    @BeforeEach
//    void setUp() {
//        validRequest = RegisterRequestDTO.builder()
//                .pseudo("TestUser")
//                .email("test@example.com")
//                .steamId("123456789")
//                .password("Password123")
//                .confirmPassword("Password123")
//                .discordId("123456789987654321")
//                .build();
//
//        validSteamProfile = SteamProfileDTO.builder()
//                .personaName("SteamUser")
//                .avatarFull("http://avatar.url")
//                .banned(false)
//                .build();
//
//        mockUser = UserAuthModel.builder()
//                .email(validRequest.getEmail())
//                .steamPseudo(validSteamProfile.getPersonaName())
//                .steamAvatar(validSteamProfile.getAvatarFull())
//                .build();
//    }
//
//    @Test
//    void shouldRegisterSuccessfully() {
//        when(steamVerificationService.verifySteamUser(validRequest.getSteamId()))
//                .thenReturn(validSteamProfile);
//
//        when(passwordEncoder.encode(validRequest.getPassword()))
//                .thenReturn("encodedPassword");
//
//        when(registerProcedureExecutor.execute(
//                any(), any(), any(), any(), any()))
//                .thenReturn(0);
//
//        when(userAuthRepository.findByEmail(validRequest.getEmail()))
//                .thenReturn(Optional.of(mockUser));
//
//        when(jwtUtils.generateToken(mockUser))
//                .thenReturn("fake-jwt-token");
//
//        AuthResponseDTO response = authService.register(validRequest);
//
//        assertNotNull(response);
//        assertEquals("fake-jwt-token", response.getToken());
//        assertEquals("SteamUser", response.getSteamPseudo());
//
//        verify(userAuthService).updateSteamProfile(mockUser, validSteamProfile);
//    }
//
//    @Test
//    void shouldThrowWhenPasswordsDoNotMatch() {
//        validRequest.setConfirmPassword("WrongPassword");
//
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
//                authService.register(validRequest)
//        );
//
//        assertEquals("Les mots de passe ne correspondent pas.", exception.getMessage());
//    }
//
//    @Test
//    void shouldThrowWhenSteamIsBanned() {
//        SteamProfileDTO bannedProfile = SteamProfileDTO.builder()
//                .banned(true)
//                .build();
//
//        when(steamVerificationService.verifySteamUser(validRequest.getSteamId()))
//                .thenReturn(bannedProfile);
//
//        assertThrows(SteamAccountBannedException.class, () ->
//                authService.register(validRequest)
//        );
//    }
//
//    @Test
//    void shouldThrowWhenDuplicateUserDetected() {
//        when(steamVerificationService.verifySteamUser(validRequest.getSteamId()))
//                .thenReturn(validSteamProfile);
//
//        when(passwordEncoder.encode(validRequest.getPassword()))
//                .thenReturn("encodedPassword");
//
//        when(registerProcedureExecutor.execute(
//                any(), any(), any(), any(), any()))
//                .thenReturn(-1); // Simule doublon
//
//        assertThrows(DuplicateUserException.class, () ->
//                authService.register(validRequest)
//        );
//    }
//}
