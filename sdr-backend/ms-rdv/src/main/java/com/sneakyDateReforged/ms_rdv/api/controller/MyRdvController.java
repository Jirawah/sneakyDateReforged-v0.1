// src/main/java/.../api/controller/MyRdvController.java
package com.sneakyDateReforged.ms_rdv.api.controller;

import com.sneakyDateReforged.ms_rdv.api.dto.RdvParticipationDTO;
import com.sneakyDateReforged.ms_rdv.api.dto.RdvSummaryDTO;
import com.sneakyDateReforged.ms_rdv.domain.enums.ParticipationStatus;
import com.sneakyDateReforged.ms_rdv.service.RdvService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/rdv/my")
@RequiredArgsConstructor
public class MyRdvController {

    private final RdvService rdvService;

    private Long resolveUserId(Long currentUserId, Long userIdParam) {
        Long uid = currentUserId != null ? currentUserId : userIdParam;
        if (uid == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId manquant (header X-User-Id ou param ?userId=)");
        }
        return uid;
    }

    // 1) Mes RDV "j'organise"
    @GetMapping("/organized")
    public List<RdvSummaryDTO> myOrganized(
            @RequestHeader(value = "X-User-Id", required = false) Long currentUserId,
            @RequestParam(value = "userId", required = false) Long userIdParam
    ) {
        Long uid = resolveUserId(currentUserId, userIdParam);
        return rdvService.listByOrganisateur(uid);
    }

    // 2) Mes participations (tous statuts ou filtré)
    @GetMapping("/participations")
    public List<RdvParticipationDTO> myParticipations(
            @RequestHeader(value = "X-User-Id", required = false) Long currentUserId,
            @RequestParam(value = "userId", required = false) Long userIdParam,
            @RequestParam(value = "status", required = false) ParticipationStatus status
    ) {
        Long uid = resolveUserId(currentUserId, userIdParam);
        return rdvService.listParticipations(uid, status);
    }
}
