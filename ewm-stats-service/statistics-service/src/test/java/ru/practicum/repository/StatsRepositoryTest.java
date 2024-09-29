package ru.practicum.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@ExtendWith(SpringExtension.class)
class StatsRepositoryTest {

    @Autowired
    private StatsRepository statsRepository;

    @BeforeEach
    public void setUp() {
        EndpointHit hit1 = createEndpointHit("app1", "/uri1", "192.168.1.1", LocalDateTime.now().minusDays(1));
        EndpointHit hit2 = createEndpointHit("app1", "/uri1", "192.168.1.2", LocalDateTime.now().minusDays(1));
        EndpointHit hit3 = createEndpointHit("app2", "/uri2", "192.168.1.1", LocalDateTime.now().minusDays(2));

        statsRepository.saveAll(Arrays.asList(hit1, hit2, hit3));
    }
    @Test
    public void findUniqueStats_emptyUri_allTime() {
        LocalDateTime start = LocalDateTime.now().minusYears(1);
        LocalDateTime end = LocalDateTime.now().plusYears(1);

        List<ViewStatsProjection> viewStatsProjections = statsRepository.findUniqueStats(start, end, null);

        assertThat(viewStatsProjections).size().isEqualTo(2);

        assertThat(viewStatsProjections.get(0).getHits()).isEqualTo(2);
        assertThat(viewStatsProjections.get(0).getUri()).isEqualTo("/uri1");
    }

    @Test
    public void findUniqueStats_withMultipleUri_allTime() {
        LocalDateTime start = LocalDateTime.now().minusYears(1);
        LocalDateTime end = LocalDateTime.now().plusYears(1);
        List<String> uris = Arrays.asList("/uri1", "/uri2", "/uri3");

        List<ViewStatsProjection> viewStatsProjections = statsRepository.findUniqueStats(start, end, uris);

        assertThat(viewStatsProjections).size().isEqualTo(2);

        assertThat(viewStatsProjections.get(0).getHits()).isEqualTo(2);
        assertThat(viewStatsProjections.get(1).getHits()).isEqualTo(1);
    }

    private EndpointHit createEndpointHit(String app, String uri, String ip, LocalDateTime timestamp) {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp(app);
        endpointHit.setUri(uri);
        endpointHit.setIp(ip);
        endpointHit.setHitTimestamp(timestamp);

        return endpointHit;
    }

}