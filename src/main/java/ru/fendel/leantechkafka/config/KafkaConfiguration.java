package ru.fendel.leantechkafka.config;

import org.apache.kafka.common.errors.RecordDeserializationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;

/**
 * Конфигурационный класс для настройки компонентов Apache Kafka.
 *
 * <p>Этот класс определяет кастомную стратегию обработки ошибок для Kafka-слушателей,
 * обеспечивая отказоустойчивость приложения при получении "битых" сообщений.
 */
@Configuration
public class KafkaConfiguration {

    /**
     * Создает и настраивает обработчик ошибок для Kafka-слушателей.
     *
     * <p>Этот обработчик использует механизм Dead-Letter Topic (DLT). Если происходит
     * ошибка десериализации, сообщение не отбрасывается, а перенаправляется
     * в специальный DLT-топик для последующего анализа.
     *
     * @param template {@link KafkaTemplate}, используемый для отправки сообщений в DLT.
     * @return настроенный экземпляр {@link DefaultErrorHandler}.
     */
    @Bean
    public DefaultErrorHandler errorHandler(final KafkaTemplate<Object, Object> template) {

        var recoverer = new DeadLetterPublishingRecoverer(template);
        var errorHandler = new DefaultErrorHandler(recoverer);

        errorHandler.addNotRetryableExceptions(
                RecordDeserializationException.class,
                DeserializationException.class
        );

        return errorHandler;
    }

    /**
     * Создает и настраивает фабрику контейнеров для Kafka-слушателей.
     *
     * <p>Эта фабрика будет использоваться для создания всех контейнеров.
     * Ключевой особенностью является установка кастомного обработчика ошибок,
     * определенного в бине {@link #errorHandler(KafkaTemplate)}.
     *
     * @param consumerFactory фабрика для создания экземпляров Kafka Consumer.
     * @param errorHandler    кастомный обработчик ошибок, который будет применен ко всем слушателям.
     * @return настроенный экземпляр {@link ConcurrentKafkaListenerContainerFactory}.
     */
    @Bean
    @Primary
    public ConcurrentKafkaListenerContainerFactory<Object, Object> kafkaListenerContainerFactory(
            final ConsumerFactory<Object, Object> consumerFactory,
            final DefaultErrorHandler errorHandler) {

        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }
}
