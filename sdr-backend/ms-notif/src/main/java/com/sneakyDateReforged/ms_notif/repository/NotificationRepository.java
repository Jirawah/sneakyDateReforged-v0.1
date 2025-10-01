package com.sneakyDateReforged.ms_notif.repository;

import com.sneakyDateReforged.ms_notif.domain.NotificationDocument;
import com.sneakyDateReforged.ms_notif.domain.enums.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationRepository extends MongoRepository<NotificationDocument, String> {

    Page<NotificationDocument> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Page<NotificationDocument> findByUserIdAndStatusOrderByCreatedAtDesc(
            Long userId, NotificationStatus status, Pageable pageable
    );

    long countByUserIdAndStatus(Long userId, NotificationStatus status);
}
