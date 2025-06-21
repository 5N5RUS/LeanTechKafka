package ru.fendel.leantechkafka.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fendel.leantechkafka.core.model.Notification;
import ru.fendel.leantechkafka.core.model.enums.NotificationStatus;
import ru.fendel.leantechkafka.core.repository.NotificationRepository;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;

    public Flux<Notification> getAllNotifications(Pageable pageable) {
        return repository.findAll()
                .skip(pageable.getOffset())
                .take(pageable.getPageSize());
    }

    public Mono<Notification> getNotificationById(Long id) {
        return repository.findById(id);
    }

    public Mono<Notification> updateStatusOnComplete(Long id) {
        return repository.findById(id).flatMap(notification -> {
            var updatedNotification = new Notification(
                    notification.id(),
                    notification.createdAt(),
                    LocalDateTime.now(),
                    notification.expirationDate(),
                    notification.message(),
                    notification.messageType(),
                    notification.userUid(),
                    NotificationStatus.COMPLETE
            );
            return repository.save(updatedNotification);
        });
    }
}