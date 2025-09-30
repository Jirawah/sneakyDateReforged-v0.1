package com.sneakyDateReforged.ms_invitation.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// À activer quand l’endpoint côté ms-rdv sera en place
@FeignClient(name = "ms-rdv", contextId = "rdvClient")
public interface RdvClient {
    @GetMapping("/rdv/{id}")
    RdvDTO get(@PathVariable("id") Long id);

    record RdvDTO(Long id, String nom, String jeu, String statut, Long organisateurId) {}
}
