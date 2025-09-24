package com.sneakyDateReforged.ms_profil.client;

import com.sneakyDateReforged.ms_profil.client.dto.RdvNextResponse;
import com.sneakyDateReforged.ms_profil.client.dto.RdvStatsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-rdv") // ⚠️ doit matcher l'ID Eureka de ton service RDV
public interface RdvClient {

    @GetMapping("/rdv/stats/{userId}")
    RdvStatsResponse getStats(@PathVariable("userId") Long userId);

    @GetMapping("/rdv/next/{userId}")
    RdvNextResponse getNextDate(@PathVariable("userId") Long userId);
}
