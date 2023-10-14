package ru.practicum;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

public class StatsClient {
    private final WebClient webClient;

    public StatsClient(String serverUrl) {
        webClient = WebClient.builder().baseUrl(serverUrl).build();
    }

    public List<ViewStatsDto> findStats(String start, String end, List<String> uris, Boolean unique) {
        String urisString = String.join(",", uris);

        return webClient.get()
                .uri("/stats?start={start}&end={end}&uris={uris}&unique={unique}", start, end, urisString, unique)
                .retrieve()
                .bodyToFlux(ViewStatsDto.class)
                .collectList()
                .block();
    }

    public void saveHit(EndpointHitDto endpointHitDto) {
        webClient.post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(endpointHitDto))
                .exchange()
                .block();
    }
}
