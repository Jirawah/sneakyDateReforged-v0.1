package com.sneakyDateReforged.ms_notif.controller;

import com.sneakyDateReforged.ms_notif.domain.NotificationDocument;
import com.sneakyDateReforged.ms_notif.dto.NotificationEventDTO;
import com.sneakyDateReforged.ms_notif.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping("/events")
    @ResponseStatus(HttpStatus.CREATED)
    public List<NotificationDocument> receiveEvent(@Valid @RequestBody NotificationEventDTO dto) {
        return eventService.handleEvent(dto);
    }
}
