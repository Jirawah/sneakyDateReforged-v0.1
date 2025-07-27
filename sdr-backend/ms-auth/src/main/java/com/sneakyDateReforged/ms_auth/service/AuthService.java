package com.sneakyDateReforged.ms_auth.service;

import com.sneakyDateReforged.ms_auth.dto.*;
import com.sneakyDateReforged.ms_auth.model.UserAuthModel;
import com.sneakyDateReforged.ms_auth.repository.UserAuthRepository;
import com.sneakyDateReforged.ms_auth.security.JwtUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserAuthRepository userAuthRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    // Enregistrement
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Les mots de passe ne correspondent pas.");
        }

        if (userAuthRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email déjà utilisé.");
        }

        if (userAuthRepository.existsByPseudo(request.getPseudo())) {
            throw new IllegalArgumentException("Pseudo déjà utilisé.");
        }

        if (userAuthRepository.existsBySteamId(request.getSteamId())) {
            throw new IllegalArgumentException("Steam ID déjà utilisé.");
        }

        // À ce stade, on pourrait appeler SteamCheckerService ici

        UserAuthModel user = UserAuthModel.builder()
                .pseudo(request.getPseudo())
                .email(request.getEmail())
                .steamId(request.getSteamId())
                .password(passwordEncoder.encode(request.getPassword()))
                .steamValidated(false)         // sera mis à jour plus tard
                .discordValidated(false)       // idem
                .role("USER")
                .build();

        userAuthRepository.save(user);

        String jwt = jwtUtils.generateToken(user);

        return new AuthResponseDTO(jwt);
    }

    // Connexion
    public AuthResponseDTO login(LoginRequestDTO request) {
        System.out.println("[LOGIN] Tentative de connexion avec : " + request.getEmail());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserAuthModel user = userAuthRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé."));

        String jwt = jwtUtils.generateToken(user);
        return new AuthResponseDTO(jwt);
    }

    // Synchronisation Discord
    @Transactional
    public void syncDiscord(DiscordSyncRequestDTO request) {
        Optional<UserAuthModel> optionalUser = userAuthRepository.findByDiscordId(request.getDiscordId());

        UserAuthModel user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            // Si l’utilisateur n’est pas encore lié par discordId, on tente un lien par pseudo ou email
            user = userAuthRepository.findByPseudo(request.getDiscordUsername())
                    .orElseThrow(() -> new IllegalArgumentException("Aucun utilisateur correspondant pour ce Discord."));
        }

        user.setDiscordId(request.getDiscordId());
        user.setDiscordUsername(request.getDiscordUsername());
        user.setDiscordDiscriminator(request.getDiscordDiscriminator());
        user.setDiscordNickname(request.getDiscordNickname());
        user.setDiscordAvatarUrl(request.getDiscordAvatarUrl());
        user.setDiscordValidated(true);

        userAuthRepository.save(user);
    }
}
