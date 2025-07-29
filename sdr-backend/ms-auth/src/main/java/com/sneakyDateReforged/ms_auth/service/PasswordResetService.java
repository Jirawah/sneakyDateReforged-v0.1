package com.sneakyDateReforged.ms_auth.service;

import com.sneakyDateReforged.ms_auth.dto.ResetPasswordRequestDTO;
import com.sneakyDateReforged.ms_auth.dto.ResetRequestDTO;
import com.sneakyDateReforged.ms_auth.model.PasswordResetToken;
import com.sneakyDateReforged.ms_auth.model.UserAuthModel;
import com.sneakyDateReforged.ms_auth.repository.PasswordResetTokenRepository;
import com.sneakyDateReforged.ms_auth.repository.UserAuthRepository;
import com.sneakyDateReforged.ms_auth.util.TokenGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.sneakyDateReforged.ms_auth.exception.InvalidResetTokenException;
import com.sneakyDateReforged.ms_auth.exception.ExpiredResetTokenException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserAuthRepository userRepo;
    private final PasswordResetTokenRepository tokenRepo;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.frontend.reset-password-url}")
    private String resetBaseUrl; // ex: http://localhost:4200/reset-password?token=

    public void requestReset(ResetRequestDTO dto) {
        System.out.println("[RESET] Demande reçue pour : " + dto.getEmail());

        userRepo.findByEmail(dto.getEmail()).ifPresent(user -> {
            String token = TokenGenerator.generateToken();

            PasswordResetToken resetToken = PasswordResetToken.builder()
                    .email(user.getEmail())
                    .token(token)
                    .expirationDate(LocalDateTime.now().plusHours(1))
                    .used(false)
                    .build();

            tokenRepo.save(resetToken);

            String resetUrl = resetBaseUrl + token;

            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                helper.setTo(user.getEmail());
                helper.setSubject("🔐 Réinitialisation de ton mot de passe");

                String htmlContent = "<div style='font-family:sans-serif; max-width:600px; margin:auto;'>" +
                        "<img src='https://raw.githubusercontent.com/Jirawah/sneakyDateReforged-Logo/refs/heads/main/sneakyDateReforged-Logo.svg' alt='SneakyDate Logo' style='width:150px; display:block; margin:0 auto 20px;' />" +
                        "<h2 style='color:#ff3366;'>Réinitialisation de ton mot de passe</h2>" +
                        "<p>Salut <strong>" + user.getPseudo() + "</strong>,</p>" +
                        "<p>Tu as demandé à réinitialiser ton mot de passe. Clique sur le bouton ci-dessous 👇</p>" +
                        "<div style='margin:30px 0; text-align:center;'>" +
                        "<a href='" + resetUrl + "' style='background-color:#ff3366; color:white; padding:12px 24px; text-decoration:none; border-radius:5px;'>Réinitialiser mon mot de passe</a>" +
                        "</div>" +
                        "<p style='font-size:0.9em; color:#888;'>Si tu n'es pas à l'origine de cette demande, ignore simplement ce message.</p>" +
                        "<hr style='margin-top:40px;'><p style='font-size:0.8em; color:#aaa;'>© SneakyDateReforged</p></div>";

                helper.setText(htmlContent, true); // true = HTML

                mailSender.send(message);
                System.out.println("[RESET] Email HTML envoyé à : " + user.getEmail());
            } catch (MessagingException e) {
                System.err.println("[RESET] Erreur envoi email HTML : " + e.getMessage());
            }
        });

        System.out.println("[RESET] Fin de traitement silencieux (email envoyé si adresse existe)");
    }

    @Transactional
    public void resetPassword(ResetPasswordRequestDTO dto) {
        PasswordResetToken token = tokenRepo.findByToken(dto.getToken())
                .orElseThrow(() -> new InvalidResetTokenException("Token invalide."));

        if (token.isUsed() || token.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new ExpiredResetTokenException("Token expiré ou déjà utilisé.");
        }

        UserAuthModel user = userRepo.findByEmail(token.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable."));

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepo.save(user);

        token.setUsed(true);
        tokenRepo.save(token);
    }
}
