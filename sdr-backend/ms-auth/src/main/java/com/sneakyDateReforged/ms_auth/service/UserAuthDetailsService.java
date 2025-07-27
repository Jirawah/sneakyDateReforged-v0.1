package com.sneakyDateReforged.ms_auth.service;

import com.sneakyDateReforged.ms_auth.model.UserAuthModel;
import com.sneakyDateReforged.ms_auth.repository.UserAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAuthDetailsService implements UserDetailsService {

    private final UserAuthRepository userAuthRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (email == null || email.isBlank()) {
            System.out.println("[AUTH] Email null ou vide reçu dans loadUserByUsername → rejeté");
            throw new UsernameNotFoundException("Email invalide : null ou vide");
        }

        UserAuthModel user = userAuthRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Aucun utilisateur avec l'email : " + email));

        System.out.println("[AUTH] Utilisateur trouvé pour : " + email);

        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles("USER") // à étendre plus tard avec une enum de rôles
                .build();
    }
}
