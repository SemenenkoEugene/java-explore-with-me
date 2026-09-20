package ru.practicum.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.EndpointHitMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.StatsRepository;
import ru.practicum.repository.ViewStatsProjection;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
class StatsServiceTest {

    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final EndpointHitDto ENDPOINT_HIT_DTO = EndpointHitDto.builder().build();

    @Mock
    private StatsRepository statsRepository;
    @Mock
    private EndpointHitMapper endpointHitMapper;

    @InjectMocks
    private StatsServiceImpl statsService;

    @BeforeEach
    void setUp() {
        Mockito.when(endpointHitMapper.fromDto(Mockito.any())).thenReturn(getEndpointHit());
    }

    @Test
    void findStats_unique() {
        Mockito.when(statsRepository.findUniqueStats(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(getMockViewStatsProjectionsList());

        statsService.findStats(null, null, null, true);

        Mockito.verify(statsRepository).findUniqueStats(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verifyNoMoreInteractions(statsRepository);
    }

    @Test
    void findStats_notUnique() {
        Mockito.when(statsRepository.findNotUniqueStats(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(getMockViewStatsProjectionsList());

        statsService.findStats(null, null, null, false);

        Mockito.verify(statsRepository).findNotUniqueStats(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verifyNoMoreInteractions(statsRepository);
    }

    @Test
    void findStats_MappingTest() {
        final List<ViewStatsProjection> mockProjections = getMockViewStatsProjectionsList();

        Mockito.when(statsRepository.findNotUniqueStats(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(mockProjections);

        final List<ViewStatsDto> resultDto = statsService.findStats(null, null, null, false);

        Assertions.assertThat(resultDto).isNotNull();
        Assertions.assertThat(resultDto.size()).isEqualTo(mockProjections.size());

        for (int i = 0; i < mockProjections.size(); i++) {
            final ViewStatsProjection mockProjection = mockProjections.get(i);
            final ViewStatsDto result = resultDto.get(i);

            Assertions.assertThat(result.getApp()).isEqualTo(mockProjection.getApp());
            Assertions.assertThat(result.getUri()).isEqualTo(mockProjection.getUri());
            Assertions.assertThat(result.getHits()).isEqualTo(mockProjection.getHits());
        }

    }

    @Test
    void findStats_invalidDateRange() {
        Assertions.assertThatThrownBy(() -> statsService.findStats(NOW.plusDays(1L), NOW.minusDays(1L), null, true))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Invalid date range");
    }

    @Test
    void createEndpoint_Test() {
        Mockito.when(statsRepository.save(Mockito.any())).thenReturn(new EndpointHit());

        statsService.saveHit(ENDPOINT_HIT_DTO);

        Mockito.verify(statsRepository).save(Mockito.any(EndpointHit.class));
        Mockito.verifyNoMoreInteractions(statsRepository);
    }

    private List<ViewStatsProjection> getMockViewStatsProjectionsList() {
        final List<ViewStatsProjection> result = new ArrayList<>();

        final ProjectionFactory projectionFactory = new SpelAwareProxyProjectionFactory();

        final ViewStatsProjection projection1 = projectionFactory.createProjection(ViewStatsProjection.class);
        projection1.setApp("App1");
        projection1.setUri("Uri1");
        projection1.setHits(100L);
        result.add(projection1);

        final ViewStatsProjection projection2 = projectionFactory.createProjection(ViewStatsProjection.class);
        projection2.setApp("App2");
        projection2.setUri("Uri2");
        projection2.setHits(200L);
        result.add(projection2);

        return result;
    }

    private EndpointHit getEndpointHit() {
        final EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp("App1");
        endpointHit.setUri("Uri1");
        endpointHit.setIp("Ip");
        endpointHit.setHitTimestamp(NOW);

        return endpointHit;
    }
}