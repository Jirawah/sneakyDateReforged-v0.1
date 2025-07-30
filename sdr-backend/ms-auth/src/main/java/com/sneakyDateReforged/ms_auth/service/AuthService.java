//package com.sneakyDateReforged.ms_auth.service;
//
//import com.sneakyDateReforged.ms_auth.dto.*;
//import com.sneakyDateReforged.ms_auth.exception.SteamAccountBannedException;
//import com.sneakyDateReforged.ms_auth.model.UserAuthModel;
//import com.sneakyDateReforged.ms_auth.repository.UserAuthRepository;
//import com.sneakyDateReforged.ms_auth.security.JwtUtils;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.util.Map;
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class AuthService {
//
//    private final UserAuthRepository userAuthRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final JwtUtils jwtUtils;
//    private final AuthenticationManager authenticationManager;
//    private final SteamVerificationService steamVerificationService;
//
//    // Enregistrement
//    @Transactional
//    public AuthResponseDTO register(RegisterRequestDTO request) {
//
//        if (!request.getPassword().equals(request.getConfirmPassword())) {
//            throw new IllegalArgumentException("Les mots de passe ne correspondent pas.");
//        }
//
//        if (userAuthRepository.existsByEmail(request.getEmail())) {
//            throw new IllegalArgumentException("Email déjà utilisé.");
//        }
//
//        if (userAuthRepository.existsByPseudo(request.getPseudo())) {
//            throw new IllegalArgumentException("Pseudo déjà utilisé.");
//        }
//
//        if (userAuthRepository.existsBySteamId(request.getSteamId())) {
//            throw new IllegalArgumentException("Steam ID déjà utilisé.");
//        }
//
//        // Vérification Steam
//        SteamProfileDTO steamProfile = steamVerificationService.verifySteamUser(request.getSteamId());
//
//        if (steamProfile.isBanned()) {
//            throw new SteamAccountBannedException("Votre compte Steam a déjà été banni.");
//        }
//
//        UserAuthModel user = UserAuthModel.builder()
//                .pseudo(request.getPseudo())
//                .email(request.getEmail())
//                .steamId(request.getSteamId())
//                .password(passwordEncoder.encode(request.getPassword()))
//                .steamPseudo(steamProfile.getPersonaName())
//                .steamAvatar(steamProfile.getAvatarFull())
//                .steamValidated(false)
//                .discordValidated(false)
//                .role("USER")
//                .build();
//
//        userAuthRepository.save(user);
//
//        String jwt = jwtUtils.generateToken(user);
//        return AuthResponseDTO.builder()
//                .token(jwt)
//                .steamPseudo(steamProfile.getPersonaName())
//                .steamAvatar(steamProfile.getAvatarFull())
//                .gamesHours(steamProfile.getGamesHours())
//                .build();
//    }
//
//    // Connexion
//    public AuthResponseDTO login(LoginRequestDTO request) {
//        System.out.println("[LOGIN] Tentative de connexion avec : " + request.getEmail());
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        request.getEmail(),
//                        request.getPassword()
//                )
//        );
//
//        UserAuthModel user = userAuthRepository.findByEmail(request.getEmail())
//                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé."));
//
//        String jwt = jwtUtils.generateToken(user);
//        return AuthResponseDTO.builder()
//                .token(jwt)
//                .steamPseudo(user.getSteamPseudo())
//                .steamAvatar(user.getSteamAvatar())
//                .gamesHours(Map.of( // juste à titre d'exemple, remplace par les vraies valeurs en BDD si dispo
//                        "PUBG", user.getPubgHours(),
//                        "Rust", user.getRustHours(),
//                        "Among Us", user.getAmongUsHours()
//                ))
//                .build();
//    }
//
//    // Synchronisation Discord
//    @Transactional
//    public void syncDiscord(DiscordSyncRequestDTO request) {
//        Optional<UserAuthModel> optionalUser = userAuthRepository.findByDiscordId(request.getDiscordId());
//
//        UserAuthModel user;
//        if (optionalUser.isPresent()) {
//            user = optionalUser.get();
//        } else {
//            // Si l’utilisateur n’est pas encore lié par discordId, on tente un lien par pseudo ou email
//            user = userAuthRepository.findByPseudo(request.getDiscordUsername())
//                    .orElseThrow(() -> new IllegalArgumentException("Aucun utilisateur correspondant pour ce Discord."));
//        }
//
//        user.setDiscordId(request.getDiscordId());
//        user.setDiscordUsername(request.getDiscordUsername());
//        user.setDiscordDiscriminator(request.getDiscordDiscriminator());
//        user.setDiscordNickname(request.getDiscordNickname());
//        user.setDiscordAvatarUrl(request.getDiscordAvatarUrl());
//        user.setDiscordValidated(true);
//
//        userAuthRepository.save(user);
//    }
//}
package com.sneakyDateReforged.ms_auth.service;

import com.sneakyDateReforged.ms_auth.dto.*;
import com.sneakyDateReforged.ms_auth.exception.DuplicateUserException;
import com.sneakyDateReforged.ms_auth.exception.SteamAccountBannedException;
import com.sneakyDateReforged.ms_auth.model.UserAuthModel;
import com.sneakyDateReforged.ms_auth.repository.UserAuthRepository;
import com.sneakyDateReforged.ms_auth.security.JwtUtils;
import jakarta.transaction.Transactional;
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

    // 🔐 Méthode d'inscription
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Les mots de passe ne correspondent pas.");
        }

        // 🕵️‍♂️ Vérification du compte Steam
        SteamProfileDTO steamProfile = steamVerificationService.verifySteamUser(request.getSteamId());
        if (steamProfile.isBanned()) {
            throw new SteamAccountBannedException("Votre compte Steam a déjà été banni.");
        }

        // 🔁 Insertion via procédure stockée
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        int resultCode = registerUserWithProcedure(
                request.getEmail(),
                request.getPseudo(),
                hashedPassword,
                request.getSteamId(),
                request.getDiscordId()
        );

        if (resultCode == -1) {
            throw new DuplicateUserException("Email, pseudo ou Steam ID déjà utilisé.");
        }

        // 🎯 Mise à jour Steam (avatar + pseudo)
        UserAuthModel user = userAuthRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé après enregistrement."));

        user.setSteamPseudo(steamProfile.getPersonaName());
        user.setSteamAvatar(steamProfile.getAvatarFull());
        userAuthRepository.save(user);

        String jwt = jwtUtils.generateToken(user);

        return AuthResponseDTO.builder()
                .token(jwt)
                .steamPseudo(user.getSteamPseudo())
                .steamAvatar(user.getSteamAvatar())
                .gamesHours(steamProfile.getGamesHours())
                .build();
    }

    // Appel à la procédure stockée qui vérifie si le mail et l'id Steam n'existe pas déjà en BDD
    private int registerUserWithProcedure(String email, String pseudo, String passwordHash, String steamId, String discordId) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("sp_register_user")
                .declareParameters(
                        new SqlParameter("p_email", Types.VARCHAR),
                        new SqlParameter("p_pseudo", Types.VARCHAR),
                        new SqlParameter("p_password", Types.VARCHAR),
                        new SqlParameter("p_steam_id", Types.VARCHAR),
                        new SqlParameter("p_discord_id", Types.VARCHAR),
                        new SqlOutParameter("p_result_code", Types.INTEGER)
                );

        Map<String, Object> inParams = new HashMap<>();
        inParams.put("p_email", email);
        inParams.put("p_pseudo", pseudo);
        inParams.put("p_password", passwordHash);
        inParams.put("p_steam_id", steamId);
        inParams.put("p_discord_id", discordId);

        try {
            Map<String, Object> result = jdbcCall.execute(inParams);
            return (Integer) result.get("p_result_code");
        } catch (DataAccessException ex) {
            throw new RuntimeException("Erreur lors de l’enregistrement via la procédure stockée", ex);
        }
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
                .gamesHours(Map.of(
                        "PUBG", user.getPubgHours(),
                        "Rust", user.getRustHours(),
                        "Among Us", user.getAmongUsHours()
                ))
                .build();
    }

    // Lien Discord
    @Transactional
    public void syncDiscord(DiscordSyncRequestDTO request) {
        Optional<UserAuthModel> optionalUser = userAuthRepository.findByDiscordId(request.getDiscordId());

        UserAuthModel user = optionalUser.orElseGet(() ->
                userAuthRepository.findByPseudo(request.getDiscordUsername())
                        .orElseThrow(() -> new IllegalArgumentException("Aucun utilisateur correspondant pour ce Discord."))
        );

        user.setDiscordId(request.getDiscordId());
        user.setDiscordUsername(request.getDiscordUsername());
        user.setDiscordDiscriminator(request.getDiscordDiscriminator());
        user.setDiscordNickname(request.getDiscordNickname());
        user.setDiscordAvatarUrl(request.getDiscordAvatarUrl());
        user.setDiscordValidated(true);

        userAuthRepository.save(user);
    }
}

