package com.sneakyDateReforged.ms_friend.repository;

import com.sneakyDateReforged.ms_friend.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    // Requêtes dérivées pour retrouver les amis par statut
    List<Friend> findByUserIdAndStatus(Long userId, String status);

    List<Friend> findByFriendIdAndStatus(Long friendId, String status);

    // uMin/uMax sont bien les noms des CHAMPS de l'entité
    @Query("select f from Friend f where f.uMin = :uMin and f.uMax = :uMax")
    Optional<Friend> findByMinMax(long uMin, long uMax);

    // Utile si tu veux vérifier une relation orientée + statut
    @Query("""
       select f from Friend f
       where f.status = 'ACCEPTED'
         and (f.userId = :userId or f.friendId = :userId)
       """)
    List<Friend> findAllAcceptedForUser(long userId);

    // Helper pour chercher une relation d’amitié sans se soucier de l’ordre
    default Optional<Friend> findSymmetric(Long a, Long b) {
        long uMin = Math.min(a, b);
        long uMax = Math.max(a, b);
        return findByMinMax(uMin, uMax);
    }
}
