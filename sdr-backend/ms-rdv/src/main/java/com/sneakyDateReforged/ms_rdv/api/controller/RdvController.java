package com.sneakyDateReforged.ms_rdv.api.controller;

import com.sneakyDateReforged.ms_rdv.api.dto.CreateRdvRequest;
import com.sneakyDateReforged.ms_rdv.api.dto.RdvDTO;
import com.sneakyDateReforged.ms_rdv.api.dto.RdvSummaryDTO;
import com.sneakyDateReforged.ms_rdv.service.RdvService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/rdv")
@RequiredArgsConstructor
public class RdvController {

    private final RdvService rdvService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RdvDTO create(@Valid @RequestBody CreateRdvRequest request) {
        return rdvService.create(request);
    }

    @GetMapping("/{id}")
    public RdvDTO get(@PathVariable Long id) {
        return rdvService.getById(id);
    }

    @GetMapping
    public List<RdvSummaryDTO> byDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String jeu) {
        return rdvService.listByDate(date, jeu);
    }
}
