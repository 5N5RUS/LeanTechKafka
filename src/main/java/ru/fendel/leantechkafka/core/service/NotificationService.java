package ru.fendel.leantechkafka.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fendel.leantechkafka.core.model.Notification;
import ru.fendel.leantechkafka.core.model.enums.NotificationStatus;
import ru.fendel.leantechkafka.core.repository.NotificationRepository;

import java.time.LocalDateTime;

/**
 * Сервис для управления уведомлениями.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    /**
     * Репозиторий для работы с уведомлениями.
     */
    private final NotificationRepository repository;

    /**
     * Возвращает поток уведомлений с учетом пагинации.
     *
     * <p>Асинхронно запрашивает все уведомления из репозитория и применяет
     * параметры пагинации к результирующему потоку.
     *
     * @param pageable объект, содержащий информацию о странице и ее размере.
     * @return {@link Flux<Notification>}, который асинхронно эммитирует
     * уведомления для запрошенной страницы.
     */
    public Flux<Notification> getAllNotifications(final Pageable pageable) {
        return repository.findAll()
                .skip(pageable.getOffset())
                .take(pageable.getPageSize());
    }

    /**
     * Находит и возвращает одно уведомление по его уникальному идентификатору.
     *
     * @param id уникальный идентификатор уведомления.
     * @return {@link Mono<Notification>}, который содержит найденное уведомление,
     * или пустой Mono, если уведомление с таким ID не найдено.
     */
    public Mono<Notification> getNotificationById(final Long id) {
        return repository.findById(id);
    }

    /**
     * Находит уведомление по ID, изменяет его статус на COMPLETE и сохраняет изменения в базе данных.
     *
     * <p>Операция выполняется в два асинхронных шага: сначала поиск, затем сохранение.
     * Используется оператор flatMap для связывания двух асинхронных вызовов
     * в одну реактивную цепочку. Также устанавливается текущее время в поле modifiedAt.
     *
     * @param id уникальный идентификатор уведомления для обновления.
     * @return {@link Mono<Notification>}, содержащий обновленное уведомление,
     * или пустой Mono, если уведомление для обновления не было найдено.
     */
    public Mono<Notification> updateStatusOnComplete(final Long id) {
        return repository.findById(id).flatMap(notification -> {
            var updatedNotification = new Notification(
                    notification.id(),
                    notification.createdAt(),
                    LocalDateTime.now(),
                    notification.expirationDate(),
                    notification.message(),
                    notification.messageType(),
                    notification.userUid(),
                    NotificationStatus.COMPLETE
            );
            return repository.save(updatedNotification);
        });
    }
}
