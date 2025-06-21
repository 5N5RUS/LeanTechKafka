package ru.fendel.leantechkafka.core.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.fendel.leantechkafka.core.model.Notification;
import ru.fendel.leantechkafka.core.model.enums.MessageType;
import ru.fendel.leantechkafka.core.model.enums.NotificationStatus;
import ru.fendel.leantechkafka.core.repository.NotificationRepository;
import ru.fendel.leantechkafka.web.dto.NotificationRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private KafkaConsumerService kafkaConsumerService;

    @Test
    @DisplayName("Должен корректно обработать валидный DTO и сохранить уведомление в БД")
    void listen_whenValidDtoReceived_shouldSaveNotification() {
        NotificationRequestDto dto = new NotificationRequestDto(
                "Тестовое уведомление из Kafka",
                "EXTERNAL",
                "kafka-user-uid",
                LocalDateTime.now().plusHours(1)
        );

        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);

        when(notificationRepository.save(any(Notification.class)))
                .thenReturn(Mono.just(new Notification(1L, null, null, null, null, null, null, null)));

        Mono<Void> result = kafkaConsumerService.listen(dto);
        StepVerifier.create(result).verifyComplete();
        verify(notificationRepository).save(notificationCaptor.capture());

        Notification capturedNotification = notificationCaptor.getValue();
        assertThat(capturedNotification.message()).isEqualTo(dto.message());
        assertThat(capturedNotification.messageType()).isEqualTo(MessageType.EXTERNAL);
        assertThat(capturedNotification.userUid()).isEqualTo(dto.userUid());
        assertThat(capturedNotification.notificationStatus()).isEqualTo(NotificationStatus.NEW);
        assertThat(capturedNotification.id()).isNull();
    }
}