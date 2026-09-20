package ru.practicum.repository;

import org.assertj.core.api.AssertionsForInterfaceTypes;
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

@DataJpaTest
@ExtendWith(SpringExtension.class)
class StatsRepositoryTest {

    @Autowired
    private StatsRepository statsRepository;

    @BeforeEach
    public void setUp() {
        final EndpointHit hit1 = createEndpointHit("app1", "/uri1", "192.168.1.1", LocalDateTime.now().minusDays(1));
        final EndpointHit hit2 = createEndpointHit("app1", "/uri1", "192.168.1.2", LocalDateTime.now().minusDays(1));
        final EndpointHit hit3 = createEndpointHit("app2", "/uri2", "192.168.1.1", LocalDateTime.now().minusDays(2));

        statsRepository.saveAll(Arrays.asList(hit1, hit2, hit3));
    }

    @Test
    public void findUniqueStats_emptyUri_allTime() {
        final LocalDateTime start = LocalDateTime.now().minusYears(1);
        final LocalDateTime end = LocalDateTime.now().plusYears(1);

        final List<ViewStatsProjection> viewStatsProjections = statsRepository.findUniqueStats(start, end, null);

        AssertionsForInterfaceTypes.assertThat(viewStatsProjections).size().isEqualTo(2);

        AssertionsForInterfaceTypes.assertThat(viewStatsProjections.getFirst().getHits()).isEqualTo(2);
        AssertionsForInterfaceTypes.assertThat(viewStatsProjections.getFirst().getUri()).isEqualTo("/uri1");
    }

    @Test
    public void findUniqueStats_withMultipleUri_allTime() {
        final LocalDateTime start = LocalDateTime.now().minusYears(1);
        final LocalDateTime end = LocalDateTime.now().plusYears(1);
        final List<String> uris = Arrays.asList("/uri1", "/uri2", "/uri3");

        final List<ViewStatsProjection> viewStatsProjections = statsRepository.findUniqueStats(start, end, uris);

        AssertionsForInterfaceTypes.assertThat(viewStatsProjections).size().isEqualTo(2);

        AssertionsForInterfaceTypes.assertThat(viewStatsProjections.get(0).getHits()).isEqualTo(2);
        AssertionsForInterfaceTypes.assertThat(viewStatsProjections.get(1).getHits()).isEqualTo(1);
    }

    private EndpointHit createEndpointHit(final String app, final String uri, final String ip, final LocalDateTime timestamp) {
        final EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp(app);
        endpointHit.setUri(uri);
        endpointHit.setIp(ip);
        endpointHit.setHitTimestamp(timestamp);

        return endpointHit;
    }

}