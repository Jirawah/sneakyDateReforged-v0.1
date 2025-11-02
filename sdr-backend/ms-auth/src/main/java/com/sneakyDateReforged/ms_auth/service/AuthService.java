package com.sneakyDateReforged.ms_auth.service;

import com.sneakyDateReforged.ms_auth.dto.*;
import com.sneakyDateReforged.ms_auth.exception.DuplicateUserException;
import com.sneakyDateReforged.ms_auth.exception.SteamAccountBannedException;
import com.sneakyDateReforged.ms_auth.model.UserAuthModel;
import com.sneakyDateReforged.ms_auth.procedure.RegisterProcedureExecutor;
import com.sneakyDateReforged.ms_auth.repository.UserAuthRepository;
import com.sneakyDateReforged.ms_auth.security.JwtUtils;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.sneakyDateReforged.ms_auth.exception.SteamUnavailableException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.sql.Types;
import java.util.HashMap;
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
    private final JdbcTemplate jdbcTemplate;
    private final UserAuthService userAuthService;
    private final RegisterProcedureExecutor registerProcedureExecutor;
    private final DiscordSyncService discordSyncService;

    // Méthode d'inscription
    @Transactional
    public void register(RegisterRequestDTO request) {

        // 1. mots de passe identiques
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Les mots de passe ne correspondent pas.");
        }

        // 2. récupérer / vérifier le profil Steam
        SteamProfileDTO steamProfile;
        try {
            steamProfile = steamVerificationService.verifySteamUser(request.getSteamId());
        } catch (SteamUnavailableException e) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    e.getMessage()
            );
        }

        if (steamProfile.isBanned()) {
            throw new SteamAccountBannedException("Votre compte Steam a déjà été banni.");
        }

        // 3. snapshot Discord capturé par le bot quand l'utilisateur a rejoint le vocal
        DiscordSyncService.DiscordSnapshot snap = discordSyncService.getLastSnapshot();

        // 3.1 pseudo final : priorité au pseudo Discord choisi (nickname serveur)
        String finalPseudo = (snap != null && snap.getChosenPseudo() != null && !snap.getChosenPseudo().isBlank())
                ? snap.getChosenPseudo()
                : request.getPseudo();

        // 4. hash du mdp
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // 5. Appel de la procédure stockée enrichie
        int resultCode = registerProcedureExecutor.execute(
                request.getEmail(),
                finalPseudo,
                hashedPassword,
                request.getSteamId(),

                snap != null ? snap.getDiscordId()            : null,
                snap != null ? snap.getUsername()             : null,
                snap != null ? snap.getDiscriminator()        : null,
                snap != null ? snap.getNickname()             : null,
                snap != null ? snap.getAvatarUrl()            : null,
                snap != null, // discord_validated -> true si on a bien un snapshot

                steamProfile.getPersonaName(),
                steamProfile.getAvatarFull()
        );

        if (resultCode == -1) {
            throw new DuplicateUserException("Email, pseudo ou Steam ID déjà utilisé.");
        }

        // 6. On recharge l'utilisateur pour compléter ce qui manque (heures de jeu etc.)
        UserAuthModel user = userAuthRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé après enregistrement."));

        // Mise à jour du profil Steam (heures, etc.)
        userAuthService.updateSteamProfile(user, steamProfile);
    }

    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginRequestDTO request) {
        System.out.println("[LOGIN] Tentative de connexion avec : " + request.getEmail());

        UserAuthModel user = userAuthRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé."));

        // Debug : log du mot de passe encodé existant
        System.out.println("[DEBUG] Password hash en base : " + user.getPassword());
        System.out.println("[DEBUG] Password brut reçu     : " + request.getPassword());

        boolean match = passwordEncoder.matches(request.getPassword(), user.getPassword());
        System.out.println("[DEBUG] Résultat passwordEncoder.matches(...) : " + match);

        // Continuer quand le mot de passe est correct uniquement
        if (!match) {
            throw new IllegalArgumentException("Mot de passe incorrect.");
        }

        // Authentification Spring Security
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        String jwt = jwtUtils.generateToken(user);
        Map<String, Integer> gamesHours = new HashMap<>();
        if (user.getPubgHours() != null) gamesHours.put("PUBG", user.getPubgHours());
        if (user.getRustHours() != null) gamesHours.put("Rust", user.getRustHours());
        if (user.getAmongUsHours() != null) gamesHours.put("Among Us", user.getAmongUsHours());

        return AuthResponseDTO.builder()
                .token(jwt)
                .steamPseudo(user.getSteamPseudo())
                .steamAvatar(user.getSteamAvatar())
                .gamesHours(gamesHours)
                .build();
    }

    // Lien Discord
    @Transactional
    public void syncDiscord(DiscordSyncRequestDTO request) {
        // Mise à jour des champs Discord (username, avatar, etc.)
        userAuthService.syncDiscordProfile(request);

        // Marquage comme validé
        userAuthRepository.findByDiscordId(request.getDiscordId()).ifPresent(user -> {
            user.setDiscordValidated(true);
            userAuthRepository.save(user);
        });
    }
}

