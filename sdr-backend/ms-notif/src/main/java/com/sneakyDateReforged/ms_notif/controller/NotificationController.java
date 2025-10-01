package com.sneakyDateReforged.ms_notif.controller;

import com.sneakyDateReforged.ms_notif.domain.enums.NotificationStatus;
import com.sneakyDateReforged.ms_notif.dto.NotificationDTO;
import com.sneakyDateReforged.ms_notif.dto.PageResponse;
import com.sneakyDateReforged.ms_notif.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/my")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/notifications")
    public PageResponse<NotificationDTO> listMyNotifications(
            Authentication auth,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) NotificationStatus status
    ) {
        Long userId = (Long) auth.getPrincipal();
        return notificationService.getMyNotifications(userId, page, size, status);
    }

    @GetMapping("/notifications/unreadCount")
    public long unreadCount(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return notificationService.countUnread(userId);
    }

    @PatchMapping("/notifications/{id}/read")
    public NotificationDTO markRead(Authentication auth, @PathVariable String id) {
        Long userId = (Long) auth.getPrincipal();
        return notificationService.markRead(userId, id);
    }

    @PatchMapping("/notifications/read-all")
    public long markAllRead(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return notificationService.markAllRead(userId);
    }
}
