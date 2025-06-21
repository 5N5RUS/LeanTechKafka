package ru.fendel.leantechkafka.core.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.fendel.leantechkafka.core.model.Notification;
import ru.fendel.leantechkafka.core.model.enums.MessageType;
import ru.fendel.leantechkafka.core.model.enums.NotificationStatus;
import ru.fendel.leantechkafka.core.repository.NotificationRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Nested
    @DisplayName("Метод getAllNotifications()")
    class GetAllNotificationsTests {

        @Test
        @DisplayName("Должен возвращать все уведомления из репозитория")
        void shouldReturnAllNotificationsFromRepository() {
            var notification1 = createTestNotification(1L);
            var notification2 = createTestNotification(2L);
            when(notificationRepository.findAll()).thenReturn(Flux.just(notification1, notification2));

            StepVerifier.create(notificationService.getAllNotifications(PageRequest.of(0, 10)))
                    .expectNext(notification1)
                    .expectNext(notification2)
                    .verifyComplete();
        }

        @Test
        @DisplayName("Должен возвращать пустой поток, если уведомлений нет")
        void shouldReturnEmptyFluxWhenNoNotifications() {
            when(notificationRepository.findAll()).thenReturn(Flux.empty());

            StepVerifier.create(notificationService.getAllNotifications(PageRequest.of(0, 10)))
                    .expectNextCount(0)
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Метод getNotificationById()")
    class GetNotificationByIdTests {

        @Test
        @DisplayName("Должен возвращать уведомление, если оно найдено по ID")
        void whenExists_shouldReturnNotification() {
            long notificationId = 1L;
            var expectedNotification = createTestNotification(notificationId);
            when(notificationRepository.findById(notificationId)).thenReturn(Mono.just(expectedNotification));

            StepVerifier.create(notificationService.getNotificationById(notificationId))
                    .assertNext(actualNotification -> {
                        assertThat(actualNotification.id()).isEqualTo(expectedNotification.id());
                        assertThat(actualNotification.message()).isEqualTo(expectedNotification.message());
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("Должен возвращать пустой Mono, если уведомление не найдено по ID")
        void whenNotExists_shouldReturnEmptyMono() {
            long nonExistentId = 99L;
            when(notificationRepository.findById(nonExistentId)).thenReturn(Mono.empty());

            StepVerifier.create(notificationService.getNotificationById(nonExistentId))
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("Метод updateStatusOnComplete()")
    class UpdateStatusOnCompleteTests {

        @Test
        @DisplayName("Должен обновить статус и вернуть обновленное уведомление")
        void shouldUpdateStatusAndReturnUpdatedNotification() {
            long notificationId = 1L;
            var originalNotification = createTestNotification(notificationId);

            when(notificationRepository.findById(notificationId)).thenReturn(Mono.just(originalNotification));
            when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation ->
                    Mono.just(invocation.getArgument(0, Notification.class))
            );

            StepVerifier.create(notificationService.updateStatusOnComplete(notificationId))
                    .assertNext(updated -> {
                        assertThat(updated.id()).isEqualTo(notificationId);
                        assertThat(updated.notificationStatus()).isEqualTo(NotificationStatus.COMPLETE);
                        assertThat(updated.modifiedAt()).isNotNull();
                        assertThat(updated.message()).isEqualTo(originalNotification.message());
                    })
                    .verifyComplete();
            verify(notificationRepository, times(1)).save(any(Notification.class));
        }

        @Test
        @DisplayName("Должен вернуть пустой Mono, если уведомление для обновления не найдено")
        void whenNotificationToUpdateNotFound_shouldReturnEmptyMono() {
            long nonExistentId = 99L;
            when(notificationRepository.findById(nonExistentId)).thenReturn(Mono.empty());

            StepVerifier.create(notificationService.updateStatusOnComplete(nonExistentId))
                    .verifyComplete();
            verify(notificationRepository, never()).save(any(Notification.class));
        }
    }

    private Notification createTestNotification(Long id) {
        return new Notification(
                id,
                LocalDateTime.now(),
                null,
                LocalDateTime.now().plusDays(1),
                "Тестовое уведомление",
                MessageType.INTERNAL,
                "test-user-uid",
                NotificationStatus.NEW
        );
    }
}