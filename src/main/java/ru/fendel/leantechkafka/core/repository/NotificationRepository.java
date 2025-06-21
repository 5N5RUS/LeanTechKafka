package ru.fendel.leantechkafka.core.repository;

import ru.fendel.leantechkafka.core.model.Notification;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends ReactiveCrudRepository<Notification, Long> {
}