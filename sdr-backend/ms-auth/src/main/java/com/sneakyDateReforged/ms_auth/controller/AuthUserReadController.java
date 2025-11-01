//package com.sneakyDateReforged.ms_auth.controller;
//
//import com.sneakyDateReforged.ms_auth.dto.ProfileBootstrapResponse;
//import com.sneakyDateReforged.ms_auth.model.UserAuthModel;           // <-- bonne entité
//import com.sneakyDateReforged.ms_auth.repository.UserAuthRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.server.ResponseStatusException;
//
//@RestController
//@RequestMapping("/auth/users")
//@RequiredArgsConstructor
//public class AuthUserReadController {
//
//    private final UserAuthRepository userRepo; // Assure-toi qu'il est typé <UserAuthModel, Long>
//
//    @GetMapping("/{userId}/profile-bootstrap")
//    public ProfileBootstrapResponse getProfileBootstrap(@PathVariable Long userId) {
//        UserAuthModel u = userRepo.findById(userId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + userId));
//        return toBootstrap(u);
//    }
//
//    private ProfileBootstrapResponse toBootstrap(UserAuthModel u) {
//        String appPseudo = firstNonBlank(u.getPseudo(), u.getDiscordUsername());
//        return ProfileBootstrapResponse.builder()
//                .userId(u.getId())
//                .email(nullSafe(u.getEmail()))
//                .pseudo(nullSafe(appPseudo))
//
//                .discordId(nullSafe(u.getDiscordId()))
//                .discordUsername(nullSafe(u.getDiscordUsername()))
//                .discordAvatarUrl(nullSafe(u.getDiscordAvatarUrl()))
//
//                .steamId(nullSafe(u.getSteamId()))
//                .steamPseudo(nullSafe(u.getSteamPseudo()))
//                .steamAvatar(nullSafe(u.getSteamAvatar()))
//
//                // mapping sur tes noms de champs exacts
//                .hoursPubg(u.getPubgHours())
//                .hoursRust(u.getRustHours())
//                .hoursAmongUs(u.getAmongUsHours())
//                .build();
//    }
//
//    private static String firstNonBlank(String a, String b) {
//        if (a != null && !a.isBlank()) return a;
//        if (b != null && !b.isBlank()) return b;
//        return null;
//    }
//    private static String nullSafe(String s) {
//        return (s == null || s.isBlank()) ? null : s.trim();
//    }
//}
//package com.sneakyDateReforged.ms_auth.controller;
//
//import com.sneakyDateReforged.ms_auth.dto.ProfileBootstrapResponse;
//import com.sneakyDateReforged.ms_auth.model.UserAuthModel;
//import com.sneakyDateReforged.ms_auth.repository.UserAuthRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.server.ResponseStatusException;
//
//import org.springframework.security.access.prepost.PreAuthorize;
//
//@RestController
//@RequestMapping("/auth/users")
//@RequiredArgsConstructor
//public class AuthUserReadController {
//
//    private final UserAuthRepository userRepo;
//
//    // ⬇️ ajoute la garde : seulement le propriétaire (ou ADMIN)
//    @PreAuthorize("#userId == @securityUtils.currentUserId() or hasRole('ADMIN')")
//    @GetMapping("/{userId}/profile-bootstrap")
//    public ProfileBootstrapResponse getProfileBootstrap(@PathVariable Long userId) {
//        UserAuthModel u = userRepo.findById(userId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + userId));
//        return toBootstrap(u);
//    }
//
//    private ProfileBootstrapResponse toBootstrap(UserAuthModel u) {
//        String appPseudo = firstNonBlank(u.getPseudo(), u.getDiscordUsername());
//        return ProfileBootstrapResponse.builder()
//                .userId(u.getId())
//                .email(nullSafe(u.getEmail()))
//                .pseudo(nullSafe(appPseudo))
//                .discordId(nullSafe(u.getDiscordId()))
//                .discordUsername(nullSafe(u.getDiscordUsername()))
//                .discordAvatarUrl(nullSafe(u.getDiscordAvatarUrl()))
//                .steamId(nullSafe(u.getSteamId()))
//                .steamPseudo(nullSafe(u.getSteamPseudo()))
//                .steamAvatar(nullSafe(u.getSteamAvatar()))
//                .hoursPubg(u.getPubgHours())
//                .hoursRust(u.getRustHours())
//                .hoursAmongUs(u.getAmongUsHours())
//                .build();
//    }
//
//    private static String firstNonBlank(String a, String b) {
//        if (a != null && !a.isBlank()) return a;
//        if (b != null && !b.isBlank()) return b;
//        return null;
//    }
//    private static String nullSafe(String s) {
//        return (s == null || s.isBlank()) ? null : s.trim();
//    }
//}
package com.sneakyDateReforged.ms_auth.controller;

