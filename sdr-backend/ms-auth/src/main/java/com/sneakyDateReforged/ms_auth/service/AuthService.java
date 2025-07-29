package com.sneakyDateReforged.ms_auth.service;

import com.sneakyDateReforged.ms_auth.dto.*;
import com.sneakyDateReforged.ms_auth.exception.SteamAccountBannedException;
import com.sneakyDateReforged.ms_auth.model.UserAuthModel;
import com.sneakyDateReforged.ms_auth.repository.UserAuthRepository;
import com.sneakyDateReforged.ms_auth.security.JwtUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserAuthRepository userAuthRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final SteamVerificationService steamVerificationService;

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

        // Vérification Steam
        SteamProfileDTO steamProfile = steamVerificationService.verifySteamUser(request.getSteamId());

        if (steamProfile.isBanned()) {
            throw new SteamAccountBannedException("Votre compte Steam a déjà été banni.");
        }

        UserAuthModel user = UserAuthModel.builder()
                .pseudo(request.getPseudo())
                .email(request.getEmail())
                .steamId(request.getSteamId())
                .password(passwordEncoder.encode(request.getPassword()))
                .steamPseudo(steamProfile.getPersonaName())
                .steamAvatar(steamProfile.getAvatarFull())
                .steamValidated(false)
                .discordValidated(false)
                .role("USER")
                .build();

        userAuthRepository.save(user);

        String jwt = jwtUtils.generateToken(user);
        return AuthResponseDTO.builder()
                .token(jwt)
                .steamPseudo(steamProfile.getPersonaName())
                .steamAvatar(steamProfile.getAvatarFull())
                .gamesHours(steamProfile.getGamesHours())
                .build();
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
        return AuthResponseDTO.builder()
                .token(jwt)
                .steamPseudo(user.getSteamPseudo())
                .steamAvatar(user.getSteamAvatar())
                .gamesHours(Map.of( // juste à titre d'exemple, remplace par les vraies valeurs en BDD si dispo
                        "PUBG", user.getPubgHours(),
                        "Rust", user.getRustHours(),
                        "Among Us", user.getAmongUsHours()
                ))
                .build();
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
