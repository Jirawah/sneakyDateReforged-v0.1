package com.sneakyDateReforged.ms_notif.service;

import com.sneakyDateReforged.ms_notif.domain.NotificationDocument;
import com.sneakyDateReforged.ms_notif.domain.enums.NotificationChannel;
import com.sneakyDateReforged.ms_notif.domain.enums.NotificationStatus;
import com.sneakyDateReforged.ms_notif.dto.NotificationDTO;
import com.sneakyDateReforged.ms_notif.dto.PageResponse; // <-- utilise la DTO, pas une classe interne
import com.sneakyDateReforged.ms_notif.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MongoTemplate mongoTemplate;

    @Value("${notifications.ttl.days:180}")
    private int ttlDays;

    public NotificationDocument createInAppNotification(NotificationDocument doc) {
        Instant now = Instant.now();
        if (doc.getCreatedAt() == null) doc.setCreatedAt(now);
        if (doc.getExpiresAt() == null) doc.setExpiresAt(now.plus(Duration.ofDays(ttlDays)));
        doc.setChannel(NotificationChannel.IN_APP);
        if (doc.getStatus() == null) doc.setStatus(NotificationStatus.PENDING);

        // IN_APP : on considère envoyé immédiatement
        doc.setStatus(NotificationStatus.SENT);
        doc.setSentAt(now);

        try {
            return notificationRepository.save(doc);
        } catch (DuplicateKeyException e) {
            // idempotence (uk_event_user)
            return doc; // ignore le doublon
        }
    }

    // <-- Retourne bien la DTO PageResponse
    public PageResponse<NotificationDTO> getMyNotifications(Long userId, Integer page, Integer size, NotificationStatus status) {
        PageRequest pr = PageRequest.of(page == null ? 0 : page, size == null ? 20 : size);
        Page<NotificationDocument> p = (status == null)
                ? notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pr)
                : notificationRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status, pr);

        List<NotificationDTO> items = p.getContent().stream().map(this::toDto).collect(Collectors.toList());
        return new PageResponse<>(
                items,
                p.getNumber(),
                p.getSize(),
                p.getTotalElements(),
                p.getTotalPages()
        );
    }

    public long countUnread(Long userId) {
        return notificationRepository.countByUserIdAndStatus(userId, NotificationStatus.PENDING)
                + notificationRepository.countByUserIdAndStatus(userId, NotificationStatus.SENT);
    }

    public NotificationDTO markRead(Long userId, String id) {
        var doc = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + id));

        // 🔒 Vérifie que la notif appartient au user connecté
        if (!Objects.equals(doc.getUserId(), userId)) {
            throw new AccessDeniedException("You are not allowed to modify this notification.");
        }

        if (doc.getStatus() != NotificationStatus.READ) {
            doc.setStatus(NotificationStatus.READ);
            doc.setReadAt(Instant.now());
            notificationRepository.save(doc);
        }
        return toDto(doc);
    }

    public long markAllRead(Long userId) {
        var now = Instant.now();

        Query q = new Query(Criteria.where("userId").is(userId)
                .and("status").in(NotificationStatus.PENDING, NotificationStatus.SENT));

        Update u = new Update()
                .set("status", NotificationStatus.READ)
                .set("readAt", now);

        var res = mongoTemplate.updateMulti(q, u, NotificationDocument.class);
        return res.getModifiedCount();
    }

    private NotificationDTO toDto(NotificationDocument d) {
        return new NotificationDTO(
                d.getId(), d.getType(), d.getChannel(), d.getStatus(),
                d.getUserId(), d.getRdvId(), d.getOrganisateurId(), d.getParticipantId(),
                d.getPayload(), d.getEventId(), d.getCreatedAt(), d.getSentAt(), d.getReadAt()
        );
    }
}
