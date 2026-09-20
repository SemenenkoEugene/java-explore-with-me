package ru.practicum;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

public class StatsClient {

    private final WebClient webClient;

    public StatsClient(final String serverUrl) {
        webClient = WebClient.builder().baseUrl(serverUrl).build();
    }

    public List<ViewStatsDto> findStats(final String start, final String end, final List<String> uris, final Boolean unique) {
        final String urisString = String.join(",", uris);

        return webClient.get()
                .uri("/stats?start={start}&end={end}&uris={uris}&unique={unique}", start, end, urisString, unique)
                .retrieve()
                .bodyToFlux(ViewStatsDto.class)
                .collectList()
                .block();
    }

    public void saveHit(final EndpointHitDto endpointHitDto) {
        webClient.post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(endpointHitDto))
                .exchange()
                .block();
    }
}
