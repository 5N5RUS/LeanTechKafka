package ru.fendel.leantechkafka.web.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.fendel.leantechkafka.core.model.Notification;
import ru.fendel.leantechkafka.core.model.enums.NotificationStatus;
import ru.fendel.leantechkafka.core.repository.NotificationRepository;
import ru.fendel.leantechkafka.web.dto.NotificationRequestDto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@AutoConfigureMockMvc
class NotificationControllerTest extends AbstractIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private KafkaTemplate<String, NotificationRequestDto> kafkaTemplate;

    @Autowired
    private NotificationRepository notificationRepository;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll().block();
    }

    @Nested
    @DisplayName("GET /notifications")
    class GetNotifications {

        @Test
        @DisplayName("Должен вернуть уведомление, полученное из Kafka")
        void whenMessageConsumed_thenNotificationIsReturned() {
            var dto = createDto("Сообщение уведомления", "integration-test-uid");
            kafkaTemplate.send("notifications-topic", dto);

            await().atMost(5, TimeUnit.SECONDS).untilAsserted(() ->
                    webTestClient.get().uri("/notifications")
                            .exchange()
                            .expectStatus().isOk()
                            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                            .expectBodyList(Notification.class).hasSize(1)
                            .value(list -> {
                                Notification notification = list.getFirst();
                                assertThat(notification.message()).isEqualTo("Сообщение уведомления");
                                assertThat(notification.userUid()).isEqualTo("integration-test-uid");
                                assertThat(notification.notificationStatus()).isEqualTo(NotificationStatus.NEW);
                            })
            );
        }

        @Test
        @DisplayName("Должен корректно работать с пагинацией")
        void whenMultipleNotificationsExist_thenPaginationWorks() {
            List<NotificationRequestDto> dtos = IntStream.range(0, 15)
                    .mapToObj(i -> createDto("Сообщение " + i, "user-" + i))
                    .toList();
            dtos.forEach(dto -> kafkaTemplate.send("notifications-topic", dto));

            await().atMost(10, TimeUnit.SECONDS)
                    .untilAsserted(() -> assertThat(notificationRepository.count().block()).isEqualTo(15L));

            webTestClient.get().uri("/notifications?page=1&size=5")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(Notification.class).hasSize(5);
        }

        @Test
        @DisplayName("Должен вернуть пустой список, если уведомлений нет")
        void whenNoNotificationsExist_thenReturnsEmptyList() {
            webTestClient.get().uri("/notifications")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(Notification.class).hasSize(0);
        }
    }

    @Nested
    @DisplayName("GET /notifications/{id}")
    class GetNotificationById {
        @Test
        @DisplayName("Должен вернуть 404 Not Found, если уведомление не найдено")
        void whenNotificationDoesNotExist_thenReturns404() {
            webTestClient.get().uri("/notifications/{id}", 999L)
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }

    @Nested
    @DisplayName("PUT /notifications/{id}/complete")
    class UpdateNotificationStatus {
        @Test
        @DisplayName("Должен обновить статус существующего уведомления")
        void whenNotificationExists_thenStatusIsUpdatedToComplete() {
            var dto = createDto("Сообщение уведомления", "update-uid");
            kafkaTemplate.send("notifications-topic", dto);
            Notification saved = awaitAndGetFirstNotification();

            webTestClient.put().uri("/notifications/{id}/complete", saved.id())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(Notification.class)
                    .value(updated -> {
                        assertThat(updated.id()).isEqualTo(saved.id());
                        assertThat(updated.notificationStatus()).isEqualTo(NotificationStatus.COMPLETE);
                        assertThat(updated.modifiedAt()).isNotNull();
                    });
        }

        @Test
        @DisplayName("Должен вернуть 404 Not Found, если уведомление для обновления не найдено")
        void whenNotificationDoesNotExist_thenReturns404() {
            webTestClient.put().uri("/notifications/{id}/complete", 999L)
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }

    private NotificationRequestDto createDto(String message, String userUid) {
        return new NotificationRequestDto(
                message,
                "INTERNAL",
                userUid,
                LocalDateTime.now().plusDays(1)
        );
    }

    private Notification awaitAndGetFirstNotification() {
        return await().atMost(5, TimeUnit.SECONDS)
                .pollInterval(Duration.ofMillis(100))
                .until(() -> notificationRepository.findAll().blockFirst(), java.util.Objects::nonNull);
    }
}