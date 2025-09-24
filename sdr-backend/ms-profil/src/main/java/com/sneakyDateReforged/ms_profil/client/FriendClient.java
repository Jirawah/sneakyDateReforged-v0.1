package com.sneakyDateReforged.ms_profil.client;

import com.sneakyDateReforged.ms_profil.client.dto.FriendCountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "ms-friend", url = "${ms-friend.url:}")
public interface FriendClient {
    // privé (JWT user)
    @GetMapping("/friends/{userId}/count")
    FriendCountResponse getFriendCounts(@PathVariable("userId") Long userId);

    // public (lecture seule)
    @GetMapping("/public/friends/{userId}/count")
    FriendCountResponse getFriendCountsPublic(@PathVariable("userId") Long userId);
}
