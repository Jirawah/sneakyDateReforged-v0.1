package com.sneakyDateReforged.ms_friend.service;

import com.sneakyDateReforged.ms_friend.entity.Friend;
import com.sneakyDateReforged.ms_friend.repository.FriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepo;

    /**
     * Recherche une relation d'amitié (peu importe l'ordre des IDs)
     */
    public Optional<Friend> findPair(Long u1, Long u2) {
        return friendRepo.findSymmetric(u1, u2);
    }

    /**
     * Crée une relation si elle n’existe pas encore
     */
    public Friend createIfNotExists(Long u1, Long u2, String status) {
        return findPair(u1, u2)
                .orElseGet(() -> friendRepo.save(
                        Friend.builder()
                                .userId(u1)
                                .friendId(u2)
                                .status(status)
                                .build()
                ));
    }

    @Transactional
    public Friend requestFriend(Long requesterId, Long targetUserId) {
        if (requesterId.equals(targetUserId)) {
            throw new IllegalArgumentException("Impossible de s'ajouter soi-même.");
        }

        var existing = findPair(requesterId, targetUserId);
        if (existing.isPresent()) {
            var f = existing.get();
            if ("ACCEPTED".equals(f.getStatus())) return f;
            if ("BLOCKED".equals(f.getStatus()))
                throw new IllegalStateException("Relation bloquée.");
            f.setStatus("PENDING"); // relance
            return friendRepo.save(f);
        }

        // Utilise createIfNotExists pour centraliser la logique
        return createIfNotExists(requesterId, targetUserId, "PENDING");
    }

    @Transactional
    public Friend acceptFriend(Long userId, Long targetUserId) {
        var f = findPair(userId, targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("Demande introuvable."));
        f.setStatus("ACCEPTED");
        return friendRepo.save(f);
    }

    @Transactional
    public void removeFriend(Long userId, Long targetUserId) {
        findPair(userId, targetUserId)
                .ifPresent(friendRepo::delete);
    }

    @Transactional(readOnly = true)
    public List<Friend> listAccepted(Long userId) {
        return friendRepo.findAllAcceptedForUser(userId);
    }
}
