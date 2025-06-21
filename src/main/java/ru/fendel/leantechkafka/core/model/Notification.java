package ru.fendel.leantechkafka.core.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import ru.fendel.leantechkafka.core.model.enums.MessageType;
import ru.fendel.leantechkafka.core.model.enums.NotificationStatus;

import java.time.LocalDateTime;

@Table("notifications.notifications")
public record Notification(
        @Id Long id,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt,
        LocalDateTime expirationDate,
        String message,
        MessageType messageType,
        String userUid,
        NotificationStatus notificationStatus
) {
}
