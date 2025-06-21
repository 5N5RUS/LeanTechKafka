package ru.fendel.leantechkafka.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.fendel.leantechkafka.core.model.enums.MessageType;
import ru.fendel.leantechkafka.core.model.Notification;
import ru.fendel.leantechkafka.core.model.enums.NotificationStatus;
import ru.fendel.leantechkafka.core.repository.NotificationRepository;
import ru.fendel.leantechkafka.web.dto.NotificationRequestDto;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final NotificationRepository notificationRepository;

    @KafkaListener(topics = "${app.kafka.topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public Mono<Void> listen(NotificationRequestDto dto) {
        log.info("Получено уведомление: {}", dto);
        Notification notification = new Notification(
                null,
                null,
                null,
                dto.expirationDate(),
                dto.message(),
                MessageType.valueOf(dto.messageType()),
                dto.userUid(),
                NotificationStatus.NEW
        );
        return notificationRepository.save(notification)
                .doOnSuccess(saved -> log.info("Уведомление сохраненно с идентификатором: {}", saved.id()))
                .doOnError(e -> log.error("Ошибка при сохранении уведомления: ", e))
                .then();
    }
}
