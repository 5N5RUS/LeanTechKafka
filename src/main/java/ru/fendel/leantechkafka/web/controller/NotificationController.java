package ru.fendel.leantechkafka.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fendel.leantechkafka.core.model.Notification;
import ru.fendel.leantechkafka.core.service.NotificationService;

/**
 * Контроллер для управления уведомлениями.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    /**
     * Сервис для управления уведомлениями.
     */
    private final NotificationService service;

    /**
     * Возвращает поток уведомлений с учетом пагинации.
     *
     * @param page номер страницы.
     * @param size размер страницы.
     * @return поток уведомлений.
     */
    @GetMapping
    public Flux<Notification> getAll(final @RequestParam(defaultValue = "0") int page,
                                     final @RequestParam(defaultValue = "10") int size) {
        return service.getAllNotifications(PageRequest.of(page, size));
    }

    /**
     * Возвращает одно уведомление по его уникальному идентификатору.
     *
     * @param id уникальный идентификатор уведомления.
     * @return одно уведомление.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Notification>> getById(final @PathVariable Long id) {
        return service.getNotificationById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Обновляет статус уведомления.
     *
     * @param id уникальный идентификатор уведомления.
     * @return обновленное уведомление.
     */
    @PutMapping("/{id}/complete")
    public Mono<ResponseEntity<Notification>> updateStatus(final @PathVariable Long id) {
        return service.updateStatusOnComplete(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
