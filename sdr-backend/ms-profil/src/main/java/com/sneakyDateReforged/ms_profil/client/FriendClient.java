package com.sneakyDateReforged.ms_profil.client;

import com.sneakyDateReforged.ms_profil.client.dto.FriendCountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-friend") // ⚠️ doit matcher l'ID Eureka du service "amis"
public interface FriendClient {

    @GetMapping("/friends/{userId}/count")
    FriendCountResponse getFriendCounts(@PathVariable("userId") Long userId);
}
