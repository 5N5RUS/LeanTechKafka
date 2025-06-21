package ru.fendel.leantechkafka.core.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import ru.fendel.leantechkafka.core.model.enums.MessageType;
import ru.fendel.leantechkafka.core.model.enums.NotificationStatus;

import java.time.LocalDateTime;

/**
 * Сущность уведомления.
 *
 * @param id                 идентификатор уведомления
 * @param createdAt          дата создания уведомления
 * @param modifiedAt         дата последнего изменения уведомления
 * @param expirationDate     дата истечения уведомления
 * @param message            текст уведомления
 * @param messageType        тип уведомления
 * @param userUid            идентификатор пользователя, которому отправлено уведомление
 * @param notificationStatus статус уведомления
 */
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
