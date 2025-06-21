package ru.fendel.leantechkafka.core.repository;

import ru.fendel.leantechkafka.core.model.Notification;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с уведомлениями.
 */
@Repository
public interface NotificationRepository extends ReactiveCrudRepository<Notification, Long> {
}
