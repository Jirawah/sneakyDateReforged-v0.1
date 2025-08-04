package com.sneakyDateReforged.ms_auth.service;

import com.sneakyDateReforged.ms_auth.dto.ResetConfirmDTO;
import com.sneakyDateReforged.ms_auth.dto.ResetRequestDTO;
import com.sneakyDateReforged.ms_auth.exception.InvalidResetTokenException;
import com.sneakyDateReforged.ms_auth.model.PasswordResetToken;
import com.sneakyDateReforged.ms_auth.model.User;
import com.sneakyDateReforged.ms_auth.repository.PasswordResetTokenRepository;
import com.sneakyDateReforged.ms_auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PasswordResetServiceTest {

    @InjectMocks
    private PasswordResetService passwordResetService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private MailService mailService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        passwordResetService = new PasswordResetService(
                userRepository,
                tokenRepository,
                mailService,
                passwordEncoder,
                "http://localhost:4200/reset-password?token="
        );
    }

    @Test
    void requestReset_shouldGenerateTokenAndSendEmail() {
        // GIVEN
        ResetRequestDTO dto = new ResetRequestDTO("test@example.com");
        User user = User.builder().email("test@example.com").pseudo("TestUser").build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(tokenRepository.save(any(PasswordResetToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        passwordResetService.requestReset(dto);

        // THEN
        verify(tokenRepository, times(1)).save(any(PasswordResetToken.class));
        verify(mailService, times(1)).sendPasswordResetEmail(eq(user), contains("http://localhost:4200/reset-password?token="));
    }

    @Test
    void confirmReset_shouldUpdatePasswordAndMarkTokenUsed() {
        // GIVEN
        String token = "test-token";
        ResetConfirmDTO dto = new ResetConfirmDTO("newPassword");

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .email("test@example.com")
                .used(false)
                .expirationDate(LocalDateTime.now().plusMinutes(30))
                .build();

        User user = User.builder().email("test@example.com").password("oldPassword").build();

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");

        // WHEN
        passwordResetService.confirmReset(token, dto);

        // THEN
        assertTrue(resetToken.isUsed());
        assertEquals("encodedPassword", user.getPassword());

        verify(userRepository, times(1)).save(user);
        verify(tokenRepository, times(1)).save(resetToken);
    }

    @Test
    void confirmReset_shouldThrowExceptionIfTokenInvalid() {
        when(tokenRepository.findByToken("invalid-token")).thenReturn(Optional.empty());

        assertThrows(InvalidResetTokenException.class, () -> {
            passwordResetService.confirmReset("invalid-token", new ResetConfirmDTO("irrelevant"));
        });
    }

    @Test
    void confirmReset_shouldThrowExceptionIfTokenExpired() {
        String token = "expired-token";

        PasswordResetToken expiredToken = PasswordResetToken.builder()
                .token(token)
                .email("test@example.com")
                .used(false)
                .expirationDate(LocalDateTime.now().minusMinutes(10))
                .build();

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(expiredToken));

        assertThrows(InvalidResetTokenException.class, () -> {
            passwordResetService.confirmReset(token, new ResetConfirmDTO("irrelevant"));
        });
    }

    @Test
    void confirmReset_shouldThrowExceptionIfTokenAlreadyUsed() {
        String token = "used-token";

        PasswordResetToken usedToken = PasswordResetToken.builder()
                .token(token)
                .email("test@example.com")
                .used(true)
                .expirationDate(LocalDateTime.now().plusMinutes(10))
                .build();

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(usedToken));

        assertThrows(InvalidResetTokenException.class, () -> {
            passwordResetService.confirmReset(token, new ResetConfirmDTO("irrelevant"));
        });
    }
}
