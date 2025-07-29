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
       System.out.println("[DEBUG] resetBaseUrl utilisé : " + resetBaseUrl); // 👈 Log ajouté ici

        userRepo.findByEmail(dto.getEmail()).ifPresent(user -> {
            System.out.println("[RESET] Utilisateur trouvé, génération du token...");

            String token = TokenGenerator.generateToken();

           PasswordResetToken resetToken = PasswordResetToken.builder()
                   .email(user.getEmail())
                   .token(token)
                   .expirationDate(LocalDateTime.now().plusHours(1))
                   .used(false)
                   .build();

           tokenRepo.save(resetToken);

           String resetUrl = resetBaseUrl + token;

          SimpleMailMessage message = new SimpleMailMessage();
          message.setTo(user.getEmail());
           message.setSubject("Réinitialisation de mot de passe");
           message.setText("Clique sur ce lien pour réinitialiser ton mot de passe : " + resetUrl);

           mailSender.send(message);

           System.out.println("[RESET] Mail de réinitialisation envoyé à : " + user.getEmail());
           System.out.println("[DEBUG] Email envoyé à : " + user.getEmail()); // Log ajouté
       });

       // Toujours ce message, que l'email soit valide ou pas
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
