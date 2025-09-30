package com.sneakyDateReforged.ms_profil.client;

import com.sneakyDateReforged.ms_profil.client.dto.RdvNextResponse;
import com.sneakyDateReforged.ms_profil.client.dto.RdvStatsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "ms-rdv")
public interface RdvClient {
    // privés
    @GetMapping("/rdv/stats/{userId}")
    RdvStatsResponse getStats(@PathVariable("userId") Long userId);

    @GetMapping("/rdv/next/{userId}")
    RdvNextResponse getNextDate(@PathVariable("userId") Long userId);

    // publics
    @GetMapping("/public/rdv/stats/{userId}")
    RdvStatsResponse getStatsPublic(@PathVariable("userId") Long userId);

    @GetMapping("/public/rdv/next/{userId}")
    RdvNextResponse getNextDatePublic(@PathVariable("userId") Long userId);
}
