package ru.fendel.leantechkafka;

import org.springframework.boot.SpringApplication;

public class TestLeanTechKafkaApplication {

    public static void main(String[] args) {
        SpringApplication.from(LeanTechKafkaApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
