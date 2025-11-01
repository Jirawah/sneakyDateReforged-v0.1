package com.sneakyDateReforged.ms_auth.security;

import com.sneakyDateReforged.ms_auth.model.UserAuthModel;
import com.sneakyDateReforged.ms_auth.repository.UserAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("securityUtils")
@RequiredArgsConstructor
public class SecurityUtils {
    private final UserAuthRepository userRepo;

    public Long currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;
        String username = auth.getName(); // chez toi: souvent l'email
        if (username == null) return null;
        return userRepo.findByEmail(username)
                .or(() -> userRepo.findByPseudo(username))
                .map(UserAuthModel::getId)
                .orElse(null);
    }
}