import com.sneakyDateReforged.ms_auth.dto.ProfileBootstrapResponse;
import com.sneakyDateReforged.ms_auth.dto.ProfileImageResponse;     // ⬅️ NEW
import com.sneakyDateReforged.ms_auth.model.UserAuthModel;
import com.sneakyDateReforged.ms_auth.repository.UserAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.security.access.prepost.PreAuthorize;        // ⬅️ garde bien cet import
import com.sneakyDateReforged.ms_auth.dto.DiscordUsernameResponse;

@RestController
@RequestMapping("/auth/users")
@RequiredArgsConstructor
public class AuthUserReadController {

    private final UserAuthRepository userRepo;

    // ⬇️ garde la garde : seulement le propriétaire (ou ADMIN)
    @PreAuthorize("#userId == @securityUtils.currentUserId() or hasRole('ADMIN')")
    @GetMapping("/{userId}/profile-bootstrap")
    public ProfileBootstrapResponse getProfileBootstrap(@PathVariable Long userId) {
        UserAuthModel u = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + userId));
        return toBootstrap(u);
    }

    /** ⬇️ NOUVEAU: retourne l'URL de l’avatar Discord stockée en BDD ms-auth */
    @PreAuthorize("#userId == @securityUtils.currentUserId() or hasRole('ADMIN')")
    @GetMapping("/{userId}/discord-avatar")
    public ProfileImageResponse getDiscordAvatar(@PathVariable Long userId) {
        UserAuthModel u = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + userId));
        String url = (u.getDiscordAvatarUrl() != null && !u.getDiscordAvatarUrl().isBlank())
                ? u.getDiscordAvatarUrl() : null;
        return new ProfileImageResponse(url);
    }

    @PreAuthorize("#userId == @securityUtils.currentUserId() or hasRole('ADMIN')")
    @GetMapping("/{userId}/discord-username")
    public DiscordUsernameResponse getDiscordUsername(@PathVariable Long userId) {
        var u = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + userId));
        return new DiscordUsernameResponse(u.getDiscordUsername());
    }

    private ProfileBootstrapResponse toBootstrap(UserAuthModel u) {
        String appPseudo = firstNonBlank(u.getPseudo(), u.getDiscordUsername());
        return ProfileBootstrapResponse.builder()
                .userId(u.getId())
                .email(nullSafe(u.getEmail()))
                .pseudo(nullSafe(appPseudo))
                .discordId(nullSafe(u.getDiscordId()))
                .discordUsername(nullSafe(u.getDiscordUsername()))
                .discordAvatarUrl(nullSafe(u.getDiscordAvatarUrl()))
                .steamId(nullSafe(u.getSteamId()))
                .steamPseudo(nullSafe(u.getSteamPseudo()))
                .steamAvatar(nullSafe(u.getSteamAvatar()))
                .hoursPubg(u.getPubgHours())
                .hoursRust(u.getRustHours())
                .hoursAmongUs(u.getAmongUsHours())
                .build();
    }

    private static String firstNonBlank(String a, String b) {
        if (a != null && !a.isBlank()) return a;
        if (b != null && !b.isBlank()) return b;
        return null;
    }
    private static String nullSafe(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}

