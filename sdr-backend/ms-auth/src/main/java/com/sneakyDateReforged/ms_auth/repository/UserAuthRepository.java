package com.sneakyDateReforged.ms_auth.repository;

import com.sneakyDateReforged.ms_auth.model.UserAuthModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAuthRepository extends JpaRepository<UserAuthModel, Long> {

    // Méthodes "existsBy" utilisées dans AuthService.register()
    boolean existsByEmail(String email);
    boolean existsByPseudo(String pseudo);
    boolean existsBySteamId(String steamId);

    // Méthodes "findBy" utilisées pour login, Discord, etc.
    Optional<UserAuthModel> findByEmail(String email);
    Optional<UserAuthModel> findByPseudo(String pseudo);
    Optional<UserAuthModel> findByDiscordId(String discordId);
    Optional<UserAuthModel> findBySteamId(String steamId);
}
