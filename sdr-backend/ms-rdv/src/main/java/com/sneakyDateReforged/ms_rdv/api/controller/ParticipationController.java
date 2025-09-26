package com.sneakyDateReforged.ms_rdv.api.controller;

import com.sneakyDateReforged.ms_rdv.api.dto.*;
import com.sneakyDateReforged.ms_rdv.service.ParticipationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rdv/{rdvId}/participations")
@RequiredArgsConstructor
public class ParticipationController {

    private final ParticipationService participationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipantDTO request(@PathVariable Long rdvId, @Valid @RequestBody ParticipationRequest req) {
        return participationService.request(rdvId, req);
    }

    @PatchMapping("/{participationId}/status")
    public ParticipantDTO updateStatus(
            @PathVariable Long rdvId,
            @PathVariable Long participationId,
            @Valid @RequestBody UpdateParticipationStatusRequest req) {
        return participationService.updateStatus(rdvId, participationId, req);
    }

    @GetMapping
    public List<ParticipantDTO> list(@PathVariable Long rdvId) {
        return participationService.list(rdvId);
    }
}
