package com.sneakyDateReforged.ms_auth.service;

import com.sneakyDateReforged.ms_auth.dto.ResetPasswordRequestDTO;
import com.sneakyDateReforged.ms_auth.dto.ResetRequestDTO;
import com.sneakyDateReforged.ms_auth.exception.ExpiredResetTokenException;
import com.sneakyDateReforged.ms_auth.exception.InvalidResetTokenException;
import com.sneakyDateReforged.ms_auth.model.PasswordResetToken;
import com.sneakyDateReforged.ms_auth.model.UserAuthModel;
import com.sneakyDateReforged.ms_auth.repository.PasswordResetTokenRepository;
import com.sneakyDateReforged.ms_auth.repository.UserAuthRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PasswordResetServiceTest {

    @Mock
    private UserAuthRepository userRepo;

    @Mock
    private PasswordResetTokenRepository tokenRepo;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordResetService passwordResetService;

    @Captor
    private ArgumentCaptor<MimeMessage> mimeMessageCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        passwordResetService = new PasswordResetService(
                userRepo,
                tokenRepo,
                mailSender,
                passwordEncoder
        );

        // Injection manuelle du champ annoté @Value
        org.springframework.test.util.ReflectionTestUtils.setField(
                passwordResetService, "resetBaseUrl", "http://localhost:4200/reset-password?token="
        );
    }

    @Test
    void requestReset_shouldGenerateTokenAndSendMail() throws MessagingException {
        // Arrange
        ResetRequestDTO dto = new ResetRequestDTO("test@email.com");
        UserAuthModel user = UserAuthModel.builder()
                .email("test@email.com")
                .pseudo("TestUser")
                .build();

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(userRepo.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        passwordResetService.requestReset(dto);

        // Assert
        verify(tokenRepo, times(1)).save(any(PasswordResetToken.class));
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void resetPassword_shouldUpdatePasswordAndMarkTokenAsUsed() {
        // Arrange
        String token = "valid-token";
        ResetPasswordRequestDTO dto = new ResetPasswordRequestDTO(token, "newPassword");

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .email("test@email.com")
                .expirationDate(LocalDateTime.now().plusHours(1))
                .used(false)
                .build();

        UserAuthModel user = UserAuthModel.builder()
                .email("test@email.com")
                .password("oldPassword")
                .build();

        when(tokenRepo.findByToken(token)).thenReturn(Optional.of(resetToken));
        when(userRepo.findByEmail("test@email.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword")).thenReturn("hashedPassword");

        // Act
        passwordResetService.resetPassword(dto);

        // Assert
        assertTrue(resetToken.isUsed());
        assertEquals("hashedPassword", user.getPassword());
        verify(userRepo, times(1)).save(user);
        verify(tokenRepo, times(1)).save(resetToken);
    }

    @Test
    void resetPassword_shouldThrowIfTokenNotFound() {
        when(tokenRepo.findByToken("invalid")).thenReturn(Optional.empty());

        ResetPasswordRequestDTO dto = new ResetPasswordRequestDTO("invalid", "newPass");

        assertThrows(InvalidResetTokenException.class, () -> passwordResetService.resetPassword(dto));
    }

    @Test
    void resetPassword_shouldThrowIfTokenExpired() {
        PasswordResetToken token = PasswordResetToken.builder()
                .token("expired")
                .email("test@email.com")
                .expirationDate(LocalDateTime.now().minusHours(1))
                .used(false)
                .build();

        when(tokenRepo.findByToken("expired")).thenReturn(Optional.of(token));

        ResetPasswordRequestDTO dto = new ResetPasswordRequestDTO("expired", "newPass");

        assertThrows(ExpiredResetTokenException.class, () -> passwordResetService.resetPassword(dto));
    }

    @Test
    void resetPassword_shouldThrowIfTokenAlreadyUsed() {
        PasswordResetToken token = PasswordResetToken.builder()
                .token("used")
                .email("test@email.com")
                .expirationDate(LocalDateTime.now().plusHours(1))
                .used(true)
                .build();

        when(tokenRepo.findByToken("used")).thenReturn(Optional.of(token));

        ResetPasswordRequestDTO dto = new ResetPasswordRequestDTO("used", "newPass");

        assertThrows(ExpiredResetTokenException.class, () -> passwordResetService.resetPassword(dto));
    }
}
