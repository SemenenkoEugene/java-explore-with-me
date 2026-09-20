package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class StatsServer {
    public static void main(final String[] args) {
        log.info("Запуск сервера статистики");
        SpringApplication.run(StatsServer.class, args);
    }
}
