package ru.fendel.leantechkafka.web.dto;

import java.time.LocalDateTime;

/**
 * DTO для уведомления.
 *
 * @param message        текст уведомления
 * @param messageType    тип уведомления
 * @param userUid        идентификатор пользователя, которому отправлено уведомление
 * @param expirationDate дата истечения уведомления
 */
public record NotificationRequestDto(
        String message,
        String messageType,
        String userUid,
        LocalDateTime expirationDate
) {
}
