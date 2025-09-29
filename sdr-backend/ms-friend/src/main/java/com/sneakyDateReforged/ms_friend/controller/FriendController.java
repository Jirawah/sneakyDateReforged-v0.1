package com.sneakyDateReforged.ms_friend.controller;

import com.sneakyDateReforged.ms_friend.dto.FriendRequestDTO;
import com.sneakyDateReforged.ms_friend.entity.Friend;
import com.sneakyDateReforged.ms_friend.service.FriendService;
import com.sneakyDateReforged.ms_friend.util.AuthPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @GetMapping("/ping")
    public String ping() { return "pong"; }

    @PostMapping
    public ResponseEntity<Friend> request(@RequestBody @Valid FriendRequestDTO req, Authentication auth) {
        Long userId = AuthPrincipal.currentUserId(auth);
        Friend f = friendService.requestFriend(userId, req.targetUserId());
        return ResponseEntity.ok(f);
    }

    @PostMapping("/accept/{targetUserId}")
    public ResponseEntity<Friend> accept(@PathVariable Long targetUserId, Authentication auth) {
        Long userId = AuthPrincipal.currentUserId(auth);
        Friend f = friendService.acceptFriend(userId, targetUserId);
        return ResponseEntity.ok(f);
    }

    @DeleteMapping("/{targetUserId}")
    public ResponseEntity<Void> remove(@PathVariable Long targetUserId, Authentication auth) {
        Long userId = AuthPrincipal.currentUserId(auth);
        friendService.removeFriend(userId, targetUserId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Friend>> list(Authentication auth) {
        Long userId = AuthPrincipal.currentUserId(auth);
        return ResponseEntity.ok(friendService.listAccepted(userId));
    }
}
