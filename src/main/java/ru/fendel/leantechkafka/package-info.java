/**
 * Корневой пакет приложения.
 *
 * <p>Этот микросервис отвечает за асинхронную обработку, хранение и предоставление
 * доступа к уведомлениям. Он демонстрирует использование современного стека технологий,
 * включая Spring WebFlux для реактивного API, R2DBC для неблокирующего доступа к базе данных,
 * и Apache Kafka для получения сообщений.
 *
 * <p>Основная точка входа в приложение находится в классе
 * {@link ru.fendel.leantechkafka.LeanTechKafkaApplication}.
 *
 * <h2>Архитектура</h2>
 * Приложение построено по слоеной архитектуре:
 * <ul>
 *     <li><b>web</b> - слой контроллеров и DTO для взаимодействия с внешним миром.</li>
 *     <li><b>core</b> - ядро приложения, содержащее бизнес-логику, доменные модели и репозитории.</li>
 *     <li><b>config</b> - конфигурационные классы для настройки инфраструктурных компонентов.</li>
 * </ul>
 *
 * @author Fendel Nikita
 * @version 1.0
 */
package ru.fendel.leantechkafka;
