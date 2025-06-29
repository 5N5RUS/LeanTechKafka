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

/**
 * Сервис-консьюмер для обработки сообщений из Apache Kafka.
 */
@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class KafkaConsumerService {

    /**
     * Репозиторий для работы с уведомлениями.
     */
    private final NotificationRepository notificationRepository;

    /**
     * Прослушивает топик Kafka, получает сообщение и сохраняет его как новое уведомление.
     *
     * <p>Метод автоматически вызывается фреймворком Spring Kafka при поступлении нового
     * сообщения в топик. Полученный DTO преобразуется в сущность {@link Notification} со статусом NEW
     * и асинхронно сохраняется в репозитории.
     *
     * <p>Возвращаемый {@link Mono<Void>} позволяет Spring Kafka управлять жизненным циклом
     * реактивной цепочки и корректно применять транзакции.
     *
     * @param dto объект {@link NotificationRequestDto}, десериализованный из JSON-сообщения в Kafka.
     * @return {@link Mono<Void>}, который сигнализирует о завершении асинхронной операции сохранения.
     */
    @KafkaListener(topics = "${app.kafka.topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public Mono<Void> listen(final NotificationRequestDto dto) {
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
