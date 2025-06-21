package ru.fendel.leantechkafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Класс, представляющий Spring Boot приложение.
 */
@SpringBootApplication
public class LeanTechKafkaApplication {

    /**
     * Запускает Spring Boot приложение.
     *
     * @param args аргументы командной строки, которые могут быть переданы при запуске.
     */
    public static void main(final String[] args) {
        SpringApplication.run(LeanTechKafkaApplication.class, args);
    }
}
