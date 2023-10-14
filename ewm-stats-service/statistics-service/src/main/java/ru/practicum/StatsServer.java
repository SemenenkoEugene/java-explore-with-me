package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class StatsServer {
    public static void main(String[] args) {
        log.info("Запуск этого сервера");
        SpringApplication.run(StatsServer.class, args);
    }
}
