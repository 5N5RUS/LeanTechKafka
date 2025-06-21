package ru.fendel.leantechkafka.web.dto;

import java.time.LocalDateTime;

public record NotificationRequestDto(
        String message,
        String messageType,
        String userUid,
        LocalDateTime expirationDate
) {
}